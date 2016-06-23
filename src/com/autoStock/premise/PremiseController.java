/**
 * 
 */
package com.autoStock.premise;

import java.util.ArrayList;

import com.autoStock.signal.extras.EncogFrame;
import com.autoStock.signal.extras.EncogFrameSupport.EncogFrameSource;

/**
 * @author Kevin
 *
 */
public class PremiseController {
	private ArrayList<PremiseBase> listOfPremise = new ArrayList<PremiseBase>();
	
	public void determinePremise(){
		for (PremiseBase premise : listOfPremise){
			premise.run();
		}
	}
	
	public ArrayList<EncogFrame> getEncogFrames(){
		ArrayList<EncogFrame> listOfEncogFrame = new ArrayList<EncogFrame>();
		
		for (PremiseBase premise : listOfPremise){
			if (premise instanceof EncogFrameSource){
				listOfEncogFrame.add(((EncogFrameSource) premise).asEncogFrame());
			}
		}
		
		return listOfEncogFrame;
	}
	
	public void reset(){
		listOfPremise.clear();
	}

	public void addPremise(PremiseBase premise) {
		listOfPremise.add(premise);
	}
	
	public boolean isEmpty(){
		return listOfPremise.size() == 0;
	}
}
