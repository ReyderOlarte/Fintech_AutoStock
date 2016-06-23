/**
 * 
 */
package com.autoStock.persist;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.autoStock.cache.DiskCache;
import com.autoStock.internal.GsonProvider;
import com.autoStock.tools.Lock;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
/**
 * @author Kevin
 *
 */
public class GenericPersister {
	private static final int PERSISTER_WRITE_HARD_DELAY = 5 * 60 * 1000;
	private static final int PERSISTER_WRITE_SOFT_DELAY = 10 * 1000;
	
	private static final String PREF_PREFIX_LIST = "gp_list_";
	private static final String PREF_PREFIX_OBJECT = "gp_object_";
	private static final String PREFS_NAME = "persistent_memory";
	private MemoryPersister memoryPersister;
	private Gson gson;
	private DiskCache diskCache = new DiskCache();
	private Lock lock = new Lock();
	
	private static class ListParameterizedType implements ParameterizedType {
	    private Type type;

	    private ListParameterizedType(Type type) {
	        this.type = type;
	    }

	    @Override
	    public Type[] getActualTypeArguments() {
	        return new Type[] {type};
	    }

	    @Override
	    public Type getRawType() {
	        return ArrayList.class;
	    }

	    @Override
	    public Type getOwnerType() {
	        return null;
	    }
	}
	
	public static enum PersistKeys {
		list_strums,
		list_answers,
		list_pipeline_ended,
	}
	
	public GenericPersister() {
		gson = new GsonProvider().getGsonInstance();
		memoryPersister = new MemoryPersister();
	}

	public void persistInto(Object object){
		memoryPersister.persistInto(object);
		syncToDisk();
	}
	
	public void persistSingle(Object object){
		memoryPersister.persistInto(object);
	}
	
	public HashMap<Class, List> getHash(){
		return memoryPersister.getHash();
	}
	
	public void syncFromDisk(){
		synchronized(lock){	
			for (String key : diskCache.keys()){
				if (key.startsWith(PREF_PREFIX_LIST)){
					
					try {
						Class clazz = Class.forName(key.replaceAll(PREF_PREFIX_LIST, ""));
						Type type = new ListParameterizedType(clazz);
						ArrayList<?> list = gson.fromJson(diskCache.readString(key), type);
						
						memoryPersister.putList(clazz, list);
					
						if (list != null && list.size() > 0){
//							Ln.e("--> Type is: " + list.get(0).getClass().getName());
						}
						
					}catch(Exception e){e.printStackTrace();}
					
				}else if (key.startsWith(PREF_PREFIX_OBJECT)){
					throw new IllegalStateException("Not supported");
				}
			}
		}
	}
	
	public void syncToDisk(){
		synchronized(lock){
			HashMap<Class, List> hashMap = memoryPersister.getHash();
		
			for (Class key : hashMap.keySet()){
				Object value = hashMap.get(key);
				if (value instanceof List){
					String serialized = gson.toJson(value, new TypeToken<ArrayList<Object>>(){}.getType());
					diskCache.writeString(PREF_PREFIX_LIST + key.getName(), serialized);
				} else {
					String serialized = gson.toJson(value, new TypeToken<ArrayList<Object>>(){}.getType());
					diskCache.writeString(PREF_PREFIX_LIST + key.getName(), serialized);
				}
			}
		}
	}

	public int getCount(Class clazz) {
		HashMap<Class, List> hashMap = memoryPersister.getHash();
		if (hashMap.containsKey(clazz) == false){
			return 0;
		}else{
			return hashMap.get(clazz).size();
		}
	}

	public void erase() {
		memoryPersister.erase();
	}
	
	public int size(){
		return memoryPersister.size();
	}
}