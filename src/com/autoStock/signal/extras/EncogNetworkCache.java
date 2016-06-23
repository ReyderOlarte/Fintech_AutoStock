/**
 * 
 */
package com.autoStock.signal.extras;

import java.util.ArrayList;
import java.util.HashMap;

import org.encog.neural.networks.BasicNetwork;

import com.google.gson.internal.Pair;
import com.rits.cloning.Cloner;

/**
 * @author Kevin
 *
 */
public class EncogNetworkCache {
	private HashMap<String, Object> networks = new HashMap<String, Object>();
	private static EncogNetworkCache instance = new EncogNetworkCache();

	public static EncogNetworkCache getInstance() {
		return instance;
	}
	
	public boolean contains(String key){
		return networks.containsKey(key);
	}
	
	public Object get(String key, boolean asClone){
		if (networks.containsKey(key) && asClone){
			return new Cloner().deepClone(networks.get(key));
		}else{
			return networks.get(key);
		}
	}

	public void put(String key, Object network) {
		networks.put(key, network);
	}

	public void remove(String networkName) {
		networks.remove(networkName);
	}
	
	public void clear() {
		networks.clear();
	}
}
