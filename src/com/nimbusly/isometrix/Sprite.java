package com.nimbusly.isometrix;

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
	int height, width;
	int rows, cols;
	
	public Sprite(Context context, int id, int cols, int rows) {
		BitmapDrawable drawable = (BitmapDrawable) context.getResources().getDrawable(id);
		bitmap = drawable.getBitmap();
		height = bitmap.getHeight() / rows;
		width = bitmap.getWidth() / cols;
	}
	
	public void draw(Canvas canvas, Screen screen, int facing, int state) {
		Point center = screen.getViewAt();
		int x0 = state * width;
		int y0 = facing * height;
		Rect src = new Rect(x0, y0, x0+width-1, y0+width-1);
		x0 = center.x - width/2;
		y0 = center.y - (height-1);
		Rect dest = new Rect(x0, y0, x0+width-1, y0+height-1); 
		canvas.drawBitmap(bitmap, src, dest, null);
	}
	


}
