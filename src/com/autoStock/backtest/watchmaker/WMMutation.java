package com.autoStock.backtest.watchmaker;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.uncommons.maths.number.ConstantGenerator;
import org.uncommons.maths.number.NumberGenerator;
import org.uncommons.maths.random.Probability;
import org.uncommons.watchmaker.framework.EvolutionaryOperator;

import com.autoStock.adjust.AdjustmentBase;
import com.autoStock.algorithm.DummyAlgorithm;
import com.autoStock.algorithm.core.AlgorithmDefinitions.AlgorithmMode;
import com.autoStock.algorithm.core.AlgorithmRemodeler;
import com.autoStock.backtest.AlgorithmModel;

/**
 * @author Kevin Kowalewski
 *
 */
public class WMMutation implements EvolutionaryOperator<AlgorithmModel> {
	private NumberGenerator<Probability> mutationProbability;
	
	public WMMutation(Probability probability){
		this.mutationProbability = new ConstantGenerator<Probability>(probability);
	}
	
	@Override
	public List<AlgorithmModel> apply(List<AlgorithmModel> listOfAlgorithmModel, Random random) {
//		Co.println("--> Asked to mutate");
		ArrayList<AlgorithmModel> list = new ArrayList<AlgorithmModel>();
		
		for (AlgorithmModel algorithmModel : listOfAlgorithmModel){
			list.add(mutate(algorithmModel.copy(), random));
		}
		
		return list;
	}
	
	private AlgorithmModel mutate(AlgorithmModel algorithmModel, Random random){
		DummyAlgorithm dummyAlgorithm = new DummyAlgorithm(null, null, AlgorithmMode.mode_backtest_with_adjustment, null);
		new AlgorithmRemodeler(dummyAlgorithm, algorithmModel).remodel();
		ArrayList<AdjustmentBase> listOfAdjustmentBase = new WMAdjustmentProvider().getListOfAdjustmentBase(dummyAlgorithm);
		
		int index = 0;
		
		for (AdjustmentBase adjustmentBase : listOfAdjustmentBase){
			adjustmentBase.getIterableBase().setCurrentIndex(algorithmModel.wmAdjustment.listOfAdjustmentBase.get(index).getIterableBase().getCurrentIndex());
			index++;
//			Co.println("--> Index: " + adjustmentBase.getIterableBase().getCurrentIndex());
		}
		
		for (AdjustmentBase adjustmentBase : listOfAdjustmentBase){
			if (mutationProbability.nextValue().nextEvent(random)){
				adjustmentBase.getIterableBase().randomize(random);
				adjustmentBase.applyValue();
			}else{
				//pass
			}
		}
		
		AlgorithmModel algorithmModelNew = AlgorithmModel.getCurrentAlgorithmModel(dummyAlgorithm);
		algorithmModelNew.wmAdjustment = new WMAdjustment();
		algorithmModelNew.wmAdjustment.listOfAdjustmentBase = listOfAdjustmentBase;
		
//		Co.println(getStringOfParams(dummyAlgorithm.signalGroup.signalOfUO.signalParameters));
		
		return algorithmModelNew;
	}
}
