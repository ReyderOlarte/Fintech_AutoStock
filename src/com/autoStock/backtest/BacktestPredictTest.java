/**
 * 
 */
package com.autoStock.backtest;

import org.encog.engine.network.activation.ActivationTANH;
import org.encog.ml.data.MLData;
import org.encog.ml.data.basic.BasicMLData;
import org.encog.ml.data.temporal.TemporalDataDescription;
import org.encog.ml.data.temporal.TemporalDataDescription.Type;
import org.encog.ml.data.temporal.TemporalMLDataSet;
import org.encog.ml.data.temporal.TemporalPoint;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.training.propagation.resilient.ResilientPropagation;
import org.encog.neural.pattern.FeedForwardPattern;
import org.encog.util.arrayutil.NormalizationAction;
import org.encog.util.arrayutil.NormalizedField;

import com.autoStock.signal.signalMetrics.SignalOfEncog;

/**
 * @author Kevin
 *
 */
public class BacktestPredictTest {
	private static final int MAX = 100;
	
	public void fun(){
		NormalizedField nf = new NormalizedField(NormalizationAction.Normalize, "Normalizer", MAX + 10, 0, 1, 0);
		
		TemporalMLDataSet tds = new TemporalMLDataSet(5, 1);
		TemporalDataDescription tdd = new TemporalDataDescription(new ActivationTANH(), Type.RAW, true, true);
		
		tds.addDescription(tdd);
		
		for (int i=1; i<=MAX; i++){
			TemporalPoint tp = new TemporalPoint(1);
			tp.setData(0, nf.normalize(i));
			tds.getPoints().add(tp);
			
			System.out.println("-->NF: " + i + " -> " + nf.normalize(i));
		}
		
		tds.generate();
		
		BasicNetwork network = getMLNetwork(5, 1);
		
//		EncogUtility.trainToError(network, tds, 0.002 / 1000);
		
		ResilientPropagation rp = new ResilientPropagation(network, tds);
		
		for (int i=0; i<20000; i++){
			rp.iteration();
			System.out.println("" + rp.getError() * 1000);
		}
		
		testInput(6, nf, network);
		testInput(7, nf, network);
		testInput(8, nf, network);
		testInput(9, nf, network);
		testInput(10, nf, network);
	}
	
	public void testInput(int startInput, NormalizedField nf, BasicNetwork network){
		MLData input = new BasicMLData(5);
		input.add(0, nf.normalize(startInput));
		input.add(1, nf.normalize(startInput+1));
		input.add(2, nf.normalize(startInput+2));
		input.add(3, nf.normalize(startInput+3));
		input.add(4, nf.normalize(startInput+4));
		
		MLData output = network.compute(input);
		System.out.println("--> Output: " + Math.round(nf.deNormalize(output.getData(0))));
	}
	
	public BasicNetwork getMLNetwork(int inputSize, int outputSize){
//		ElmanPattern pattern = new ElmanPattern();
//		JordanPattern pattern = new JordanPattern();
		
		int inputWindow = SignalOfEncog.getInputWindowLength();
		
		FeedForwardPattern pattern = new FeedForwardPattern();
		pattern.setInputNeurons(inputSize);
		pattern.addHiddenLayer(inputWindow/2);
//		pattern.addHiddenLayer(inputWindow/3);
		pattern.setOutputNeurons(outputSize);
		pattern.setActivationFunction(new ActivationTANH());
		return (BasicNetwork) pattern.generate();
	}
}
