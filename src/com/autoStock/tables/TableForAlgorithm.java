/**
 * 
 */
package com.autoStock.tables;

import java.text.DecimalFormat;
import java.util.ArrayList;

import com.autoStock.account.BasicAccount;
import com.autoStock.algorithm.AlgorithmBase;
import com.autoStock.signal.Signaler;
import com.autoStock.signal.SignalDefinitions.SignalMetricType;
import com.autoStock.signal.SignalDefinitions.SignalPointType;
import com.autoStock.signal.SignalGroup;
import com.autoStock.strategy.StrategyResponse;
import com.autoStock.strategy.StrategyResponse.StrategyAction;
import com.autoStock.tools.DateTools;
import com.autoStock.tools.MathTools;
import com.autoStock.tools.StringTools;
import com.autoStock.types.QuoteSlice;

/**
 * @author Kevin
 *
 */
public class TableForAlgorithm extends BaseTable {
	public static boolean INCLUDE_SIGNALS = false;
	private static DecimalFormat decimalFormat = new DecimalFormat("#.00");
	private AlgorithmBase algorithmBase;
	
	public TableForAlgorithm(AlgorithmBase algorithmBase) {
		this.algorithmBase = algorithmBase;
	}

	public void addTableRow(ArrayList<QuoteSlice> listOfQuoteSlice, Signaler signal, SignalGroup signalGroup, StrategyResponse strategyResponse, BasicAccount basicAccount){
		ArrayList<String> columnValues = new ArrayList<String>();
		QuoteSlice quoteSlice = listOfQuoteSlice.get(listOfQuoteSlice.size()-1);

		columnValues.add(DateTools.getPretty(quoteSlice.dateTime));
		columnValues.add(String.valueOf(quoteSlice.sizeVolume));
		columnValues.add(decimalFormat.format(quoteSlice.priceClose));
		columnValues.add(String.valueOf(StringTools.addPlus(MathTools.round(quoteSlice.priceClose - listOfQuoteSlice.get(listOfQuoteSlice.size() - 2).priceClose))));
		
		if (INCLUDE_SIGNALS){
			columnValues.add(String.valueOf(new DecimalFormat("0.00").format(signalGroup.signalOfDI.getStrength())));
			columnValues.add(String.valueOf(new DecimalFormat("0.00").format(signalGroup.signalOfUO.getStrength())));
			columnValues.add(String.valueOf(new DecimalFormat("0.00").format(signalGroup.signalOfCCI.getStrength())));
			columnValues.add(String.valueOf(new DecimalFormat("0.00").format(signalGroup.signalOfRSI.getStrength())));
			columnValues.add(String.valueOf(new DecimalFormat("0.00").format(signalGroup.signalOfSTORSI.getStrength())));
			columnValues.add(String.valueOf(new DecimalFormat("0.00").format(signalGroup.signalOfMACD.getStrength())));
			columnValues.add(String.valueOf(new DecimalFormat("0.00").format(signalGroup.signalOfTRIX.getStrength())));
			columnValues.add(String.valueOf(new DecimalFormat("0.00").format(signalGroup.signalOfROC.getStrength())));
			columnValues.add(String.valueOf(new DecimalFormat("0.00").format(signalGroup.signalOfMFI.getStrength())));
			columnValues.add(String.valueOf(new DecimalFormat("0.0000").format(signalGroup.signalOfCrossover.getStrength())));
		}
		
		columnValues.add(strategyResponse.positionGovernorResponse.status.name().replaceAll("changed_", ""));
		columnValues.add(strategyResponse.strategyAction == StrategyAction.no_change ? "-" : (strategyResponse.strategyAction.name().replaceAll("algorithm_", "") + ", " + strategyResponse.strategyActionCause.name().replaceAll("_condition", "")));
		columnValues.add(strategyResponse.positionGovernorResponse.signalPoint.signalPointType == SignalPointType.no_change ? "-" : strategyResponse.positionGovernorResponse.signalPoint.signalPointType.name());
		columnValues.add(strategyResponse.positionGovernorResponse.signalPoint.signalMetricType == SignalMetricType.no_change ? "-" : strategyResponse.positionGovernorResponse.signalPoint.signalMetricType.name());
		columnValues.add(TableTools.getTransactionDetails(strategyResponse));
		columnValues.add(TableTools.getProfitLossDetails(strategyResponse, true));
		columnValues.add(decimalFormat.format(basicAccount.getBalance()) + " -> " + String.format("%1$5s","%" + decimalFormat.format(algorithmBase.getYieldCurrent()) + "%"));
		
		listOfDisplayRows.add(columnValues);
	}

	@Override
	public ArrayList<ArrayList<String>> getDisplayRows() {
		return 	listOfDisplayRows;
	}

}
