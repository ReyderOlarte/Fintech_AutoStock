package com.autoStock.backtest.watchmaker;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import com.autoStock.Co;
import com.autoStock.backtest.BacktestEvaluator;
import com.autoStock.internal.Global;
import com.autoStock.tools.Benchmark;
import com.autoStock.tools.DateTools;
import com.autoStock.tools.ListTools;
import com.autoStock.trading.types.HistoricalDataList;
import com.autoStock.types.Exchange;
import com.autoStock.types.Symbol;
import com.google.gson.internal.Pair;

/**
 * @author Kevin Kowalewski
 *
 */
public class WMBacktestDoD {
	private Exchange exchange;
	private ArrayList<Symbol> listOfSymbols;
	public final BacktestEvaluator backtestEvaluator = new BacktestEvaluator();
	private ArrayList<WMBacktestContainer> listOfWMBacktestContainer = new ArrayList<WMBacktestContainer>();
	private ArrayList<HistoricalDataList> listOfHistoricalDataList = new ArrayList<HistoricalDataList>();
	private Date dateStart;
	private Date dateEnd;
	private int currentDayIndex = 0;
	private Benchmark bench = new Benchmark();
	private static final int DOD_RUN_PERIOD = 12;
	private static final int DOD_TEST_PERIOD = 1;
	private static final int DOD_RUNS = 1;
	
	public WMBacktestDoD(Exchange exchange, Date dateStart, ArrayList<Symbol> listOfSymbols) {
		this.exchange = exchange;
		this.listOfSymbols = listOfSymbols;
		this.dateStart = dateStart;
		//this.dateEnd = dateEnd;
		
		Global.callbackLock.requestLock();
		
		if (listOfSymbols.size() == 0){
			throw new IllegalArgumentException("No symbols specified");
		}
		
		Co.println(String.format("--> Day over Day backtest starting at %s for %s days", DateTools.getPretty(dateStart), DOD_RUN_PERIOD));
		
//		if (DateTools.getListOfDatesOnWeekdays(dateStart, dateEnd).size() < DOD_RUN_PERIOD){
//			throw new IllegalArgumentException("Insufficient days for DoD. Needs / Supplied: " + DOD_RUN_PERIOD + ", " + (DateTools.getListOfDatesOnWeekdays(dateStart, dateEnd).size()));
//		}
		
		ListTools.removeDuplicates(listOfSymbols);
		bench.printTick("Started");
		
		for (int i=0; i<DOD_RUNS; i++){
			Pair <Date, Date> pair = getRunPeriodAtIndex(i);
			Co.println("--> DoD: " + pair.first + " to " + pair.second);
		}
		
		Co.println("\n");
		
		runStage1();
		
		bench.printTick("Ended");
		Global.callbackLock.releaseLock();
	}
	
	private void runStage1(){
		for (int i=0; i<DOD_RUNS; i++){
			Pair <Date, Date> pair = getRunPeriodAtIndex(i);
			
			Co.println("--> DoD: " + pair.first + ", " + pair.second);
			
			//listOfHistoricalDataList = BacktestUtils.getHistoricalDataList(exchange, dateStart, dateEnd, listOfSymbols);
			setupBacktestContainers(pair.first, pair.second);
			runBacktest();
		}
	}
	
	private Pair<Date, Date> getRunPeriodAtIndex(int index){
		GregorianCalendar calendarForStart = new GregorianCalendar();
		calendarForStart.setTime(dateStart);
		calendarForStart.roll(Calendar.DAY_OF_MONTH, index * DOD_RUN_PERIOD);
		
		while (DateTools.isWeekday(calendarForStart) == false){
			calendarForStart.roll(Calendar.DAY_OF_MONTH, 1);
		}
		
		GregorianCalendar calendarForEnd = (GregorianCalendar) calendarForStart.clone();
		calendarForEnd.roll(Calendar.DAY_OF_MONTH, DOD_RUN_PERIOD-1);
		
		Pair<Date, Date> pair = new Pair<Date, Date>(calendarForStart.getTime(), calendarForEnd.getTime());
		return pair;
	}
	
	private void setupBacktestContainers(Date dateStart, Date dateEnd){
		listOfWMBacktestContainer.clear();
		for (Symbol symbol : listOfSymbols){
			WMBacktestContainer wmBacktestContainer = new WMBacktestContainer(symbol, exchange, dateStart, dateEnd);
			listOfWMBacktestContainer.add(wmBacktestContainer);
		}
	}
	
	public void runBacktest(){
		for (WMBacktestContainer wmBacktestContainer : listOfWMBacktestContainer){
			wmBacktestContainer.runBacktest();
		}
	}
}
