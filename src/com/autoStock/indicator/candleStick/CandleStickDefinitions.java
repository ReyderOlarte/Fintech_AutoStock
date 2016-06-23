package com.autoStock.indicator.candleStick;

/**
 * @author Kevin Kowalewski
 *
 */
public class CandleStickDefinitions {
	public static enum CandleStickTrend {
		trend_up,
		trend_down,
		none,
	}
	
	public static enum CandleStickIdentity {
		hanging_man(CandleStickTrend.trend_down),
		;
		
		public final CandleStickTrend candleStickPatternTrend;
		
		private CandleStickIdentity(CandleStickTrend candleStickPatternTrend){
			this.candleStickPatternTrend = candleStickPatternTrend;
		}
	}	
}
