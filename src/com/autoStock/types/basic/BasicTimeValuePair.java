/**
 * 
 */
package com.autoStock.types.basic;

import java.util.Date;

/**
 * @author Kevin Kowalewski
 *
 */
public class BasicTimeValuePair{
	public Date date;
	public String value;
	
	public BasicTimeValuePair(Date date, String value){
		this.date = date;
		this.value = value;
	}
}