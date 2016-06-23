package com.autoStock;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicLong;

import com.autoStock.account.AccountProvider;
import com.autoStock.adjust.AdjustmentCampaign;
import com.autoStock.adjust.AdjustmentCampaignProvider;
import com.autoStock.adjust.AdjustmentIdentifier;
import com.autoStock.adjust.AdjustmentRebaser;
import com.autoStock.adjust.AdjustmentSeriesForAlgorithm;
import com.autoStock.algorithm.AlgorithmBase;
import com.autoStock.algorithm.AlgorithmTest;
import com.autoStock.algorithm.core.AlgorithmDefinitions.AlgorithmMode;
import com.autoStock.backtest.AlgorithmModel;
import com.autoStock.backtest.BacktestEvaluation;
import com.autoStock.backtest.BacktestEvaluationBuilder;
import com.autoStock.backtest.BacktestEvaluationWriter;
import com.autoStock.backtest.BacktestEvaluator;
import com.autoStock.backtest.BacktestUtils;
import com.autoStock.backtest.SingleBacktest;
import com.autoStock.cluster.ComputeResultForBacktest;
import com.autoStock.cluster.ComputeUnitForBacktest;
import com.autoStock.com.CommandHolder;
import com.autoStock.com.ListenerOfCommandHolderResult;
import com.autoStock.comServer.ClusterServer;
import com.autoStock.finance.SecurityTypeHelper.SecurityType;
import com.autoStock.internal.Global;
import com.autoStock.tools.Benchmark;
import com.autoStock.tools.TimeToCompletionEstimate;
import com.autoStock.trading.platform.ib.definitions.HistoricalDataDefinitions.Resolution;
import com.autoStock.types.Exchange;
import com.autoStock.types.Symbol;
import com.google.gson.internal.Pair;

/**
 * @author Kevin Kowalewski
 *
 */
public class MainClusteredBacktest implements ListenerOfCommandHolderResult {
	private static MainClusteredBacktest instance;
	private ArrayList<Long> listOfComputeUnitResultIds = new ArrayList<Long>();
	private ArrayList<String> listOfSymbols;
	private Exchange exchange;
	private ClusterServer clusterServer;
	private AdjustmentCampaignProvider adjustmentCampaignProvider = AdjustmentCampaignProvider.getInstance();
	private AtomicLong atomicIntForRequestId = new AtomicLong();
	private Date dateStart;
	private Date dateEnd;
	private final int computeUnitIterationSize = 64;
	private Benchmark bench = new Benchmark();
	private Benchmark benchTotal = new Benchmark();
	private Thread threadForWatcher;
	public final BacktestEvaluator backtestEvaluator = new BacktestEvaluator();
	public HashMap<Symbol, AlgorithmBase> hashOfAlgorithmBase = new HashMap<Symbol, AlgorithmBase>();
	private TimeToCompletionEstimate eta = new TimeToCompletionEstimate();
	
	int rebases = 0;
	
	public MainClusteredBacktest(Exchange exchange, Date dateStart, Date dateEnd, ArrayList<String> listOfSymbols) {
		this.exchange = exchange;
		this.listOfSymbols = listOfSymbols;
		this.dateStart = dateStart;
		this.dateEnd = dateEnd;
		instance = this;
		
		Global.callbackLock.requestLock();
		Thread.currentThread().setPriority(Thread.MAX_PRIORITY);
		
		for (String string : listOfSymbols){
			Symbol symbol = new Symbol(string, SecurityType.type_stock);
			AlgorithmBase algorithmBase = new AlgorithmTest(exchange, symbol, AlgorithmMode.mode_backtest_silent, AccountProvider.getInstance().getAccount(symbol));
			algorithmBase.initialize();
			
			AdjustmentCampaign adjustmentCampaign = new AdjustmentSeriesForAlgorithm(algorithmBase);
			adjustmentCampaign.initialize();
			adjustmentCampaign.applyValues();
			
			adjustmentCampaignProvider.addAdjustmentCampaignForAlgorithm(adjustmentCampaign, symbol);
			
			hashOfAlgorithmBase.put(symbol, algorithmBase);
		}
		
		startRequestServer();
	}
	
	
	public static MainClusteredBacktest getInstance(){
		return instance;
	}
	
	private void startRequestServer(){
		clusterServer = new ClusterServer(this);
		clusterServer.startServer();
	}
	
