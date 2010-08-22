package com.nimbusly.isometrix;

import java.io.IOException;
import java.io.InputStream;
import java.nio.IntBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;
import javax.microedition.khronos.opengles.GL11;
import javax.microedition.khronos.opengles.GL11Ext;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.opengl.GLSurfaceView;
import android.opengl.GLUtils;
import android.os.Debug;
import android.util.Log;
import android.view.SurfaceHolder;

public class GameLoop extends Thread implements SurfaceHolder.Callback, GLSurfaceView.Renderer {

	private boolean isRunning;
	private SurfaceHolder surfaceHolder;
	private static final String TAG = "GameLoop";
	private Game game;
	BitmapDrawable[] tiles;
	int greyTextureName, greenTextureName;
	private Sprite character;
	private Screen screen;
	private GlTiler tiler;
	private Point lastScreenOffset = null;
	private Context context;
	private Bitmap background;
	
	private long loopStartTime;
	private long frames;

	private class CanvasTiler implements Screen.Tiler {
		public Canvas canvas;

		public void drawTile(Point origin, int width, int height, int tileIndex) {
			Drawable tile = tiles[tileIndex];
			tile.setBounds(origin.x, origin.y, origin.x + width - 1, origin.y + height - 1);
			tile.draw(canvas);
		}

	}
	
	private class GlTiler implements Screen.Tiler {
		public GL10 gl;

		public void drawTile(Point origin, int width, int height, int tileIndex) {
			int tile = tileIndex == 0 ? greenTextureName : greyTextureName;
			gl.glBindTexture(GL10.GL_TEXTURE_2D, tile);
			((GL11Ext) gl).glDrawTexiOES(origin.x, origin.y, 0, origin.x + width - 1, origin.y + height - 1);
		}

	}
	
	public GameLoop(Context context, Game ground, GLSurfaceView view, GameController controller) {
		super();
		this.context = context;
		view.setRenderer(this);
		this.surfaceHolder = view.getHolder();
		surfaceHolder.addCallback(this);
		screen = new Screen(100, 100);
		this.game = ground;
		controller.setScreen(screen);
//		greyTile = (BitmapDrawable) context.getResources().getDrawable(R.drawable.grey_tile);
//		greenTile = (BitmapDrawable) context.getResources().getDrawable(R.drawable.green_tile);
		//character = new Sprite(context, R.drawable.walking, 12, 8);
		tiler = new GlTiler();
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
            ++frames;
            if (frames % 100 == 0) {
            	frameRate = Long.toString(getFrameRate());
        		long elapsed = System.currentTimeMillis() - loopStartTime;
        		//Log.i(TAG, "Frame rate:" + frameRate + "; " + elapsed/frames + " ms/frame");
            }
        }
		//Debug.stopMethodTracing();
	}
	
//	protected void updateVideo(Canvas canvas) {
//		synchronized(game) {
//			screen.viewAt(new Point(game.getPosition()));
//		}
//		canvas.clipRect(0, 0, screen.getWidth()-1, screen.getHeight()-1);
//		canvas.drawColor(0x000000, PorterDuff.Mode.SRC);
//		tiler.canvas = canvas;
//		screen.drawTiles(0, 0, screen.getWidth()-1, screen.getHeight()-1);
//		character.draw(canvas, screen, game.getPosition(), game.getFacing(), game.getWalkState());
//	}
	
