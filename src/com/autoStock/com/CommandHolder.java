/**
 * 
 */
package com.autoStock.com;

import com.autoStock.comServer.CommunicationDefinitions.Command;

/**
 * @author Kevin Kowalewski
 * 
 */
public class CommandHolder<T> {
	public Command command;
	public T commandParameters;
	
	public CommandHolder(Command command){
		this.command = command;
	}
	
	public CommandHolder(Command command, T commandParameters){
		this.command = command;
		this.commandParameters = commandParameters;
	}
}
