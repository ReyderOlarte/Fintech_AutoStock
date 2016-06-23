/**
 * 
 */
package com.autoStock.comServer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.lang.reflect.Type;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;

import com.autoStock.Co;
import com.autoStock.cluster.ComputeUnitForBacktest;
import com.autoStock.com.CommandHolder;
import com.autoStock.com.ListenerOfCommandHolderResult;
import com.autoStock.comServer.CommunicationDefinitions.CommunicationCommand;
import com.autoStock.internal.ApplicationStates;
import com.autoStock.internal.GsonClassAdapter;
import com.autoStock.signal.SignalDefinitions.IndicatorParameters;
import com.autoStock.signal.SignalDefinitions.SignalParameters;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

/**
 * @author Kevin Kowalewski
 *
 */
public class ClusterClient {
	private Socket clientSocket;
	public PrintWriter printWriter;
	private ListenerOfCommandHolderResult listener;
	
	public ClusterClient(ListenerOfCommandHolderResult listener){
		this.listener = listener;
	}
	
	public void startClient(){
		while (clientSocket == null || printWriter == null){
			try {
				clientSocket = new Socket(InetAddress.getByName("192.168.1.150"), 8888);
				printWriter = new PrintWriter(clientSocket.getOutputStream(), true);
			}catch (Exception e){
				Co.println("--> Failed to connect");
			}
			
			try {Thread.sleep(1000);}catch(InterruptedException e){return;}
		}
	    
	    listenForResponse();
	}
	
	public void stop(){
		try {this.printWriter.flush();}catch(Exception e){e.printStackTrace();}
		try {this.clientSocket.close();}catch(Exception e){e.printStackTrace();}
	}
	
	private void listenForResponse(){
		Thread thread = new Thread(new Runnable(){
			@Override
			public void run() {
				BufferedReader in = null;
				String receivedString = new String();
				String receivedLine = new String();
				
				try {
					in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
					
					while (true) {
						try {
							receivedLine = in.readLine();
						} catch (SocketException socketException){
							Co.println("--> Socket closed...");
							ApplicationStates.shutdown();
							return;
						} catch (IOException e) {
							e.printStackTrace();
							return;
						}
						
//						Co.println("Got line: " + receivedLine);
						
						if (receivedLine.trim().equals(CommunicationCommand.com_end_communication.command)) {
							return;
						} else if (receivedLine.trim().equals(CommunicationCommand.com_end_command.command)) {
							GsonBuilder gsonBuilder = new GsonBuilder();
							gsonBuilder.registerTypeAdapter(SignalParameters.class, new GsonClassAdapter());
							gsonBuilder.registerTypeAdapter(IndicatorParameters.class, new GsonClassAdapter());
							CommandHolder<?> commandHolder = gsonBuilder.create().fromJson(receivedString, CommandHolder.class);
							
							switch (commandHolder.command){
								case compute_unit_backtest :
//									Co.println("--> Got compute unit");
									Type type = new TypeToken<CommandHolder<ComputeUnitForBacktest>>(){}.getType();
									CommandHolder<ComputeUnitForBacktest> commandHolderTyped = gsonBuilder.create().fromJson(receivedString, type);
									listener.receivedCommand(commandHolderTyped);
									break;
								case no_units_left:
									Co.println("--> Got no units left");
									listener.receivedCommand(commandHolder);
									break;
								case restart_node:
									Co.println("--> Got restart node");
									printWriter.close();
									clientSocket.close();
									ApplicationStates.shutdown();
									break;
							}
							
							
//							CommandHolder commandHolderGeneric = new CommandReceiver().receiveGsonString(receivedString);
//							
//							if (commandHolderGeneric.command == Command.compute_unit_backtest){
//								Type type = new TypeToken<CommandHolder<ComputeUnitForBacktest>>(){}.getType();
//								CommandHolder commandHolderTyped = new Gson().fromJson(receivedString, type);
//								listener.receivedCommand(commandHolderTyped);
//							}else if (commandHolderGeneric.command == Command.no_units_left){
//								listener.receivedCommand(commandHolderGeneric);
//							}else if (commandHolderGeneric.command == Command.restart_node){
//								Co.println("--> Restart!");
//								printWriter.close();
//								clientSocket.close();
//								ApplicationStates.shutdown();
//							}
//							printWriter.println(CommunicationCommands.com_ok_command.command);
							
							receivedString = new String();
						} else {
							receivedString = receivedString.concat(receivedLine);
						}
					}
				}catch(Exception e){
					e.printStackTrace();
				}	
			}
				
		});
		
		thread.start();
	}
}
