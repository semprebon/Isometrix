package com.nimbusly.isometrix.test;

import com.nimbusly.isometrix.Game;
import com.nimbusly.isometrix.Point;

import junit.framework.TestCase;

public class TestGame extends TestCase {

	private Game game;
	
	protected void setUp() throws Exception {
		super.setUp();
		game = new Game();
		game.updateState(1000); // halfway
	}
	
	public void testGameShouldMoveToDestination() {
		game.setPosition(new Point(0,0));
		game.setDestination(new Point(6,8)); // distance = 10 pixels
		game.setSpeed(1); // 1 pixels/ms 
		game.updateState(1005); // halfway
		assertEquals(new Point(3,4), game.getPosition());
		game.updateState(1010); // rest of way
		assertEquals(new Point(6,8), game.getPosition());
	}

}
