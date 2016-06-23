package com.autoStock.backtest.watchmaker;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.encog.ml.MLRegression;
import org.uncommons.maths.random.MersenneTwisterRNG;
import org.uncommons.maths.random.Probability;
import org.uncommons.watchmaker.framework.CachingFitnessEvaluator;
import org.uncommons.watchmaker.framework.EvolutionObserver;
import org.uncommons.watchmaker.framework.EvolutionaryOperator;
import org.uncommons.watchmaker.framework.FitnessEvaluator;
import org.uncommons.watchmaker.framework.GenerationalEvolutionEngine;
import org.uncommons.watchmaker.framework.PopulationData;
import org.uncommons.watchmaker.framework.islands.IslandEvolution;
import org.uncommons.watchmaker.framework.islands.IslandEvolutionObserver;
import org.uncommons.watchmaker.framework.islands.RingMigration;
import org.uncommons.watchmaker.framework.operators.EvolutionPipeline;
import org.uncommons.watchmaker.framework.selection.RankSelection;
import org.uncommons.watchmaker.framework.selection.RouletteWheelSelection;
import org.uncommons.watchmaker.framework.selection.SigmaScaling;
import org.uncommons.watchmaker.framework.selection.StochasticUniversalSampling;
import org.uncommons.watchmaker.framework.selection.TournamentSelection;
import org.uncommons.watchmaker.framework.termination.GenerationCount;
import org.uncommons.watchmaker.framework.termination.TargetFitness;

import com.autoStock.Co;
import com.autoStock.account.AccountProvider;
import com.autoStock.account.BasicAccount;
import com.autoStock.algorithm.DummyAlgorithm;
import com.autoStock.algorithm.core.AlgorithmDefinitions.AlgorithmMode;
import com.autoStock.algorithm.extras.StrategyOptionsOverride;
import com.autoStock.backtest.AlgorithmModel;
import com.autoStock.backtest.BacktestEvaluation;
import com.autoStock.backtest.BacktestEvaluationBuilder;
import com.autoStock.backtest.BacktestEvaluationReader;
import com.autoStock.backtest.BacktestEvaluationWriter;
import com.autoStock.backtest.SingleBacktest;
import com.autoStock.backtest.encog.EncogBacktestContainer.Mode;
import com.autoStock.backtest.encog.TrainEncogSignal;
import com.autoStock.backtest.encog.TrainEncogSignal.EncogNetworkType;
import com.autoStock.backtest.encog.TrainEncogSignalNew;
import com.autoStock.backtest.watchmaker.WMEvolutionParams.WMEvolutionThorough;
import com.autoStock.backtest.watchmaker.WMEvolutionParams.WMEvolutionType;
import com.autoStock.internal.ApplicationStates;
import com.autoStock.internal.StateRequestListener;
import com.autoStock.signal.signalMetrics.SignalOfEncog;
import com.autoStock.strategy.StrategyOptionDefaults;
import com.autoStock.tools.DateTools;
import com.autoStock.tools.MathTools;
import com.autoStock.trading.platform.ib.definitions.HistoricalDataDefinitions.Resolution;
import com.autoStock.trading.types.HistoricalData;
import com.autoStock.types.Exchange;
import com.autoStock.types.Symbol;

/**
 * @author Kevin Kowalewski
 *
 */
public class WMBacktestContainer implements EvolutionObserver<AlgorithmModel>, IslandEvolutionObserver<AlgorithmModel> {
	private static final boolean BLANK_ENCOG = false;
	private static final boolean TRAIN_SOE = false;
	private static final boolean USE_SOO = true;
	private static final int ISLAND_COUNT = Runtime.getRuntime().availableProcessors() -1;
	private WMEvolutionType evolutionType = WMEvolutionType.type_generational;
	private WMEvolutionThorough evolutionThorough = WMEvolutionThorough.thorough_quick;
	public DummyAlgorithm algorithm;
	public Symbol symbol;
	public Exchange exchange;
	public Date dateStart;
	public Date dateEnd;
	private double bestResult = 0;
	private HistoricalData historicalData;

	private WMCandidateFactory wmCandidateFactory;
	private MersenneTwisterRNG randomNumberGenerator = new MersenneTwisterRNG();
	
