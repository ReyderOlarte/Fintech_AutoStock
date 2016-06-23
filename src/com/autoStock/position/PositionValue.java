package com.autoStock.position;


/**
 * @author Kevin Kowalewski
 *
 */
public class PositionValue {
	public final double valueRequested;
	public final double valueFilled;
	public final double valueIntrinsic;

	public final double valueRequestedWithFee;
	public final double valueFilledWithFee;
	public final double valueIntrinsicWithFee;
	
	public final double priceRequestedWithFee;
	public final double priceFilledWithFee;
	public final double priceIntrinsicWithFee;

	public final double valueCurrent;
	public final double valueCurrentWithFee;
	
	public final double priceCurrent;
	public final double priceCurrentWithFee;
	
	public final double unitPriceRequested;
	public final double unitPriceIntrinsic;
	public final double unitPriceFilled;
	public final double unitPriceCurrent;
	
	public final double profitLossAfterComission;
	public final double percentGainLoss;
	
	public PositionValue(double valueRequested, double valueFilled, double valueIntrinsic, 
						 double valueRequestedWithFees, double valueFilledWithFees, double valueIntrinsicWithFees, 
						 double priceRequestedWithFees, double priceFilledWithFees, double priceIntrinsicWithFees, 
						 double valueCurrent, double valueCurrentWithFees, 
						 double priceCurrent, double priceCurrentWithFees, 
						 double unitPriceRequested, double unitPriceIntrinsic, double unitPriceFilled, 
						 double unitPriceCurrent, double profitLossAfterComission, double percentGainLoss) {
		this.valueRequested = valueRequested;
		this.valueFilled = valueFilled;
		this.valueIntrinsic = valueIntrinsic;
		this.valueRequestedWithFee = valueRequestedWithFees;
		this.valueFilledWithFee = valueFilledWithFees;
		this.valueIntrinsicWithFee = valueIntrinsicWithFees;
		this.priceRequestedWithFee = priceRequestedWithFees;
		this.priceFilledWithFee = priceFilledWithFees;
		this.priceIntrinsicWithFee = priceIntrinsicWithFees;
		this.valueCurrent = valueCurrent;
		this.valueCurrentWithFee = valueCurrentWithFees;
		this.priceCurrent = priceCurrent;
		this.priceCurrentWithFee = priceCurrentWithFees;
		this.unitPriceRequested = unitPriceRequested;
		this.unitPriceIntrinsic = unitPriceIntrinsic;
		this.unitPriceFilled = unitPriceFilled;
		this.unitPriceCurrent = unitPriceCurrent;
		this.profitLossAfterComission = profitLossAfterComission;
		this.percentGainLoss = percentGainLoss;
	}	
}
