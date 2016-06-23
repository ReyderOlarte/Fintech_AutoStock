package com.autoStock.order;

import java.util.ArrayList;

import com.autoStock.tables.TableController;
import com.autoStock.tables.TableDefinitions.AsciiTables;

/**
 * @author Kevin Kowalewski
 *
 */
public class OrderTable {
	public ArrayList<ArrayList<String>> listOfRows = new ArrayList<ArrayList<String>>();
	
	public void addRow(ArrayList<String> listOfString){
		listOfRows.add(listOfString);
	}
	
	public void display(){
		new TableController().displayTable(AsciiTables.order_manager, listOfRows);
	}
}
