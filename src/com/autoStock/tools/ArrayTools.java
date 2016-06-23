/**
 * 
 */
package com.autoStock.tools;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

/**
 * @author Kevin Kowalewski
 * 
 */
public class ArrayTools {
	public static float[] shiftArrayDown(float[] array, int shift) {
		float[] arrayOfFloat = new float[array.length];

		for (int i = shift; i < array.length; i++) {
			arrayOfFloat[i] = array[i - shift];
		}

		return arrayOfFloat;
	}

	public static double[] shiftArrayDown(double[] array, int shift) {
		double[] arrayOfdouble = new double[array.length];

		for (int i = shift; i < array.length; i++) {
			arrayOfdouble[i] = array[i - shift];
		}

		return arrayOfdouble;
	}

	public static double[] subArray(double[] array, int start, int end) {
		double[] arrayOfDouble = new double[end - start];

		for (int i = start; i < end; i++) {
			arrayOfDouble[i - start] = array[i];
		}

		return arrayOfDouble;
	}
	
	public static int[] subArray(int[] array, int start, int end) {
		int[] arrayOfInt = new int[end - start];

		for (int i = start; i < end; i++) {
			arrayOfInt[i - start] = array[i];
		}

		return arrayOfInt;
	}

	public static int[] getArrayFromListOfInt(List<Integer> integers) {
//		Co.println("--> Size is: " + integers.size());
		int[] ret = new int[integers.size()];
		for (int i = 0; i < ret.length; i++) {
			ret[i] = integers.get(i).intValue();
		}
		return ret;
	}
	
	public static double[] getArrayFromListOfDouble(List<Double> doubles) {
		double[] ret = new double[doubles.size()];
		for (int i = 0; i < ret.length; i++) {
			ret[i] = doubles.get(i).doubleValue();
		}
		return ret;
	}
	
	public static double[] convertToDouble(int[] arrayOfInt) {
		double[] ret = new double[arrayOfInt.length];
		for (int i = 0; i < ret.length; i++) {
			ret[i] = Double.valueOf(arrayOfInt[i]);
		}
		return ret;
	}
	
	public static int[] convertToInt(double[] arrayOfDouble){
		int[] ret = new int[arrayOfDouble.length];
		for (int i=0; i < ret.length; i++) {
			ret[i] = (int) arrayOfDouble[i];
		}
		
		return ret;
	}
	
	public static Date[] getArrayFromListOfDates(ArrayList<Date> dates){
		Date[] ret = new Date[dates.size()];
		for (int i = 0; i < ret.length; i++) {
			ret[i] = dates.get(i);
		}
		return ret;
	}
	
	@SuppressWarnings("unchecked")
	public static void sort2DStringArray(String[][] arrayOfString){
		Arrays.sort(arrayOfString, new Comparator() {
		    public int compare(Object o1, Object o2) {
		        String[] elt1 = (String[])o1;
		        String[] elt2 = (String[])o2;
		        return elt1[0].compareTo(elt2[0]);
		    }
		});
	}
	
	public static <T> T getLast(T[] array){
		return array[array.length-1];
	}
	
	public static double getLast(double[] array){
		return array[array.length-1];
	}
	
	public static double[] getDoubleArray(List<Double> listOfDouble){
		double[] arrayOfDouble = new double[listOfDouble.size()];
		
		for (int i=0; i<listOfDouble.size(); i++){
			arrayOfDouble[i] = listOfDouble.get(i);
		}
		
		return arrayOfDouble;
	}
	
	public static int getIndex(int[] arrayOfInt, int search){
		for (int i=0; i<arrayOfInt.length; i++){
			if (arrayOfInt[i] == search){
				return i;
			}
		}
		
		return -1;
	}
	
	public static int getLastIndex(int[] arrayOfInt, int search){
		int index = -1;
		for (int i=0; i<arrayOfInt.length; i++){
			if (arrayOfInt[i] == search){
				index = i;
			}
		}
		
		return index;
	}
	
