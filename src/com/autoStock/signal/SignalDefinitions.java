/**
 * 
 */
package com.autoStock.signal;
import java.util.ArrayList;
import java.util.Arrays;

import com.autoStock.guage.SignalGuage;
import com.autoStock.tools.MathTools;
import com.autoStock.types.basic.MutableDouble;
import com.autoStock.types.basic.MutableEnum;
import com.autoStock.types.basic.MutableInteger;
import com.google.gson.internal.Pair;
import com.rits.cloning.Cloner;

/**
 * @author Kevin Kowalewski
 *
 */
public class SignalDefinitions {
	public static enum SignalSource{
		from_algorithm,
		from_market_trend,
		from_news,
		from_manual
	}
	
	public static enum SignalPointType {
		long_entry,
		long_exit,
		short_entry,
		short_exit,
		reentry,
		no_change,
		none,
	}
	
	public static enum SignalGuageType {
		guage_peak,
		guage_trough,
		guage_threshold_met,
		guage_threshold_left,
		
		custom_always_true,
		custom_always_false,
		none,
		;
	}
	
	public static enum SignalBounds {
		bounds_upper,
		bounds_lower,
	}
	
	public static enum SignalCoherence {
		//cusp
		//fringe
		//peak
		//trough
		//steady
		//tapered
		//crossover
	}
	
	public static enum SignalMetricType {
		metric_adx,
		metric_cci,
		metric_rsi,
		metric_di,
		metric_ppc,
		metric_macd,
		metric_trix,
		metric_roc,
		metric_mfi,
		metric_willr,
		metric_storsi,
		metric_uo,
		metric_ar_up,
		metric_ar_down,
		metric_sar,
		metric_crossover,
		
		metric_candlestick_group,
		metric_encog,
		
		none,
		mixed,
		no_change, 
		injected,
		;
		
		public static ArrayList<SignalMetricType> asList(){
			return new ArrayList<SignalMetricType>(Arrays.asList(values()));
		}
	}
	
	public static abstract class IndicatorParameters {
		public MutableInteger periodLength;
		public int resultSetLength;
		
		public IndicatorParameters(MutableInteger periodLength, int resultSetLength){
			this.periodLength = periodLength;
			this.resultSetLength = resultSetLength;
		}
		
		public IndicatorParameters(){} //For Gson
		
		public IndicatorParameters copy(){
			return new Cloner().deepClone(this);
		}
	}
	
	public static abstract class SignalParameters {
		public NormalizeInterface normalizeInterface;
		public MutableInteger maxSignalAverage;
		public MutableInteger periodLength = new MutableInteger(0);
		public MutableInteger maxSignalNormalizedWindow;
		public SignalGuage[] arrayOfSignalGuageForLongEntry;
		public SignalGuage[] arrayOfSignalGuageForLongExit;
		public SignalGuage[] arrayOfSignalGuageForShortEntry;
		public SignalGuage[] arrayOfSignalGuageForShortExit;
		public String extras;
		
		public SignalParameters(NormalizeInterface normalizeInterface, MutableInteger maxSignalAverage, SignalGuage[] arrayOfSignalGuageForLongEntry, SignalGuage[] arrayOfSignalGuageForLongExit, SignalGuage[] arrayOfSignalGuageForShortEntry, SignalGuage[] arrayOfSignalGuageForShortExit) {
			this.normalizeInterface = normalizeInterface;
			this.maxSignalAverage = maxSignalAverage;
			this.arrayOfSignalGuageForLongEntry = arrayOfSignalGuageForLongEntry;
			this.arrayOfSignalGuageForLongExit = arrayOfSignalGuageForLongExit;
			this.arrayOfSignalGuageForShortEntry = arrayOfSignalGuageForShortEntry;
			this.arrayOfSignalGuageForShortExit = arrayOfSignalGuageForShortExit;
			
			if (maxSignalAverage == null){this.maxSignalAverage = new MutableInteger(1);}
			if (normalizeInterface == null){this.normalizeInterface = new NormalizeInterface(){@Override public double normalize(double input) {return input;}};}
		}
		
		public SignalParameters(){} //For Gson
		
		public double getNormalizedValue(double input){
			return this.normalizeInterface.normalize(input);
		}
		
