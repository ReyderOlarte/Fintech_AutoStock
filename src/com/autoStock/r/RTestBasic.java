package com.autoStock.r;

import java.util.Arrays;

import org.rosuda.JRI.Rengine;

import com.autoStock.Co;
import com.autoStock.tools.Benchmark;

public class RTestBasic {
	public void run(){
		Rengine rEngine = RJavaController.getInstance().getREngine();

		Benchmark bench = new Benchmark();
		bench.printTick("--> Started up");
		
		double[][] ohlc = new double[][]{
				{10.34, 10.35, 10.34},
				{10.30, 10.33, 10.32},
				{10.34, 10.35, 10.33},
				{10.34, 10.35, 10.34},
				{10.34, 10.35, 10.35},
				{10.34, 10.35, 10.32},
				{10.34, 10.39, 10.34},
				{10.34, 10.35, 10.34},
				{10.34, 10.35, 10.34},
				{10.34, 10.38, 10.34},
				{10.38, 10.35, 10.39},
				{10.34, 10.35, 10.34},
				{10.34, 10.32, 10.34},
				{10.34, 10.35, 10.38},
				{10.32, 10.35, 10.34},
				{10.34, 10.35, 10.34},
				{10.34, 10.33, 10.34},
				{10.34, 10.35, 10.34},
				{10.34, 10.35, 10.34},
				{10.33, 10.37, 10.35},
				{10.34, 10.35, 10.34},
				{10.34, 10.33, 10.30},
				{10.36, 10.35, 10.32},
				{10.34, 10.35, 10.34},
				{10.35, 10.35, 10.34},
				{10.34, 10.35, 10.34},
				{10.34, 10.35, 10.34},
				{10.32, 10.37, 10.34},
				{10.34, 10.35, 10.36},
				{10.31, 10.35, 10.34},
				};
		
		RUtils.assignAsRMatrix(rEngine, ohlc, "a");
		
		Co.println("" + rEngine.eval("exists(\"a\")").asBool());
		
		bench.tick();
		
		for (double value : Arrays.copyOfRange(rEngine.eval("CCI(a)").asDoubleArray(), 19, 30)){Co.println("--> Value: " + value);}
		
		bench.printTick("Done");
	}
}
