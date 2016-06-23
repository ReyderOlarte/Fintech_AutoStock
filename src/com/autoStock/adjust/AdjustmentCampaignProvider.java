package com.autoStock.adjust;

import java.util.ArrayList;

import com.autoStock.types.Symbol;
import com.google.gson.internal.Pair;

/**
 * @author Kevin Kowalewski
 *
 */
public class AdjustmentCampaignProvider {
	private static AdjustmentCampaignProvider adjustmentCampaignProvider = new AdjustmentCampaignProvider();
	private ArrayList<Pair<AdjustmentIdentifier, AdjustmentCampaign>> listOfPair =  new ArrayList<Pair<AdjustmentIdentifier, AdjustmentCampaign>>();
	
	public static enum AdjustmentTarget {
		target_algorithm,
		target_global,
		none,
	}
	
	private AdjustmentCampaignProvider(){}
	
	public static AdjustmentCampaignProvider getInstance(){
		return adjustmentCampaignProvider;
	}
	
	public AdjustmentCampaign getAdjustmentCampaignForAlgorithm(Symbol symbol){			
		for (Pair<AdjustmentIdentifier, AdjustmentCampaign> pair : listOfPair){
			if (pair.first.identifier.equals(symbol)){
				return pair.second;
			}
		}
		
		return null;
	}
	
	public void addAdjustmentCampaignForAlgorithm(AdjustmentCampaign adjustmentCampaign, Symbol symbol){
		listOfPair.add(new Pair<AdjustmentIdentifier, AdjustmentCampaign>(new AdjustmentIdentifier(AdjustmentTarget.target_algorithm, symbol), adjustmentCampaign));
	}
	
	public ArrayList<Pair<AdjustmentIdentifier, AdjustmentCampaign>> getListOfAdjustmentCampaign(){
		return listOfPair;
	}

	public void applyBoilerplateValues() {
		throw new UnsupportedOperationException();
	}

	public boolean runBoilerplateAdjustment() {
		throw new UnsupportedOperationException();
	}
	
	public boolean isRebasingIndividual(){
		for (Pair<AdjustmentIdentifier, AdjustmentCampaign> pair : listOfPair) {
			if (pair.second.isRebasing){
				return true;
			}
		}
		
		return false;
	}
} 