	public synchronized ComputeUnitForBacktest getNextComputeUnit(){
		
		Co.println("--> Generating unit...");
		
		if (benchTotal.hasTicked == false){
			benchTotal.tick();
		}
		
		ComputeUnitForBacktest computeUnit = new ComputeUnitForBacktest(atomicIntForRequestId.getAndIncrement(), exchange, dateStart, dateEnd);
		
		for (Pair<AdjustmentIdentifier, AdjustmentCampaign> pair : adjustmentCampaignProvider.getListOfAdjustmentCampaign()){
			
			int addedUnits = 0;
			ArrayList<AlgorithmModel> listOfAlgorithmModel = new ArrayList<AlgorithmModel>();
			
//			Co.println("--> Has more? " + pair.second.hasMore());
			if (pair.second.rebaseRequired()){
				Co.println("--> Rebase required...");
				
				SingleBacktest singleBacktest = new SingleBacktest(BacktestUtils.getBaseHistoricalData(exchange, pair.first.identifier, dateStart, dateEnd, Resolution.min), AlgorithmMode.mode_backtest_silent);
				singleBacktest.remodel(AlgorithmModel.getCurrentAlgorithmModel(hashOfAlgorithmBase.get(pair.first.identifier)));
				singleBacktest.selfPopulateBacktestData();
				singleBacktest.runBacktest();
				
				new AdjustmentRebaser(pair.second, singleBacktest.backtestContainer).rebase();
				
				Co.println("--> Algorithm rebased");
				
				rebases++;
			}
			
			while (pair.second.hasMore() && addedUnits < computeUnitIterationSize){
				if (pair.second.hasRun() == false && addedUnits == 0){
					pair.second.applyValues();
				} else {
					pair.second.runAdjustment();	
				}
				
				listOfAlgorithmModel.add(AlgorithmModel.getCurrentAlgorithmModel(hashOfAlgorithmBase.get(pair.first.identifier)));
				
				addedUnits++;
			}
			
			if (addedUnits > 0){
				Co.println("--> Added to unit: " + pair.first.identifier.name + ", " + addedUnits);
				computeUnit.hashOfAlgorithmModel.put(pair.first.identifier, listOfAlgorithmModel);
			}
			
			eta.setMaxIndex(pair.second.getMaxIndex());
			eta.update(pair.second.getCurrentIndex());
			
			pair.second.printPercentComplete(pair.first.identifier.name + " - " + (int) eta.getETAInMinutes() + " minutes");
		}
		
		Co.println("--> Issued unit: " + atomicIntForRequestId.get() + "\n");
		
		bench.tick();
		
		if (computeUnit.hashOfAlgorithmModel.keySet().size() > 0){
			return computeUnit;
		}else{
			Co.println("--> No units left... Rebases: " + rebases);
			if (threadForWatcher == null) {startWatcher();}
			return null;
		}
	}
	
	private void startWatcher() {
		threadForWatcher = new Thread(new Runnable() {
			@Override
			public void run() {
				int waitFor = 30;

				while (isComplete() == false) {
					try {Thread.sleep(1000);}catch(InterruptedException e){return;}
					if (waitFor == 0) {
						displayResultTable();
						Co.println("--> Warning: compute unit(s) went missing... Sent / Received: " + atomicIntForRequestId.get() + ", " + listOfComputeUnitResultIds.size());
						Global.callbackLock.releaseLock();
					}else{
						Co.println("--> Waiting..." + waitFor);
					}

					waitFor--;
				}
			}
		});
		threadForWatcher.start();
	}


	public void displayResultTable(){		
		Co.println("--> ********************* ********************* Clustered Backtest Results ********************* *********************");
		
		backtestEvaluator.pruneForFinish();
		backtestEvaluator.reverse();
		
		for (String string : listOfSymbols){
			Symbol symbol = new Symbol(string, SecurityType.type_stock);
			Co.println("--> SYMBOL BACKTEST: " + symbol.name);
			
			if (backtestEvaluator.getResults(symbol) == null){
				Co.println("--> No positive results");
				return;
			}
			
			for (BacktestEvaluation backtestEvaluation : backtestEvaluator.getResults(symbol)){
				Co.println("\n\n--> String representation: " + backtestEvaluation.toString());
			}
			
			BacktestEvaluation bestEvaluation = backtestEvaluator.getResults(symbol).get(backtestEvaluator.getResults(symbol).size() -1);
			
			new BacktestEvaluationWriter().writeToDatabase(bestEvaluation, false);
			
			Co.println("********");
			
			Co.print(new BacktestEvaluationBuilder().buildOutOfSampleEvaluation(bestEvaluation).toString());
		}
	}

	@Override
	public synchronized void receivedCommand(CommandHolder commandHolder) {
		Co.println("--> Received command: " + commandHolder.command);
		
		ComputeResultForBacktest computeResultForBacktest = (ComputeResultForBacktest) commandHolder.commandParameters;
		
		listOfComputeUnitResultIds.add(computeResultForBacktest.requestId);
		
		for (Symbol symbol : computeResultForBacktest.hashOfBacktestEvaluation.keySet()){
			for (BacktestEvaluation backtestEvaluation : computeResultForBacktest.hashOfBacktestEvaluation.get(symbol)){
				backtestEvaluator.addResult(symbol, backtestEvaluation, true);				
			}
		}
	}
	
	public boolean isComplete(){
		return atomicIntForRequestId.get() == listOfComputeUnitResultIds.size();
	}
}