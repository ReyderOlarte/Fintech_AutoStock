package com.autoStock.tools;

import java.util.Date;

import com.autoStock.Co;

/**
 * @author Kevin Kowalewski
 *
 */
public class Benchmark {
	private long startMark = getCurrentTime();
	private long lastMark = getCurrentTime();
	public boolean hasTicked = false;
	public boolean useNs = false;
	

	public Benchmark(){}
	
	public Benchmark(boolean useNs){
		this.useNs = useNs;
	}

	public void tick(){
		if (hasTicked == false){hasTicked = true;}
		long currentTimeMills = getCurrentTime();
		//Co.log("Tick: " + (currentTimeMills - lastMark) + "ms");
		lastMark = currentTimeMills;
	}
	
	public void printTick(String action){
		long currentTimeMills = getCurrentTime();
		Co.log("Tick: [" + action + "] " + MiscTools.getCommifiedValue((currentTimeMills - lastMark), 0) + (useNs ? "ns" : "ms"));
		lastMark = currentTimeMills;
	}
	
	public void printTotal(){
		long currentTimeMills = getCurrentTime();
		Co.log("Benchmark: " + MiscTools.getCommifiedValue((currentTimeMills - startMark), 0));
	}
	
	public long getCurrentTime(){
		if (useNs){
			return System.nanoTime();
		}else{
			return System.currentTimeMillis();
		}
	}
}
