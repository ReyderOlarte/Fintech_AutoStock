package com.autoStock.tools;

import java.lang.reflect.Field;
import java.util.Comparator;

/**
 * @author Kevin Kowalewski
 *
 */

public class ReflectiveComparator{
	public class FieldComparator implements Comparator<Object> {
		private String fieldName;
		
		public FieldComparator(String fieldName){
			this.fieldName = fieldName;
		}
		
		@SuppressWarnings({ "unchecked", "rawtypes" })
		@Override
		public int compare(Object object1, Object object2) {
			try {
				Field field = object1.getClass().getDeclaredField(fieldName);
				field.setAccessible(true);
				
				Comparable object1FieldValue = (Comparable) field.get(object1);
				Comparable object2FieldValue = (Comparable) field.get(object2);
				
				return object1FieldValue.compareTo(object2FieldValue);
			}catch (Exception e){}
			
			return 0;
		}
	}
	
	public static class ListComparator implements Comparator<Object> {
	    private String fieldName;
	    private SortDirection sortDirection;
	    
	    public static enum SortDirection {
	    	asc,
	    	desc,
	    }

	    public ListComparator(String fieldName, SortDirection sortDirection) {
	        this.fieldName = fieldName;
	        this.sortDirection = sortDirection;
	    }

	    @SuppressWarnings({ "unchecked", "rawtypes" })
		@Override
	    public int compare(Object object1, Object object2) {
	        try {
	            Field field = object1.getClass().getDeclaredField(fieldName);
//	            if (!Comparable.class.isAssignableFrom(field.getType())) {
//	                System.out.println(field.getType());
//	                throw new IllegalStateException("Field not Comparable: " + field);
//	            }
	            field.setAccessible(true);
	            Comparable o1FieldValue = (Comparable) field.get(object1);
	            Comparable o2FieldValue = (Comparable) field.get(object2);
	            
	            if (o1FieldValue == null){ return -1;}
	            if (o2FieldValue == null){ return 1;}
	            
	            if (sortDirection == SortDirection.asc){
		            return o1FieldValue.compareTo(o2FieldValue);    	
	            }else if (sortDirection == SortDirection.desc){
		            return o2FieldValue.compareTo(o1FieldValue);
	            }else{
	            	throw new UnsupportedOperationException();
	            }
	        
	        } catch (NoSuchFieldException e) {
	            throw new IllegalStateException("Field doesn't exist", e);
	        } catch (IllegalAccessException e) {
	            throw new IllegalStateException("Field inaccessible", e);
	        }
	    }
	}
}