package com.autoStock.tools;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;

import com.autoStock.tools.ReflectiveComparator.ListComparator.SortDirection;

/**
 * @author Kevin Kowalewski
 *
 */
public class ListComparator {
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public boolean compareTwoLists(ArrayList firstArrayList, ArrayList secondArrayList, String field){
		int largestSize = 0;
		
		if (firstArrayList == null && secondArrayList != null){return true;}
		if (firstArrayList != null && secondArrayList == null){return true;}
		if (firstArrayList == null && secondArrayList == null){return false;}
		
		if (firstArrayList.size() != secondArrayList.size()){
			return true;
		}
		
		if (firstArrayList.size() >= secondArrayList.size()){largestSize = firstArrayList.size();}
		else{largestSize = secondArrayList.size();}
		
		Collections.sort(firstArrayList, new ReflectiveComparator.ListComparator(field, SortDirection.asc));
		Collections.sort(secondArrayList, new ReflectiveComparator.ListComparator(field, SortDirection.asc));
		
		for (int i=0; i<largestSize; i++){
			if (firstArrayList.get(i) == null && secondArrayList.get(i) != null){return true;}
			if (firstArrayList.get(i) != null && secondArrayList.get(i) == null){return true;}
			if (firstArrayList.get(i) != null && secondArrayList.get(i) != null){
				if (new ReflectiveComparator(). new FieldComparator(field).compare(firstArrayList.get(i), secondArrayList.get(i)) != 0){
					return true;
				}
			}
		}
		return false;
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public ArrayList getListDifferences(ArrayList firstArrayList, ArrayList secondArrayList, String fieldName){
		ArrayList<Object> differences =  new ArrayList<Object>();
		
		if (firstArrayList == null && secondArrayList == null){return null;}
		if (firstArrayList == null && secondArrayList != null ){return secondArrayList;}
		if (firstArrayList != null && secondArrayList == null){return firstArrayList;}
		
		Collections.sort(firstArrayList, new ReflectiveComparator.ListComparator(fieldName, SortDirection.asc));
		Collections.sort(secondArrayList, new ReflectiveComparator.ListComparator(fieldName, SortDirection.asc));
		
		for (Object object : firstArrayList){
			Field field = null;			
			Object fieldValue = null;
	        try {field = object.getClass().getDeclaredField(fieldName);}catch(Exception e){}
	        try {fieldValue = field.get(object);}catch(Exception e){}
	        field.setAccessible(true);
	        
	        //try {Log.d(LOG_TAG, "--> ******** Comparing (first): " + fieldValue.toString());}catch(Exception e){Log.d(LOG_TAG, "--> Could not get first list value...");}
	       //try {Log.d(LOG_TAG, "--> ******** Comparing (second): " + getReflectedObject(secondArrayList, fieldName, object).toString());}catch(Exception e){Log.d(LOG_TAG, "--> Could not get second list value");}
	        
	        if (fieldValue.equals(getReflectedObject(secondArrayList, fieldName, fieldValue))){
	        	//Log.d(LOG_TAG, "--> Second list has first list value: " + fieldValue.toString());
	        }else{
	        	//Log.d(LOG_TAG, "--> Second list does not have first list value: " + fieldValue.toString());
	        	differences.add(object);
	        }
		}
		
		return differences;
	}
	
	@SuppressWarnings("rawtypes")
	public Object getReflectedObject(ArrayList arrayList, String fieldName, Object value){
		for (Object object : arrayList){
	        Field field = null;
	        try {field = object.getClass().getDeclaredField(fieldName);}catch(Exception e){}
	        field.setAccessible(true);
	        	        	        
	        try {
	        	if (field.get(object).equals(value)){
	        		return field.get(object);
	        	}
	        }catch(Exception e){}
		}
		
		return null;
	}
}
