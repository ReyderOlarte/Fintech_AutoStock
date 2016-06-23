/**
 * 
 */
package com.autoStock.types;

import java.util.ArrayList;

import com.autoStock.finance.SecurityTypeHelper.SecurityType;
import com.autoStock.generated.basicDefinitions.TableDefinitions.DbStockHistoricalPrice;

/**
 * @author Kevin Kowalewski
 *
 */
public class ConvertTypes {
	public ArrayList<QuoteSlice> convertToQuoteSlice(ArrayList<DbStockHistoricalPrice> listOfDbStockHistoricalPrice){
		ArrayList<QuoteSlice> listOfQuoteSlice = new ArrayList<QuoteSlice>();
		
		for (DbStockHistoricalPrice dbStockHistoricalPrice : listOfDbStockHistoricalPrice){
			QuoteSlice typeQuoteSlice = new QuoteSlice();
			typeQuoteSlice.symbol = new Symbol(dbStockHistoricalPrice.symbol, SecurityType.type_stock);
			typeQuoteSlice.dateTime = dbStockHistoricalPrice.dateTime;
			typeQuoteSlice.priceOpen = dbStockHistoricalPrice.priceOpen;
			typeQuoteSlice.priceHigh = dbStockHistoricalPrice.priceHigh;
			typeQuoteSlice.priceLow = dbStockHistoricalPrice.priceLow;
			typeQuoteSlice.priceClose = dbStockHistoricalPrice.priceClose;
			typeQuoteSlice.sizeVolume = dbStockHistoricalPrice.sizeVolume;
			
			listOfQuoteSlice.add(typeQuoteSlice);
		}
		
		return listOfQuoteSlice;
	}
}
