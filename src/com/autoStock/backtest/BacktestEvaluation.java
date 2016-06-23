package com.autoStock.backtest;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import com.autoStock.account.AccountProvider;
import com.autoStock.backtest.BacktestUtils.BacktestResultTransactionDetails;
import com.autoStock.guage.SignalGuage;
import com.autoStock.position.PositionGovernorResponse;
import com.autoStock.signal.SignalDefinitions.SignalParameters;
import com.autoStock.strategy.StrategyResponse;
import com.autoStock.tables.TableController;
import com.autoStock.tables.TableDefinitions.AsciiTables;
import com.autoStock.tools.DateTools;
import com.autoStock.tools.MiscTools;
import com.autoStock.types.Exchange;
import com.autoStock.types.Symbol;
import com.google.gson.GsonBuilder;
import com.google.gson.internal.Pair;
import com.rits.cloning.Cloner;

/**
 * @author Kevin Kowalewski
 *
 */
public class BacktestEvaluation {
	public boolean allowNegativeScore = false;
	public BacktestResultTransactionDetails transactionDetails;
	
	public final Symbol symbol;
	public final Exchange exchange;
	public final Date dateStart;
	public final Date dateEnd;
	
	public int transactions;
	public double transactionFeesPaid;
	public double accountBalance;
	public double percentGain;
	public double percentTradeProfit;
	public double percentTradeLoss;
	public double percentYield;
	
	public AlgorithmModel algorithmModel = new AlgorithmModel();
	
	public ArrayList<DescriptorForSignal> listOfDescriptorForSignal = new ArrayList<>();
	public ArrayList<DescriptorForIndicator> listOfDescriptorForIndicator = new ArrayList<>();
	public ArrayList<DescriptorForAdjustment> listOfDescriptorForAdjustment = new ArrayList<>();
	
	public ArrayList<ArrayList<String>> listOfDisplayRowsFromStrategyResponse;
	public ArrayList<ArrayList<String>> listOfDisplayRowsFromAlgorithm;
	public ArrayList<Pair<Date, Double>> listOfDailyYield;
	
	public BacktestEvaluation(BacktestContainer backtestContainer){
		this.symbol = backtestContainer.symbol;
		this.exchange = backtestContainer.exchange;
		this.dateStart = backtestContainer.dateContainerStart;
		this.dateEnd = backtestContainer.dateContainerEnd;
	}
	
	public double getScore(){
		return BacktestScoreProvider.getScore(this, allowNegativeScore);
	}
	
	public static class DescriptorForSignal {
		public String signalName;
		public String signalPointType;
		public int maxSignalAverage;
		public String extras = "";
		
		public ArrayList<DescriptorForGuage> listOfDescriptorForGuage = new ArrayList<DescriptorForGuage>();
		
		public SignalParameters signalParamaters;
		
		@Override
		public String toString() {
			String string = new String();
			string += signalName + "(" + maxSignalAverage + "), " + (extras.equals("") ? "" : "[" + extras + "]") + signalPointType;
			string += "\n";
			
			for (DescriptorForGuage descriptorForGuage : listOfDescriptorForGuage){
				string += descriptorForGuage.toString();
			}
			
			return string;
		}
	}
	
	public static class DescriptorForGuage {
		public String guageType;
		public String guageBoundsName;
		public String guageThreshold;
		
		public DescriptorForGuage(SignalGuage signalGuage) {
			guageType = signalGuage.mutableEnumForSignalGuageType.value.name();
			guageBoundsName = signalGuage.signalBounds.name();
			guageThreshold = String.valueOf(signalGuage.threshold);
		}

		@Override
		public String toString() {
			return "   " + guageType + ", " + guageBoundsName + ", " + guageThreshold;
		}
	}
	
	public static class DescriptorForIndicator {
		public String indicatorName;
		public int indicatorPeriodLength;
		public int indicatorResultSetLength;
		
		public DescriptorForIndicator(String indicatorName, int indicatorPeriodLength, int resultsetLength) {
			this.indicatorName = indicatorName;
			this.indicatorPeriodLength = indicatorPeriodLength;
			this.indicatorResultSetLength = resultsetLength;
		}
		
		@Override
		public String toString() {
			return indicatorName + " : " + indicatorPeriodLength + ", " + indicatorResultSetLength;
		}
	}
	
	public static class DescriptorForAdjustment {
		public String adjustmentType;
		public String adjustmentDescription;
		public String adjustmentValue;
		
		@Override
		public String toString() {
			return adjustmentType + ", " + adjustmentDescription + ", " + adjustmentValue;
		}
	}
	
