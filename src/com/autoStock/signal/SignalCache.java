/**
 * 
 */
package com.autoStock.signal;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;

import com.autoStock.Co;
import com.autoStock.algorithm.AlgorithmBase;
import com.autoStock.internal.GsonProvider;
import com.autoStock.signal.signalMetrics.SignalOfCandlestickGroup;
import com.autoStock.signal.signalMetrics.SignalOfEncog;
import com.autoStock.types.QuoteSlice;
import com.google.gson.reflect.TypeToken;

/**
 * @author Kevin
 *
 */
public class SignalCache {	
	public static final File CACHE_FILE = new File("signal_cache.file");
	private ArrayList<SignalCachePackage> listOfPackage = new ArrayList<SignalCachePackage>();
	public AlgorithmBase algorithmBase;
	
	public SignalCache() {}
	
	public SignalCache(AlgorithmBase algorithmBase) {
		this.algorithmBase = algorithmBase;
	}

	public void setAlgorithmBase(AlgorithmBase algorithmBase){
		this.algorithmBase = algorithmBase;
	}

	public void storeSignalGroup(){
		ArrayList<SignalBase> listOfSignalBase = new ArrayList<SignalBase>();
		
		for (SignalBase signalBase : algorithmBase.signalGroup.getListOfSignalBase()){
			if (signalBase instanceof SignalOfCandlestickGroup == false 
				&& signalBase instanceof SignalOfEncog == false){
				
				if (algorithmBase.listOfSignalMetricTypeAnalyze.contains(signalBase.signalMetricType)){
					//Co.println("********** Storing signal ***********" + signalBase.getClass().getSimpleName());
					listOfSignalBase.add(signalBase);
				}
			}
		}
		
		listOfPackage.add(new SignalCachePackage(algorithmBase.symbol, algorithmBase.exchange, algorithmBase.startingDate, listOfSignalBase));
	}
	
	public void setToQuoteSlice(QuoteSlice quoteSlice, int index){
		//Co.println("--> Setting to quote slice: " + quoteSlice.toString());
		
		ArrayList<SignalBase> listToRestoreFrom = getSignalCachePackage().listOfListOfSignalBase;
		
		if (listToRestoreFrom.size() == 0){
			throw new IllegalStateException("List of restored SignalBase is 0");
		}
		
		for (SignalBase signalBase : listToRestoreFrom){
			//Co.println("--> Restoring: " + signalBase.getClass().getSimpleName());
			SignalBase signalBaseRestoreInto = algorithmBase.signalGroup.getSignalBaseForType(signalBase.signalMetricType);
			if (signalBase.listOfNormalizedAveragedValue.size() > 0 && signalBase.listOfNormalizedValuePersist.size() > 0){
				signalBaseRestoreInto.setInputCached(signalBase.listOfNormalizedAveragedValuePersist.get(index), signalBase.listOfNormalizedValuePersist.get(index), signalBase.listOfRawValuePersist.get(index));
				//Co.println("--> SignalBase is at: " + signalBaseRestoreInto.signalMetricType.name() + ", " + signalBaseRestoreInto.listOfNormalizedAveragedValuePersist.size() + ", " + signalBase.listOfNormalizedValuePersist.get(index) + ", " + signalBaseRestoreInto.getStrength());
			}
		}
	}
	
	public SignalCachePackage getSignalCachePackage(){
		for (SignalCachePackage pack : listOfPackage){
			if (pack.dateStart.equals(algorithmBase.startingDate)){
				return pack;
			}
		}
		
		return null;
	}
	
	public void writeToDisk(){
		Co.println("--> Writing to disk");
		
		try {
			FileWriter fileWriter = new FileWriter(CACHE_FILE);
			fileWriter.write(new GsonProvider().getGsonForSignalBase().toJson(listOfPackage, new TypeToken<ArrayList<SignalCachePackage>>(){}.getType()));
			fileWriter.close();
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	public void restoreFromDisk() {
		if (CACHE_FILE.exists()){
			Co.println("--> Restoring from disk");
			try {
				FileReader fileReader = new FileReader(CACHE_FILE);
				listOfPackage = new GsonProvider().getGsonForSignalBase().fromJson(fileReader, new TypeToken<ArrayList<SignalCachePackage>>(){}.getType());
			}catch(Exception e){
				e.printStackTrace();
			}
		}
	}

	public boolean isAvailable() {
		return getSignalCachePackage() != null;
	}
	
	public static void erase(){
		CACHE_FILE.delete();
	}
}
