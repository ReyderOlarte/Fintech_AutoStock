/**
 * 
 */
package com.autoStock.comServer;

import java.io.PrintWriter;

import com.autoStock.MainClusteredBacktest;
import com.autoStock.cluster.ComputeUnitForBacktest;
import com.autoStock.com.CommandHolder;
import com.autoStock.com.CommandSerializer;
import com.autoStock.comServer.CommunicationDefinitions.Command;

/**
 * @author Kevin Kowalewski
 *
 */
public class CommandResponder {	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void receivedCommand(CommandHolder commandHolder, PrintWriter printWriter){
//		Co.println("--> Responding to command: " + commandHolder.command.name());
		
		if (commandHolder.command == Command.accept_unit){
			ComputeUnitForBacktest computeUnitForBacktest = MainClusteredBacktest.getInstance().getNextComputeUnit();
			if (computeUnitForBacktest == null){
				new CommandSerializer().sendSerializedCommand(new CommandHolder(Command.no_units_left), printWriter);
			}else{
				new CommandSerializer().sendSerializedCommand(new CommandHolder(Command.compute_unit_backtest, computeUnitForBacktest), printWriter);
			}
		}
		
		printWriter.flush();
	}
}