		public ArrayList<Pair<SignalPointType, SignalGuage[]>> getGuages(){
			ArrayList<Pair<SignalPointType, SignalGuage[]>> listOfGuages = new ArrayList<Pair<SignalPointType, SignalGuage[]>>();
			
			listOfGuages.add(new Pair<SignalPointType, SignalGuage[]>(SignalPointType.long_entry, arrayOfSignalGuageForLongEntry));
			listOfGuages.add(new Pair<SignalPointType, SignalGuage[]>(SignalPointType.long_exit, arrayOfSignalGuageForLongExit));
			listOfGuages.add(new Pair<SignalPointType, SignalGuage[]>(SignalPointType.short_entry, arrayOfSignalGuageForShortEntry));
			listOfGuages.add(new Pair<SignalPointType, SignalGuage[]>(SignalPointType.short_exit, arrayOfSignalGuageForShortExit));
			
			return listOfGuages;
		}
		
		public ArrayList<SignalGuage> getGuagesForType(SignalPointType signalPointType, SignalGuageType... arrayOfSignalGuageTypes){
			ArrayList<Pair<SignalPointType, SignalGuage[]>> listOfGuages = getGuages();
			ArrayList<SignalGuage> listOfGuagesForType = new ArrayList<SignalGuage>();
			
			for (Pair<SignalPointType, SignalGuage[]> pair : listOfGuages){
				if (pair.first == signalPointType && pair.second != null){
					for (SignalGuage signalGuage : pair.second){
						for (SignalGuageType signalGuageType : arrayOfSignalGuageTypes){
							if (signalGuage.mutableEnumForSignalGuageType.value == signalGuageType){
								listOfGuagesForType.add(signalGuage);
							}
						}
					}
				}
			}
			
			return listOfGuagesForType;
		}
		
		@Override
		public String toString() {
			return "1 = " + normalizeInterface.normalize(1);
		}
		
		public SignalParameters copy(){
			return new Cloner().deepClone(this);
		}

		public boolean hasValidGuages() {
			if (arrayOfSignalGuageForLongEntry == null || arrayOfSignalGuageForLongExit == null || arrayOfSignalGuageForShortEntry == null || arrayOfSignalGuageForShortExit == null){
				return false;
			}
			return true;
		}
	}
	
	public static class SignalParametersForADX extends SignalParameters {
		public SignalParametersForADX(){
			super(new NormalizeInterface(){@Override public double normalize(double input) {return input - 50;}}, new MutableInteger(5),
			new SignalGuage[]{new SignalGuage(new MutableEnum<SignalGuageType>(SignalGuageType.guage_threshold_met), SignalBounds.bounds_lower, 48)},
			new SignalGuage[]{new SignalGuage(new MutableEnum<SignalGuageType>(SignalGuageType.guage_threshold_met), SignalBounds.bounds_upper, -44)},
			new SignalGuage[]{new SignalGuage(new MutableEnum<SignalGuageType>(SignalGuageType.guage_threshold_met), SignalBounds.bounds_lower, -100)},
			new SignalGuage[]{new SignalGuage(new MutableEnum<SignalGuageType>(SignalGuageType.guage_threshold_met), SignalBounds.bounds_upper, -100)});
		}
	}
	
	public static class IndicatorParametersForADX extends IndicatorParameters {
		public IndicatorParametersForADX() {super(new MutableInteger(30), 1);}
	}
	
	public static class SignalParametersForPPC extends SignalParameters {
		public SignalParametersForPPC(){
			super(new NormalizeInterface(){@Override public double normalize(double input) {return input;}}, null, null, null, null, null);
		}
	}
	
	public static class IndicatorParametersForPPC extends IndicatorParameters {
		public IndicatorParametersForPPC() {super(new MutableInteger(30), 1);}
	}
	
