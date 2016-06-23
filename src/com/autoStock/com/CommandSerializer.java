package com.autoStock.com;

import java.io.PrintWriter;

import com.autoStock.comServer.CommunicationDefinitions.Command;
import com.autoStock.comServer.CommunicationDefinitions.CommunicationCommand;
import com.autoStock.internal.GsonClassAdapter;
import com.autoStock.signal.SignalDefinitions.IndicatorParameters;
import com.autoStock.signal.SignalDefinitions.SignalParameters;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

/**
 * @author Kevin Kowalewski
 *
 */
public class CommandSerializer {
	private GsonBuilder gsonBuilder = new GsonBuilder();
	private Gson gson;
	
	public CommandSerializer(){
		gsonBuilder.registerTypeAdapter(SignalParameters.class, new GsonClassAdapter());
		gsonBuilder.registerTypeAdapter(IndicatorParameters.class, new GsonClassAdapter());
		gsonBuilder.enableComplexMapKeySerialization();
		gson = gsonBuilder.create();
	}
	
	public void sendSerializedCommand(Command command, PrintWriter printWriter){
		String string = gson.toJson(new CommandHolder(command), new TypeToken<CommandHolder>(){}.getType());
		
		printWriter.println(string);
		printWriter.println(CommunicationCommand.com_end_command.command);
	}
	
	public void sendSerializedCommand(CommandHolder commandHolder, PrintWriter printWriter){
		String string = gson.toJson(commandHolder, new TypeToken<CommandHolder>(){}.getType());
		
		printWriter.println(string);
		printWriter.println(CommunicationCommand.com_end_command.command);
	}
}
