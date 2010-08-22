package com.nimbusly.isometrix;

import javax.microedition.khronos.opengles.GL10;
import javax.microedition.khronos.opengles.GL11;
import javax.microedition.khronos.opengles.GL11Ext;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.Log;

/**
 * A background is a texture that is assembled from tiles and can be scrolled over in the view port.
 * 
 * @author semprebon
 *
 */
public class Background {

	public int x = 0, y = 0, z = 1;
	public int viewWidth, viewHeight;
	public int textureWidth, textureHeight;
	
	// TODO: for device independencies, these should be computed based on screen size:
	public int tileHeight = 24, tileWidth = 48;
	public Texture texture;
	
	int[] crop = new int[4];
	
	private byte[] map = new byte[] {
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
			0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
	};
	int mapWidth = 20;
	int mapHeight = 20;

	private Background(int width, int height) {
		this.viewWidth = width;
		this.viewHeight = height;
	}
	
	public Background(Context context, GL10 gl, int width, int height, int[] tileResources) {
		this(width, height);
		
		Bitmap[] tiles = new Bitmap[tileResources.length];
		for (int i = 0; i < tileResources.length; ++i) {
			tiles[i] = Texture.loadBitmapFromResource(context, tileResources[i]);
		};
		tileHeight = tiles[0].getHeight();
		tileWidth = tiles[0].getWidth();

		Bitmap bitmap = assembleBitmap(map, mapWidth, mapHeight, tiles);
		texture = new Texture(gl, bitmap);
	}
	
	public Background(Context context, GL10 gl, int referenceId, int width, int height) {
		this(width, height);
		texture = new Texture(context, gl, referenceId);
		textureWidth = texture.bitmapWidth;
		textureHeight = texture.bitmapHeight;
	}
	
	/**
	 * Draw the background from the current position
	 * 
	 * @param gl
	 */
	public void draw(GL10 gl) {
		crop[0] = x; crop[1] = y; crop[2] = viewWidth; crop[3] = viewHeight;
        gl.glBindTexture(GL10.GL_TEXTURE_2D, texture.textureName);
		((GL11)gl).glTexParameteriv(GL10.GL_TEXTURE_2D, GL11Ext.GL_TEXTURE_CROP_RECT_OES, crop, 0);
		((GL11Ext) gl).glDrawTexiOES(0, 0, 1, viewWidth, viewHeight);
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
	public Bitmap assembleBitmap(byte[] map, int width, int height, Bitmap[] tiles) {
		textureWidth = width * tileWidth;
		textureHeight = height * tileHeight;
		
		// TODO: compute size dynamically?
		Bitmap result = Bitmap.createBitmap(1024, 1024, Bitmap.Config.RGB_565);
		Canvas canvas = new Canvas(result);
		int x = 0;
		int y = (textureHeight / 2) - tileHeight/2;
		Rect dest = new Rect(0, y, x+tileWidth*2, y+tileHeight);
		Paint paint = null;
		Rect src = null;
		for (int row = 0; row < height; ++row) {
			for (int col = 0; col < width; ++col) {
				int tileIndex = map[row*mapWidth + col];
				canvas.drawBitmap(tiles[tileIndex], src, dest, paint);
				dest.offset(tileWidth, tileHeight / 2);
			}
			dest.offset(-(mapWidth-1)*tileWidth, -(mapWidth+1)*tileHeight/2);
		}
		return result;
	}

}
