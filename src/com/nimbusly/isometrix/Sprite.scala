package com.nimbusly.isometrix

import android.util.Log
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.opengles.GL10;
import javax.microedition.khronos.opengles.GL11;
import javax.microedition.khronos.opengles.GL11Ext;

import android.content.Context;
import android.graphics.Bitmap;

object Sprite {
}
/**
 * A sprite represents an animated bitmap that can be drawn on a screen.
     * Create a sprite from a drawable png image.
     * 
     * The sprite image is a cols x rows array of sub-images, each of which represents one frame of 
     * sprite animation. Each row represents an image facing (E, NE, N, NW, W, SW, S and SE). Each
     * column represents a state or animation step. The sprite state and facing can be changed, and
     * when drawn, it will select the appropriate sub-image to draw based on the current facing and
     * state.
     * 
     * @param context
     * @param id resource id of image
     * @param cols number of columns (states) of sub-images
     * @param rows number of rows (facings) of sub-images
 * 
 * @author semprebon
 *
 */
class Sprite(context : Context, gl : GL10, id : Int, val width : Int, val height : Int, var isVob : Boolean = false) {
	
	/**
	 * This class defines a single frame from the texture. By pre-computing these values, we can extract the
	 * desired frame more quickly
	 */
	class SpriteFrame(sprite : Sprite, frame : Int) {
		def texture = sprite.texture
		def xFrameCount = texture.bitmapWidth / width
		def yFrameCount = texture.bitmapHeight / height
		def width = sprite.width
		def height = sprite.height
		
		val xOffset = width * (frame % xFrameCount)
		val yOffset = height * (frame / xFrameCount)
		val normXOffset = xOffset * 1.0f / texture.bitmapWidth
		val normYOffset = yOffset * 1.0f / texture.bitmapHeight
		
		/**
		 * The crop array for use with direct-drawn sprites; x, y, width, height in texture pixel coordinates
		 */
		val crop = Array(xOffset, yOffset+height, width, -height)

		/**
		 * The texture vertext vector for use with VOB-drawn sprites; A set of four points corresponding
		 * to the vertexes of the sprite, in texture coordinates (0.0 - 1.0)
		 */
		val textureBuffer :  FloatBuffer = loadBuffer(Array(
		    normXOffset, normYOffset + textureHeight,
		    normXOffset + textureWidth, normYOffset + textureHeight,
		 	normXOffset, normYOffset,
		 	normXOffset + textureWidth, normYOffset))
	}
	
	val TAG = "Sprite"

	private val texture = new Texture(context, gl, id)
	
	val xFrameCount = texture.bitmapWidth / width
	val yFrameCount = texture.bitmapHeight / height
	val frameCount = xFrameCount * yFrameCount

	private val spriteFrames = 0.until(frameCount).map((i : Int) => { new SpriteFrame(this, i) })
	
	def frameCoordinates(f : Int) : (Int, Int) = (width * (f % xFrameCount), height * (f / xFrameCount))
	
	def normalizedFrameCoordinates(f : Int) : (Float, Float) = {
		val (x, y) = frameCoordinates(f)
		(x * 1.0f / texture.bitmapWidth, y * 1.0f / texture.bitmapHeight)
	}
	
	private val crop = new Array[Int](4)

	var x = 0
	var y = 0
	var frame = 0
	
    // This is an array of all the vertices in the object. In the case of the rectangular
	// sprite, there are four. These are represented in normalized format (1.0 x 1.0 square) which
	// then needs to be translated and scaled before being rendered.
	private val vertices = Array(
		0.0f, 0.0f, 0.0f,
    	1.0f, 0.0f, 0.0f,
    	0.0f, 1.0f, 0.0f,
    	1.0f, 1.0f, 0.0f)
	private var vertexBuffer : FloatBuffer = loadBuffer(vertices)
	
	// This defines coodinates in texture space (0.0 - 1.0) that correspond to the
	// coordinates of the sprite
	val textureWidth = width * 1.0f / texture.bitmapWidth
	val textureHeight = height * 1.0f / texture.bitmapHeight
	
	// Mapping coordinates for the vertices
	// private val textureVertices = Array(               
	//     0.0f, textureHeight,
	//     textureWidth, textureHeight,
	// 	0.0f, 0.0f,
	// 	textureWidth, 0.0f)
	private val textureVertices = new Array[Float](8)
	//     0.0f, textureHeight,
	//     textureWidth, textureHeight,
	// 	0.0f, 0.0f,
	// 	textureWidth, 0.0f)
	private var textureBuffer :  FloatBuffer = loadBuffer(textureVertices)
	private val textureBuffer3 = ByteBuffer.allocateDirect(8 * 4).asFloatBuffer()
	

