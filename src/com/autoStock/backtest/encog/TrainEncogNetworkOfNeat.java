/**
 * 
 */
package com.autoStock.backtest.encog;

import org.encog.ml.CalculateScore;
import org.encog.ml.ea.train.basic.TrainEA;
import org.encog.neural.neat.NEATNetwork;
import org.encog.neural.neat.NEATPopulation;
import org.encog.neural.neat.NEATUtil;

import com.autoStock.Co;
import com.autoStock.signal.signalMetrics.SignalOfEncog;

/**
 * @author Kevin
 *
 */
public class TrainEncogNetworkOfNeat extends TrainEncogWithScore {
	private static TrainEA train;
	
	public TrainEncogNetworkOfNeat(CalculateScore calculateScore, String networkName){
		super(calculateScore, networkName);
		train = NEATUtil.constructNEATTrainer(calculateScore, SignalOfEncog.getInputWindowLength(), 2, 256);
	}
	
	@Override
	public void train(int count, double score) {
		for (int i = 0; i < count; i++) {
			train.iteration();
			
			Co.println("--> Training... " + i + ", " + train.getError());
			if (train.getError() < score){Co.println("--> Warning, network was not able to return to score: " + score + ", " + train.getError());}
			
			bestScore = Math.max(train.getError(), bestScore);
		}
	}

	@Override
	public boolean saveNetwork() {
		return encogNetworkProvider.saveNeatNetwork((NEATNetwork) train.getMethod(), networkName);
	}
}
