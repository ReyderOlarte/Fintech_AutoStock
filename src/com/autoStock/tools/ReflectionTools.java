/**
 * 
 */
package com.autoStock.tools;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author Kevin Kowalewski
 * 
 */
public class ReflectionTools {
	public ArrayList<String> getValuesToStringArryay(Object object) {
		try {
			ArrayList<String> listOfString = new ArrayList<String>();
			for (Field field : object.getClass().getFields()) {
				Object fieldObject = field.get(object);
				
				if (fieldObject != null){
					if (field.getType() == String.class){
						listOfString.add(field.getName() + " = " + (String) fieldObject);
					}else if (field.getType() == long.class || field.getType() == int.class || field.getType() == float.class || field.getType() == double.class){
						listOfString.add(field.getName() + " = " + (String) String.valueOf(fieldObject));
					}else if (field.getType() == Date.class){
						listOfString.add(field.getName() + " = " + DateTools.getPretty((Date)fieldObject));
					}else {
						listOfString.add(fieldObject.toString());
						//throw new UnsatisfiedLinkError("Can't handle type: " + field.getType().getSimpleName());
					}
				}else{
					listOfString.add(field.getName() + " = null");
				}
			}

			return listOfString;
		} catch (Exception e) {
			e.printStackTrace();
			throw new IllegalStateException();
		}
	}
//	
//	private List<Double> getValues(Object object, String... fields){
//		
//	}
}
