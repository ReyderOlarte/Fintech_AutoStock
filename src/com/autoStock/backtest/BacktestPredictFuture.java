/**
 * 
 */
package com.autoStock.backtest;

import java.awt.Color;
import java.awt.Dimension;
import java.nio.channels.IllegalSelectorException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.TimeZone;

import javax.swing.JPanel;

import org.encog.engine.network.activation.ActivationFunction;
import org.encog.engine.network.activation.ActivationTANH;
import org.encog.mathutil.randomize.NguyenWidrowRandomizer;
import org.encog.ml.data.MLData;
import org.encog.ml.data.MLDataPair;
import org.encog.ml.data.MLDataSet;
import org.encog.ml.data.basic.BasicMLData;
import org.encog.ml.data.basic.BasicMLDataPair;
import org.encog.ml.data.basic.BasicMLDataSet;
import org.encog.ml.data.temporal.TemporalDataDescription;
import org.encog.ml.data.temporal.TemporalDataDescription.Type;
import org.encog.ml.data.temporal.TemporalMLDataSet;
import org.encog.ml.data.temporal.TemporalPoint;
import org.encog.ml.train.MLTrain;
import org.encog.neural.data.basic.BasicNeuralData;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.training.TrainingSetScore;
import org.encog.neural.networks.training.propagation.back.Backpropagation;
import org.encog.neural.networks.training.propagation.manhattan.ManhattanPropagation;
import org.encog.neural.networks.training.propagation.resilient.RPROPType;
import org.encog.neural.networks.training.propagation.resilient.ResilientPropagation;
import org.encog.neural.networks.training.pso.NeuralPSO;
import org.encog.neural.pattern.ElmanPattern;
import org.encog.neural.pattern.FeedForwardPattern;
import org.encog.neural.pattern.JordanPattern;
import org.encog.util.arrayutil.NormalizationAction;
import org.encog.util.arrayutil.NormalizedField;
import org.encog.util.time.TimeUnit;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.SegmentedTimeline;
import org.jfree.chart.axis.Timeline;
import org.jfree.chart.plot.CombinedDomainXYPlot;
import org.jfree.chart.plot.DatasetRenderingOrder;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.CandlestickRenderer;
import org.jfree.chart.renderer.xy.StandardXYItemRenderer;
import org.jfree.data.time.Minute;
import org.jfree.data.time.RegularTimePeriod;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.time.TimeSeriesDataItem;
import org.jfree.data.xy.DefaultHighLowDataset;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RectangleInsets;
import org.jfree.ui.RefineryUtilities;
import org.slf4j.helpers.SubstituteLoggerFactory;

import com.autoStock.Co;
import com.autoStock.chart.ChartDataFiller;
import com.autoStock.chart.ChartForAlgorithmTest.TimeSeriesType;
import com.autoStock.database.DatabaseDefinitions.BasicQueries;
import com.autoStock.database.DatabaseDefinitions.QueryArg;
import com.autoStock.database.DatabaseDefinitions.QueryArgs;
import com.autoStock.database.DatabaseQuery;
import com.autoStock.finance.SecurityTypeHelper.SecurityType;
import com.autoStock.generated.basicDefinitions.TableDefinitions.DbStockHistoricalPrice;
import com.autoStock.indicator.CommonAnalysisData;
import com.autoStock.internal.ApplicationStates;
import com.autoStock.internal.Global;
import com.autoStock.signal.extras.EncogNetworkProvider;
import com.autoStock.tools.ArrayTools;
import com.autoStock.tools.DateTools;
import com.autoStock.tools.ListTools;
import com.autoStock.tools.MathTools;
import com.autoStock.tools.QuoteSliceTools;
import com.autoStock.tools.ResultsTools;
import com.autoStock.tools.StringTools;
import com.autoStock.trading.platform.ib.definitions.HistoricalDataDefinitions.Resolution;
import com.autoStock.trading.types.HistoricalData;
import com.autoStock.types.Exchange;
import com.autoStock.types.Symbol;
import com.autoStock.misc.Pair;

