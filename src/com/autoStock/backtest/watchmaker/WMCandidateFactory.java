package com.autoStock.backtest.watchmaker;

import java.util.ArrayList;
import java.util.Random;

import org.uncommons.watchmaker.framework.factories.AbstractCandidateFactory;

import com.autoStock.account.AccountProvider;
import com.autoStock.account.BasicAccount;
import com.autoStock.adjust.AdjustmentBase;
import com.autoStock.algorithm.AlgorithmBase;
import com.autoStock.algorithm.DummyAlgorithm;
import com.autoStock.algorithm.core.AlgorithmRemodeler;
import com.autoStock.algorithm.core.AlgorithmDefinitions.AlgorithmMode;
import com.autoStock.algorithm.extras.StrategyOptionsOverride;
import com.autoStock.backtest.AlgorithmModel;

/**
 * @author Kevin Kowalewski
 *
 */
public class WMCandidateFactory extends AbstractCandidateFactory<AlgorithmModel>{
	public WMBacktestContainer wmBacktestContainer;
	private StrategyOptionsOverride soo;

	public WMCandidateFactory(WMBacktestContainer wmBacktestContainer, StrategyOptionsOverride soo) {
		this.wmBacktestContainer = wmBacktestContainer;
		this.soo = soo;
	}

	@Override
	public AlgorithmModel generateRandomCandidate(Random random) {
		DummyAlgorithm dummyAlgorithm = new DummyAlgorithm(wmBacktestContainer.exchange, wmBacktestContainer.symbol, AlgorithmMode.mode_backtest_with_adjustment, new BasicAccount(AccountProvider.defaultBalance));
		ArrayList<AdjustmentBase> listOfAdjustmentBase = new WMAdjustmentProvider().getListOfAdjustmentBase(dummyAlgorithm);
		
		for (AdjustmentBase adjustmentBase : listOfAdjustmentBase){
//			Co.println("--> Have adjustment: " + adjustmentBase.getDescription() + ", " + adjustmentBase.getClass().getSimpleName());
//			if (adjustmentBase instanceof AdjustmentOfSignalMetric){
//				Co.println("--> Adjustment min, max: " + ((IterableOfInteger)adjustmentBase.getIterableBase()).getMin() + ", " + ((IterableOfInteger)adjustmentBase.getIterableBase()).getMin() + ", " + ((IterableOfInteger)adjustmentBase.getIterableBase()).getStep());
				
				adjustmentBase.getIterableBase().randomize(random);			
				adjustmentBase.applyValue();
				
//				Co.println("--> And now " + ((AdjustmentOfSignalMetric)adjustmentBase).getValue());
//			}
		}
		
		if (soo != null){soo.override(dummyAlgorithm.strategyBase.strategyOptions);}
		
		AlgorithmModel algorithmModel = getCurrentAlgorithmModel(dummyAlgorithm, listOfAdjustmentBase);
		algorithmModel.wmAdjustment.listOfAdjustmentBase = listOfAdjustmentBase;
		
//		Co.println("--> Generated random candidate? " + algorithmModel.toString());
//		Co.println("--> " + algorithmModel.listOfSignalParameters.get(10).toString());
//		Co.println("--> CHECK **** " + algorithmModel.wmAdjustment.listOfAdjustmentBase.get(0).getIterableBase().getCurrentIndex());
		
//		Co.println("\n--> Candidate");
		
//		Co.println("--> " + algorithmModel.listOfSignalParameters.get(10).arrayOfSignalGuageForLongEntry[0].threshold);
//		Co.println("--> " + algorithmModel.listOfSignalParameters.get(10).arrayOfSignalGuageForLongExit[0].threshold);
//		Co.println("--> " + algorithmModel.listOfSignalParameters.get(10).arrayOfSignalGuageForShortEntry[0].threshold);
//		Co.println("--> " + algorithmModel.listOfSignalParameters.get(10).arrayOfSignalGuageForShortExit[0].threshold);
		
		return algorithmModel;
	}
	
	private AlgorithmModel getCurrentAlgorithmModel(AlgorithmBase algorithmBase, ArrayList<AdjustmentBase> listOfAdjustmentBase){
		AlgorithmModel algorithmModel = AlgorithmModel.getCurrentAlgorithmModel(algorithmBase);
		algorithmModel.wmAdjustment = new WMAdjustment();
		algorithmModel.wmAdjustment.listOfAdjustmentBase = listOfAdjustmentBase;
		
		return algorithmModel;
	}
}
