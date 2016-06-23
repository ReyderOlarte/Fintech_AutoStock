package com.autoStock.adjust;

import com.autoStock.adjust.AdjustmentCampaign.AdjustmentType;
import com.autoStock.signal.SignalBase;
import com.autoStock.signal.SignalDefinitions.SignalGuageType;
import com.autoStock.signal.SignalDefinitions.SignalPointType;

/**
 * @author Kevin Kowalewski
 *
 */
public class AdjustmentOfSignalMetricThreshold extends AdjustmentBase {
	public final SignalBase signalBase;
	public final AdjustmentType adjustmentType;
	
	public AdjustmentOfSignalMetricThreshold(SignalBase signalBase, AdjustmentType adjustmentType, IterableBase iterableBase){
		this.iterableBase = iterableBase;
		this.description = signalBase.signalMetricType.name() + ", " + adjustmentType.name(); 
		this.adjustmentType = adjustmentType;
		this.signalBase = signalBase;
	}

	@Override
	public void applyValue() {
		try {
			if (adjustmentType == AdjustmentType.signal_metric_long_entry){
				signalBase.signalParameters.getGuagesForType(SignalPointType.long_entry, SignalGuageType.guage_threshold_met, SignalGuageType.guage_threshold_left).get(0).threshold = ((IterableOfDouble)iterableBase).getDouble();			
			}else if (adjustmentType == AdjustmentType.signal_metric_long_exit){
				signalBase.signalParameters.getGuagesForType(SignalPointType.long_exit, SignalGuageType.guage_threshold_met, SignalGuageType.guage_threshold_left).get(0).threshold = ((IterableOfDouble)iterableBase).getDouble();
			}else if (adjustmentType == AdjustmentType.signal_metric_short_entry){
				signalBase.signalParameters.getGuagesForType(SignalPointType.short_entry, SignalGuageType.guage_threshold_met, SignalGuageType.guage_threshold_left).get(0).threshold = ((IterableOfDouble)iterableBase).getDouble();
			}else if (adjustmentType == AdjustmentType.signal_metric_short_exit){
				signalBase.signalParameters.getGuagesForType(SignalPointType.short_exit, SignalGuageType.guage_threshold_met, SignalGuageType.guage_threshold_left).get(0).threshold = ((IterableOfDouble)iterableBase).getDouble();
			}else {
				throw new UnsupportedOperationException("Unknown adjustment type: " + adjustmentType.name());
			}
		}catch(IndexOutOfBoundsException e){
			//pass, could be none so nothing to adjust
		}
		
//		Co.println("--> Adjusted: " + adjustmentType.name() + ", " + iterableBase.getCurrentIndex() + "," + ((IterableOfInteger)iterableBase).getInt());
	}
	
	public double getValue(){
		return ((IterableOfDouble)iterableBase).getDouble();
	}
}
