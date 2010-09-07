package com.nimbusly.isometrix.test

import org.scalatest._
import org.scalatest.matchers.ShouldMatchers
import scala.collection.mutable._
import com.nimbusly.isometrix._


class ScreenSpec  extends WordSpec with ShouldMatchers {
	"screen" when {
		val screen = new Screen(100, 100)
		val tileCalls = new HashMap[Point, Int]
		val mockTiler = new Tiler() {
			def drawTile(origin : Point, width : Int, height : Int, tileIndex : Int) {
				tileCalls.put(new Point(origin), tileIndex)
			}
		};
		screen.tiler = mockTiler
		val game = new Game()
		screen.game = game
		screen.tileSize = 60
		
//		"screenOffset for view at origin" should {
//			screen.viewAt = new Point(0, 0)
//			"return origin" in {
//				screen.screenOffset should be (new Point(0,0))
//			}
//		}
//		"toScreen for viewed point" should {
//			screen.viewAt = new Point(60, -30)
//			"return offset from origin of viewed point" in {
//				screen.toScreen(new Point(0,0)) should be (new Point(50,50))
//			}
//		}
		"toScreen for point in game coordinates" should {
			screen.viewAt = new Point(8, 10)
			"return corresponding point in screen coordinates" in {
				screen.toScreen(new Point(2,6)) should be (new Point(32,52))
			}
		}
		"toGame for point in screen coordinates" should {
			screen.viewAt = new Point(8, 10)
			"return corresponding point in game coordinates" in {
				screen.toGame(new Point(32,52)) should be (new Point(3,7)) 
			}
			
		}
	}
	
}
/*
	

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
*/
