/**
 * 
 */
package com.autoStock.algorithm;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;

import com.autoStock.Co;
import com.autoStock.account.AccountProvider;
import com.autoStock.account.BasicAccount;
import com.autoStock.algorithm.core.AlgorithmChart;
import com.autoStock.algorithm.core.AlgorithmDefinitions.AlgorithmMode;
import com.autoStock.algorithm.core.AlgorithmListener;
import com.autoStock.algorithm.core.AlgorithmState;
import com.autoStock.algorithm.reciever.ReceiverOfQuoteSlice;
import com.autoStock.context.ContextController;
import com.autoStock.indicator.CommonAnalysisData;
import com.autoStock.indicator.IndicatorGroup;
import com.autoStock.position.ListenerOfPositionStatusChange;
import com.autoStock.position.PositionDefinitions.PositionType;
import com.autoStock.position.PositionGovernor;
import com.autoStock.position.PositionGovernorResponse;
import com.autoStock.position.PositionGovernorResponseStatus;
import com.autoStock.position.PositionManager;
import com.autoStock.position.PositionOptions;
import com.autoStock.premise.PremiseController;
import com.autoStock.retrospect.Prefill;
import com.autoStock.retrospect.Prefill.PrefillMethod;
import com.autoStock.signal.SignalCache;
import com.autoStock.signal.SignalDefinitions.SignalMetricType;
import com.autoStock.signal.SignalGroup;
import com.autoStock.strategy.StrategyBase;
import com.autoStock.strategy.StrategyResponse;
import com.autoStock.strategy.StrategyResponse.StrategyAction;
import com.autoStock.strategy.StrategyResponse.StrategyActionCause;
import com.autoStock.tables.TableForAlgorithm;
import com.autoStock.tools.Benchmark;
import com.autoStock.tools.DateTools;
import com.autoStock.tools.ListTools;
import com.autoStock.tools.MathTools;
import com.autoStock.tools.MiscTools;
import com.autoStock.trading.platform.ib.definitions.HistoricalDataDefinitions.Resolution;
import com.autoStock.trading.types.Position;
import com.autoStock.trading.yahoo.FundamentalData;
import com.autoStock.types.Exchange;
import com.autoStock.types.QuoteSlice;
import com.autoStock.types.Symbol;

/**
 * @author Kevin Kowalewski
 *
 */
public abstract class AlgorithmBase implements ListenerOfPositionStatusChange, ReceiverOfQuoteSlice {
	private int periodLength;
	public Exchange exchange;
	public Symbol symbol;
	public AlgorithmState algorithmState = new AlgorithmState();
	public AlgorithmMode algorithmMode;
	public AlgorithmListener algorithmListener;
	public AlgorithmChart algorithmChart;
	public TableForAlgorithm tableForAlgorithm;
	public IndicatorGroup indicatorGroup;
	public SignalGroup signalGroup;
	public CommonAnalysisData commonAnalysisData = new CommonAnalysisData();
	public final PositionGovernor positionGovernor;
	public final ArrayList<QuoteSlice> listOfQuoteSlice = new ArrayList<QuoteSlice>();
	public final ArrayList<QuoteSlice> listOfQuoteSlicePersistForDay = new ArrayList<QuoteSlice>();
	public final ArrayList<StrategyResponse> listOfStrategyResponse = new ArrayList<StrategyResponse>();
	public ArrayList<SignalMetricType> listOfSignalMetricTypeActive = new ArrayList<SignalMetricType>();
	public ArrayList<SignalMetricType> listOfSignalMetricTypeAnalyze = new ArrayList<SignalMetricType>();
	public QuoteSlice firstQuoteSlice;
	public Position position;
	public StrategyBase strategyBase;
	private Prefill prefill;
	public Date startingDate;
	public Date endDate;
	protected FundamentalData fundamentalData;
	public final BasicAccount basicAccount;
	public Double dayStartingBalance;
	public String algorithmSource;
	protected int receiveIndex;
	protected int processedIndex;
	protected Benchmark bench = new Benchmark(true);
	public SignalCache signalCache;
	public PremiseController premiseController = new PremiseController();
	public ContextController contextController = new ContextController();
	public boolean analyizeUsedOnly = false;
	//public Resolution resolution; //ugh
	
	public AlgorithmBase(Exchange exchange, Symbol symbol, AlgorithmMode algorithmMode, BasicAccount basicAccount){
		this.exchange = exchange;
		this.symbol = symbol;
		this.algorithmMode = algorithmMode;
		this.basicAccount = basicAccount;
		
		signalGroup = new SignalGroup(this);
		indicatorGroup = new IndicatorGroup(commonAnalysisData, signalGroup);
		positionGovernor = new PositionGovernor(algorithmMode == AlgorithmMode.mode_engagement ? PositionManager.getGlobalInstance() : new PositionManager());
		
		//Hack for SignalOfEncog
		if (exchange != null && symbol != null){
			signalGroup.signalOfEncog.setNetworkName(exchange.name + "-" + symbol.name);
		}
	}
	

