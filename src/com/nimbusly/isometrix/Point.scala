package com.nimbusly.isometrix

class Point(var x : Int = 0, var y : Int = 0) {

	def this(p : Point) = this(p.x, p.y)
	
	def set(x : Int, y : Int) : Point = {
		this.x = x
		this.y = y
		return this
	}
	
	def offset(dx : Int, dy : Int) : Point = {
		return set(x + dx, y + dy)
	}
	
	override def toString : String = "Point(" + x + "," + y + ")"
	
	override def equals(other : Any) : Boolean = {
		other match {
			case p:Point => this.x == p.x && this.y == p.y
			case _ => false
		}
	}
	
	override def hashCode : Int = x ^ y

	def distanceTo(other : Point) : Int = {
		val dx: Int = x - other.x
		val dy: Int = y - other.y
		math.round(math.sqrt(dx*dx + dy*dy).asInstanceOf[Float])
	}
	
}