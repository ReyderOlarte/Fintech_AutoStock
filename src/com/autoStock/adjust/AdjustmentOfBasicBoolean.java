package com.autoStock.adjust;

import com.autoStock.types.basic.MutableBoolean;

/**
 * @author Kevin Kowalewski
 *
 */
public class AdjustmentOfBasicBoolean extends AdjustmentBase {
	private MutableBoolean mutableBoolean;
	
	public AdjustmentOfBasicBoolean(String description, MutableBoolean mutableBoolean, IterableOfBoolean iterableOfBoolean){
		this.iterableBase = iterableOfBoolean;
		this.description = description;
		this.mutableBoolean = mutableBoolean;
	}
	
	@Override
	public void applyValue() {
		mutableBoolean.value = getValue();
	}

	public boolean getValue() {
		return ((IterableOfBoolean)iterableBase).getBoolean();
	}
}
