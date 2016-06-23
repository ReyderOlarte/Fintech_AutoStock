package com.autoStock.chart;

import java.awt.Color;
import java.awt.Paint;

import org.jfree.chart.renderer.xy.XYAreaRenderer2;
import org.jfree.data.xy.XYDataset;

/**
 * @author Kevin Kowalewski
 * 
 */
public class PostivieNegativeXYBarRenderer extends XYAreaRenderer2 {
	private int rendererIndex;
	
	public PostivieNegativeXYBarRenderer(int rendererIndex){
		super();
		this.rendererIndex = rendererIndex;
//		setShadowVisible(false);
//		setBarPainter(new StandardXYBarPainter());
	}
	
	@Override
	public Paint getItemPaint(int x_row, int x_col) {
		XYDataset xyDataset = getPlot().getDataset(rendererIndex);
		double l_value = xyDataset.getYValue(x_row, x_col);

		if (l_value < 0) {
			return Color.decode("#FFC7C7");
		} else {
			return Color.decode("#A8FFB7");
		}
	}
}
