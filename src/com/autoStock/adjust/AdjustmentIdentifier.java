package com.autoStock.adjust;

import com.autoStock.adjust.AdjustmentCampaignProvider.AdjustmentTarget;
import com.autoStock.types.Symbol;

public class AdjustmentIdentifier {
	public AdjustmentTarget adjustmentTarget;
	public Symbol identifier;
	
	public AdjustmentIdentifier(AdjustmentTarget adjustmentTarget, Symbol identifier) {
		this.adjustmentTarget = adjustmentTarget;
		this.identifier = identifier;
	}
}
