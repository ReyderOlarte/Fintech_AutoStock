/**
 * 
 */
package com.autoStock;

import java.nio.channels.IllegalSelectorException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;

import org.encog.engine.network.activation.ActivationBiPolar;
import org.encog.engine.network.activation.ActivationSigmoid;
import org.encog.engine.network.activation.ActivationSteepenedSigmoid;
import org.encog.engine.network.activation.ActivationTANH;
import org.encog.ml.MLRegression;
import org.encog.ml.data.basic.BasicMLData;
import org.encog.ml.data.basic.BasicMLDataSet;
import org.encog.ml.train.MLTrain;
import org.encog.ml.train.strategy.HybridStrategy;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.training.TrainingSetScore;
import org.encog.neural.networks.training.anneal.NeuralSimulatedAnnealing;
import org.encog.neural.networks.training.propagation.manhattan.ManhattanPropagation;
import org.encog.neural.networks.training.propagation.resilient.RPROPType;
import org.encog.neural.networks.training.propagation.resilient.ResilientPropagation;
import org.encog.neural.networks.training.pso.NeuralPSO;
import org.encog.neural.pattern.FeedForwardPattern;
import org.jfree.chart.event.ChartChangeListener;

import com.autoStock.algorithm.AlgorithmBase;
import com.autoStock.algorithm.core.AlgorithmDefinitions.AlgorithmMode;
import com.autoStock.algorithm.core.AlgorithmListener;
import com.autoStock.algorithm.core.AlgorithmRemodeler;
import com.autoStock.algorithm.extras.StrategyOptionsOverride;
import com.autoStock.backtest.BacktestEvaluation;
import com.autoStock.backtest.BacktestEvaluationBuilder;
import com.autoStock.backtest.BacktestEvaluationReader;
import com.autoStock.backtest.ListenerOfBacktest;
import com.autoStock.backtest.SingleBacktest;
import com.autoStock.cache.GenericPersister;
import com.autoStock.chart.CombinedLineChart.StoredSignalPoint;
import com.autoStock.finance.SecurityTypeHelper.SecurityType;
import com.autoStock.indicator.results.ResultsWILLR;
import com.autoStock.internal.ApplicationStates;
import com.autoStock.internal.Global;
import com.autoStock.misc.Pair;
import com.autoStock.position.PositionGovernorResponse;
import com.autoStock.position.PositionGovernorResponseStatus;
import com.autoStock.signal.SignalDefinitions.SignalMetricType;
import com.autoStock.signal.extras.EncogInputWindow;
import com.autoStock.signal.extras.EncogNetworkProvider;
import com.autoStock.signal.signalMetrics.SignalOfEncog;
import com.autoStock.strategy.StrategyOptionDefaults;
import com.autoStock.strategy.StrategyResponse;
import com.autoStock.tools.ArrayTools;
import com.autoStock.tools.DateTools;
import com.autoStock.tools.ListTools;
import com.autoStock.tools.MathTools;
import com.autoStock.trading.platform.ib.definitions.HistoricalDataDefinitions.Resolution;
import com.autoStock.trading.types.HistoricalData;
import com.autoStock.trading.types.Position;
import com.autoStock.types.Exchange;
import com.autoStock.types.QuoteSlice;
import com.autoStock.types.Symbol;

/**
 * @author Kevin
 *
 */
public class MainGenerateIdeal implements AlgorithmListener, ListenerOfBacktest {
	private static final int ITERATIONS = 512;
	private GenericPersister genericPersister = new GenericPersister();
	private ArrayList<StoredSignalPoint> lStoredPoints = new ArrayList<StoredSignalPoint>();
	private SingleBacktest singleBacktest;
	private ArrayList<ArrayList<Double>> listOfInput = new ArrayList<ArrayList<Double>>();
	private ArrayList<ArrayList<Double>> listOfIdeal = new ArrayList<ArrayList<Double>>();
	private ArrayList<Pair<Integer, Date>> listOfIdealPair = new ArrayList<>();
	private StrategyOptionsOverride soo = StrategyOptionDefaults.getDefaultOverride();
	private Exchange exchange = new Exchange("NYSE");
	private Symbol symbol = new Symbol("MS", SecurityType.type_stock);
	private Date dateStart = DateTools.getDateFromString("02/03/2014");
	private Date dateEnd = DateTools.getDateFromString("09/01/2015");
//	private Date dateStart = DateTools.getDateFromString("09/08/2014");
//	private Date dateEnd = DateTools.getDateFromString("09/08/2014");
	private double crossValidationRatio = 0.30d;
	private HistoricalData historicalData;
	private HistoricalData historicalDataForRegular;
	private HistoricalData historicalDataForCross;
	private static enum MGIMode {to_points, from_points, direct};
	private MGIMode mgiMode = MGIMode.from_points;
	
