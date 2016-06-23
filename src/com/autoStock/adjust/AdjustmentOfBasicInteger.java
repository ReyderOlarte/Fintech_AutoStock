package com.autoStock.adjust;

import com.autoStock.types.basic.MutableInteger;

/**
 * @author Kevin Kowalewski
 *
 */
public class AdjustmentOfBasicInteger extends AdjustmentBase {
	private MutableInteger immutableInteger;
	
	public AdjustmentOfBasicInteger(String description, MutableInteger immutableInteger, IterableOfInteger iterableOfInteger){
		this.iterableBase = iterableOfInteger;
		this.description = description;
		this.immutableInteger = immutableInteger;
	}
	
	@Override
	public void applyValue() {
		immutableInteger.value = getValue();
	}

	public int getValue() {
		return ((IterableOfInteger)iterableBase).getInt();
	}
}
