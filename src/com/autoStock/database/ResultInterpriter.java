/**
 * 
 */
package com.autoStock.database;

import java.sql.ResultSet;

import com.autoStock.database.queryResults.QueryResult;
import com.autoStock.generated.basicDefinitions.TableDefinitions.DbExchange;
import com.autoStock.generated.basicDefinitions.TableDefinitions.DbGson;
import com.autoStock.generated.basicDefinitions.TableDefinitions.DbStockHistoricalPrice;
import com.autoStock.generated.basicDefinitions.TableDefinitions.DbSymbol;
import com.autoStock.generated.basicDefinitions.TableDefinitions.DbWhitelist;

/**
 * @author Kevin Kowalewski
 *
 */
public class ResultInterpriter { // This class should go away
	public Object interprit(ResultSet resultSet, Class resultClass){
		try {
			if (resultClass == DbStockHistoricalPrice.class){
				return new DatabaseBinder().getDbStockHistoricalPrice(resultSet.getLong(1), resultSet.getString(2), resultSet.getString(3), resultSet.getInt(4), resultSet.getDouble(5), resultSet.getDouble(6), resultSet.getDouble(7), resultSet.getDouble(8), resultSet.getInt(9), resultSet.getString(10));
			}else if (resultClass == DbSymbol.class){
				return new DatabaseBinder().getDbSymbol(resultSet.getLong(1), resultSet.getString(2), resultSet.getString(3), resultSet.getString(4));
			}else if (resultClass == QueryResult.QrSymbolCountFromExchange.class){
				return new DatabaseBinder().getQrSymbolCountFromExchange(resultSet.getString(1), resultSet.getInt(2), resultSet.getLong(3));
			}else if (resultClass == DbExchange.class){
				return new DatabaseBinder().getDbExchange(resultSet.getLong(1), resultSet.getString(2), resultSet.getString(3), resultSet.getString(4), resultSet.getString(5), resultSet.getString(6), resultSet.getString(7));
			}else if (resultClass == DbWhitelist.class){
				return new DatabaseBinder().getDbWhitelist(resultSet.getInt(1), resultSet.getString(2), resultSet.getString(3), resultSet.getString(4), resultSet.getInt(5), resultSet.getString(6));
			}else if (resultClass == DbGson.class){
				return new DatabaseBinder().getDbGson(resultSet.getInt(1), resultSet.getString(2));
			}else{
				throw new UnsupportedOperationException();
			}
		}catch (Exception e){e.printStackTrace();}
		
		throw new UnsatisfiedLinkError();
	}
}