	public static class SignalParametersForDI extends SignalParameters {
		public SignalParametersForDI(){
			super(new NormalizeInterface(){@Override public double normalize(double input) {return input * 1;}}, new MutableInteger(1),
			new SignalGuage[]{new SignalGuage(new MutableEnum<SignalGuageType>(SignalGuageType.guage_threshold_met), SignalBounds.bounds_lower, 0)},
			new SignalGuage[]{new SignalGuage(new MutableEnum<SignalGuageType>(SignalGuageType.guage_threshold_met), SignalBounds.bounds_upper, 0)},
			new SignalGuage[]{new SignalGuage(new MutableEnum<SignalGuageType>(SignalGuageType.guage_threshold_met), SignalBounds.bounds_upper, 0)},
			new SignalGuage[]{new SignalGuage(new MutableEnum<SignalGuageType>(SignalGuageType.guage_threshold_met), SignalBounds.bounds_lower, 0)});
		}
	}
	
	public static class IndicatorParametersForDI extends IndicatorParameters {
		public IndicatorParametersForDI() {super(new MutableInteger(30), 1);}
	}
	
	public static class SignalParametersForCCI extends SignalParameters {
		public SignalParametersForCCI() {
			super(new NormalizeInterface(){@Override public double normalize(double input) {return (input / 15);}}, new MutableInteger(1),
			    new SignalGuage[]{new SignalGuage(new MutableEnum<SignalGuageType>(SignalGuageType.guage_threshold_met), SignalBounds.bounds_lower, 0)},
				new SignalGuage[]{new SignalGuage(new MutableEnum<SignalGuageType>(SignalGuageType.guage_threshold_met), SignalBounds.bounds_upper, 0)},				  
				new SignalGuage[]{new SignalGuage(new MutableEnum<SignalGuageType>(SignalGuageType.guage_threshold_met), SignalBounds.bounds_upper, 0)},
				new SignalGuage[]{new SignalGuage(new MutableEnum<SignalGuageType>(SignalGuageType.guage_threshold_met), SignalBounds.bounds_lower, 0)}
			);
		}
	}
	
	public static class IndicatorParametersForCCI extends IndicatorParameters {
		public IndicatorParametersForCCI() {super(new MutableInteger(30), 1);}
	}
	
	public static class SignalParametersForMACD extends SignalParameters {
		public SignalParametersForMACD() {
			super(new NormalizeInterface(){@Override public double normalize(double input){return (input * 800);}}, new MutableInteger(3),
			new SignalGuage[]{new SignalGuage(new MutableEnum<SignalGuageType>(SignalGuageType.guage_threshold_met), SignalBounds.bounds_lower, -10)},
			new SignalGuage[]{new SignalGuage(new MutableEnum<SignalGuageType>(SignalGuageType.guage_threshold_met), SignalBounds.bounds_upper, 20)},
			new SignalGuage[]{new SignalGuage(new MutableEnum<SignalGuageType>(SignalGuageType.guage_threshold_met), SignalBounds.bounds_lower, -100)},
			new SignalGuage[]{new SignalGuage(new MutableEnum<SignalGuageType>(SignalGuageType.guage_threshold_met), SignalBounds.bounds_upper, -100)});
		}
	}
	
	public static class IndicatorParametersForMACD extends IndicatorParameters {
		public IndicatorParametersForMACD() {super(new MutableInteger(30), 1);}
	}
	
	public static class SignalParametersForRSI extends SignalParameters {
		public SignalParametersForRSI(){
			super(new NormalizeInterface(){@Override public double normalize(double input) {return (input / 2) - 22;}}, new MutableInteger(3),
			new SignalGuage[]{new SignalGuage(new MutableEnum<SignalGuageType>(SignalGuageType.guage_threshold_left), SignalBounds.bounds_lower, -15)},
			new SignalGuage[]{new SignalGuage(new MutableEnum<SignalGuageType>(SignalGuageType.guage_threshold_left), SignalBounds.bounds_upper, 30)},
			new SignalGuage[]{new SignalGuage(new MutableEnum<SignalGuageType>(SignalGuageType.guage_threshold_left), SignalBounds.bounds_upper, -15)},
			new SignalGuage[]{new SignalGuage(new MutableEnum<SignalGuageType>(SignalGuageType.guage_threshold_left), SignalBounds.bounds_lower, 30)});
		}
	}
	
	public static class IndicatorParametersForRSI extends IndicatorParameters {
		public IndicatorParametersForRSI() {super(new MutableInteger(30), 1);}
	}
	
