package com.autoStock.trading.yahoo;

import org.apache.http.HttpEntity;
import org.apache.http.util.EntityUtils;

import com.autoStock.network.HTTPRequestBase;
import com.autoStock.types.Exchange;
import com.autoStock.types.Symbol;

/**
 * @author Kevin Kowalewski
 *
 */
public class YahooFundamentals extends HTTPRequestBase {
	private Exchange exchange;
	private Symbol symbol;
	private RequestFundamentalsListener requestFundamentalsListener;
	
	public YahooFundamentals(RequestFundamentalsListener requestFundamentalsListener, Exchange exchange, Symbol symbol){
		this.requestFundamentalsListener = requestFundamentalsListener;
		this.exchange = exchange;
		this.symbol = symbol;
	}
	
	public void execute(){
		String requestString;
		
		if (exchange.name.equals("NYSE") || exchange.name.equals("NASDAQ")){
			requestString = "http://finance.yahoo.com/d/quotes.csv?s="+ symbol.name.replaceAll(" ", "-") + "&f=sa2vdyehgj1rs7";
		}else if (exchange.name.equals("ASX")){
			requestString = "http://finance.yahoo.com/d/quotes.csv?s="+ symbol.name.replaceAll(" ", "-") + ".AX&f=sa2vdyehgj1rs7";
		}else{
			throw new IllegalStateException("Yahoo fundamentals doesn't know how to handle exchange: " + exchange.name);
		}
		
		sendHttpGetRequest(requestString);
	}

	@Override
	public void receivedResponse(HttpEntity httpEntity) {
		try {
			String response = EntityUtils.toString(httpEntity);
//			Co.println("--> Response: " + response);
			
			String[] responseElements = response.trim().split(",");
			
			FundamentalData fundamentalData = new FundamentalData(exchange, symbol, 
				Integer.valueOf(checkNA(responseElements[1])),
				Integer.valueOf(checkNA(responseElements[2])),
				Double.valueOf(checkNA(responseElements[3])),
				Double.valueOf(checkNA(responseElements[4])),
				Double.valueOf(checkNA(responseElements[5])),
				Double.valueOf(checkNA(responseElements[6])),
				Double.valueOf(checkNA(responseElements[7])),
				yahooNumberToInteger(checkNA(responseElements[8])),
				Double.valueOf(checkNA(responseElements[9])),
				Double.valueOf(checkNA(responseElements[10]))
			);
			
			if (fundamentalData.avgDailyVolume == 0){
				requestFundamentalsListener.failed(null);
			}else{
				requestFundamentalsListener.success(fundamentalData);
			}
			
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	@Override
	public void receivedFaultResponse(HttpFaultResponse httpFaultResponse) {
		throw new UnsupportedOperationException("Http fault: " + httpFaultResponse.name());
	}
	
	private String checkNA(String value){
		if (value.equals("N/A")){
			return "0";
		}else{
			return value;
		}
	}
	
	private int yahooNumberToInteger(String value){
		if (value.contains("B")){
			return (int) (Double.valueOf(value.replace("B", ""))*1000000000);
		}else if (value.contains("M")){
			return (int) (Double.valueOf(value.replace("M", ""))*1000000);
		}else{
			return (int) (Double.valueOf(value)*1);
		}
	}

	public void cancel() {
		try {
			httpGet.abort();
			thread.interrupt();
		}catch(Exception e){}
	}
}
