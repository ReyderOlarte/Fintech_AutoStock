/**
 * 
 */
package com.autoStock.premise;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

import com.autoStock.Co;
import com.autoStock.backtest.BacktestUtils;
import com.autoStock.backtest.BacktestUtils.LookDirection;
import com.autoStock.database.DatabaseQuery;
import com.autoStock.database.DatabaseDefinitions.BasicQueries;
import com.autoStock.database.DatabaseDefinitions.QueryArg;
import com.autoStock.database.DatabaseDefinitions.QueryArgs;
import com.autoStock.exchange.request.RequestHistoricalData;
import com.autoStock.exchange.request.base.RequestHolder;
import com.autoStock.exchange.request.listener.RequestHistoricalDataListener;
import com.autoStock.exchange.results.ExResultHistoricalData.ExResultSetHistoricalData;
import com.autoStock.generated.basicDefinitions.TableDefinitions.DbStockHistoricalPrice;
import com.autoStock.internal.ApplicationStates;
import com.autoStock.signal.extras.EncogFrame;
import com.autoStock.signal.extras.EncogFrame.FrameType;
import com.autoStock.signal.extras.EncogFrameSupport;
import com.autoStock.signal.extras.EncogFrameSupport.EncogFrameSource;
import com.autoStock.signal.extras.EncogSubframe;
import com.autoStock.tools.DateTools;
import com.autoStock.tools.MathTools;
import com.autoStock.tools.QuoteSliceTools;
import com.autoStock.trading.platform.ib.definitions.HistoricalDataDefinitions.Resolution;
import com.autoStock.trading.types.HistoricalData;
import com.autoStock.types.Exchange;
import com.autoStock.types.QuoteSlice;
import com.autoStock.types.Symbol;

/**
 * @author Kevin
 * So hacky but meh
 *
 */
public class PremiseOfOHLC extends PremiseBase implements EncogFrameSource {
	public Exchange exchange;
	public Symbol symbol;
	public Date dateStart;
	public int days;
	public int duration;
	public int expectedQuotes;
	public Resolution resolution;
	public ArrayList<QuoteSlice> listOfQuotes = new ArrayList<QuoteSlice>();
	
	public PremiseOfOHLC(Exchange exchange, Symbol symbol, Date dateStart, Resolution resolution, int days) {
		this.exchange = exchange;
		this.symbol = symbol;
		this.dateStart = dateStart;
		this.resolution = resolution;
		this.days = days;
		
		duration =  (int) (new HistoricalData(exchange, symbol, (Date)dateStart.clone(), (Date)dateStart.clone(), resolution).setStartAndEndDatesToExchange().duration / 60) * days;
		
		if (resolution != Resolution.day){
			expectedQuotes = duration / resolution.asMinutes();
		}else if (resolution == Resolution.day){
			expectedQuotes = days; 
		}
		
		//Co.println("--> Days: " + days + " at resolution " + resolution.asMinutes());
		//Co.println("--> Duration: " + duration);
		//Co.println("--> Expected quotes: " + expectedQuotes);
	}

	@Override
	public void run(){
		populate();
	}
	
	private void populate(){
		ArrayList<HistoricalData> list = BacktestUtils.getHistoricalDataListForDates(new HistoricalData(exchange, symbol, dateStart, null, resolution), LookDirection.backward, days + 5);
		
		external_loop : for (HistoricalData historicalData : list){
			//Co.println("--> Have day: " + historicalData.startDate.toString());
			
			if (resolution == Resolution.day){
				historicalData.startDate = DateTools.getSameDateMinTime(historicalData.startDate);
				historicalData.endDate = DateTools.getSameDateMaxTime(historicalData.endDate);
			}
			
			ArrayList<DbStockHistoricalPrice> listOfResults = (ArrayList<DbStockHistoricalPrice>) new DatabaseQuery().getQueryResults(BasicQueries.basic_historical_price_range, new QueryArg(QueryArgs.symbol, historicalData.symbol.name), new QueryArg(QueryArgs.exchange, historicalData.exchange.name), new QueryArg(QueryArgs.resolution, historicalData.resolution.asMinutes()), new QueryArg(QueryArgs.startDate, DateTools.getSqlDate(historicalData.startDate)), new QueryArg(QueryArgs.endDate, DateTools.getSqlDate(historicalData.endDate)));
			//Co.println(" " + listOfResults.size());
			
			for (DbStockHistoricalPrice price : listOfResults){
				QuoteSlice quoteSlice = new QuoteSlice(symbol, price.priceOpen, price.priceHigh, price.priceLow, price.priceClose, 0, 0, price.sizeVolume, price.dateTime, resolution);
				listOfQuotes.add(quoteSlice);
				
				if (listOfQuotes.size() == expectedQuotes){
					break external_loop;
				}
			}
		}
		
		//Co.println("--> Have expected, results: " + expectedQuotes + ", " + listOfQuotes.size());
	}

	@Override
	public EncogFrame asEncogFrame() { //Trying as deltas
		EncogFrame encogFrame = new EncogFrame("OHLC for: " + symbol.name + ", " + DateTools.getPretty(dateStart) + ", " + resolution.name(), FrameType.percent_change);
		ArrayList<Double> values = new ArrayList<Double>();
		ArrayList<Double> valueOpen = new ArrayList<Double>();
		ArrayList<Double> valueHigh = new ArrayList<Double>();
		ArrayList<Double> valueLow = new ArrayList<Double>();
		ArrayList<Double> valueClose = new ArrayList<Double>();

		
		for (QuoteSlice quote : listOfQuotes){
			valueOpen.add(quote.priceOpen);
			valueHigh.add(quote.priceHigh);
			valueLow.add(quote.priceLow);
			valueClose.add(quote.priceClose);
		}
		
		valueOpen = MathTools.getDeltasAsPercent(valueOpen);
		valueHigh = MathTools.getDeltasAsPercent(valueHigh);
		valueLow = MathTools.getDeltasAsPercent(valueLow);
		valueClose = MathTools.getDeltasAsPercent(valueClose);
		
		for (int i=1; i<listOfQuotes.size(); i++){ //Purposely remove first as its always zero
			values.add(valueOpen.get(i));
			values.add(valueHigh.get(i));
			values.add(valueLow.get(i));
			values.add(valueClose.get(i));
		}
		
		encogFrame.addSubframe(new EncogSubframe(this.getClass().getSimpleName(), values, FrameType.percent_change, 10, -10));
		return encogFrame;
	}
}