	public static int[] randomizeArrayNoDuplicates(int[] arrayOfInt){
		int[] arrayOfResult = new int[arrayOfInt.length];
		boolean[] arrayOfTaken = new boolean[arrayOfInt.length];
		
		int candidateIndex;
		
		for (int i=0; i<arrayOfInt.length; i++){
			do {
				candidateIndex = (int) (Math.random() * arrayOfInt.length);
			}while (arrayOfTaken[candidateIndex]);
			
			arrayOfResult[i] = arrayOfInt[candidateIndex];
			arrayOfTaken[candidateIndex] = true;
		}
		
		return arrayOfResult;
	}
	
	public static int[] randomizeArrayAllowDuplicates(int[] arrayOfInt){
		int[] arrayOfResult = new int[arrayOfInt.length];
		
		for (int i=0; i<arrayOfInt.length; i++){
			arrayOfResult[i] = arrayOfInt[(int) (Math.random() * arrayOfInt.length)];
		}
		
		return arrayOfResult;
	}
	
	public static int getRandomElement(int[] arrayOfInt){
		return arrayOfInt[(int) (Math.random() * arrayOfInt.length)];
	}
	
	public static int[] generateArray(int from, int to){
		int[] arrayOfInt = new int[Math.abs(to-from + 1)];
		
		int count=0;
		
		for (int i=from; i<=to; i++){
			if (i <= 0){
				arrayOfInt[count] = from - i;				
			}else{
				arrayOfInt[count] = i;
			}
			
			count++;
		}
		
		return arrayOfInt;
	}
	
	public static double[][] chunkArray(double[] arrayOfDouble, int chunkLength){
		int chunks = arrayOfDouble.length/chunkLength;
		double[][] returnArray = new double[chunks][chunkLength];
		
		if (arrayOfDouble.length % chunkLength != 0){
			throw new IllegalArgumentException("Chunk, array length need to have a remainder of zero");
		}
		
		for (int c=0; c<chunks; c++){
			for (int i=0; i<chunkLength; i++){
				returnArray[c][i] = arrayOfDouble[i+(c*chunkLength)];
			}
		}
		
		return returnArray;
	}
	
	public static double[][] toMatrix(double[]... arrays){
		double[][] returnArray = new double[arrays[0].length][arrays.length];
		int max = 0;
		
		for (int i=0; i<arrays.length; i++){
			max = Math.max(arrays[i].length, max);
		}
		
		for (int i=0; i<max; i++){
			for (int c=0; c<arrays.length; c++){
				returnArray[i][c] = arrays[c][i];
			}
		}
		
		return returnArray;
	}
	
	public static <T> T[] concat(T[] first, T[] second) {
		T[] result = Arrays.copyOf(first, first.length + second.length);
		System.arraycopy(second, 0, result, first.length, second.length);
		return result;
	}
	
	public static <T> T[] concatAll(T[] first, T[]... rest) {
		int totalLength = first.length;
		
		for (T[] array : rest) {
			totalLength += array.length;
		}
		
		T[] result = Arrays.copyOf(first, totalLength);
		
		int offset = first.length;
		
		for (T[] array : rest) {
			System.arraycopy(array, 0, result, offset, array.length);
			offset += array.length;
		}
		
		return result;
	}
	
	public static <T> T[] removeElement(T[] array, int element){
		ArrayList<T> list = new ArrayList<T>(Arrays.asList(array));
		if (array.length == 0){return (T[]) Array.newInstance(array[0].getClass(), 0);}
		list.remove(element);
		return list.toArray((T[]) Array.newInstance(array[0].getClass(), 0));
	}
	
	public static double[] removeElement(double[] array, int element){
		double[] n = new double[array.length - 1];
	    System.arraycopy(array, 0, n, 0, element );
	    System.arraycopy(array, element+1, n, element, array.length - element-1);
	    return n;
	}
	
	public static int[] removeElement(int[] array, int element){
		int[] n = new int[array.length - 1];
	    System.arraycopy(array, 0, n, 0, element );
	    System.arraycopy(array, element+1, n, element, array.length - element-1);
	    return n;
	}
}