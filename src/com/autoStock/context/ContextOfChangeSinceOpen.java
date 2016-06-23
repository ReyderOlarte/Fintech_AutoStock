/**
 * 
 */
package com.autoStock.context;

import com.autoStock.signal.extras.EncogFrame;
import com.autoStock.signal.extras.EncogSubframe;
import com.autoStock.signal.extras.EncogFrame.FrameType;
import com.autoStock.signal.extras.EncogFrameSupport.EncogFrameSource;
import com.autoStock.trading.types.Position;
import com.autoStock.types.QuoteSlice;

/**
 * @author Kevin
 *
 */
public class ContextOfChangeSinceOpen extends ContextBase implements EncogFrameSource {
	private QuoteSlice firstQuoteSlice;
	private QuoteSlice currentQuoteSlice;

	@Override
	public EncogFrame asEncogFrame() {
		EncogFrame encogFrame = new EncogFrame(getClass().getSimpleName(), FrameType.percent_change);
		EncogSubframe subframeForPositionValue = new EncogSubframe(getClass().getSimpleName(), new double[]{(currentQuoteSlice.priceClose / firstQuoteSlice.priceClose) -1}, FrameType.percent_change, 1, -1);
		encogFrame.addSubframe(subframeForPositionValue);
		return encogFrame;
	}

	@Override
	public void run() {}

	public void setCurrentQuoteSlice(QuoteSlice firstQuoteSlice, QuoteSlice currentQuoteSlice) {
		this.currentQuoteSlice = currentQuoteSlice;
		if (this.firstQuoteSlice == null){this.firstQuoteSlice = firstQuoteSlice;}
	}

}
