/**
 * 
 */
package com.autoStock.chart;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowEvent;
import java.awt.geom.Rectangle2D;
import java.io.Closeable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JPanel;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartMouseEvent;
import org.jfree.chart.ChartMouseListener;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.AxisLocation;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.entity.XYItemEntity;
import org.jfree.chart.labels.StandardXYToolTipGenerator;
import org.jfree.chart.plot.CombinedDomainXYPlot;
import org.jfree.chart.plot.DatasetRenderingOrder;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.ValueMarker;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.CandlestickRenderer;
import org.jfree.chart.renderer.xy.StandardXYItemRenderer;
import org.jfree.chart.renderer.xy.XYShapeRenderer;
import org.jfree.data.DomainOrder;
import org.jfree.data.general.DatasetChangeListener;
import org.jfree.data.general.DatasetGroup;
import org.jfree.data.time.Minute;
import org.jfree.data.time.RegularTimePeriod;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.time.TimeSeriesDataItem;
import org.jfree.data.xy.DefaultHighLowDataset;
import org.jfree.data.xy.DefaultXYDataset;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RectangleInsets;
import org.jfree.ui.RefineryUtilities;
import org.jfree.ui.TextAnchor;
import org.jfree.util.ShapeUtilities;
import org.jnativehook.GlobalScreen;
import org.jnativehook.keyboard.NativeKeyListener;

import com.autoStock.Co;
import com.autoStock.algorithm.AlgorithmBase;
import com.autoStock.algorithm.core.ActiveAlgorithmContainer;
import com.autoStock.cache.GenericPersister;
import com.autoStock.chart.ChartForAlgorithmTest.TimeSeriesType;
import com.autoStock.chart.ChartForAlgorithmTest.TimeSeriesTypePair;
import com.autoStock.input.GlobalInputHook;
import com.autoStock.internal.ApplicationStates;
import com.autoStock.signal.SignalDefinitions;
import com.autoStock.signal.SignalDefinitions.SignalGuageType;
import com.autoStock.signal.SignalDefinitions.SignalMetricType;
import com.autoStock.signal.SignalDefinitions.SignalPointType;
import com.autoStock.signal.SignalPoint;
import com.autoStock.strategy.StrategyResponse;
import com.autoStock.types.Exchange;
import com.autoStock.types.Symbol;
import com.google.gson.internal.Pair;

/**
 * @author Kevin Kowalewski
 * 
 */
public class CombinedLineChart {
	public int usedColor = -1;
	private ArrayList<Pair<Integer, Double>> listOfPair = new ArrayList<Pair<Integer, Double>>();
	private GenericPersister genericPersister = GenericPersister.getStaticInstance();
	private XYPlot subPlotForPriceCopy;

	public static class StoredSignalPoint {
		public int index;
		public Date date;
		public double price;
		public Exchange exchange;
		public Symbol symbol;

		public SignalPointType signalPointType;
		transient public StrategyResponse strategyResponse;
		
		public StoredSignalPoint(int index, double price, StrategyResponse strategyResponse, Date date, Symbol symbol, Exchange exchange) {
			this(index, price, strategyResponse.positionGovernorResponse.signalPoint.signalPointType, date, symbol, exchange);
			this.strategyResponse = strategyResponse;
		}

		public StoredSignalPoint(int index, double price, SignalPointType signalPoint, Date date, Symbol symbol, Exchange exchange) {
			this.index = index;
			this.price = price;
			this.signalPointType = signalPoint;
			this.date = date;
			this.exchange = exchange;
			this.symbol = symbol;
		}
	}

	public class LineChartDisplay extends ApplicationFrame implements ActionListener, KeyListener {
		public String title;
		public TimeSeriesTypePair[] arrayOfTimeSeriesPair;
		public DefaultHighLowDataset defaultHighLowDataset;
		private AlgorithmBase algorithmBase;

