package com.autoStock.types.basic;

/**
 * @author Kevin Kowalewski
 *
 */
public class MutableEnum<T extends Enum<T>> {
	public T value;
	
	public MutableEnum(){
		
	}
	
	public MutableEnum(T enumObject){
		this.value = enumObject;
	}
}
