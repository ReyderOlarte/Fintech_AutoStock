package com.autoStock.algorithm.core;

import java.util.ArrayList;
import java.util.Date;

import com.autoStock.Co;
import com.autoStock.account.AccountProvider;
import com.autoStock.algorithm.AlgorithmTest;
import com.autoStock.algorithm.core.AlgorithmDefinitions.AlgorithmMode;
import com.autoStock.exchange.request.RequestMarketSymbolData;
import com.autoStock.exchange.request.base.RequestHolder;
import com.autoStock.exchange.request.listener.RequestMarketSymbolDataListener;
import com.autoStock.exchange.results.ExResultMarketSymbolData.ExResultSetMarketSymbolData;
import com.autoStock.position.PositionDefinitions.PositionType;
import com.autoStock.position.PositionManager;
import com.autoStock.trading.platform.ib.definitions.HistoricalDataDefinitions.Period;
import com.autoStock.trading.types.MarketSymbolData;
import com.autoStock.trading.types.Position;
import com.autoStock.trading.yahoo.FundamentalData;
import com.autoStock.trading.yahoo.RequestFundamentalsListener;
import com.autoStock.trading.yahoo.YahooFundamentals;
import com.autoStock.types.Exchange;
import com.autoStock.types.QuoteSlice;
import com.autoStock.types.Symbol;

/**
 * @author Kevin Kowalewski
 *
 */
public class ActiveAlgorithmContainer {
	public final AlgorithmTest algorithm;
	public final Symbol symbol;
	public final Exchange exchange;
	public RequestMarketSymbolData requestMarketData;
	private YahooFundamentals yahooFundamentals;
	private ActivationListener activationListener;
	public final ArrayList<QuoteSlice> listOfQuoteSlice = new ArrayList<QuoteSlice>();
	
	public ActiveAlgorithmContainer(Exchange exchange, Symbol symbol, String additionSource, ActivationListener activationListener){
		this.symbol = symbol;
		this.exchange = exchange;
		this.activationListener = activationListener;
		algorithm = new AlgorithmTest(exchange, symbol, AlgorithmMode.mode_engagement, AccountProvider.getInstance().getGlobalAccount());
		algorithm.algorithmSource = additionSource;
	}
	
	public void activate(){
		yahooFundamentals = new YahooFundamentals(new RequestFundamentalsListener() {
			@Override
			public void failed(RequestHolder requestHolder) {
				activationListener.failed(ActiveAlgorithmContainer.this, "Failed to get fundamentals");
				return;
			}
			
			@Override
			public void success(FundamentalData fundamentalData) {
				if (fundamentalData.avgDailyVolume < 500000 && fundamentalData.todaysVolume < 300000){
					activationListener.failed(ActiveAlgorithmContainer.this, "Low volume");
					return;
				}
				
				algorithm.setFundamentalData(fundamentalData);
				algorithm.init(new Date(), new Date());
				startAlgorithmFeed();
				activationListener.activated(ActiveAlgorithmContainer.this);
			}
		}, exchange, symbol);
		
		yahooFundamentals.execute();
	}
	
	private void startAlgorithmFeed(){
		requestMarketData = new RequestMarketSymbolData(new RequestHolder(this), new RequestMarketSymbolDataListener() {
			@Override
			public void failed(RequestHolder requestHolder) {
				Co.println("--> Completed?");
			}
			
			@Override
			public void receiveQuoteSlice(RequestHolder requestHolder, QuoteSlice quoteSlice) {
				if (quoteSlice.priceClose != 0){
					listOfQuoteSlice.add(quoteSlice);
					algorithm.receiveQuoteSlice(quoteSlice);
				}
			}
			
			@Override
			public void completed(RequestHolder requestHolder, ExResultSetMarketSymbolData exResultSetMarketData) {
				Co.println("--> Completed?");
			}
		}, new MarketSymbolData(exchange, symbol), Period.min.seconds * 1000);
	}
	
	public void deactivate(){
		Co.println("--> Deactivating: " + symbol.name);
		if (requestMarketData != null){requestMarketData.cancel();}
		if (yahooFundamentals != null){yahooFundamentals.cancel();}
		algorithm.endOfFeed(symbol);
		Position position = PositionManager.getGlobalInstance().getPosition(symbol);
		if (position != null){
			if (position.positionType == PositionType.position_long || position.positionType == PositionType.position_long_entry){
				PositionManager.getGlobalInstance().executePosition(algorithm.getCurrentQuoteSlice(), position.exchange, algorithm.strategyBase.signaler, PositionType.position_long_exit, position, null, position.basicAccount);
			}else if (position.positionType == PositionType.position_short || position.positionType == PositionType.position_short_entry){
				PositionManager.getGlobalInstance().executePosition(algorithm.getCurrentQuoteSlice(), position.exchange, algorithm.strategyBase.signaler, PositionType.position_short_exit, position, null, position.basicAccount);
			}else if (position.positionType == PositionType.position_failed){
				Co.println("--> Warning! Position status was failed while deactivating algorithm...");
			}else if (position.positionType == PositionType.position_cancelling || position.positionType == PositionType.position_cancelled){
				Co.println("--> Warning! Position status was cancelled while deactivating algorithm...");
			}else if (position.positionType == PositionType.position_long_exit || position.positionType == PositionType.position_short_exit){
				//pass
			}else{
				throw new IllegalStateException();
			}
		}
	}
	
	public static interface ActivationListener {
		public void activated(ActiveAlgorithmContainer activeAlgorithmContainer);
		public void failed(ActiveAlgorithmContainer activeAlgorithmContainer, String reason);
	}
}
