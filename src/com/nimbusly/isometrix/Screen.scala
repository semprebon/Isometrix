package com.nimbusly.isometrix

trait Tiler {
	def drawTile(origin : Point, width : Int, height : Int, tileIndex : Int)
}
	
class Screen(var width : Int, var height : Int) {

	var viewAt : Point = new Point(0,0)
	var tileSize : Int = 0
	var tiler : Tiler = null
	var game : Game = null
	
	def resize(newWidth : Int, newHeight : Int) : Unit = {
		width = newWidth
		height = newHeight
	}
	
	/**
	 * Compute screen coordinates for point in game coordinates
	 * 
	 * @param point
	 * @return point converted to screen coordinates
	 */
	def toScreen(point : Point) : Point = {
		val x = (point.x - viewAt.x) * tileSize / game.tileSize
		val y = (point.y - viewAt.y) * tileSize / game.tileSize
		point.set(width/2 + (x + y), height/2 + (y - x)/2)
		return point;
	}
	
	def toGame(point : Point) : Point = {
		val x = (point.x - width/2) * game.tileSize / tileSize
		val y = (point.y - height/2) * game.tileSize / tileSize
		point.set(viewAt.x + x/2 - y, viewAt.y + x/2 + y)
		point
	}
	
	def screenOffset() : Point = {
		val x = viewAt.x * tileSize / game.tileSize
		val y = viewAt.y * tileSize / game.tileSize
		new Point((x + y), (y - x)/2)
	}

	/**
	 * Draw the tiles for a given section of screen image
	 */
	def drawTiles(x0 : Int, y0 : Int, x1 : Int, y1 : Int) {
		val gameTileSize = game.tileSize
		val min_y = IntegerMath.roundDown(toGame(new Point(x0, y0)).y, gameTileSize)
		val min_x = IntegerMath.roundDown(toGame(new Point(x0, y1)).x, gameTileSize)
		val max_y = IntegerMath.roundUp(toGame(new Point(x1, y1)).y, gameTileSize)
		val max_x = IntegerMath.roundUp(toGame(new Point(x1, y0)).x, gameTileSize)
		var origin = new Point()
		val p = new Point()
		for (x <- min_x until(max_x, gameTileSize)) {
			for (y <- min_y until(max_y, gameTileSize)) {
				p.set(x, y)
				origin = toScreen(origin.set(x, y)).offset(0, -tileSize/2)
				tiler.drawTile(origin, tileSize*2, tileSize, game.tileAt(p));
			}
		}
	}
}