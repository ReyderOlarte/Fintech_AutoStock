package com.autoStock.cache;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.Writer;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import com.google.common.base.Charsets;
import com.google.common.io.Files;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * @author Kevin Kowalewski
 *
 */
public class DiskCache {
	private final String CACHE_ROOT = "./cache/";
	private final File fileForCacheRoot = new File(CACHE_ROOT);
//	private final File fileForCacheDir;
	private Gson gson = new GsonBuilder().setPrettyPrinting().create();
	private String path;
	
	public DiskCache(){
//		fileForCacheDir = new File(CACHE_ROOT + "/" + path + "/");
		if (fileForCacheRoot.exists() == false){fileForCacheRoot.mkdir();}
//		if (fileForCacheDir.exists() == false){fileForCacheRoot.mkdir();}
	}

	public boolean containsKey(String key) {
		if (new File(CACHE_ROOT + key + ".cache").exists()){
			return true;
		}
		return false;
	}
	
	public List<String> keys(){
		ArrayList<String> listOfKeys = new ArrayList<String>();
		for (File file : new File(CACHE_ROOT).listFiles()){
			listOfKeys.add(file.getName().replaceAll(".cache", ""));
		}
		
		return listOfKeys;
	}

	public ArrayList<?> readList(String key, Type classForGson) {
		try {
			BufferedReader bufferedReader = new BufferedReader(new FileReader(CACHE_ROOT + key + ".cache"));
			return gson.fromJson(bufferedReader, classForGson);
		}catch(Exception e){e.printStackTrace();}
		
		return null;
	}

	public void writeList(String key, ArrayList<Object> listOfResults) {
		if (listOfResults == null || listOfResults.size() == 0){return;}
		writeString(key, gson.toJson(listOfResults));
	}
	
	public synchronized void writeString(String key, String value){
		try {
			File file = new File(CACHE_ROOT + key + ".cache");
			Writer output = new BufferedWriter(new FileWriter(file));
			output.write(value);
			output.close();
//			if (file.renameTo(new File(CACHE_ROOT + key + ".cache")) == false){
//				file.delete();
//			}
		}catch(Exception e){e.printStackTrace();}
	}

	public String readString(String key) {
		try {
			return Files.toString(new File(CACHE_ROOT + key + ".cache"), Charsets.UTF_8);
		}catch(Exception e){e.printStackTrace();}
		
		return null;
	}
}
