/**
 * 
 */
package com.autoStock.menu;

import com.autoStock.Co;
import com.autoStock.internal.ApplicationStates;
import com.autoStock.menu.MenuDefinitions.MenuArgumentTypes;
import com.autoStock.menu.MenuDefinitions.MenuArguments;
import com.autoStock.menu.MenuDefinitions.MenuStructures;
import com.autoStock.tools.MiscTools;
import com.autoStock.tools.StringTools;
import com.autoStock.tools.ValidilityCheck;

/**
 * @author Kevin Kowalewski
 *
 */
public class MenuController {
	public void displayMenu(MenuStructures menuStructure){
		if (menuStructure == MenuStructures.menu_main){
			for (MenuStructures menuStructureEntry : MenuStructures.values()){
				displayMenuStructure(menuStructureEntry);
			}
		}else{
			displayMenuStructure(menuStructure);
		}
	}
	
	private void displayMenuStructure(MenuStructures menuStructure){
		Co.print("\n" + StringTools.removePrefix(menuStructure.name(),"_") + "\n  ");
		for (MenuArguments menuArgumentsEntry : menuStructure.arrayOfMenuArguments){
			Co.print(StringTools.removePrefix(menuArgumentsEntry.name(), "_") + "[");
			for (MenuArgumentTypes menuArgumentTypesEntry : menuArgumentsEntry.arrayOfArgumentTypes){
				if (menuArgumentsEntry.arrayOfArgumentTypes.length-1 == MiscTools.getArrayIndex(menuArgumentsEntry.arrayOfArgumentTypes, menuArgumentTypesEntry)){
					Co.print(StringTools.removePrefix(menuArgumentTypesEntry.name(), "_") + "");
				}else{
					Co.print(StringTools.removePrefix(menuArgumentTypesEntry.name(), "_") + " | ");
				}
			}
			Co.print("] ");
		}
		Co.print("\n");
	}
	
	public MenuStructures getRelatedMenu(String[] arguments){
		if (arguments.length == 0){return null;}
		String command = arguments[0];
		for (MenuStructures menuStructureEntry : MenuStructures.values()){
			if (StringTools.removePrefix(menuStructureEntry.name(), "_").equals(command)){
				return menuStructureEntry;
			}
		}
		
		if (arguments[0].toUpperCase().contains("QC")){
			return MenuStructures.menu_quick_command;
		}
		
		return null;
	}

	public void handleMenuStructure(MenuStructures menuStructure, String[] args) {
		if (menuStructure == null){
			Co.println("\nAn unknown command was entered...");
			displayMenu(MenuStructures.menu_main);
			ApplicationStates.shutdown();
			return;
		}
		
		if (menuStructure.arrayOfMenuArguments[0] == MenuArguments.arg_none){
			return;
		}
		
		int index = 1;
		for (MenuArguments menuArgument : menuStructure.arrayOfMenuArguments){
			try {
				menuArgument.value = args[index];
			}catch (IndexOutOfBoundsException e){
				Co.println("\nError: Insufficient arguments supplied. Run this application with the following signature:");
				displayMenu(menuStructure);
				ApplicationStates.shutdown();
				return;
			}
			
			if (new ValidilityCheck().isValidMenuArgument(menuArgument) == false){
				Co.println("\nInvalid menu argument value for: " + menuArgument.name() + ", " + menuArgument.argumentDescription);
				ApplicationStates.shutdown();
				return;
			}

			index++;
		}
	}
}
