/**
 * 
 */
package com.autoStock.premise;

import org.encog.persist.EncogWriteHelper;

import com.autoStock.Co;
import com.autoStock.signal.extras.EncogFrame;
import com.autoStock.signal.extras.EncogFrameSupport.EncogFrameSource;
import com.autoStock.tools.DateTools;
import com.autoStock.trading.platform.ib.definitions.HistoricalDataDefinitions.Resolution;
import com.autoStock.trading.types.HistoricalData;
import com.autoStock.types.Exchange;
import com.autoStock.types.Symbol;

/**
 * @author Kevin
 *
 */
public class PremiseTest {
	private PremiseController premiseController = new PremiseController();
	
	public void run(){
		Co.println("--> Premise test A");
	
		//		premiseController.addPremise(new PremiseOfOHLC(new Exchange("NYSE"), new Symbol("MS"), DateTools.getDateFromString("03/09/2012"), Resolution.day, 5));
		//premiseController.addPremise(new PremiseOfOHLC(new Exchange("NYSE"), new Symbol("MS"), DateTools.getDateFromString("03/09/2012"), Resolution.min_15, 1));
		
		//premiseController.determinePremise();
		
//		for (EncogFrame encogFrame : premiseController.getEncogFrames()){
//			Co.println("--> Have encog frame: " + encogFrame.description + " of " + encogFrame.asDoubleList().size());
//		}
		
		
//		PremiseBase premiseBase = new PremiseOfOHLC(new Exchange("NYSE"), new Symbol("MS"), DateTools.getDateFromString("02/27/2012"), Resolution.min_15, 1);
//		premiseBase.run();
//		Co.println("A: " + ((EncogFrameSource)premiseBase).asEncogFrame().asDoubleList().size());
//
//		premiseBase = new PremiseOfOHLC(new Exchange("NYSE"), new Symbol("MS"), DateTools.getDateFromString("02/28/2012"), Resolution.min_15, 1);
//		premiseBase.run();
//		Co.println("B: " + ((EncogFrameSource)premiseBase).asEncogFrame().asDoubleList().size());
		
		PremiseBase premiseBase = new PremiseOfOHLC(new Exchange("NYSE"), new Symbol("MS"), DateTools.getFirstWeekdayBefore(DateTools.getDateFromString("10/1/2014")), Resolution.min_15, 3);
		premiseBase.run();
		Co.println("C: " + ((EncogFrameSource)premiseBase).asEncogFrame().asNormalizedDoubleList().size());
	}
}
