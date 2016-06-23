package com.autoStock.backtest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;

import com.autoStock.Co;
import com.autoStock.account.AccountProvider;
import com.autoStock.account.BasicAccount;
import com.autoStock.algorithm.AlgorithmTest;
import com.autoStock.algorithm.core.AlgorithmDefinitions.AlgorithmMode;
import com.autoStock.algorithm.core.AlgorithmRemodeler;
import com.autoStock.algorithm.extras.StrategyOptionsOverride;
import com.autoStock.algorithm.reciever.ReceiverOfQuoteSlice;
import com.autoStock.generated.basicDefinitions.TableDefinitions.DbStockHistoricalPrice;
import com.autoStock.position.PositionDefinitions.PositionType;
import com.autoStock.position.PositionManager;
import com.autoStock.signal.SignalBase;
import com.autoStock.signal.SignalCache;
import com.autoStock.signal.SignalRangeLimit;
import com.autoStock.strategy.StrategyOptionDefaults;
import com.autoStock.strategy.StrategyOptions;
import com.autoStock.strategy.StrategyResponse;
import com.autoStock.tools.DateTools;
import com.autoStock.trading.types.HistoricalData;
import com.autoStock.trading.types.Position;
import com.autoStock.types.Exchange;
import com.autoStock.types.QuoteSlice;
import com.autoStock.types.Symbol;
import com.google.gson.internal.Pair;

/**
 * @author Kevin Kowalewski
 * 
 */
public class BacktestContainer implements ReceiverOfQuoteSlice {
	private final boolean USE_PRECOMPUTED_ALGORITHM_MODEL = true;
	private final boolean USE_SO_OVERRIDE = true;
	public final Symbol symbol;
	public final Exchange exchange;
	public HistoricalData historicalData;
	public final AlgorithmTest algorithm;
	private ListenerOfBacktest listener;
	private Backtest backtest;
	private AlgorithmMode algorithmMode;
	private boolean isComplete;
	private BasicAccount basicAccount;
	private ArrayList<DbStockHistoricalPrice> listOfDbHistoricalPrices = new ArrayList<DbStockHistoricalPrice>();
	public ArrayList<StrategyResponse> listOfStrategyResponse = new ArrayList<StrategyResponse>();
	public HashMap<SignalBase, SignalRangeLimit> hashOfSignalRangeLimit = new HashMap<SignalBase, SignalRangeLimit>();
	public ArrayList<Pair<Date, Double>> listOfYield = new ArrayList<Pair<Date, Double>>();
	
	public SignalCache signalCache;
	
	public Date dateContainerStart;
	public Date dateContainerEnd;

	@SuppressWarnings({ "unchecked", "unused" })
	public BacktestContainer(Symbol symbol, Exchange exchange, ListenerOfBacktest listener, AlgorithmMode algorithmMode) {
		this.symbol = symbol;
		this.exchange = exchange;
		this.listener = listener;
		this.algorithmMode = algorithmMode;
		
		if (algorithmMode == AlgorithmMode.mode_backtest){
			basicAccount = AccountProvider.getInstance().getGlobalAccount();
		}else{
			basicAccount = AccountProvider.getInstance().newAccountForSymbol(symbol);			
		}
		
		algorithm = new AlgorithmTest(exchange, symbol, algorithmMode, basicAccount);
		algorithm.signalCache = signalCache;

		if (algorithmMode == AlgorithmMode.mode_backtest && USE_PRECOMPUTED_ALGORITHM_MODEL){
			StrategyOptionsOverride override = StrategyOptionDefaults.getDefaultOverride();
			
			AlgorithmModel algorithmModel = BacktestEvaluationReader.getPrecomputedModel(exchange, symbol, USE_SO_OVERRIDE ? override : null);
			if (algorithmModel != null){
				Co.println("--> Evaluation available");
				new AlgorithmRemodeler(algorithm, algorithmModel).remodel(true, true, true, false);
				Co.println("--> Evaluation available");
			}
		}
	}
	
	public void setSignalCache(SignalCache signalCache){
		if (signalCache != null){
			this.signalCache = signalCache;
			this.signalCache.algorithmBase = algorithm;
			algorithm.signalCache = this.signalCache;
		}
	}

	public void setBacktestData(ArrayList<DbStockHistoricalPrice> listOfDbStockHistoricalPrice, HistoricalData historicalData){
		this.listOfDbHistoricalPrices = listOfDbStockHistoricalPrice;
		this.historicalData = historicalData;
		
		setContainerDates();

		algorithm.init(historicalData.startDate, historicalData.endDate);
		
		BacktestUtils.pruneToExchangeHours(listOfDbStockHistoricalPrice, exchange);
	}
	
