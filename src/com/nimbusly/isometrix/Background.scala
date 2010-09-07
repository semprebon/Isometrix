package com.nimbusly.isometrix

import javax.microedition.khronos.opengles.GL10;
import javax.microedition.khronos.opengles.GL11;
import javax.microedition.khronos.opengles.GL11Ext;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;

object Background {
	def xyz(x : Int) = 24*x
}

/**
 * A background is a texture that is assembled from tiles and can be scrolled over in the view port.
 * 
 * @author semprebon
 *
 */
class Background(width : Int, height : Int) {
	var viewWidth = width
	var viewHeight = height
	var x = 0
	var y = 0
	var z = 1
	
	var textureWidth = 0
	var textureHeight = 0
	
	// TODO: for device independencies, these should be computed based on screen size:
	var tileHeight = 24
	var tileWidth = 48
	
	var texture : Texture = null
	
	var crop = Array[Int](0,0,0,0);
	
	val map = Array[Byte](
			0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
			0,1,1,1,1,1,0,0,0,0,0,1,1,1,1,1,1,1,1,0,
			0,1,1,1,1,1,0,0,0,0,0,1,1,1,1,1,1,1,1,0,
			0,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,0,
			0,1,1,1,1,1,0,0,0,1,0,1,1,1,1,1,1,1,1,0,
			0,0,0,0,0,0,0,0,0,1,0,1,1,1,1,1,1,1,1,0,
			0,0,0,0,0,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0,
			0,1,1,1,1,1,1,1,1,1,1,1,1,0,1,1,1,1,1,0,
			0,1,0,0,0,0,0,0,0,0,0,0,1,0,1,1,1,1,1,0,
			0,1,1,1,0,1,1,1,1,1,1,0,1,0,1,1,1,1,1,0,
			0,1,0,1,0,1,0,1,0,0,0,0,1,0,1,1,1,1,1,0,
			0,1,0,1,1,1,0,1,0,1,1,0,0,0,1,1,1,1,1,0,
			0,1,0,0,0,0,0,1,0,0,1,0,1,1,1,1,1,1,1,0,
			0,1,0,1,1,1,0,1,1,1,1,0,1,0,1,1,1,1,1,0,
			0,1,0,1,0,1,0,0,0,0,1,0,1,0,1,1,1,1,1,0,
			0,1,0,1,0,1,1,1,1,1,1,0,1,0,0,0,1,0,0,0,
			0,1,0,1,0,0,0,0,0,0,1,0,1,0,0,0,1,0,0,0,
			0,1,0,1,0,0,0,0,0,0,0,0,1,0,0,1,1,1,0,0,
			0,1,1,1,1,1,1,1,1,1,1,1,1,0,0,1,1,1,0,0,
			0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0
	)
	var mapWidth = 20
	var mapHeight = 20

	def this(context : Context, gl : GL10, width : Int, height : Int, tileResources : Array[Int]) {
		this(width, height);
		
		var tiles = new Array[Bitmap](tileResources.length)
		for (i <- 0 to tileResources.length-1) {
			tiles(i) = TextureObj.loadBitmapFromResource(context, i)
		}
		tileHeight = tiles(0).getHeight()
		tileWidth = tiles(0).getWidth()

		var bitmap = assembleBitmap(map, mapWidth, mapHeight, tiles)
		texture = new Texture(gl, bitmap)
	}
	
	def this(context : Context, gl : GL10,referenceId :Int, width : Int, height : Int) {
		this(width, height);
		texture = new Texture(context, gl, referenceId)
		textureWidth = texture.bitmapWidth
		textureHeight = texture.bitmapHeight
	}
	
	/**
	 * Draw the background from the current position
	 * 
	 * @param gl
	 */
	def draw(gl : GL10) {
		crop(0) = x 
		crop(1) = y 
		crop(2) = viewWidth 
		crop(3) = viewHeight
        gl.glBindTexture(GL10.GL_TEXTURE_2D, texture.textureName);
		gl.asInstanceOf[GL11].glTexParameteriv(GL10.GL_TEXTURE_2D, GL11Ext.GL_TEXTURE_CROP_RECT_OES, crop, 0);
		gl.asInstanceOf[GL11Ext].glDrawTexiOES(0, 0, 1, viewWidth, viewHeight);
	}

	/**
	 * Create the bitmap
	 * 
	 * TODO: Should probably load from image
	 * 
	 * @param map map defined as an array of tile indexes which define what tile to use at each point
	 * @param width width of map in tiles
	 * @param height height of map in tiles
	 * @param tiles tiles to use in drawing map
	 * 
	 * @return bitmap representing map
	 */
	def assembleBitmap(map : Array[Byte], width : Int, height : Int, 
			tiles : Array[Bitmap]) : Bitmap = {
		textureWidth = width * tileWidth
		textureHeight = height * tileHeight
		
		// TODO: compute size dynamically?
		var result = Bitmap.createBitmap(1024, 1024, Bitmap.Config.RGB_565)
		var canvas = new Canvas(result)
		var x = 0
		var y = (textureHeight / 2) - tileHeight/2
		var dest = new Rect(0, y, x+tileWidth*2, y+tileHeight)
		var paint : Paint = null
		var src : Rect = null // where is this set?
		for (row <- 0 to height-1) {
			for (col <- 0 to width-1) {
				val tileIndex = map(row*mapWidth + col)
				canvas.drawBitmap(tiles(tileIndex), src, dest, paint)
				dest.offset(tileWidth, tileHeight / 2)
			}
			dest.offset(-(mapWidth-1)*tileWidth, -(mapWidth+1)*tileHeight/2)
		}
		return result
	}

}