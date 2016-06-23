/**
 * 
 */
package com.autoStock.r;

import org.rosuda.JRI.RMainLoopCallbacks;
import org.rosuda.JRI.Rengine;

import com.autoStock.Co;

/**
 * @author Kevin
 *
 */
public class REmptyCallback implements RMainLoopCallbacks {
	@Override
	public String rChooseFile(Rengine arg0, int arg1) {
		return null;
	}
	
	@Override
	public String rReadConsole(Rengine arg0, String arg1, int arg2) {
		return null;
	}
	
	@Override
	public void rBusy(Rengine arg0, int arg1) {}

	@Override
	public void rFlushConsole(Rengine arg0) {}

	@Override
	public void rLoadHistory(Rengine arg0, String arg1) {}

	@Override
	public void rSaveHistory(Rengine arg0, String arg1) {}

	@Override
	public void rShowMessage(Rengine arg0, String arg1) {}

	@Override
	public void rWriteConsole(Rengine arg0, String arg1, int arg2) {}
}
