/**
 * 
 */
package com.autoStock;

import java.util.ArrayList;

import com.autoStock.algorithm.core.ActiveAlgorithmManager;
import com.autoStock.algorithm.external.ExternalConditionDefintions;
import com.autoStock.exchange.ExchangeStatusListener;
import com.autoStock.exchange.ExchangeStatusObserver;
import com.autoStock.exchange.request.MultipleRequestMarketScanner;
import com.autoStock.exchange.request.RequestMarketScanner.MarketScannerType;
import com.autoStock.exchange.request.base.RequestHolder;
import com.autoStock.exchange.request.listener.MultipleRequestMarketScannerListener;
import com.autoStock.exchange.results.MultipleResultMarketScanner.MultipleResultSetMarketScanner;
import com.autoStock.index.IndexMarketDataProvider;
import com.autoStock.internal.ApplicationStates;
import com.autoStock.internal.Global;
import com.autoStock.order.OrderDefinitions.OrderMode;
import com.autoStock.position.PositionManager;
import com.autoStock.types.Exchange;
import com.autoStock.types.Index;

/**
 * @author Kevin Kowalewski
 * 
 */
public class MainEngagement implements MultipleRequestMarketScannerListener, ExchangeStatusListener {
	private Exchange exchange;
	private ExchangeStatusObserver exchangeStatusObserver;
	private ActiveAlgorithmManager activeAlgorithmManager = new ActiveAlgorithmManager();
	private MultipleRequestMarketScanner multipleRequestMarketScanner = new MultipleRequestMarketScanner(this);
	private IndexMarketDataProvider indexMarketDataProvider;

	public MainEngagement(Exchange exchange) {
		Global.callbackLock.requestLock();

		this.exchange = exchange;
		exchangeStatusObserver = new ExchangeStatusObserver(exchange);
		exchangeStatusObserver.addListener(this);
		exchangeStatusObserver.observeExchangeStatus();
		PositionManager.getGlobalInstance().orderMode = OrderMode.mode_exchange;
		
		activeAlgorithmManager.initalize();
	}

	private void engagementStart() {
		multipleRequestMarketScanner.addRequest(exchange, MarketScannerType.type_percent_gain_open);
//		multipleRequestMarketScanner.addRequest(exchange, MarketScannerType.type_percent_gain);
//		multipleRequestMarketScanner.addRequest(exchange, MarketScannerType.type_high_open_gap);
//		multipleRequestMarketScanner.addRequest(exchange, MarketScannerType.type_implied_volatility_gain);
//		multipleRequestMarketScanner.addRequest(exchange, MarketScannerType.type_hot_by_price);
		multipleRequestMarketScanner.addRequest(exchange, MarketScannerType.type_most_active);
//		multipleRequestMarketScanner.addRequest(exchange, MarketScannerType.type_top_trade_rate);
//		multipleRequestMarketScanner.addRequest(exchange, MarketScannerType.type_hot_by_volume);
		
		if (exchange.name.equals("NYSE")){indexMarketDataProvider = new IndexMarketDataProvider(exchange, new Index("INDU"));}
		multipleRequestMarketScanner.startScanners();
	}
	
	private void engagementWarn(ExchangeState exchangeState){
		Co.println("--> Received warning, time: " + exchangeState.timeUntilFuture.hours + ":" + exchangeState.timeUntilFuture.minutes + ":" + exchangeState.timeUntilFuture.seconds);
		if (exchangeState == ExchangeState.status_close_future && exchangeState.timeUntilFuture.hours == 0 && exchangeState.timeUntilFuture.minutes <= ExternalConditionDefintions.maxScannerExitMinutes){
			multipleRequestMarketScanner.stopScanner();
		}
		activeAlgorithmManager.warnAll(exchangeState);
	}
	
	private void engagementStop(){
		Co.println("--> Received stop");
		ArrayList<ArrayList<String>> listOfAlgorithmManagerRows = (ArrayList<ArrayList<String>>) activeAlgorithmManager.getAlgorithmTable().clone();
		if (indexMarketDataProvider != null){indexMarketDataProvider.cancel();}
		activeAlgorithmManager.stopAll();
		activeAlgorithmManager.displayEndOfDayStats(listOfAlgorithmManagerRows);
		Global.callbackLock.releaseLock();
		ApplicationStates.shutdown();
	}

	public synchronized void handleCompletedMarketScanner(MultipleResultSetMarketScanner multipleResultSetMarketScanner) {
		ArrayList<String> listOfString = new ArrayList<String>();
//		algorithmManager.pruneListOfSymbols(listOfString, exchange);
		if (activeAlgorithmManager.setListOfSymbols(multipleResultSetMarketScanner.listOfMultipleResultRowMarketScanner, exchange) == false){
			multipleRequestMarketScanner.stopScanner();
		}
	}

	@Override
	public void failed(RequestHolder requestHolder) {

	}

	@Override
	public void stateChanged(ExchangeState exchangeState) {
		Co.println("--> Got new state: " + exchangeState.name());
		if (exchangeState == ExchangeState.status_open){
			engagementStart();
		}else if (exchangeState == ExchangeState.status_close_future || exchangeState == ExchangeState.status_open_future){
			engagementWarn(exchangeState);
		}else if (exchangeState == ExchangeState.status_closed){
			engagementStop();
		}
	}

	@Override
	public void completed(MultipleResultSetMarketScanner multipleResultSetMarketScanner) {
		handleCompletedMarketScanner(multipleResultSetMarketScanner);
	}
}
