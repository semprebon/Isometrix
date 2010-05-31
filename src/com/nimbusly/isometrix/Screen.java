package com.nimbusly.isometrix;

public class Screen {

	private int viewWidth, viewHeight;
	private Point center;
	private int tileSize;
	private Tiler tiler;
	private Game game;
	
	public Screen(int width, int height) {
		resize(width, height);
	}
	
	public void resize(int width, int height) {
		viewWidth = width;
		viewHeight = height;
	}
	
	public void viewAt(Point point) {
		center = point;
	}
	
	public Point getViewAt() {
		return center;
	}
	
	public int getWidth() {
		return viewWidth;
	}
	
	public int getHeight() {
		return viewHeight;
	}
	
	public int getTileSize() {
		return tileSize;
	}
	
	// TODO: Tile size is hard-coded in model and image - ok?
	public void setTileSize(int tileSize) {
		this.tileSize = tileSize;
	}
	
	public void setTiler(Tiler tiler) {
		this.tiler = tiler;
	}
	
	public void setGame(Game game) {
		this.game = game;
		setTileSize(game.getTileSize());
	}
	
	/**
	 * Compute screen coordinates for point in game coordinates
	 * 
	 * @param point
	 * @return point converted to screen coordinates
	 */
	public Point toScreen(Point point) {
		int x = point.x - center.x;
		int y = point.y - center.y;
		point.set(viewWidth/2 + (x + y), viewHeight/2 + (y - x)/2);
		return point;
	}
	
	public Point toGame(Point point) {
		int x = point.x - viewWidth/2;
		int y = point.y - viewHeight/2;
		point.set(center.x + x/2 - y, center.y + x/2 + y);
		return point;
	}
	
	public interface Tiler {
		void drawTile(Point origin, int width, int height, int tileIndex);
	}
	
	public void drawTiles() {
		int min_y = IntegerMath.roundDown(toGame(new Point(0,0)).y, tileSize);
		int min_x = IntegerMath.roundDown(toGame(new Point(0, viewHeight)).x, tileSize);
		int max_y = IntegerMath.roundUp(toGame(new Point(viewWidth, viewHeight)).y, tileSize);
		int max_x = IntegerMath.roundUp(toGame(new Point(viewWidth, 0)).x, tileSize);
		Point origin = new Point(0, 0);
		Point p = new Point(0, 0);
		for (int x = min_x; x <= max_x; x += tileSize) {
			for (int y = min_y; y <= max_y; y += tileSize) {
				p.set(x, y);
				origin = toScreen(origin.set(x, y)).offset(0, -tileSize/2);
				tiler.drawTile(origin, tileSize*2, tileSize, game.tileAt(p));
			}
		}
	}
	
}