	@Override
	public abstract void receiveQuoteSlice(QuoteSlice quoteSlice);

	@Override
	public abstract void endOfFeed(Symbol symbol);
	
	public void initialize(){
		if (algorithmMode.displayChart) {
			algorithmChart = new AlgorithmChart(symbol.name + " - " + new SimpleDateFormat("EEE MMM dd yyyy").format(startingDate), this);
		}
		
		if (algorithmMode.populateTable){
			tableForAlgorithm = new TableForAlgorithm(this);
		}
		
		if (algorithmMode == AlgorithmMode.mode_backtest_with_adjustment){
			//Check Strategy actually contains adjustment values... 
		}
		
		receiveIndex = 0;
		processedIndex = 0;
		
		signalGroup.setIndicatorGroup(indicatorGroup);
		indicatorGroup.setAnalyze(listOfSignalMetricTypeAnalyze);
		indicatorGroup.setActive(listOfSignalMetricTypeAnalyze);
		periodLength = Math.max(signalGroup.getMaxPeriodLength(), indicatorGroup.getMinPeriodLength(true));
		
		//Co.println("--> SG / IG: " + signalGroup.getMaxPeriodLength() + " / " + indicatorGroup.getMinPeriodLength(true));
		
		dayStartingBalance = basicAccount.getBalance();
		
//		if (dayStartingBalance - AccountProvider.defaultBalance < -3000){
//			throw new IllegalStateException(String.format("Large default balance gap. Difference: %s", (dayStartingBalance - AccountProvider.defaultBalance)));
//		}
		
		listOfQuoteSlice.clear();
		listOfQuoteSlicePersistForDay.clear();
		listOfStrategyResponse.clear();
		commonAnalysisData.reset();
		signalGroup.reset();
		algorithmState.reset();
		
		if (signalCache != null){
			signalCache.restoreFromDisk();
		}
		
		if (algorithmListener != null){algorithmListener.initialize(startingDate, endDate);}
	}
	
	public StrategyResponse requestExitExternally(){
		QuoteSlice quoteSlice = listOfQuoteSlice.get(listOfQuoteSlice.size()-1);
		quoteSlice.dateTime = DateTools.getChangedBySubtracting(quoteSlice.dateTime, -1);
		StrategyResponse strategyResponse = strategyBase.requestExit(position, quoteSlice, new PositionOptions(this));
		handleStrategyResponse(strategyResponse);
		populateAlgorithmDetails(quoteSlice, strategyResponse);
		
		return strategyResponse;
	}
	
	public void baseInformStrategy(QuoteSlice quoteSlice){
		StrategyResponse strategyResponse = strategyBase.informStrategy(indicatorGroup, signalGroup, listOfQuoteSlice, listOfStrategyResponse, position, new PositionOptions(this));
		handleStrategyResponse(strategyResponse);
		populateAlgorithmDetails(quoteSlice, strategyResponse);
	}
	
	public void populateAlgorithmDetails(QuoteSlice quoteSlice, StrategyResponse strategyResponse){
		if (algorithmMode.displayChart) {
			algorithmChart.addChartPointData(firstQuoteSlice, quoteSlice, strategyResponse, position);
		}
		
		if (algorithmMode.displayTable || algorithmMode.populateTable) {
			tableForAlgorithm.addTableRow(listOfQuoteSlice, strategyBase.signaler, signalGroup, strategyResponse, basicAccount);
		}
	}
	
	protected void setAnalyzeAndActive(ArrayList<SignalMetricType> listOfSignalMetricTypeAnalyze, ArrayList<SignalMetricType> listOfSignalMetricTypeActive) {
		this.listOfSignalMetricTypeAnalyze = listOfSignalMetricTypeAnalyze;
		this.listOfSignalMetricTypeActive = listOfSignalMetricTypeActive;
	}
	
	public void setAlgorithmListener(AlgorithmListener algorithmListener){
		this.algorithmListener = algorithmListener;
	}
	
	public ReceiverOfQuoteSlice getReceiver(){
		return this;
	}
	
	public void handlePositionChange(boolean isReentry, Position position){
		if (isReentry == false){
			algorithmState.transactions++;
		}
	}
	