/**
 * @author Kevin
 *
 */
public class BacktestPredictFuture {
	private static final int DAY_GAPS_SIZE = 0;
	private static final int INPUT_PER_ITEM = 30;
	private static final int IDEAL_OFFSET = 1;
	private static final int IDEAL_SIZE = 1 ;
	
	private static final boolean DISCARD = true;
	private Exchange exchange = new Exchange("NYSE");
	private Symbol symbol = new Symbol("MS", SecurityType.type_stock);
	private Date dateStart = DateTools.getDateFromString("01/03/2014");
	private Date dateEnd = DateTools.getDateFromString("01/03/2015");
	private double crossValidationRatio = 0.25;
	private ArrayList<PredictionResult> listOfPredictionResult = new ArrayList<PredictionResult>();
	private PredictionResult result = new PredictionResult(0, 0);
	
	private BasicMLDataSet mlDataSetReg;
	private BasicMLDataSet mlDataSetCross;
	private DefaultHighLowDataset dataSetForDefaultHighLowDataset;
	private TimeSeriesCollection TSC = new TimeSeriesCollection();
	private NormalizedField normalizer = new NormalizedField(NormalizationAction.Normalize, null, 0.010, -0.010, 1, -1);
	private DecimalFormat dc = new DecimalFormat("#.########");
	
	private BasicNetwork network;
	private CommonAnalysisData commonAnalysisData = new CommonAnalysisData();
	
	private int crossCount = 0;
	private int regularEnd = 0;
	
	public BacktestPredictFuture(){
		Global.callbackLock.requestLock();
	}
	
	private static class PredictionResult {
		public int countCorrect = 0;
		public int countIncorrect = 0;
		
		public PredictionResult(int countCorrect, int countIncorrect) {
			this.countCorrect = countCorrect;
			this.countIncorrect = countIncorrect;
		}
	}
	
