/**
 * 
 */
package com.autoStock.finance;

import java.util.concurrent.atomic.AtomicInteger;

import com.autoStock.tools.MathTools;
import com.google.common.util.concurrent.AtomicDouble;

/**
 * @author Kevin Kowalewski
 * 
 */
public class Account {
	private static Account instance = new Account();
//	public final double bankBalanceDefault = 50000; 
	public final double bankBalanceDefault = 1000000;
	private final double transactionFeesDefault = 0;
	private AtomicDouble bankBalance = new AtomicDouble();
	private AtomicDouble transactionFeesPaid = new AtomicDouble();
	private AtomicInteger transactions = new AtomicInteger();

	public Account() {
		resetAccount();
	}

	public static Account getInstance() {
		return instance;
	}

	public double getAccountBalance() {
		synchronized (this) {
			return MathTools.round(bankBalance.get());
		}
	}

	public double getTransactionFeesPaid() {
		synchronized (this) {
			return MathTools.round(transactionFeesPaid.get());
		}
	}

	public int getTransactions() {
		synchronized (this) {
			return transactions.get();
		}
	}

	public void changeAccountBalance(double positionCost, double transactionCost) {
		synchronized (this) {
			// Co.println("--> Changing account balance by: " + positionCost + ", " + transactionCost + ", " + (positionCost + transactionCost));

			bankBalance.addAndGet(positionCost);
			bankBalance.addAndGet(transactionCost * -1);
			transactionFeesPaid.getAndAdd(transactionCost);
			transactions.incrementAndGet();
		}
	}

	public static double getTransactionCost(int units, double price) { // TODO: add exchange and security type
		double cost = 0;
		if (units <= 500) {
//			cost = Math.max(1.30, units * 0.013);
			cost = (1.30 >= units * 0.013) ? 1.30 : units * 0.013;
		} else {
			cost = (500 * 0.013) + ((units - 500) * 0.008);
		}

//		return Math.min(cost, units * price * 0.005);
		return (cost <= units * price * 0.005) ? cost : units * price * 0.005;
	}

	public void resetAccount() {
		bankBalance.set(bankBalanceDefault);
		transactionFeesPaid.set(transactionFeesDefault);
		transactions.set(0);
	}
}
