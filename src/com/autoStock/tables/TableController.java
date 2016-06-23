/**
 * 
 */
package com.autoStock.tables;

import java.util.ArrayList;

import com.bethecoder.ascii_table.ASCIITable;

/**
 * @author Kevin Kowalewski
 *
 */
public class TableController {
	public void displayTable(TableDefinitions.AsciiTables table, ArrayList<ArrayList<String>> values){
		if (checkTable(table, values)){
			ASCIITable.getInstance().printTable(new TableHelper().getArrayOfColumns(table.arrayOfColumns), new TableHelper().getArrayOfRows(table.arrayOfColumns,values));
		}
	}
	
	public boolean checkTable(TableDefinitions.AsciiTables table, ArrayList<ArrayList<String>> values){
		if (values.size() == 0){return false;}
		
		if (table.arrayOfColumns == null || table.arrayOfColumns.length != values.get(0).size()){
			throw new IllegalStateException("AsciiTable definition, lenth of values don't match (columns, provided): " + table.arrayOfColumns.length + "," + values.get(0).size());
		}
		
		return true;
	}
	
	public String getTable(TableDefinitions.AsciiTables table, ArrayList<ArrayList<String>> values){
		if (checkTable(table, values)){
			return ASCIITable.getInstance().getTable(new TableHelper().getArrayOfColumns(table.arrayOfColumns),new TableHelper().getArrayOfRows(table.arrayOfColumns,values));			
		}
		
		return null;
	}
}
