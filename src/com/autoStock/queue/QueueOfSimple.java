package com.autoStock.queue;

import java.util.concurrent.atomic.AtomicBoolean;

import com.autoStock.tools.Lock;

/**
 * @author Kevin Kowalewski
 *
 */
public class QueueOfSimple {
	private AtomicBoolean completed = new AtomicBoolean();
	private Thread threadForQueue;
	private Lock lock = new Lock();
	private volatile Runnable runnable;
//	
//	public QueueOfSimple(){
//		threadForQueue = new Thread(new Runnable(){
//			@Override
//			public void run() {
//				while (true){
//					try {Thread.sleep(1);}catch(InterruptedException e){throw new IllegalStateException();}
//					synchronized (lock) {
//						if (runnable != null){
//							runnable.run();
//							completed.set(true);
//							runnable = null;
//						}
//					}
//				}
//			}
//		});
//		
//		threadForQueue.start();
//	}
//	
//	public void addRunnable(Runnable inboundRunnable){
//		synchronized (lock) {
//			runnable = inboundRunnable;
//			completed.set(false);
//			
//			while (completed.get() != true){
//				try {Thread.sleep(1);}catch(InterruptedException e){throw new IllegalStateException();}
//			}
//		}
//	}
}