	@SuppressWarnings("unused")
	public void run(){
		// Load the data
		HistoricalData historicalData = new HistoricalData(new Exchange("NYSE"), new Symbol("MS", SecurityType.type_stock), dateStart, dateEnd, Resolution.min);
		historicalData.setStartAndEndDatesToExchange();
		ArrayList<DbStockHistoricalPrice> listOfResultsIS = (ArrayList<DbStockHistoricalPrice>) new DatabaseQuery().getQueryResults(BasicQueries.basic_historical_price_range, new QueryArg(QueryArgs.symbol, historicalData.symbol.name), new QueryArg(QueryArgs.exchange, new Exchange("NYSE").name), new QueryArg(QueryArgs.resolution, Resolution.min.asMinutes()), new QueryArg(QueryArgs.startDate, DateTools.getSqlDate(historicalData.startDate)), new QueryArg(QueryArgs.endDate, DateTools.getSqlDate(historicalData.endDate)));
		
		BacktestUtils.pruneToExchangeHours(listOfResultsIS, new Exchange("NYSE"));
		
		BasicMLDataSet dataSet = new BasicMLDataSet();
		
		// Build the input & ideal lists as % deltas
		ArrayList<Pair<DbStockHistoricalPrice, MLData>> priceIdealList = new ArrayList<>();
		
		commonAnalysisData.setAnalysisData(QuoteSliceTools.getListOfQuoteSliceFromDbStockHistoricalPrice(listOfResultsIS));
		
		double pero[] = new double[commonAnalysisData.length()];
		double perh[] = new double[commonAnalysisData.length()];
		double perl[] = new double[commonAnalysisData.length()];
		double perc[] = new double[commonAnalysisData.length()];
		double peroc[] = new double[commonAnalysisData.length()];
		double perlh[] = new double[commonAnalysisData.length()];
		
		Co.println("--> C: " + commonAnalysisData.length());
		
		for (int i=0; i<15; i++){
			Co.println("--> PC: " + commonAnalysisData.arrayOfPriceClose[i]);
		}
		
		int discard = 0;
		Date lastDate = null;
		ArrayList<Integer> discards = new ArrayList<>();
		
		for (int i=0; i<commonAnalysisData.length() - IDEAL_OFFSET - INPUT_PER_ITEM - IDEAL_SIZE; i++){
			
			if (lastDate == null){lastDate = (Date) commonAnalysisData.arrayOfDates[i].clone(); lastDate.setHours(0); lastDate.setMinutes(0); lastDate.setSeconds(0);}
			Date predDate = (Date)commonAnalysisData.arrayOfDates[i + IDEAL_OFFSET + IDEAL_SIZE].clone(); predDate.setHours(0); predDate.setMinutes(0); predDate.setSeconds(0);

			if (predDate.getTime() != lastDate.getTime()){
				//Co.println("--> Prediction spans into " + lastDate + " to " + predDate);
				lastDate = predDate;
				discard = INPUT_PER_ITEM + IDEAL_OFFSET;
			}
			
			if (DISCARD && discard > 0 && i < commonAnalysisData.length() - (IDEAL_OFFSET + IDEAL_SIZE)){
				Co.println("--> Discarding: " + commonAnalysisData.arrayOfDates[i + 1 + IDEAL_OFFSET]);
				discards.add(i + 1 + IDEAL_OFFSET);
				discard--; continue;
			}
			
			pero[i] = (commonAnalysisData.arrayOfPriceOpen[i + 1] / commonAnalysisData.arrayOfPriceOpen[i]) -1;
			perh[i] = (commonAnalysisData.arrayOfPriceHigh[i + 1] / commonAnalysisData.arrayOfPriceHigh[i]) -1;
			perl[i] = (commonAnalysisData.arrayOfPriceLow[i + 1] / commonAnalysisData.arrayOfPriceLow[i]) -1;
			perc[i] = (commonAnalysisData.arrayOfPriceClose[i + 1] / commonAnalysisData.arrayOfPriceClose[i]) -1;
			
			peroc[i] = (commonAnalysisData.arrayOfPriceOpen[i + 1] / commonAnalysisData.arrayOfPriceClose[i + 1]) -1;
			perlh[i] = (commonAnalysisData.arrayOfPriceLow[i + 1] / commonAnalysisData.arrayOfPriceHigh[i + 1]) -1;
			
			//Co.println("--> Diff: " + commonAnalysisData.arrayOfPriceClose[i] + " -> " + commonAnalysisData.arrayOfPriceClose[i + 1] + " = " + new DecimalFormat("#.#######").format(perc[i]));
			
			if (i >= INPUT_PER_ITEM){
				//Co.println("--> Last input window date: " + commonAnalysisData.arrayOfDates[i + 1]);
				
				double[] ideal = new double[IDEAL_SIZE];
						
				for (int idealIndex = 0; idealIndex < IDEAL_SIZE; idealIndex++){
					int idealInSet = i + 1 + IDEAL_OFFSET + idealIndex;
					double idealValue = (commonAnalysisData.arrayOfPriceClose[i + 1 + IDEAL_OFFSET + idealIndex] / commonAnalysisData.arrayOfPriceClose[i + IDEAL_OFFSET]) -1;
					
					//if (idealValue > 0){idealValue = 0.010;}
					//if (idealValue  < 0){idealValue = -0.010;}
					//ideal[idealIndex] = idealValue; 
					
					ideal[idealIndex] = normalizer.normalize(idealValue);
					//Co.println("--> Added ideal: " + dataSet.size() + " : " + commonAnalysisData.arrayOfDates[idealInSet] + ", " + new DecimalFormat("#.########").format(ideal[idealIndex]) + " from price " + commonAnalysisData.arrayOfPriceClose[i + IDEAL_OFFSET] + " to price " + (commonAnalysisData.arrayOfPriceClose[i + 1 + IDEAL_OFFSET + idealIndex]));					
				}
				
				ArrayList<Double> input = new ArrayList<Double>();
				input.addAll(ListTools.getListFromArray(Arrays.copyOfRange(pero, i -INPUT_PER_ITEM, i)));
				input.addAll(ListTools.getListFromArray(Arrays.copyOfRange(perh, i -INPUT_PER_ITEM, i)));
				input.addAll(ListTools.getListFromArray(Arrays.copyOfRange(perl, i -INPUT_PER_ITEM, i)));
				input.addAll(ListTools.getListFromArray(Arrays.copyOfRange(perc, i -INPUT_PER_ITEM, i)));
//				input.addAll(ListTools.getListFromArray(Arrays.copyOfRange(peroc, i -INPUT_PER_ITEM, i)));
//				input.addAll(ListTools.getListFromArray(Arrays.copyOfRange(perlh, i -INPUT_PER_ITEM, i)));
				
				for (int r=0; r<input.size(); r++){
					double normInput = normalizer.normalize(input.get(r));
					//Co.println("Norm: " + new DecimalFormat("#.########").format(input.get(r)) + " to " + new DecimalFormat("#.########").format(normInput));					
					input.set(r, normInput);
				}
				
				BasicMLDataPair pair = new BasicMLDataPair(
				new BasicMLData(ArrayTools.getArrayFromListOfDouble(input)),
				new BasicMLData(ideal));
				dataSet.add(pair);
				
				//Co.println("\n");
			}
		}
		
		int removed = 0;
		
		for (Integer i : discards){
			//Co.println("--> NEED TO DISCARD at: " + i);
			commonAnalysisData.remove(i - removed);
			removed++;
		}
		
//		double min = Double.MAX_VALUE;
//		double max = Double.MIN_VALUE;
//		
//		for (MLDataPair pair : BasicMLDataSet.toList(dataSet)){
//			if (pair.getIdeal().getData(0) == normalizer.getNormalizedHigh()){throw new IllegalStateException("Check normalizer: " + pair.getIdeal().getData(0));}
//			min = Math.min(min, pair.getIdeal().getData(0));
//			max = Math.max(max, pair.getIdeal().getData(0));
//		} 
		
//		Co.println("--> Min/Max: " + min + ", " + max);
//		if (true){return;}
		
		if (crossValidationRatio == 0){
			mlDataSetReg = dataSet;
		} else{
			crossCount = (int) (crossValidationRatio * (double)dataSet.size());
			regularEnd = dataSet.size() - crossCount;
			
			Co.println("--> DSSize: " + dataSet.size());
			Co.println("--> Regular end: " + regularEnd);
			Co.println("--> Cross start: " + (regularEnd + 1));
			
			//mlDataSetReg = dataSet;
			mlDataSetReg = new BasicMLDataSet(BasicMLDataSet.toList(dataSet).subList(0, regularEnd));
			mlDataSetCross = new BasicMLDataSet(BasicMLDataSet.toList(dataSet).subList(regularEnd, dataSet.size()));
			
			Co.println("--> Check sizes: " + mlDataSetReg.size() + ", " + mlDataSetCross.size());
		}
		
		network = getMLNetwork(dataSet.getInputSize(), 1);
		network.reset();
		
		MLTrain train = new ResilientPropagation(network, mlDataSetReg);
		((ResilientPropagation)train).setRPROPType(RPROPType.iRPROPp);
		
		for (int i=0; i<4096; i++){
			train.iteration();
			Co.println(i + ". " + (train.getError() * 1000));
			
			if (i % 25 == 0){
				computeAccuracy(mlDataSetReg, false);
				if (crossValidationRatio > 0){computeAccuracy(mlDataSetCross, true);}
			}
		}
		
		train.finishTraining();
		
		//mlDataSetReg = new BasicMLDataSet(BasicMLDataSet.toList(dataSet).subList(0, regularEnd));
		
		new EncogNetworkProvider().saveNetwork((BasicNetwork) train.getMethod(), historicalData.exchange.name + "-" + historicalData.symbol.name + "-predict");
		
		// Compute Regular
		computeAccuracy(mlDataSetReg, false);
		
		// Compute Cross
		if (crossValidationRatio > 0){computeAccuracy(mlDataSetCross, true);}
		
		if (true){return;}
		
		//TSC.addSeries(new ChartDataFiller().getTimeSeries("Price ($)", ResultsTools.getBasicPair(commonAnalysisData.arrayOfDates, commonAnalysisData.arrayOfPriceClose)));
		
		TimeSeries tsPrice = new TimeSeries("Price");
		
		Date day = null;
		Date cur = null;
		
		for (int i=0; i<commonAnalysisData.length(); i++){
			if (day == null){day = (Date) commonAnalysisData.arrayOfDates[i].clone();} day.setHours(0); day.setMinutes(0); day.setSeconds(0);
			cur = (Date) commonAnalysisData.arrayOfDates[i].clone(); cur.setHours(0); cur.setMinutes(0); cur.setSeconds(0);
			
			if (day.getTime() != cur.getTime()){
				Date temp = (Date) commonAnalysisData.arrayOfDates[i -1].clone();
				
				for (int x=1; x<=DAY_GAPS_SIZE; x++){
					temp = DateTools.getRolledDate(temp, Calendar.MINUTE, 1);
					tsPrice.add(new TimeSeriesDataItem(new Minute(temp), null));
					Co.println("--> Added null at: " + temp);
				}
				
				day = cur;
			}
			
			tsPrice.add(new TimeSeriesDataItem(new Minute(commonAnalysisData.arrayOfDates[i]), commonAnalysisData.arrayOfPriceClose[i]));
		}
		
		TSC.addSeries(tsPrice);

		// Compute Cross
		
		addTimeSeries(mlDataSetReg, new TimeSeries("Regular"), false);
		addTimeSeries(mlDataSetCross, new TimeSeries("Cross"), true);
		
		dataSetForDefaultHighLowDataset = new DefaultHighLowDataset("Price series", commonAnalysisData.arrayOfDates, commonAnalysisData.arrayOfPriceHigh, commonAnalysisData.arrayOfPriceLow, commonAnalysisData.arrayOfPriceOpen, commonAnalysisData.arrayOfPriceClose, ArrayTools.convertToDouble(commonAnalysisData.arrayOfSizeVolume));
		
		new PredictDisplay("autoStock - Forward Price Prediction");
	}
	
