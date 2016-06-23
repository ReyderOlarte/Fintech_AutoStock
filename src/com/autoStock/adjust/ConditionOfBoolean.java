package com.autoStock.adjust;

/**
 * @author Kevin Kowalewski
 *
 */
public class ConditionOfBoolean extends ConditionBase {	
	
	public ConditionOfBoolean(IterableBase iterableBase) {
		super(iterableBase);
	}

	@Override
	public boolean allowedCondition() {
		return ((IterableOfBoolean)iterableBase).getBoolean();
	}	
}
