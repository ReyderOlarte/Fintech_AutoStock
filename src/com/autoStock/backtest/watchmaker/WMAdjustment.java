package com.autoStock.backtest.watchmaker;

import java.util.ArrayList;

import com.autoStock.adjust.AdjustmentBase;
import com.rits.cloning.Cloner;

/**
 * @author Kevin Kowalewski
 *
 */
public class WMAdjustment {
	public ArrayList<AdjustmentBase> listOfAdjustmentBase = new ArrayList<AdjustmentBase>();
	
	public WMAdjustment copy(){
		return new Cloner().deepClone(this);
	}
}