	private void computeAccuracy(BasicMLDataSet mlDataSet, boolean isCross){
		result.countCorrect = 0;
		result.countIncorrect = 0;
		
		for (int i=0; i<mlDataSet.getRecordCount(); i++){
			double idealValue = normalizer.deNormalize(mlDataSet.getData().get(i).getIdeal().getData(0));
			double computedValue = normalizer.deNormalize(network.compute(mlDataSet.getData().get(i).getInput()).getData(0));
			
			double[] input = mlDataSet.getData().get(i).getInputArray();
			double[] inputDenorm = new double[input.length];
			
			for (int r=0; r<input.length; r++){
				inputDenorm[r] = normalizer.deNormalize(input[r]);
			}
			
			//Co.println("--> Inputs, ideal, computed: " + StringTools.arrayOfDoubleToString(input) + " = " + new DecimalFormat("#.########").format(idealValue) + " / " + new DecimalFormat("#.########").format(computedValue));
			//if (idealValue == 0){Co.println("--> Ideal was zero!"); Co.println("X: " + idealValue + ", " + computedValue + " / " + dc.format(MathTools.roundTo(computedValue, 4)) + ", " +  dc.format(MathTools.roundTo(computedValue, 5)) + ", " +  dc.format(MathTools.roundTo(computedValue, 6)));}
			
			if (idealValue > 0 && computedValue > 0){result.countCorrect++;}
			else if (idealValue < 0 && computedValue < 0){result.countCorrect++;} //Co.println("--> Correct with: " + dc.format(idealValue) + " / " + dc.format(computedValue));
//			else if (idealValue == 0 && MathTools.roundTo(computedValue, 4) == 0){result.countCorrect++;}
//			else if (idealValue == 0 && MathTools.roundTo(computedValue, 4) <= 0.0005 && computedValue > 0){result.countCorrect++;}
//			else if (idealValue == 0 && MathTools.roundTo(computedValue, 4) == -0.0005 && computedValue < 0){result.countCorrect++;}
			else if (idealValue == 0){}
			else {
				//Co.println("--> Incorrect with: " + StringTools.arrayOfDoubleToString(input) + " = " + new DecimalFormat("#.########").format(idealValue) + " / " + new DecimalFormat("#.########").format(computedValue));
				result.countIncorrect++;
			}
		}
		
		Co.println("--> Directional accuracy (" + (isCross ? "Cross" : "Regular") + "): " + result.countIncorrect + " / " + result.countCorrect + " = " + new DecimalFormat("#.##").format(((double)result.countCorrect / ((double)result.countCorrect + (double)result.countIncorrect) * 100)) + "%");
		
	}
	
