package com.turtlesort.icegen.generators;

import java.util.Random;

import com.turtlesort.icegen.IceMap;
import com.turtlesort.icegen.IceMap.Tile;

/**
 * A very simple IceMap generator. Doesn't produce very elegant or difficult maps.
 */
public class BasicMapGenerator implements IceMapGenerator {

	private int minWidth;
	private int maxWidth;
	private int minHeight;
	private int maxHeight;
	
	public BasicMapGenerator(int minWidth, int minHeight, int maxWidth, int maxHeight){
		this.minWidth = minWidth;
		this.minHeight = minHeight;
		this.maxWidth = maxWidth;
		this.maxHeight = maxHeight;
	}
	
	public IceMap generate(){
		
		Random r = new Random();
		
		IceMap map = new IceMap(
				this.minWidth + r.nextInt(this.maxWidth - this.minWidth),
				this.minHeight + r.nextInt(this.maxHeight - this.minHeight)
		);
		
		// Place a starting tile in the first row
		map.setStartTile(r.nextInt(map.getWidth()), 0);
		map.setTileType(map.getStartX(), map.getStartY(), IceMap.Tile.FLOOR);
		
		// Place an ending tile in the last row
		map.setEndTile(r.nextInt(map.getWidth()), map.getHeight() - 1);
		map.setTileType(map.getEndX(), map.getEndY(), IceMap.Tile.FLOOR);
		
		// Random (non-ice) islands 
		int islands = 8 + r.nextInt(8);
		while(islands-- > 0){
			int x1 = r.nextInt(map.getWidth());
			int y1 = r.nextInt(map.getHeight());
			int x2 = x1 + r.nextInt(4);
			int y2 = y1 + r.nextInt(4);
			
			for(int i = x1; i < x2 ; i++){
				for(int j = y1; j < y2; j++){
					if(map.isTile(i, j)){
						map.setTileType(i, j, IceMap.Tile.FLOOR);
					}
				}
			}
			
		}
		
		// Random boulders
		
		int boulders = 35 + r.nextInt(15);
		while(boulders-- > 0){
			int x = r.nextInt(map.getWidth());
			int y = r.nextInt(map.getHeight());
			if(map.isTile(x, y) && !map.isStart(x, y) && !map.isEnd(x, y)){
				map.setTileType(x, y, IceMap.Tile.SOLID);
			}
		}
		
		return map;
	}
	
}
