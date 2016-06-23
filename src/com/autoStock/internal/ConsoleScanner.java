/**
 * 
 */
package com.autoStock.internal;

import java.util.ArrayList;
import java.util.Scanner;

import com.autoStock.Co;

/**
 * @author Kevin
 *
 */
public class ConsoleScanner {
	private static ArrayList<ConsoleListener> listOfConsoleListner = new ArrayList<ConsoleListener>();
	
	public static enum ConsoleMatch {
		command_end_now("END"),
		command_end_safely("ENDS"),
		none(null),
		;
		
		public final String line;
		
		ConsoleMatch(String line){
			this.line = line;
		}
	}
	
	public static interface ConsoleListener {
		public void receivedMatch(ConsoleMatch consoleMatch);
		public void receivedLine(String line);
	}
	
	public static void start(){
		new Thread(new Runnable(){
			@SuppressWarnings("resource")
			@Override
			public void run() {
				Scanner scanner = new Scanner(System.in);
				while (true){
					if (scanner.hasNext()){
						String line = scanner.nextLine();
						
						for (ConsoleMatch consoleMatch : ConsoleMatch.values()){
							if (consoleMatch.line != null && consoleMatch.line.equals(line.trim())){
								receivedMatch(consoleMatch);
							}
						}
						
						receivedLine(line);
					}
				}
			}
		}).start();
	}
	
	private static void receivedLine(String line){
		for (ConsoleListener consoleListener : listOfConsoleListner){consoleListener.receivedLine(line);}
	}
	
	private static void receivedMatch(ConsoleMatch match){
		for (ConsoleListener consoleListener : listOfConsoleListner){consoleListener.receivedMatch(match);}
	}
	
	public static void addListener(ConsoleListener consoleListener){
		listOfConsoleListner.add(consoleListener);
	}
	
	public static void removeListener(ConsoleListener consoleListener){
		listOfConsoleListner.remove(consoleListener);
	}
}
