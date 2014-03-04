package com.turtlesort.icegen.generators;

import java.util.LinkedList;
import java.util.Random;

import com.turtlesort.icegen.IceMap;
import com.turtlesort.icegen.IceMapSolver;
import com.turtlesort.icegen.NavigationNode;

/**
 * Combines techniques used in the BasicMapGenerator and InterferenceMapGenerator.
 */
public class ExperimentalMapGenerator implements IceMapGenerator{

	private static final int MOVE_LIMIT = 15;
	private static final boolean PRUNE_SOLUTION_SET = false;
	
	private Random random = new Random();
	
	@Override
	public IceMap generate() {
		
		IceMap map = new IceMap(12 + random.nextInt(5), 12 + random.nextInt(5));
		
		initialize(map);
		addRandomBoulders(map);
		addInterference(map);
		
		return map;
	}

	/*
	 * Adds the starting and end tiles. Adds the solid tile border around the map.
	 */
	private void initialize(IceMap map) {
		
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
		map.setStartTile(1 + random.nextInt(map.getWidth() - 2), map.getHeight() - 1); // last row, but avoid corners
		map.setTileType(map.getStartX(), map.getStartY() - 1, IceMap.Tile.FLOOR);
		
		// The end tile is placed at the top of the map
		map.setEndTile(1 + random.nextInt(map.getWidth() - 2), 0); // first row, but avoid corners
		map.setTileType(map.getEndX(), map.getEndY() + 1, IceMap.Tile.FLOOR);
		
	}

	/*
	 * Adds solid tiles to the ice map. Ensures that these solid tiles are not adjacent.
	 */
	private void addRandomBoulders(IceMap map) {
		int attempts = 0;
		int maximumAttempts = 500;
		
		int randomBoulders = (map.getWidth() * map.getHeight()) / 15;
		
		while(randomBoulders > 0 && attempts < maximumAttempts){
			
			int x = random.nextInt(map.getWidth());
			int y = random.nextInt(map.getHeight());
			
			if(map.getTileType(x, y) != IceMap.Tile.SOLID && !map.isStart(x, y) && !map.isEnd(x, y)){
				
				boolean noAdjacentBoulders = true;
				for(int i = x - 1; i <= x + 1; i++){
					for(int j = y - 1; j <= y + 1; j++){
						if(map.getTileType(i, j) != IceMap.Tile.ICE){
							noAdjacentBoulders = false;
						}
					}	
				}
				
				if(noAdjacentBoulders){
					map.setTileType(x, y, IceMap.Tile.SOLID);
					randomBoulders--;
				}
				
			}
			
			attempts++;
		}
	}

	/*
	 * Solves the map. Takes the optimal solution and finds the move with the greatest
	 * distance. Places a solid tile on the map that intereferes with that specific move.
	 * Re-solve the map and repeat.
	 */
	private void addInterference(IceMap map) {
		
		IceMapSolver solver = new IceMapSolver(map);
		LinkedList<NavigationNode[]> solutions = solver.solve(MOVE_LIMIT, PRUNE_SOLUTION_SET);
		int attempts = 0; 
		
		int lastX = -1;
		int lastY = -1;
		IceMap.Tile lastTile = null;
		
		while(solutions.size() >= 1 && attempts < 1000){

			// Deal with the first solution only
			if(solutions.size() > 0){
				
				NavigationNode[] solution = solutions.get(0);
				
				int maxDistance = 0;
				int selectedMoveIndex = 0;
				
				// Find a move in the optimal solution to interfere with
				for(int i = 0; i < solution.length - 1; i++){
					NavigationNode a = solution[i];
					NavigationNode b = solution[i+1];

					// For the move with that achieves the most distance
					if(this.distance(a, b) > maxDistance){
						maxDistance = this.distance(a, b);
						selectedMoveIndex = i;
					}
				}
				
				if(maxDistance > 1){ // The move must achieve a distance of at least 3 tiles
					
					System.out.println("Interfering with a move of distance " + maxDistance);
					
					NavigationNode a = solution[selectedMoveIndex];
					NavigationNode b = solution[selectedMoveIndex+1];
					
					int x = (a.getDestinationX() + b.getDestinationX())/2;
					int y = (a.getDestinationY() + b.getDestinationY())/2;

					/**/
					if(x == a.getDestinationX()){
						x += random.nextInt(3) - 1;
					}
					else{
						y += random.nextInt(3) - 1;
					}
			
					lastX = x;
					lastY = y;
					lastTile = map.getTileType(x, y);
					
					map.setTileType(x, y, IceMap.Tile.SOLID);
				}
				
			}
			solutions = solver.solve(MOVE_LIMIT, PRUNE_SOLUTION_SET);
			
			if(solutions.isEmpty() && lastTile != null){
				map.setTileType(lastX, lastY, lastTile);
				solutions = solver.solve(MOVE_LIMIT, PRUNE_SOLUTION_SET);
				System.out.println("Reversed last interference");
			}
			
			attempts++;
		}


		
		System.out.println("Attempts: " + attempts);
		
	}

	
	private int distance(NavigationNode a, NavigationNode b){
		return Math.abs((a.getDestinationX() - b.getDestinationX()) + (a.getDestinationY() - b.getDestinationY()));
	}

}
