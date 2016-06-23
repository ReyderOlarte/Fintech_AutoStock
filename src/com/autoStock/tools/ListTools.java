/**
 * 
 */
package com.autoStock.tools;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.encog.ml.data.MLData;

import com.autoStock.signal.extras.EncogFrame;

/**
 * @author Kevin Kowalewski
 *
 */
public class ListTools {
	public static void removeDuplicates(ArrayList<?> arrayList){
		Set setOfObject = new LinkedHashSet(arrayList);
		arrayList.clear();
		arrayList.addAll(setOfObject);
	}
	
	public static ArrayList<Object> mergeLists(ArrayList<Object>... listOfArrayLists){
		ArrayList<Object> listOfObject = new ArrayList<Object>();
		
		for (ArrayList<Object> list : listOfArrayLists){
			listOfObject.addAll(list);
		}
		
		return listOfObject;
	}
	
	public static <T> ArrayList<T> reverseList(ArrayList<T> listOfObject){
		ArrayList<T> listOfReturnObject = new ArrayList<T>();
		
		for (int i=listOfObject.size()-1; i>=0; i--){
			listOfReturnObject.add(listOfObject.get(i));
		}
		
		return listOfReturnObject;
	}
	
	public static ArrayList<String> getArrayListFromString(String input, String separator){
		return new ArrayList<String>(Arrays.asList(input.split(separator)));
	}

	public static ArrayList<Integer> getListFromArray(int[] arrayOfInt) {
		ArrayList<Integer> listOfInteger = new ArrayList<Integer>();
		for (int integer : arrayOfInt){
			listOfInteger.add(new Integer(integer));	
		}
		
		return listOfInteger;
	}

	public static ArrayList<Double> getListFromArray(double[] arrayOfDouble) {
		ArrayList<Double> listOfDouble = new ArrayList<Double>();
		for (double value : arrayOfDouble){
			listOfDouble.add(new Double(value));
		}

		return listOfDouble;
	}

	public static <T> ArrayList<T> getList(List<T> asList) {
		return new ArrayList<T>(asList);
	}

	public static <T> ArrayList<T> combineLists(ArrayList<T>... arrayOfLists) {
		ArrayList<T> list = new ArrayList<T>();
		for (ArrayList<T> listIn : arrayOfLists){
			list.addAll(listIn);
		}
		return list;
	}
	
	public static <T> ArrayList<T> subList(List<T> list, int start, int end){
		if (end == -1){end = list.size();}
		return new ArrayList<T>(list.subList(start, end));
	}

	public static <T> ArrayList<T> getLast(List<T> list, int elements) {
		return subList(list, list.size() - elements, -1);
	}
	
	public static <T> T getLast(List<T> list){
		if (list.size() == 0 || list == null){return null;}
		return list.get(list.size()-1);
	}

	public static <T> T getFirst(List<T> list){
		if (list.size() == 0 || list == null){return null;}
		return list.get(0);
	}
}
