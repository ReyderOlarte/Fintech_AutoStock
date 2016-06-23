package com.autoStock.tools;

import java.util.ArrayList;

import com.autoStock.finance.SecurityTypeHelper.SecurityType;
import com.autoStock.types.Symbol;

/**
 * @author Kevin Kowalewski
 *
 */
public class SymbolTools {
	public static ArrayList<Symbol> getListOfSymbolFromListOfString(ArrayList<String> listOfString, SecurityType securityType){
		ArrayList<Symbol> listOfSymbol = new ArrayList<Symbol>();
		
		for (String string : listOfString){
			listOfSymbol.add(new Symbol(string, securityType));
		}
		
		return listOfSymbol;
	}
}
