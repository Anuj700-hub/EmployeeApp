package com.example.logcatviewer.standout.ui;

import java.util.Locale;

public class TouchInfo {
	/**
	 * The state of the window.
	 */
	public int firstX, firstY, lastX, lastY;
	public double dist, scale, firstWidth, firstHeight;
	public float ratio;

	/**
	 * Whether we're past the move threshold already.
	 */
	public boolean moving;

	@Override
	public String toString() {
		return String
				.format(Locale.US,
						"WindowTouchInfo { firstX=%d, firstY=%d,lastX=%d, lastY=%d, firstWidth=%d, firstHeight=%d }",
						firstX, firstY, lastX, lastY, firstWidth, firstHeight);
	}
}
