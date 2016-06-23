/**
 * 
 */
package com.autoStock.backtest.watchmaker;

/**
 * @author Kevin
 *
 */

public class WMEvolutionParams {
	public enum WMEvolutionType {
		type_island,
		type_generational,
		none,
	}
	
	public enum WMEvolutionThorough {
		thorough_quick(3),
		thorough_slow(5),
		;
		
		int generationCount;
		
		WMEvolutionThorough(int generationCount){
			this.generationCount = generationCount;
		}
	}
}
