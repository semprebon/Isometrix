package com.nimbusly.isometrix.test

import org.scalatest._
import org.scalatest.matchers.ShouldMatchers
import scala.collection.mutable._
import com.nimbusly.isometrix._

class GameSpec  extends WordSpec with ShouldMatchers {
	
	"game with desintaion set" when {
		val game = new Game();
		game.updateState(1000); // halfway
		game.setPosition(new Point(0,0));
		game.setDestination(new Point(6,8)); // distance = 10 pixels
		game.setSpeed(game.getTileSize()); // ms/tile 

		"updated with enough tics to get halfway to destination" should {
			game.updateState(1005);
			"be at destination" in {
				game.getPosition() should be new Point(3,4)
			}
		}
		"updated with enough tics to get to destination" should {
			game.updateState(2000);
			"be at destination" in {
				game.getPosition() should be new Point(6,8)
			}
		}
		"updated with enough tics to get past destination" should {
			game.updateState(3000);
			"be at destination" in {
				game.getPosition() should be new Point(6,8)
			}
		}
	}
	
}