	private EvolutionaryOperator<AlgorithmModel> evolutionaryPipeline;
	private GenerationalEvolutionEngine<AlgorithmModel> evolutionEngine;
	private IslandEvolution<AlgorithmModel> islandEvolutionEngine;
	private FitnessEvaluator<AlgorithmModel> fitnessEvaluator;
	
	private TrainEncogSignal trainEncogSignal;
	private StrategyOptionsOverride soo = USE_SOO ? StrategyOptionDefaults.getDefaultOverride() : null;

	public WMBacktestContainer(Symbol symbol, Exchange exchange, Date dateStart, Date dateEnd) {
		this.symbol = symbol;
		this.exchange = exchange;
		this.dateStart = dateStart;
		this.dateEnd = dateEnd;
		
		this.historicalData = new HistoricalData(exchange, symbol, dateStart, dateEnd, Resolution.min);
	
		algorithm = new DummyAlgorithm(exchange, symbol, AlgorithmMode.mode_backtest_with_adjustment, new BasicAccount(AccountProvider.defaultBalance));
		
		wmCandidateFactory = new WMCandidateFactory(this, soo);
		List<EvolutionaryOperator<AlgorithmModel>> operators = new ArrayList<EvolutionaryOperator<AlgorithmModel>>();
		operators.add(new WMMutation(new Probability(0.25)));
		operators.add(new WMCrossover(1));
		evolutionaryPipeline = new EvolutionPipeline<AlgorithmModel>(operators);
		
		trainEncogSignal = new TrainEncogSignal(AlgorithmModel.getEmptyModel(), historicalData, true, "complete", Mode.full);
		fitnessEvaluator = new CachingFitnessEvaluator<AlgorithmModel>(new WMBacktestEvaluator(historicalData, soo));
	}
	
	@SuppressWarnings("unused")
	public void runBacktest(){
		AlgorithmModel algorithmModel = null;
		
		if (BLANK_ENCOG && TRAIN_SOE){
			Co.print("--> Blanking the network... ");
			if (SignalOfEncog.encogNetworkType == EncogNetworkType.basic){
				trainEncogSignal.getTrainer().saveNetwork();
			}else if (SignalOfEncog.encogNetworkType == EncogNetworkType.neat){
				trainEncogSignal.setDetails(BacktestEvaluationReader.getPrecomputedModel(exchange, symbol));
				for (int i=0; i<TrainEncogSignal.TRAINING_ITERATIONS/3; i++){
					trainEncogSignal.getTrainer().train(3, 0);
					if (trainEncogSignal.getTrainer().bestScore > 0.01){trainEncogSignal.getTrainer().saveNetwork(); break;}
				}
			}
			Co.println("OK!");
		}
		
		if (evolutionType == WMEvolutionType.type_island){
			islandEvolutionEngine = new IslandEvolution<>(evolutionThorough == WMEvolutionThorough.thorough_quick ? ISLAND_COUNT : ISLAND_COUNT * 2, 
					new RingMigration(), 
					wmCandidateFactory, 
					evolutionaryPipeline, 
					fitnessEvaluator, 
					new RouletteWheelSelection(), 
					randomNumberGenerator);
			
			islandEvolutionEngine.addEvolutionObserver(this);
			
			if (evolutionThorough == WMEvolutionThorough.thorough_quick){
				algorithmModel = islandEvolutionEngine.evolve(128, 16, 3, 16, new TargetFitness(Integer.MAX_VALUE, true), new GenerationCount(3));
			}else{
				algorithmModel = islandEvolutionEngine.evolve(512, 16, 64, 16, new TargetFitness(Integer.MAX_VALUE, true), new GenerationCount(8));
			}
		}else if (evolutionType == WMEvolutionType.type_generational){
			evolutionEngine = new GenerationalEvolutionEngine<AlgorithmModel>(wmCandidateFactory,
				evolutionaryPipeline, 
				fitnessEvaluator, 
//				new RouletteWheelSelection(),
//				new RankSelection(),
//				new TournamentSelection(new Probability(0.60)),
//				new StochasticUniversalSampling(),
				new SigmaScaling(),
				randomNumberGenerator);
			
			evolutionEngine.addEvolutionObserver(this);
			
			if (evolutionThorough == WMEvolutionThorough.thorough_quick){
				algorithmModel = evolutionEngine.evolve(128, 16, new TargetFitness(Integer.MAX_VALUE, true), new GenerationCount(128));
			}else{
				algorithmModel = evolutionEngine.evolve(1024, 32, new TargetFitness(Integer.MAX_VALUE, true), new GenerationCount(16));
			}
		}else{
			throw new IllegalArgumentException();
		}
				
		WMBacktestEvaluator wmBacktestEvaluator = new WMBacktestEvaluator(new HistoricalData(exchange, symbol, dateStart, dateEnd, Resolution.min), soo);
		BacktestEvaluation backtestEvaluation = wmBacktestEvaluator.getBacktestEvaluation(algorithmModel, true);
		
		double fitness = backtestEvaluation.getScore();
		
		Co.println("\n\nBest result: " + fitness + "\n");
		
		if (bestResult != fitness){
			Co.println("--> Warning: bestResult != current evaluation\n");
//			throw new IllegalComponentStateException("Backtest result did not match best: " + bestResult + ", " + fitness); 
		}		
		
//		for (AdjustmentBase adjustmentBase : algorithmModel.wmAdjustment.listOfAdjustmentBase){
//			Co.println(new BacktestEvaluationBuilder().getAdjustmentDescriptor(adjustmentBase).toString());
//		}
		
		Co.print(wmBacktestEvaluator.getBacktestEvaluation(algorithmModel, true).toString());
		
//		Co.print(new TableController().displayTable(AsciiTables.algorithm_test,);)
		
		Date oosStart = DateTools.getFirstWeekdayAfter(dateEnd);
		Date oosEnd = DateTools.getRolledDate(oosStart, Calendar.DAY_OF_MONTH, 90);

		HistoricalData historicalData = new HistoricalData(exchange, symbol, oosStart, oosEnd, Resolution.min);
		historicalData.setStartAndEndDatesToExchange();
		
		SingleBacktest singleBacktest = new SingleBacktest(historicalData, AlgorithmMode.mode_backtest_single_with_tables);
		singleBacktest.remodel(algorithmModel);
		singleBacktest.selfPopulateBacktestData();
		singleBacktest.runBacktest();
		
		Co.println(new BacktestEvaluationBuilder().buildEvaluation(singleBacktest.backtestContainer).toString());
		
//		Co.print(backtestEvaluationOutOfSample.toString());
		if (backtestEvaluation.getScore() > 0){
			new BacktestEvaluationWriter().writeToDatabase(backtestEvaluation, false);
			Co.println("--> Wrote evaluation");
		}
	}

