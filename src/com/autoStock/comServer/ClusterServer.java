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
import java.net.ServerSocket;
import java.net.Socket;

import com.autoStock.Co;
import com.autoStock.cluster.ComputeResultForBacktest;
import com.autoStock.com.CommandHolder;
import com.autoStock.com.ListenerOfCommandHolderResult;
import com.autoStock.comServer.CommunicationDefinitions.CommunicationCommand;
import com.autoStock.internal.GsonClassAdapter;
import com.autoStock.signal.SignalDefinitions.SignalParameters;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

/**
 * @author Kevin Kowalewski
 * 
 */
public class ClusterServer {
	private ListenerOfCommandHolderResult listener;
	private Thread threadForRequestServer;
	
	public ClusterServer(ListenerOfCommandHolderResult listener){
		this.listener = listener;
	}

	public void startServer() {
		threadForRequestServer = new Thread(new Runnable(){
			@SuppressWarnings("resource")
			@Override
			public void run() {
				Co.println("--> Starting server...");
				Thread.currentThread().setPriority(Thread.MAX_PRIORITY);
				ServerSocket server = null;
				Socket incoming = null;
		
				try {
					server = new ServerSocket(8888, 128, InetAddress.getByName("0.0.0.0"));
				} catch (Exception e) {
					e.printStackTrace();
				}
		
				while (true) {
					try {
						incoming = server.accept();
					} catch (Exception e) {
						e.printStackTrace();
					}
					ClientThread cs = new ClientThread(incoming);
					cs.start();
					
					try {Thread.sleep(1000);}catch(Exception e){}
				}
			}
		});
		
		threadForRequestServer.start();
	}

	private class ClientThread extends Thread {
		private Socket socket;

		public ClientThread(Socket socket) {
			super();
			this.socket = socket;
			Thread.currentThread().setPriority(Thread.MAX_PRIORITY);
		}

		@Override
		public void run() {
			BufferedReader in = null;
			PrintWriter out = null;
			String receivedLine = new String();
			String receivedString = new String();
			 
			Co.println("Instnace: " + socket.toString());

			try {
				in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			} catch (Exception e) {
				e.printStackTrace();
			}
			try {
				out = new PrintWriter(socket.getOutputStream(), true);
			} catch (Exception e) {
				e.printStackTrace();
			}

			try {
				while ((receivedLine = in.readLine()) != null) {
//					Co.println("Got line: " + receivedLine);

					if (receivedLine.trim().equals(CommunicationCommand.com_end_communication.command)) {
						return;
					} else if (receivedLine.trim().equals(CommunicationCommand.com_end_command.command)) {
						GsonBuilder gsonBuilder = new GsonBuilder();
						gsonBuilder.registerTypeAdapter(SignalParameters.class, new GsonClassAdapter());
						CommandHolder<?> commandHolder = gsonBuilder.create().fromJson(receivedString, CommandHolder.class);
						
						switch (commandHolder.command){
							case accept_unit:
								Co.println("--> Got accept unit");
								new CommandResponder().receivedCommand(commandHolder, out);
								break;
							case backtest_results:
								Type type = new TypeToken<CommandHolder<ComputeResultForBacktest>>(){}.getType();
								CommandHolder<ComputeResultForBacktest> commandHolderTyped = gsonBuilder.create().fromJson(receivedString, type);
								listener.receivedCommand(commandHolderTyped);
								Co.println("--> Got backtest results");
								break;
						}
						
						
//						CommandHolder commandHolderGeneric = new CommandReceiver().receiveGsonString(receivedString);
//						
//						if (commandHolderGeneric.command == Command.accept_unit){
//							new CommandResponder().receivedCommand(commandHolderGeneric, out);
//						}else if (commandHolderGeneric.command == Command.backtest_results){
//							Type type = new TypeToken<CommandHolder<ComputeResultForBacktest>>(){}.getType();
//							CommandHolder commandHolderTyped = new Gson().fromJson(receivedString, type);
//							listener.receivedCommand(commandHolderTyped);
//						}
						
						receivedString = new String();
					} else {
						receivedString = receivedString.concat(receivedLine);
					}
				}
			}catch(IOException e){
//				e.printStackTrace();
				Co.println("--> Client disconnected abruptly...");
				try {socket.close();}catch(Exception ex){}
			}
		}
	}
}
