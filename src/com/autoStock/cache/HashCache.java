package com.autoStock.cache;

import java.util.HashMap;

/**
 * @author Kevin Kowalewski
 *
 */
public class HashCache {
	private HashMap<String, Object> hashOfValues = new HashMap<String, Object>();

	public boolean containsKey(String key){
		return hashOfValues.containsKey(key);
	}
	
	public void addValue(String key, Object object){
		hashOfValues.put(key, object);
	}
	
	public Object getValue(String key){
		return hashOfValues.get(key);
	}
}
