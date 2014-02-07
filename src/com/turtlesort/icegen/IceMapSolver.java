package com.turtlesort.icegen;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedList;

import com.turtlesort.icegen.IceMap.Tile;

/**
 * Finds solutions to for an ice map.
 */
public class IceMapSolver {

	private static final int DEFAULT_MOVE_LIMIT = 10;
	
	public static enum Direction {
		UP, DOWN, LEFT, RIGHT
	}
	
	private IceMap map;
	private boolean trackVisitedTiles;
	private HashSet<String> visitedTiles;
	
	/**
	 * Constructor.
	 * @param map The ice map to solve.
	 */
	public IceMapSolver(IceMap map){
		this.map = map;
		
	}
	
	/**
	 * Finds a set of solutions that will solve this IceMap. Each solution is a
	 * sequence of moves (up, down, left, right) that will lead from the starting
	 * tile to the end tile.
	 * @param moveLimit The maximum number of moves a solution should have.
	 * @return A linked list of solutions. Each solution is an array of moves represented
	 * as directions to take. The list is sorted in ascending order according to the number of moves per solution. The
	 * first solution (at index 0) always has the least moves. If there are no solutions then
	 * the linked list will be empty.
	 */
	public LinkedList<NavigationNode[]> solve(int moveLimit){
		return this.solve(moveLimit, false);
	}
	
	/**
	 * Finds a set of solutions that will solve this IceMap. Each solution is a
	 * sequence of moves (up, down, left, right) that will lead from the starting
	 * tile to the end tile.
	 * @param moveLimit The maximum number of moves a solution should have.
	 * @return A linked list of solutions. Each solution is an array of moves represented
	 * as directions to take. The list is sorted in ascending order according to the number of moves per solution. The
	 * first solution (at index 0) always has the least moves. If there are no solutions then
	 * the linked list will be empty.
	 */
	public LinkedList<NavigationNode[]> solve(){
		return this.solve(DEFAULT_MOVE_LIMIT, false);
	}
	
	/**
	 * Finds a set of solutions that will solve this IceMap. Each solution is a
	 * sequence of moves (up, down, left, right) that will lead from the starting
	 * tile to the end tile.
	 * @param moveLimit The maximum number of moves a solution should have.
	 * @param trackVisitedTiles If true, solutions that share intermediate tiles
	 * as other solutions are ignored.
	 * @return A linked list of solutions. Each solution is an array of moves represented
	 * as directions to take. The list is sorted in ascending order according to the number of moves per solution. The
	 * first solution (at index 0) always has the least moves. If there are no solutions then
	 * the linked list will be empty.
	 */
	public LinkedList<NavigationNode[]> solve(int moveLimit, boolean trackVisitedTiles){
		
		this.visitedTiles = new HashSet<String>();
		
		this.trackVisitedTiles = trackVisitedTiles;
		
		NavigationTree tree = new NavigationTree(this.map.getStartX(), this.map.getStartY());
		this.visitedTiles.add(tree.root.x + "," + tree.root.y);
		this.findSolution(tree.root, null, 0,  moveLimit);
		
		//tree.printSolutions();
		
		LinkedList<NavigationNode[]> solutions = tree.getSolutions();
		
		Collections.sort(solutions, new Comparator<NavigationNode[]>(){
			@Override
			public int compare(NavigationNode[] arg0, NavigationNode[] arg1) {
				return arg0.length - arg1.length;
			}
		});
		
		return solutions;
	}
	
	/**
	 * Depth first search.
	 */
	private void findSolution(NavigationNode node, Direction lastMove, int depth, int limit){
		
		if(++depth > limit) return;
		
		node.children = this.findChildren(node.x, node.y, lastMove);
		
		for(NavigationNode child : node.children){
			this.findSolution(child, child.direction, depth, limit);
		}
	}
	
	/**
	 * Given a starting position, checks whether it is possible to move either up, down, left, or right
	 * to a new tile.
	 */
	private LinkedList<NavigationNode> findChildren(int x, int y, Direction lastMove){
		
		NavigationNode[] possibleNodes = new NavigationNode[Direction.values().length];
		
		int i = 0;
		for(Direction d : Direction.values()){
			if(!this.isOppositeDirection(lastMove, d)){ // Solution optimization, prevents solutions with left/right or up/down consecutive pairs
				possibleNodes[i++] = findChild(x, y, d);
			}
		}
		
		LinkedList<NavigationNode> result = new LinkedList<NavigationNode>();
		for(int j = 0; j < possibleNodes.length; j++){
			if(possibleNodes[j] != null){
				result.add(possibleNodes[j]);
			}
		}
		
		return result;
		
	}
	
	/**
	 * Given a position on the map, checks whether it is possible to move in Direction <code>d</code> to
	 * a new tile. If the specified position is the the end tile or if a possible move results in
	 * a tile that has been traveled to before, null is returned.
	 */
	private NavigationNode findChild(int x, int y, Direction d){
		
		if(this.map.isEnd(x, y)) return null;
		
		int newX = x;
		int newY = y;
		
		if(d == Direction.UP){
			
			IceMap.Tile tile = this.map.getTileType(newX, newY - 1);
			
			while(tile != IceMap.Tile.SOLID){ // Floor or ice?
				newY--;
				if(tile == IceMap.Tile.FLOOR) break;
				tile = this.map.getTileType(newX, newY - 1);
			}
		}
		
		if(d == Direction.DOWN){
			Tile tile = this.map.getTileType(newX, newY + 1);
			
			while(tile != IceMap.Tile.SOLID){ 
				newY++;	
				if(tile == IceMap.Tile.FLOOR) break;
				tile = this.map.getTileType(newX, newY + 1);
			}
		}
		
		if(d == Direction.LEFT){
			Tile tile = this.map.getTileType(newX - 1, newY);
			
			while(tile != IceMap.Tile.SOLID){
				newX--;
				if(tile == IceMap.Tile.FLOOR)break;
				tile = this.map.getTileType(newX - 1, newY);
			}
		}

		if(d == Direction.RIGHT){
			Tile tile = this.map.getTileType(newX + 1, newY);
			
			while(tile != IceMap.Tile.SOLID){
				newX++;	
				if(tile == IceMap.Tile.FLOOR) break;
				tile = this.map.getTileType(newX + 1, newY);
			}
		}


		
		if((newX != x || newY != y) && !this.visitedTiles.contains(newX + "," + newY)){
		
			NavigationNode node = new NavigationNode();
			node.x = newX;
			node.y = newY;
			node.direction = d;
			
			
			if(this.map.isEnd(newX, newY)){
				node.isEnd = true;
			}
			
			// We skip adding the end node to visited tiles so we find multiple solutions
			else if(this.trackVisitedTiles){
				
				this.visitedTiles.add(newX + "," + newY);
			}
			
			
			return node;
		}
		
		return null;
		
	}
	
	private boolean isOppositeDirection(Direction d1, Direction d2){
		return (d1 == Direction.UP && d2 == Direction.DOWN)
				|| (d1 == Direction.DOWN && d2 == Direction.UP)
				|| (d1 == Direction.LEFT && d2 == Direction.RIGHT)
				|| (d1 == Direction.RIGHT && d2 == Direction.LEFT);
	}

	
}
