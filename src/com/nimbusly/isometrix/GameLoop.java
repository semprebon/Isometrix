package com.nimbusly.isometrix;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Debug;
import android.util.Log;
import android.view.SurfaceHolder;

public class GameLoop extends Thread implements SurfaceHolder.Callback {

	private boolean isRunning;
	private SurfaceHolder surfaceHolder;
	private static final String TAG = "GameLoop";
	private Game game;
	BitmapDrawable greyTile, greenTile;
	private Sprite character;
	private Screen screen;
	private CanvasTiler tiler;
	private Point lastScreenOffset = null;
	private Context context;
	private Bitmap background;
	
	private long loopStartTime;
	private long frames;

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
		this.context = context;
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
		screen.setTileSize(60);
	}

	@Override
	public void run() {
		Log.i(TAG, "Thread started");
		loopStartTime = System.currentTimeMillis();
		frames = 0;
		String frameRate = "";
        Paint textPaint = new Paint();
        textPaint.setColor(0xffffff);
        //Debug.startMethodTracing("GameThread");
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
            ++frames;
            if (frames % 100 == 0) {
            	frameRate = Long.toString(getFrameRate());
        		long elapsed = System.currentTimeMillis() - loopStartTime;
        		//Log.i(TAG, "Frame rate:" + frameRate + "; " + elapsed/frames + " ms/frame");
            }
        }
		//Debug.stopMethodTracing();
	}
	
	protected void updateVideo(Canvas canvas) {
		synchronized(game) {
			screen.viewAt(new Point(game.getPosition()));
		}
		canvas.clipRect(0, 0, screen.getWidth()-1, screen.getHeight()-1);
		canvas.drawColor(0x000000, PorterDuff.Mode.SRC);
		tiler.canvas = canvas;
		screen.drawTiles(0, 0, screen.getWidth()-1, screen.getHeight()-1);
		character.draw(canvas, screen, game.getPosition(), game.getFacing(), game.getWalkState());
	}
	
	protected void drawBackground(Canvas canvas) {
		if (lastScreenOffset != null) {
			screen.drawTiles(0, 0, screen.getWidth()-1, screen.getHeight()-1);
			lastScreenOffset = screen.screenOffset();
			return;
		}
		Point currentScreenOffset = screen.screenOffset();
		Point offset = lastScreenOffset.offset(-currentScreenOffset.x, -currentScreenOffset.y);
		
		Rect current = new Rect(0, 0, screen.getWidth() - 1, screen.getHeight() - 1);
		Rect previous = new Rect(current);
		previous.offset(offset.x, offset.y);
		if (previous.intersect(current)) {
			current.intersect(previous);
			// copy and just draw missing areas;
		} else {
			screen.drawTiles(0, 0, screen.getWidth()-1, screen.getHeight()-1);
			lastScreenOffset = currentScreenOffset;
			return;
		}
	}
	
	/**
	 * Timing instrumentation
	 */
	public long getFrameRate() {
		long elapsed = System.currentTimeMillis() - loopStartTime;
		return frames * 1000 / elapsed;
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
