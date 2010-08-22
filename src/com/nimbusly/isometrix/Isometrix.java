package com.nimbusly.isometrix;

import android.app.Activity;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.view.SurfaceView;
import android.view.Window;

public class Isometrix extends Activity {
	
	private GLSurfaceView gameView;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
		gameView = new GLSurfaceView(this);
		gameView.setRenderer(new GameRenderer(this));
		setContentView(gameView);

//        Game ground = new Game();
//        setContentView(R.layout.game);
//        gameView = (GLSurfaceView) findViewById(R.id.gameView);
//        GameController gameController = new GameController(ground);
//        gameView.setOnTouchListener(gameController);
//        gameView.setFocusable(true);
//        gameView.setFocusableInTouchMode(true);
//        gameView.requestFocus();
//        gameLoop = new GameLoop(this, ground, gameView, gameController);
    }
    
	@Override
	protected void onResume() {
		super.onResume();
		gameView.onResume();
	}

	@Override
	protected void onPause() {
		super.onPause();
		gameView.onPause();
	}
}