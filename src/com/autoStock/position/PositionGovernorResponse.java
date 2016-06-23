/**
 * 
 */
package com.autoStock.position;

import java.util.Date;

import com.autoStock.signal.SignalPoint;
import com.autoStock.trading.types.Position;

/**
 * @author Kevin Kowalewski
 *
 */
public class PositionGovernorResponse {
	public Position position;
	public PositionValue positionValue;
	public PositionGovernorResponseStatus status = PositionGovernorResponseStatus.none;
	public PositionGovernorResponseReason reason = PositionGovernorResponseReason.none;
	public SignalPoint signalPoint = new SignalPoint();
	public Date dateOccurred;
	
	public PositionGovernorResponse getFailedResponse(PositionGovernorResponseReason reason){
		this.reason = reason;
		status = PositionGovernorResponseStatus.failed;
		return this;
	}
}
