/**
 * 
 */
package com.autoStock.backtest.watchmaker;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import org.uncommons.watchmaker.framework.EvaluatedCandidate;
import org.uncommons.watchmaker.framework.islands.Migration;

/**
 * @author Kevin
 *
 */
public class WMIslandMigration implements Migration {
	//Modified to migrate the top n fittest candidates to all islands, reducing the random migrant count
	
	@Override
	public <T> void migrate(List<List<EvaluatedCandidate<T>>> islandPopulations, int migrantCount, Random rng) {
		int includeFittest = migrantCount / 4;
		migrantCount -= includeFittest;
		
        // The first batch of immigrants is from the last island to the first.
        List<EvaluatedCandidate<T>> lastIsland = islandPopulations.get(islandPopulations.size() - 1);
        Collections.shuffle(lastIsland, rng);
        List<EvaluatedCandidate<T>> migrants = lastIsland.subList(lastIsland.size() - migrantCount, lastIsland.size());
        
//        Co.println("--> Migrating: " + migrantCount);
        
        List<EvaluatedCandidate<T>> listOfFittestCandidates = getFittestCandidates(islandPopulations);
        Collections.reverse(listOfFittestCandidates);
        
//        Co.println("--> Fittest candidates");
//        
//		for (EvaluatedCandidate<T> candidate : listOfFittestCandidates){
//			Co.println("--> Candidate: " + candidate.getFitness());
//		}

        for (List<EvaluatedCandidate<T>> island : islandPopulations) {
        	List<EvaluatedCandidate<T>> immigrants = migrants;
        	
            if (island != lastIsland){
                // Select the migrants that will move to the next island to make room for the immigrants here.
                // Randomise the population so that there is no bias concerning which individuals are migrated.
                Collections.shuffle(island, rng);
            	
                migrants = new ArrayList<EvaluatedCandidate<T>>(island.subList(island.size() - migrantCount, island.size()));
            }
            // Copy the immigrants over the last members of the population (those that are themselves
            // migrating to the next island).
            for (int i = 0; i < immigrants.size(); i++){
                island.set(island.size() - migrantCount - includeFittest + i, immigrants.get(i));
            }
            
            for (int i = 0; i < includeFittest; i++){
            	island.set((island.size() - includeFittest) + i, listOfFittestCandidates.get(i));
            	//Co.println("--> Setting fit candidate: " + listOfFittestCandidates.get(i).getFitness() + " into island: " + islandPopulations.indexOf(island));
            }
        }
	}
	
	private <T> List<EvaluatedCandidate<T>> getFittestCandidates(List<List<EvaluatedCandidate<T>>> listOfIslands){
		ArrayList<EvaluatedCandidate<T>> listOfFittestCandidates = new ArrayList<EvaluatedCandidate<T>>();
		
		for (List<EvaluatedCandidate<T>> island : listOfIslands){
			Collections.sort(island);
			
			listOfFittestCandidates.addAll(island.subList(island.size() - 8, island.size()));
		}
		
		Collections.sort(listOfFittestCandidates);
		
		return listOfFittestCandidates;
	}
}
