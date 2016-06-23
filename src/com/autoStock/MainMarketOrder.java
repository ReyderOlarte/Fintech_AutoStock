package com.autoStock;

import java.util.Date;

import com.autoStock.account.AccountProvider;
import com.autoStock.exchange.request.RequestMarketSymbolData;
import com.autoStock.exchange.request.base.RequestHolder;
import com.autoStock.exchange.request.listener.RequestMarketSymbolDataListener;
import com.autoStock.exchange.results.ExResultMarketSymbolData.ExResultSetMarketSymbolData;
import com.autoStock.finance.SecurityTypeHelper.SecurityType;
import com.autoStock.internal.Global;
import com.autoStock.order.OrderDefinitions.OrderMode;
import com.autoStock.position.ListenerOfPositionStatusChange;
import com.autoStock.position.PositionDefinitions.PositionType;
import com.autoStock.position.PositionManager;
import com.autoStock.position.PositionValueTable;
import com.autoStock.trading.platform.ib.definitions.HistoricalDataDefinitions.Resolution;
import com.autoStock.trading.types.MarketSymbolData;
import com.autoStock.trading.types.Position;
import com.autoStock.types.Exchange;
import com.autoStock.types.QuoteSlice;
import com.autoStock.types.Symbol;

/**
 * @author Kevin Kowalewski
 * 
 */
public class MainMarketOrder implements ListenerOfPositionStatusChange {
	private RequestMarketSymbolData requestMarketSymbolData;
	
	public MainMarketOrder(final Exchange exchange, final PositionType positionType, final String symbol, final int units) {
		Global.callbackLock.requestLock();
		PositionManager.getGlobalInstance().orderMode = OrderMode.mode_exchange;
		
		requestMarketSymbolData = new RequestMarketSymbolData(new RequestHolder(null), new RequestMarketSymbolDataListener() {
			@Override
			public void failed(RequestHolder requestHolder) {
				
			}
			
			@Override
			public void receiveQuoteSlice(RequestHolder requestHolder, QuoteSlice quoteSlice) {
				requestMarketSymbolData.cancel();
				
				Position position = new Position(positionType, units, new Symbol(symbol, SecurityType.type_stock), exchange, quoteSlice.priceClose, null, AccountProvider.getInstance().getGlobalAccount(), new Date(), PositionManager.getGlobalInstance());
				position.setPositionListener(MainMarketOrder.this);
				position.executePosition();
			}
			
			@Override
			public void completed(RequestHolder requestHolder, ExResultSetMarketSymbolData exResultSetMarketData) {
				
			}
		}, new MarketSymbolData(exchange, new Symbol(symbol, SecurityType.type_stock)) , Resolution.sec_5.asSeconds());
	}

	@Override
	public void positionStatusChanged(Position position) {
		Co.println("\n\n--> Position status changed: " + position.positionType.name());
		new PositionValueTable().printTable(position, position.getPositionValue());
	}
}
