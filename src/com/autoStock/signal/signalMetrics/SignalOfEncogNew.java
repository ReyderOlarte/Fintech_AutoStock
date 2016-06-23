package com.autoStock.signal.signalMetrics;

import java.util.ArrayList;

import org.encog.ml.MLMethod;
import org.encog.ml.MLRegression;
import org.encog.ml.data.MLData;
import org.encog.ml.data.basic.BasicMLData;
import org.encog.neural.networks.BasicNetwork;
import org.encog.util.arrayutil.NormalizationAction;
import org.encog.util.arrayutil.NormalizedField;

import com.autoStock.Co;
import com.autoStock.algorithm.AlgorithmBase;
import com.autoStock.backtest.encog.TrainEncogBase;
import com.autoStock.backtest.encog.TrainEncogNetworkOfBasic;
import com.autoStock.position.PositionDefinitions.PositionType;
import com.autoStock.signal.SignalBase;
import com.autoStock.signal.SignalDefinitions.SignalMetricType;
import com.autoStock.signal.SignalDefinitions.SignalParameters;
import com.autoStock.signal.SignalDefinitions.SignalParametersForEncog;
import com.autoStock.signal.SignalDefinitions.SignalPointType;
import com.autoStock.signal.SignalGroup;
import com.autoStock.signal.SignalPoint;
import com.autoStock.signal.extras.EncogFrame;
import com.autoStock.signal.extras.EncogInputWindow;
import com.autoStock.signal.extras.EncogNetworkCache;
import com.autoStock.signal.extras.EncogNetworkGenerator;
import com.autoStock.signal.extras.EncogNetworkProvider;
import com.autoStock.signal.extras.EncogFrame.FrameType;
import com.autoStock.tools.ArrayTools;
import com.autoStock.tools.Benchmark;
import com.autoStock.tools.ListTools;
import com.autoStock.trading.types.Position;

/**
 * @author Kevin Kowalewski
 * 
 */
public class SignalOfEncogNew extends SignalBase {
	private static final double NEURON_THRESHOLD = 0.95;
	private EncogInputWindow encogInputWindow;
	private EncogNetworkProvider encogNetworkProvider = new EncogNetworkProvider();
	private ArrayList<EncogNetwork> listOfNetworks = new ArrayList<EncogNetwork>();
	private String networkName;
	private Benchmark bench = new Benchmark();
	
	public static class EncogNetwork {
		public String name;
		public int inputs;
		public int outputs;
		public MLMethod method;
		public ArrayList<EncogFrame> listOfFrame = new ArrayList<EncogFrame>();
		
		public EncogNetwork(MLMethod method, String name, EncogFrame encogFrame) {
			this.method = method;
			this.name = name;
			listOfFrame.add(encogFrame);
		}
		
		public EncogNetwork(int inputs, int outputs, String name) {
			this.name = name;
			this.inputs = inputs;
			this.outputs = outputs;
		}
		
		public EncogNetwork generate(){
			this.method = EncogNetworkGenerator.getBasicNetwork(inputs, outputs, null);
			return this;
		}
		
		@Override
		public String toString() {
			return name + " : " + ((BasicNetwork)method).getInputCount() + ", " + ((BasicNetwork)method).getOutputCount();
		}
	}
	
	public SignalOfEncogNew(SignalParametersForEncog signalParameters, AlgorithmBase algorithmBase) {
		super(SignalMetricType.metric_encog, signalParameters, algorithmBase);
	}

	public void setInput(EncogInputWindow encogInputWindow) {
		if (encogInputWindow == null){throw new IllegalArgumentException("Can't set a null EncogInputWindow");}
		this.encogInputWindow = encogInputWindow;
	}
	
	public void setNetworkName(String networkName){
		this.networkName = networkName;
		//Co.println("--> Set name");
		createNetworks();
		readFromDisk();
	}
	
	public void setNetwork(MLRegression network, int which){
		//Co.println("--> Set network at / with: " + which + ", " + ((BasicNetwork)network).getInputCount());
		listOfNetworks.get(which).method = network;
	}
	
	public void setNetwork(MLRegression network){
		throw new IllegalAccessError("Don't call this");
	}
	
	public void createNetworks(){
//		for (EncogFrame encogFrame : encogInputWindow.getFrames()){
//			new EncogNetwork(EncogNetworkGenerator.getBasicNetwork(encogFrame.getLength(), outputs), null);
//		}

//		listOfNetworks.add(new EncogNetwork(1, 4, networkName + "-0").generate());
		listOfNetworks.add(new EncogNetwork(80, 4, networkName + "-0").generate());
	}
	
	private void readFromDisk(){
		for (EncogNetwork encogNetwork : listOfNetworks){
			readNetwork(encogNetwork);
		}
	}
	
	private void readNetwork(EncogNetwork encogNetwork){
		BasicNetwork basicNetworkCached = (BasicNetwork) EncogNetworkCache.getInstance().get(encogNetwork.name, true);
		
		if (basicNetworkCached != null){
			//Co.println("--> Used cached: " + encogNetwork.name);
			encogNetwork.method = basicNetworkCached;
		}else{
			BasicNetwork basicNetworkFromDisk = encogNetworkProvider.getBasicNetwork(encogNetwork.name);
			EncogNetworkCache.getInstance().put(encogNetwork.name, basicNetworkFromDisk);
			encogNetwork.method = basicNetworkFromDisk;
		}
	}
	
