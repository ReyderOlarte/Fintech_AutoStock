/**
 * 
 */
package com.autoStock.context;

import java.util.ArrayList;

import com.autoStock.premise.PremiseBase;
import com.autoStock.signal.extras.EncogFrame;
import com.autoStock.signal.extras.EncogFrameSupport.EncogFrameSource;

/**
 * @author Kevin
 *
 */
public class ContextController {
	private ArrayList<ContextBase> listOfContext = new ArrayList<ContextBase>();
	
	public void determineContext(){
		for (ContextBase context : listOfContext){
			context.run();
		}
	}
	
	public ArrayList<EncogFrame> getEncogFrames(){
		ArrayList<EncogFrame> listOfEncogFrame = new ArrayList<EncogFrame>();
		
		for (ContextBase premise : listOfContext){
			if (premise instanceof EncogFrameSource){
				listOfEncogFrame.add(((EncogFrameSource) premise).asEncogFrame());
			}
		}
		
		return listOfEncogFrame;
	}
	
	public void reset(){
		listOfContext.clear();
	}

	public void addContext(ContextBase context) {
		listOfContext.add(context);
	}

	public ContextBase getByClass(Class clazz) {
		for (ContextBase contextBase : listOfContext){
			if (contextBase.getClass() == clazz){
				return contextBase;
			}
		}
		
		return null;
	}
	
	public boolean isEmpty(){
		return listOfContext.size() == 0;
	}
}
