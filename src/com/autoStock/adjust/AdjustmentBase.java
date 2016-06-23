package com.autoStock.adjust;

/**
 * @author Kevin Kowalewski
 *
 */
public abstract class AdjustmentBase {
	protected IterableBase iterableBase;
	protected String description;
	public abstract void applyValue();
	
	public IterableBase getIterableBase(){
		return iterableBase;
	}
	
	public String getDescription(){
		return description;
	}
}
