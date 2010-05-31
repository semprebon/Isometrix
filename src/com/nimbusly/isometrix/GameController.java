package com.nimbusly.isometrix;

import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnKeyListener;

public class GameController implements OnKeyListener,  View.OnTouchListener {

	private Game game;
	private static final String TAG = "GameController";
	private Screen screen;
	
	public GameController(Game game) {
		this.game = game;
	}
	
	public boolean onKey(View view, int keyCode, KeyEvent keyEvent) {
		Log.i(TAG, "onKey: " + keyCode);
		switch (keyCode) {
		case KeyEvent.KEYCODE_DPAD_LEFT: 	game.move(0, +1); break;
		case KeyEvent.KEYCODE_DPAD_RIGHT: 	game.move(0, -1); break;
		case KeyEvent.KEYCODE_DPAD_UP: 		game.move(+1, 0); break;
		case KeyEvent.KEYCODE_DPAD_DOWN:	game.move(-1, 0); break;
		default: return false;
		}
		return true;
	}
	
	public boolean onTouch(View view, MotionEvent event) {
		Log.i(TAG, "onTouch called with " + event.getAction());
		if (event.getAction() == MotionEvent.ACTION_DOWN) {
			int x = (int) event.getX();
			int y = (int) event.getY();
			Point p = screen.toGame(new Point(x, y));
			Log.i(TAG, "Going to " + p);
			game.setDestination(p);
		}
		return false;
	}

	public void setScreen(Screen screen) {
		this.screen = screen;
	}
}
