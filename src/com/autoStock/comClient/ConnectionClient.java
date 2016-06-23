/**
 * 
 */
package com.autoStock.comClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;

import com.autoStock.Co;
import com.autoStock.com.CommandHolder;
import com.autoStock.comServer.CommunicationDefinitions.Command;
import com.autoStock.comServer.ConnectionServer;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

/**
 * @author Kevin Kowalewski
 *
 */
public class ConnectionClient {
	public static final String EndCommunication = "QUIT";
	public static final String EndCommand = "END";
	private Socket clientSocket;
	private PrintWriter printWriter;
	
	public void startClient(){
	    try {this.clientSocket = new Socket(InetAddress.getByName("127.0.0.1"), 8888);}catch (Exception e){e.printStackTrace();}
	    try {this.printWriter = new PrintWriter(clientSocket.getOutputStream(), false);}catch (Exception e){e.printStackTrace();}
	}
	
	public void stop(){
		try {
			this.printWriter.flush();
		}catch(Exception e){e.printStackTrace();}
		try {this.clientSocket.close();}catch(Exception e){e.printStackTrace();}
	}
	
	public void sendSerializedCommand(Command command){
		String string = new Gson().toJson(new CommandHolder(command), new TypeToken<CommandHolder>(){}.getType());
		this.printWriter.println(string);
		this.printWriter.println(ConnectionServer.EndCommand);
		this.printWriter.flush();
	}
	
	public void sendSerializedCommand(Command command, Object... params){
		String string = new Gson().toJson(new CommandHolder(command), new TypeToken<CommandHolder>(){}.getType());
		this.printWriter.println(string);
		this.printWriter.println(ConnectionServer.EndCommand);
		this.printWriter.flush();
	}
	
	public void listenForResponse(){
		BufferedReader in = null;
		String receivedString = new String();
		String receivedLine = new String();

		try {
			in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
			
			while (true) {
				try {
					receivedLine = in.readLine();
				} catch (IOException e) {
					e.printStackTrace();
				}
				// System.out.println("Got line: " + receivedLine);
				if (receivedLine == null) {
					break;
				}
				if (receivedLine.trim().equals(EndCommunication)) {
					Co.println("End communication!");
					return;
				} else if (receivedLine.trim().equals(EndCommand)) {
					Co.println("End command!");
					receivedString = new String();
				} else {
					receivedString = receivedString.concat(receivedLine);
				}
			}
		}catch(Exception e){}	
	}
}
