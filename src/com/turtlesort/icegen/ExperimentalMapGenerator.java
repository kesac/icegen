package com.turtlesort.icegen;

import java.util.LinkedList;
import java.util.Random;

/**
 * Exploring ways to create challenging IceMaps.
 */
public class ExperimentalMapGenerator implements IceMapGenerator {

	@Override
	public IceMap generate() {
		
		Random r = new Random();
		IceMap map = new IceMap(15,15);
		
		for(int i = 0; i < map.getWidth(); i++){
			map.setTileType(i, 0, IceMap.Tile.SOLID);
			map.setTileType(i, map.getHeight() - 1, IceMap.Tile.SOLID);
		}
		
		for(int i = 0; i < map.getHeight(); i++){
			map.setTileType(0, i, IceMap.Tile.SOLID);
			map.setTileType(map.getWidth() - 1, i, IceMap.Tile.SOLID);
		}
		
		
		// Set up start and end, make sure it is solvable before any obstacles come into play
		map.setStartTile(1 + r.nextInt(map.getWidth() - 2), map.getHeight() - 1); // last row, but avoid corners
		map.setTileType(map.getStartX(), map.getStartY(), IceMap.Tile.FLOOR);
		map.setTileType(map.getStartX(), map.getStartY() - 1, IceMap.Tile.FLOOR);
		
		map.setEndTile(1 + r.nextInt(map.getWidth() - 2), 0); // first row, but avoid corners
		map.setTileType(map.getEndX(), map.getEndY(), IceMap.Tile.FLOOR);
		map.setTileType(map.getEndX(), map.getEndY() + 1, IceMap.Tile.FLOOR);
		
		IceMapSolver solver = new IceMapSolver(map);
		LinkedList<NavigationNode[]> solutions = solver.solve(10);
		int attempts = 0; 
		
		while(solutions.size() > 1 && attempts < 100){

			// Deal with the first solution only
			if(solutions.size() > 0){
				NavigationNode[] solution = solutions.get(0);
				for(int i = 0; i < solution.length - 1; i++){
					NavigationNode a = solution[i];
					NavigationNode b = solution[i+1];

					if(this.distance(a, b) > 5){

						int x = (a.getDestinationX() + b.getDestinationX())/2;
						int y = (a.getDestinationY() + b.getDestinationY())/2;

						if(x == a.getDestinationX()){
							x += r.nextInt(3) - 1;
						}
						else{
							y += r.nextInt(3) - 1;
						}
						
						map.setTileType(x, y, IceMap.Tile.SOLID);
						

					}

				}
			}
			solutions = solver.solve(10);
			attempts++;
		}
		
		
		return map;
	}

	private int distance(NavigationNode a, NavigationNode b){
		return Math.abs((a.getDestinationX() - b.getDestinationX()) + (a.getDestinationY() - b.getDestinationY()));
	}
	
}
