package com.autoStock.account;

import java.util.ArrayList;

import com.autoStock.types.Symbol;
import com.google.gson.internal.Pair;

/**
 * @author Kevin Kowalewski
 *
 */
public class AccountProvider {
	public static final double defaultBalance = 100000;
	private static AccountProvider instance = new AccountProvider();
	private BasicAccount accountForGlobalAccess = new BasicAccount(defaultBalance);
	private ArrayList<Pair<Symbol, BasicAccount>> listOfAccount = new ArrayList<Pair<Symbol, BasicAccount>>();
	
	private AccountProvider(){
		accountForGlobalAccess.setBalance(defaultBalance);
	}
	
	public static AccountProvider getInstance(){
		return instance;
	}
	
	public BasicAccount getAccount(Symbol symbol){
		synchronized (listOfAccount){
			for (Pair<Symbol, BasicAccount> pair : listOfAccount){
				if (pair.first.name.equals(symbol.name)){
					return pair.second;
				}
			}
		}
			
		return null;
	}
	
	public BasicAccount getGlobalAccount(){
		return accountForGlobalAccess;
	}
	
	public void reset(){
		
	}

	public synchronized BasicAccount newAccountForSymbol(Symbol symbol) {
		BasicAccount basicAccount = new BasicAccount(defaultBalance);
		if (getAccount(symbol) == null){listOfAccount.add(new Pair<Symbol, BasicAccount>(symbol, basicAccount));}
		return basicAccount;
	}
}
