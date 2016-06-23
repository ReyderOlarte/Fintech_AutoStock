/**
 * 
 */
package com.autoStock.trading.platform.ib.definitions;

/**
 * @author Kevin Kowalewski
 *
 */
public class HistoricalDataDefinitions {
	
	public static int MIN_PERIOD = 30;
	
	public static enum Period{
		year(new Resolution[]{Resolution.day},365*24*60*60),
		month_6(new Resolution[]{Resolution.day},182*24*60*60),
		month_3(new Resolution[]{Resolution.day},90*24*60*60),
		month(new Resolution[]{Resolution.day, Resolution.hour},31*26*60*60),
		week(new Resolution[]{Resolution.day, Resolution.hour, Resolution.min_30, Resolution.min_15},7*26*60*60),
		day(new Resolution[]{Resolution.hour, Resolution.min_30, Resolution.min_15, Resolution.min},24*60*60),
		hour_1(new Resolution[]{Resolution.min_30, Resolution.min_15, Resolution.min, Resolution.sec_30,  Resolution.sec_15, Resolution.sec_5},1*60*60),
		min_30(new Resolution[]{Resolution.min_15, Resolution.min, Resolution.sec_30, Resolution.sec_15, Resolution.sec_5, Resolution.sec},30*60),
		min(new Resolution[]{Resolution.sec_30, Resolution.sec_15, Resolution.sec_5, Resolution.sec},60),
		;
		
		public Resolution[] arrayOfResolution;
		public int seconds;
		
		Period(Resolution[] arrayOfResolution, int duration){
			this.arrayOfResolution = arrayOfResolution;
			this.seconds = duration;
		}
	}
	
	public static enum Resolution {
		day(86400, "1 day"),
		hour(3600, "1 hour"),
		min_30(1800, "30 mins"),
		min_15(900, "15 mins"),
		min_10(600, "10 mins"),
		min_5(300, "5 mins"),
		min(60, "1 min"),
		sec_30(30, "30 secs"),
		sec_15(15, "15 secs"),
		sec_5(5, "5 secs"),
		sec(1, "1 secs"),
		tick(0, "1 ticks"),
		unknown(0, "unknown");
		;
		
		private int seconds;
		public String barSize;
		
		Resolution(int seconds, String barSize){
			this.seconds = seconds;
			this.barSize = barSize;
		}
		
		public int asSeconds(){
			return seconds;
		}
		
		public int asMinutes(){
			return seconds / 60;
		}
		
		public static Resolution fromMinutes(int minutes){
			for (Resolution resolution : Resolution.values()){
				if (resolution.seconds / 60 == minutes){
					return resolution;
				}
			}
			
			return Resolution.unknown;
		}
	}
	
	public static Resolution getBestResolution(long duration){
		Period bestPeriod = Period.year;
		for (Period period : Period.values()){
			if (period.seconds >= duration){
				bestPeriod = period;
			}
		}
		
		return bestPeriod.arrayOfResolution[bestPeriod.arrayOfResolution.length-1];
	}
	
	public static Period getBestPeriod(Resolution resolution){
		for (Period period : Period.values()){
			for (Resolution tempResolution : period.arrayOfResolution){
				if (resolution == tempResolution){
					return period;
				}
			}
		}
		
		return null;
	}
}
