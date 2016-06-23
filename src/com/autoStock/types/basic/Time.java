package com.autoStock.types.basic;

/**
 * @author Kevin Kowalewski
 *
 */
public class Time {
	public int hours;
	public int minutes;
	public int seconds;
	
	public Time(){}
	
	public Time(int hours, int minutes, int seconds) {
		this.hours = hours;
		this.minutes = minutes;
		this.seconds = seconds;
	}

	public boolean isFuture(){
		if (hours >= 0 && minutes >= 0 && seconds >= 0){
			return true;
		}
		return false;
	}
	
	public boolean isPast(){
		if (hours <= 0 && minutes <= 0 && seconds <= 0){
			return true;
		}
		return false;
	}
	
	public int asSeconds(){
		return (hours *60 * 60) + (minutes * 60) + seconds;
	}
	
	@Override
	public String toString(){
		return "Time: " + hours + " : " + minutes + " : " + seconds;
	}
}
