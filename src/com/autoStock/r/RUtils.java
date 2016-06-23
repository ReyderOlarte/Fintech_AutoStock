/**
 * 
 */
package com.autoStock.r;

import org.rosuda.JRI.REXP;
import org.rosuda.JRI.Rengine;

/**
 * @author Kevin
 *
 */
public class RUtils {
	public static REXP assignAsRMatrix(Rengine rEngine, double[][] sourceArray, String nameToAssignOn) {
		if (sourceArray.length == 0) {return null;}

		rEngine.assign(nameToAssignOn, sourceArray[0]);
		
		REXP resultMatrix = rEngine.eval(nameToAssignOn + " <- matrix( " + nameToAssignOn + " ,nr=1)");
		
		for (int i = 1; i < sourceArray.length; i++) {
			rEngine.assign("temp", sourceArray[i]);
			resultMatrix = rEngine.eval(nameToAssignOn + " <- rbind(" + nameToAssignOn + ",matrix(temp, nr=1))");
		}

		rEngine.assign("temp", new String());
		
		return resultMatrix;
	}
}