//	protected void drawBackground(Canvas canvas) {
//		if (lastScreenOffset != null) {
//			screen.drawTiles(0, 0, screen.getWidth()-1, screen.getHeight()-1);
//			lastScreenOffset = screen.screenOffset();
//			return;
//		}
//		Point currentScreenOffset = screen.screenOffset();
//		Point offset = lastScreenOffset.offset(-currentScreenOffset.x, -currentScreenOffset.y);
//		
//		Rect current = new Rect(0, 0, screen.getWidth() - 1, screen.getHeight() - 1);
//		Rect previous = new Rect(current);
//		previous.offset(offset.x, offset.y);
//		if (previous.intersect(current)) {
//			current.intersect(previous);
//			// copy and just draw missing areas;
//		} else {
//			screen.drawTiles(0, 0, screen.getWidth()-1, screen.getHeight()-1);
//			lastScreenOffset = currentScreenOffset;
//			return;
//		}
//	}
	
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

	/**
	 * Called by GLSurface view when its time to draw a frame.
	 */
	public void onDrawFrame(GL10 gl) {
		synchronized(game) {
			screen.viewAt(new Point(game.getPosition()));
		}
		tiler.gl = gl;
		screen.drawTiles(0, 0, screen.getWidth()-1, screen.getHeight()-1);
		//character.draw(canvas, screen, game.getPosition(), game.getFacing(), game.getWalkState());
	}

	public void onSurfaceChanged(GL10 gl, int width, int height) {
		gl.glViewport(0, 0, width, height);
		gl.glLoadIdentity();
        gl.glOrthof(0.0f, width, 0.0f, height, 0.0f, 1.0f);
        gl.glShadeModel(GL10.GL_FLAT);
        gl.glEnable(GL10.GL_BLEND);
        gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
        gl.glColor4x(0x10000, 0x10000, 0x10000, 0x10000);
        gl.glEnable(GL10.GL_TEXTURE_2D);
		screen.resize(width, height);
	}

	public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        /*
         * Some one-time OpenGL initialization can be made here probably based
         * on features of this particular context
         */
        gl.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT, GL10.GL_FASTEST);

        gl.glClearColor(0.5f, 0.5f, 0.5f, 1);
        gl.glShadeModel(GL10.GL_FLAT);
        gl.glDisable(GL10.GL_DEPTH_TEST);
        gl.glEnable(GL10.GL_TEXTURE_2D);
        /*
         * By default, OpenGL enables features that improve quality but reduce
         * performance. One might want to tweak that especially on software
         * renderer.
         */
        gl.glDisable(GL10.GL_DITHER);
        gl.glDisable(GL10.GL_LIGHTING);

        gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);

        //load textures
        sBitmapOptions.inPreferredConfig = Bitmap.Config.RGB_565;
//        greyTextureName = loadBitmap(context, gl, R.drawable.grey_tile);
//        greenTextureName = loadBitmap(context, gl, R.drawable.green_tile);
	}    
	private static BitmapFactory.Options sBitmapOptions = new BitmapFactory.Options();
	
	/** 
     * Loads a bitmap into OpenGL and sets up the common parameters for 
     * 2D texture maps. 
     */
    protected int loadBitmap(Context context, GL10 gl, int resourceId) {
        int textureName = -1;
        if (context != null && gl != null) {
        	int[] textureNames = new int[1];
            gl.glGenTextures(1, IntBuffer.wrap(textureNames));
            textureName = textureNames[0];
            gl.glBindTexture(GL10.GL_TEXTURE_2D, textureName);

            gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_NEAREST);
            gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR);

            gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_S, GL10.GL_CLAMP_TO_EDGE);
            gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_T, GL10.GL_CLAMP_TO_EDGE);

            gl.glTexEnvf(GL10.GL_TEXTURE_ENV, GL10.GL_TEXTURE_ENV_MODE, GL10.GL_REPLACE);

            InputStream is = context.getResources().openRawResource(resourceId);
            Bitmap bitmap;
            try {
                bitmap = BitmapFactory.decodeStream(is, null, sBitmapOptions);
            } finally {
                try {
                    is.close();
                } catch (IOException e) {
                    // Ignore.
                }
            }

            GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, bitmap, 0);
            int[] mCropWorkspace = new int[4];
            mCropWorkspace[0] = 0;
            mCropWorkspace[1] = bitmap.getHeight();
            mCropWorkspace[2] = bitmap.getWidth();
            mCropWorkspace[3] = -bitmap.getHeight();
            
            bitmap.recycle();

            ((GL11) gl).glTexParameteriv(GL10.GL_TEXTURE_2D, 
                    GL11Ext.GL_TEXTURE_CROP_RECT_OES, mCropWorkspace, 0);

            
            int error = gl.glGetError();
            if (error != GL10.GL_NO_ERROR) {
                Log.e(TAG, "Texture Load GLError: " + error);
            }
        
        }

        return textureName;
    }


	
}
