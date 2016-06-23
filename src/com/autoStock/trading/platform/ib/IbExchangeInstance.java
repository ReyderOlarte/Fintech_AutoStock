/**
 * 
 */
package com.autoStock.trading.platform.ib;

import java.text.SimpleDateFormat;

import com.autoStock.Co;
import com.autoStock.exchange.ExchangeDefinitions.ExchangeDesignation;
import com.autoStock.exchange.request.RequestMarketScanner.MarketScannerType;
import com.autoStock.exchange.request.base.RequestHolder;
import com.autoStock.internal.Config;
import com.autoStock.trading.platform.ib.core.Contract;
import com.autoStock.trading.platform.ib.core.EClientSocket;
import com.autoStock.trading.platform.ib.core.ScannerSubscription;
import com.autoStock.trading.platform.ib.subset.SubsetOfScannerSubscription;
import com.autoStock.trading.types.HistoricalData;
import com.autoStock.trading.types.Order;
import com.autoStock.trading.types.RealtimeData;
import com.autoStock.types.Exchange;
import com.autoStock.types.Index;
import com.autoStock.types.Symbol;

/**
 * @author Kevin Kowalewski
 * 
 */
public class IbExchangeInstance {
	public IbExchangeWrapper ibExchangeWrapper;
	public IbExchangeClientSocket ibExchangeClientSocket;

	public void init() {
		ibExchangeWrapper = new IbExchangeWrapper();
		ibExchangeClientSocket = new IbExchangeClientSocket();

		try {
			ibExchangeClientSocket.init(ibExchangeWrapper);
			ibExchangeClientSocket.connect();
		} catch (Exception e) {
		} // e.printStackTrace();
	}

	public EClientSocket getEclientSocket() {
		return ibExchangeClientSocket.eClientSocket;
	}

	public void getAccountUpdates() {
		ibExchangeClientSocket.eClientSocket.reqAccountUpdates(true, Config.plIbUsername);
	}

	public void getOpenOrders() {
		ibExchangeClientSocket.eClientSocket.reqOpenOrders();
	}

	public void placeLongEntry(Order order, RequestHolder requestHolder) {
		Contract contract = new Contract();
		com.autoStock.trading.platform.ib.core.Order ibOrder = new com.autoStock.trading.platform.ib.core.Order();
		contract.m_exchange = order.exchange.exchangeDesignation == ExchangeDesignation.ASX ? "ASX" : "SMART";
		contract.m_symbol = order.symbol.name;
		contract.m_currency = order.exchange.currency.name();
		contract.m_secType = "STK";
		ibOrder.m_orderId = requestHolder.requestId;
		ibOrder.m_action = "BUY";
		ibOrder.m_orderType = "LMT";
		ibOrder.m_lmtPrice = order.getUnitPriceRequested() + (order.exchange.exchangeDesignation == ExchangeDesignation.TSEJ ? 5 : 0.25d);
		ibOrder.m_auxPrice = order.getUnitPriceRequested() + (order.exchange.exchangeDesignation == ExchangeDesignation.TSEJ ? 5 : 0.25d);
		ibOrder.m_totalQuantity = order.getUnitsRequested();

		ibExchangeClientSocket.eClientSocket.placeOrder(requestHolder.requestId, contract, ibOrder);
	}

	public void placeLongExit(Order order, RequestHolder requestHolder) {
		Contract contract = new Contract();
		com.autoStock.trading.platform.ib.core.Order ibOrder = new com.autoStock.trading.platform.ib.core.Order();
		contract.m_exchange = order.exchange.exchangeDesignation == ExchangeDesignation.ASX ? "ASX" : "SMART";
		contract.m_symbol = order.symbol.name;
		contract.m_secType = "STK";
		contract.m_currency = order.exchange.currency.name();
		ibOrder.m_orderId = requestHolder.requestId;
		ibOrder.m_action = "SELL";
		ibOrder.m_orderType = "MKT";
		ibOrder.m_auxPrice = 0;
		ibOrder.m_totalQuantity = order.getUnitsRequested();

		ibExchangeClientSocket.eClientSocket.placeOrder(requestHolder.requestId, contract, ibOrder);
	}

	public void placeShortEntry(Order order, RequestHolder requestHolder) {
		Contract contract = new Contract();
		com.autoStock.trading.platform.ib.core.Order ibOrder = new com.autoStock.trading.platform.ib.core.Order();
		contract.m_exchange = order.exchange.exchangeDesignation == ExchangeDesignation.ASX ? "ASX" : "SMART";
		contract.m_symbol = order.symbol.name;
		contract.m_secType = "STK";
		contract.m_currency = order.exchange.currency.name();
		ibOrder.m_orderId = requestHolder.requestId;
		ibOrder.m_action = "SELL";
		ibOrder.m_orderType = "MTL";
		ibOrder.m_auxPrice = 0;
		ibOrder.m_totalQuantity = order.getUnitsRequested();

		ibExchangeClientSocket.eClientSocket.placeOrder(requestHolder.requestId, contract, ibOrder);
	}