	public void run(){
		Global.callbackLock.requestLock();
		
		// Load the data
		historicalData = new HistoricalData(exchange, symbol, dateStart, dateEnd, Resolution.min);
		historicalData.setStartAndEndDatesToExchange();
		
		singleBacktest = new SingleBacktest(historicalData, AlgorithmMode.mode_backtest_single_with_tables);
		singleBacktest.setListenerOfBacktestCompleted(this);
		singleBacktest.backtestContainer.algorithm.setAlgorithmListener(this);
		new AlgorithmRemodeler(singleBacktest.backtestContainer.algorithm, BacktestEvaluationReader.getPrecomputedModel(exchange, symbol, soo)).remodel(true, true, true, true); 
		singleBacktest.backtestContainer.algorithm.strategyBase.strategyOptions.listOfSignalMetricType.clear();
		singleBacktest.backtestContainer.algorithm.strategyBase.strategyOptions.listOfSignalMetricType.add(SignalMetricType.metric_encog);
		
		singleBacktest.selfPopulateBacktestData();
		singleBacktest.runBacktest();
		
		Co.print(new BacktestEvaluationBuilder().buildEvaluation(singleBacktest.backtestContainer).toString());
	}
	
	@Override
	public void initialize(Date startingDate, Date endDate) {
		ArrayList<StoredSignalPoint> list = getList(startingDate, endDate); 
		if (list.size() > 0 && mgiMode == MGIMode.from_points){
			singleBacktest.backtestContainer.algorithm.positionGovernor.listOfPredSignalPoint = list; 
		}else{
			String name = exchange.name + "-" + symbol.name + "-day-" + DateTools.getEncogDate(startingDate);
			Co.println("--> Loaded daily network: " + name);
			BasicNetwork basicNetwork = new EncogNetworkProvider().getBasicNetwork(name);
//			if (basicNetwork == null){throw new IllegalStateException("Couldn't find network to load: " + name);}
			singleBacktest.backtestContainer.algorithm.signalGroup.signalOfEncog.setNetwork(basicNetwork, 0);
			singleBacktest.backtestContainer.algorithm.positionGovernor.listOfPredSignalPoint = null;
		}
		
		Co.println("--> Initialize on date: " + DateTools.getPretty(startingDate) + " with list of input of: " + listOfInput.size());
	}
	
	private ArrayList<StoredSignalPoint> getList(Date dateStart, Date dateEnd){
		ArrayList<StoredSignalPoint> list = genericPersister.getCount(StoredSignalPoint.class) > 0 ? new ArrayList<StoredSignalPoint>(genericPersister.getList(StoredSignalPoint.class)) : null;
		ArrayList<StoredSignalPoint> returnList = new ArrayList<StoredSignalPoint>();
		if (list == null){return returnList;}
		
		for (StoredSignalPoint csp : list){
			if (csp.date.getTime() >= dateStart.getTime() && csp.date.getTime() <= dateEnd.getTime()){
				returnList.add(csp);
			}
		}
		return returnList;
	}

	@Override
	public void receiveStrategyResponse(StrategyResponse strategyResponse) {
		Co.println("--> Received strategy response");
	}
	
	boolean haveChange = false;
	PositionGovernorResponse positionGovernorResponse = null;

	@Override
	public void receiveChangedStrategyResponse(StrategyResponse strategyResponse) {
		//Co.println("--> Received changed strategy response: " + strategyResponse.positionGovernorResponse.status.name());
		positionGovernorResponse = strategyResponse.positionGovernorResponse;
		haveChange = true;
	}

