package com.autoStock.tools;

import java.io.File;
import java.util.ArrayList;

import com.autoStock.tables.TableController;
import com.autoStock.tables.TableDefinitions.AsciiColumns;
import com.autoStock.tables.TableDefinitions.AsciiTables;

/**
 * @author Kevin Kowalewski
 *
 */
public class ExportTools {
	public void exportToCSV(File file, AsciiTables table, ArrayList<ArrayList<String>> values){
		new TableController().checkTable(table, values);
	}
	
	public String exportToString(AsciiTables table, ArrayList<ArrayList<String>> values){
		StringBuilder stringBuilder = new StringBuilder();
		
		for (int i=0; i<table.arrayOfColumns.length; i++){
			AsciiColumns column = table.arrayOfColumns[i];
			stringBuilder.append(column.name());
			
			if (i != table.arrayOfColumns.length-1){
				stringBuilder.append(",");
			}
		}
		
		stringBuilder.append("\n");
		
		for (ArrayList<String> listOfString : values){
			for (String string : listOfString){
				stringBuilder.append(string.replaceAll(",", " "));
				
				if (listOfString.indexOf(string) != listOfString.size() -1){
					stringBuilder.append(",");
				}
			}
			
			stringBuilder.append("\n");
		}
		
		return stringBuilder.toString();
	}
}
