package com.autoStock.order;

/**
 * @author Kevin Kowalewski
 *
 */
public class OrderValue {
	public final double valueRequested;
	public final double valueFilled;
	public final double valueIntrinsic;
	
	public final double valueRequestedWithFees;
	public final double valueFilledWithFees;
	public final double valueIntrinsicWithFees;
	
	public final double priceRequestedWithFees;
	public final double priceFilledWithFees;
	public final double priceIntrinsicWithFees;
	
	public final double unitPriceRequested;
	public final double unitPriceFilled;
	public final double unitPriceIntrinsic;
	
	public final double transactionFees;
	
	public OrderValue(double valueRequested, double valueFilled, double valueIntrinsic, 
					  double valueRequestedWithFees, double valueFilledWithFees, double valueIntrinsicWithFees, 
					  double priceRequestedWithFees, double priceFilledWithFees, double priceIntrinsicWithFees, 
					  double unitPriceRequested, double unitPriceFilled, double unitPriceIntrinsic, 
					  double transactionFees) {
		this.valueRequested = valueRequested;
		this.valueFilled = valueFilled;
		this.valueIntrinsic = valueIntrinsic;
		
		this.valueRequestedWithFees = valueRequestedWithFees;
		this.valueFilledWithFees = valueFilledWithFees;
		this.valueIntrinsicWithFees = valueIntrinsicWithFees;
		
		this.priceRequestedWithFees = priceRequestedWithFees;
		this.priceFilledWithFees = priceFilledWithFees;
		this.priceIntrinsicWithFees = priceIntrinsicWithFees;
		
		this.unitPriceRequested = unitPriceRequested;
		this.unitPriceFilled = unitPriceFilled;
		this.unitPriceIntrinsic = unitPriceIntrinsic;
		
		this.transactionFees = transactionFees;
	}
}
