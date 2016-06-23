/**
 * 
 */
package com.autoStock.backtest.encog;

import java.util.ArrayList;

import org.encog.ml.CalculateScore;
import org.encog.ml.MLMethod;
import org.encog.neural.networks.BasicNetwork;

import com.autoStock.Co;
import com.autoStock.signal.extras.EncogNetworkCache;
import com.autoStock.signal.signalMetrics.SignalOfEncogNew.EncogNetwork;

/**
 * @author Kevin
 *
 */
public class TrainEncogMultiNetwork extends TrainEncogWithScore {
	private ArrayList<TrainEncogBase> listOfNetworksToTrain = new ArrayList<TrainEncogBase>();
	
	public TrainEncogMultiNetwork(CalculateScore calculateScore, String networkName) {
		super(calculateScore, networkName);
	}
	
	public void addNetwork(MLMethod network, int which){
		listOfNetworksToTrain.add(new TrainEncogNetworkOfBasic(calculateScore, (BasicNetwork)network, networkName + "-" + which, 30));
	}

	@Override
	public void train(int count, double score) {
		
//		listOfNetworksToTrain.get(0).train(30, 100);
		
		for (int i=0; i<count; i++){
			int index = 0;
			
			for (TrainEncogBase trainer : listOfNetworksToTrain){
				Co.println("--> Training network: " + trainer.networkName + " at " + index);
				((EncogScoreProviderNew)calculateScore).setWhichNetwork(index);
				EncogNetworkCache.getInstance().clear();
				trainer.train(30, score);
				index++;
			}
		}
		
		EncogNetworkCache.getInstance().clear();
	}

	@Override
	public boolean saveNetwork() {
		for (TrainEncogBase trainer : listOfNetworksToTrain){
			Co.println("--> A: " + ((TrainEncogNetworkOfBasic)trainer).network.getInputCount());
			trainer.saveNetwork();
		}
		
		return true;
	}
}