		public LineChartDisplay(String title, DefaultHighLowDataset defaultHighLowDataset, AlgorithmBase algorithmBase, TimeSeriesTypePair... timeSeriesPairs) {
			super("autoStock - Chart - " + title);

			this.title = title;
			this.defaultHighLowDataset = defaultHighLowDataset;
			this.algorithmBase = algorithmBase;
			arrayOfTimeSeriesPair = timeSeriesPairs;

			final ChartPanel chartPanel = (ChartPanel) createPanel();
			chartPanel.setPreferredSize(new Dimension(1600, 1000));
			chartPanel.setHorizontalAxisTrace(true);
			addKeyListener(this);

			setContentPane(chartPanel);
			setVisible(true);
			
			toFront();
			pack();

			RefineryUtilities.positionFrameOnScreen(this, 0, 0);
		}
		
		JFreeChart chartOut;

		public JPanel createPanel() {
			JFreeChart chart = createChart();
			chart.setAntiAlias(true);
			chart.setTextAntiAlias(true);
			ChartPanel panel = new ChartPanel(chart, false);
			panel.setFillZoomRectangle(true);
			panel.setMouseWheelEnabled(true);
			
			chartOut = chart;

			panel.addChartMouseListener(new ChartMouseListener() {
				@Override
				public void chartMouseMoved(ChartMouseEvent arg0) {

				}

				@Override
				public void chartMouseClicked(ChartMouseEvent mouseEvent) {
					Co.println("--> Chart mouse clicked");

					if (mouseEvent.getEntity() != null && mouseEvent.getEntity() instanceof XYItemEntity) {
						XYDataset xyDataSet = ((XYItemEntity) mouseEvent.getEntity()).getDataset();

						int index = ((XYItemEntity) mouseEvent.getEntity()).getItem();
						double value = xyDataSet.getYValue(((XYItemEntity) mouseEvent.getEntity()).getSeriesIndex(), ((XYItemEntity) mouseEvent.getEntity()).getItem());

						Co.println("--> Added entity: " + index + ", " + value);

						listOfPair.add(new Pair<Integer, Double>(index, value));

						Co.println("\n\n");

						for (Pair<Integer, Double> pair : listOfPair) {
							Co.println("--> Index, value: " + pair.first + ", " + pair.second);
						}

//						clickPoint = new ClickPoint(index, SignalPointType.none);
					} else {
						Co.println("--> Try again!");
					}
				}
			});

			return panel;
		}

