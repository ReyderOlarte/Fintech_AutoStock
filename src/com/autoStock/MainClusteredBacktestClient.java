package com.autoStock;

import java.util.ArrayList;
import java.util.HashMap;

import com.autoStock.account.AccountProvider;
import com.autoStock.backtest.BacktestDefinitions.BacktestType;
import com.autoStock.backtest.BacktestEvaluation;
import com.autoStock.backtest.ListenerOfMainBacktestCompleted;
import com.autoStock.cluster.ComputeResultForBacktest;
import com.autoStock.cluster.ComputeUnitForBacktest;
import com.autoStock.com.CommandHolder;
import com.autoStock.com.CommandSerializer;
import com.autoStock.com.ListenerOfCommandHolderResult;
import com.autoStock.comServer.ClusterClient;
import com.autoStock.comServer.CommunicationDefinitions.Command;
import com.autoStock.internal.ApplicationStates;
import com.autoStock.internal.Global;
import com.autoStock.order.OrderDefinitions.OrderMode;
import com.autoStock.position.PositionManager;
import com.autoStock.types.Symbol;

/**
 * @author Kevin Kowalewski
 *
 */
public class MainClusteredBacktestClient implements ListenerOfCommandHolderResult, ListenerOfMainBacktestCompleted {
	private ClusterClient clusterClient;
	private ComputeUnitForBacktest computeUnitForBacktest;
	private HashMap<Symbol, ArrayList<BacktestEvaluation>> hashOfBacktestEvaluation = new HashMap<Symbol, ArrayList<BacktestEvaluation>>();
	private MainBacktest mainBacktest;
	private long processedUnits = 0;

	public MainClusteredBacktestClient() {
		Global.callbackLock.requestLock();
		PositionManager.getGlobalInstance().orderMode = OrderMode.mode_simulated;
		
		clusterClient = new ClusterClient(this);
		clusterClient.startClient();
		
		requestNextUnit();
	}
	
	public void requestNextUnit(){
		new CommandSerializer().sendSerializedCommand(Command.accept_unit, clusterClient.printWriter);
	}

	public void runNextBacktest(){
		AccountProvider.getInstance().getGlobalAccount().reset();
		PositionManager.getGlobalInstance().reset();
		
//		for (AlgorithmModel algorithmModel : computeUnitForBacktest.listOfAlgorithmModel){
//			Co.println("--> Received parameters");
//
//			for (SignalParameters signalParameter : algorithmModel.listOfSignalParameters){
//				if (signalParameter.normalizeInterface != null){
//					Co.println("--> " + signalParameter.arrayOfSignalGuageForLongEntry[0].threshold);
//					Co.println("--> " + signalParameter.arrayOfSignalGuageForLongExit[0].threshold);
//				}
//			}
//		}
		
//		if (backtestIndex == computeUnitForBacktest.listOfAlgorithmModel.size()){
//			allBacktestsCompleted();
//		}else{
			mainBacktest = new MainBacktest(computeUnitForBacktest.exchange, computeUnitForBacktest.dateStart, computeUnitForBacktest.dateEnd, BacktestType.backtest_clustered_client, this, computeUnitForBacktest.hashOfAlgorithmModel);
//		}
	}
	
//	public void allBacktestsCompleted(){
//		Co.println("--> All backtests completed...");
//		sendBacktestResult();
//		requestNextUnit();
//	}
	
	public void sendBacktestResult(){
		CommandHolder<ComputeResultForBacktest> commandHolder = new CommandHolder<ComputeResultForBacktest>(Command.backtest_results, new ComputeResultForBacktest(computeUnitForBacktest.requestId, hashOfBacktestEvaluation));
		new CommandSerializer().sendSerializedCommand(commandHolder, clusterClient.printWriter);
		
		hashOfBacktestEvaluation.clear();
		mainBacktest.backtestEvaluator.reset();
	}

	@Override
	public void receivedCommand(CommandHolder commandHolder) {
		if (commandHolder.command == Command.compute_unit_backtest){
			computeUnitForBacktest = (ComputeUnitForBacktest) commandHolder.commandParameters;
			
//			Co.println("--> Compute unit: " + computeUnitForBacktest.requestId + ", " + computeUnitForBacktest.dateStart + ", " + computeUnitForBacktest.dateEnd);
//			Co.println("--> Compute unit symbols: " + computeUnitForBacktest.hashOfAlgorithmModel.keySet().size());
//			Co.println("--> Compute unit sizes: ");
			
			for (Symbol symbol : computeUnitForBacktest.hashOfAlgorithmModel.keySet()){
//				Co.println("--> Symbol, size: " + symbol.symbolName + ", " + computeUnitForBacktest.hashOfAlgorithmModel.get(symbol).size());
				hashOfBacktestEvaluation.put(symbol, new ArrayList<BacktestEvaluation>());
			}
			
			runNextBacktest();
			
			processedUnits++;
		}else if(commandHolder.command == Command.no_units_left){
			Co.println("--> No units left... exiting... Processed units: " + processedUnits);
			try {Thread.sleep(1000);}catch(InterruptedException e){return;}
			ApplicationStates.shutdown();
		}
	}
	
	@Override
	public void backtestCompleted(){
		for (Symbol symbol : hashOfBacktestEvaluation.keySet()){
			ArrayList<BacktestEvaluation> listOfBacktestEvaluation = mainBacktest.backtestEvaluator.getResults(symbol);
			Co.println("--> Have results: " + symbol.name + ", " + (listOfBacktestEvaluation ==  null ? "0" : listOfBacktestEvaluation.size()));
			
			if (hashOfBacktestEvaluation.get(symbol).size() != 0){
				throw new IllegalStateException();
			}
			
			if (listOfBacktestEvaluation != null){
				hashOfBacktestEvaluation.get(symbol).addAll(listOfBacktestEvaluation);
			}
		}
		
		sendBacktestResult();
		requestNextUnit();
		
	}
}
