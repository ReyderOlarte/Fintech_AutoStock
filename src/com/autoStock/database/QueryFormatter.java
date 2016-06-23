/**
 * 
 */
package com.autoStock.database;

import com.autoStock.database.DatabaseDefinitions.BasicQueries;
import com.autoStock.database.DatabaseDefinitions.QueryArg;

/**
 * @author Kevin Kowalewski
 *
 */
public class QueryFormatter {
	public String format(BasicQueries basicQuery, QueryArg... queryArg){
		if (basicQuery.listOfFormatterArguments.length != queryArg.length){
			throw new IllegalArgumentException("Invalid length of Formatter Arguments");
		}
		
		String[] listOfArguments = new String[queryArg.length];
		
		int i = 0;
		for (QueryArg argument : queryArg){
			listOfArguments[i] = argument.value;
			i++;
		}
		
		return String.format(basicQuery.query, listOfArguments);
	}
}
