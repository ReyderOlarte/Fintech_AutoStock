/**
 * 
 */
package com.autoStock.signal.extras;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import org.apache.commons.codec.digest.DigestUtils;
import org.encog.neural.neat.NEATNetwork;
import org.encog.neural.neat.PersistNEATPopulation;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.PersistBasicNetwork;

import com.autoStock.Co;
import com.autoStock.tools.Lock;

/**
 * @author Kevin
 *
 */
public class EncogNetworkProvider {
	private static final String TRAINED_NETWORK_PATH = "./trained";
	
	public static enum NetworkType {
		basic,
		neat,
	}

	public boolean saveNetwork(BasicNetwork basicNetwork, String networkName){
		try {
			new PersistBasicNetwork().save(new FileOutputStream(new File(getNetworkPath(NetworkType.basic, networkName))), basicNetwork);
			return true;
		}catch(Exception e){}
		
		return false;
	}
	
	public boolean saveNeatNetwork(NEATNetwork neatNetwork, String networkName){
		try {
			new PersistNEATPopulation().save(new FileOutputStream(new File(getNetworkPath(NetworkType.neat, networkName))), neatNetwork);
			return true;
		}catch(Exception e){}
		
		return false;
	}
	
	public BasicNetwork getBasicNetwork(String networkName){
		try {
			return (BasicNetwork) new PersistBasicNetwork().read(new FileInputStream(new File(getNetworkPath(NetworkType.basic, networkName))));
		}catch(Exception e){} Co.println("--> Network was null while reading");
		
		return null;
	}
	
	public NEATNetwork getNeatNetwork(String networkName){		
		try {
			return (NEATNetwork) new PersistNEATPopulation().read(new FileInputStream(new File(getNetworkPath(NetworkType.neat, networkName))));
		}catch(Exception e){} // Co.println("--> Network was null while reading");
		
		return null;
	}
	
	public String getNetworkPath(NetworkType networkType, String networkName){
		return TRAINED_NETWORK_PATH + "/" + networkName + "-" + networkType.name() + ".ml"; 
	}
}
