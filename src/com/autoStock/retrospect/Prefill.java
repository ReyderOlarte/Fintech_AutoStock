package com.autoStock.retrospect;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import com.autoStock.Co;
import com.autoStock.algorithm.AlgorithmBase;
import com.autoStock.backtest.BacktestUtils.LookDirection;
import com.autoStock.database.DatabaseDefinitions.BasicQueries;
import com.autoStock.database.DatabaseDefinitions.QueryArg;
import com.autoStock.database.DatabaseDefinitions.QueryArgs;
import com.autoStock.database.DatabaseQuery;
import com.autoStock.exchange.request.RequestHistoricalData;
import com.autoStock.exchange.request.base.RequestHolder;
import com.autoStock.exchange.request.listener.RequestHistoricalDataListener;
import com.autoStock.exchange.results.ExResultHistoricalData.ExResultSetHistoricalData;
import com.autoStock.generated.basicDefinitions.TableDefinitions.DbStockHistoricalPrice;
import com.autoStock.signal.signalMetrics.SignalOfEncog;
import com.autoStock.strategy.StrategyOptions;
import com.autoStock.tools.DateConditions.QuoteAvailableDateCondition;
import com.autoStock.tools.DateTools;
import com.autoStock.tools.ListTools;
import com.autoStock.tools.Lock;
import com.autoStock.tools.QuoteSliceTools;
import com.autoStock.trading.platform.ib.definitions.HistoricalDataDefinitions.Resolution;
import com.autoStock.trading.types.HistoricalData;
import com.autoStock.types.Exchange;
import com.autoStock.types.QuoteSlice;
import com.autoStock.types.Symbol;
import com.autoStock.types.basic.Time;

/**
 * @author Kevin Kowalewski
 *
 */
public class Prefill {
	private Symbol symbol;
	private Exchange exchange;
	private PrefillMethod prefillMethod;
	private Calendar calendarForStart = GregorianCalendar.getInstance();
	private Calendar calendarForEnd = GregorianCalendar.getInstance();
	
	public static enum PrefillMethod {
		method_database,
		method_broker,
	}
	
	public Prefill(Symbol symbol, Exchange exchange, PrefillMethod prefillMethod){
		this.symbol = symbol;
		this.exchange = exchange;
		this.prefillMethod = prefillMethod;
	}
	
	public void prefillAlgorithm(AlgorithmBase algorithmBase, StrategyOptions strategyOptions){
		if (prefillMethod == PrefillMethod.method_database){
			prefillFromDatabase(algorithmBase, strategyOptions);
		}else if (prefillMethod == PrefillMethod.method_broker){
			prefillFromBroker(algorithmBase, strategyOptions);
		}else{
			throw new UnsupportedOperationException();
		}
		
//		Co.println("--> Added: " + algorithmBase.listOfQuoteSlice.size());
	}
	
	private void prefillFromBroker(final AlgorithmBase algorithmBase, StrategyOptions strategyOptions){
		setupPrefill(algorithmBase.startingDate, algorithmBase.exchange.timeOpenForeign, algorithmBase.exchange.timeCloseForeign, algorithmBase.getPeriodLength());
		
		Date startDate = new Date();
		startDate.setTime(startDate.getTime() - (algorithmBase.getPeriodLength() * 60 * 1000));
		
		HistoricalData historicalData = new HistoricalData(exchange, symbol, startDate, new Date(), Resolution.min);
		
		final Lock lock = new Lock();
		
//		Co.println("--> Trying to prefill from broker");
		
		RequestHistoricalData requestHistoricalData = new RequestHistoricalData(new RequestHolder(null), new RequestHistoricalDataListener() {
			@Override
			public void failed(RequestHolder requestHolder) {
				Co.println("--> Failed to prefill...");
				synchronized(lock){try {lock.notify();}catch(Exception e){}}
			}
			
			@Override
			public void completed(RequestHolder requestHolder, ExResultSetHistoricalData exResultSetHistoricalData) {
				Co.println("--> Prefill OK: " + exResultSetHistoricalData.listOfExResultRowHistoricalData.size());
				algorithmBase.listOfQuoteSlice.addAll(QuoteSliceTools.getListOfQuoteSliceFromExResultRowHistoricalData(exResultSetHistoricalData.listOfExResultRowHistoricalData));
				synchronized(lock){try {lock.notify();}catch(Exception e){}}
			}
		}, historicalData);
		
		synchronized(lock){try {lock.wait();}catch(Exception e){e.printStackTrace();}}
	}
	
