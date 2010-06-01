package com.nimbusly.isometrix.test;

import java.util.HashMap;
import java.util.Map;

import com.nimbusly.isometrix.*;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;


public class TestScreen extends TestCase {

	private Screen screen;
	private Screen.Tiler mockTiler;
	private Map<Point, Integer> tileCalls;
	private Game game;
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		screen = new Screen(100, 100);
		tileCalls = new HashMap<Point, Integer>();
		mockTiler = new Screen.Tiler() {
			public void drawTile(Point origin, int width, int height, int tileIndex) {
				assertEquals(width, screen.getTileSize() * 2);
				assertEquals(height, screen.getTileSize());
				tileCalls.put(new Point(origin), tileIndex);
			}
		};
		screen.setTiler(mockTiler);
		game = new Game();
		screen.setGame(game);
		screen.setTileSize(60);
	}

	public void testScreenOffsetShouldReturnZeroForViewAtZero() {
		screen.viewAt(new Point(0, 0));
		assertEquals(new Point(0,0), screen.screenOffset());
	}
	
	public void testScreenOffsetShouldReturnAmoutShiftedFromZero() {
		screen.viewAt(new Point(32, 0));
		assertEquals(new Point(60,-30), screen.screenOffset());
	}
	
	public void testToScreenShouldReturnCenterOfScreenForViewedPoint() {
		screen.viewAt(new Point(0, 0));
		assertEquals(new Point(50,50), screen.toScreen(new Point(0,0)));
	}
	
	public void testToScreenShouldReturnCorrectScreenPointForGamePoint() {
		screen.viewAt(new Point(0, 0));
		assertEquals(new Point(83,51), screen.toScreen(new Point(8,10)));
		screen.viewAt(new Point(8, 10));
		assertEquals(new Point(32,52), screen.toScreen(new Point(2,6)));
	}
	
	public void testToGameShouldReturnCorrectGamePointForScreenPoint() {
		screen.viewAt(new Point(8, 10));
		assertEquals(new Point(3,7), screen.toGame(new Point(32,52)));
	}

	public void testDrawTilesShouldDrawTileAtOrigin() {
		screen.viewAt(new Point(0,0));
		screen.drawTiles(0, 0, 100, 100);
		assertTrue(tileCalls.size() > 0);
		int size = screen.getTileSize();
		assertEquals(game.tileAt(new Point(0, 0)), (int) tileCalls.get(new Point(50, 50-size/2)));
		assertEquals(game.tileAt(new Point(-size*32/60, 0)), (int) tileCalls.get(new Point(50-size, 50)));
	}
	
	public static Test suite() {
		return new TestSuite(TestScreen.class);
	}
}