		private JFreeChart createChart() {
			CombinedDomainXYPlot plot = new CombinedDomainXYPlot(new DateAxis("Domain"));
			plot.setGap(10);
			resetColor();

			if (getPairForType(TimeSeriesType.type_signals) != null) {
				XYPlot subPlotForSignals = new XYPlot(getPairForType(TimeSeriesType.type_signals).timeSeriesCollection, null, new NumberAxis(getPairForType(TimeSeriesType.type_signals).timeSeriesType.displayName), new StandardXYItemRenderer());

				for (int i = 0; i < getPairForType(TimeSeriesType.type_signals).timeSeriesCollection.getSeriesCount(); i++) {
					subPlotForSignals.getRenderer().setSeriesPaint(i, getColor());
				}

				subPlotForSignals.setDatasetRenderingOrder(DatasetRenderingOrder.FORWARD);
				((NumberAxis) subPlotForSignals.getRangeAxis()).setAutoRangeIncludesZero(false);

				StandardXYItemRenderer renderer = (StandardXYItemRenderer) subPlotForSignals.getRenderer();
				renderer.setLegendLine(new Rectangle2D.Double(-4.0, -4.0, 4.0, 4.0));
				// renderer.setBaseLegendShape(new Rectangle2D.Double(-4.0, -4.0, 4.0, 4.0));
				// subPlotForSignals.setRangeCrosshairVisible(true);

				for (TimeSeries timeSeries : (List<TimeSeries>) getPairForType(TimeSeriesType.type_signals).timeSeriesCollection.getSeries()) {
					SignalMetricType signalMetricType = SignalDefinitions.SignalMetricType.valueOf(timeSeries.getDescription());

					if (algorithmBase.signalGroup.getSignalBaseForType(signalMetricType) == null || algorithmBase.signalGroup.getSignalBaseForType(signalMetricType).signalParameters.hasValidGuages() == false) {
						continue;
					}

					double thresholdForLongEntry = -100;
					double thresholdForLongExit = -100;
					double thresholdForShortEntry = -100;
					double thresholdForShortExit = -100;

					try {
						thresholdForLongEntry = algorithmBase.signalGroup.getSignalBaseForType(signalMetricType).signalParameters.getGuagesForType(SignalPointType.long_entry, SignalGuageType.guage_threshold_met, SignalGuageType.guage_threshold_left).get(0).threshold;
					} catch (IndexOutOfBoundsException e) {
					}
					try {
						thresholdForLongExit = algorithmBase.signalGroup.getSignalBaseForType(signalMetricType).signalParameters.getGuagesForType(SignalPointType.long_exit, SignalGuageType.guage_threshold_met, SignalGuageType.guage_threshold_left).get(0).threshold;
					} catch (IndexOutOfBoundsException e) {
					}
					try {
						thresholdForShortEntry = algorithmBase.signalGroup.getSignalBaseForType(signalMetricType).signalParameters.getGuagesForType(SignalPointType.short_entry, SignalGuageType.guage_threshold_met, SignalGuageType.guage_threshold_left).get(0).threshold;
					} catch (IndexOutOfBoundsException e) {
					}
					try {
						thresholdForShortExit = algorithmBase.signalGroup.getSignalBaseForType(signalMetricType).signalParameters.getGuagesForType(SignalPointType.short_exit, SignalGuageType.guage_threshold_met, SignalGuageType.guage_threshold_left).get(0).threshold;
					} catch (IndexOutOfBoundsException e) {
					}

					// ValueMarker markerForLongEntry = new ValueMarker(thresholdForLongEntry);
					// markerForLongEntry.setPaint(Color.decode("#33AA00"));
					// markerForLongEntry.setAlpha(1.0f);
					// markerForLongEntry.setLabel(signalMetricType.name().replaceAll("metric_", "") + " - long entry");
					// markerForLongEntry.setStroke(new BasicStroke(0.5f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0.0f, new float[] {4.0f, 2.0f}, 2.0f));
					// markerForLongEntry.setLabelOffset(new RectangleInsets(8,10,8,0));
					// markerForLongEntry.setLabelTextAnchor(TextAnchor.CENTER_LEFT);
					// subPlotForSignals.addRangeMarker(markerForLongEntry);
					//
					// ValueMarker markerForLongExit = new ValueMarker(thresholdForLongExit);
					// markerForLongExit.setPaint(Color.decode("#FF0000"));
					// markerForLongExit.setAlpha(1.0f);
					// markerForLongExit.setLabel(signalMetricType.name().replaceAll("metric_", "") + " - long exit");
					// markerForLongExit.setStroke(new BasicStroke(0.5f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0.0f, new float[] {4.0f, 2.0f}, 2.0f));
					// markerForLongExit.setLabelOffset(new RectangleInsets(8,10,8,0));
					// markerForLongExit.setLabelTextAnchor(TextAnchor.CENTER_LEFT);
					// subPlotForSignals.addRangeMarker(markerForLongExit);
					//
					// ValueMarker markerForShortEntry = new ValueMarker(thresholdForShortEntry);
					// markerForShortEntry.setPaint(Color.decode("#33AA00"));
					// markerForShortEntry.setAlpha(1.0f);
					// markerForShortEntry.setLabel(signalMetricType.name().replaceAll("metric_", "") + " - short entry");
					// markerForShortEntry.setStroke(new BasicStroke(0.5f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0.0f, new float[] {4.0f, 2.0f}, 2.0f));
					// markerForShortEntry.setLabelOffset(new RectangleInsets(8,10,8,0));
					// markerForShortEntry.setLabelTextAnchor(TextAnchor.CENTER_LEFT);
					// subPlotForSignals.addRangeMarker(markerForShortEntry);
					//
					// ValueMarker markerForShortExit = new ValueMarker(thresholdForShortExit);
					// markerForShortExit.setPaint(Color.decode("#FF0000"));
					// markerForShortExit.setAlpha(1.0f);
					// markerForShortExit.setLabel(signalMetricType.name().replaceAll("metric_", "") + " - short exit");
					// markerForShortExit.setStroke(new BasicStroke(0.5f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0.0f, new float[] {4.0f, 2.0f}, 2.0f));
					// markerForShortExit.setLabelOffset(new RectangleInsets(8,10,8,0));
					// markerForShortExit.setLabelTextAnchor(TextAnchor.CENTER_LEFT);
					// subPlotForSignals.addRangeMarker(markerForShortExit);
				}

				// if (getPairForType(TimeSeriesType.type_debug) != null){
				// subPlotForSignals.setDataset(1, getPairForType(TimeSeriesType.type_debug).timeSeriesCollection);
				// // subPlotForSignals.getRangeAxis(1).setAutoRange(true);
				// // ((NumberAxis)subPlotForSignals.getRangeAxis(1)).setAutoRangeIncludesZero(false);
				//
				// subPlotForSignals.setRenderer(1, new XYShapeRenderer());
				// subPlotForSignals.getRenderer(1).setSeriesShape(0, ShapeUtilities.createDiamond(2));
				// subPlotForSignals.getRenderer(1).setSeriesPaint(0, Color.BLACK);
				// }
				//
				subPlotForSignals.getRenderer().setBaseToolTipGenerator(StandardXYToolTipGenerator.getTimeSeriesInstance());

				// subPlotForSignals.setDomainCrosshairVisible(true);
				// subPlotForSignals.setDomainCrosshairLockedOnData(true);
				// subPlotForSignals.setRangeCrosshairVisible(true);
				// subPlotForSignals.setRangeCrosshairLockedOnData(true);

				plot.add(subPlotForSignals, 1);
			}

			if (getPairForType(TimeSeriesType.type_price) != null) {
				XYPlot subPlotForPrice = new XYPlot(getPairForType(TimeSeriesType.type_price).timeSeriesCollection, null, new NumberAxis("Action"), new StandardXYItemRenderer());
				subPlotForPriceCopy = subPlotForPrice;
				subPlotForPrice.getRenderer().setSeriesPaint(0, Color.DARK_GRAY);
				subPlotForPrice.getRangeAxis().setAutoRange(true);
				((NumberAxis) subPlotForPrice.getRangeAxis()).setAutoRangeIncludesZero(false);
				subPlotForPrice.setDatasetRenderingOrder(DatasetRenderingOrder.FORWARD);

				ValueMarker markerForZero = new ValueMarker(0);
				markerForZero.setPaint(Color.decode("#33AA00"));
				markerForZero.setAlpha(1.0f);
				markerForZero.setLabel("LABEL");
				markerForZero.setStroke(new BasicStroke(0.5f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0.0f, new float[] { 4.0f, 2.0f }, 2.0f));
				markerForZero.setLabelOffset(new RectangleInsets(8, 10, 8, 0));
				markerForZero.setLabelTextAnchor(TextAnchor.CENTER_LEFT);

				subPlotForPrice.setDataset(2, getPairForType(TimeSeriesType.type_long_entry_price).timeSeriesCollection);
				subPlotForPrice.setRenderer(2, new XYShapeRenderer());
				subPlotForPrice.getRenderer(2).setSeriesShape(0, ShapeUtilities.createUpTriangle(4));
				subPlotForPrice.getRenderer(2).setSeriesPaint(0, Color.decode("#33CC33"));

				subPlotForPrice.setDataset(3, getPairForType(TimeSeriesType.type_long_exit_price).timeSeriesCollection);
				subPlotForPrice.setRenderer(3, new XYShapeRenderer());
				subPlotForPrice.getRenderer(3).setSeriesShape(0, ShapeUtilities.createDownTriangle(4));
				subPlotForPrice.getRenderer(3).setSeriesPaint(0, Color.RED);

				subPlotForPrice.setDataset(4, getPairForType(TimeSeriesType.type_short_entry_price).timeSeriesCollection);
				subPlotForPrice.setRenderer(4, new XYShapeRenderer());
				subPlotForPrice.getRenderer(4).setSeriesShape(0, ShapeUtilities.createUpTriangle(4));
				subPlotForPrice.getRenderer(4).setSeriesPaint(0, Color.decode("#8000FF"));

				subPlotForPrice.setDataset(5, getPairForType(TimeSeriesType.type_short_exit_price).timeSeriesCollection);
				subPlotForPrice.setRenderer(5, new XYShapeRenderer());
				subPlotForPrice.getRenderer(5).setSeriesShape(0, ShapeUtilities.createDownTriangle(4));
				subPlotForPrice.getRenderer(5).setSeriesPaint(0, Color.decode("#FF8000"));

				subPlotForPrice.setDataset(6, getPairForType(TimeSeriesType.type_reentry_price).timeSeriesCollection);
				subPlotForPrice.setRenderer(6, new XYShapeRenderer());
				subPlotForPrice.getRenderer(6).setSeriesShape(0, ShapeUtilities.createUpTriangle(4));
				subPlotForPrice.getRenderer(6).setSeriesPaint(0, Color.decode("#A0A0A0"));

				// subPlotForPrice.mapDatasetToRangeAxis(7, 7);

				subPlotForPrice.getRenderer().setBaseToolTipGenerator(new StandardXYToolTipGenerator() {
					@Override
					public String generateToolTip(XYDataset dataset, int series, int item) {
						return "$" + String.valueOf(dataset.getY(0, item).doubleValue());
					}
				});

				plot.add(subPlotForPrice, 1);
			}

			XYPlot subPlotForYield = new XYPlot(getPairForType(TimeSeriesType.type_value).timeSeriesCollection, null, new NumberAxis(getPairForType(TimeSeriesType.type_value).timeSeriesType.displayName), new PostivieNegativeXYBarRenderer(0));
			subPlotForYield.getRenderer().setSeriesPaint(0, Color.DARK_GRAY);
			// subPlotForPrice.getRangeAxis().setAutoRange(true);
			((NumberAxis) subPlotForYield.getRangeAxis()).setAutoRangeIncludesZero(false);
			subPlotForYield.setRangeAxisLocation(0, AxisLocation.BOTTOM_OR_RIGHT);
			subPlotForYield.getRangeAxis().setRange(-0.5, 3.0);
			subPlotForYield.setDatasetRenderingOrder(DatasetRenderingOrder.FORWARD);

			// subPlotForPrice.setDataset(5, getPairForType(TimeSeriesType.type_short_exit_price).timeSeriesCollection);
			// subPlotForPrice.setRenderer(5, new XYShapeRenderer());
			// subPlotForPrice.getRenderer(5).setSeriesShape(0, ShapeUtilities.createDownTriangle(4));
			// subPlotForPrice.getRenderer(5).setSeriesPaint(0, Color.decode("#FF8000"));

			subPlotForYield.setDataset(1, getPairForType(TimeSeriesType.type_yield).timeSeriesCollection);
			subPlotForYield.setRangeAxis(1, new NumberAxis(TimeSeriesType.type_yield.displayName));
			subPlotForYield.setRangeAxisLocation(1, AxisLocation.BOTTOM_OR_LEFT);
			subPlotForYield.setRenderer(1, new StandardXYItemRenderer());
			subPlotForYield.getRenderer(1).setSeriesPaint(0, Color.decode("#D0D0D0"));
			subPlotForYield.getRangeAxis(1).setRange(-0.5, 3.0);
			// ((NumberAxis)subPlotForPrice.getRangeAxis(1)).setAutoRangeIncludesZero(false);
			// subPlotForPrice.getRangeAxis().setAutoRange(true);
			subPlotForYield.mapDatasetToRangeAxis(1, 1);

			ValueMarker valueMarker = new ValueMarker(0);
			valueMarker.setPaint(Color.decode("#CCCCCC"));
			valueMarker.setAlpha(1.0f);
			valueMarker.setLabel("");
			valueMarker.setStroke(new BasicStroke(0.5f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0.0f, new float[] { 4.0f, 2.0f }, 2.0f));
			valueMarker.setLabelOffset(new RectangleInsets(8, 10, 8, 0));
			valueMarker.setLabelTextAnchor(TextAnchor.CENTER_LEFT);
			subPlotForYield.addRangeMarker(valueMarker);

			plot.add(subPlotForYield);

			// subPlotForPrice.setDataset(2, getPairForType(TimeSeriesType.type_entry_price).timeSeriesCollection);
			// subPlotForPrice.setRenderer(2, new XYShapeRenderer());
			// // subPlotForPrice.getRenderer(2).setSeriesShape(0, ShapeUtilities.createUpTriangle(5));
			// subPlotForPrice.getRenderer(2).setSeriesPaint(0, Color.GREEN);
			//
			// subPlotForPrice.setDataset(3, getPairForType(TimeSeriesType.type_exit_price).timeSeriesCollection);
			// subPlotForPrice.setRenderer(3, new XYShapeRenderer());
			// // subPlotForPrice.getRenderer(3).setSeriesShape(0, ShapeUtilities.create(5));
			// subPlotForPrice.getRenderer(3).setSeriesPaint(0, Color.RED);

			try {
				XYPlot subPlotForCandleStick = ChartFactory.createCandlestickChart("Candlestick Demo", "Time", "Candle Stick", defaultHighLowDataset, false).getXYPlot();
				((NumberAxis) subPlotForCandleStick.getRangeAxis()).setAutoRangeIncludesZero(false);
				subPlotForCandleStick.setBackgroundPaint(Color.white);
				subPlotForCandleStick.setDomainGridlinePaint(Color.lightGray);
				subPlotForCandleStick.setRangeGridlinePaint(Color.lightGray);
				subPlotForCandleStick.setRangeAxis(0, new NumberAxis("Price"));
				subPlotForCandleStick.getRangeAxis(0).setAutoRange(true);
				((NumberAxis) subPlotForCandleStick.getRangeAxis(0)).setAutoRangeIncludesZero(false);
				subPlotForCandleStick.setRangeAxis(1, new NumberAxis("Volume"));

				((CandlestickRenderer) subPlotForCandleStick.getRenderer()).setUseOutlinePaint(true);
				plot.add(subPlotForCandleStick, 1);
			} catch (Exception e) {
			}

			// if (getPairForType(TimeSeriesType.type_debug) != null){
			// XYPlot subPlotForDebug = new XYPlot(getPairForType(TimeSeriesType.type_debug).timeSeriesCollection, null, new
			// NumberAxis(getPairForType(TimeSeriesType.type_debug).timeSeriesType.displayName), new StandardXYItemRenderer());
			// subPlotForDebug.getRangeAxis().setAutoRange(true);
			// ((NumberAxis)subPlotForDebug.getRangeAxis()).setAutoRangeIncludesZero(false);
			//
			// subPlotForDebug.setRenderer(new XYShapeRenderer());
			// subPlotForDebug.getRenderer().setSeriesShape(0, ShapeUtilities.createDiamond(4));
			// subPlotForDebug.getRenderer().setSeriesPaint(0, Color.BLACK);
			//
			// plot.add(subPlotForDebug, 1);
			// }

			plot.setOrientation(PlotOrientation.VERTICAL);
			plot.setBackgroundPaint(Color.lightGray);
			plot.setDomainGridlinePaint(Color.white);
			plot.setRangeGridlinePaint(Color.white);
			plot.setAxisOffset(new RectangleInsets(5.0, 5.0, 5.0, 5.0));

			DateAxis axis = (DateAxis) plot.getDomainAxis();
			axis.setDateFormatOverride(new SimpleDateFormat("HH:mm"));

			JFreeChart chart = new JFreeChart("autoStock - Analysis - " + title, JFreeChart.DEFAULT_TITLE_FONT, plot, true);

			chart.setBackgroundPaint(Color.white);
			
			 pairs.add(getPairForType(TimeSeriesType.type_long_entry_price));
			 pairs.add(getPairForType(TimeSeriesType.type_long_exit_price));
			 pairs.add(getPairForType(TimeSeriesType.type_short_entry_price));
			 pairs.add(getPairForType(TimeSeriesType.type_short_exit_price));
			 pairs.add(getPairForType(TimeSeriesType.type_reentry_price));
			 itemIndex = getNotNull(0, pairs.get(currentPairIndex).timeSeriesCollection);

			return chart;

		}

