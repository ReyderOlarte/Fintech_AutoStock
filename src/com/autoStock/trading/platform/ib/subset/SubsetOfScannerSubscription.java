package com.autoStock.trading.platform.ib.subset;

import com.autoStock.exchange.ExchangeDefinitions.ExchangeDesignation;
import com.autoStock.exchange.request.RequestMarketScanner.MarketScannerType;
import com.autoStock.trading.platform.ib.core.ScannerSubscription;
import com.autoStock.types.Exchange;

/**
 * @author Kevin Kowalewski
 * 
 */
public class SubsetOfScannerSubscription {
	public static final int maxResults = 25;

	public ScannerSubscription getScanner(Exchange exchange, MarketScannerType marketScannerType) {
		ScannerSubscription scannerSubscription = new ScannerSubscription();

		scannerSubscription.stockTypeFilter("ALL");
		scannerSubscription.averageOptionVolumeAbove(0);
		scannerSubscription.aboveVolume(200000);

		if (exchange.exchangeDesignation == ExchangeDesignation.NYSE) {
			scannerSubscription.instrument("STK");
			scannerSubscription.locationCode("STK.NYSE");
			scannerSubscription.abovePrice(2.00);
			scannerSubscription.belowPrice(50.00);
		} else if (exchange.exchangeDesignation == ExchangeDesignation.NASDAQ){
			scannerSubscription.instrument("STK");
			scannerSubscription.locationCode("STK.NASDAQ");
			scannerSubscription.abovePrice(2.00);
			scannerSubscription.belowPrice(100.00);
		} else if (exchange.exchangeDesignation == ExchangeDesignation.ASX) {
			scannerSubscription.instrument("STOCK.HK");
			scannerSubscription.locationCode("STK.HK.ASX");
			scannerSubscription.abovePrice(2.00);
			scannerSubscription.belowPrice(100.00);
		} else if (exchange.exchangeDesignation == ExchangeDesignation.TSEJ) {
			scannerSubscription.instrument("STOCK.HK");
			scannerSubscription.locationCode("STK.HK.TSE_JPN");
		} else {
			throw new UnsupportedOperationException();
		}

		modifyScannerWithType(scannerSubscription, marketScannerType);

		return scannerSubscription;
	}

	private void modifyScannerWithType(ScannerSubscription scannerSubscription, MarketScannerType marketScannerType) {
		if (marketScannerType == MarketScannerType.type_percent_gain_open) {
			scannerSubscription.scanCode("TOP_OPEN_PERC_GAIN");
			scannerSubscription.aboveVolume(100000);
			scannerSubscription.numberOfRows(30);
		}

		else if (marketScannerType == MarketScannerType.type_percent_gain) {
			scannerSubscription.scanCode("TOP_PERC_GAIN");
			scannerSubscription.aboveVolume(100000);
			scannerSubscription.numberOfRows(30);
		}

		else if (marketScannerType == MarketScannerType.type_high_open_gap) {
			scannerSubscription.scanCode("HIGH_OPEN_GAP");
			scannerSubscription.aboveVolume(100000);
			scannerSubscription.numberOfRows(30);
		}
		
		else if (marketScannerType == MarketScannerType.type_implied_volatility_gain) {
			scannerSubscription.scanCode("TOP_OPT_IMP_VOLAT_GAIN");
			scannerSubscription.aboveVolume(100000);
			scannerSubscription.numberOfRows(30);
		}

		else if (marketScannerType == MarketScannerType.type_top_trade_rate) {
			scannerSubscription.scanCode("TOP_TRADE_RATE");
			scannerSubscription.aboveVolume(100000);
			scannerSubscription.numberOfRows(30);
		}

		else if (marketScannerType == MarketScannerType.type_most_active) {
			scannerSubscription.scanCode("MOST_ACTIVE_USD");
			scannerSubscription.aboveVolume(100000);
			scannerSubscription.numberOfRows(30);
		}

		else if (marketScannerType == MarketScannerType.type_hot_by_price) {
			scannerSubscription.scanCode("HOT_BY_PRICE");
			scannerSubscription.aboveVolume(100000);
			scannerSubscription.numberOfRows(30);
		} 
		
		else if (marketScannerType == MarketScannerType.type_hot_by_volume) {
			scannerSubscription.scanCode("HOT_BY_VOLUME");
			scannerSubscription.aboveVolume(100000);
			scannerSubscription.numberOfRows(30);
		} 
		
		else {
			throw new UnsupportedOperationException();
		}
	}
}
