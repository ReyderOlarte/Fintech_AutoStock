package com.autoStock.adjust;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;

import com.autoStock.Co;

/**
 * @author Kevin Kowalewski
 *
 */
public abstract class AdjustmentCampaign {
	protected ArrayList<IterableBase> listOfIterableBase = new ArrayList<IterableBase>();
	protected ArrayList<AdjustmentBase> listOfAdjustmentBase = new ArrayList<AdjustmentBase>();
	private Permutation permutation = new Permutation(listOfIterableBase);
	private boolean hasRun;
	public boolean isRebasing;
	private BigDecimal currentIndex = BigDecimal.valueOf(0);
	
	public static enum AdjustmentType {
		signal_metric_long_entry,
		signal_metric_long_exit,
		signal_metric_short_entry,
		signal_metric_short_exit,
	}
	
	protected abstract void initializeAdjustmentCampaign();
	
	public void initialize(){
		initializeAdjustmentCampaign();
		for (AdjustmentBase adjustmentBase : listOfAdjustmentBase){
			listOfIterableBase.add(adjustmentBase.getIterableBase());
		}
	}
	
	public boolean runAdjustment(){
		hasRun = true;
		
		if (permutation.allDone()){
			return false;
		}else{
			permutation.masterIterate();
//			permutation.printIterableSet();
			applyValues();
			
			currentIndex = currentIndex.add(BigDecimal.valueOf(1));
			
			return true;
		}
	}
	
	public boolean rebaseRequired(){
		for (IterableBase iterableBase : listOfIterableBase){
			if (iterableBase.rebaseRequired){
				return true;
			}
		}
		
		return false;
	}
	
	public void rebased(){
		for (IterableBase iterableBase : listOfIterableBase){
			iterableBase.rebaseRequired = false;
		}
	}
	
	public boolean hasMore(){
		return !permutation.allDone();
	}
	
	public boolean hasRun(){
		return hasRun;
	}
	
	public void printPercentComplete(String prefix){
		BigDecimal maximumIndex = getMaxIndex();
		
		Co.println("--> [" + prefix + "] Current / Max: " + currentIndex.toPlainString() + ", " + maximumIndex.toPlainString() + " " + new DecimalFormat("0.00").format(currentIndex.divide(maximumIndex, 4, RoundingMode.HALF_UP).doubleValue() * 100) +"%");
	}
	
	public BigDecimal getMaxIndex(){
		BigDecimal maximumIndex = BigDecimal.valueOf(1);
		
		for (IterableBase iterableBase : listOfIterableBase){
			maximumIndex = maximumIndex.multiply(BigDecimal.valueOf(iterableBase.getMaxValues()));
		}
		
		return maximumIndex;
	}
	
	public BigDecimal getCurrentIndex(){
		return currentIndex;
	}
	
	public void applyValues(){
//		Co.println("--> Apply values");
		for (AdjustmentBase adjustmentBase : listOfAdjustmentBase){
//			Co.println("--> Applied: " + adjustmentBase.getClass().getSimpleName() + ", " + adjustmentBase.description);
//			Co.println("--> Check: " + ((AdjustmentOfBasicInteger)adjustmentBase).getValue());
			adjustmentBase.applyValue();
		}
	}
	
	public ArrayList<AdjustmentBase> getListOfAdjustmentBase(){
		return listOfAdjustmentBase;
	}
}
