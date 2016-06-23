/**
 * 
 */
package com.autoStock.persist;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @author Kevin
 *
 */
public class MemoryPersister {
	private HashMap<Class, List> hashOfLists = new HashMap<Class, List>(512);
	
	public void persistInto(Object object){
		List listToInsertInto; 
		
		if (hashOfLists.containsKey(object.getClass())){
			listToInsertInto = hashOfLists.get(object.getClass());
		}else{
			listToInsertInto = new ArrayList<Object>();
			hashOfLists.put(object.getClass(), listToInsertInto);
		}
		
		listToInsertInto.add(object);
	}
	
	public void persistSingle(Object object){
		throw new UnsupportedOperationException("Implement me!");
	}
	
	public void putList(Class clazz, ArrayList<?> list) {
		hashOfLists.put(clazz, list);
	}

	public HashMap<Class, List> getHash() {
		return hashOfLists;
	}

	public void erase() {
		for (Class key : hashOfLists.keySet()){
			hashOfLists.get(key).clear();
		}
	}
	
	public void clear() {
		hashOfLists.clear();
	}

	public int size() {
		return hashOfLists.size();
	}
}