		public TimeSeriesTypePair getPairForType(TimeSeriesType timeSeriesType) {
			for (TimeSeriesTypePair pair : arrayOfTimeSeriesPair) {
				if (pair.timeSeriesType == timeSeriesType) {
					return pair;
				}
			}

			return null;
		}

		public Color getColor() {
			Color[] arrayOfColors = new Color[] { Color.BLACK, Color.BLUE, Color.GREEN, Color.RED, Color.PINK, Color.MAGENTA, Color.CYAN, Color.ORANGE, Color.DARK_GRAY, Color.LIGHT_GRAY };
			if (usedColor >= arrayOfColors.length - 1) {
				usedColor = -1;
			}
			usedColor++;
			return arrayOfColors[usedColor];
		}

		public void resetColor() {
			usedColor = -1;
		}

		@Override
		public void actionPerformed(ActionEvent actionEvent) {}

		ArrayList<TimeSeriesTypePair> pairs = new ArrayList<TimeSeriesTypePair>();
		
		int currentPairIndex = 0;
		int itemIndex = 0;
		int currentOffset = 0;
		
		private int getNotNull(int offset, TimeSeriesCollection tsc){
			int i = 0;
			
			for (Object object : tsc.getSeries(0).getItems()){
				if (((TimeSeriesDataItem)object).getValue() != null){
					if (i > offset){return i;}
				}
				i++;
			}
			return -1;
		}
		
