package com.autoStock.adjust;

/**
 * @author Kevin Kowalewski
 *
 */
public abstract class ConditionBase {
	protected IterableBase iterableBase;
	
	public ConditionBase(IterableBase iterableBase){
		this.iterableBase = iterableBase;
	}
	
	public abstract boolean allowedCondition();
}
