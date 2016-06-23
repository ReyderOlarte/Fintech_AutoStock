/**
 * 
 */
package com.autoStock.cache;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.autoStock.Co;
import com.autoStock.internal.GsonProvider;
import com.autoStock.persist.MemoryPersister;
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
	
	private static GenericPersister staticInstance = new GenericPersister();
	
	public static GenericPersister getStaticInstance(){
		return staticInstance;
	}
	
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
		syncFromDisk();
	}

	public void persistInto(Object object, boolean noSync){
		memoryPersister.persistInto(object);
		if (noSync == false){syncToDisk();}
	}
	
	public void persistSingle(Object object){
		memoryPersister.persistInto(object);
	}
	
	public HashMap<Class, List> getHash(){
		return memoryPersister.getHash();
	}
	
	public void syncFromDisk(){
		
		Co.println("--> Sync");
		
		synchronized(lock){	
			for (String key : diskCache.keys()){
				if (key.startsWith(PREF_PREFIX_LIST)){
					try {
						Class clazz = Class.forName(key.replaceAll(PREF_PREFIX_LIST, ""));
						Type type = new ListParameterizedType(clazz);
						ArrayList<?> list = gson.fromJson(diskCache.readString(key), type);
//						Co.println("--> Have class: " + clazz.getName());
						
						if (list != null && list.size() > 0){
							memoryPersister.putList(clazz, list);
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
					//Co.println("--> Synced to disk? " + ((List)value).size());
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
	
	public List getList(Class clazz){
		return memoryPersister.getHash().get(clazz);
	}

	public void erase() {
		memoryPersister.erase();
	}
	
	public int size(){
		return memoryPersister.size();
	}
}