package com.autoStock.algorithm.core;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;

import com.autoStock.Co;
import com.autoStock.account.AccountProvider;
import com.autoStock.algorithm.core.ActiveAlgorithmContainer.ActivationListener;
import com.autoStock.exchange.ExchangeStatusListener.ExchangeState;
import com.autoStock.exchange.results.MultipleResultMarketScanner.MultipleResultRowMarketScanner;
import com.autoStock.finance.SecurityTypeHelper.SecurityType;
import com.autoStock.position.PositionManager;
import com.autoStock.replay.ReplayController;
import com.autoStock.tables.TableController;
import com.autoStock.tables.TableDefinitions.AsciiTables;
import com.autoStock.trading.platform.ib.definitions.HistoricalDataDefinitions.Resolution;
import com.autoStock.types.Exchange;
import com.autoStock.types.Symbol;
import com.google.gson.Gson;
import com.google.gson.internal.Pair;

/**
 * @author Kevin Kowalewski
 *
 */
public class ActiveAlgorithmManager implements ActivationListener {
	private ArrayList<ActiveAlgorithmContainer> listOfActiveAlgorithmContainer = new ArrayList<ActiveAlgorithmContainer>();
	private ArrayList<Pair<Symbol,Exchange>> listOfDiscards = new ArrayList<Pair<Symbol,Exchange>>();
	private AlgorithmInfoManager algorithmInfoManager = new AlgorithmInfoManager();
	private ActiveAlgorithmManagerTable activeAlgorithmManagerTable = new ActiveAlgorithmManagerTable();
	private Thread threadForDisplay;
	
	public void initalize() {
		threadForDisplay = new Thread(new Runnable(){
			@Override
			public void run() {
				try{Thread.sleep(1000 * 5);}catch(InterruptedException e){}
				while (true){
					try{Thread.sleep(1000 * Resolution.min.asSeconds());}catch(InterruptedException e){return;}
					displayAlgorithmTable();
				}
			}
		});
		
		threadForDisplay.start();
	}
	
	public boolean setListOfSymbols(ArrayList<Symbol> listOfSymbols, Exchange exchange, String additionSource){
		for (Symbol symbol : listOfSymbols){
			if (listOfActiveAlgorithmContainer.size() >= 100){
				Co.println("--> Reached market data concurrent request limit. Not adding symbol: " + symbol);
				return false;
			}else if (getAlgorithmContainerForSymbol(symbol.name, exchange.name) == null && isInDiscard(new Pair<Symbol,Exchange>(symbol, exchange)) == false){
				Co.println("Will run algorithm for symbol: " + additionSource  + ", " + symbol.name);
			}
		}
		
		return true;
	}
	
	public boolean setListOfSymbols(ArrayList<MultipleResultRowMarketScanner> listOfMultipleResultRowMarketScanner, Exchange exchange){
		for (MultipleResultRowMarketScanner result : listOfMultipleResultRowMarketScanner){
			if (listOfActiveAlgorithmContainer.size() >= 100){
				Co.println("--> Reached market data concurrent request limit. Not adding symbol: " + result.marketScannerType.name() + ", " + result.symbol);
				return false;
			}else if (getAlgorithmContainerForSymbol(result.symbol, exchange.name) == null && isInDiscard(new Pair<Symbol,Exchange>(new Symbol(result.symbol, SecurityType.type_stock), exchange)) == false){
				Co.println("Will run algorithm for symbol: " + result.marketScannerType.name() + ", " + result.symbol);
				
				ActiveAlgorithmContainer container = new ActiveAlgorithmContainer(exchange, new Symbol(result.symbol, SecurityType.type_stock), result.marketScannerType.name(), this);
				container.activate();
			}
		}
		
		Co.println("Active algorithm count: " + listOfActiveAlgorithmContainer.size());
		Co.println("Discarded symbol count: " + listOfDiscards.size());
		
		return true;
	}
	
	public boolean isInDiscard(Pair<Symbol, Exchange> pair){
		for (Pair<Symbol,Exchange> pairLocal : listOfDiscards){
			if (pairLocal.first.name.equals(pair.first.name) && pairLocal.second.name.equals(pair.second.name)){
				return true;
			}
		}
		
		return false;
	}
	
	public void pruneListOfSymbols(ArrayList<String> listOfSymbols, Exchange exchange){
		for (Iterator<ActiveAlgorithmContainer> iterator = listOfActiveAlgorithmContainer.iterator(); iterator.hasNext();){
			ActiveAlgorithmContainer container = iterator.next();
			if (listOfSymbols.contains(container.symbol.name) == false){
				Co.println("--> No longer want: " + container.symbol.name);
				algorithmInfoManager.deactivatedSymbol(container.symbol, container.listOfQuoteSlice);
				container.deactivate();
				iterator.remove();
			}
		}
	}
	
