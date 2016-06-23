/**
 * 
 */
package com.autoStock.internal;

import com.autoStock.signal.SignalBase;
import com.autoStock.signal.SignalDefinitions.IndicatorParameters;
import com.autoStock.signal.SignalDefinitions.SignalParameters;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * @author Kevin
 *
 */
public class GsonProvider {
	public Gson getGsonInstance(){
		GsonBuilder gsonBuilder = new GsonBuilder();
		gsonBuilder.setPrettyPrinting();
		gsonBuilder.serializeSpecialFloatingPointValues();
		return gsonBuilder.create();
	}
	
	public Gson getGsonForBacktestEvaluations(){
		GsonBuilder gsonBuilder = new GsonBuilder();
		gsonBuilder.registerTypeAdapter(SignalParameters.class, new GsonClassAdapter());
		gsonBuilder.registerTypeAdapter(IndicatorParameters.class, new GsonClassAdapter());
		return gsonBuilder.create();
	}
	
	public Gson getGsonForSignalBase(){
		GsonBuilder gsonBuilder = new GsonBuilder();
		gsonBuilder.registerTypeAdapter(SignalBase.class, new GsonClassAdapter());
		gsonBuilder.registerTypeAdapter(SignalParameters.class, new GsonClassAdapter());
		gsonBuilder.registerTypeAdapter(IndicatorParameters.class, new GsonClassAdapter());
		gsonBuilder.setPrettyPrinting();
		gsonBuilder.serializeSpecialFloatingPointValues();
		return gsonBuilder.create();
	}
}
