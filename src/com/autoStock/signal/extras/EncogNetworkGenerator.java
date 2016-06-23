/**
 * 
 */
package com.autoStock.signal.extras;

import org.encog.engine.network.activation.ActivationBiPolar;
import org.encog.engine.network.activation.ActivationFunction;
import org.encog.engine.network.activation.ActivationSteepenedSigmoid;
import org.encog.engine.network.activation.ActivationTANH;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.pattern.ElmanPattern;
import org.encog.neural.pattern.FeedForwardPattern;
import org.encog.neural.pattern.JordanPattern;

import com.autoStock.signal.signalMetrics.SignalOfEncog;

/**
 * @author Kevin
 *
 */
public class EncogNetworkGenerator {
	public static BasicNetwork getBasicNetwork(int inputs, int outputs, ActivationFunction activationFunctionOutput){
		return getFeedForwardNetwork(inputs, outputs, activationFunctionOutput);
	}
	
	private static BasicNetwork getFeedForwardNetwork(int inputs, int outputs, ActivationFunction activationFunctionOutput){
		FeedForwardPattern pattern = new FeedForwardPattern();
		pattern.setInputNeurons(inputs);
		pattern.addHiddenLayer(inputs/2);
		pattern.addHiddenLayer(inputs/3);
		pattern.setOutputNeurons(outputs);
		pattern.setActivationFunction(new ActivationTANH());
		if (activationFunctionOutput != null){pattern.setActivationOutput(activationFunctionOutput);}
		return (BasicNetwork) pattern.generate();
	}
	
	private static BasicNetwork getRecurrentNetwork(int inputs, int outputs){
//		ElmanPattern pattern = new ElmanPattern();
		JordanPattern pattern = new JordanPattern();
	
		pattern.setInputNeurons(inputs);
		pattern.addHiddenLayer(inputs/2);
		pattern.setOutputNeurons(outputs);
		pattern.setActivationFunction(new ActivationTANH());
		return (BasicNetwork) pattern.generate();		
	}
}
