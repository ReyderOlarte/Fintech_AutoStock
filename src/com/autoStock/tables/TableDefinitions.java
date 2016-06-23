package com.autoStock.tables;


/**
 * @author Kevin Kowalewski
 *
 */
public class TableDefinitions {
	public static enum AsciiTables{
		stock_historical_price_live(new AsciiColumns[]{AsciiColumns.symbol, AsciiColumns.dateTime, AsciiColumns.price, AsciiColumns.volume, AsciiColumns.sizeCount, AsciiColumns.change}), //, Columns.sizeVolume
		stock_historical_price_db(new AsciiColumns[]{AsciiColumns.id, AsciiColumns.symbol, AsciiColumns.priceOpen, AsciiColumns.priceHigh, AsciiColumns.priceLow, AsciiColumns.priceClose, AsciiColumns.volume, AsciiColumns.dateTime}),
		analysis_test(new AsciiColumns[]{AsciiColumns.dateTime, AsciiColumns.priceClose, AsciiColumns.change, AsciiColumns.change, AsciiColumns.signal , AsciiColumns.PPC, AsciiColumns.ADX, AsciiColumns.CCI, AsciiColumns.BBUpper, AsciiColumns.BBLower, AsciiColumns.MACDHistogram, AsciiColumns.STORSIK, AsciiColumns.STORISD}),
		algorithm(new AsciiColumns[]{AsciiColumns.dateTime, AsciiColumns.volume, AsciiColumns.price, AsciiColumns.change, AsciiColumns.DI, AsciiColumns.UO, AsciiColumns.CCI, AsciiColumns.RSI, AsciiColumns.STORSI, AsciiColumns.MACD, AsciiColumns.TRIX, AsciiColumns.ROC, AsciiColumns.MFI, AsciiColumns.WILLR, AsciiColumns.positionGovernor, AsciiColumns.strategy, AsciiColumns.signalPoint, AsciiColumns.signalMetric, AsciiColumns.transactionDetails, AsciiColumns.profitLoss, AsciiColumns.bankBalance}),
		algorithm_no_signals(new AsciiColumns[]{AsciiColumns.dateTime, AsciiColumns.volume, AsciiColumns.price, AsciiColumns.change, AsciiColumns.positionGovernor, AsciiColumns.strategy, AsciiColumns.signalPoint, AsciiColumns.signalMetric, AsciiColumns.transactionDetails, AsciiColumns.profitLoss, AsciiColumns.bankBalance}),
		algorithm_manager(new AsciiColumns[]{AsciiColumns.dateTime, AsciiColumns.symbol, AsciiColumns.status, AsciiColumns.signal, AsciiColumns.strategy, AsciiColumns.position, AsciiColumns.priceVisible, AsciiColumns.priceEntered, AsciiColumns.priceClose, AsciiColumns.percentChange, AsciiColumns.percentChange, AsciiColumns.transactionDetails, AsciiColumns.signalMetric}),
		order_manager(new AsciiColumns[]{AsciiColumns.dateTime, AsciiColumns.symbol, AsciiColumns.orderType, AsciiColumns.orderStatus, AsciiColumns.orderUnitsRequested, AsciiColumns.orderUnitsRemaining, AsciiColumns.orderUnitsFilled, AsciiColumns.orderPriceRequested, AsciiColumns.orderPriceFilledAvg, AsciiColumns.orderPriceFilledLast}),
		backtest_strategy_response(new AsciiColumns[]{AsciiColumns.dateTime, AsciiColumns.symbol, AsciiColumns.price, AsciiColumns.strategy, AsciiColumns.positionGovernor, AsciiColumns.signal, AsciiColumns.transactionDetails, AsciiColumns.profitLoss, AsciiColumns.bankBalance}),
		;
		
		public AsciiColumns[] arrayOfColumns;
		
		AsciiTables (AsciiColumns[] arrayOfColumns){
			this.arrayOfColumns = arrayOfColumns;
		}
		
		public AsciiTables injectColumns(AsciiColumns... columns){
			AsciiColumns[] tempArrayOfColumns = new AsciiColumns[arrayOfColumns.length + columns.length];
	
			int i = 0;
			for (AsciiColumns column : arrayOfColumns){
				tempArrayOfColumns[i] = column;
				i++;
			}
			
			for (AsciiColumns column : columns){
				tempArrayOfColumns[i] = column;
				i++;
			}
			
			arrayOfColumns = tempArrayOfColumns;
			
			return this;
		}
		
		public boolean containsColumn(AsciiColumns column){
			for (AsciiColumns asciiColumn : this.arrayOfColumns){
				if (asciiColumn == column){
					return true;
				}
			}
			return false;
		}
	}
	
	public static enum AsciiColumns {
		id,
		symbol,
		orderType,
		quantity,
		priceLimit,
		priceStop,
		priceOpen,
		priceHigh,
		priceLow,
		priceClose,
		goodAfterDate,
		goodUntilDate,
		priceAverageFill,
		exchange,
		currency,
		securityType,
		price,
		priceVisible,
		priceEntered,
		sizeBid,
		sizeAsk,
		sizeLast,
		volume,
		dateTime,
		change,
		sizeCount,
		PPC,
		ADX,
		BBUpper,
		BBLower,
		MACDHistogram,
		STORSIK,
		STORISD,
		signal,
		DI,
		UO,
		CCI,
		RSI,
		MACD,
		STORSI,
		TRIX,
		ROC,
		MFI,
		WILLR,
		inducedAction,
		bankBalance,
		transactionDetails,
		peakDetect,
		position,
		percentChange,
		positionGovernor,
		strategy,
		signalPoint,
		signalMetric,
		profitLoss, 
		debug, 
		orderUnitsRequested,
		orderUnitsRemaining,
		orderUnitsFilled, 
		orderPriceRequested, 
		orderPriceFilledAvg,
		orderPriceFilledLast,
		orderStatus,
		status,
	}
}