		private boolean exists(int index){
			TimeSeriesTypePair pair = pairs.get(currentPairIndex);
			return pair.timeSeriesCollection.getSeries(0).getDataItem(index).getValue() != null;
		}
		
		private void delete(int index){
			TimeSeriesTypePair pair = pairs.get(currentPairIndex);
			pair.timeSeriesCollection.getSeries(0).update(index, null);
			subPlotForPriceCopy.setDataset(pair.timeSeriesType.seriesIndex, pairs.get(currentPairIndex).timeSeriesCollection);
		}
		
		private void moveToIndex(int fromIndex, int toIndex){
			TimeSeriesTypePair pair = pairs.get(currentPairIndex);
			double newPrice = getPairForType(TimeSeriesType.type_price).timeSeriesCollection.getSeries(0).getDataItem(toIndex).getValue().doubleValue();
			
			pair.timeSeriesCollection.getSeries(0).update(toIndex, newPrice);
			pair.timeSeriesCollection.getSeries(0).update(fromIndex, null);
		}
		
		private void addSignalPoint(TimeSeriesTypePair pair){
			double newPrice = getPairForType(TimeSeriesType.type_price).timeSeriesCollection.getSeries(0).getDataItem(0).getValue().doubleValue();
			RegularTimePeriod date = getPairForType(TimeSeriesType.type_price).timeSeriesCollection.getSeries(0).getDataItem(0).getPeriod();
			pair.timeSeriesCollection.getSeries(0).addOrUpdate(new TimeSeriesDataItem(date, newPrice));
		}