	public void placeShortExit(Order order, RequestHolder requestHolder) {
		Contract contract = new Contract();
		com.autoStock.trading.platform.ib.core.Order ibOrder = new com.autoStock.trading.platform.ib.core.Order();
		contract.m_exchange = order.exchange.exchangeDesignation == ExchangeDesignation.ASX ? "ASX" : "SMART";
		contract.m_symbol = order.symbol.name;
		contract.m_secType = "STK";
		contract.m_currency = order.exchange.currency.name();
		ibOrder.m_orderId = requestHolder.requestId;
		ibOrder.m_action = "BUY";
		ibOrder.m_orderType = "MTL";
		ibOrder.m_auxPrice = 0;
		ibOrder.m_totalQuantity = order.getUnitsRequested();

		ibExchangeClientSocket.eClientSocket.placeOrder(requestHolder.requestId, contract, ibOrder);
	}

	public void getScanner(RequestHolder requestHolder, Exchange exchange, MarketScannerType marketScannerType) {
		ScannerSubscription scanner = new SubsetOfScannerSubscription().getScanner(exchange, marketScannerType);
		ibExchangeClientSocket.eClientSocket.reqScannerSubscription(requestHolder.requestId, scanner);
	}

	public void getRealtimeData(RealtimeData typeRealtimeData, RequestHolder requestHolder) {
		// Co.println("Request id: " + requestHolder.requestId);
		Contract contract = new Contract();
		contract.m_exchange = "ASX";
		contract.m_symbol = typeRealtimeData.symbol;
		contract.m_secType = typeRealtimeData.securityType;
		contract.m_currency = "AUD";
		ibExchangeClientSocket.eClientSocket.reqRealTimeBars(requestHolder.requestId, contract, 5, "TRADES", false);
	}

	public void getMarketDataForSymbol(Exchange exchange, Symbol symbol, RequestHolder requestHolder) {
		// Co.println("Request id: " + requestHolder.requestId);
		Contract contract = new Contract();
		contract.m_exchange = exchange.exchangeDesignation == ExchangeDesignation.ASX ? "ASX" : "SMART";
		contract.m_symbol = symbol.name;
		contract.m_currency = exchange.currency.name();
		contract.m_secType = "STK";
		contract.m_includeExpired = true;
		ibExchangeClientSocket.eClientSocket.reqMktData(requestHolder.requestId, contract, "100,101,104,105,106,107,165,236,293,294,295,411", false);
	}

	public void getMarketDataForIndex(Exchange exchange, Index index, RequestHolder requestHolder) {
		Contract contract = new Contract();
		contract.m_exchange = exchange.exchangeDesignation.name();
		contract.m_symbol = index.indexName;
		contract.m_currency = exchange.currency.name();
		contract.m_secType = "IND";
		contract.m_includeExpired = true;
		ibExchangeClientSocket.eClientSocket.reqMktData(requestHolder.requestId, contract, "100,101,104,105,106,107,165,236,293,294,295,411", false);
	}

	public void getHistoricalPrice(HistoricalData historicalData, RequestHolder requestHolder) {
		// Co.println("Request id: " + requestHolder.requestId);
		Contract contract = new Contract();
		contract.m_exchange = historicalData.exchange.name;
		contract.m_symbol = historicalData.symbol.name;
		contract.m_secType = historicalData.symbol.securityType.ibStringName;
		contract.m_currency = historicalData.exchange.currency.name();
		String endDate = new SimpleDateFormat("yyyyMMdd HH:mm:ss").format(historicalData.endDate) + " est";
		String duration = String.valueOf(historicalData.duration) + " S";
		ibExchangeClientSocket.eClientSocket.reqHistoricalData(requestHolder.requestId, contract, endDate, duration, "1 min", "TRADES", 0, 2);
	}
	
	public void getNextValidOrderId(){
		ibExchangeClientSocket.eClientSocket.reqIds(1);
	}

	public void cancelScanner(RequestHolder requestHolder) {
		ibExchangeClientSocket.eClientSocket.cancelScannerSubscription(requestHolder.requestId);
	}

	public void cancelMarketData(RequestHolder requestHolder) {
		ibExchangeClientSocket.eClientSocket.cancelMktData(requestHolder.requestId);
	}

	public void cancelMarketOrder(RequestHolder requestHolder) {
		Co.println("--> Cancelling order: " + requestHolder.requestId);
		ibExchangeClientSocket.eClientSocket.cancelOrder(requestHolder.requestId);
	}
}
