package com.autoStock.tools;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author Kevin Kowalewski
 *
 */
public class TimeToCompletionEstimate {
	public Date date = new Date();
	private BigDecimal currentValue;
	private BigDecimal maximumValue;
	
	public TimeToCompletionEstimate(BigDecimal maxValue){
		this.maximumValue = maxValue;
	}
	
	public TimeToCompletionEstimate(){
		
	}
	
	public void setMaxIndex(BigDecimal maxValue){
		this.maximumValue = maxValue;
	}
	
	public void update(BigDecimal currentValue){
		this.currentValue = currentValue;
	}
	
	public double getETAInMinutes(){
		double secondsPassed = (new Date().getTime() - date.getTime()) / 1000;		
		double unitsPerMinute = (currentValue.longValue() / secondsPassed) * 60;		
		double eta = (maximumValue.doubleValue() - currentValue.doubleValue()) / unitsPerMinute;
		
//		Co.println("--> Has been: " + secondsPassed + " and processed until: " + currentIndex.toPlainString());
//		Co.println("--> Units per minute is: " + unitsPerMinute);
//		Co.println("--> ETA is: " + eta + " minutes");
//		
		return eta;
	}
}
