package com.autoStock.backtest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.concurrent.ConcurrentHashMap;

import com.autoStock.Co;
import com.autoStock.tools.ListTools;
import com.autoStock.types.Symbol;

/**
 * @author Kevin Kowalewski
 *
 */
public class BacktestEvaluator {
	private static final int bufferResults = 256;
	public static final int maxResults = 16;
	
	private ConcurrentHashMap<Symbol, ArrayList<BacktestEvaluation>> hashOfBacktestEvaluation = new ConcurrentHashMap<Symbol, ArrayList<BacktestEvaluation>>();

	public void addResult(Symbol symbol, BacktestEvaluation backtestEvaluation, boolean autoPrune){
		
		if (backtestEvaluation.getScore() <= 0){
			Co.println("--> Score is <= 0: " + backtestEvaluation.getScore());
			return;
		}
		
		if (hashOfBacktestEvaluation.containsKey(symbol) == false){
			hashOfBacktestEvaluation.put(symbol, new ArrayList<BacktestEvaluation>(Arrays.asList(new BacktestEvaluation[]{backtestEvaluation})));
		}else{
			hashOfBacktestEvaluation.get(symbol).add(backtestEvaluation);
		}
		
		if (autoPrune){
			pruneResults(bufferResults, true);
		}
		
//		Co.println("--> Added: " + symbol.symbolName + ", " + backtestEvaluation.accountBalance + ", Score: " + backtestEvaluation.getScore() + ", " + backtestEvaluation.transactions + ", %" + new DecimalFormat("0.00").format(backtestEvaluation.percentYield));
		
//		for (DescriptorForSignal descriptor : backtestEvaluation.listOfDescriptorForSignal){
//			Co.println("--> Descriptor: " + descriptor.toString());
//		}
		
		
//		printBest();
		
//		if (backtestEvaluation.percentYield > 0.20){
//			throw new IllegalAccessError();
//		}
//		throw new IllegalStateException();
	}
	
	public void printBest(){
		for (Symbol symbol : hashOfBacktestEvaluation.keySet()){
			ArrayList<BacktestEvaluation> listOfBacktestEvaluation = hashOfBacktestEvaluation.get(symbol);
			
			for (BacktestEvaluation backtestEvaluation : (ArrayList<BacktestEvaluation>) ListTools.reverseList(listOfBacktestEvaluation)){
				Co.println("--> BEST SCORES: " + backtestEvaluation.percentYield + ", " + backtestEvaluation.getScore());
			}
		}
	}
	
	public void pruneResults(int results, boolean enforceSizeRestriction){
		for (Symbol symbol : hashOfBacktestEvaluation.keySet()){
			ArrayList<BacktestEvaluation> listOfBacktestEvaluation = hashOfBacktestEvaluation.get(symbol);
			if (listOfBacktestEvaluation.size() <= results && enforceSizeRestriction){
				continue;
			}
			
			Collections.sort(listOfBacktestEvaluation, new BacktestEvaluationComparator());
			hashOfBacktestEvaluation.put(symbol, new ArrayList<BacktestEvaluation>(listOfBacktestEvaluation.subList(0, Math.min(results, listOfBacktestEvaluation.size()))));
		}
	}

	public void pruneForFinish() {
		printBest();
		pruneResults(maxResults, false);
	}

	public ArrayList<BacktestEvaluation> getResults(Symbol symbol) {
		return hashOfBacktestEvaluation.get(symbol);
	}

	public void reverse() {
		for (Symbol symbol : hashOfBacktestEvaluation.keySet()){
			ArrayList<BacktestEvaluation> listOfBacktestEvaluation = hashOfBacktestEvaluation.get(symbol);
			listOfBacktestEvaluation = (ArrayList<BacktestEvaluation>) ListTools.reverseList(listOfBacktestEvaluation);
			hashOfBacktestEvaluation.put(symbol, listOfBacktestEvaluation);
		}
	}
	
	public void reset(){
		hashOfBacktestEvaluation.clear();
	}
}
