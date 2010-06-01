package com.nimbusly.isometrix;

public class IntegerMath {
	
	public static int round(int value, int divisor) {
		return (value + divisor/2) / divisor;
	}
	
	public static int roundDown(int value, int increment) {
		return floor(value, increment) * increment;
	}
	
	public static int roundUp(int value, int increment) {
		return ceiling(value, increment) * increment;
	}
	
	public static int floor(int value, int divisor) {
		if (value >= 0) {
			return value / divisor;
		} else {
			return -ceiling(-value, divisor);
		}
	}
	
	public static int ceiling(int value, int divisor) {
		if (value >= 0) {
			return (value + divisor - 1) / divisor;
		} else {
			return -floor(-value, divisor);
		}
	}

	public static int signum(int value) {
		return (value > 0) ? +1 : (value == 0) ? 0 : -1;
	}
}
