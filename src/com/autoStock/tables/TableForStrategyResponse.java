/**
 * 
 */
package com.autoStock.tables;

import java.text.DecimalFormat;
import java.util.ArrayList;

import com.autoStock.backtest.BacktestContainer;
import com.autoStock.signal.SignalMoment;
import com.autoStock.strategy.StrategyResponse;
import com.autoStock.tools.DateTools;
import com.autoStock.tools.MiscTools;

/**
 * @author Kevin
 *
 */
public class TableForStrategyResponse extends BaseTable {
	private BacktestContainer backtestContainer;
	
	public TableForStrategyResponse(BacktestContainer backtestContainer){
		this.backtestContainer = backtestContainer;
	}

	@Override
	public ArrayList<ArrayList<String>> getDisplayRows() {
		for (StrategyResponse strategyResponse : backtestContainer.listOfStrategyResponse) {
			ArrayList<String> listOfString = new ArrayList<String>();
			listOfString.add(DateTools.getPretty(strategyResponse.quoteSlice.dateTime));
			listOfString.add(backtestContainer.symbol.name);
			listOfString.add(new DecimalFormat("#.00").format(strategyResponse.quoteSlice.priceClose));
			listOfString.add(strategyResponse.strategyActionCause.name().replaceAll("proceed_changed", "*").replaceAll("changed", "").replaceAll("proceed_", "").replaceAll("_condition_", " -> ").replaceAll("_", " "));
			listOfString.add(strategyResponse.positionGovernorResponse.status.name().replaceAll("changed_", "").replaceAll("_", " ").replaceAll("none", " "));

			String stringForSignal = new String();

			for (SignalMoment signalMoment : strategyResponse.signaler.getListOfSignalMoment()) {
				stringForSignal += signalMoment.signalMetricType.name().replace("metric_", "") + ":" + new DecimalFormat("0.00").format(signalMoment.strength);
				if (signalMoment.debug != null && signalMoment.debug.length() > 0){stringForSignal += " - " + signalMoment.debug;}
			}

			listOfString.add(stringForSignal);

			listOfString.add(TableTools.getTransactionDetails(strategyResponse));
			listOfString.add(TableTools.getProfitLossDetails(strategyResponse, false));
			listOfString.add(MiscTools.getCommifiedValue(strategyResponse.basicAccountCopy.getBalance()));

			listOfDisplayRows.add(listOfString);
		}
		
		return listOfDisplayRows;
	}
}
