package com.nimbusly.isometrix.test;

import com.nimbusly.isometrix.IntegerMath;

import junit.framework.TestCase;

public class TestIntegerMath extends TestCase {

	public void testFloor() {
		assertEquals(2, IntegerMath.floor(14, 5));
		assertEquals(3, IntegerMath.floor(15, 5));
		assertEquals(0, IntegerMath.floor(0, 5));
		assertEquals(-1, IntegerMath.floor(-1, 5));
		assertEquals(-1, IntegerMath.floor(-5, 5));
		assertEquals(-2, IntegerMath.floor(-6, 5));
	}
	
	public void testCeiling() {
		assertEquals(3, IntegerMath.ceiling(15, 5));
		assertEquals(4, IntegerMath.ceiling(16, 5));
		assertEquals(0, IntegerMath.ceiling(0, 5));
		assertEquals(0, IntegerMath.ceiling(-1, 5));
		assertEquals(-1, IntegerMath.ceiling(-5, 5));
		assertEquals(-1, IntegerMath.ceiling(-6, 5));
	}
	
	public void testRoundUp() {
		assertEquals(15, IntegerMath.roundUp(14, 5));
		assertEquals(15, IntegerMath.roundUp(15, 5));
		assertEquals(20, IntegerMath.roundUp(16, 5));
		assertEquals(0, IntegerMath.roundUp(0, 5));
		assertEquals(0, IntegerMath.roundUp(-4, 5));
		assertEquals(-5, IntegerMath.roundUp(-5, 5));
	}
	public void testRoundDown() {
		assertEquals(10, IntegerMath.roundDown(14, 5));
		assertEquals(-5, IntegerMath.roundDown(-1, 5));
	}
}