		@Override
		public void keyPressed(KeyEvent keyEvent) {
			//Co.println("--> " + keyEvent.getKeyCode());
			
			StoredSignalPoint clickPoint = null;
			
			if (keyEvent.getKeyCode() == 10){ // Enter
				int nextNotNull = getNotNull(itemIndex, pairs.get(currentPairIndex).timeSeriesCollection);
				
				if (nextNotNull != -1){
					itemIndex = nextNotNull;
				}else{
					currentPairIndex++;
					if (currentPairIndex == 5){currentPairIndex = 0;}
					itemIndex = 0;
					itemIndex = getNotNull(itemIndex, pairs.get(currentPairIndex).timeSeriesCollection);
				}
			}
			
			if (keyEvent.getKeyCode() == 39){ // Right
				if (exists(itemIndex + 1)){moveToIndex(itemIndex, itemIndex + 2); itemIndex += 2;
				}else{try {moveToIndex(itemIndex, ++itemIndex);}catch(ArrayIndexOutOfBoundsException e){}}
			}
			
			if (keyEvent.getKeyCode() == 37){ // Left
				if (itemIndex <= 0){itemIndex = 1;}
				if (exists(itemIndex - 1)){moveToIndex(itemIndex, itemIndex - 2); itemIndex -= 2;
				}else{try {moveToIndex(itemIndex, --itemIndex);}catch(ArrayIndexOutOfBoundsException e){}}
			}
			
			if (keyEvent.getKeyCode() == 127){ // Delete
				delete(itemIndex);
			}
			
			if (keyEvent.getKeyCode() == 27){ // Esc
				Co.print("--> Saving ChartSignalPoints... ");
				boolean changed = false;
				int saved = 0;

				if (genericPersister.getCount(StoredSignalPoint.class) > 0){
					for (Iterator<StoredSignalPoint> iterator = genericPersister.getList(StoredSignalPoint.class).iterator(); iterator.hasNext();){
						StoredSignalPoint csp = iterator.next();
						
						if (csp.date.getTime() >= algorithmBase.startingDate.getTime() && csp.date.getTime() <= algorithmBase.endDate.getTime()){
							iterator.remove();
						}
					}
				}
				
				if (changed){genericPersister.syncToDisk();}
				
				for (TimeSeriesTypePair pair : pairs){
					for (int i=0; i<pair.timeSeriesCollection.getSeries(0).getItemCount(); i++){
						TimeSeriesDataItem item = pair.timeSeriesCollection.getSeries(0).getDataItem(i);
						
						if (item.getValue() != null){
							SignalPointType spt = SignalPointType.none;
							if (pair.timeSeriesType == TimeSeriesType.type_long_entry_price){spt = SignalPointType.long_entry;}
							if (pair.timeSeriesType == TimeSeriesType.type_short_entry_price){spt = SignalPointType.short_entry;}
							if (pair.timeSeriesType == TimeSeriesType.type_reentry_price){spt = SignalPointType.reentry;}
							if (pair.timeSeriesType == TimeSeriesType.type_long_exit_price){spt = SignalPointType.long_exit;}
							if (pair.timeSeriesType == TimeSeriesType.type_short_exit_price){spt = SignalPointType.short_exit;}
						
							genericPersister.persistInto(new StoredSignalPoint(i, item.getValue().doubleValue(), spt, item.getPeriod().getStart(), algorithmBase.symbol, algorithmBase.exchange), false);
							saved++;
						}
					}
				}
				
				Co.println("" + saved + " saved");
				dispose();
			}
			
			if (keyEvent.getKeyCode() == 97){
				TimeSeriesTypePair pair = getPairForType(TimeSeriesType.type_long_entry_price);
				addSignalPoint(pair);
				itemIndex = 0;
				currentPairIndex = 0;
			}
			
			if (keyEvent.getKeyCode() == 98){
				TimeSeriesTypePair pair = getPairForType(TimeSeriesType.type_short_entry_price);
				addSignalPoint(pair);
				itemIndex = 0;
				currentPairIndex = 2;
			}
			
			if (keyEvent.getKeyCode() == 99){
				TimeSeriesTypePair pair = getPairForType(TimeSeriesType.type_reentry_price);
				addSignalPoint(pair);
				itemIndex = 0;
				currentPairIndex = 4;
			}
			
			if (keyEvent.getKeyCode() == 100){
				TimeSeriesTypePair pair = getPairForType(TimeSeriesType.type_long_exit_price);
				addSignalPoint(pair);
				itemIndex = 0;
				currentPairIndex = 1;
			}
			
			if (keyEvent.getKeyCode() == 101){
				TimeSeriesTypePair pair = getPairForType(TimeSeriesType.type_short_exit_price);
				addSignalPoint(pair);
				itemIndex = 0;
				currentPairIndex = 3;
			}
		}

		@Override
		public void keyReleased(KeyEvent e) {
		}

		@Override
		public void keyTyped(KeyEvent e) {
		}
		
		@Override
		public void windowClosing(WindowEvent event) {
			if (event.getWindow() == this){dispose();}
		}
	}
}
