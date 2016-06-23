package com.autoStock.tools;


/**
 * @author Kevin Kowalewski
 *
 */
public class Lock extends Object{
	//Synchronize on this
	
	public void blockThread(){
		synchronized(this){
//			if (Thread.currentThread().getState() == State.WAITING){
//				throw new IllegalStateException("Thread already blocked");
//			}
			
			try {wait();} catch (InterruptedException e) {e.printStackTrace();}
		}
	}
	
	public void releaseThread(){
		synchronized (this) {
//			if (Thread.currentThread().getState() != State.WAITING){
//				Co.println("--> State: " + Thread.currentThread().getState().name());
//				throw new IllegalStateException("Thread wasn't blocked");
//			}
			
			notify();
		}
	}
}
