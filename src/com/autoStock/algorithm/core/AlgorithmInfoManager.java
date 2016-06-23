package com.autoStock.algorithm.core;

import java.util.ArrayList;
import java.util.Date;

import com.autoStock.types.Exchange;
import com.autoStock.types.QuoteSlice;
import com.autoStock.types.Symbol;

/**
 * @author Kevin Kowalewski
 *
 */
public class AlgorithmInfoManager {
	public ArrayList<AlgorithmInfo> listOfAlgorithmInfo = new ArrayList<AlgorithmInfo>();

	public void activatedSymbol(Date date, Symbol symbol, Exchange exchange){
		listOfAlgorithmInfo.add(new AlgorithmInfo(date, symbol, exchange));
	}
	
	public void deactivatedSymbol(Symbol symbol, ArrayList<QuoteSlice> listOfQuoteSlice){
		for (AlgorithmInfo algorithmInfo : listOfAlgorithmInfo){
			if (algorithmInfo.symbol.equals(symbol)){
				algorithmInfo.dateDeactivated = new Date();
				algorithmInfo.listOfQuoteSlice = listOfQuoteSlice;
			}
		}
	}
	
	public static class AlgorithmInfo{
		public Date dateActivated;
		public Date dateDeactivated;
		public Symbol symbol;
		public Exchange exchange;
		public ArrayList<QuoteSlice> listOfQuoteSlice;
		
		public AlgorithmInfo(Date arrivalDate, Symbol symbol, Exchange exchange){
			this.dateActivated = arrivalDate;
			this.symbol = symbol;
		}
	}
}