	public void handleStrategyResponse(StrategyResponse strategyResponse) {
		if (strategyResponse.strategyAction == StrategyAction.algorithm_disable){
			if (algorithmState.isDisabled == false){
				disable(strategyResponse.strategyActionCause.name());
				listOfStrategyResponse.add(strategyResponse);
				
				if (algorithmListener != null){
					algorithmListener.receiveChangedStrategyResponse(strategyResponse);
				}
			}
		}else if (strategyResponse.strategyAction == StrategyAction.algorithm_changed){
			PositionGovernorResponse positionGovernorResponse = strategyResponse.positionGovernorResponse;
			if (positionGovernorResponse.status == PositionGovernorResponseStatus.changed_long_entry
				|| positionGovernorResponse.status == PositionGovernorResponseStatus.changed_long_exit
				|| positionGovernorResponse.status == PositionGovernorResponseStatus.changed_short_entry
				|| positionGovernorResponse.status == PositionGovernorResponseStatus.changed_short_exit){
					handlePositionChange(false, strategyResponse.positionGovernorResponse.position);
			}else if (positionGovernorResponse.status == PositionGovernorResponseStatus.changed_long_reentry || positionGovernorResponse.status == PositionGovernorResponseStatus.changed_short_reentry){
				handlePositionChange(true, strategyResponse.positionGovernorResponse.position);
			}
			
			listOfStrategyResponse.add(strategyResponse);
			
			if (algorithmListener != null){
				algorithmListener.receiveChangedStrategyResponse(strategyResponse);
			}
		}else if (strategyResponse.strategyAction == StrategyAction.algorithm_proceed){
			algorithmListener.receiveStrategyResponse(strategyResponse);
		}else if (strategyResponse.strategyAction== StrategyAction.algorithm_pass){
			listOfStrategyResponse.add(strategyResponse);
		} else if (strategyResponse.strategyAction == StrategyAction.no_change || strategyResponse.strategyAction == StrategyAction.none){
			//pass 
		} else {
			throw new IllegalArgumentException("Can't handle: " + strategyResponse.strategyAction);
		}
	}
	
	public void disable(String reason){
		algorithmState.isDisabled = true;
		algorithmState.disabledReason = reason;
	}
	
	public void receivedQuoteSlice(QuoteSlice quoteSlice){
//		if (algorithmMode.displayMessages) {
//			Co.println("Received quote: " + quoteSlice.symbol + ", " + DateTools.getPretty(quoteSlice.dateTime) + ", " + "O,H,L,C,V: " + +MathTools.round(quoteSlice.priceOpen) + ", " + MathTools.round(quoteSlice.priceHigh) + ", " + MathTools.round(quoteSlice.priceLow) + ", " + MathTools.round(quoteSlice.priceClose) + ", " + quoteSlice.sizeVolume);
//		}
		
		if (firstQuoteSlice == null){
			firstQuoteSlice = quoteSlice;
		}
		
		listOfQuoteSlice.add(quoteSlice);
		listOfQuoteSlicePersistForDay.add(quoteSlice);
		
		position = positionGovernor.getPositionManager().getPosition(quoteSlice.symbol);
		positionGovernor.getPositionManager().updatePositionPrice(quoteSlice, position);
	}
	
	public void finishedReceiveQuoteSlice(){
		boolean processed = listOfQuoteSlice.size() >= periodLength;
		
		if (algorithmListener != null){algorithmListener.receiveTick(ListTools.getLast(listOfQuoteSlice), receiveIndex, processedIndex, processed);}
		
		if (processed) {
			listOfQuoteSlice.remove(0);
			processedIndex++;
		}
		
		receiveIndex++;
	}
	
	public QuoteSlice getCurrentQuoteSlice(){
		return listOfQuoteSlice.size() == 0 ? null : listOfQuoteSlice.get(listOfQuoteSlice.size()-1);
	}
	
	public QuoteSlice getFirstQuoteSlice(){
		return firstQuoteSlice;
	}
	
	protected void prefill(){
		prefill = new Prefill(symbol, exchange, algorithmMode == AlgorithmMode.mode_engagement ? PrefillMethod.method_broker : PrefillMethod.method_database);					
		prefill.prefillAlgorithm(this, strategyBase.strategyOptions);
	}
	
	public void setFundamentalData(FundamentalData fundamentalData){
		this.fundamentalData = fundamentalData;
	}
	
	public int getPeriodLength(){
		return periodLength;
	}
	
	protected void setStrategy(StrategyBase strategyBase){
		this.strategyBase = strategyBase;
	}

	@Override
	public void positionStatusChanged(Position position) {
		if (position.positionType == PositionType.position_cancelled){
			Co.println("--> Position was cancelled... Disabling: " + position.symbol.name);
			disable(position.positionType.name());
		}
	}
	
	public double getYieldCurrent(){
		double yield = 0;
		
		for (Position positionIn : MiscTools.getUniquePositions(listOfStrategyResponse)){
			yield += positionIn.getCurrentPercentGainLoss(true);	
		}
		
		return yield;
	}
}
