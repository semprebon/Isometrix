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
import android.opengl.GLU;
import android.opengl.GLUtils;
import android.opengl.GLSurfaceView.Renderer;

public class GameRenderer implements Renderer {

	private Cube cube;
	private float xrot;
	private float yrot; 	
	private float zrot; 
	
	private Context context;

	
	public GameRenderer(Context context) {
		this.context = context; 
		cube = new Cube();
	}

	public void onSurfaceCreated(GL10 gl, EGLConfig config) {
		cube.loadGLTexture(gl, this.context);

		gl.glEnable(GL10.GL_TEXTURE_2D);			//Enable Texture Mapping ( NEW )
		gl.glShadeModel(GL10.GL_SMOOTH); 			//Enable Smooth Shading
		gl.glClearColor(0.0f, 0.0f, 0.0f, 0.5f); 	//Black Background
		gl.glClearDepthf(1.0f); 					//Depth Buffer Setup
		gl.glEnable(GL10.GL_DEPTH_TEST); 			//Enables Depth Testing
		gl.glDepthFunc(GL10.GL_LEQUAL); 			//The Type Of Depth Testing To Do
		
		//Really Nice Perspective Calculations
		gl.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT, GL10.GL_NICEST); 
	}

	/**
	 * Here we do our drawing
	 */
	public void onDrawFrame(GL10 gl) {
		//Clear Screen And Depth Buffer
		gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);	
		gl.glLoadIdentity();					//Reset The Current Modelview Matrix
		
		/*
		 * Minor changes to the original tutorial
		 * 
		 * Instead of drawing our objects here,
		 * we fire their own drawing methods on
		 * the current instance
		 */
		gl.glTranslatef(0.0f, 0.0f, -5.0f);	//Move down 1.2 Unit And Into The Screen 6.0
		gl.glRotatef(xrot,1.0f,0.0f,0.0f);	
		gl.glRotatef(yrot,0.0f,1.0f,0.0f);	
		gl.glRotatef(zrot,0.0f,0.0f,1.0f);	
		cube.draw(gl);						//Draw the square
				
		xrot += 0.3f;
		yrot += 0.2f;
		zrot += 0.4f;
	}

	/**
	 * If the surface changes, reset the view
	 */
	public void onSurfaceChanged(GL10 gl, int width, int height) {
		if(height == 0) { 						//Prevent A Divide By Zero By
			height = 1; 						//Making Height Equal One
		}

		gl.glViewport(0, 0, width, height); 	//Reset The Current Viewport
        gl.glMatrixMode(GL10.GL_PROJECTION); 	//Select The Projection Matrix
		gl.glOrthof(0.0f, width, 0.0f, height, 0.0f, 1.0f);
		gl.glLoadIdentity(); 					//Reset The Projection Matrix

		//Calculate The Aspect Ratio Of The Window
		//GLU.gluPerspective(gl, 45.0f, (float)width / (float)height, 0.1f, 100.0f);

		//gl.glRotatef(35.264f, 1.0f, 0.0f, 0.0f);
		//gl.glRotatef(-45.0f, 0.0f, 1.0f, 0.0f);
		//gl.glScalef(1.0f, 1.0f, -1.0f);

		gl.glMatrixMode(GL10.GL_MODELVIEW); 	//Select The Modelview Matrix
		gl.glLoadIdentity(); 					//Reset The Modelview Matrix
	}
	
	public static class Cube {
		
		/** The buffer holding the vertices */
		private FloatBuffer vertexBuffer;
		private FloatBuffer textureBuffer;
		/** The buffer holding the indices */
		private ByteBuffer  indexBuffer;
		/** Our texture pointer */
		private int[] textures = new int[1];
		
		/** The initial vertex definition */
	    private float vertices[] = {
				//Vertices according to faces
	    		-1.0f, -1.0f, 1.0f, //Vertex 0
	    		1.0f, -1.0f, 1.0f,  //v1
	    		-1.0f, 1.0f, 1.0f,  //v2
	    		1.0f, 1.0f, 1.0f,   //v3
	    		
	    		1.0f, -1.0f, 1.0f,	//...
	    		1.0f, -1.0f, -1.0f,    		
	    		1.0f, 1.0f, 1.0f,
	    		1.0f, 1.0f, -1.0f,
	    		
	    		1.0f, -1.0f, -1.0f,
	    		-1.0f, -1.0f, -1.0f,    		
	    		1.0f, 1.0f, -1.0f,
	    		-1.0f, 1.0f, -1.0f,
	    		
	    		-1.0f, -1.0f, -1.0f,
	    		-1.0f, -1.0f, 1.0f,    		
	    		-1.0f, 1.0f, -1.0f,
	    		-1.0f, 1.0f, 1.0f,
	    		
	    		-1.0f, -1.0f, -1.0f,
	    		1.0f, -1.0f, -1.0f,    		
	    		-1.0f, -1.0f, 1.0f,
	    		1.0f, -1.0f, 1.0f,
	    		
	    		-1.0f, 1.0f, 1.0f,
	    		1.0f, 1.0f, 1.0f,    		
	    		-1.0f, 1.0f, -1.0f,
	    		1.0f, 1.0f, -1.0f,
									};
	    private float texture[] = {    		
	    		//Mapping coordinates for the vertices
	    		0.0f, 0.0f,
	    		0.0f, 1.0f,
	    		1.0f, 0.0f,
	    		1.0f, 1.0f, 
	    		
	    		0.0f, 0.0f,
	    		0.0f, 1.0f,
	    		1.0f, 0.0f,
	    		1.0f, 1.0f,
	    		
	    		0.0f, 0.0f,
	    		0.0f, 1.0f,
	    		1.0f, 0.0f,
	    		1.0f, 1.0f,
	    		
	    		0.0f, 0.0f,
	    		0.0f, 1.0f,
	    		1.0f, 0.0f,
	    		1.0f, 1.0f,
	    		
	    		0.0f, 0.0f,
	    		0.0f, 1.0f,
	    		1.0f, 0.0f,
	    		1.0f, 1.0f,
	    		
	    		0.0f, 0.0f,
	    		0.0f, 1.0f,
	    		1.0f, 0.0f,
	    		1.0f, 1.0f,
													};
		
	    private byte indices[] = {
				//Faces definition
	    		0,1,3, 0,3,2, 			//Face front
	    		4,5,7, 4,7,6, 			//Face right
	    		8,9,11, 8,11,10, 		//... 
	    		12,13,15, 12,15,14, 	
	    		16,17,19, 16,19,18, 	
	    		20,21,23, 20,23,22, 	
									};
		/**
		 * The Square constructor.
		 * 
		 * Initiate the buffers.
		 */
		public Cube() {
			//
			ByteBuffer byteBuf = ByteBuffer.allocateDirect(vertices.length * 4);
			byteBuf.order(ByteOrder.nativeOrder());
			vertexBuffer = byteBuf.asFloatBuffer();
			vertexBuffer.put(vertices);
			vertexBuffer.position(0);

			byteBuf = ByteBuffer.allocateDirect(texture.length * 4);
			byteBuf.order(ByteOrder.nativeOrder());
			textureBuffer = byteBuf.asFloatBuffer();
			textureBuffer.put(texture);
			textureBuffer.position(0);

			indexBuffer = ByteBuffer.allocateDirect(indices.length);
			indexBuffer.put(indices);
			indexBuffer.position(0);
		}

		/**
		 * The object own drawing function.
		 * Called from the renderer to redraw this instance
		 * with possible changes in values.
		 * 
		 * @param gl - The GL context
		 */
		public void draw(GL10 gl) {
			gl.glBindTexture(GL10.GL_TEXTURE_2D, textures[0]);
			//Enable vertex buffer
			gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
			gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);

			//Set the face rotation
			gl.glFrontFace(GL10.GL_CW);
			
			//Point to our vertex buffer
			gl.glVertexPointer(3, GL10.GL_FLOAT, 0, vertexBuffer);
			gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, textureBuffer);
			
			
			//Draw the vertices as triangle strip
			gl.glDrawElements(GL10.GL_TRIANGLES, indices.length, GL10.GL_UNSIGNED_BYTE, indexBuffer);
			
			//Disable the client state before leaving
			gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
			gl.glDisableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
		}

		public void loadGLTexture(GL10 gl, Context context) {
			//Get the texture from the Android resource directory
			InputStream is = context.getResources().openRawResource(R.drawable.nehe);
			Bitmap bitmap = null;
			try {
				//BitmapFactory is an Android graphics utility for images
				bitmap = BitmapFactory.decodeStream(is);

			} finally {
				//Always clear and close
				try {
					is.close();
					is = null;
				} catch (IOException e) {
				}
			}

			//Generate one texture pointer...
			gl.glGenTextures(1, textures, 0);
			//...and bind it to our array
			gl.glBindTexture(GL10.GL_TEXTURE_2D, textures[0]);
			
			//Create Nearest Filtered Texture
			gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_NEAREST);
			gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR);

			//Different possible texture parameters, e.g. GL10.GL_CLAMP_TO_EDGE
			gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_S, GL10.GL_REPEAT);
			gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_T, GL10.GL_REPEAT);
			
			//Use the Android GLUtils to specify a two-dimensional texture image from our bitmap
			GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, bitmap, 0);
			
			//Clean up
			bitmap.recycle();
		}
	}


}
