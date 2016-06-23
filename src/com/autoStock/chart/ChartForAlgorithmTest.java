/**
 * 
 */
package com.autoStock.chart;

import java.util.ArrayList;
import java.util.Date;

import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.DefaultHighLowDataset;

import com.autoStock.algorithm.AlgorithmBase;
import com.autoStock.signal.SignalDefinitions.SignalMetricType;
import com.autoStock.strategy.StrategyOptions;
import com.autoStock.tools.ArrayTools;
import com.autoStock.tools.ResultsTools;

/**
 * @author Kevin Kowalewski
 * 
 */
public class ChartForAlgorithmTest {
	private String title;
	private AlgorithmBase algorithmBase;
	public ArrayList<Double> listOfSignalADX = new ArrayList<Double>();
	public ArrayList<Double> listOfSignalPPC = new ArrayList<Double>();
	public ArrayList<Double> listOfSignalDI = new ArrayList<Double>();
	public ArrayList<Double> listOfSignalCCI = new ArrayList<Double>();
	public ArrayList<Double> listOfSignalMACD = new ArrayList<Double>();
	public ArrayList<Double> listOfSignalSTORSI = new ArrayList<Double>();
	public ArrayList<Double> listOfSignalRSI = new ArrayList<Double>();
	public ArrayList<Double> listOfSignalTRIX = new ArrayList<Double>();
	public ArrayList<Double> listOfSignalMFI = new ArrayList<Double>();
	public ArrayList<Double> listOfSignalROC = new ArrayList<Double>();
	public ArrayList<Double> listOfSignalWILLR = new ArrayList<Double>();
	public ArrayList<Double> listOfSignalUO = new ArrayList<Double>();
	public ArrayList<Double> listOfSignalARUp = new ArrayList<Double>();
	public ArrayList<Double> listOfSignalARDown = new ArrayList<Double>();
	public ArrayList<Double> listOfSignalSAR = new ArrayList<Double>();
	public ArrayList<Double> listOfSignalCrossover = new ArrayList<Double>();
	public ArrayList<Double> listOfIndicatorSAR = new ArrayList<Double>();
	
	public ArrayList<Double> listOfIndicatorEMAFirst = new ArrayList<Double>();
	public ArrayList<Double> listOfIndicatorEMASecond = new ArrayList<Double>();
	
	//public ArrayList<Integer> listOfSignalSTORSID = new ArrayList<Integer>();

	public ArrayList<Date> listOfDate = new ArrayList<Date>();
	public ArrayList<Double> listOfSizeVolume = new ArrayList<Double>();
	
	public ArrayList<Double> listOfPriceOpen = new ArrayList<Double>();
	public ArrayList<Double> listOfPriceHigh = new ArrayList<Double>();
	public ArrayList<Double> listOfPriceLow = new ArrayList<Double>();
	public ArrayList<Double> listOfPriceClose = new ArrayList<Double>();
	
	public ArrayList<Double> listOfValue = new ArrayList<Double>();
	public ArrayList<Double> listOfYield = new ArrayList<Double>();
	
	public ArrayList<Double> listOfLongEntryAtPrice = new ArrayList<Double>();
	public ArrayList<Double> listOfShortEntryAtPrice = new ArrayList<Double>();
	public ArrayList<Double> listOfReEntryAtPrice = new ArrayList<Double>();
	public ArrayList<Double> listOfLongExitAtPrice = new ArrayList<Double>();
	public ArrayList<Double> listOfShortExitAtPrice = new ArrayList<Double>();
	
	public ArrayList<Double> listOfEntryAtSignal = new ArrayList<Double>();
	public ArrayList<Double> listOfExitAtSignal = new ArrayList<Double>();
	
	public ArrayList<Double> listOfMACD = new ArrayList<Double>();
	public ArrayList<Double> listOfDI = new ArrayList<Double>();
	public ArrayList<Double> listOfCCI = new ArrayList<Double>();
	public ArrayList<Double> listOfRSI = new ArrayList<Double>();
	
	public ArrayList<Double> listOfDebug1 = new ArrayList<Double>();
	public ArrayList<Double> listOfDebug2 = new ArrayList<Double>();
	public ArrayList<Double> listOfDebug3 = new ArrayList<Double>();
	
	public StrategyOptions strategyOptions;
	
	public ChartForAlgorithmTest(String title, AlgorithmBase algorithmBase){
		this.title = title;
		this.algorithmBase = algorithmBase;
	}
	
