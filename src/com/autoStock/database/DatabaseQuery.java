/**
 * 
 */
package com.autoStock.database;

import java.lang.reflect.Type;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;

import com.autoStock.Co;
import com.autoStock.cache.DiskCache;
import com.autoStock.cache.HashCache;
import com.autoStock.database.DatabaseDefinitions.BasicQueries;
import com.autoStock.database.DatabaseDefinitions.QueryArg;
import com.autoStock.generated.basicDefinitions.TableDefinitions.DbExchange;
import com.autoStock.generated.basicDefinitions.TableDefinitions.DbStockHistoricalPrice;
import com.autoStock.tools.MiscTools;
import com.autoStock.tools.ThreadTools;
import com.google.gson.reflect.TypeToken;

/**
 * @author Kevin Kowalewski
 *
 */
public class DatabaseQuery {	
	private static HashCache hashCache = new HashCache();
	private static DiskCache diskCache = new DiskCache();
	
	public ArrayList<?> getQueryResults(BasicQueries dbQuery, QueryArg... queryArg){
		try {
			String query = new QueryFormatter().format(dbQuery, queryArg);
			String queryHash = MiscTools.getHash(query);
			
			//Co.println("Executing query: " + query);
			//ThreadTools.printStackTrace();
			
			if (dbQuery.isCachable){
				if (hashCache.containsKey(queryHash)){
					//Co.println("--> Using memory cache");
					return (ArrayList<?>) hashCache.getValue(queryHash);
				}
				
				if (diskCache.containsKey(queryHash) && getGsonType(dbQuery) != null){
	//				Co.println("--> Using disk cache");
					ArrayList<?> listOfResults = diskCache.readList(queryHash, getGsonType(dbQuery));
					hashCache.addValue(queryHash, listOfResults);
					return listOfResults; 
				}
			}
			
			Connection connection = DatabaseCore.getConnection();
			Statement statement = connection.createStatement();
			ResultSet resultSet = statement.executeQuery(query);
			
			ArrayList<Object> listOfResults = new ArrayList<Object>();

			while (resultSet.next()){
				listOfResults.add(new ResultInterpriter().interprit(resultSet, dbQuery.resultClass));
			}
			
			resultSet.close();
			statement.close();
			connection.close();
			
			if (dbQuery.isCachable){
				hashCache.addValue(queryHash, listOfResults);
				diskCache.writeList(queryHash, listOfResults);
			}
			
			return listOfResults;
			
		}catch (Exception e){
			Co.println("Could not execute query: " + new QueryFormatter().format(dbQuery, queryArg));
			e.printStackTrace(); return null;}
	}
	
	public int insert(BasicQueries basicQuery, QueryArg... queryArg){
		int rowId = -1;
		
		try {
			String query = new QueryFormatter().format(basicQuery, queryArg);
		
			Connection connection = DatabaseCore.getConnection();
			Statement statement = connection.createStatement();
		
			statement.execute(query);
			ResultSet resultSet = statement.executeQuery("select LAST_INSERT_ID()");
			
			if (resultSet.next()){
				rowId = resultSet.getInt(1);
			}
			
			resultSet.close();
			statement.close();
			connection.close();
			
			return rowId;
		}catch(Exception e){
			Co.println("Could not execute query: " + new QueryFormatter().format(basicQuery, queryArg));
			e.printStackTrace();
		}

		return rowId;
	}

	private Type getGsonType(BasicQueries dbQuery) {
		if (dbQuery.resultClass == DbStockHistoricalPrice.class){
			return new TypeToken<ArrayList<DbStockHistoricalPrice>>(){}.getType();
		}else if (dbQuery.resultClass == DbExchange.class){
			return new TypeToken<ArrayList<DbExchange>>(){}.getType();
		}
		
		return null;
	}
}
