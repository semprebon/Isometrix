package com.nimbusly.isometrix

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.opengl.GLSurfaceView.Renderer;
import android.util.Log;

/**
 * GameRenderer is a custom GL Renderer that is responsible for rendering the game 
 * into the GLSurfaceView. Rendering begins when this class is added to a GlSurfaceView,
 * although it can be stopped and restarted as needed.
 * 
 * @author semprebon
 *
 */
class GameRenderer(context: Context) extends Renderer {
	protected var screenWidth = 1;
	protected var screenHeight = 1;

	private val TAG = "GameRenderer"
	
	protected var startTime = 0L;
	protected var frameCount = 0;
	protected var logMessage: StringBuffer = new StringBuffer(100);
	
	var stopped: Boolean = true;
	var lock: AnyRef = new Object()

	implicit def runnable(f: () => Unit): Runnable =
		new Runnable() { def run() = f() }

	/**
	 * Called from another thread to stop rendering thread.
	 * 
	 * stopRendering first queues an even to set a stop flag, then waits for that
	 * event to be executed and the flag to be set.
	 * to
	 * 
	 * @param view the GLSurfaceView this renderer is rendering to
	 */
	def stopRendering(view: GLSurfaceView): Unit = {
		view.queueEvent(runnable(() => GameRenderer.this.stop(view)))
        lock.synchronized {
            while (!stopped) {
               	lock.wait(); 
            }
        }
	}

	/**
	 * Stops the view rendering from rendering thread
	 *
	 * @param view the GLSurfaceView this renderer is rendering to
	 */
	def stop(view: GLSurfaceView) {
		view.onPause();
		lock.synchronized {
			stopped = true;
		}
	}
	
	/**
	 * Initialize the GL surface.
	 */
	def onSurfaceCreated(gl: GL10, config: EGLConfig) {
		gl.glEnable(GL10.GL_TEXTURE_2D);			//Enable Texture Mapping ( NEW )
		gl.glShadeModel(GL10.GL_FLAT); 			//Enable Smooth Shading
		gl.glClearColor(0.5f, 0.5f, 0.5f, 1.0f); 	//Black Background
		gl.glDisable(GL10.GL_DEPTH_TEST); 			//Enables Depth Testing
        gl.glDisable(GL10.GL_DITHER);
        gl.glDisable(GL10.GL_LIGHTING);
		gl.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT, GL10.GL_FASTEST);
		Log.d(TAG, "Extensions:" + gl.glGetString(GL10.GL_EXTENSIONS))
		Log.d(TAG, "Version:" + gl.glGetString(GL10.GL_VERSION))
		startTime = System.currentTimeMillis();
	}

	/**
	 * Reset anything that needs to be reset if the surface changes (as in dimension changes)
	 */
	def onSurfaceChanged(gl: GL10, width : Int, height : Int) {
		
		if (height == 0) { 						//Prevent A Divide By Zero By
			return 						//Making Height Equal One
		}
		screenWidth = width;
		screenHeight = height;
		Log.d(TAG, "Setting window size to " + width + "x" + height)
		gl.glViewport(0, 0, width, height); 	//Reset The Current Viewport
        gl.glMatrixMode(GL10.GL_PROJECTION); 	//Select The Projection Matrix
		gl.glOrthof(0.0f, width, 0.0f, height, 0.001f, 100.0f);
		//gl.glLoadIdentity(); 					//Reset The Projection Matrix

		gl.glMatrixMode(GL10.GL_MODELVIEW); 	//Select The Modelview Matrix
		gl.glLoadIdentity(); 					//Reset The Modelview Matrix
	}
	
	/**
	 * Render the scene. You should override this class to implement your game-specific drawing.
	 * 
	 * TODO: this should probably call an abstract method to do the drawing
	 */
	def onDrawFrame(gl: GL10) {
		val currentTime = System.currentTimeMillis();
		val elapsed = currentTime - startTime;
		if (currentTime > startTime + 10000) {
			reportFramesPerSecond(elapsed, frameCount);
			startTime = currentTime;
			frameCount = 0;
		}
		frameCount += 1; 
	}

	/**
	 * Report fromes per second to the log (allocates memory)
	 * 
	 * @param elapsed
	 * @param frameCount
	 */
	def reportFramesPerSecond(elapsed : Long, frameCount : Long) {
		Log.d(TAG, "1 frame took " + (elapsed / frameCount) + "ms; FPS = " + (frameCount * 1000 / elapsed))
	}
	
}