	public void algorithmSweep(){
		for (ActiveAlgorithmContainer activeAlgorithmContainer : listOfActiveAlgorithmContainer){
			if (activeAlgorithmContainer.algorithm.algorithmState.isDisabled){
				activeAlgorithmContainer.deactivate();
			}
		}
	}
	
	public void warnAll(ExchangeState exchangeState){
		//No-op
	}
	
	public void stopAll(){
		Co.println("--> STOP ALL!!!");
		
		if (threadForDisplay != null){
			threadForDisplay.interrupt();
		}
		
		deactivateAll();
		ReplayController.writeToFile(algorithmInfoManager.listOfAlgorithmInfo);
		
		int count = 0;
		
		while (PositionManager.getGlobalInstance().getPositionListSize() > 0){
			try {Thread.sleep(1000);}catch(InterruptedException e){return;}
			Co.println("Position manager still has: " + PositionManager.getGlobalInstance().getPositionListSize() + " positions..." + count);
			if (count == 30){
				Co.println("Warning, position(s) still not exited");
				deactivateAll();
			}else if (count > 60){
				Co.println("Error, failed to exit all positions");
				return;
			}
		}
	}
	
	private void deactivateAll(){
		for (Iterator<ActiveAlgorithmContainer> iterator = listOfActiveAlgorithmContainer.iterator(); iterator.hasNext();){
			ActiveAlgorithmContainer container = iterator.next();
			container.deactivate();
			algorithmInfoManager.deactivatedSymbol(container.symbol, container.listOfQuoteSlice);
		}
	}
	
	private ActiveAlgorithmContainer getAlgorithmContainerForSymbol(String symbol, String exchange){
		for (ActiveAlgorithmContainer container : listOfActiveAlgorithmContainer){
			if (container.symbol.name.equals(symbol) && container.exchange.name.equals(exchange)){
				return container;
			}
		}
		
		return null;
	}
	
	public void displayAlgorithmTable(){
		new TableController().displayTable(AsciiTables.algorithm_manager, getAlgorithmTable());
		Co.println("--> Current entered position P&L: " + PositionManager.getGlobalInstance().getCurrentProfitLossBeforeComission() + " / " + PositionManager.getGlobalInstance().getCurrentProfitLossAfterComission(false));
		Co.println("--> Current fees paid: " + AccountProvider.getInstance().getGlobalAccount().getTransactionFees());
		Co.println("--> Current account balance: " + AccountProvider.getInstance().getGlobalAccount().getBalance());
		Co.println("--> All position value including fees: " + PositionManager.getGlobalInstance().getAllPositionValueIncludingFees()); 
		Co.println("--> Complete gain from starting account balance: $" + new DecimalFormat("#.###").format((AccountProvider.getInstance().getGlobalAccount().getBalance() + PositionManager.getGlobalInstance().getAllPositionValueIncludingFees()) - AccountProvider.getInstance().defaultBalance));
	}
	
	public ArrayList<ArrayList<String>> getAlgorithmTable(){
		activeAlgorithmManagerTable.clear();
		
		for (Iterator<ActiveAlgorithmContainer> iterator = listOfActiveAlgorithmContainer.iterator(); iterator.hasNext();){
			ActiveAlgorithmContainer container = iterator.next();
			
			activeAlgorithmManagerTable.addRow(container.algorithm, container.algorithm.listOfQuoteSlice);
		}
		
		return activeAlgorithmManagerTable.getListOfDisplayRows();
	}
	
	public void displayEndOfDayStats(ArrayList<ArrayList<String>> listOfAlgorithmDisplayRows){
		Co.println("--> Account balance, transactions, fees paid: " + AccountProvider.getInstance().getGlobalAccount().getBalance() + ", " + AccountProvider.getInstance().getGlobalAccount().getTransactions() + ", " + AccountProvider.getInstance().getGlobalAccount().getTransactionFees());
		new TableController().displayTable(AsciiTables.algorithm_manager, listOfAlgorithmDisplayRows);
		
		Co.println(new Gson().toJson(algorithmInfoManager.listOfAlgorithmInfo));
	}

	@Override
	public void activated(ActiveAlgorithmContainer activeAlgorithmContainer) {
		Co.println("--> Activated: " + activeAlgorithmContainer.symbol.name);
		listOfActiveAlgorithmContainer.add(activeAlgorithmContainer);
		algorithmInfoManager.activatedSymbol(new Date(), activeAlgorithmContainer.symbol, activeAlgorithmContainer.exchange);	
	}

	@Override
	public void failed(ActiveAlgorithmContainer activeAlgorithmContainer, String reason) {
		Co.println("--> Discarded: " + activeAlgorithmContainer.symbol.name + "(" + reason + ")");
		listOfDiscards.add(new Pair<Symbol,Exchange>(new Symbol(activeAlgorithmContainer.symbol.name, SecurityType.type_stock), activeAlgorithmContainer.exchange));
	}
}
