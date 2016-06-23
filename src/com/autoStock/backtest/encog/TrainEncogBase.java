/**
 * 
 */
package com.autoStock.backtest.encog;

import java.awt.IllegalComponentStateException;

import org.encog.ml.CalculateScore;

import com.autoStock.signal.extras.EncogNetworkProvider;

/**
 * @author Kevin
 *
 */
public abstract class TrainEncogBase {
	protected EncogNetworkProvider encogNetworkProvider = new EncogNetworkProvider();
	public String networkName;
	public double bestScore;
	
	public abstract void train(int count, double score);
	public abstract boolean saveNetwork();

	public boolean networkExists() { 
		if (this instanceof TrainEncogNetworkOfBasic){
			return encogNetworkProvider.getBasicNetwork(networkName) != null; 
		}else if (this instanceof TrainEncogNetworkOfNeat){
			return encogNetworkProvider.getNeatNetwork(networkName) != null; 
		}
		
		throw new IllegalComponentStateException("Don't understand network type!");
	}
}
