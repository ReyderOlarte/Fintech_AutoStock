package com.autoStock.algorithm.core;

import java.text.DecimalFormat;
import java.util.ArrayList;

import com.autoStock.algorithm.AlgorithmTest;
import com.autoStock.position.PositionDefinitions.PositionType;
import com.autoStock.position.PositionManager;
import com.autoStock.position.PositionValue;
import com.autoStock.signal.SignalBase;
import com.autoStock.tools.DateTools;
import com.autoStock.tools.MathTools;
import com.autoStock.tools.StringTools;
import com.autoStock.trading.types.Position;
import com.autoStock.types.QuoteSlice;

/**
 * @author Kevin Kowalewski
 *
 */
public class ActiveAlgorithmManagerTable {
	private ArrayList<ArrayList<String>> listOfDisplayRows = new ArrayList<ArrayList<String>>();
	
	public void addRow(AlgorithmTest algorithm, ArrayList<QuoteSlice> listOfQuoteSlice){
		ArrayList<String> columnValues = new ArrayList<String>();
		
		Position position = PositionManager.getGlobalInstance().getPosition(algorithm.symbol);
		PositionValue positionValue = position == null ? null : position.getPositionValue();
		
		double percentGainFromAlgorithm = 0;
		double percentGainFromPosition = 0;
		
		if (algorithm.firstQuoteSlice != null && algorithm.getCurrentQuoteSlice() != null){
			if (algorithm.firstQuoteSlice.priceClose != 0 && algorithm.getCurrentQuoteSlice().priceClose != 0){
				percentGainFromAlgorithm = ((algorithm.getCurrentQuoteSlice().priceClose / algorithm.firstQuoteSlice.priceClose) -1) * 100;
				if (Double.isNaN(percentGainFromAlgorithm)){
					percentGainFromAlgorithm = 0;
				}
			}
		}
		
		if (position != null && (position.positionType == PositionType.position_long || position.positionType == PositionType.position_short)){
			if (positionValue.unitPriceCurrent != 0 && positionValue.unitPriceFilled != 0){
				percentGainFromPosition = position.getCurrentPercentGainLoss(false);
				if (Double.isNaN(percentGainFromPosition)){
					percentGainFromPosition = 0;
				}
			}
		}
		
		columnValues.add(algorithm.getCurrentQuoteSlice() != null && algorithm.getCurrentQuoteSlice().dateTime != null ? DateTools.getPretty(algorithm.getCurrentQuoteSlice().dateTime) : "?"); 
		columnValues.add(algorithm.symbol.name + ", " + algorithm.algorithmSource);
		columnValues.add(algorithm.algorithmState.isDisabled == true ? ("disabled (" + algorithm.algorithmState.disabledReason + ")") : " - ");
		columnValues.add(algorithm.strategyBase.lastStrategyResponse == null ? "-" : (algorithm.strategyBase.lastStrategyResponse.positionGovernorResponse.signalPoint.signalPointType.name() + ", " + algorithm.strategyBase.lastStrategyResponse.positionGovernorResponse.signalPoint.signalMetricType.name()));
		columnValues.add(algorithm.strategyBase.currentStrategyResponse == null ? "-" : (algorithm.strategyBase.currentStrategyResponse.strategyActionCause.name()));
		columnValues.add(position == null ? "-" : position.positionType.name());
		columnValues.add(String.valueOf(algorithm.getFirstQuoteSlice() == null ? 0 : MathTools.round(algorithm.getFirstQuoteSlice().priceClose)));
		columnValues.add(String.valueOf(position == null ? "-" : positionValue.unitPriceIntrinsic));
		columnValues.add(String.valueOf(algorithm.getCurrentQuoteSlice() == null ? 0 : MathTools.round(algorithm.getCurrentQuoteSlice().priceClose)));
		columnValues.add(String.valueOf(new DecimalFormat("#.###").format(percentGainFromAlgorithm)));
		columnValues.add(String.valueOf(new DecimalFormat("#.###").format(percentGainFromPosition)));
		columnValues.add(String.valueOf(position == null ? "-" : ("P&L: " + StringTools.addPlus(position.getPositionProfitLossBeforeComission()) + " / " + StringTools.addPlus(position.getPositionProfitLossAfterComission(false)))));
		//columnValues.add(String.valueOf(position == null ? "-" : (position.getFirstKnownUnitPrice() + ", " +  position.getLastKnownUnitPrice() + ", " + position.getPositionValue().valueCurrent + ", " + position.getPositionValue().valueIntrinsic + ", " + position.getPositionValue().unitPriceFilled + ", " + position.positionUtils.getOrderUnitsFilled() + ", " + + position.positionUtils.getOrderUnitsIntrinsic())));
		
		
		String stringForSignalMetrics = new String();
		
		if (algorithm.strategyBase.signaler != null){
			for (SignalBase signalBase : algorithm.strategyBase.signaler.getListOfSignalBase()){
				//signalMetrics += " (" + signalMetric.signalMetricType.name() + ":" + signalMetric.strength + ":" + signalMetric.getSignalPoint(position == null ? false : true, position == null ? PositionType.position_none : position.positionType).signalPointType.name() + ")";
				stringForSignalMetrics = "Replace this";
			}
		}else{
			stringForSignalMetrics = "?";
		}
		
		columnValues.add(stringForSignalMetrics);
		
		listOfDisplayRows.add(columnValues);
	}
	
	public ArrayList<ArrayList<String>> getListOfDisplayRows(){
		return listOfDisplayRows;
	}

	public void clear() {
		listOfDisplayRows.clear();
	}
}
