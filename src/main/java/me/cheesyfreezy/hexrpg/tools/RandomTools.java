package me.cheesyfreezy.hexrpg.tools;

import java.util.Random;

public class RandomTools {
	/**
	 * Generates a random percentage between 0 and 100
	 * @return A random integer value between 0 and 100
	 */
	public static double getRandomPercentage() {
		Random random = new Random();
		return random.nextDouble() * 100;
	}
	
	/**
	 * Generates a random value between the minimum (inclusive) and maximum (inclusive) range
	 * @param min The minimum value of the random roll range (inclusive)
	 * @param max The maximum value of the random roll range (inclusive)
	 * @return A random double value between the given range
	 */
	public static double getRandomRange(double min, double max) {
		Random random = new Random();
		return min + random.nextDouble() * (max - min);
	}
}