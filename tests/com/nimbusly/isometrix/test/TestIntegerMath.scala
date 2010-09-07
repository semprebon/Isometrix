package com.nimbusly.isometrix.test

import org.scalatest._
import org.scalatest.matchers.ShouldMatchers
import scala.collection.mutable.Stack
import com.nimbusly.isometrix._

class IntegerMathSpec extends WordSpec with ShouldMatchers {
	"floor" when {
		"passed positive integer as first parameter" should {
			"divide and round down" in {
				IntegerMath.floor(14, 5) should be (2)
				IntegerMath.floor(15, 5) should be (3)
			}
		}
		"passed two zero as the first parameter" should {
			"return 0" in {
				IntegerMath.floor(0, 5) should be (0)
			}
		}
		"passed negative as first parameter" should {
			"divide and round down (toward -infinity)" in {
				IntegerMath.floor(-5, 5) should be (-1)
				IntegerMath.floor(-6, 5) should be (-2)
			}
		}
	}

	"ceiling" when {
		"passed positive integer as first parameter" should {
			"divide and up" in {
				IntegerMath.ceiling(15, 5) should be (3)
				IntegerMath.ceiling(16, 5) should be (4)
			}
		}
		"passed two zero as the first parameter" should {
			"return 0" in {
				IntegerMath.ceiling(0, 5) should be (0)
			}
		}
		"passed negative as first parameter" should {
			"divide and round up toward 0" in {
				IntegerMath.ceiling(-1, 5) should be (0)
				IntegerMath.ceiling(-5, 5) should be (-1)
				IntegerMath.ceiling(-6, 5) should be (-1)
			}
		}
	}

	"roundDown" when {
		"passed positive integer as first parameter" should {
			"divide and round down to nearest multiple of first paraqmeter" in {
				IntegerMath.roundDown(14, 5) should be (10)
				IntegerMath.roundDown(15, 5) should be (15)
			}
		}
		"passed negative as first parameter" should {
			"divide and round down (toward -infinity) to nearest multiple of first paraqmeter" in {
				IntegerMath.roundDown(-1, 5) should be (-5)
				IntegerMath.roundDown(-5, 5) should be (-5)
			}
		}
	}

	"roundUp" when {
		"passed positive integer as first parameter" should {
			"round up to nearest multiple of second parameter" in {
				IntegerMath.roundUp(14, 5) should be (15)
				IntegerMath.roundUp(15, 5) should be (15)
				IntegerMath.roundUp(16, 5) should be (20)
			}
		}
		"passed two zero as the first parameter" should {
			"return 0" in {
				IntegerMath.roundUp(0, 5) should be (0)
			}
		}
		"passed negative as first parameter" should {
			"round up toward 0 to the nearest multiple of second parameter" in {
				IntegerMath.roundUp(-1, 5) should be (0)
				IntegerMath.roundUp(-4, 5) should be (0)
				IntegerMath.roundUp(-5, 5) should be (-5)
			}
		}
	}
}