package com.autoStock.backtest;

import java.util.Date;

import com.autoStock.Co;
import com.autoStock.position.PositionGovernorResponse;
import com.autoStock.position.PositionHistory.ProfitOrLoss;
import com.autoStock.strategy.StrategyResponse;
import com.autoStock.trading.types.Position;
import com.google.gson.internal.Pair;

/**
 * @author Kevin Kowalewski
 *
 */
public class BacktestScoreProvider {
	public static double getScore(BacktestEvaluation backtestEvaluation, boolean allowNegativeScore){
		double score = 0;
		
//		score = getScoreOnlyYield(backtestEvaluation);
//		score = getScoreDoDYield(backtestEvaluation);
//		score = getScorePerTrans(backtestEvaluation);
		score = getFancyScore(backtestEvaluation);
		
		if (allowNegativeScore){return score;}
		else {return score > 0 ? score : 0;}
	}
	
	private static double getFancyScore(BacktestEvaluation backtestEvaluation){
		double score = 0;
		
		for (Pair<StrategyResponse, Double> pair : backtestEvaluation.transactionDetails.listOfTransactionYield){
			//Co.println("--> " + pair.first.strategyAction.name() + " -> " + pair.first.positionGovernorResponse.status.name() + ", " + pair.first.quoteSlice.dateTime + ", " + pair.first.positionGovernorResponse.position.getPositionHistory().getAge().asSeconds());
			
			if (pair.first.positionGovernorResponse.position.getPositionHistory().getAge().asSeconds() <= 60 * 5){
				if (pair.second > 0.10){score += pair.second;}
				else if (pair.second > 0){score += pair.second / 2;}
				else if (pair.second < 0){score += pair.second * 2;}
			}
			else if (pair.second < 0){score += pair.second * 2;} //Adding a negative number
			else if (pair.second < 0.010){score += pair.second / 2;}
			else {score += pair.second;}
		}
		
		return score;
	}
	
	private static double getScorePerTrans(BacktestEvaluation backtestEvaluation){
		double score = 0;
		int penalty = 1;
		
		for (Pair<StrategyResponse, Double> pair : backtestEvaluation.transactionDetails.listOfTransactionYield){
			score += pair.second;
			
			if (pair.second < 0){
				penalty++;
			} else if (pair.second < 0.10){
				score -= pair.second;
			}
		}
		
		score /= penalty;
		
		return score;
	}
	
	private static double getScoreDoDYield(BacktestEvaluation backtestEvaluation){
		double score = 0;
		int penalty = 1;
		
		for (Pair<Date, Double> pair : backtestEvaluation.listOfDailyYield){
			score += pair.second;
			if (pair.second < 0){penalty++;}
		}
		
		score /= penalty;
		
		return score;
	}
	
	private static double getScoreOnlyYield(BacktestEvaluation backtestEvaluation){
		return backtestEvaluation.percentYield;
	}
}
