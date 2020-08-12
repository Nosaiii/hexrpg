package me.cheesyfreezy.hexrpg.tools;

import java.util.Arrays;

import org.apache.commons.lang.StringUtils;

public class PrimitiveTypeTools {
	public static boolean isInt(String str) {
		try {
			Integer.parseInt(str);
		} catch(NumberFormatException e) {
			return false;
		}
		return true;
	}
	
	public static boolean isDouble(String str) {
		try {
			Double.parseDouble(str);
		} catch(NumberFormatException e) {
			return false;
		}
		return true;
	}
	
	public static int[] parseIntRange(String str) {
		int[] numbers = new int[2];
		double[] dNumbers = parseDoubleRange(str);
		
		for(int i = 0; i < numbers.length; i++) {
			numbers[i] = (int) dNumbers[i];
		}
		
		return numbers;
	}
	
	public static double[] parseDoubleRange(String str) {
		double[] numbers = new double[2];
		
		String[] strSplitted = str.split("-");
		if(strSplitted.length != numbers.length) {
			return numbers;
		}
		
		if(isInt(strSplitted[0]) && isInt(strSplitted[1])) {
			for(int i=0;i<numbers.length;i++) {
				numbers[i] = Double.parseDouble(strSplitted[i]);
			}
		}
		
		Arrays.sort(numbers);
		
		return numbers;
	}
	
	public static double round(double d, int decimals) {
		double multiplier = Double.parseDouble("1" + StringUtils.repeat("0", decimals));
		return Math.round(d * multiplier) / multiplier;
	}
}