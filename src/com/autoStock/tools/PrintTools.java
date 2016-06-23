/**
 * 
 */
package com.autoStock.tools;

import java.util.List;

/**
 * @author Kevin
 *
 */
public class PrintTools {
	public static String getString(int[] array){
		String string = new String();
		
		for (int i=0; i<array.length; i++){
			if (i == 0){
				string += array[i];
			}else{
				string += ", " + array[i];
			}
		}
		
		return string;
	}
	
	public static String getString(double[] array){
		String string = new String();
		
		for (int i=0; i<array.length; i++){
			if (i == 0){
				string += array[i];
			}else{
				string += ", " + array[i];
			}
		}
		
		return string;
	}
	
	public static String getString(List<?> listOfObject){
		String string = new String();
		
		for (int i=0; i<listOfObject.size(); i++){
			if (i == 0){
				string += listOfObject.get(i).toString();
			}else{
				string += ", " + listOfObject.get(i).toString();
			}
		}
		
		return string;
	}
}
