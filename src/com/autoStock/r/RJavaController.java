/**
 * 
 */
package com.autoStock.r;

import java.io.*;
import java.awt.Frame;
import java.awt.FileDialog;
import java.util.Enumeration;

import org.rosuda.JRI.Rengine;
import org.rosuda.JRI.REXP;
import org.rosuda.JRI.RList;
import org.rosuda.JRI.RVector;
import org.rosuda.JRI.RMainLoopCallbacks;

import com.autoStock.Co;
import com.autoStock.osPlatform.Os;
import com.autoStock.osPlatform.Os.OsType;


/**
 * @author Kevin
 *
 */
public class RJavaController {
	public static RJavaController instance = new RJavaController();
	private boolean useCompiler = false;
	private boolean useTextConsole = false;
	private Rengine rEngine;
	
	private RJavaController(){
		if (Rengine.versionCheck() == false){throw new IllegalStateException("Could not verify REngine version");}
		loadRInstance();	
	}
	
	private void loadRInstance() {
		rEngine = new Rengine(new String[]{}, false, useTextConsole ? new TextConsoleX() : new REmptyCallback());
		if (rEngine.waitForR() == false){throw new IllegalStateException("Could start R properly...");}
		
		loadLibraries("quantmod", "TTR");
		
		if (useCompiler){
			rEngine.eval("require(\"compiler\")");
			rEngine.eval("enableJIT(0)");
		}
		
		Co.println("--> Started instance");
	}
	
	private void loadLibraries(String... libraries){
		for (String library : libraries){
			rEngine.eval("library(\"" + library + "\")");
		}
	}
	
	public synchronized Rengine getREngine(){
		return rEngine;
	}

	public static RJavaController getInstance() {
		return instance;
	}
}
