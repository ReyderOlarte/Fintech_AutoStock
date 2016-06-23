package com.autoStock.backtest.encog;

import org.encog.engine.network.activation.ActivationBiPolar;
import org.encog.engine.network.activation.ActivationFunction;
import org.encog.engine.network.activation.ActivationSteepenedSigmoid;
import org.encog.engine.network.activation.ActivationTANH;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.pattern.ElmanPattern;
import org.encog.neural.pattern.FeedForwardPattern;
import org.encog.neural.pattern.JordanPattern;

import com.autoStock.Co;
import com.autoStock.backtest.AlgorithmModel;
import com.autoStock.backtest.encog.EncogBacktestContainer.Mode;
import com.autoStock.signal.SignalCache;
import com.autoStock.signal.extras.EncogNetworkCache;
import com.autoStock.signal.extras.EncogNetworkGenerator;
import com.autoStock.signal.signalMetrics.SignalOfEncog;
import com.autoStock.trading.types.HistoricalData;

/**
 * @author Kevin Kowalewski
 * 
 */
public class TrainEncogSignal {
	public static final int TRAINING_ITERATIONS = 32;
	private boolean saveNetwork;
	private HistoricalData historicalData;
	private EncogScoreProvider encogScoreProvider = new EncogScoreProvider();
	private TrainEncogBase encogTrainer;
	
	public static enum EncogNetworkType {
		neat,
		basic,
	}
	
	public TrainEncogSignal(AlgorithmModel algorithmModel, HistoricalData historicalData, boolean saveNetwork, String networkSufix, Mode mode){
		this.historicalData = historicalData;
		this.saveNetwork = saveNetwork;
		encogScoreProvider.setDetails(algorithmModel, historicalData);
		encogScoreProvider.setSuperLoose(false);
		
		if (SignalOfEncog.encogNetworkType == EncogNetworkType.basic){
			FeedForwardPattern pattern = new FeedForwardPattern();
			pattern.setInputNeurons(SignalOfEncog.getInputWindowLength());
			if (mode == Mode.day_over_day){
				pattern.addHiddenLayer(SignalOfEncog.getInputWindowLength() / 5);
			}else if (mode == Mode.full){
				pattern.addHiddenLayer(SignalOfEncog.getInputWindowLength() / 3);
				pattern.addHiddenLayer(SignalOfEncog.getInputWindowLength() / 3);
			}
			pattern.setOutputNeurons(SignalOfEncog.getOutputLength());
			pattern.setActivationFunction(new ActivationTANH()); 
			pattern.setActivationOutput(new ActivationTANH());
			encogTrainer = new TrainEncogNetworkOfBasic(encogScoreProvider, (BasicNetwork) pattern.generate(), historicalData.exchange.name + "-" + historicalData.symbol.name + "-" + networkSufix, TRAINING_ITERATIONS);
		}else if (SignalOfEncog.encogNetworkType == EncogNetworkType.neat){
			encogTrainer = new TrainEncogNetworkOfNeat(encogScoreProvider, historicalData.exchange.name + "-" + historicalData.symbol.name + "-" + networkSufix);
		}
	}
	
	public void execute(AlgorithmModel algorithmModel, double score) {
		encogScoreProvider.setDetails(algorithmModel, historicalData);
		
		Co.println("...");
		
//		SignalCache signalCache = new SignalCache();
//		SignalCache.erase();
//		encogScoreProvider.setSignalCache(signalCache);
		
		encogTrainer.train(TRAINING_ITERATIONS, score);
		
		if (saveNetwork){encogTrainer.saveNetwork();}
		EncogNetworkCache.getInstance().clear();
		
		Co.println(" . ");
	}
	
	public void setDetails(AlgorithmModel algorithmModel) {
		encogScoreProvider.setDetails(algorithmModel, historicalData);
	}
	
	public TrainEncogBase getTrainer(){
		return encogTrainer;
	}

	public boolean networkExists() {
		return encogTrainer.networkExists();
	}

	public EncogScoreProvider getScoreProvider() {
		return encogScoreProvider;
	}
}
