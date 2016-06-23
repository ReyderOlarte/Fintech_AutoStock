package com.autoStock.adjust;

import com.autoStock.types.basic.MutableEnum;

/**
 * @author Kevin Kowalewski
 *
 */
public class AdjustmentOfEnum<T extends Enum<T>> extends AdjustmentBase {
	private MutableEnum<T> immutableEnum;
	
	public AdjustmentOfEnum(String description, IterableOfEnum<T> iterableOfEnum, MutableEnum<T> immutableEnum){
		this.iterableBase = iterableOfEnum;
		this.description = description;
		this.immutableEnum = immutableEnum;
	}
	
	@Override
	public void applyValue() {
		immutableEnum.value = getValue();
	}
	
	public T getValue(){
		return ((IterableOfEnum<T>)iterableBase).getEnum();
	}
}
