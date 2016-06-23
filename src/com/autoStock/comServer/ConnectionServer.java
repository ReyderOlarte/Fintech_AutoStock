/**
 * 
 */
package com.autoStock.comServer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * @author Kevin Kowalewski
 * 
 */
public class ConnectionServer {
	public static final String EndCommunication = "QUIT";
	public static final String EndCommand = "END";

	public void startServer() {
		ServerSocket server = null;
		Socket incoming = null;

		try {
			server = new ServerSocket(8888, 5, InetAddress.getByName("127.0.0.1"));
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
			cs.run();
		}
	}

	private class ClientThread extends Thread {
		private Socket socket;
		
		public ClientThread(Socket sock) {
			super("ClientThread");
			socket = sock;
			start();
		}

		@Override
		public void run() {
			BufferedReader in = null;
			PrintWriter out = null;
			String receivedLine = new String();
			String receivedString = new String();

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
					return;
				} else if (receivedLine.trim().equals(EndCommand)) {
					new CommandReceiver().receiveGsonString(receivedString);
					out.println(EndCommand);
					receivedString = new String();
				} else {
					receivedString = receivedString.concat(receivedLine);
				}
			}
		}
	}
}
