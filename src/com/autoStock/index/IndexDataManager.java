package com.autoStock.index;

/**
 * @author Kevin Kowalewski
 *
 */
public class IndexDataManager {
	private static IndexDataManager instance = new IndexDataManager();
	
	public IndexDataManager getInstance(){
		return instance;
	}
}
