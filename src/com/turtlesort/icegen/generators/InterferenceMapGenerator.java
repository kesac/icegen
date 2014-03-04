package com.turtlesort.icegen.generators;

import java.util.LinkedList;
import java.util.Random;

import com.turtlesort.icegen.IceMap;
import com.turtlesort.icegen.IceMapSolver;
import com.turtlesort.icegen.NavigationNode;

/**
 * Creates a trivially solvable IceMap then finds the optimal solution.
 * Interferes with the optimal solution by placing a solid tile in the way
 * of the solution's path. Checks to see if the map is still
 * solvable and if it is, repeats the interference step.  
 */
public class InterferenceMapGenerator implements IceMapGenerator {

	@Override
	public IceMap generate() {
		
		Random r = new Random();
		IceMap map = new IceMap(15,15);
		
		// The edge tiles of the map are all solid
		for(int i = 0; i < map.getWidth(); i++){
			map.setTileType(i, 0, IceMap.Tile.SOLID);
			map.setTileType(i, map.getHeight() - 1, IceMap.Tile.SOLID);
		}
		
		for(int i = 0; i < map.getHeight(); i++){
			map.setTileType(0, i, IceMap.Tile.SOLID);
			map.setTileType(map.getWidth() - 1, i, IceMap.Tile.SOLID);
		}
		
		
		// The start tile is placed at the bottom of the map
		map.setStartTile(1 + r.nextInt(map.getWidth() - 2), map.getHeight() - 1); // last row, but avoid corners
		map.setTileType(map.getStartX(), map.getStartY(), IceMap.Tile.FLOOR);
		map.setTileType(map.getStartX(), map.getStartY() - 1, IceMap.Tile.FLOOR);
		
		// The end tile is placed at the top of the map
		map.setEndTile(1 + r.nextInt(map.getWidth() - 2), 0); // first row, but avoid corners
		map.setTileType(map.getEndX(), map.getEndY(), IceMap.Tile.FLOOR);
		map.setTileType(map.getEndX(), map.getEndY() + 1, IceMap.Tile.FLOOR);
		
		IceMapSolver solver = new IceMapSolver(map);
		LinkedList<NavigationNode[]> solutions = solver.solve(10);
		int attempts = 0; 
		
		int lastX = -1;
		int lastY = -1;
		IceMap.Tile lastTile = null;
		
		while(solutions.size() > 1 && attempts < 1000){

			// Deal with the first solution only
			if(solutions.size() > 0){
				
				NavigationNode[] solution = solutions.get(0);
				
				// Find a move in the optimal solution to interfere with
				for(int i = 0; i < solution.length - 1; i++){
					NavigationNode a = solution[i];
					NavigationNode b = solution[i+1];

					// For the the current move check whether there is at least 5 tiles from the source
					// to the destination
					if(this.distance(a, b) > 5){

						int x = (a.getDestinationX() + b.getDestinationX())/2;
						int y = (a.getDestinationY() + b.getDestinationY())/2;

						/**/
						if(x == a.getDestinationX()){
							x += r.nextInt(3) - 1;
						}
						else{
							y += r.nextInt(3) - 1;
						}
						/**/	
						
						lastX = x;
						lastY = y;
						lastTile = map.getTileType(x, y);
						
						map.setTileType(x, y, IceMap.Tile.SOLID);
					}

				}
			}
			solutions = solver.solve(10);
			attempts++;
		}
		
		if(solutions.isEmpty() && lastTile != null){
			map.setTileType(lastX, lastY, lastTile);
		}
		
		return map;
	}

	private int distance(NavigationNode a, NavigationNode b){
		return Math.abs((a.getDestinationX() - b.getDestinationX()) + (a.getDestinationY() - b.getDestinationY()));
	}
	
}
