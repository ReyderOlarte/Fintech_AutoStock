package com.autoStock.types.basic;

/**
 * @author Kevin Kowalewski
 *
 */
public class MutableBoolean {
	public boolean value;
	
	public MutableBoolean(){
		value = false;
	}
	
	public MutableBoolean(boolean value){
		this.value = value;
	}
}
