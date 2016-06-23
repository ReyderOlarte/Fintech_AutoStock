package com.autoStock.backtest.encog;

import org.encog.neural.networks.BasicNetwork;

import com.autoStock.Co;
import com.autoStock.backtest.AlgorithmModel;
import com.autoStock.backtest.encog.TrainEncogSignal.EncogNetworkType;
import com.autoStock.signal.SignalDefinitions.SignalParametersForEncog;
import com.autoStock.signal.extras.EncogNetworkCache;
import com.autoStock.signal.signalMetrics.SignalOfEncog;
import com.autoStock.signal.signalMetrics.SignalOfEncogNew;
import com.autoStock.signal.signalMetrics.SignalOfEncogNew.EncogNetwork;
import com.autoStock.trading.types.HistoricalData;

/**
 * @author Kevin Kowalewski
 * 
 */
public class TrainEncogSignalNew {
	public static final int TRAINING_ITERATIONS = 30;
	private HistoricalData historicalData;
	private EncogScoreProviderNew encogScoreProvider = new EncogScoreProviderNew();
	private TrainEncogMultiNetwork encogTrainer;
	
	public TrainEncogSignalNew(AlgorithmModel algorithmModel, HistoricalData historicalData){
		this.historicalData = historicalData;
		encogScoreProvider.setDetails(algorithmModel, historicalData);		
		
		encogTrainer = new TrainEncogMultiNetwork(encogScoreProvider, historicalData.exchange.name + "-" + historicalData.symbol.name);
		
		//Might want new networks...
		SignalOfEncogNew signalOfEncog = new SignalOfEncogNew(new SignalParametersForEncog(), null);
		signalOfEncog.createNetworks();
		
		int index = 0;
		
		for (EncogNetwork network : signalOfEncog.getNetworks()){
			//Co.println("--> Adding: " + ((BasicNetwork)network.method).getInputCount());
			encogTrainer.addNetwork(network.method, index);
			index++;
		}
	}
	
	public void execute(AlgorithmModel algorithmModel, double score) {
		encogScoreProvider.setDetails(algorithmModel, historicalData);
		
		Co.println("...");
		
		encogTrainer.train(TRAINING_ITERATIONS, score);
		encogTrainer.saveNetwork();
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
}
