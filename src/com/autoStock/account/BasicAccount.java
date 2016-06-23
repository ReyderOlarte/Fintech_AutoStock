package com.autoStock.account;

import java.util.concurrent.atomic.AtomicInteger;

import com.autoStock.tools.MathTools;
import com.google.common.util.concurrent.AtomicDouble;
import com.rits.cloning.Cloner;

/**
 * @author Kevin Kowalewski
 *
 */
public class BasicAccount {
	private AtomicInteger transactions = new AtomicInteger();
	private AtomicDouble accountBalance = new AtomicDouble();
	private AtomicDouble transactionFeesPaid = new AtomicDouble();
	
	public BasicAccount(double balance){
		accountBalance.set(balance);
	}
	
	public double getBalance(){
		return accountBalance.get();
	}
	
	public int getTransactions(){
		return transactions.get();
	}
	
	public void modifyBalance(double amount){
		accountBalance.addAndGet(amount);
	}
	
	public void modifyBalance(double amount, double transactionCost){
		accountBalance.addAndGet(amount);
		accountBalance.addAndGet(transactionCost * -1);
		transactionFeesPaid.addAndGet(transactionCost);
		transactions.incrementAndGet();
	}
	
	public void reset(){
		accountBalance.set(AccountProvider.defaultBalance);
		transactionFeesPaid.set(0);
		transactions.set(0);
	}

	public void setBalance(double balance) {
		accountBalance.set(balance);
	}

	public double getTransactionFees() {
		return MathTools.round(transactionFeesPaid.get());
	}

	public BasicAccount copy() {
		return new Cloner().deepClone(this);
	}
}
