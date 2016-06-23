package com.autoStock.signal.extras;

public class EncogFrameSupport {
	public interface EncogFrameSource {
		public EncogFrame asEncogFrame();
	}
	
	public interface EncogSubframeSource {
		public EncogSubframe asEncogSubframe();
	}
}