	private void addTimeSeries(MLDataSet mlDataSet, TimeSeries ts, boolean isCross){
		Date day = null;
		
		for (int i=0; i<mlDataSet.getRecordCount(); i++){
			//Co.println("--> Computed: " + network.compute(mlDataSetReg.get(i + offset).getInput()).getData(0));
			
			double computed = normalizer.deNormalize(network.compute(mlDataSet.get(i).getInput()).getData(0));
			double currentPriceClose = commonAnalysisData.arrayOfPriceClose[i + INPUT_PER_ITEM + IDEAL_OFFSET + (isCross ? regularEnd : 0)];
			Date currentDate = commonAnalysisData.arrayOfDates[i + INPUT_PER_ITEM + IDEAL_OFFSET + 1 + (isCross ? regularEnd : 0)];
			double value =  currentPriceClose * ( 1 + computed); 
			
//			value = MathTools.round(value); 
//			Co.println("--> Current price: " + currentPriceClose);
//			Co.println("--> Computed: " + new DecimalFormat("#.######").format(computed));
//			Co.println("--> Actual: " + new DecimalFormat("#.######").format(normalizer.deNormalize(mlDataSetReg.get(i).getIdeal().getData(0))));
//			Co.println("--> Result: " + value);
//			Co.println("\n");
			
			if (day == null){day = (Date) commonAnalysisData.arrayOfDates[i + INPUT_PER_ITEM + IDEAL_OFFSET].clone(); day.setHours(0); day.setMinutes(0); day.setSeconds(0);}
			Date curDate = (Date)commonAnalysisData.arrayOfDates[i + INPUT_PER_ITEM + IDEAL_OFFSET].clone(); curDate.setHours(0); curDate.setMinutes(0); curDate.setSeconds(0);
			
			if (day.getTime() != curDate.getTime()){
				day = curDate;
				Co.println("--> DAY CHANGE to: " + curDate);

				Date temp = (Date)commonAnalysisData.arrayOfDates[i + INPUT_PER_ITEM + IDEAL_OFFSET -1].clone();
				
				for (int x=1; x<=DAY_GAPS_SIZE; x++){
					temp = DateTools.getRolledDate(temp, Calendar.MINUTE, 1);
					ts.add(new TimeSeriesDataItem(new Minute(temp), null));
					Co.println("--> Added null at (B): " + temp);
				}
			}
			
			ts.add(new TimeSeriesDataItem(new Minute(currentDate), value)); 
		}
		
		TSC.addSeries(ts);
	}
	
