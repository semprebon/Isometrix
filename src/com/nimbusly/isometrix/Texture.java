package com.nimbusly.isometrix;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.opengles.GL10;
import javax.microedition.khronos.opengles.GL11;
import javax.microedition.khronos.opengles.GL11Ext;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLUtils;
import android.util.Log;

/**
 * This represents a GL Texture in Android java that is created from a bitmap.
 * 
 * Textures should be created in either onSurfaceCreated or onSurfaceChanged
 *
 * @author semprebon
 */

public class Texture {

	private static final String TAG = "Texture";
	
	protected int textureName;
	private int resourceId;
	public int bitmapWidth, bitmapHeight;
	
	/**
	 * Create a texture from a drawable
	 * 
	 * @param resourceId id of drawable
	 */
	public Texture(Context context, GL10 gl, int resourceId) {
		this.resourceId = resourceId;
		
        Bitmap bitmap = loadBitmapFromResource(context, resourceId);
		bitmapWidth = bitmap.getWidth();
		bitmapHeight = bitmap.getHeight();
		textureName = loadGLTexture(gl, bitmap);
	}

	/**
	 * Create a texture from a dynamically generated bitmap
	 * 
	 * @param resourceId id of drawable
	 */
	public Texture(GL10 gl, Bitmap bitmap) {
		this.resourceId = -1;
		bitmapWidth = bitmap.getWidth();
		bitmapHeight = bitmap.getHeight();
		textureName = loadGLTexture(gl, bitmap);
	}

	/**
	 * Create a Bitmap object from a png image resource
	 * 
	 * @param context Application context
	 * @param id resource id of png image
	 * @return Bitmap
	 */
	public static Bitmap loadBitmapFromResource(Context context, int id) {
		//Get the texture from the Android resource directory
		InputStream is = context.getResources().openRawResource(id);
		Bitmap bitmap = null;
		try {
			bitmap = BitmapFactory.decodeStream(is);
		} finally {
			//Always clear and close
			try {
				is.close();
				is = null;
			} catch (IOException e) {
			}
		}
		return bitmap;
	}
	
    private static int[] textureNameWorkspace = new int[1];

    /**
	 * Create a GL texture from a bitmap
	 * 
	 * @param gl GL
	 * @param bitmap bitmaap to use as texture
	 * @return texture name of texture
	 */
	public static int loadGLTexture(GL10 gl, Bitmap bitmap) {
        gl.glGenTextures(1, textureNameWorkspace, 0);
        gl.glBindTexture(GL10.GL_TEXTURE_2D, textureNameWorkspace[0]);

        gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_NEAREST);
        gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR);

        gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_S, GL10.GL_CLAMP_TO_EDGE);
        gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_T, GL10.GL_CLAMP_TO_EDGE);

        gl.glTexEnvf(GL10.GL_TEXTURE_ENV, GL10.GL_TEXTURE_ENV_MODE, GL10.GL_REPLACE);
        
        GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, bitmap, 0);

        int error = gl.glGetError();
        if (error != GL10.GL_NO_ERROR) {
            Log.e(TAG, "Texture Load GLError: " + error);
        }
        return textureNameWorkspace[0];
    }

}
