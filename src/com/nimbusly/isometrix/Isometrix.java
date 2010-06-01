package com.nimbusly.isometrix;

import android.app.Activity;
import android.os.Bundle;
import android.view.SurfaceView;
import android.view.Window;

public class Isometrix extends Activity {
	
	public GameLoop gameLoop;
	private SurfaceView gameView;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        Game ground = new Game();
        setContentView(R.layout.game);
        gameView = (SurfaceView) findViewById(R.id.gameView);
        GameController gameController = new GameController(ground);
        gameView.setOnTouchListener(gameController);
        gameView.setFocusable(true);
        gameView.setFocusableInTouchMode(true);
        gameView.requestFocus();
        gameLoop = new GameLoop(this, ground, gameView.getHolder(), gameController);
    }
    
}