package com.autoStock.position;

import com.autoStock.Co;
import com.autoStock.trading.types.Position;

/**
 * @author Kevin Kowalewski
 *
 */
public class PositionValueTable {
	public synchronized void printTable(Position position, PositionValue positionValue){
		Co.println("\n\n --> PositionValue... " + position.symbol.name);
		Co.println(
				   "\n valueRequested -> " + positionValue.valueRequested //OK
				   + "\n valueFilled -> " + positionValue.valueFilled //OK
				   + "\n valueIntrinsic -> " + positionValue.valueIntrinsic //OK
				   
				   + "\n\n valueRequestedWithFees -> " + positionValue.valueRequestedWithFee //OK
				   + "\n valueFilledWithFees -> " + positionValue.valueFilledWithFee  //OK 
				   + "\n valueInstrinsicWithFees -> " + positionValue.valueIntrinsicWithFee //OK
				   
				   + "\n\n priceRequestedWithFees -> " + positionValue.priceRequestedWithFee //OK
				   + "\n priceFilledWithFees -> " + positionValue.priceFilledWithFee //OK
				   + "\n priceIntrinsicWithFees -> " + positionValue.priceIntrinsicWithFee //OK
				   
				   + "\n\n valueCurrent -> " + positionValue.valueCurrent //OK
				   + "\n valueCurrentWithFees -> " + positionValue.valueCurrentWithFee //OK

				   + "\n\n priceCurrent -> " + positionValue.priceCurrent //OK
				   + "\n priceCurrentWithFees -> " + positionValue.priceCurrentWithFee //OK	

				   + "\n\n unitPriceRequested -> " + positionValue.unitPriceRequested //OK
				   + "\n unitPriceFilled -> " + positionValue.unitPriceFilled //OK
				   + "\n unitPriceIntrinsic -> " + positionValue.unitPriceIntrinsic //OK
				   + "\n unitPriceCurrent -> " + positionValue.unitPriceCurrent //OK
		);
	}
}