	public ArrayList<EncogNetwork> getNetworks(){
		return listOfNetworks;
	}

	
	@Override
	public SignalPoint getSignalPoint(Position position) {
		
		if (encogInputWindow == null || listOfNetworks.size() == 0){
			return new SignalPoint();
		}
		
		BasicNetwork mainNetwork = (BasicNetwork) listOfNetworks.get(0).method;
		ArrayList<Double> inputFromNetworks = new ArrayList<Double>();
		
		int index = 0;
		
		for (EncogFrame encogFrame : encogInputWindow.getFrames()){
			EncogNetwork network = listOfNetworks.get(index);
			BasicMLData input = new BasicMLData(encogFrame.getLength());

//			Co.println("--> Input: " + network.name + ", " + encogFrame.description + ", " + encogFrame.frameType.name() + ", " + encogFrame.getLength());
//			
//			for (Double value : encogFrame.asDoubleList()){
//				Co.print(" " + value);
//			}
			
			Co.println("--> Have frame: " + encogFrame.frameType.name());
			
			// ** Fix this if ever needed, EncogFrames are auto normalized to be acceptable NN input
			
//			if (encogFrame.frameType == FrameType.raw){
//				for (int i=0; i<encogFrame.getLength(); i++){
//					input.add(i, normalizerLarge.normalize(encogFrame.asNormalizedDoubleList()()[i]));
//					dataCheck(input.getData(i));
//				}
//			}else if (encogFrame.frameType == FrameType.percent_change || encogFrame.frameType == FrameType.delta_change){
//				for (int i=0; i<encogFrame.getLength(); i++){
//					input.add(i, normalizerSmall.normalize(encogFrame.asDoubleArray()[i]));
//					dataCheck(input.getData(i));
//				}
//			}else{
//				throw new IllegalArgumentException("Can't handle frame type: " + encogFrame.description + ", " + encogFrame.frameType.name());
//			}
//			
//			if (true){
//				return getSignalPointFromData(mainNetwork, input, havePosition, positionType);
//			}
			
//			for (Double value : input.getData()){
//				Co.print(" " + value);
//			}
//			Co.println("\n");
//			
			MLData output = ((BasicNetwork)network.method).compute(input);
//			
//			Co.println("--> Output from network at/is: " + index + ", " + output.size());
//			
//			for (Double value : output.getData()){
//				Co.print(" " + value);
//			}
//			
//			Co.println("\n");
			
			inputFromNetworks.addAll(ListTools.getListFromArray(output.getData()));
			index++;
		}
		
		return null;
		
//		MLData mainInput = new BasicMLData(ArrayTools.getArrayFromListOfDouble(inputFromNetworks));
		
//		Co.println("--> Input to main is: ");
//		for (Double value : mainInput.getData()){
//			Co.print(" " + value);
//		}
//		Co.println("\n");
		
		//Co.println("--> Output from main is: ");
//		for (Double value : output.getData()){
//			//Co.print(" " + value);
//			if (value > NEURON_THRESHOLD){
////				throw new IllegalAccessError();
////				Co.println("--> Cool");
//			}
//		}
//		Co.println("\n");


	}
	
	private SignalPoint getSignalPointFromData(MLMethod method, MLData input, boolean havePosition, PositionType positionType){
		SignalPoint signalPoint = new SignalPoint();
		MLData output = ((BasicNetwork)method).compute(input);

		double valueForLongEntry = output.getData(0);
		double valueForShortEntry = output.getData(1);
		double valueForAnyExit = output.getData(2);
		double valueForReeentry = output.getData(3);
		
//		Co.println("--> Values: " + valueForLongEntry + ", " + valueForShortEntry + ", " + valueForAnyExit);

		if (valueForLongEntry >= NEURON_THRESHOLD) {
			signalPoint.signalPointType = SignalPointType.long_entry;
			signalPoint.signalMetricType = SignalMetricType.metric_encog;
		} else if (valueForShortEntry >= NEURON_THRESHOLD) {
			signalPoint.signalPointType = SignalPointType.short_entry;
			signalPoint.signalMetricType = SignalMetricType.metric_encog;
		} else if (valueForReeentry >= NEURON_THRESHOLD) {
			signalPoint.signalPointType = SignalPointType.reentry;
			signalPoint.signalMetricType = SignalMetricType.metric_encog;
		} else if (valueForAnyExit >= NEURON_THRESHOLD && havePosition) {
			if (positionType == PositionType.position_long) {
				signalPoint.signalPointType = SignalPointType.long_exit;
			} else if (positionType == PositionType.position_short) {
				signalPoint.signalPointType = SignalPointType.short_exit;
			} else {
				throw new IllegalStateException("Have position of type: " + positionType.name());
			}
			signalPoint.signalMetricType = SignalMetricType.metric_encog;
		}
		
//		bench.printTick("signalPoint");
		
		return signalPoint;
	}
	
	private void dataCheck(double value){
		if (Double.isNaN(value) || Double.isInfinite(value)){
			throw new IllegalArgumentException("NaN or Infinity: " + value);
		}
	}
	
	public boolean isLongEnough(SignalBase... arrayOfSignalBase) {
		for (SignalBase signalBase : arrayOfSignalBase){
			if (signalBase.listOfNormalizedValuePersist.size() <= 20){
				return false;
			}
		}
		
		return true;
	}
}
