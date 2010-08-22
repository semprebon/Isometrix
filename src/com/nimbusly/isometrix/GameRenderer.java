package com.nimbusly.isometrix;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLSurfaceView;
import android.opengl.GLU;
import android.opengl.GLUtils;
import android.opengl.GLSurfaceView.Renderer;
import android.util.Log;

public class GameRenderer implements Renderer {

	protected Context context;
	protected int screenWidth, screenHeight;

	private static final String TAG = "GameRenderer";
	
	protected long startTime;
	protected long frameCount = 0;
	StringBuffer logMessage = new StringBuffer(100);
	
	Boolean stopped;

	public GameRenderer(Context context) {
		this.context = context; 
	}

	/**
	 * Called from another thread to stop rendering thread
	 * @param view
	 */
	public void stopRendering(final GLSurfaceView view) {
		view.queueEvent(new Runnable() {
			public void run() {
	            GameRenderer.this.stop(view);
	        }
		});
        synchronized (stopped) {
            while (!stopped) {
                try { stopped.wait(); } catch (InterruptedException e) {}
            }
        }
	}

	/**
	 * This supports a work around to a race condition in pause. To pause renderer from input thread,
	 * queueEvent(
	 * @param view
	 */
	public void stop(GLSurfaceView view) {
		view.onPause();
		synchronized (stopped) {
			stopped = true;
		}
	}
	/**
	 * Initialize the GL surface
	 */
	public void onSurfaceCreated(GL10 gl, EGLConfig config) {
		gl.glEnable(GL10.GL_TEXTURE_2D);			//Enable Texture Mapping ( NEW )
		gl.glShadeModel(GL10.GL_FLAT); 			//Enable Smooth Shading
		gl.glClearColor(0.5f, 0.5f, 0.5f, 1.0f); 	//Black Background
		gl.glDisable(GL10.GL_DEPTH_TEST); 			//Enables Depth Testing
        gl.glDisable(GL10.GL_DITHER);
        gl.glDisable(GL10.GL_LIGHTING);
		gl.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT, GL10.GL_FASTEST);
		startTime = System.currentTimeMillis();
	}

	/**
	 * If the surface changes, reset the view
	 */
	public void onSurfaceChanged(GL10 gl, int width, int height) {
		if(height == 0) { 						//Prevent A Divide By Zero By
			height = 1; 						//Making Height Equal One
		}
		screenWidth = width;
		screenHeight = height;

		gl.glViewport(0, 0, width, height); 	//Reset The Current Viewport
        gl.glMatrixMode(GL10.GL_PROJECTION); 	//Select The Projection Matrix
		gl.glOrthof(0.0f, width, 0.0f, height, 0.0f, 1.0f);
		gl.glLoadIdentity(); 					//Reset The Projection Matrix

		gl.glMatrixMode(GL10.GL_MODELVIEW); 	//Select The Modelview Matrix
		gl.glLoadIdentity(); 					//Reset The Modelview Matrix
	}
	
	/**
	 * Here we do our drawing
	 */
	public void onDrawFrame(GL10 gl) {
		long currentTime = System.currentTimeMillis();
		long elapsed = currentTime - startTime;
		if (currentTime > startTime + 10000) {
			reportFramesPerSecond(elapsed, frameCount);
			startTime = currentTime;
			frameCount = 0;
		}
		frameCount += 1; 
	}

	public void reportFramesPerSecond(long elapsed, long frameCount) {
		Log.d(TAG, "1 frame took " + (elapsed / frameCount) + "ms; FPS = " + (frameCount * 1000 / elapsed));
	}
	
}