	public BasicNetwork getMLNetwork(int inputSize, int outputSize){
		FeedForwardPattern pattern = new FeedForwardPattern();
		pattern.setInputNeurons(inputSize);
		pattern.addHiddenLayer((int)((double)inputSize/1.5));
		pattern.addHiddenLayer(inputSize/3);
//		pattern.addHiddenLayer(inputSize/5);
		pattern.setOutputNeurons(outputSize);
		pattern.setActivationFunction(new ActivationTANH());
		pattern.setActivationOutput(new ActivationTANH());
		return (BasicNetwork) pattern.generate();
	}
	
	
	public class PredictDisplay extends ApplicationFrame {
		public PredictDisplay(String title) {
			super(title);
			
			final ChartPanel chartPanel = (ChartPanel) createPanel();
			chartPanel.setPreferredSize(new Dimension(1600, 1000));
			chartPanel.setHorizontalAxisTrace(true);
			setContentPane(chartPanel);
			setVisible(true);
			toFront();
			pack();
			RefineryUtilities.positionFrameOnScreen(this, 0, 0);
		}
		
		public JPanel createPanel() {
			JFreeChart chart = createChart();
			chart.setAntiAlias(true);
			chart.setTextAntiAlias(true);
			ChartPanel panel = new ChartPanel(chart, false);
			panel.setFillZoomRectangle(true);
			panel.setMouseWheelEnabled(true);
			return panel;
		}
		
