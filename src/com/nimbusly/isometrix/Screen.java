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
	}
	
	/**
	 * Compute screen coordinates for point in game coordinates
	 * 
	 * @param point
	 * @return point converted to screen coordinates
	 */
	public Point toScreen(Point point) {
		int x = (point.x - center.x) * tileSize / game.getTileSize();
		int y = (point.y - center.y) * tileSize / game.getTileSize();
		point.set(viewWidth/2 + (x + y), viewHeight/2 + (y - x)/2);
		return point;
	}
	
	public Point toGame(Point point) {
		int x = (point.x - viewWidth/2) * game.getTileSize() / tileSize;
		int y = (point.y - viewHeight/2) * game.getTileSize() / tileSize;
		point.set(center.x + x/2 - y, center.y + x/2 + y);
		return point;
	}
	
	public Point screenOffset() {
		int x = center.x * tileSize / game.getTileSize();
		int y = center.y * tileSize / game.getTileSize();
		return new Point((x + y), (y - x)/2);
	}

	public interface Tiler {
		void drawTile(Point origin, int width, int height, int tileIndex);
	}
	
	/**
	 * Draw the tiles for a given section of screen image
	 */
	public void drawTiles(int x0, int y0, int x1, int y1) {
		int gameTileSize = game.getTileSize();
		int min_y = IntegerMath.roundDown(toGame(new Point(x0, y0)).y, gameTileSize);
		int min_x = IntegerMath.roundDown(toGame(new Point(x0, y1)).x, gameTileSize);
		int max_y = IntegerMath.roundUp(toGame(new Point(x1, y1)).y, gameTileSize);
		int max_x = IntegerMath.roundUp(toGame(new Point(x1, y0)).x, gameTileSize);
		Point origin = new Point();
		Point p = new Point();
		for (int x = min_x; x <= max_x; x += gameTileSize) {
			for (int y = min_y; y <= max_y; y += gameTileSize) {
				p.set(x, y);
				origin = toScreen(origin.set(x, y)).offset(0, -tileSize/2);
				tiler.drawTile(origin, tileSize*2, tileSize, game.tileAt(p));
			}
		}
	}
	
}
