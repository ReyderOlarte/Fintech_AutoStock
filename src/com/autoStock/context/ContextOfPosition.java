/**
 * 
 */
package com.autoStock.context;

import com.autoStock.Co;
import com.autoStock.position.PositionDefinitions.PositionType;
import com.autoStock.signal.extras.EncogFrame;
import com.autoStock.signal.extras.EncogSubframe;
import com.autoStock.signal.extras.EncogFrame.FrameType;
import com.autoStock.signal.extras.EncogFrameSupport.EncogFrameSource;
import com.autoStock.trading.types.Position;

/**
 * @author Kevin
 *
 */
public class ContextOfPosition extends ContextBase implements EncogFrameSource{
	private Position position;

	@Override
	public EncogFrame asEncogFrame() {
		EncogFrame encogFrame = new EncogFrame(getClass().getSimpleName(), FrameType.raw);
		EncogSubframe subframeForPositionValue = new EncogSubframe(getClass().getSimpleName(), new double[]{position != null ? position.getCurrentPercentGainLoss(true) : 0}, FrameType.raw, 3, -3);
		EncogSubframe subFrameForHavePositionLong = new EncogSubframe(getClass().getSimpleName(), new double[]{position != null && position.positionType == PositionType.position_long ? 1 : -1}, FrameType.raw, 1, -1);
		EncogSubframe subFrameForHavePositionShort = new EncogSubframe(getClass().getSimpleName(), new double[]{position != null && position.positionType == PositionType.position_short ? 1 : -1}, FrameType.raw, 1, -1);
		encogFrame.addSubframe(subframeForPositionValue);
		encogFrame.addSubframe(subFrameForHavePositionLong);
		encogFrame.addSubframe(subFrameForHavePositionShort);
		return encogFrame;
	}

	@Override
	public void run() {}

	public void setPosition(Position position) {
		this.position = position;
	}
}
