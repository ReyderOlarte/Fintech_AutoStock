/**
 * 
 */
package com.autoStock.tools;

import java.text.DecimalFormat;
import java.util.ArrayList;

/**
 * @author Kevin Kowalewski
 *
 */
public class StringTools {
	public static String toDecimal(float number){
		return new DecimalFormat("#.##").format(number);
	}
	
	public static String toDecimal(double number){
		return new DecimalFormat("#.##").format(number);
	}
	
	public static String removePrefix(String string, String prefix){
		return string.substring(string.indexOf(prefix)+1);
	}
	
	public static String addPlusToPositiveNumbers(int number){
		if (number > 0){
			return "+" + number;
		}else{
			return String.valueOf(number);
		}
	}
	
	public static String addPlus(double number){
		if (number > 0){
			return "+" + number;
		}else{
			return String.valueOf(number);
		}
	}
	
	public static String addPlusToPositiveNumbers(float number){
		if (number > 0){
			return "+" + number;
		}else{
			return String.valueOf(number);
		}
	}
	
	public static String addPlusToPositiveNumbers(String number){
		float value = Float.valueOf(number);
		if (value > 0){
			return "+" + number;
		}else{
			return String.valueOf(number);
		}
	}
	
	public static String blankZeroValues(String number){
		float value = Float.valueOf(number);
		
		if (value == 0){
			return "";
		}else{
			return number;
		}
	}
	
	public static String arrayOfStringToString(String[] arrayOfString){
		String returnString = new String();
		
		for (String string : arrayOfString){
			returnString = returnString.concat(string);
		}
		
		return returnString;
	}
	
	public static String arrayOfDoubleToString(double[] arrayOfdouble){
		String returnString = new String();
		
		for (Double value : arrayOfdouble){
			returnString += new DecimalFormat("#.########").format(value) + ", ";
		}
		
		return returnString;
	}
	
	public static String listOfStringToString(ArrayList<String> listOfString, boolean addSpace){
		String returnString = new String();
		
		for (String string : listOfString){
			returnString = returnString.concat(addSpace ? " " + string : string);
		}
		
		return returnString;
	}
}
