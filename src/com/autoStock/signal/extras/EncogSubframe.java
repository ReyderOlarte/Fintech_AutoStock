/**
 * 
 */
package com.autoStock.signal.extras;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.encog.util.arrayutil.NormalizationAction;
import org.encog.util.arrayutil.NormalizedField;

import com.autoStock.Co;
import com.autoStock.signal.extras.EncogFrame.FrameType;
import com.autoStock.signal.signalMetrics.SignalOfEncog;
import com.autoStock.tools.ArrayTools;
import com.autoStock.tools.ListTools;
import com.autoStock.tools.MathTools;
import com.autoStock.tools.StringTools;

/**
 * @author Kevin
 *
 */
public class EncogSubframe {
	public final String description;
	private double[] values;
	public FrameType frameType;
	private NormalizedField normalizer;

	public EncogSubframe(String description, ArrayList<Double> values, FrameType frameType) {
		this(description, ArrayTools.getDoubleArray(values), frameType, 0, 0);
	}
	
	public EncogSubframe(String description, ArrayList<Double> values, FrameType frameType, double normalizerHigh, double normalizerLow) {
		this(description, ArrayTools.getDoubleArray(values), frameType, normalizerHigh, normalizerLow);
	}
	
	public EncogSubframe(String description, double[] values, FrameType frameType, double normailzerHigh, double normailzerLow) {
		this.description = description;
		this.values = values;
		this.frameType = frameType;
		
		if (normailzerHigh == 0 && normailzerLow == 0){
			throw new IllegalArgumentException("Define the normalizer: " + description);
		}
		
		normalizer = new NormalizedField(NormalizationAction.Normalize, null, normailzerHigh, normailzerLow, 1, -1); 
	}
	
	public double[] asNormalizedDoubleArray(){
		if (frameType == FrameType.category){
			return values;
		}
		
		double[] normalizedValues = new double[values.length];
		
		for (int i=0; i<values.length; i++){
			if (frameType != FrameType.percent_change){
				if (values[i] > normalizer.getActualHigh()){throw new IllegalArgumentException("Input value too high for normalizer: " + description + " -> " + normalizer.getActualHigh() + ", " + values[i]);}
				if (values[i] < normalizer.getActualLow()){throw new IllegalArgumentException("Input value too low for normalizer: " + description + " -> " + normalizer.getActualLow() + ", " + values[i]);}
			}
			normalizedValues[i] = normalizer.normalize(values[i]);
		}
		
		return normalizedValues;
	}
	
	public ArrayList<Double> asNormalizedDoubleList(){
		return ListTools.getListFromArray(asNormalizedDoubleArray());
	}
	
	public void replaceNaN(){
		for (int i=0; i<values.length; i++){
			if (Double.isNaN(values[i])){
				values[i] = 0;
			}
		}
	}
	
	public boolean isOutOfRange(){
		for (Double value : frameType == FrameType.category ? values : asNormalizedDoubleArray()){
			if (value < -1 || value > 1){
				return true;
			}
		}
		
		return false;
	}
	
	public boolean isAllZeros(){
		for (Double value : values){
			if (value != 0){
				return false;
			}
		}
		
		return true;
	}
	
	public NormalizedField getNormalizer(){
		return normalizer;
	}
	
	@Override
	public String toString() {
		return "Subframe: " + description + " normalizer: " + normalizer.getActualHigh() + ":" +normalizer.getActualLow() + " length: " + values.length + " -> " + StringTools.arrayOfDoubleToString(values);
	}
}
