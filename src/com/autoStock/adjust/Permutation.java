package com.autoStock.adjust;

import java.util.ArrayList;

import com.autoStock.Co;

/**
 * @author Kevin Kowalewski
 *
 */
public class Permutation {
	private ArrayList<IterableBase> listOfIterableBase;
	
	public Permutation(ArrayList<IterableBase> listOfIterableBase){
		this.listOfIterableBase = listOfIterableBase;
	}
	
	public boolean masterIterate(){
		if (allDone()){
			return false;
		}
		
		for (IterableBase iterableBase : listOfIterableBase){
			if (iterableBase.skip()){
				continue;
			}
			
			iterableBase.iterate();
			
			if (iterableBase.hasMore() == false){
				iterableBase.reset();
				continue;
			}
			
			break;
		}
		
		return true;
	}
	
	public boolean allDone(){
		for (IterableBase iterableBase : listOfIterableBase){
			if (iterableBase.skip()){
				continue;
			}
			if (iterableBase.isDone() == false){
				return false;
			}
		}	
		return true;
	}
	
	public void printIterableSet(){
		for (IterableBase iterableBase : listOfIterableBase){
			Co.print("--> Iterated ");
			if (iterableBase instanceof IterableOfInteger){
				Co.print(((IterableOfInteger)iterableBase).getInt() + " ");
			}else if (iterableBase instanceof IterableOfDouble){
				Co.print(((IterableOfDouble)iterableBase).getDouble() + " ");
			}else if (iterableBase instanceof IterableOfBoolean){
				Co.print(((IterableOfBoolean)iterableBase).getBoolean() + " ");
			}else if (iterableBase instanceof IterableOfEnum){
				Co.print(((IterableOfEnum)iterableBase).getEnumObject().getSimpleName() + " " + ((IterableOfEnum)iterableBase).getEnum().name());
			}
		}
		
		Co.println("");
	}
}
