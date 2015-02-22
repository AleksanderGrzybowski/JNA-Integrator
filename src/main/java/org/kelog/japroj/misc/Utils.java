package org.kelog.japroj.misc;

import java.util.logging.Logger;

public class Utils {

	// from SO
	public static void disableLogging() {
		Logger l0 = Logger.getLogger("");
		l0.removeHandler(l0.getHandlers()[0]);
	}
}
