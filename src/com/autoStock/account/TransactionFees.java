package com.autoStock.account;

import com.autoStock.tools.MathTools;

/**
 * @author Kevin Kowalewski
 *
 */
public class TransactionFees {
	public static double getTransactionCost(int units, double price) { // TODO: add exchange and security type
		double cost = 0;
		if (units <= 500) {
			cost = units * 0.013;
		} else {
			cost = (500 * 0.013) + ((units - 500) * 0.008);
		}

		return MathTools.round(Math.max(1.30, cost));
	}
}
