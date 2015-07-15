package com.myim.Game.util;

import java.text.DecimalFormat;

public class HandleUtils {

	private static DecimalFormat formatter;

	public static DecimalFormat getDecimalFormatter() {
		if (formatter == null) {
			formatter = new DecimalFormat("##.####");
		}
		return formatter;
	}
}
