/**
 * 
 */
package com.autoStock.backtest;

import java.util.ArrayList;

import com.autoStock.algorithm.reciever.ReceiverOfQuoteSlice;
import com.autoStock.dataFeed.DataFeedHistoricalPrices;
import com.autoStock.dataFeed.listener.DataFeedListenerOfQuoteSlice;
import com.autoStock.generated.basicDefinitions.TableDefinitions.DbStockHistoricalPrice;
import com.autoStock.trading.types.HistoricalData;
import com.autoStock.types.QuoteSlice;
import com.autoStock.types.Symbol;

/**
 * @author Kevin Kowalewski
 *
 */
public class Backtest implements DataFeedListenerOfQuoteSlice {
	private HistoricalData typeHistoricalData;
	public DataFeedHistoricalPrices dataFeedHistoricalPrices;
	private ArrayList<DbStockHistoricalPrice> listOfPrices;
	private ReceiverOfQuoteSlice receiverOfQuoteSlice;
	private Symbol symbol;
	
	public Backtest(HistoricalData typeHistoricalData, ArrayList<DbStockHistoricalPrice> listOfPrices, Symbol symbol){
		this.typeHistoricalData = typeHistoricalData;
		this.listOfPrices = listOfPrices;
		this.dataFeedHistoricalPrices = new DataFeedHistoricalPrices(typeHistoricalData, listOfPrices);
		this.symbol = symbol;
	}
	
	public void performBacktest(ReceiverOfQuoteSlice reciever, boolean beBlocking){
		receiverOfQuoteSlice = reciever;
		dataFeedHistoricalPrices.addListener(this);
		
		if (listOfPrices.size() == 0){
			endOfFeed();
		}else{
			dataFeedHistoricalPrices.setupFeed();
			dataFeedHistoricalPrices.startFeed(beBlocking);
		}
	}

	@Override
	public void receivedQuoteSlice(QuoteSlice typeQuoteSlice) {
		receiverOfQuoteSlice.receiveQuoteSlice(typeQuoteSlice);
	}

	@Override
	public void endOfFeed() {
		receiverOfQuoteSlice.endOfFeed(symbol);
	}	
}