	public void prepare(){
		if (listOfDbHistoricalPrices.size() == 0) {
			endOfFeed(symbol);
			return;
		}
		
		backtest = new Backtest(historicalData, listOfDbHistoricalPrices, symbol);
	}
	
	public void perform(boolean beBlocking){
		backtest.performBacktest(this, beBlocking);
	}

	public void runBacktest() {
		prepare();
		perform(false);
	}
	
	private void setContainerDates(){
		if (dateContainerStart == null){
			dateContainerStart = historicalData.startDate;
		}else{
			dateContainerStart = DateTools.getEarliestDate(Arrays.asList(new Date[]{dateContainerStart, historicalData.startDate}));
		}
		
		if (dateContainerEnd == null){
			dateContainerEnd = historicalData.endDate;
		}else{
			dateContainerEnd = DateTools.getLatestDate(Arrays.asList(new Date[]{dateContainerEnd, historicalData.endDate}));
		}
	}

	public void setListener(ListenerOfBacktest listenerOfBacktestCompleted) {
		this.listener = listenerOfBacktestCompleted;
	}

	public void reset() {
		listOfStrategyResponse.clear();
		algorithm.basicAccount.reset();
		hashOfSignalRangeLimit.clear();
		listOfYield.clear();
		algorithm.positionGovernor.reset();
		Co.println("******* RESET");
	}

	@Override
	public void receiveQuoteSlice(QuoteSlice quoteSlice) {
		algorithm.receiveQuoteSlice(quoteSlice);
		
//		try {
//		}catch(IllegalStateException e){
//			e.printStackTrace();
//			Co.println(new BacktestEvaluationBuilder().buildEvaluation(this).toString());
//			
//			Co.println("--> Len: " + algorithm.tableForAlgorithm.getDisplayRows().size());
//			
//			new TableController().displayTable(AsciiTables.algorithm, algorithm.tableForAlgorithm.getDisplayRows());
//			ApplicationStates.shutdown();
//		}
	}

	@Override
	public void endOfFeed(Symbol symbol) {
		listOfYield.add(new Pair<Date, Double>(historicalData.startDate, algorithm.getYieldCurrent()));
		
		//If running with mode exchange
		if (algorithmMode == AlgorithmMode.mode_engagement && PositionManager.getGlobalInstance().getPosition(symbol) != null){
			Co.println("--> Warning! Exchange data ends before exchange close and a position exists...");
			Position position = PositionManager.getGlobalInstance().getPosition(symbol);
		
			if (position.positionType == PositionType.position_long){
				PositionManager.getGlobalInstance().executePosition(algorithm.getCurrentQuoteSlice(), exchange, algorithm.strategyBase.signaler, PositionType.position_long_exit, position, null, basicAccount);
			}else if (position.positionType == PositionType.position_short){
				PositionManager.getGlobalInstance().executePosition(algorithm.getCurrentQuoteSlice(), exchange, algorithm.strategyBase.signaler, PositionType.position_short_exit, position, null, basicAccount);
			}else{
				throw new IllegalStateException();
			}
			
//			Co.print(new BacktestEvaluationBuilder().buildEvaluation(this).toString());
//			throw new IllegalStateException("Position manager still has position for: " + symbol.symbolName);
		}else if (algorithm.position != null && (algorithmMode == AlgorithmMode.mode_backtest_single_with_tables || algorithmMode == AlgorithmMode.mode_backtest_single_no_tables || algorithmMode == AlgorithmMode.mode_backtest) && (algorithm.position.positionType == PositionType.position_long || algorithm.position.positionType == PositionType.position_short)){
			algorithm.requestExitExternally();
			//throw new IllegalStateException("EOF Yet position exists: " + symbol.symbolName);
		}
		
		listOfStrategyResponse.addAll(algorithm.listOfStrategyResponse);
		
		algorithm.endOfFeed(symbol);
		listener.onCompleted(symbol, algorithm);
	}

	public void markAsComplete() {
		isComplete = true;
		if (algorithm.signalCache != null && SignalCache.CACHE_FILE.exists() == false){algorithm.signalCache.writeToDisk();}
	}

	public void markAsIncomplete() {
		isComplete = false;
	}

	public boolean isIncomplete() {
		return !isComplete;
	}
}
