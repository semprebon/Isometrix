package com.nimbusly.isometrix;

public class Point {
	public int x, y;
	
	public Point(int x, int y) {
		set(x, y);
	}

	public Point(Point p) {
		set(p.x, p.y);
	}
	
	public Point set(int x, int y) {
		this.x = x;
		this.y = y;
		return this;
	}
	
	public Point offset(int dx, int dy) {
		return set(x + dx, y + dy);
	}
	
	@Override
	public String toString() {
		return "Point(" + x + "," + y + ")";
	}
	
	@Override
	public boolean equals(Object other) {
		if (this == other) return true;
		if (!(other instanceof Point)) return false;
		Point otherPoint = (Point) other;
		return this.x == otherPoint.x && this.y == otherPoint.y;
	}
	
	@Override
	public int hashCode() {
		return x ^ y;
	}

	public double distanceTo(Point other) {
		int dx = x - other.x;
		int dy = y - other.y;
		return Math.sqrt(dx*dx + dy*dy);
	}
	
	
}