	// The index buffer basically defines the two triangle areas that make up the sprite
	// by referencing the vectors in vertextBuffer. This allows a multi-sided object to be
	// represented by a minimal number of its vertices. In this case, the sprite is actually
	// made up of a mesh of two triangular elements.
	private val indices : Array[Byte] = Array(0,1,3, 0,3,2)
	private var indexBuffer : ByteBuffer = ByteBuffer.allocateDirect(indices.length)
	indexBuffer.put(indices)
	indexBuffer.position(0)

    def loadBuffer(values : Array[Float]) : FloatBuffer = {
		val byteBuf = ByteBuffer.allocateDirect(values.length * 4)
		byteBuf.order(ByteOrder.nativeOrder())
		val result : FloatBuffer = byteBuf.asFloatBuffer()
		result.put(values)
		result.position(0)
		result
	}
	
	/**
	 * Draw sprite at current position using current facing and state
	 * 
	 * @param gl
	 */
	def draw(gl : GL10) {
		if (isVob) drawAsVob(gl) else drawDirect(gl) 
	}
	
	/**
	 * Directly draw the cropped section of the texture on to the screen using the draw_texture
	 * GL 1.1 extension. This should be fast for painting 2d elements to the screen, but may not be
	 * supported
	 * 
	 * @param gl
	 */
	def drawDirect(gl : GL10) {
		// val (x0, y0) = frameCoordinates(frame)
		// Log.d(TAG, "Got xFrameCount=" + xFrameCount + " from " + texture.bitmapWidth)
		// Log.d(TAG, "croping texture at " + x0 + "," + y0)
		// crop(0) = x0; crop(1) = y0+height; crop(2) = width; crop(3) = -height;
        gl.glBindTexture(GL10.GL_TEXTURE_2D, texture.textureName)
		gl.asInstanceOf[GL11].glTexParameteriv(GL10.GL_TEXTURE_2D, GL11Ext.GL_TEXTURE_CROP_RECT_OES, 
			spriteFrames(frame).crop, 0)
		gl.asInstanceOf[GL11Ext].glDrawTexiOES(x, y, 1, width, height)
	}
	
	def setPosition(x : Int, y : Int) {
		vertexBuffer.put(x)
		vertexBuffer.put(y)
		vertexBuffer.put(x + width)
		vertexBuffer.put(y + height)
		vertexBuffer.position(0)
	}
	
	def updateTextureCrop(frame : Int) {
		def putTextureCropPoint(x : Float, y : Float) { 
			textureBuffer.put(x); 
			textureBuffer.put(y); 
		}

		val (x0, y0) = normalizedFrameCoordinates(frame)
		Log.d(TAG, "croping texture at " + x0 + "," + y0 + " for " + textureWidth + "x" + textureHeight)
		textureBuffer.rewind
		putTextureCropPoint(x0, y0 + textureHeight) 
		putTextureCropPoint(x0 + textureWidth, y0 + textureHeight)
		putTextureCropPoint(x0, y0)
		putTextureCropPoint(x0 + textureWidth, y0)
		textureBuffer.rewind
	}
	
	/**
	 * Draws using VBO
	 * @param gl
	 */
	def drawAsVob(gl : GL10) {
		//updateTextureCrop(frame)
		
		// translate to position before drawing
        //gl.glPushMatrix();
		//gl.glMatrixMode(GL10.GL_PROJECTION); 	//Select The Projection Matrix
		//gl.glOrthof(-1.0f, 1.0f, -1.0f, 1.0f, 0.01f, 1000.0f);

        gl.glLoadIdentity()

        gl.glTranslatef(x, y, 0.0F)
        gl.glScalef(width, height, 1.0f)
        //setPosition(x, y)
        // set gl state
        gl.glBindTexture(GL10.GL_TEXTURE_2D, texture.textureName)
		gl.glEnableClientState(GL10.GL_VERTEX_ARRAY)
		gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY)

		//Set the face rotation
		//gl.glFrontFace(GL10.GL_CCW)
		
		//Enable the vertex and texture state
		//gl.glColor4f(1.0f, 0.5f, 0.5f, 1.0f)
		gl.glVertexPointer(3, GL10.GL_FLOAT, 0, vertexBuffer)
		gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, textureBuffer)
		
		//Draw the vertices as triangles, based on the Index Buffer information
		//gl.glDrawArrays(GL10.GL_TRIANGLES, 0, testVertices.length)
		gl.glDrawElements(GL10.GL_TRIANGLES, indices.length, GL10.GL_UNSIGNED_BYTE, indexBuffer)
		//gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 0, vertices.length / 3);
		
		//Disable the client state before leaving
		gl.glDisableClientState(GL10.GL_VERTEX_ARRAY)
		gl.glDisableClientState(GL10.GL_TEXTURE_COORD_ARRAY)

		//Set the face rotation
		//gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 0, vertices.length / 3)
	
        //gl.glPopMatrix();

	}
	
}
