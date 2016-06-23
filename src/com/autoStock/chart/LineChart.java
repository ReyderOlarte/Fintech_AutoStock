/**
 * 
 */
package com.autoStock.chart;

import java.awt.Color;
import java.text.SimpleDateFormat;

import javax.swing.JPanel;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.XYDataset;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RectangleInsets;
import org.jfree.ui.RefineryUtilities;

/**
 * @author Kevin Kowalewski
 * 
 */
public class LineChart {

	public class LineChartDisplay extends ApplicationFrame {

		public LineChartDisplay(TimeSeriesCollection timeSeriesCollection) {
			super("autoStock - Chart");

			ChartPanel chartPanel = (ChartPanel) createPanel(timeSeriesCollection);
			chartPanel.setPreferredSize(new java.awt.Dimension(500*2, 270*2));
			setContentPane(chartPanel);

			RefineryUtilities.centerFrameOnScreen(this);
			setVisible(true);
			pack();
		}

		public JPanel createPanel(XYDataset dataset) {
			JFreeChart chart = createChart(dataset);
			ChartPanel panel = new ChartPanel(chart);
			panel.setFillZoomRectangle(true);
			panel.setMouseWheelEnabled(true);
			return panel;
		}

		private JFreeChart createChart(XYDataset dataset) {

			JFreeChart chart = ChartFactory.createTimeSeriesChart("autoStock - Analysis",
					"Date", // x-axis label
					"Price Per Share", // y-axis label
					dataset, // data
					true, // create legend?
					true, // generate tooltips?
					false // generate URLs?
					);

			chart.setBackgroundPaint(Color.white);

			XYPlot plot = (XYPlot) chart.getPlot();
			plot.setBackgroundPaint(Color.lightGray);
			plot.setDomainGridlinePaint(Color.white);
			plot.setRangeGridlinePaint(Color.white);
			plot.setAxisOffset(new RectangleInsets(5.0, 5.0, 5.0, 5.0));

			DateAxis axis = (DateAxis) plot.getDomainAxis();
			axis.setDateFormatOverride(new SimpleDateFormat("HH:mm"));

			return chart;

		}
	}
}
