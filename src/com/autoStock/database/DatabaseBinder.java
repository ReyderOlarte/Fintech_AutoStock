/**
 * 
 */
package com.autoStock.database;

import com.autoStock.database.queryResults.QueryResult;
import com.autoStock.generated.basicDefinitions.TableDefinitions;
import com.autoStock.generated.basicDefinitions.TableDefinitions.DbExchange;
import com.autoStock.generated.basicDefinitions.TableDefinitions.DbGson;
import com.autoStock.generated.basicDefinitions.TableDefinitions.DbStockHistoricalPrice;
import com.autoStock.generated.basicDefinitions.TableDefinitions.DbSymbol;
import com.autoStock.generated.basicDefinitions.TableDefinitions.DbWhitelist;
import com.autoStock.trading.platform.ib.definitions.HistoricalDataDefinitions.Resolution;
import com.autoStock.tools.DateTools;

/**
 * @author Kevin Kowalewski
 *
 */
public class DatabaseBinder {
	public DbStockHistoricalPrice getDbStockHistoricalPrice(long id, String symbol, String exchange, int resolution, double priceOpen, double priceHigh, double priceLow, double priceClose, int sizeVolume, String date){
		DbStockHistoricalPrice dbStockHistoricalPrice = new TableDefinitions.DbStockHistoricalPrice();
		dbStockHistoricalPrice.id = id;
		dbStockHistoricalPrice.symbol = symbol;
		dbStockHistoricalPrice.exchange = exchange;
		dbStockHistoricalPrice.priceOpen = priceOpen;
		dbStockHistoricalPrice.priceHigh = priceHigh;
		dbStockHistoricalPrice.priceLow = priceLow;
		dbStockHistoricalPrice.priceClose = priceClose;
		dbStockHistoricalPrice.sizeVolume = sizeVolume;
		dbStockHistoricalPrice.dateTime = DateTools.getDateFromString(date);
		dbStockHistoricalPrice.resolution = Resolution.fromMinutes(resolution);
		return dbStockHistoricalPrice;
	}
	
	public DbSymbol getDbSymbol(long id, String symbol, String exchange, String description){
		DbSymbol dbSymbol = new TableDefinitions.DbSymbol();
		dbSymbol.id = id;
		dbSymbol.symbol = symbol;
		dbSymbol.exchange = exchange;
		dbSymbol.description = description;
		return dbSymbol;
	}
	
	public DbExchange getDbExchange(long id, String exchange, String currency, String timeOpen, String timeClose, String timeOffset, String timeZone){
		DbExchange dbExchange = new TableDefinitions.DbExchange();
		dbExchange.id = id;
		dbExchange.exchange = exchange;
		dbExchange.currency = currency;
		dbExchange.timeOpen = DateTools.getTimeFromString(timeOpen);
		dbExchange.timeClose = DateTools.getTimeFromString(timeClose);
		dbExchange.timeOffset = DateTools.getTimeFromString(timeOffset);
		dbExchange.timeZone = timeZone;
		return dbExchange;
	}

	public Object getQrSymbolCountFromExchange(String symbol, int count, long sizeVolume) {
		return new QueryResult.QrSymbolCountFromExchange(symbol, count, sizeVolume);
	}

	public DbWhitelist getDbWhitelist(int id, String symbol, String exchange, String dateLastAdjustment, int adjustmentId, String reason) {
		DbWhitelist dbWhitelist = new TableDefinitions.DbWhitelist();
		dbWhitelist.id = id;
		dbWhitelist.symbol = symbol;
		dbWhitelist.exchange = exchange;
		dbWhitelist.dateLastAdjustment = dateLastAdjustment == null ? null : DateTools.getDateFromString(dateLastAdjustment);
		dbWhitelist.adjustmentId = adjustmentId;
		dbWhitelist.reason = reason;
		
		return dbWhitelist;
	}

	public DbGson getDbGson(int id, String gsonString) {
		DbGson dbGson = new DbGson();
		dbGson.id = id;
		dbGson.gsonString = gsonString;
		return dbGson;
	}
}