	public static class SignalParametersForTRIX extends SignalParameters {
		public SignalParametersForTRIX() {
			super(new NormalizeInterface(){@Override public double normalize(double input) {return (input * 300);}}, new MutableInteger(1),
			new SignalGuage[]{new SignalGuage(new MutableEnum<SignalGuageType>(SignalGuageType.guage_threshold_met), SignalBounds.bounds_lower, 48)},
			new SignalGuage[]{new SignalGuage(new MutableEnum<SignalGuageType>(SignalGuageType.guage_threshold_met), SignalBounds.bounds_upper, -44)},
			new SignalGuage[]{new SignalGuage(new MutableEnum<SignalGuageType>(SignalGuageType.guage_threshold_met), SignalBounds.bounds_lower, -100)},
			new SignalGuage[]{new SignalGuage(new MutableEnum<SignalGuageType>(SignalGuageType.guage_threshold_met), SignalBounds.bounds_upper, -100)});
		}
	}
	
	public static class IndicatorParametersForTRIX extends IndicatorParameters {
		public IndicatorParametersForTRIX() {super(new MutableInteger(30), 1);}
	}
	
	public static class SignalParametersForROC extends SignalParameters {
		public SignalParametersForROC() {
			super(new NormalizeInterface(){@Override public double normalize(double input) {return ((input / 1.2) * 30);}}, new MutableInteger(5),
			new SignalGuage[]{new SignalGuage(new MutableEnum<SignalGuageType>(SignalGuageType.guage_threshold_met), SignalBounds.bounds_lower, 11)},
			new SignalGuage[]{new SignalGuage(new MutableEnum<SignalGuageType>(SignalGuageType.guage_threshold_met), SignalBounds.bounds_upper, -18)},
			new SignalGuage[]{new SignalGuage(new MutableEnum<SignalGuageType>(SignalGuageType.guage_threshold_met), SignalBounds.bounds_lower, -100)},
			new SignalGuage[]{new SignalGuage(new MutableEnum<SignalGuageType>(SignalGuageType.guage_threshold_met), SignalBounds.bounds_upper, -100)});
		}
	}
	
	public static class IndicatorParametersForROC extends IndicatorParameters {
		public IndicatorParametersForROC() {super(new MutableInteger(30), 1);}
	}
	
	public static class SignalParametersForMFI extends SignalParameters {
		public SignalParametersForMFI() {
			super(new NormalizeInterface(){@Override public double normalize(double input) {return (input * 1.0) - 50;}}, new MutableInteger(1),
			new SignalGuage[]{new SignalGuage(new MutableEnum<SignalGuageType>(SignalGuageType.guage_threshold_met), SignalBounds.bounds_lower, 20)},
			new SignalGuage[]{new SignalGuage(new MutableEnum<SignalGuageType>(SignalGuageType.guage_threshold_met), SignalBounds.bounds_upper, -15)},
			new SignalGuage[]{new SignalGuage(new MutableEnum<SignalGuageType>(SignalGuageType.guage_threshold_met), SignalBounds.bounds_lower, -100)},
			new SignalGuage[]{new SignalGuage(new MutableEnum<SignalGuageType>(SignalGuageType.guage_threshold_met), SignalBounds.bounds_upper, -100)});
		}
	}
	
	public static class IndicatorParametersForMFI extends IndicatorParameters {
		public IndicatorParametersForMFI() {super(new MutableInteger(30), 1);}
	}
	
	public static class SignalParametersForWILLR extends SignalParameters {
		public SignalParametersForWILLR() {
			super(new NormalizeInterface(){@Override public double normalize(double input) {return ((input / 2) + 10);}}, new MutableInteger(3),
			new SignalGuage[]{new SignalGuage(new MutableEnum<SignalGuageType>(SignalGuageType.guage_threshold_left), SignalBounds.bounds_lower, -30)},
			new SignalGuage[]{new SignalGuage(new MutableEnum<SignalGuageType>(SignalGuageType.guage_threshold_met), SignalBounds.bounds_upper, 28)},
			new SignalGuage[]{new SignalGuage(new MutableEnum<SignalGuageType>(SignalGuageType.guage_threshold_met), SignalBounds.bounds_upper, -100)},
			new SignalGuage[]{new SignalGuage(new MutableEnum<SignalGuageType>(SignalGuageType.guage_threshold_met), SignalBounds.bounds_lower, -100)});
		}
	}
	
