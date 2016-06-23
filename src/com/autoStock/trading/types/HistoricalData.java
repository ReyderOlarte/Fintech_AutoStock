package com.autoStock.trading.types;

import java.util.Date;

import com.autoStock.internal.ApplicationStates;
import com.autoStock.tools.DateTools;
import com.autoStock.trading.platform.ib.definitions.HistoricalDataDefinitions.Resolution;
import com.autoStock.types.Exchange;
import com.autoStock.types.Symbol;
import com.rits.cloning.Cloner;

/**
 * @author Kevin Kowalewski
 *
 */
public class HistoricalData implements Cloneable {
	public Exchange exchange;
	public Symbol symbol;
	public Date startDate;
	public Date endDate;
	public Resolution resolution;
	public long duration;
	
	public HistoricalData(Exchange exchange, Symbol symbol, Date startDate, Date endDate, Resolution resolution){
		this.exchange = exchange;
		this.symbol = symbol;
		this.startDate = startDate;
		this.endDate = endDate;
		this.resolution = resolution;
		
		if (startDate != null && endDate != null){
			this.duration = (endDate.getTime() / 1000 - startDate.getTime() / 1000);
		}
	}
	
	public HistoricalData setStartAndEndDatesToExchange(){
		startDate.setHours(exchange.timeOpenForeign.hours);
		startDate.setMinutes(exchange.timeOpenForeign.minutes);
		endDate.setHours(exchange.timeCloseForeign.hours);
		endDate.setMinutes(exchange.timeCloseForeign.minutes);
		
		this.duration = (endDate.getTime() / 1000 - startDate.getTime() / 1000);
		
		return this;
	}
	
	public HistoricalData copy(){
		return new Cloner().deepClone(this);
	}
	
	@Override
	public String toString() {
		try {
			return exchange.name + " : " + symbol.name + ", " + DateTools.getPretty(startDate) + " to " + DateTools.getPretty(endDate);
		}catch(Exception e){return super.toString();}
	}
}
