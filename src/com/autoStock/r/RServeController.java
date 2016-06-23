package com.autoStock.r;

import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

import org.math.R.RserverConf;
import org.math.R.Rsession;

import com.autoStock.Co;
import com.autoStock.osPlatform.Os;
import com.autoStock.osPlatform.Os.OsType;

public class RServeController {
	private static final int instancePoolSize = 32;
	private ArrayList<RProcess> listOfRServeProcesses = new ArrayList<RProcess>();

	public void start(){
		// should use Os.OsShell
		
		if (Os.identifyOS() != OsType.windows){
			throw new UnsupportedOperationException("R support not available for anything other than Windows");
		}
		
		for (int i=0; i<instancePoolSize; i++){
			try {
				Process process = Runtime.getRuntime().exec("C:\\Program Files\\R\\R-3.1.2\\bin\\R.exe -e \"library(Rserve);Rserve(FALSE,args='--no-save --slave --RS-port 100" + i + "')\" --no-save --slave");
				listOfRServeProcesses.add(new RProcess(i, 5000 + i, process));
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		
		Co.println("--> Started instances: " + instancePoolSize);
	}
	
	public synchronized Rsession getRSession(){
		for (RProcess rProcess : listOfRServeProcesses){
			if (rProcess.isBusy.get() == false){
				rProcess.isBusy.set(true);
				
				Co.println("--> Providing instnace at port: " + rProcess.port);
				
				RserverConf rserverConf = new RserverConf("localhost", rProcess.port, null, null, null);
			    return Rsession.newRemoteInstance(new PrintStream(new OutputStream() {@Override public void write(int b) {}}), rserverConf);
			}
		}
		
		throw new IllegalStateException("No RSessions are available");
	}
	
	public static class RProcess {
		public int index;
		public int port;
		public Process process;
		public AtomicBoolean isBusy;
		
		public RProcess(int index, int port, Process process) {
			this.index = index;
			this.port = port;
			this.process = process;
		}
	}
}