		private JFreeChart createChart() {
			CombinedDomainXYPlot plot = new CombinedDomainXYPlot(new DateAxis("Domain"));
			
			SegmentedTimeline stl = new SegmentedTimeline(SegmentedTimeline.MINUTE_SEGMENT_SIZE, 390 + DAY_GAPS_SIZE, 1050 - DAY_GAPS_SIZE);
			stl.setStartTime(BacktestPredictFuture.this.dateStart.getTime());
			((DateAxis)plot.getDomainAxis()).setTimeline(stl);

			plot.setGap(10);
			plot.setOrientation(PlotOrientation.VERTICAL);
			plot.setBackgroundPaint(Color.lightGray);
			plot.setDomainGridlinePaint(Color.white);
			plot.setRangeGridlinePaint(Color.white);
			plot.setAxisOffset(new RectangleInsets(5.0, 5.0, 5.0, 5.0));
			
			try {
				XYPlot subPlotForCandleStick = ChartFactory.createCandlestickChart("Candlestick Demo", "Time", "Candle Stick", dataSetForDefaultHighLowDataset, false).getXYPlot();
				((NumberAxis) subPlotForCandleStick.getRangeAxis()).setAutoRangeIncludesZero(false);
				subPlotForCandleStick.setBackgroundPaint(Color.white);
				subPlotForCandleStick.setDomainGridlinePaint(Color.lightGray);
				subPlotForCandleStick.setRangeGridlinePaint(Color.lightGray);
				subPlotForCandleStick.setRangeAxis(0, new NumberAxis("Price"));
				subPlotForCandleStick.getRangeAxis(0).setAutoRange(true);
				((NumberAxis) subPlotForCandleStick.getRangeAxis(0)).setAutoRangeIncludesZero(false);
				subPlotForCandleStick.setRangeAxis(1, new NumberAxis("Volume"));
				
				((CandlestickRenderer) subPlotForCandleStick.getRenderer()).setUseOutlinePaint(true);
				plot.add(subPlotForCandleStick, 1);
				
				XYPlot subPlotForPrice = new XYPlot(TSC, null, new NumberAxis("Action"), new StandardXYItemRenderer());
				subPlotForPrice.getRenderer().setSeriesPaint(0, Color.DARK_GRAY);
				subPlotForPrice.getRangeAxis().setAutoRange(true);
				((NumberAxis) subPlotForPrice.getRangeAxis()).setAutoRangeIncludesZero(false);
				subPlotForPrice.setDatasetRenderingOrder(DatasetRenderingOrder.FORWARD);
				subPlotForPrice.getRenderer().setSeriesPaint(1, Color.RED);
							
				plot.add(subPlotForPrice);
			} catch (Exception e) {}

			DateAxis axis = (DateAxis) plot.getDomainAxis();
			axis.setDateFormatOverride(new SimpleDateFormat("MM/dd HH:mm"));

			JFreeChart chart = new JFreeChart("autoStock - Fordward Price Prediction", JFreeChart.DEFAULT_TITLE_FONT, plot, true);
			chart.setBackgroundPaint(Color.white);
			return chart;
		}
	}
}

// NEW CURRENT