	@Override
	public synchronized void receiveTick(QuoteSlice quote, int receivedIndex, int processedIndex, boolean processed) {
		//Co.println("--> Received tick " + receivedIndex + ", " + processedIndex);
		
		EncogInputWindow eiw = singleBacktest.backtestContainer.algorithm.signalGroup.signalOfEncog.getInputWindow();
		ArrayList<Double> listOfIdealOutputs = new ArrayList<Double>();
		
		if (mgiMode == MGIMode.to_points && haveChange && positionGovernorResponse != null){
			lStoredPoints.add(new StoredSignalPoint(receivedIndex, quote.priceClose, positionGovernorResponse.signalPoint.signalPointType, quote.dateTime, symbol, exchange));
		}
		
		if (processed && eiw != null && positionGovernorResponse != null){
			if (haveChange && (positionGovernorResponse.status != PositionGovernorResponseStatus.changed_long_entry || positionGovernorResponse.status != PositionGovernorResponseStatus.changed_short_entry) 
				&& (positionGovernorResponse.signalPoint.signalMetricType == SignalMetricType.metric_encog 
					|| positionGovernorResponse.signalPoint.signalMetricType == SignalMetricType.injected
					//|| (positionGovernorResponse.signalPoint.signalMetricType == SignalMetricType.injected && positionGovernorResponse.status == PositionGovernorResponseStatus.changed_long_entry)
				    //|| (positionGovernorResponse.signalPoint.signalMetricType == SignalMetricType.injected && positionGovernorResponse.status == PositionGovernorResponseStatus.changed_short_entry)
				)){
				
				haveChange = false;
				
				Co.println("--> Have change at tick: " + DateTools.getPretty(quote.dateTime) + " to status " + positionGovernorResponse.status.name() +  " with EIW: " + eiw.getHash());
				//Co.println(eiw.describeContents());
				
				if (positionGovernorResponse.status == PositionGovernorResponseStatus.changed_long_entry){
					addLongEntry(listOfIdealOutputs, 1);
				}
				else if (positionGovernorResponse.status == PositionGovernorResponseStatus.changed_short_entry){
					addShortEntry(listOfIdealOutputs, 1);
				}
//				else if (positionGovernorResponse.status == PositionGovernorResponseStatus.changed_long_reentry || positionGovernorResponse.status == PositionGovernorResponseStatus.changed_short_reentry){
//					addReentry(listOfIdealOutputs, 1);
//				}
				else if (positionGovernorResponse.status == PositionGovernorResponseStatus.changed_long_exit){ // || positionGovernorResponse.status == PositionGovernorResponseStatus.changed_short_exit){
					addLongExit(listOfIdealOutputs, 1);
				}
				else if (positionGovernorResponse.status == PositionGovernorResponseStatus.changed_short_exit){
					addShortExit(listOfIdealOutputs, 1);
				}
				
				else { throw new IllegalStateException(positionGovernorResponse.status.name() + " from " + positionGovernorResponse.signalPoint.signalMetricType.name()); }
			}else{
//				Position position = singleBacktest.backtestContainer.algorithm.position;
//				
//				if (position != null && position.isFilledAndOpen()){ // && singleBacktest.backtestContainer.algorithm.position.getPositionHistory().getAge().asSeconds() <= 60 * 2 && singleBacktest.backtestContainer.algorithm.position.getCurrentPercentGainLoss(false) > 0.05){
//					if (singleBacktest.backtestContainer.algorithm.position.isLong()){
//						addLongEntry(listOfIdealOutputs, 1);
//					}else if (singleBacktest.backtestContainer.algorithm.position.isShort()){
//						addShortEntry(listOfIdealOutputs, 1);
//					}else {throw new IllegalStateException();}
//				}else {
//					addNone(listOfIdealOutputs, 1);					
//				}
				
				addNone(listOfIdealOutputs, 1);
			}
			
			listOfInput.add(ListTools.getListFromArray(eiw.getAsWindow(true)));
			listOfIdeal.add(listOfIdealOutputs);
			listOfIdealPair.add(new Pair<Integer, Date>(listOfIdeal.size(), quote.dateTime));
		}
	}
	
