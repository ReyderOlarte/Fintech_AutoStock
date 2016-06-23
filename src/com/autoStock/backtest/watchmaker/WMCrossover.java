package com.autoStock.backtest.watchmaker;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.uncommons.watchmaker.framework.operators.AbstractCrossover;

import com.autoStock.adjust.AdjustmentBase;
import com.autoStock.algorithm.DummyAlgorithm;
import com.autoStock.algorithm.core.AlgorithmDefinitions.AlgorithmMode;
import com.autoStock.algorithm.core.AlgorithmRemodeler;
import com.autoStock.backtest.AlgorithmModel;

/**
 * @author Kevin Kowalewski
 *
 */
public class WMCrossover extends AbstractCrossover<AlgorithmModel> {
	public WMCrossover(int crossoverPoints) {
		super(crossoverPoints);
	}

	@Override
	protected List<AlgorithmModel> mate(AlgorithmModel parent1, AlgorithmModel parent2, int crossoverPoints, Random random) {
//		Co.println("--> Asked to crossover");
		ArrayList<AlgorithmModel> listOfAlgorithmModel = new ArrayList<AlgorithmModel>();
		
		if (parent1 == parent2){
//			Co.println("--> Warning parent1 and 2 are the same");
//			throw new IllegalArgumentException("Parent 1 and 2 are the same");
		}
		
		if (parent1 == null || parent2 == null){
			throw new IllegalArgumentException("Parent is null: " + parent1 + ", " + parent2);
		}
		
		if (crossoverPoints != 1){
			throw new UnsupportedOperationException("Can't handle crossover points other than 1");
		}
		
//		if (parent1.wmAdjustment.listOfAdjustmentBase.size() % (crossoverPoints+1) != 0){
//			throw new IllegalArgumentException("Crossover point remainder needs to be zero: " + (parent1.wmAdjustment.listOfAdjustmentBase.size() % (crossoverPoints+1) != 0));
//		}
		
		int crossoverSize = parent1.wmAdjustment.listOfAdjustmentBase.size() / (crossoverPoints +1);
		
//		Co.println("--> Crossover size:" + crossoverSize);
		
		AlgorithmModel offspring1 = parent1.copy();
		AlgorithmModel offspring2 = parent2.copy();
		
		DummyAlgorithm dummyAlgorithmFor1 = new DummyAlgorithm(null, null, AlgorithmMode.mode_backtest_with_adjustment, null);
		DummyAlgorithm dummyAlgorithmFor2 = new DummyAlgorithm(null, null, AlgorithmMode.mode_backtest_with_adjustment, null);
		
		new AlgorithmRemodeler(dummyAlgorithmFor1, offspring1).remodel();
		new AlgorithmRemodeler(dummyAlgorithmFor2, offspring2).remodel();
		
		ArrayList<AdjustmentBase> listOfAdjustmentBaseForOffspring1 = new WMAdjustmentProvider().getListOfAdjustmentBase(dummyAlgorithmFor1);
		ArrayList<AdjustmentBase> listOfAdjustmentBaseForOffspring2 = new WMAdjustmentProvider().getListOfAdjustmentBase(dummyAlgorithmFor2);
		
		int index = 0;
		
//		for (AdjustmentBase adjustmentBase : parent1.wmAdjustment.listOfAdjustmentBase){
//			Co.println("--> Parent 1 indexes: " + adjustmentBase.getIterableBase().getCurrentIndex());
//		}
//		
//		for (AdjustmentBase adjustmentBase : parent2.wmAdjustment.listOfAdjustmentBase){
//			Co.println("--> Parent 2 indexes: " + adjustmentBase.getIterableBase().getCurrentIndex());
//		}
		
		for (AdjustmentBase adjustmentBase : listOfAdjustmentBaseForOffspring1){
			if (index < crossoverSize){
				adjustmentBase.getIterableBase().setCurrentIndex(parent1.wmAdjustment.listOfAdjustmentBase.get(index).getIterableBase().getCurrentIndex());
				adjustmentBase.applyValue();
//				Co.println("--> Parent 1 Index: " + adjustmentBase.getIterableBase().getCurrentIndex());
			}else{
				adjustmentBase.getIterableBase().setCurrentIndex(parent1.wmAdjustment.listOfAdjustmentBase.get(index).getIterableBase().getCurrentIndex());
				adjustmentBase.applyValue();				
//				Co.println("--> Parent 2 Index: " + adjustmentBase.getIterableBase().getCurrentIndex());
			}
			index++;
		}
		
//		Co.println(" Offspring 2");
		
		index = 0;
		
		for (AdjustmentBase adjustmentBase : listOfAdjustmentBaseForOffspring2){
			if (index < crossoverSize){
				adjustmentBase.getIterableBase().setCurrentIndex(parent2.wmAdjustment.listOfAdjustmentBase.get(index).getIterableBase().getCurrentIndex());
				adjustmentBase.applyValue();
//				Co.println("--> Parent 2 Index: " + adjustmentBase.getIterableBase().getCurrentIndex());
			}else{
				adjustmentBase.getIterableBase().setCurrentIndex(parent1.wmAdjustment.listOfAdjustmentBase.get(index).getIterableBase().getCurrentIndex());
				adjustmentBase.applyValue();				
//				Co.println("--> Parent 1 Index: " + adjustmentBase.getIterableBase().getCurrentIndex());
			}
			index++;
		}
//		
//		Co.print(getStringOfParams(dummyAlgorithmFor1.signalGroup.signalOfUO.signalParameters));
//		Co.print(getStringOfParams(dummyAlgorithmFor2.signalGroup.signalOfUO.signalParameters));
		
		AlgorithmModel algorithmModelForOffspring1 = AlgorithmModel.getCurrentAlgorithmModel(dummyAlgorithmFor1);
		AlgorithmModel algorithmModelForOffspring2 = AlgorithmModel.getCurrentAlgorithmModel(dummyAlgorithmFor2);
		
		algorithmModelForOffspring1.wmAdjustment = new WMAdjustment();
		algorithmModelForOffspring2.wmAdjustment = new WMAdjustment();
		
		algorithmModelForOffspring1.wmAdjustment.listOfAdjustmentBase = listOfAdjustmentBaseForOffspring1;
		algorithmModelForOffspring2.wmAdjustment.listOfAdjustmentBase = listOfAdjustmentBaseForOffspring2;
		
		listOfAlgorithmModel.add(algorithmModelForOffspring1);
		listOfAlgorithmModel.add(algorithmModelForOffspring2);
		
		return listOfAlgorithmModel;
	}
}