package com.autoStock.replay;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import com.autoStock.Co;
import com.autoStock.algorithm.core.AlgorithmInfoManager.AlgorithmInfo;
import com.autoStock.types.Exchange;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

/**
 * @author Kevin Kowalewski
 *
 */
public class ReplayController {
	private static File file = new File("./replay.data");
	
	public static void writeToFile(ArrayList<AlgorithmInfo> listOfAlgorithmInfo){
		try {
			if (file.exists()){file.delete();}
			FileOutputStream fileOutputStream = new FileOutputStream(file);
			byte[] arrayOfBytes = new Gson().toJson(listOfAlgorithmInfo).getBytes();
			fileOutputStream.write(arrayOfBytes);
		}catch(Exception e){e.printStackTrace();}
		
		Co.println("--> Written OK!");
	}
	 
	public static void buildFromTextFile(Exchange exchange, String fileLocation){
		
		FileInputStream fileInputStream;
		DataInputStream dataInputStream;
		BufferedReader bufferedReader;

		String stringForFileLine;
		String stringForGson = new String();
		
		try {
			fileInputStream = new FileInputStream(fileLocation);
			dataInputStream = new DataInputStream(fileInputStream);
			bufferedReader = new BufferedReader(new InputStreamReader(dataInputStream));
		}catch(Exception e){return;}
		
		try {
			while ((stringForFileLine = bufferedReader.readLine()) != null){
				stringForGson += stringForFileLine;
			}
		}catch(Exception e){}
		

		ArrayList<AlgorithmInfo> listOfAlgorithmInfo = new Gson().fromJson(stringForGson, new TypeToken<ArrayList<AlgorithmInfo>>() {}.getType());
		
//		buildFromArrayList(exchange, listOfAlgorithmInfo);
		
	}
	
//	public static void buildFromArrayList(Exchange exchange, ArrayList<AlgorithmInfo> listOfAlgorithmInfo){
//		Co.println("--> ArrayList size: " + listOfAlgorithmInfo.size());
//		
//		for (AlgorithmInfo algorithmInfo : listOfAlgorithmInfo){
//			if (algorithmInfo.dateDeactivated != null){
//				Co.println("insert into replay(exchange,symbol, dateTimeActivated, dateTimeDeactivate) values("
//					+ exchange.exchangeName 
//					+ algorithmInfo.symbol 
//					+ DateTools.getPrettyDate(algorithmInfo.dateActivated)
//					+ DateTools.getPrettyDate(algorithmInfo.dateDeactivated)); 
//			}
//		}
//	}
}
