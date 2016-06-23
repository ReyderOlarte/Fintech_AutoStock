package com.autoStock.adjust;

import com.autoStock.types.basic.MutableDouble;

/**
 * @author Kevin Kowalewski
 *
 */
public class AdjustmentOfBasicDouble extends AdjustmentBase {
	private MutableDouble immutableDouble;
	
	public AdjustmentOfBasicDouble(String description, MutableDouble immutableDouble, IterableOfDouble iterableOfDouble){
		this.description = description;
		this.iterableBase = iterableOfDouble;
		this.immutableDouble = immutableDouble;
	}
	
	@Override
	public void applyValue() {
		immutableDouble.value = ((IterableOfDouble)iterableBase).getDouble();
	}

	public double getValue() {
		return ((IterableOfDouble)iterableBase).getDouble();
	}
}
