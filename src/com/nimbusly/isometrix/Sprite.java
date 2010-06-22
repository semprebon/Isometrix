package com.nimbusly.isometrix;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.opengles.GL10;
import javax.microedition.khronos.opengles.GL11;
import javax.microedition.khronos.opengles.GL11Ext;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;

/**
 * A sprite represents an animated bitmap that can be drawn on a screen.
 * 
 * @author semprebon
 *
 */
public class Sprite {
	Bitmap bitmap;
	public int height, width;
	int rows, cols;
	Texture texture; 
	int[] crop = new int[4];

	public int x = 0, y = 0;
	public int facing = 0, state = 0;
	
	private FloatBuffer vertexBuffer;
	private FloatBuffer textureBuffer;
	/** The buffer holding the indices */
	private ByteBuffer  indexBuffer;
	private static float vertices[] = {
		0.0f, 0.0f, 0.0f,
    	1.0f, 0.0f, 0.0f,
    	0.0f, 1.0f, 0.0f,
    	1.0f, 1.0f, 0.0f,
	};
	
    private float textureVertices[] = {    		
    		//Mapping coordinates for the vertices
    		0.0f, 0.0f,
    		0.0f, 1.0f,
    		1.0f, 0.0f,
    		1.0f, 1.0f, 
    };
	
    private byte indices[] = {
			//Faces definition
    		0,1,3, 0,3,2,
	};

    private FloatBuffer loadBuffer(float[] values) {
		ByteBuffer byteBuf = ByteBuffer.allocateDirect(values.length * 4);
		byteBuf.order(ByteOrder.nativeOrder());
		FloatBuffer result = byteBuf.asFloatBuffer();
		result.put(values);
		result.position(0);
		return result;
	}
	
    /**
     * Create a sprite from a drawable png image
     * 
     * @param context
     * @param id resource id of image
     * @param cols number of columns of sub-images
     * @param rows number of rows of sub-images
     */
	public Sprite(Context context, GL10 gl, int id, int cols, int rows) {
		texture = new Texture(context, gl, id);
		width = texture.bitmapWidth / cols;
		height = texture.bitmapHeight / rows;
	}

	/**
	 * Draw using textdra
	 * @param gl
	 */
	public void draw(GL10 gl) {
		int x0 = state * width;
		int y0 = facing * height;
		crop[0] = x0; crop[1] = y0; crop[2] = width; crop[3] = height;
        gl.glBindTexture(GL10.GL_TEXTURE_2D, texture.textureName);
		((GL11)gl).glTexParameteriv(GL10.GL_TEXTURE_2D, GL11Ext.GL_TEXTURE_CROP_RECT_OES, crop, 0);
		((GL11Ext) gl).glDrawTexiOES(0, 0, 1, width, height);
	}
	
	/**
	 * Draws using VBO
	 * @param gl
	 */
	public void drawX(GL10 gl) {
        gl.glBindTexture(GL10.GL_TEXTURE_2D, texture.textureName);
		gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
		gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);

		//Set the face rotation
		gl.glFrontFace(GL10.GL_CCW);
		
		//Enable the vertex and texture state
		gl.glVertexPointer(3, GL10.GL_FLOAT, 0, vertexBuffer);
		gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, textureBuffer);
		
		//Draw the vertices as triangles, based on the Index Buffer information
		gl.glDrawElements(GL10.GL_TRIANGLES, indices.length, GL10.GL_UNSIGNED_BYTE, indexBuffer);
		//gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 0, vertices.length / 3);
		
		//Disable the client state before leaving
		gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
		gl.glDisableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
//        gl.glPushMatrix();
//        gl.glLoadIdentity();
//        gl.glTranslatef(x, y, z);
//        //mGrid.draw(gl, true, false);
//        gl.glPopMatrix();

        //Set the face rotation
		gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 0, vertices.length / 3);
		gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
	}



}
