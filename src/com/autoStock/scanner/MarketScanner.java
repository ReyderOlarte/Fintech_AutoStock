/**
 * 
 */
package com.autoStock.scanner;

import java.util.ArrayList;
import java.util.Date;

import com.autoStock.Co;
import com.autoStock.algorithm.AlgorithmTest;
import com.autoStock.algorithm.core.AlgorithmListener;
import com.autoStock.algorithm.reciever.ReceiverOfQuoteSlice;
import com.autoStock.backtest.Backtest;
import com.autoStock.position.PositionManager;
import com.autoStock.strategy.StrategyResponse;
import com.autoStock.types.QuoteSlice;
import com.autoStock.types.ShorlistOfStock;
import com.autoStock.types.Symbol;

/**
 * @author Kevin Kowalewski
 *
 */
public class MarketScanner implements ReceiverOfQuoteSlice, AlgorithmListener {
	private ArrayList<ShorlistOfStock> listOfShortlistedStocks;
	private ArrayList<Backtest> listOfBacktest = new ArrayList<Backtest>();
	private ArrayList<AlgorithmTest> listOfAlgorithmTest = new ArrayList<AlgorithmTest>();
	private Shortlist shortlist = new Shortlist("NYSE");
	private PositionManager positionManager = PositionManager.getGlobalInstance();
	private int endOfAlgorithmCount = 0;
	
	public MarketScanner(){
		Co.println("Getting shortlist");
		//listOfSymbolsFromDatabase = (ArrayList<DbSymbol>) new DatabaseQuery().getQueryResults(BasicQueries.basic_get_symbol_list_from_exchange, QueryArgs.exchange.setValue("NYSE"));
		listOfShortlistedStocks = shortlist.getShortlistedStocks();
	}
	
	public void startScan(){
		Co.println("Fetching symbol historical data...");
		for (ShorlistOfStock shortlistedStock : listOfShortlistedStocks){
//			HistoricalData typeHistoricalData = new HistoricalData(shortlistedStock.symbol, "STK", DateTools.getDateFromString("2011-01-05 09:30:00"), DateTools.getDateFromString("2011-01-05 15:30:00"), Resolution.min);
//			ArrayList<DbStockHistoricalPrice> listOfResults = (ArrayList<DbStockHistoricalPrice>) new DatabaseQuery().getQueryResults(
//					BasicQueries.basic_historical_price_range,
//					QueryArgs.symbol.setValue(typeHistoricalData.symbol),
//					QueryArgs.startDate.setValue(DateTools.getSqlDate(typeHistoricalData.startDate)),
//					QueryArgs.endDate.setValue(DateTools.getSqlDate(typeHistoricalData.endDate)));
					
//			Backtest backtest = new Backtest(typeHistoricalData, listOfResults, null);
		
//			if (listOfResults.size() != 0){
//				Co.println("Has size: " + listOfResults.size());
//				listOfBacktest.add(backtest);
//			}
		}
		
		Co.println("Initializing backtests... ");
		
		for (Backtest backtest : listOfBacktest){
			AlgorithmTest algorithmTest = new AlgorithmTest(null, null, null, null);
			algorithmTest.init(new Date(), new Date());
			algorithmTest.setAlgorithmListener(this);
			backtest.performBacktest(algorithmTest.getReceiver(), false);
			listOfAlgorithmTest.add(algorithmTest);
		}
	}

	@Override
	public void receiveQuoteSlice(QuoteSlice quoteSlice) {
		Co.println("Received slice: " + quoteSlice.symbol);
	}

	@Override
	public void endOfFeed(Symbol symbol) {
		Co.println("End of feed");
	}

	@Override
	public void receiveStrategyResponse(StrategyResponse strategyResponse) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void receiveChangedStrategyResponse(StrategyResponse strategyResponse) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void endOfAlgorithm() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void receiveTick(QuoteSlice quote, int index, int processedIndex, boolean processed) {
		
	}

	@Override
	public void initialize(Date startingDate, Date endDate) {
		
	}

//	@Override
//	public void recieveSignal(Signal signal, QuoteSlice typeQuoteSlice) {
//
//	}

//	@Override
//	public void endOfAlgorithm() {
//		Co.println("End of algo: " + endOfAlgorithmCount + "," + listOfAlgorithmTest.size());
//		if (endOfAlgorithmCount == listOfAlgorithmTest.size()-1){
//			PositionManager.getInstance().executeSellAll();
//			Co.println("Account balance: " + Account.getInstance().getAccountBalance() + " Fees paid: " + Account.getInstance().getTransactionFeesPaid());
//		}
//		
//		endOfAlgorithmCount++;
//	}
//
//	@Override
//	public void receiveStrategyResponse(PositionGovernorResponse positionGovernorResponse) {
//	
//	}
}