	private void prefillFromDatabase(AlgorithmBase algorithmBase, StrategyOptions strategyOptions){
//		setupPrefill(listOfDate.get(0), algorithmBase.exchange.timeOpenForeign, algorithmBase.exchange.timeCloseForeign, algorithmBase.getPeriodLength());

		ArrayList<QuoteSlice> listOfQuoteSliceForReturn = new ArrayList<QuoteSlice>();
		ArrayList<Date> listOfDate = DateTools.getListOfDatesOnWeekdays(DateTools.getFirstWeekdayBefore(algorithmBase.startingDate), LookDirection.backward, 1, new QuoteAvailableDateCondition(algorithmBase.exchange, algorithmBase.symbol, algorithmBase.startingDate));
		HistoricalData historicalData = new HistoricalData(exchange, symbol, (Date)listOfDate.get(0).clone(), (Date)listOfDate.get(0).clone(), Resolution.min);
		historicalData.setStartAndEndDatesToExchange();
		
		ArrayList<QuoteSlice> listOfQuoteSlice = QuoteSliceTools.getListOfQuoteSliceFromDbStockHistoricalPrice((ArrayList<DbStockHistoricalPrice>) new DatabaseQuery().getQueryResults(BasicQueries.basic_historical_price_range, new QueryArg(QueryArgs.symbol, historicalData.symbol.name), new QueryArg(QueryArgs.exchange, historicalData.exchange.name), new QueryArg(QueryArgs.resolution, historicalData.resolution.asMinutes()), new QueryArg(QueryArgs.startDate, DateTools.getSqlDate(historicalData.startDate)), new QueryArg(QueryArgs.endDate, DateTools.getSqlDate(historicalData.endDate))));
		
		for (int i=listOfQuoteSlice.size()-1; i>listOfQuoteSlice.size()-algorithmBase.getPeriodLength(); i--){
			listOfQuoteSliceForReturn.add(listOfQuoteSlice.get(i));
		}
		
		// Period length could be less than prefill shift value so check accordingly!
		if (listOfQuoteSliceForReturn.size() > 0){
			for (int i=0; i<strategyOptions.prefillShift.value; i++){
				//Co.println("--> Shift: " + strategyOptions.prefillShift.value + " " + listOfQuoteSliceForReturn.size());
				if (listOfQuoteSliceForReturn.size() > 0){
					listOfQuoteSliceForReturn.remove(0);
				}
			}
		}
		
		//Co.println("--> Prefilled size: " + listOfQuoteSliceForReturn.size());
		algorithmBase.listOfQuoteSlice.addAll(ListTools.reverseList(listOfQuoteSliceForReturn));
	}
	
	public void setupPrefill(Date startingDate, Time timeOpenForeign, Time timeCloseForeign, int periodLength){
		calendarForStart.setTime(startingDate);
		calendarForStart.set(Calendar.SECOND, 0);
		calendarForEnd.setTime(startingDate);
		
		Time time = DateTools.getTimeUntilTime(DateTools.getTimeFromDate(startingDate), timeOpenForeign);
		
		int minutesFromDatabase = (periodLength - (time.asSeconds() / 60));
		int minutesFromBroker = time.asSeconds() / 60; 
		
//		Co.println("--> Minutes from database, broker: " + minutesFromDatabase + ", " + minutesFromBroker);
		
		if (prefillMethod == PrefillMethod.method_database){
//			Co.println("--> Only database");
			calendarForStart.add(Calendar.DAY_OF_MONTH, -1);
			calendarForStart.set(Calendar.HOUR, timeCloseForeign.hours);
			calendarForStart.set(Calendar.MINUTE, (minutesFromDatabase -1) * -1);
			
			calendarForEnd = (Calendar) calendarForStart.clone();
			calendarForEnd.set(Calendar.HOUR, timeCloseForeign.hours);
			calendarForEnd.set(Calendar.MINUTE, timeCloseForeign.minutes);
		}else if (prefillMethod == PrefillMethod.method_broker){
			
		}else{
			throw new UnsupportedOperationException();
		}
		
//		Co.println("--> Prefill: " + DateTools.getPrettyDate(calendarForStart.getTime()) + ", " + DateTools.getPrettyDate(calendarForEnd.getTime()));
	}
}