	private void addLongEntry(ArrayList<Double> listOfIdealOutputs, double as){
		listOfIdealOutputs.add(as);
		listOfIdealOutputs.add(-1d);
		listOfIdealOutputs.add(-1d);
		listOfIdealOutputs.add(-1d);
//		listOfIdealOutputs.add(-1d);
//		listOfIdealOutputs.add(-1d);
	}
	
	private void addShortEntry(ArrayList<Double> listOfIdealOutputs, double as){
		listOfIdealOutputs.add(-1d);
		listOfIdealOutputs.add(as);
		listOfIdealOutputs.add(-1d);
		listOfIdealOutputs.add(-1d);
//		listOfIdealOutputs.add(-1d);
//		listOfIdealOutputs.add(-1d);	
	}
	
//	private void addReentry(ArrayList<Double> listOfIdealOutputs, double as){
//		listOfIdealOutputs.add(-1d);
//		listOfIdealOutputs.add(-1d);
//		listOfIdealOutputs.add(as);
//		listOfIdealOutputs.add(-1d);
//		listOfIdealOutputs.add(-1d);
//		listOfIdealOutputs.add(-1d);
//	}
//	
	private void addLongExit(ArrayList<Double> listOfIdealOutputs, double as){
		listOfIdealOutputs.add(-1d);
		listOfIdealOutputs.add(-1d);
		listOfIdealOutputs.add(as);
		listOfIdealOutputs.add(-1d);
//		listOfIdealOutputs.add(-1d);
//		listOfIdealOutputs.add(-1d);
	}
	
	private void addShortExit(ArrayList<Double> listOfIdealOutputs, double as){
		listOfIdealOutputs.add(-1d);
		listOfIdealOutputs.add(-1d);
		listOfIdealOutputs.add(-1d);
		listOfIdealOutputs.add(as);
//		listOfIdealOutputs.add(-1d);
//		listOfIdealOutputs.add(-1d);
	}
	
	private void addNone(ArrayList<Double> listOfIdealOutputs, double as){
		listOfIdealOutputs.add(-1d);
		listOfIdealOutputs.add(-1d);
		listOfIdealOutputs.add(-1d);
		listOfIdealOutputs.add(-1d);
//		listOfIdealOutputs.add(-1d);
//		listOfIdealOutputs.add(-1d);
	}
	
	int currentDay = 0;

	@Override
	public void endOfAlgorithm() {		
		//Co.println("--> Have input list of: " + listOfInput.size());
		//Co.println("--> Have ideal list of: " + listOfIdeal.size());
//		Co.println("***** Current day: " + currentDay);
		currentDay++;
	}
	
