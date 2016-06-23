package com.autoStock.generated.basicDefinitions;

import java.util.Date;
import com.autoStock.types.basic.Time;
import com.autoStock.trading.platform.ib.definitions.HistoricalDataDefinitions.Resolution;
public class TableDefinitions {

	public static class DbBacktestResult {
		public long id;
		public Date dateStart;
		public Date dateEnd;
		public Date dateRun;
		public String exchange;
		public String symbol;
		public double balanceInBand;
		public double balanceOutBand;
		public double percentGainInBand;
		public double percentGainOutBand;
		public int tradeEntry;
		public int tradeReentry;
		public int tradeExit;
		public int tradeWins;
		public int tradeLoss;
		public int gsonId;
	}

	public static DbBacktestResult dbBacktestResult = new TableDefinitions.DbBacktestResult();

	public static class DbBacktestValue {
		public int id;
		public int backtestId;
		public String field;
		public String value;
		public boolean adjusted;
	}

	public static DbBacktestValue dbBacktestValue = new TableDefinitions.DbBacktestValue();

	public static class DbExchange {
		public long id;
		public String exchange;
		public String currency;
		public Time timeOpen;
		public Time timeClose;
		public Time timeOffset;
		public String timeZone;
	}

	public static DbExchange dbExchange = new TableDefinitions.DbExchange();

	public static class DbGson {
		public int id;
		public String gsonString;
	}

	public static DbGson dbGson = new TableDefinitions.DbGson();

	public static class DbMarketOrder {
		public long id;
		public String symbol;
		public String orderType;
		public int quantity;
		public double priceLimit;
		public double priceStop;
		public Date goodAfterDate;
		public Date goodUntilDate;
		public double priceAverageFill;
	}

	public static DbMarketOrder dbMarketOrder = new TableDefinitions.DbMarketOrder();

	public static class DbReplay {
		public int id;
		public String exchange;
		public String symbol;
		public Date dateTimeActivated;
		public Date dateTimeDeactivated;
		public double profitLoss;
	}

	public static DbReplay dbReplay = new TableDefinitions.DbReplay();

	public static class DbStockHistoricalPrice {
		public long id;
		public String symbol;
		public String exchange;
		public Resolution resolution;
		public double priceOpen;
		public double priceHigh;
		public double priceLow;
		public double priceClose;
		public int sizeVolume;
		public Date dateTime;
	}

	public static DbStockHistoricalPrice dbStockHistoricalPrice = new TableDefinitions.DbStockHistoricalPrice();

	public static class DbSymbol {
		public long id;
		public String symbol;
		public String exchange;
		public String description;
	}

	public static DbSymbol dbSymbol = new TableDefinitions.DbSymbol();

	public static class DbWhitelist {
		public int id;
		public String symbol;
		public String exchange;
		public Date dateLastAdjustment;
		public int adjustmentId;
		public String reason;
	}

	public static DbWhitelist dbWhitelist = new TableDefinitions.DbWhitelist();

}
