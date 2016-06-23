/**
 * 
 */
package com.autoStock.chart;

import java.util.ArrayList;
import java.util.Calendar;

import org.jfree.data.time.Minute;
import org.jfree.data.time.TimeSeries;

import com.autoStock.types.basic.BasicTimeValuePair;

/**
 * @author Kevin Kowalewski
 *
 */
public class ChartDataFiller {
	
	public TimeSeries getTimeSeries(String label, ArrayList<BasicTimeValuePair> listOfBasicTimeValuePair){
		return getTimeSeries(label, null, listOfBasicTimeValuePair);
	}
	
	public TimeSeries getTimeSeries(String label, Enum enumForDescription, ArrayList<BasicTimeValuePair> listOfBasicTimeValuePair){
		TimeSeries timeSeries = new TimeSeries(label);
		if (enumForDescription != null){
			timeSeries.setDescription(enumForDescription.name());
		}
		
		for (BasicTimeValuePair basicTimeValuePair : listOfBasicTimeValuePair){
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(basicTimeValuePair.date);
			if (Double.valueOf(basicTimeValuePair.value) != Double.MIN_VALUE){
				timeSeries.add(new Minute(calendar.get(Calendar.MINUTE), calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.DAY_OF_MONTH), calendar.get(Calendar.MONTH)+1, calendar.get(Calendar.YEAR)), Double.valueOf(basicTimeValuePair.value));
			}else{
				timeSeries.add(new Minute(calendar.get(Calendar.MINUTE), calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.DAY_OF_MONTH), calendar.get(Calendar.MONTH)+1, calendar.get(Calendar.YEAR)), null);
			}
		}
		
		return timeSeries;
	}
}
