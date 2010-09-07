package com.nimbusly.isometrix

import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnKeyListener;

class GameController(val game : Game) extends OnKeyListener with View.OnTouchListener {
	private var screen : Screen = null
	
	private val TAG = "GameController"
		
	def onKey(view : View, keyCode : Int, keyEvent : KeyEvent) : Boolean = {
		Log.i(TAG, "onKey: " + keyCode)
		keyCode match {
			case KeyEvent.KEYCODE_DPAD_LEFT => 	game.move(0, +1)
			case KeyEvent.KEYCODE_DPAD_RIGHT =>	game.move(0, -1)
			case KeyEvent.KEYCODE_DPAD_UP => 	game.move(+1, 0)
			case KeyEvent.KEYCODE_DPAD_DOWN =>	game.move(-1, 0)
			case _ => return false
		}
		return true
	}
	
	def onTouch(view : View, event : MotionEvent) : Boolean = {
		Log.i(TAG, "onTouch called with " + event.getAction());
		if (event.getAction() == MotionEvent.ACTION_DOWN) {
			val x = event.getX().asInstanceOf[Int]
			val y = event.getY().asInstanceOf[Int]
			val p = screen.toGame(new Point(x, y))
			Log.i(TAG, "Going to " + p)
			game.setDestination(p)
		}
		false
	}
}