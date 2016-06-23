/**
 * 
 */
package com.autoStock.signal;

import java.util.ArrayList;
import java.util.Date;

import com.autoStock.types.Exchange;
import com.autoStock.types.Symbol;

/**
 * @author Kevin
 *
 */
public class SignalCachePackage {
	public Symbol symbol;
	public Exchange exchange;
	public Date dateStart;
	public ArrayList<SignalBase> listOfListOfSignalBase;
	
	public SignalCachePackage() {}
	
	public SignalCachePackage(Symbol symbol, Exchange exchange, Date dateStart, ArrayList<SignalBase> listOfSignalBase) {
		this.symbol = symbol;
		this.exchange = exchange;
		this.dateStart = dateStart;
		this.listOfListOfSignalBase = listOfSignalBase;
	}
}