	@Override
	public void onCompleted(Symbol symbol, AlgorithmBase algorithmBase) {
		Co.println("--> Backtest completed!");
		Co.println("--> Have input list of: " + listOfInput.size());
		Co.println("--> Have ideal list of: " + listOfIdeal.size());
		//Co.println("--> Have StoredSignalPoints: " + lStoredPoints.size());
		
		if (listOfInput.get(0).size() != SignalOfEncog.getInputWindowLength()){throw new IllegalMonitorStateException("Size should be: " + listOfInput.get(0).size());}
		
		if (mgiMode == MGIMode.to_points){
			genericPersister.erase();
			Co.print("--> Writing... ");
			for (StoredSignalPoint sp : lStoredPoints){genericPersister.persistInto(sp, true);}
			genericPersister.syncToDisk();
			Co.println("" + lStoredPoints.size());
			return;
		}
		
		ArrayList<ArrayList<Double>> listOfCrossInput = null; // = new ArrayList<ArrayList<Double>>();
		ArrayList<ArrayList<Double>> listOfCrossIdeal = null; // = new ArrayList<ArrayList<Double>>();
		
		if (crossValidationRatio > 0){
			int crossStart = listOfIdeal.size () - (int) (crossValidationRatio * (double)listOfInput.size());
			
			for (int i=0; i<1024; i++){
				if (listOfIdealPair.get(crossStart + i).second.getDay() != listOfIdealPair.get(crossStart).second.getDay()){
					//Co.println("--> Offsets to: " + i);
					crossStart += i;
					break;
				}
			}
			
			listOfCrossInput = ListTools.getLast(listOfInput, listOfInput.size() - crossStart);//(int) (crossValidationRatio * (double)listOfInput.size()));
			listOfCrossIdeal = ListTools.getLast(listOfIdeal, listOfIdeal.size() - crossStart);//(int) (crossValidationRatio * (double)listOfIdeal.size()));
			listOfInput = new ArrayList<ArrayList<Double>>(listOfInput.subList(0, listOfInput.size() - listOfCrossIdeal.size()));
			listOfIdeal = new ArrayList<ArrayList<Double>>(listOfIdeal.subList(0, listOfIdeal.size() - listOfCrossIdeal.size()));
			Co.println("--> Using cross validation ratio: " + crossValidationRatio + " with " + listOfCrossInput.size() + " records");
			Co.println("--> Have input list of: " + listOfInput.size());
			Co.println("--> Have ideal list of: " + listOfIdeal.size());
			Co.println("--> Regular data starts on: " + listOfIdealPair.get(0).second + " and ends on " + listOfIdealPair.get(crossStart-1).second);
			Co.println("--> Cross validation starts on: " + listOfIdealPair.get(listOfInput.size()).second + " and ends on " + ListTools.getLast(listOfIdealPair).second);
			
			historicalDataForRegular = new HistoricalData(exchange, symbol, listOfIdealPair.get(0).second, listOfIdealPair.get(crossStart-1).second, Resolution.min);
			historicalDataForRegular.setStartAndEndDatesToExchange();
			
			historicalDataForCross = new HistoricalData(exchange, symbol, listOfIdealPair.get(listOfInput.size()).second, ListTools.getLast(listOfIdealPair).second, Resolution.min);
			historicalDataForCross.setStartAndEndDatesToExchange();
		}
		
		BasicMLDataSet dataSet = new BasicMLDataSet();
		BasicMLDataSet dataSetCross = new BasicMLDataSet();
		
		for (int i=0; i<listOfInput.size(); i++){
			dataSet.add(new BasicMLData(ArrayTools.getArrayFromListOfDouble(listOfInput.get(i))), new BasicMLData(ArrayTools.getArrayFromListOfDouble(listOfIdeal.get(i))));
		}
		
		if (crossValidationRatio > 0){
			for (int i=0; i<listOfCrossInput.size(); i++){
				dataSetCross.add(new BasicMLData(ArrayTools.getArrayFromListOfDouble(listOfCrossInput.get(i))), new BasicMLData(ArrayTools.getArrayFromListOfDouble(listOfCrossIdeal.get(i))));
			}
		}
		
		// Train the network
		BasicNetwork network = getNetwork(); //EncogNetworkGenerator.getBasicNetwork(SignalOfEncog.getInputWindowLength(), 3);
		
//		new NguyenWidrowRandomizer().randomize(network);

//		MLTrain train = new ManhattanPropagation(network, dataSet, 0.015);
		MLTrain train = new ResilientPropagation(network, dataSet); //, 0.05, 25);
		((ResilientPropagation)train).setRPROPType(RPROPType.iRPROPp);
//		MLTrain train = NEATUtil.constructNEATTrainer(new TrainingSetScore(dataSet), SignalOfEncog.getInputWindowLength(), 3, 512);
//		MLTrain train = new NeuralPSO(network, dataSet);
//		train.addStrategy(new HybridStrategy(new NeuralPSO(network, dataSet), 0.100, 200, 200));
//		train.addStrategy(new HybridStrategy(new NeuralSimulatedAnnealing(network, new TrainingSetScore(dataSet), 10, 2, 100), 0.010, 250, 250));
	
		DecimalFormat df = new DecimalFormat("0000.00000000000000");
		int zeroCount = 15;
		
		for (int i=0; i<ITERATIONS; i++){
			train.iteration();
			
			if (crossValidationRatio == 0){
				BacktestEvaluation beReg = getEvaluationWith(network, historicalData.startDate, historicalData.endDate);
				Co.println(i + ". " + df.format(train.getError() * 1000).replaceAll("\\G0", " ") + " = " + MathTools.roundTo(beReg.getScore(), 2) + "(%"  + MathTools.round(beReg.percentYield) + ")");
			} else{				
				// Find out score for relevant backtest
				if (i % 10 == 0){
					BacktestEvaluation beReg = getEvaluationWith(network, historicalDataForRegular.startDate, historicalDataForRegular.endDate);
					BacktestEvaluation beCross = getEvaluationWith(network, historicalDataForCross.startDate, historicalDataForCross.endDate);
					Co.println(i + ". " + df.format(train.getError() * 1000).replaceAll("\\G0", " ") + " / " + df.format(network.calculateError(dataSetCross) * 1000).replaceAll("\\G0", " ") + " = " + MathTools.roundTo(beReg.getScore(), 2) + "(%"  + MathTools.round(beReg.percentYield) + ") / " + MathTools.roundTo(beCross.getScore(), 2) + "(%"  + MathTools.round(beCross.percentYield) + ")"); 
				}else{
					Co.println(i + ". " + df.format(train.getError() * 1000).replaceAll("\\G0", " "));
				}
				
			}
			
			if (zeroCount == 0){break;}
			else if (train.getError() <= 0.000000000000010){zeroCount--;}
		}
		
		train.finishTraining();
		
		// Verficiation stage
		if (train.getError() * 1000 < 9999){
			Co.println("--> Good score");
			
			HistoricalData historicalData = new HistoricalData(exchange, symbol, dateStart, dateEnd, Resolution.min);
			historicalData.setStartAndEndDatesToExchange();
			
			SingleBacktest singleBacktest = new SingleBacktest(historicalData, AlgorithmMode.mode_backtest_single_with_tables);
			singleBacktest.remodel(BacktestEvaluationReader.getPrecomputedModel(exchange, symbol, soo));
			singleBacktest.backtestContainer.algorithm.strategyBase.strategyOptions.listOfSignalMetricType.clear();
			singleBacktest.backtestContainer.algorithm.strategyBase.strategyOptions.listOfSignalMetricType.add(SignalMetricType.metric_encog);
			singleBacktest.backtestContainer.algorithm.signalGroup.signalOfEncog.setNetwork((MLRegression) train.getMethod(), 0);
			//singleBacktest.backtestContainer.algorithm.signalGroup.signalOfEncog.describeWindow = true;
			singleBacktest.selfPopulateBacktestData();
			singleBacktest.runBacktest();
			
			Co.println(new BacktestEvaluationBuilder().buildEvaluation(singleBacktest.backtestContainer).toString());
			
			new EncogNetworkProvider().saveNetwork((BasicNetwork) train.getMethod(), "NYSE-MS");
		}
	}
		