	public static enum TimeSeriesType {
		type_signals("Signals", 0),
		type_price("Price", 0),
		type_value("Value %", 0),
		type_yield("Yield %", 0),
		type_long_entry_price("Long Entry", 2),
		type_short_entry_price("Short Entry", 4),
		type_reentry_price("Reentry", 6),
		type_long_exit_price("Exit", 3),
		type_short_exit_price("Exit", 5),
		type_entry_signal("Entry", 0),
		type_exit_signal("Exit", 0),
		type_debug("Debug", 0),
		;
		
		public String displayName;
		public int seriesIndex; // hack
		
		TimeSeriesType(String displayName, int seriesIndex){
			this.displayName = displayName;
			this.seriesIndex = seriesIndex;
		}
	}

	public void display() {
		TimeSeriesCollection TSCForSignals = new TimeSeriesCollection();
		TimeSeriesCollection TSCForPrice = new TimeSeriesCollection();
		TimeSeriesCollection TSCForValue = new TimeSeriesCollection();
		TimeSeriesCollection TSCForYield = new TimeSeriesCollection();
		TimeSeriesCollection TSCForLongEntryAtPrice = new TimeSeriesCollection();
		TimeSeriesCollection TSCForShortEntryAtPrice = new TimeSeriesCollection();
		TimeSeriesCollection TSCForReEntryAtPrice = new TimeSeriesCollection();
		TimeSeriesCollection TSCForLongExitAtPrice = new TimeSeriesCollection();
		TimeSeriesCollection TSCForShortExitAtPrice = new TimeSeriesCollection();
		TimeSeriesCollection TSCForEntryAtSignal = new TimeSeriesCollection();
		TimeSeriesCollection TSCForExitAtSignal = new TimeSeriesCollection();
		TimeSeriesCollection TSCForDebug = new TimeSeriesCollection();
		
		DefaultHighLowDataset dataSetForDefaultHighLowDataset = new DefaultHighLowDataset("Price series", ArrayTools.getArrayFromListOfDates(listOfDate), ArrayTools.getArrayFromListOfDouble(listOfPriceHigh), ArrayTools.getArrayFromListOfDouble(listOfPriceLow), ArrayTools.getArrayFromListOfDouble(listOfPriceOpen), ArrayTools.getArrayFromListOfDouble(listOfPriceClose), ArrayTools.getArrayFromListOfDouble(listOfSizeVolume));

//		if (algorithmBase.listOfSignalMetricTypeAnalyze.contains(SignalMetricType.metric_adx)){TSCForSignals.addSeries(new ChartDataFiller().getTimeSeries("Signal ADX ", SignalMetricType.metric_adx, ResultsTools.getBasicPair(ArrayTools.getArrayFromListOfDates(listOfDate), ArrayTools.getArrayFromListOfDouble(listOfSignalADX))));}
//		if (algorithmBase.listOfSignalMetricTypeAnalyze.contains(SignalMetricType.metric_ppc)){TSCForSignals.addSeries(new ChartDataFiller().getTimeSeries("Signal PPC ", SignalMetricType.none, ResultsTools.getBasicPair(ArrayTools.getArrayFromListOfDates(listOfDate), ArrayTools.getArrayFromListOfDouble(listOfSignalPPC))));}
//		if (algorithmBase.listOfSignalMetricTypeAnalyze.contains(SignalMetricType.metric_di)){TSCForSignals.addSeries(new ChartDataFiller().getTimeSeries("Signal DI", SignalMetricType.metric_di, ResultsTools.getBasicPair(ArrayTools.getArrayFromListOfDates(listOfDate), ArrayTools.getArrayFromListOfDouble(listOfSignalDI))));}
//		if (algorithmBase.listOfSignalMetricTypeAnalyze.contains(SignalMetricType.metric_cci)){TSCForSignals.addSeries(new ChartDataFiller().getTimeSeries("Signal CCI", SignalMetricType.metric_cci, ResultsTools.getBasicPair(ArrayTools.getArrayFromListOfDates(listOfDate), ArrayTools.getArrayFromListOfDouble(listOfSignalCCI))));}
//		if (algorithmBase.listOfSignalMetricTypeAnalyze.contains(SignalMetricType.metric_macd)){TSCForSignals.addSeries(new ChartDataFiller().getTimeSeries("Signal MACD", SignalMetricType.metric_macd, ResultsTools.getBasicPair(ArrayTools.getArrayFromListOfDates(listOfDate), ArrayTools.getArrayFromListOfDouble(listOfSignalMACD))));}
//		if (algorithmBase.listOfSignalMetricTypeAnalyze.contains(SignalMetricType.metric_storsi)){TSCForSignals.addSeries(new ChartDataFiller().getTimeSeries("Signal STORSI", SignalMetricType.metric_storsi, ResultsTools.getBasicPair(ArrayTools.getArrayFromListOfDates(listOfDate), ArrayTools.getArrayFromListOfDouble(listOfSignalSTORSI))));}
//		if (algorithmBase.listOfSignalMetricTypeAnalyze.contains(SignalMetricType.metric_rsi)){TSCForSignals.addSeries(new ChartDataFiller().getTimeSeries("Signal RSI", SignalMetricType.metric_rsi, ResultsTools.getBasicPair(ArrayTools.getArrayFromListOfDates(listOfDate), ArrayTools.getArrayFromListOfDouble(listOfSignalRSI))));}
//		if (algorithmBase.listOfSignalMetricTypeAnalyze.contains(SignalMetricType.metric_trix)){TSCForSignals.addSeries(new ChartDataFiller().getTimeSeries("Signal TRIX", SignalMetricType.metric_trix, ResultsTools.getBasicPair(ArrayTools.getArrayFromListOfDates(listOfDate), ArrayTools.getArrayFromListOfDouble(listOfSignalTRIX))));}
//		if (algorithmBase.listOfSignalMetricTypeAnalyze.contains(SignalMetricType.metric_mfi)){TSCForSignals.addSeries(new ChartDataFiller().getTimeSeries("Signal MFI", SignalMetricType.metric_mfi, ResultsTools.getBasicPair(ArrayTools.getArrayFromListOfDates(listOfDate), ArrayTools.getArrayFromListOfDouble(listOfSignalMFI))));}
//		if (algorithmBase.listOfSignalMetricTypeAnalyze.contains(SignalMetricType.metric_roc)){TSCForSignals.addSeries(new ChartDataFiller().getTimeSeries("Signal ROC", SignalMetricType.metric_roc, ResultsTools.getBasicPair(ArrayTools.getArrayFromListOfDates(listOfDate), ArrayTools.getArrayFromListOfDouble(listOfSignalROC))));}
//		if (algorithmBase.listOfSignalMetricTypeAnalyze.contains(SignalMetricType.metric_willr)){TSCForSignals.addSeries(new ChartDataFiller().getTimeSeries("Signal WILLR", SignalMetricType.metric_willr, ResultsTools.getBasicPair(ArrayTools.getArrayFromListOfDates(listOfDate), ArrayTools.getArrayFromListOfDouble(listOfSignalWILLR))));}
//		if (algorithmBase.listOfSignalMetricTypeAnalyze.contains(SignalMetricType.metric_uo)){TSCForSignals.addSeries(new ChartDataFiller().getTimeSeries("Signal UO", SignalMetricType.metric_uo, ResultsTools.getBasicPair(ArrayTools.getArrayFromListOfDates(listOfDate), ArrayTools.getArrayFromListOfDouble(listOfSignalUO))));}
//		if (algorithmBase.listOfSignalMetricTypeAnalyze.contains(SignalMetricType.metric_ar_up)){TSCForSignals.addSeries(new ChartDataFiller().getTimeSeries("Signal AR", SignalMetricType.metric_ar_up, ResultsTools.getBasicPair(ArrayTools.getArrayFromListOfDates(listOfDate), ArrayTools.getArrayFromListOfDouble(listOfSignalARUp))));}
//		if (algorithmBase.listOfSignalMetricTypeAnalyze.contains(SignalMetricType.metric_ar_down)){TSCForSignals.addSeries(new ChartDataFiller().getTimeSeries("Signal AR", SignalMetricType.metric_ar_down, ResultsTools.getBasicPair(ArrayTools.getArrayFromListOfDates(listOfDate), ArrayTools.getArrayFromListOfDouble(listOfSignalARDown))));}
//		if (algorithmBase.listOfSignalMetricTypeAnalyze.contains(SignalMetricType.metric_sar)){TSCForSignals.addSeries(new ChartDataFiller().getTimeSeries("Signal SAR", SignalMetricType.metric_sar, ResultsTools.getBasicPair(ArrayTools.getArrayFromListOfDates(listOfDate), ArrayTools.getArrayFromListOfDouble(listOfSignalSAR))));}
//
		
		if (algorithmBase.listOfSignalMetricTypeAnalyze.contains(SignalMetricType.metric_crossover)){TSCForSignals.addSeries(new ChartDataFiller().getTimeSeries("Signal Crossover", SignalMetricType.metric_crossover, ResultsTools.getBasicPair(ArrayTools.getArrayFromListOfDates(listOfDate), ArrayTools.getArrayFromListOfDouble(listOfSignalCrossover))));}
//		TSCForSignals.addSeries(new ChartDataFiller().getTimeSeries("Signal Debug", SignalMetricType.none, ResultsTools.getBasicPair(ArrayTools.getArrayFromListOfDates(listOfDate), ArrayTools.getArrayFromListOfDouble(listOfDebug1))));
				
		//if (strategyOptions.listOfSignalMetricType.contains(SignalMetricType.metric_crossover)){
			TSCForPrice.addSeries(new ChartDataFiller().getTimeSeries("Indicator EMA First", SignalMetricType.metric_crossover, ResultsTools.getBasicPair(ArrayTools.getArrayFromListOfDates(listOfDate), ArrayTools.getArrayFromListOfDouble(listOfIndicatorEMAFirst))));
			TSCForPrice.addSeries(new ChartDataFiller().getTimeSeries("Indicator EMA Second", SignalMetricType.metric_crossover, ResultsTools.getBasicPair(ArrayTools.getArrayFromListOfDates(listOfDate), ArrayTools.getArrayFromListOfDouble(listOfIndicatorEMASecond))));
		//}
		
		if (strategyOptions.listOfSignalMetricType.contains(SignalMetricType.metric_sar)){
			TSCForSignals.addSeries(new ChartDataFiller().getTimeSeries("Indicator SAR", SignalMetricType.metric_sar, ResultsTools.getBasicPair(ArrayTools.getArrayFromListOfDates(listOfDate), ArrayTools.getArrayFromListOfDouble(listOfIndicatorSAR))));
		}

//		timeSeriesCollection2.addSeries(new ChartDataFiller().getTimeSeriesFromResults("DI Value", ResultsTools.getResultsAsListOfBasicTimeValuePair(ArrayTools.getArrayFromListOfDates(listOfDate), ArrayTools.getArrayFromListOfDouble(listOfDI))));
//		timeSeriesCollection2.addSeries(new ChartDataFiller().getTimeSeriesFromResults("CCI Value", ResultsTools.getResultsAsListOfBasicTimeValuePair(ArrayTools.getArrayFromListOfDates(listOfDate), ArrayTools.getArrayFromListOfDouble(listOfCCI))));
//		timeSeriesCollection2.addSeries(new ChartDataFiller().getTimeSeriesFromResults("MACD Value", ResultsTools.getResultsAsListOfBasicTimeValuePair(ArrayTools.getArrayFromListOfDates(listOfDate), ArrayTools.getArrayFromListOfDouble(listOfMACD))));
//		timeSeriesCollection2.addSeries(new ChartDataFiller().getTimeSeriesFromResults("RSI Value", ResultsTools.getResultsAsListOfBasicTimeValuePair(ArrayTools.getArrayFromListOfDates(listOfDate), ArrayTools.getArrayFromListOfDouble(listOfRSI))));
		TSCForPrice.addSeries(new ChartDataFiller().getTimeSeries("Price ($)", ResultsTools.getBasicPair(ArrayTools.getArrayFromListOfDates(listOfDate), ArrayTools.getArrayFromListOfDouble(listOfPriceClose))));
		if (listOfValue.size() != 0){
			TSCForValue.addSeries(new ChartDataFiller().getTimeSeries("Value (%)", ResultsTools.getBasicPair(ArrayTools.getArrayFromListOfDates(listOfDate), ArrayTools.getArrayFromListOfDouble(listOfValue))));
		}
		
		if (listOfYield.size() != 0){
			TSCForYield.addSeries(new ChartDataFiller().getTimeSeries("Yield (%)", ResultsTools.getBasicPair(ArrayTools.getArrayFromListOfDates(listOfDate), ArrayTools.getArrayFromListOfDouble(listOfYield))));
		}
		
		if (listOfLongEntryAtPrice.size() != 0){
			TSCForLongEntryAtPrice.addSeries(new ChartDataFiller().getTimeSeries("Long Entry", ResultsTools.getBasicPair(ArrayTools.getArrayFromListOfDates(listOfDate), ArrayTools.getArrayFromListOfDouble(listOfLongEntryAtPrice))));
		}
		
		if (listOfShortEntryAtPrice.size() != 0){
			TSCForShortEntryAtPrice.addSeries(new ChartDataFiller().getTimeSeries("Short Entry", ResultsTools.getBasicPair(ArrayTools.getArrayFromListOfDates(listOfDate), ArrayTools.getArrayFromListOfDouble(listOfShortEntryAtPrice))));
		}
		
		if (listOfReEntryAtPrice.size() != 0){
			TSCForReEntryAtPrice.addSeries(new ChartDataFiller().getTimeSeries("Reentry", ResultsTools.getBasicPair(ArrayTools.getArrayFromListOfDates(listOfDate), ArrayTools.getArrayFromListOfDouble(listOfReEntryAtPrice))));
		}
		
		if (listOfLongExitAtPrice.size() != 0){
			TSCForLongExitAtPrice.addSeries(new ChartDataFiller().getTimeSeries("Exit", ResultsTools.getBasicPair(ArrayTools.getArrayFromListOfDates(listOfDate), ArrayTools.getArrayFromListOfDouble(listOfLongExitAtPrice))));
		}
		
		if (listOfShortExitAtPrice.size() != 0){
			TSCForShortExitAtPrice.addSeries(new ChartDataFiller().getTimeSeries("Exit", ResultsTools.getBasicPair(ArrayTools.getArrayFromListOfDates(listOfDate), ArrayTools.getArrayFromListOfDouble(listOfShortExitAtPrice))));
		}
		
		if (listOfDebug1.size() != 0){
			TSCForSignals.addSeries(new ChartDataFiller().getTimeSeries("Debug 1", SignalMetricType.none, ResultsTools.getBasicPair(ArrayTools.getArrayFromListOfDates(listOfDate), ArrayTools.getArrayFromListOfDouble(listOfDebug1))));
		}
		
		if (listOfDebug2.size() != 0){
			TSCForSignals.addSeries(new ChartDataFiller().getTimeSeries("Debug 2", SignalMetricType.none, ResultsTools.getBasicPair(ArrayTools.getArrayFromListOfDates(listOfDate), ArrayTools.getArrayFromListOfDouble(listOfDebug2))));
		}
		
		if (listOfDebug3.size() != 0){
			TSCForSignals.addSeries(new ChartDataFiller().getTimeSeries("Debug 3", SignalMetricType.none, ResultsTools.getBasicPair(ArrayTools.getArrayFromListOfDates(listOfDate), ArrayTools.getArrayFromListOfDouble(listOfDebug3))));
		}
	
		new CombinedLineChart().new LineChartDisplay(title, 
			dataSetForDefaultHighLowDataset,
			algorithmBase,
			new TimeSeriesTypePair(TimeSeriesType.type_signals, TSCForSignals),
			new TimeSeriesTypePair(TimeSeriesType.type_price, TSCForPrice), 
			new TimeSeriesTypePair(TimeSeriesType.type_value, TSCForValue),
			new TimeSeriesTypePair(TimeSeriesType.type_yield, TSCForYield),
			new TimeSeriesTypePair(TimeSeriesType.type_long_entry_price, TSCForLongEntryAtPrice),
			new TimeSeriesTypePair(TimeSeriesType.type_short_entry_price, TSCForShortEntryAtPrice),
			new TimeSeriesTypePair(TimeSeriesType.type_reentry_price, TSCForReEntryAtPrice),
			new TimeSeriesTypePair(TimeSeriesType.type_long_exit_price, TSCForLongExitAtPrice),
			new TimeSeriesTypePair(TimeSeriesType.type_short_exit_price, TSCForShortExitAtPrice),
			new TimeSeriesTypePair(TimeSeriesType.type_entry_signal, TSCForEntryAtSignal),
			new TimeSeriesTypePair(TimeSeriesType.type_exit_signal, TSCForExitAtSignal),
			new TimeSeriesTypePair(TimeSeriesType.type_debug, TSCForDebug)
		);
		
	}
	
	public static class TimeSeriesTypePair {
		public TimeSeriesType timeSeriesType;
		public TimeSeriesCollection timeSeriesCollection;
		
		public TimeSeriesTypePair(TimeSeriesType timeSeriesType, TimeSeriesCollection timeSeriesCollection){
			this.timeSeriesType = timeSeriesType;
			this.timeSeriesCollection = timeSeriesCollection;
		}
	}
}
