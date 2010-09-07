package com.nimbusly.isometrix

object IntegerMath {

	def round(value : Int, divisor : Int) : Int = (value + divisor/2) / divisor
	
	def roundDown(value : Int, increment : Int) : Int = floor(value, increment) * increment
	
	def roundUp(value : Int, increment : Int) : Int = ceiling(value, increment) * increment
	
	def floor(value : Int, divisor : Int) : Int = 
		if (value >= 0) value / divisor else -ceiling(-value, divisor)
	
	def ceiling(value : Int, divisor : Int) : Int = 
		if (value >= 0) (value + divisor - 1) / divisor else -floor(-value, divisor)

	def signum(value : Int) : Int = if (value > 0) +1 else if (value == 0) 0 else -1
}