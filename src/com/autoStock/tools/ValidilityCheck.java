package com.autoStock.tools;

import com.autoStock.menu.MenuDefinitions.MenuArgumentTypes;
import com.autoStock.menu.MenuDefinitions.MenuArguments;
import com.autoStock.trading.platform.ib.definitions.HistoricalDataDefinitions;
import com.autoStock.trading.platform.ib.definitions.HistoricalDataDefinitions.Resolution;

/**
 * @author Kevin Kowalewski
 *
 */
public class ValidilityCheck {
	
	public boolean isValidMenuArgument(MenuArguments menuArgument){
		//Co.println("Checking: " + menuArgument.name());
		for (MenuArgumentTypes menuArgumentType : menuArgument.arrayOfArgumentTypes){
			if (menuArgumentType == MenuArgumentTypes.basic_date){
				return isValidDate(menuArgument.value);
			}
			
			else if (menuArgumentType == MenuArgumentTypes.basic_integer){
				return isValidInt(menuArgument.value);
			}
			
			else if (menuArgumentType == MenuArgumentTypes.basic_float){
				return isValidFloat(menuArgument.value);
			}
			
			else if (menuArgumentType == MenuArgumentTypes.basic_period){
				return isValidPeriod(menuArgument.value);
			}
			
			else if (menuArgumentType.name().startsWith("const")){
				return isValidMenuDefinitionConst(menuArgument.value);
			}
			
			else if (menuArgumentType == MenuArgumentTypes.basic_resolution){
				return isValidResolution(menuArgument.value);
			}
			
			else if (menuArgumentType == MenuArgumentTypes.basic_string){
				return true;
			}
			
			else if (menuArgumentType == MenuArgumentTypes.basic_string_array){
				return true;
			}
			
			else {
				throw new UnsatisfiedLinkError();
			}
		}
		
		return false;
	}
	
	public boolean isValidResolution(String value){
		if (Resolution.valueOf(value) != null){
			return true;
		}
		
		return false;
	}
	
	public boolean isValidInt(String value){
		try {
			Integer.parseInt(value);
			return true;
		}catch (NumberFormatException e){
			return false;
		}
	}
	
	public boolean isValidFloat(String value){
		try {
			Float.parseFloat(value);
			return true;
		}catch (NumberFormatException e){
			return false;
		}
	}
	
	public boolean isValidDate(String value){
		if (new DateTools().getDateFromString(value) != null){
			return true;
		}else{
			return false;
		}
	}
	
	public boolean isValidPeriod(String value){
		for (HistoricalDataDefinitions.Resolution resolution : HistoricalDataDefinitions.Resolution.values()){
			if (value.toLowerCase().equals(resolution.name().toLowerCase())){
				return true;
			}
		}
		
		return false;
	}
	
	public boolean isValidMenuDefinitionConst(String value){
		for (MenuArgumentTypes tempArgumentType : MenuArgumentTypes.values()){
			if (tempArgumentType.name().startsWith("const") && StringTools.removePrefix(tempArgumentType.name(), "_").equals(value)){
				return true;
			}
		}
		return false;
	}
}
