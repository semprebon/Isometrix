package com.nimbusly.isometrix;

import android.util.Log;

public class Game {

	private static final String TAG = "Ground";

	private Point position;
	private int width = 0;
	private int height = 0;
	private Point origin, destination;
	private long startTime;
	private int walkState;
	private int facing;
	private static final int TILE_SIZE = 32;
	
	private static final int[][] DIRECTION_INDEX = new int[][] {
		new int[] { 6, 7, 0 }, 
		new int[] { 5, 0, 1 }, 
		new int[] { 4, 3, 2 }
	};
	private long speed = 400; // ms per tile
	private long time;
	
	public void move(int dx, int dy) {
		//Log.d(TAG, "Moving " + dx + "," + dy);
		synchronized(this) {
			facing = directionIndex(dx, dy);
			position.x = (position.x + dx) % width;
			position.y = (position.y + dy) % width;
		}
	}
	
	public void updateState(long time) {
		this.time = time;
		walkState = (int) Math.abs((time / 100) % 12);
		// compute movement
		if (destination != null && !position.equals(destination)) {
			double distance = origin.distanceTo(destination);
			long step = (time - startTime) * TILE_SIZE / speed;
			if (step < distance) {
				int dx = (int) Math.round(step * (destination.x - origin.x) / distance);
				int dy = (int) Math.round(step * (destination.y - origin.y) / distance);
				facing = directionIndex(dx, dy);
				position.set(origin.x + dx, origin.y + dy);
			} else {
				position.set(destination.x, destination.y);
			}
		}
	}
	
	public Game() {
		position = new Point(width / 2, height / 2);
	}
	
	public int getWidth() {
		return width;
	}
	
	public int getHeight() {
		return height;
	}
	
	public int getXPosition() { return position.x; }
	public int getYPosition() { return position.y; }
	public Point getPosition() { return position; }
	public int getWalkState() { return walkState; }
	public int getFacing() { return facing; }
	
	public int tileAt(Point p) {
		int col = p.x / TILE_SIZE;
		int row = p.y / TILE_SIZE;
		return (col + row) % 2;
	}
	
	public int getTileSize() {
		return TILE_SIZE;
	}
	
	public void setPosition(Point point) {
		position.set(point.x, point.y);
	}
	
	public void setDestination(Point point) {
		destination = point;
		origin = new Point(position.x, position.y);
		startTime = time;
	}
	
	private int directionIndex(int dx, int dy) {
		return DIRECTION_INDEX[IntegerMath.signum(dx)+1][IntegerMath.signum(dy)+1];
	}

	public void setSpeed(long speed) {
		this.speed = speed;
	}
}
