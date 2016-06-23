package com.autoStock.backtest.encog;

import java.util.ArrayList;
import java.util.Date;

import com.autoStock.backtest.BacktestEvaluator;
import com.autoStock.backtest.BacktestUtils;
import com.autoStock.internal.Global;
import com.autoStock.tools.Benchmark;
import com.autoStock.tools.ListTools;
import com.autoStock.trading.types.HistoricalData;
import com.autoStock.trading.types.HistoricalDataList;
import com.autoStock.types.Exchange;
import com.autoStock.types.Symbol;

/**
 * @author Kevin Kowalewski
 *
 */
public class EncogBacktest {
	private Exchange exchange;
	private ArrayList<Symbol> listOfSymbols;
	public final BacktestEvaluator backtestEvaluator = new BacktestEvaluator();
	private ArrayList<EncogBacktestContainer> listOfBacktestContainer = new ArrayList<EncogBacktestContainer>();
	private ArrayList<HistoricalDataList> listOfHistoricalDataList = new ArrayList<HistoricalDataList>();
	private Date dateStart;
	private Date dateEnd;
	private int currentDayIndex = 0;
	private Benchmark bench = new Benchmark();
	
	public EncogBacktest(Exchange exchange, Date dateStart, Date dateEnd, ArrayList<Symbol> listOfSymbols) {
		this.exchange = exchange;
		this.listOfSymbols = listOfSymbols;
		this.dateStart = dateStart;
		this.dateEnd = dateEnd;
		
		Global.callbackLock.requestLock();
		
		if (listOfSymbols.size() == 0){
			throw new IllegalArgumentException("No symbols specified");
		}
		
		ListTools.removeDuplicates(listOfSymbols);
		listOfHistoricalDataList = BacktestUtils.getHistoricalDataList(exchange, dateStart, dateEnd, listOfSymbols);
		
		bench.printTick("Started");
		
		setupBacktestContainers();
		runBacktest();
		
		bench.printTick("Ended");
		Global.callbackLock.releaseLock();
	}
	
	private void setupBacktestContainers(){
		for (Symbol symbol : listOfSymbols){
			HistoricalData historicalData = new HistoricalData(exchange, symbol, dateStart, dateEnd, null);
			EncogBacktestContainer backtestContainer = new EncogBacktestContainer(symbol, exchange, dateStart, dateEnd);
			listOfBacktestContainer.add(backtestContainer);
		}
	}
	
	public void runBacktest(){
		for (EncogBacktestContainer backtestContainer : listOfBacktestContainer){
			backtestContainer.runBacktest();
		}
	}
}