	public static class IndicatorParametersForWILLR extends IndicatorParameters {
		public IndicatorParametersForWILLR() {super(new MutableInteger(30), 1);}
	}
	
	
	public static class SignalParametersForSTORSI extends SignalParameters {
		public SignalParametersForSTORSI() {
			super(new NormalizeInterface(){@Override public double normalize(double input) {return (input);}}, new MutableInteger(3),
			new SignalGuage[]{new SignalGuage(new MutableEnum<SignalGuageType>(SignalGuageType.guage_threshold_met), SignalBounds.bounds_lower, 48)},
			new SignalGuage[]{new SignalGuage(new MutableEnum<SignalGuageType>(SignalGuageType.guage_threshold_met), SignalBounds.bounds_upper, -44)},
			new SignalGuage[]{new SignalGuage(new MutableEnum<SignalGuageType>(SignalGuageType.guage_threshold_met), SignalBounds.bounds_lower, -100)},
			new SignalGuage[]{new SignalGuage(new MutableEnum<SignalGuageType>(SignalGuageType.guage_threshold_met), SignalBounds.bounds_upper, -100)});
		}
	}
	
	public static class IndicatorParametersForSTORSI extends IndicatorParameters {
		public IndicatorParametersForSTORSI() {super(new MutableInteger(30), 1);}
	}
	
	public static class SignalParametersForUO extends SignalParameters {
		public SignalParametersForUO() {
			super(new NormalizeInterface(){@Override public double normalize(double input) {return ((double)input / 3 - 40);}}, new MutableInteger(1),
				new SignalGuage[]{new SignalGuage(new MutableEnum<SignalGuageType>(SignalGuageType.guage_threshold_met), SignalBounds.bounds_lower, 0)},
				new SignalGuage[]{new SignalGuage(new MutableEnum<SignalGuageType>(SignalGuageType.guage_threshold_met), SignalBounds.bounds_upper, 0)},				  
				new SignalGuage[]{new SignalGuage(new MutableEnum<SignalGuageType>(SignalGuageType.guage_threshold_met), SignalBounds.bounds_upper, 0)},
				new SignalGuage[]{new SignalGuage(new MutableEnum<SignalGuageType>(SignalGuageType.guage_threshold_met), SignalBounds.bounds_lower, 0)}
			);
		}
		
		public double preComputedMiddle = 0;
	}
	
	public static class IndicatorParametersForUO extends IndicatorParameters {
		public IndicatorParametersForUO() {super(new MutableInteger(30), 1);}
	}
	
	public static class SignalParametersForARUp extends SignalParameters {
		public SignalParametersForARUp(){
			super(new NormalizeInterface(){@Override public double normalize(double input) {return (MathTools.pow(input - 50, 0.85));}}, new MutableInteger(3),
			new SignalGuage[]{new SignalGuage(new MutableEnum<SignalGuageType>(SignalGuageType.guage_threshold_met), SignalBounds.bounds_lower, 48)},
			new SignalGuage[]{new SignalGuage(new MutableEnum<SignalGuageType>(SignalGuageType.guage_threshold_met), SignalBounds.bounds_upper, -44)},
			new SignalGuage[]{new SignalGuage(new MutableEnum<SignalGuageType>(SignalGuageType.guage_threshold_met), SignalBounds.bounds_lower, -100)},
			new SignalGuage[]{new SignalGuage(new MutableEnum<SignalGuageType>(SignalGuageType.guage_threshold_met), SignalBounds.bounds_upper, -100)});
		}
	}
	
	public static class IndicatorParametersForARUp extends IndicatorParameters {
		public IndicatorParametersForARUp() {super(new MutableInteger(30), 1);}
	}
	