	@SuppressWarnings("unused")
	@Override
	public void populationUpdate(PopulationData<? extends AlgorithmModel> data) {		
//		SingleBacktest singleBacktest = new SingleBacktest(historicalData, AlgorithmMode.mode_backtest_single);
//		singleBacktest.remodel(data.getBestCandidate());
//		singleBacktest.selfPopulateBacktestData();
//		singleBacktest.runBacktest();
//		
//		BacktestEvaluation backtestEvaluation = new BacktestEvaluationBuilder().buildEvaluation(singleBacktest.backtestContainer);
		
		Co.println("--> Generation " + data.getGenerationNumber() + ", " + MathTools.round(data.getBestCandidateFitness())); // + " Out of sample: " + backtestEvaluation.getScore() + "\n");
		
		bestResult = data.getBestCandidateFitness();
		
//		trainEncogSignal.setDetails(data.getBestCandidate());
//		double escore = trainEncogSignal.getScoreProvider().calculateScore(new EncogNetworkProvider(historicalData.exchange.exchangeName + "-" + historicalData.symbol.symbolName).getBasicNetwork());
//		Co.println("--> EScore: " + escore);
//		Co.println(data.getBestCandidate().getUniqueIdentifier());
		
		if (TRAIN_SOE && data.getGenerationNumber() != 0 && data.getGenerationNumber() % 5 == 0){ // && data.getBestCandidateFitness() > 0 
			try {
				trainEncogSignal.execute(data.getBestCandidate(), bestResult);
			}catch(IllegalStateException e){
				//Co.println(trainEncogSignal.getScoreProvider().getAlgorithmModel().getUniqueIdentifier());
				e.printStackTrace();
				ApplicationStates.shutdown();
			}
		}
	}

	@Override
	public void islandPopulationUpdate(int islandIndex, PopulationData<? extends AlgorithmModel> data) {
		Co.print("\n--> Generation [" + islandIndex + "] " + data.getGenerationNumber() + ", " + data.getBestCandidateFitness());
	}
}
