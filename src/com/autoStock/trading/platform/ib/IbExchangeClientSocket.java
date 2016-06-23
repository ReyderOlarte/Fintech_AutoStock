/**
 * 
 */
package com.autoStock.trading.platform.ib;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Random;

import com.autoStock.internal.Config;
import com.autoStock.trading.platform.ib.core.EClientSocket;

/**
 * @author Kevin Kowalewski
 *
 */
public class IbExchangeClientSocket {
	public EClientSocket eClientSocket;
	public Socket socket;
	
	public void init(IbExchangeWrapper ibExchangeWrapper) throws UnknownHostException, IOException{
		try {
			socket = new Socket(Config.plIbTwsHost, Config.plIbTwsPort);
		}catch (Exception e){
//			Co.println("Failed to connect to TWS!");
		}
		eClientSocket = new EClientSocket(ibExchangeWrapper);
	}
	
	public void connect() throws IOException{
		eClientSocket.eConnect(socket, new Random().nextInt(10000));
	}
}
