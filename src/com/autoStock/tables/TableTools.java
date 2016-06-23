/**
 * 
 */
package com.autoStock.tables;

import java.text.DecimalFormat;

import com.autoStock.position.PositionGovernorResponseStatus;
import com.autoStock.strategy.StrategyResponse;
import com.autoStock.strategy.StrategyResponse.StrategyActionCause;

/**
 * @author Kevin
 *
 */
public class TableTools {
	private static DecimalFormat decimalFormat = new DecimalFormat("#.00");
	
	public static String getTransactionDetails(StrategyResponse strategyResponse){ 
		String responseString = "";
		if (strategyResponse.positionGovernorResponse.position != null){
			if (strategyResponse.positionGovernorResponse.status == PositionGovernorResponseStatus.changed_long_entry || strategyResponse.positionGovernorResponse.status == PositionGovernorResponseStatus.changed_short_entry){
				responseString = "(" + strategyResponse.positionGovernorResponse.position.getInitialUnitsFilled();
				responseString += " * " + decimalFormat.format(strategyResponse.positionGovernorResponse.position.getPositionValue().unitPriceFilled);
				responseString += ") + " + decimalFormat.format(strategyResponse.positionGovernorResponse.position.getPositionUtils().getOrderTransactionFeesIntrinsic());
				responseString += " = $" + decimalFormat.format(strategyResponse.positionGovernorResponse.position.getPositionValue().priceFilledWithFee);
			}else if (strategyResponse.positionGovernorResponse.status == PositionGovernorResponseStatus.changed_long_reentry || strategyResponse.positionGovernorResponse.status == PositionGovernorResponseStatus.changed_short_reentry){
				responseString = "(" + strategyResponse.positionGovernorResponse.position.getLastEntryOrder().getUnitsIntrinsic();
				responseString += " * " + strategyResponse.positionGovernorResponse.position.getLastEntryOrder().getUnitPriceFilled();
				responseString += ") + " + decimalFormat.format(strategyResponse.positionGovernorResponse.position.getLastEntryOrder().getTransactionFees());
				responseString += " = $" + decimalFormat.format(strategyResponse.positionGovernorResponse.position.getLastEntryOrder().getOrderValue().priceIntrinsicWithFees);
			}else if (strategyResponse.positionGovernorResponse.status == PositionGovernorResponseStatus.changed_long_exit || strategyResponse.positionGovernorResponse.status == PositionGovernorResponseStatus.changed_short_exit){
				responseString = "(" + strategyResponse.positionGovernorResponse.position.getLastExitOrder().getUnitsIntrinsic();
				responseString += " * " + strategyResponse.positionGovernorResponse.position.getLastExitOrder().getUnitPriceFilled();
				responseString += ") - " + decimalFormat.format(strategyResponse.positionGovernorResponse.position.getLastExitOrder().getTransactionFees());
				responseString += " = $" + decimalFormat.format(strategyResponse.positionGovernorResponse.position.getPositionValue().valueCurrentWithFee);
			}
		}
		
		return responseString;
	}

	public static String getProfitLossDetails(StrategyResponse strategyResponse, boolean forAlgorithm) {
		String responseString = "";
		if (strategyResponse.positionGovernorResponse.position != null){
			String valueGainString = String.format("%1$5s", "$" + strategyResponse.positionGovernorResponse.position.getPositionValue().profitLossAfterComission);
			String percentGainString = String.format("%1$5s","%" + decimalFormat.format(strategyResponse.positionGovernorResponse.position.getPositionValue().percentGainLoss));
			
			if (strategyResponse.strategyActionCause == StrategyActionCause.pass_condition_entry && forAlgorithm == false){
				
			}else if (strategyResponse.positionGovernorResponse.status == PositionGovernorResponseStatus.changed_long_entry || strategyResponse.positionGovernorResponse.status == PositionGovernorResponseStatus.changed_short_entry){
				//responseString = valueGainString;
			}else{
				responseString = valueGainString + " -> " + percentGainString;
			}
		}
		
		return responseString;
	}
}
