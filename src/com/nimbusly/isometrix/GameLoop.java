package com.nimbusly.isometrix;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.SurfaceHolder;

public class GameLoop extends Thread implements SurfaceHolder.Callback {

	private boolean isRunning;
	private SurfaceHolder surfaceHolder;
	private static final String TAG = "GameLoop";
	private Game game;
	private BitmapDrawable greyTile, greenTile;
	private Sprite character;
	private Screen screen;
	private CanvasTiler tiler;

	private class CanvasTiler implements Screen.Tiler {
		public Canvas canvas;

		public void drawTile(Point origin, int width, int height, int tileIndex) {
			Drawable tile = tileIndex == 0 ? greenTile : greyTile;
			tile.setBounds(origin.x, origin.y, origin.x + width - 1, origin.y + height - 1);
			tile.draw(canvas);
		}

	}
	
	public GameLoop(Context context, Game ground, SurfaceHolder surfaceHolder, GameController controller) {
		super();
		this.surfaceHolder = surfaceHolder;
		surfaceHolder.addCallback(this);
		screen = new Screen(100, 100);
		this.game = ground;
		controller.setScreen(screen);
		greyTile = (BitmapDrawable) context.getResources().getDrawable(R.drawable.grey_tile);
		greenTile = (BitmapDrawable) context.getResources().getDrawable(R.drawable.green_tile);
		character = new Sprite(context, R.drawable.walking, 12, 8);
		tiler = new CanvasTiler();
		screen.setTiler(tiler);
		screen.setGame(ground);
	}

	@Override
	public void run() {
		Log.i(TAG, "Thread started");
		while (isRunning) {
            Canvas canvas = null;
            long now = System.currentTimeMillis();
           	game.updateState(now);
            try {
                canvas = surfaceHolder.lockCanvas(null);
                synchronized (surfaceHolder) {
                    updateVideo(canvas);
                }
            } finally {
                if (canvas != null) {
                    surfaceHolder.unlockCanvasAndPost(canvas);
                }
            }
        }
			
	}
	
	private void updateVideo(Canvas canvas) {
		synchronized(game) {
			screen.viewAt(new Point(game.getXPosition() * 20, game.getYPosition() * 20));
		}
		canvas.clipRect(0, 0, screen.getWidth()-1, screen.getHeight()-1);
		canvas.drawColor(0x000000, PorterDuff.Mode.SRC);
		tiler.canvas = canvas;
		screen.drawTiles();
		character.draw(canvas, screen, game.getFacing(), game.getWalkState());
	}
	
	/**
	 * Surface Handler
	 */
	public void surfaceChanged(SurfaceHolder holder, int format, int width,	int height) {
		screen.resize(width, height);
	}

	public void surfaceCreated(SurfaceHolder holder) {
		isRunning = true;
		start();
	}

	public void surfaceDestroyed(SurfaceHolder holder) {
        boolean retry = true;
        isRunning = false;
        while (retry) {
            try {
                this.join();
                retry = false;
            } catch (InterruptedException e) {
            }
        }
	}

	
}
