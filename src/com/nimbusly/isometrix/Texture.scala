package com.nimbusly.isometrix

import java.io.IOException;
import java.io.InputStream;

import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLUtils;
import android.util.Log;

object TextureObj {
	private val TAG = "Texture(Object)";

	/**
	 * Create a Bitmap object from a png image resource
	 * 
	 * @param context Application context
	 * @param id resource id of png image
	 * @return Bitmap
	 */
	def loadBitmapFromResource(context : Context, id : Int) : Bitmap = {
		//Get the texture from the Android resource directory
		val is = context.getResources().openRawResource(id)
		var bitmap : Bitmap = null;
		try {
			bitmap = BitmapFactory.decodeStream(is)
		} finally {
			//Always clear and close
			try {
				is.close()
			} catch {
				case e:IOException => Log.e(TAG, "Unable to load texture", e) 
			}
		}
		bitmap
	}

    private val textureNameWorkspace = Array(1)
    
    /**
	 * Create a GL texture from a bitmap
	 * 
	 * Note: This code is NOT THREAD SAFE!
	 * 
	 * @param gl GL
	 * @param bitmap bitmaap to use as texture
	 * @return texture name of texture
	 */
	def loadGLTexture(gl : GL10, bitmap : Bitmap) : Int = {
        gl.glGenTextures(1, textureNameWorkspace, 0)
        gl.glBindTexture(GL10.GL_TEXTURE_2D, textureNameWorkspace(0))

        gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_NEAREST)
        gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR)

        gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_S, GL10.GL_CLAMP_TO_EDGE)
        gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_T, GL10.GL_CLAMP_TO_EDGE)

        gl.glTexEnvf(GL10.GL_TEXTURE_ENV, GL10.GL_TEXTURE_ENV_MODE, GL10.GL_REPLACE)
        
        GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, bitmap, 0)

        val error = gl.glGetError()
        if (error != GL10.GL_NO_ERROR) {
            Log.e(TAG, "Texture Load GLError: " + error)
        }
        return textureNameWorkspace(0)
    }

}

/**
 * This represents a GL Texture in Android java that is created from a bitmap.
 * 
 * Textures should be created in either onSurfaceCreated or onSurfaceChanged
 *
 * @author semprebon	
 */
class Texture(resourceId : Int) {


	private val TAG = "Texture";
	
	var textureName : Int = 0
	var resourceId : Int = 0
	var (bitmapWidth, bitmapHeight) = (0, 0)
	
	/**
	 * Create a texture from a drawable
	 * 
	 * @param resourceId id of drawable
	 */
	def this(context : Context, gl : GL10, resourceId : Int) = {
		this(resourceId)
		val bitmap = TextureObj.loadBitmapFromResource(context, resourceId)
		bitmapWidth = bitmap.getWidth()
		bitmapHeight = bitmap.getHeight()
		textureName = TextureObj.loadGLTexture(gl, bitmap)
	}

	/**
	 * Create a texture from a dynamically generated bitmap
	 * 
	 * @param resourceId id of drawable
	 */
	def this(gl : GL10, bitmap : Bitmap) = {
		this(-1)
		bitmapWidth = bitmap.getWidth()
		bitmapHeight = bitmap.getHeight()
		textureName = TextureObj.loadGLTexture(gl, bitmap)
	}

    private def roundToTextureSize(x : Int) : Int = {
    	var size = 1
    	while (size < 4096) {
    		val nextSize = size * size
    		if (x > size && x <= nextSize) {
    			return nextSize
    		}
    		size = nextSize
    	}
    	0
    }

}