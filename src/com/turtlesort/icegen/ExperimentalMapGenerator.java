package com.turtlesort.icegen;

import java.util.Random;

/**
 *
 */
public class ExperimentalMapGenerator implements IceMapGenerator {

	@Override
	public IceMap generate() {
		
		Random r = new Random();
		IceMap map = new IceMap(15,15);
		
		
		// Set up start and end, make sure it is solvable before any obstacles come into play
		map.setStartTile(1 + r.nextInt(map.getWidth() - 2), map.getHeight() - 1); // last row, but avoid corners
		map.setTileType(map.getStartX(), map.getStartY(), IceMap.Tile.FLOOR);
		
		map.setEndTile(1 + r.nextInt(map.getWidth() - 2), 0); // first row, but avoid corners
		map.setTileType(map.getEndX(), map.getEndY(), IceMap.Tile.FLOOR);
		
		
		
		
		return map;
	}

}
