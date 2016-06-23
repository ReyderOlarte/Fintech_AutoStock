/**
 * 
 */
package com.autoStock.backtest.encog;

import org.encog.ml.CalculateScore;

/**
 * @author Kevin
 *
 */
public abstract class TrainEncogWithScore extends TrainEncogBase {
	protected CalculateScore calculateScore;

	public TrainEncogWithScore(CalculateScore calculateScore, String networkName){
		this.calculateScore = calculateScore;
		this.networkName = networkName; 
	}
}
