package com.autoStock.cluster;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import com.autoStock.backtest.AlgorithmModel;
import com.autoStock.types.Exchange;
import com.autoStock.types.Symbol;

/**
 * @author Kevin Kowalewski
 *
 */
public class ComputeUnitForBacktest {
	public Exchange exchange;
	public Date dateStart;
	public Date dateEnd;
	public long requestId;
	public HashMap<Symbol, ArrayList<AlgorithmModel>> hashOfAlgorithmModel = new HashMap<Symbol, ArrayList<AlgorithmModel>>(); 
			
	public ComputeUnitForBacktest(long requestId, Exchange exchange, Date dateStart, Date dateEnd) {
		this.requestId = requestId;
		this.exchange = exchange;
		this.dateStart = dateStart;
		this.dateEnd = dateEnd;
	}
}