	private BasicNetwork getNetwork(){
		FeedForwardPattern pattern = new FeedForwardPattern();
		pattern.setInputNeurons(SignalOfEncog.getInputWindowLength());
		pattern.addHiddenLayer((int) ((double)SignalOfEncog.getInputWindowLength() / (double) 1.5));
		pattern.addHiddenLayer((int) ((double)SignalOfEncog.getInputWindowLength() / (double) 3));
//		pattern.addHiddenLayer((int) ((double)SignalOfEncog.getInputWindowLength() / (double) 6));
		pattern.setOutputNeurons(SignalOfEncog.getOutputLength());
		pattern.setActivationFunction(new ActivationTANH());
		pattern.setActivationOutput(new ActivationTANH());
		return (BasicNetwork) pattern.generate();
	}

	
	private BacktestEvaluation getEvaluationWith(BasicNetwork network, Date dateStart, Date dateEnd){
		HistoricalData historicalData = new HistoricalData(exchange, symbol, dateStart, dateEnd, Resolution.min);
		historicalData.setStartAndEndDatesToExchange();
		
		SingleBacktest singleBacktest = new SingleBacktest(historicalData, AlgorithmMode.mode_backtest_single_with_tables);
		singleBacktest.remodel(BacktestEvaluationReader.getPrecomputedModel(exchange, symbol, soo));
		singleBacktest.backtestContainer.algorithm.strategyBase.strategyOptions.listOfSignalMetricType.clear();
		singleBacktest.backtestContainer.algorithm.strategyBase.strategyOptions.listOfSignalMetricType.add(SignalMetricType.metric_encog);
		singleBacktest.backtestContainer.algorithm.signalGroup.signalOfEncog.setNetwork(network, 0);
		singleBacktest.selfPopulateBacktestData();
		singleBacktest.runBacktest();
		
		return new BacktestEvaluationBuilder().buildEvaluation(singleBacktest.backtestContainer);
	}
}
