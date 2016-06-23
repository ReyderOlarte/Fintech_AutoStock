/**
 * 
 */
package com.autoStock.comServer;

import java.util.ArrayList;

import com.autoStock.Co;
import com.autoStock.algorithm.AlgorithmBase;
import com.autoStock.context.ContextBase;
import com.autoStock.signal.extras.EncogFrame;
import com.autoStock.signal.extras.EncogSubframe;
import com.autoStock.signal.extras.EncogFrame.FrameType;
import com.autoStock.signal.extras.EncogFrameSupport.EncogFrameSource;
import com.autoStock.tools.ArrayTools;
import com.autoStock.tools.ListTools;
import com.autoStock.tools.MathTools;
import com.autoStock.tools.StringTools;
import com.autoStock.types.QuoteSlice;

/**
 * @author Kevin
 *
 */
public class ContextOfOHLC extends ContextBase implements EncogFrameSource {
	private AlgorithmBase algorithmBase;
	
	@Override
	public void run() {}

	@Override
	public EncogFrame asEncogFrame() {
		EncogFrame encogFrame = new EncogFrame(getClass().getSimpleName(), FrameType.raw);
		
		ArrayList<Double> listOfClose = new ArrayList<Double>();
		ArrayList<Double> listOfCO = new ArrayList<Double>();
	
		for (QuoteSlice quoteSlice : ListTools.getLast(algorithmBase.listOfQuoteSlice, 30)){
			listOfClose.add(quoteSlice.priceClose);
			listOfCO.add(quoteSlice.priceClose - quoteSlice.priceOpen);
//			Co.println(quoteSlice.toString());
		}
		
//		Co.println("\n");
//		Co.println(StringTools.arrayOfDoubleToString(ArrayTools.getArrayFromListOfDouble(listOfClose)));
//		Co.println(StringTools.arrayOfDoubleToString(ArrayTools.getArrayFromListOfDouble(listOfCO)));
//		Co.println("\n");
		
		EncogSubframe subframe1 = new EncogSubframe(getClass().getSimpleName(), ListTools.subList(MathTools.getDeltasAsPercent(listOfClose), 1, -1), FrameType.raw, 1.0, -1.0);
		encogFrame.addSubframe(subframe1);
		
		EncogSubframe subframe2 = new EncogSubframe(getClass().getSimpleName(), listOfCO, FrameType.raw, 1.0, -1.0);
		encogFrame.addSubframe(subframe2);		
		
		return encogFrame;
	}

	public void setAlgorithmBase(AlgorithmBase algorithmBase) {
		this.algorithmBase = algorithmBase;
	}  
}
