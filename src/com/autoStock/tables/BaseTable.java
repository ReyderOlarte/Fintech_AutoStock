/**
 * 
 */
package com.autoStock.tables;

import java.util.ArrayList;

import com.autoStock.tools.PrintTools;

/**
 * @author Kevin
 *
 */
public abstract class BaseTable {
	protected ArrayList<ArrayList<String>> listOfDisplayRows = new ArrayList<ArrayList<String>>();
	public abstract ArrayList<ArrayList<String>> getDisplayRows();
	
	public String getLastRowAsString(){
		return PrintTools.getString(listOfDisplayRows.get(listOfDisplayRows.size()-1));
	}
}
