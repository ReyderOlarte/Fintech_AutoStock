package com.autoStock.adjust;

import java.util.ArrayList;
import java.util.Random;

/**
 * @author Kevin Kowalewski
 * @param <E>
 *
 */
public class IterableOfEnum<E extends Enum<E>> extends IterableBase {
	private E enumObject;
	private ArrayList<E> listOfEnum = new ArrayList<E>();
	
	public IterableOfEnum(E... enumObjects){
		this.enumObject = enumObjects[0];
		
		for (E enumbObjectLocal : enumObjects){
//			Co.println("--> Added:" + enumbObjectLocal.name());
			listOfEnum.add(enumbObjectLocal);
		}
	}
	
	public E getEnum() {
		return listOfEnum.get(currentIndex);
	}
	
	@Override
	public void randomize(Random random) {
		super.randomize(random);
	}
	
	public Class<E> getEnumObject(){
		return enumObject.getDeclaringClass();
	}
	
	@Override
	public boolean hasMore() {
		return currentIndex <= getMaxIndex();
	}

	@Override
	public int getMaxIndex() {
		return getMaxValues()-1;
	}

	@Override
	public int getMaxValues() {
		return listOfEnum.size();
	}

	@Override
	public boolean isDone() {
		return currentIndex == getMaxIndex();
	}

	@Override
	public boolean skip() {
		return false;
	}
}