	public static class SignalParametersForARDown extends SignalParameters {
		public SignalParametersForARDown(){
			super(new NormalizeInterface(){@Override public double normalize(double input) {return (MathTools.pow(input - 50, 0.85));}}, new MutableInteger(3),
			new SignalGuage[]{new SignalGuage(new MutableEnum<SignalGuageType>(SignalGuageType.guage_threshold_met), SignalBounds.bounds_lower, 48)},
			new SignalGuage[]{new SignalGuage(new MutableEnum<SignalGuageType>(SignalGuageType.guage_threshold_met), SignalBounds.bounds_upper, -44)},
			new SignalGuage[]{new SignalGuage(new MutableEnum<SignalGuageType>(SignalGuageType.guage_threshold_met), SignalBounds.bounds_lower, -100)},
			new SignalGuage[]{new SignalGuage(new MutableEnum<SignalGuageType>(SignalGuageType.guage_threshold_met), SignalBounds.bounds_upper, -100)});
		}
	}
	
	public static class IndicatorParametersForARDown extends IndicatorParameters {
		public IndicatorParametersForARDown() {super(new MutableInteger(30), 1);}
	}
		
	public static class SignalParametersForSAR extends SignalParameters {
		public SignalParametersForSAR(){
			super(new NormalizeInterface(){@Override public double normalize(double input) {return input;}}, new MutableInteger(1),
			new SignalGuage[]{new SignalGuage(new MutableEnum<SignalGuageType>(SignalGuageType.guage_threshold_met), SignalBounds.bounds_lower, -100)},
			new SignalGuage[]{new SignalGuage(new MutableEnum<SignalGuageType>(SignalGuageType.guage_threshold_met), SignalBounds.bounds_upper, -100)},
			new SignalGuage[]{new SignalGuage(new MutableEnum<SignalGuageType>(SignalGuageType.guage_threshold_met), SignalBounds.bounds_lower, -100)},
			new SignalGuage[]{new SignalGuage(new MutableEnum<SignalGuageType>(SignalGuageType.guage_threshold_met), SignalBounds.bounds_upper, -100)});
		}
	}
	
	public static class IndicatorParametersForSAR extends IndicatorParameters {
		public IndicatorParametersForSAR() {super(new MutableInteger(30), 1);}
	}
	
	public static class SignalParametersForEMAFirst extends SignalParameters {
		public SignalParametersForEMAFirst(){
			super(new NormalizeInterface(){@Override public double normalize(double input) {return input;}}, new MutableInteger(1),
			new SignalGuage[]{new SignalGuage(new MutableEnum<SignalGuageType>(SignalGuageType.guage_threshold_met), SignalBounds.bounds_lower, -100)},
			new SignalGuage[]{new SignalGuage(new MutableEnum<SignalGuageType>(SignalGuageType.guage_threshold_met), SignalBounds.bounds_upper, -100)},
			new SignalGuage[]{new SignalGuage(new MutableEnum<SignalGuageType>(SignalGuageType.guage_threshold_met), SignalBounds.bounds_lower, -100)},
			new SignalGuage[]{new SignalGuage(new MutableEnum<SignalGuageType>(SignalGuageType.guage_threshold_met), SignalBounds.bounds_upper, -100)});
		}
	}
	
	public static class SignalParametersForCrossover extends SignalParameters {
		public SignalParametersForCrossover(){
			super(new NormalizeInterface(){@Override public double normalize(double input) {return input * 250;}}, null, 
				new SignalGuage[]{new SignalGuage(new MutableEnum<SignalGuageType>(SignalGuageType.guage_threshold_met), SignalBounds.bounds_lower, 0)},
				new SignalGuage[]{new SignalGuage(new MutableEnum<SignalGuageType>(SignalGuageType.guage_threshold_met), SignalBounds.bounds_upper, 0)},
				new SignalGuage[]{new SignalGuage(new MutableEnum<SignalGuageType>(SignalGuageType.guage_threshold_met), SignalBounds.bounds_lower, 0)},
				new SignalGuage[]{new SignalGuage(new MutableEnum<SignalGuageType>(SignalGuageType.guage_threshold_met), SignalBounds.bounds_upper, 0)});
		}
	}
	
	public static class SignalParametersForEncog extends SignalParameters {
		public SignalParametersForEncog(){
			super(null, null, null, null, null, null);
		}
	}
	
	public static class SignalParametersForCandlestickGroup extends SignalParameters {
		public SignalParametersForCandlestickGroup(){
			super(null, null, null, null, null, null);
		}
	}
	
	public static class IndicatorParametersForBasic extends IndicatorParameters {
		public IndicatorParametersForBasic() {super(new MutableInteger(30), 1);}
	}
}
