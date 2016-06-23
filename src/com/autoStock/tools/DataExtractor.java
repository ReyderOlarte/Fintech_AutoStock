/**
 * 
 */
package com.autoStock.tools;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Date;

/**
 * @author Kevin Kowalewski
 *
 */
public class DataExtractor {
	public ArrayList<Date> extractDate(ArrayList<?> listOfObject, String fieldName){
		ArrayList<Date> listOfResults = new ArrayList<Date>();
		for (Object object : listOfObject){
			try {
				Field field = object.getClass().getField(fieldName);
				field.setAccessible(true);
				listOfResults.add((Date)field.get(object));
			}catch(Exception e){}
		}
		
		return listOfResults;
	}
	
	public ArrayList<Float> extractFloat(ArrayList<?> listOfObject, String fieldName){
		ArrayList<Float> listOfResults = new ArrayList<Float>();
		for (Object object : listOfObject){
			try {
				Field field = object.getClass().getField(fieldName);
				field.setAccessible(true);
				listOfResults.add((Float)field.get(object));
			}catch(Exception e){}
		}
		
		return listOfResults;
	}
	
	public ArrayList<Double> extractDouble(ArrayList<?> listOfObject, String fieldName){
		ArrayList<Double> listOfResults = new ArrayList<Double>();
		for (Object object : listOfObject){
			try {
				Field field = object.getClass().getField(fieldName);
				field.setAccessible(true);
				listOfResults.add((Double)field.get(object));
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		
		return listOfResults;
	}
	
	public ArrayList<String> extractString(ArrayList<?> listOfObject, String fieldName){
		ArrayList<String> listOfResults = new ArrayList<String>();
		for (Object object : listOfObject){
			try {
				Field field = object.getClass().getField(fieldName);
				field.setAccessible(true);
				listOfResults.add((String)field.get(object));
			}catch(Exception e){}
		}
		
		return listOfResults;
	}
	
	public ArrayList<Integer> extractInteger(ArrayList<?> listOfObject, String fieldName){
		ArrayList<Integer> listOfResults = new ArrayList<Integer>();
		for (Object object : listOfObject){
			try {
				Field field = object.getClass().getField(fieldName);
				field.setAccessible(true);
				listOfResults.add((Integer)field.get(object));
			}catch(Exception e){}
		}
		
		return listOfResults;
	}
}
