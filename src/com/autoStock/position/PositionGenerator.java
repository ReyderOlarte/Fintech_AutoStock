/**
 * 
 */
package com.autoStock.position;

import com.autoStock.Co;
import com.autoStock.account.BasicAccount;
import com.autoStock.position.PositionDefinitions.PositionType;
import com.autoStock.signal.Signaler;
import com.autoStock.trading.types.Position;
import com.autoStock.types.Exchange;
import com.autoStock.types.QuoteSlice;

/**
 * @author Kevin Kowalewski
 *
 */
public class PositionGenerator {
	private boolean throwOnInsufficientBalance = false;
	private static final int entryPositionFunding = 10000;
	private static final int reentryPositionFunding = 2000;
	
	public Position generatePosition(QuoteSlice quoteSlice, Signaler signal, PositionType positionType, Exchange exchange, PositionOptions positionOptions, BasicAccount basicAccount, PositionManager positionManager){
		int positionUnits = getPositionInitialUnits(quoteSlice.priceClose, signal, basicAccount);
		
		if (positionUnits != 0){
			return new Position(positionType, positionUnits, quoteSlice.symbol, exchange, quoteSlice.priceClose, positionOptions, basicAccount, quoteSlice.dateTime, positionManager);
		}
		
		return null;
	}
	
	private int getPositionInitialUnits(double price, Signaler signal, BasicAccount basicAccount){
		double accountBalance = basicAccount.getBalance();
		int units = (int) (entryPositionFunding / price);

		if (accountBalance <= 0 || accountBalance < units * price){
			Co.println("Insufficient account blanace for trade");
			if (throwOnInsufficientBalance){
				throw new IllegalStateException();
			}
			return 0;
		}
		
		return units;
	}
	
	public int getPositionReentryUnits(double price, Signaler signal, BasicAccount basicAccount){
		double accountBalance = basicAccount.getBalance();
		int units = (int) (reentryPositionFunding / price);

		if (accountBalance <= 0 || accountBalance < units * price){
			Co.println("Insufficient account blanace for trade");
			if (throwOnInsufficientBalance){
				throw new IllegalStateException();
			}
			return 0;
		}
		
		return units;
	}
}
