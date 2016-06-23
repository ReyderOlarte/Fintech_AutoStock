package com.autoStock.algorithm;

import java.util.Arrays;
import java.util.Date;

import org.apache.commons.lang3.ArrayUtils;

import com.autoStock.Co;
import com.autoStock.account.BasicAccount;
import com.autoStock.algorithm.core.AlgorithmDefinitions.AlgorithmMode;
import com.autoStock.comServer.ContextOfOHLC;
import com.autoStock.context.ContextOfChangeSinceHighLow;
import com.autoStock.context.ContextOfChangeSinceOpen;
import com.autoStock.context.ContextOfPosition;
import com.autoStock.indicator.IndicatorOfSAR;
import com.autoStock.premise.PremiseOfOHLC;
import com.autoStock.signal.SignalDefinitions.SignalMetricType;
import com.autoStock.signal.extras.EncogFrame;
import com.autoStock.signal.extras.EncogFrame.FrameType;
import com.autoStock.signal.extras.EncogSubframe;
import com.autoStock.strategy.StrategyOfTest;
import com.autoStock.tables.TableController;
import com.autoStock.tables.TableDefinitions.AsciiTables;
import com.autoStock.tools.DateTools;
import com.autoStock.tools.ListTools;
import com.autoStock.tools.ThreadTools;
import com.autoStock.trading.platform.ib.definitions.HistoricalDataDefinitions.Resolution;
import com.autoStock.trading.types.HistoricalData;
import com.autoStock.types.Exchange;
import com.autoStock.types.QuoteSlice;
import com.autoStock.types.Symbol;

/**
 * @author Kevin Kowalewski
 * 
 */
public class AlgorithmTest extends AlgorithmBase {
	public AlgorithmTest(Exchange exchange, Symbol symbol, AlgorithmMode algorithmMode, BasicAccount basicAccount) {
		super(exchange, symbol, algorithmMode, basicAccount);
		setStrategy(new StrategyOfTest(this));
	}

	public void init(Date startingDate, Date endDate){
		this.startingDate = startingDate;
		this.endDate = endDate;
		
		if (analyizeUsedOnly){
			setAnalyzeAndActive(strategyBase.strategyOptions.listOfSignalMetricType, strategyBase.strategyOptions.listOfSignalMetricType);
		}else{
			setAnalyzeAndActive(SignalMetricType.asList(), strategyBase.strategyOptions.listOfSignalMetricType);
		}
		
////		if (algorithmMode == AlgorithmMode.mode_backtest_single){
//			setAnalyzeAndActive(ListTools.getList(Arrays.asList(
//			new SignalMetricType[]{SignalMetricType.metric_cci, SignalMetricType.metric_di, SignalMetricType.metric_rsi, SignalMetricType.metric_uo, SignalMetricType.metric_trix, SignalMetricType.metric_willr, SignalMetricType.metric_crossover}))
//			, strategyBase.strategyOptions.listOfSignalMetricType);
////			setAnalyzeAndActive(SignalMetricType.asList(), strategyBase.strategyOptions.listOfSignalMetricType);
////		}else{
////			setAnalyzeAndActive(SignalMetricType.asList(), strategyBase.strategyOptions.listOfSignalMetricType); //ListTools.getList(Arrays.asList(new SignalMetricType[]{SignalMetricType.metric_cci})));
////		}

		initialize();
		
		if (strategyBase.strategyOptions.enablePrefill){
			prefill();
		}
		
//		if (strategyBase.strategyOptions.enablePremise){
////			Co.println("--> Today: " + DateTools.getPrettyDate(startingDate));
////			Co.println("--> Earliest weekday: " + DateTools.getPrettyDate(DateTools.getFirstWeekdayBefore(startingDate)));
//			premiseController.reset();
//			premiseController.addPremise(new PremiseOfOHLC(exchange, symbol, DateTools.getFirstWeekdayBefore(startingDate), Resolution.hour, 3));
//			premiseController.determinePremise();
//		}
		
		if (strategyBase.strategyOptions.enableContext){
			contextController.reset();
			contextController.addContext(new ContextOfPosition());
			contextController.addContext(new ContextOfChangeSinceOpen());
			contextController.addContext(new ContextOfChangeSinceHighLow());
			//contextController.addContext(new ContextOfOHLC());
		}
	}

	@Override
	public void receiveQuoteSlice(QuoteSlice quoteSlice) {
		receivedQuoteSlice(quoteSlice);
			
		if (listOfQuoteSlice.size() >= getPeriodLength()) {
			//Co.print("\n --> QS: " + quoteSlice.toString());
			
			if (signalCache != null && signalCache.isAvailable()){
				//Co.println("--> Using cache");
				signalCache.setToQuoteSlice(quoteSlice, receiveIndex);
			}else{
				commonAnalysisData.setAnalysisData(listOfQuoteSlice);
				indicatorGroup.setDataSet();
				indicatorGroup.analyze();
				signalGroup.generateSignals(commonAnalysisData, position);
			}
				
			if (strategyBase.strategyOptions.enableContext && contextController.isEmpty() == false){
				((ContextOfPosition)contextController.getByClass(ContextOfPosition.class)).setPosition(position);
				((ContextOfChangeSinceOpen)contextController.getByClass(ContextOfChangeSinceOpen.class)).setCurrentQuoteSlice(firstQuoteSlice, quoteSlice);
				((ContextOfChangeSinceHighLow)contextController.getByClass(ContextOfChangeSinceHighLow.class)).setCurrentQuoteSlice(quoteSlice, listOfQuoteSlicePersistForDay);
//				((ContextOfOHLC)contextController.getByClass(ContextOfOHLC.class)).setAlgorithmBase(this);
				contextController.determineContext();
			}
			
			//if (strategyBase.strategyOptions.listOfSignalMetricType.contains(SignalMetricType.metric_encog)){
				signalGroup.processEncog(ListTools.combineLists(contextController.getEncogFrames(), premiseController.getEncogFrames()));
			//}
			
			baseInformStrategy(quoteSlice);
		}
		
		finishedReceiveQuoteSlice();
	}

	@Override
	public void endOfFeed(Symbol symbol) {
		if (algorithmMode.displayChart) {
			algorithmChart.display();
		}
		if (algorithmMode.displayTable) {
			Co.println("--> " + symbol.name);
			new TableController().displayTable(tableForAlgorithm.INCLUDE_SIGNALS ? AsciiTables.algorithm : AsciiTables.algorithm_no_signals, tableForAlgorithm.getDisplayRows());
		}
		//if (algorithmListener != null) {algorithmListener.endOfAlgorithm();}
		//if (signalCache.getSignalCachePackage() == null){signalCache.storeSignalGroup();}
	}
}
