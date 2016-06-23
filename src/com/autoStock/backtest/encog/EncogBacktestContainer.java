package com.autoStock.backtest.encog;

import java.util.ArrayList;
import java.util.Date;

import com.autoStock.Co;
import com.autoStock.account.AccountProvider;
import com.autoStock.account.BasicAccount;
import com.autoStock.algorithm.DummyAlgorithm;
import com.autoStock.algorithm.core.AlgorithmDefinitions.AlgorithmMode;
import com.autoStock.algorithm.extras.StrategyOptionsOverride;
import com.autoStock.backtest.AlgorithmModel;
import com.autoStock.backtest.BacktestEvaluationReader;
import com.autoStock.backtest.BacktestUtils;
import com.autoStock.backtest.encog.TrainEncogSignal.EncogNetworkType;
import com.autoStock.signal.signalMetrics.SignalOfEncog;
import com.autoStock.strategy.StrategyOptionDefaults;
import com.autoStock.tools.DateTools;
import com.autoStock.trading.platform.ib.definitions.HistoricalDataDefinitions.Resolution;
import com.autoStock.trading.types.HistoricalData;
import com.autoStock.types.Exchange;
import com.autoStock.types.Symbol;

/**
 * @author Kevin Kowalewski
 *
 */
public class EncogBacktestContainer {
	private static boolean USE_SO_OVERRIDE = true;
	public DummyAlgorithm algorithm;
	public Symbol symbol;
	public Exchange exchange;
	public Date dateStart;
	public Date dateEnd;
	private TrainEncogSignal trainEncogSignal;
	private HistoricalData historicalData;
	private int currentDay;
	public static enum Mode {day_over_day, full}
	private final Mode MODE = Mode.day_over_day;
//	private final Mode MODE = Mode.full;

	public EncogBacktestContainer(Symbol symbol, Exchange exchange, Date dateStart, Date dateEnd) {
		this.symbol = symbol;
		this.exchange = exchange;
		this.dateStart = dateStart;
		this.dateEnd = dateEnd;
		
		this.historicalData = new HistoricalData(exchange, symbol, dateStart, dateEnd, Resolution.min);
		this.historicalData.setStartAndEndDatesToExchange();
	
		algorithm = new DummyAlgorithm(exchange, symbol, AlgorithmMode.mode_backtest_with_adjustment, new BasicAccount(AccountProvider.defaultBalance));
	}
	
	public void runBacktest(){
		StrategyOptionsOverride strategyOptionsOverride = StrategyOptionDefaults.getDefaultOverride();
		
		if (MODE == Mode.full){
			trainEncogSignal = new TrainEncogSignal(AlgorithmModel.getEmptyModel(), historicalData, false, "full", MODE);
			blankNetwork();
			trainEncogSignal.execute(BacktestEvaluationReader.getPrecomputedModel(exchange, symbol, USE_SO_OVERRIDE ? strategyOptionsOverride : null), 0);
			trainEncogSignal.getTrainer().saveNetwork();
		}else{
			ArrayList<HistoricalData> listOfHistoricalData = BacktestUtils.getHistoricalDataListForDates(historicalData);
			
			for (HistoricalData historicalDataIn : listOfHistoricalData){
				trainEncogSignal = new TrainEncogSignal(AlgorithmModel.getEmptyModel(), historicalDataIn, false, "day-" + DateTools.getEncogDate(historicalDataIn.startDate), MODE);
				Co.println("--> Blanking the network... " + DateTools.getPretty(historicalDataIn.startDate));
				blankNetwork();
				trainEncogSignal.execute(BacktestEvaluationReader.getPrecomputedModel(exchange, symbol, USE_SO_OVERRIDE ? strategyOptionsOverride : null), 0);
				trainEncogSignal.getTrainer().saveNetwork();
				currentDay++;
			}
		}
	}
	
	private void blankNetwork(){
		if (SignalOfEncog.encogNetworkType == EncogNetworkType.basic){
			trainEncogSignal.getTrainer().saveNetwork();
		}else if (SignalOfEncog.encogNetworkType == EncogNetworkType.neat){
			trainEncogSignal.setDetails(BacktestEvaluationReader.getPrecomputedModel(exchange, symbol));
			for (int i=0; i<TrainEncogSignal.TRAINING_ITERATIONS; i++){
				trainEncogSignal.getTrainer().train(1, 0);
				if (trainEncogSignal.getTrainer().bestScore != 0){trainEncogSignal.getTrainer().saveNetwork(); break;}
			}
		}
	}
}
