package com.turtlesort.icegen;

/**
 * An instance of this class represents a grid of tiles. 
 */
public class IceMap {
	
	public static enum Tile {
		ICE, FLOOR, SOLID
	};
	
	private Tile[][] map;
	private int startX;
	private int startY;
	private int endX;
	private int endY;
	
	/**
	 * Creates a grid with the desired dimensions. By default, all tiles
	 * are ice tiles.
	 * @param width The desired width of the map
	 * @param height The desired height of the map
	 */
	public IceMap(int width, int height){
		this.map = new Tile[width][height];
		this.startX = 0;
		this.startY = 0;
		this.endX = 0;
		this.endY = 0;
		
		for(int i = 0; i < width; i++){
			for(int j = 0; j < height; j++){
				this.map[i][j] = Tile.ICE;
			}
		}
	}
		
	public void setStartTile(int x, int y){
		this.startX = x;
		this.startY = y;
	}
	
	public void setEndTile(int x, int y){
		this.endX = x;
		this.endY = y;
	}
	
	public void setTileType(int x, int y, Tile tile){
		this.map[x][y] = tile;
	}

	/**
	 * Note: Out of bounds locations are returned as a solid tile.
	 */
	public Tile getTileType(int x, int y){
		return isTile(x,y) ? this.map[x][y] : Tile.SOLID;
	}

	public int getStartX() {
		return this.startX;
	}

	public int getStartY() {
		return this.startY;
	}

	public int getEndX() {
		return this.endX;
	}

	public int getEndY() {
		return this.endY;
	}
	
	public boolean isStart(int x, int y){
		return x == this.getStartX() && y == this.getStartY();
	}
	
	public boolean isEnd(int x, int y){
		return x == this.getEndX() && y == this.getEndY();
	}
	
	public boolean isTile(int x, int y){
		return x >= 0 && x < this.getWidth() && y >= 0 && y < this.getHeight();
	}
	
	public int getWidth(){
		return this.map.length;
	}
	
	public int getHeight(){
		return this.map[0].length;
	}
	
}