	@Override
	public String toString() {
		String string = new String();
		
		if (transactions > 0 && listOfDisplayRowsFromStrategyResponse.size() == 0){
			throw new IllegalStateException("List can't be zero sized... Transactions: " + transactions);
		}
		
		string += "\n***** $" +  MiscTools.getCommifiedValue(accountBalance - AccountProvider.defaultBalance) + " / %" + new DecimalFormat("#.00").format(percentYield) + " Score: " + getScore() + " *****";
		string += "\n--> Date " + DateTools.getPretty(dateStart) + " to " + DateTools.getPretty(dateEnd);
		string += "\n--> Transactions: " + transactions;
		string += "\n--> Transaction fees: $" + new DecimalFormat("#.00").format(transactionFeesPaid);
		string += "\n--> Transaction details (long, short, re-entry, exit): " + transactionDetails.countForTradeLongEntry + " / " + transactionDetails.countForTradeShortEntry + ", " + transactionDetails.countForTradesReentry + ", " + transactionDetails.countForTradeExit;
		string += "\n--> Transaction profit / loss (profit, loss): %" + new DecimalFormat("#.00").format(percentTradeProfit) + ", %" + new DecimalFormat("#.00").format(percentTradeLoss) + " / " + transactionDetails.countForTradesProfit + ", " + transactionDetails.countForTradesLoss;
		string += "\n--> Transaction avg profit / loss: $" + new DecimalFormat("#.00").format(transactionDetails.avgTradeWin) + ", $" + new DecimalFormat("#.00").format(transactionDetails.avgTradeLoss);
		string += "\n--> Trade max profit, loss / min profit, loss : " + new DecimalFormat("#.00").format(transactionDetails.maxTradeWin)
			+ ", " + new DecimalFormat("#.00").format(transactionDetails.maxTradeLoss)
			+ " / " + new DecimalFormat("#.00").format(transactionDetails.minTradeWin)
			+ ", " + new DecimalFormat("#.00").format(transactionDetails.minTradeLoss);
		
		string += "\n";
		
		for (Pair<Date, Double> pair : listOfDailyYield){
			string += "\n - Daily yield: " + new SimpleDateFormat("dd/MM/yyyy").format(pair.first) + ", %" + new DecimalFormat("#.00").format(pair.second);
		}
		
		string += "\n * Average daily yield: " + new DecimalFormat("#.00").format(getAverageDailyYield()) + "%"; 
		
		string += "\n";
		
		for (DescriptorForSignal descriptorForSignal : listOfDescriptorForSignal){
			string += "\n - " + descriptorForSignal.toString();
		}
		
		if (listOfDescriptorForSignal != null && listOfDescriptorForSignal.size() > 0){
			string += "\n";	
		}
		
		string += "\n";
		
		string += " - Algorithm period: " + algorithmModel.periodLength + "\n";
		
		for (DescriptorForIndicator descriptorForIndicator : listOfDescriptorForIndicator){
			string += "\n - " + descriptorForIndicator.toString();
		}
		
		string += "\n";
		
		for (DescriptorForAdjustment descriptorForAdjustment : listOfDescriptorForAdjustment){
			string += "\n - " + descriptorForAdjustment.toString();
		}
		
		string += "\n";
		
		string += algorithmModel.strategyOptions.toString();
		
//		string += "Sizes: " + listOfDisplayRowsFromStrategyResponse.size() + ", " + listOfDisplayRowsFromStrategyResponse.get(0).size();
		
		string += "\n" + (listOfDisplayRowsFromStrategyResponse.size() == 0 ? "No transactions occurred" : new TableController().getTable(AsciiTables.backtest_strategy_response, listOfDisplayRowsFromStrategyResponse));
		
		return string;
	}
	
	public String getSingleLine(){
		String string = new DecimalFormat("#.00").format(getScore()) + "[%" + new DecimalFormat("#.00").format(percentYield) + "] ";
		
		for (Pair<Date, Double> pair : listOfDailyYield){
			string += " " + new SimpleDateFormat("dd/MM/yyyy").format(pair.first) + " -> %" + new DecimalFormat("#.00").format(pair.second) + "\n";
		}
		
		return string;
	}
	
	private double getAverageDailyYield(){
		double average = 0;
		if (listOfDailyYield.size() == 0){return 0;}
		
		for (Pair<Date, Double> pair : listOfDailyYield){
			average += pair.second;
		}
		
		return average / listOfDailyYield.size();
	}
	
	public String getUniqueIdentifier(){
		GsonBuilder builder = new GsonBuilder().serializeSpecialFloatingPointValues();
		return MiscTools.getHash(builder.create().toJson(this));
	}
}
