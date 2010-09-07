package com.nimbusly.isometrix

class Game {

	private val TAG = "Ground"

	private var width = 0
	private var height = 0
	private var position = new Point(width / 2, height / 2)
	private var origin : Point = null
	private var destination : Point = null 
	private var startTime = 0L
	private var walkState = 0
	private var facing = 0
	val tileSize = 32
	
	private val DIRECTION_INDEX = Array(Array(6, 7, 0), Array(5, 0, 1), Array(4, 3, 2))
	
	private var speed = 400L // ms per tile
	private var time = 0L
	
	def move(dx : Int, dy : Int) {
		//Log.d(TAG, "Moving " + dx + "," + dy);
		this.synchronized {
			facing = directionIndex(dx, dy)
			position.x = (position.x + dx) % width
			position.y = (position.y + dy) % width
		}
	}
	
	def updateState(time : Long) {
		this.time = time
		walkState = math.abs((time / 100) % 12).asInstanceOf[Int]
		// compute movement
		if (destination != null && !position.equals(destination)) {
			val distance = origin.distanceTo(destination)
			val step = (time - startTime) * tileSize / speed
			if (step < distance) {
				val dx = step * (destination.x - origin.x) / distance
				val dy = step * (destination.y - origin.y) / distance
				facing = directionIndex(dx.asInstanceOf[Int], dy.asInstanceOf[Int])
				position.set((origin.x + dx).asInstanceOf[Int], (origin.y + dy).asInstanceOf[Int])
			} else {
				position.set(destination.x, destination.y)
			}
		}
	}
	
	def tileAt(p : Point) : Int = {
		val col = p.x / tileSize
		val row = p.y / tileSize
		(col + row) % 2
	}
	
	def setDestination(point : Point) {
		destination = point
		origin = new Point(position.x, position.y)
		startTime = time
	}
	
	private def directionIndex(dx : Int, dy : Int) : Int = {
		return DIRECTION_INDEX(IntegerMath.signum(dx)+1)(IntegerMath.signum(dy)+1)
	}
}