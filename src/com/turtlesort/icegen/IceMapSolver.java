package com.turtlesort.icegen;

import java.util.HashSet;
import java.util.LinkedList;

public class IceMapSolver {

	private static final int DEFAULT_MOVE_LIMIT = 10;
	public static enum Direction {
		UP, DOWN, LEFT, RIGHT
	}
	
	private IceMap map;
	private boolean trackVisitedTiles;
	private HashSet<String> visitedTiles;
	
	
	public IceMapSolver(IceMap map){
		this.map = map;
		
	}
	
	/**
	 * Finds a set of solutions that will solve this IceMap. Each solution is a
	 * sequence of moves (up, down, left, right) that will lead from the starting
	 * tile to the end tile.
	 * @param moveLimit The maximum number of moves a solution should have.
	 * @return A linked list of solutions. Each solution is an array of moves represented
	 * as directions to take.
	 */
	public LinkedList<Direction[]> solve(int moveLimit){
		return this.solve(moveLimit, false);
	}
	
	/**
	 * Finds a set of solutions that will solve this IceMap. Each solution is a
	 * sequence of moves (up, down, left, right) that will lead from the starting
	 * tile to the end tile.
	 * @param moveLimit The maximum number of moves a solution should have.
	 * @return A linked list of solutions. Each solution is an array of moves represented
	 * as directions to take.
	 */
	public LinkedList<Direction[]> solve(){
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
	 * as directions to take.
	 */
	public LinkedList<Direction[]> solve(int moveLimit, boolean trackVisitedTiles){
		
		this.visitedTiles = new HashSet<String>();
		
		this.trackVisitedTiles = trackVisitedTiles;
		
		NavigationTree tree = new NavigationTree(this.map.getStartX(), this.map.getStartY());
		this.visitedTiles.add(tree.root.x + "," + tree.root.y);
		this.findSolution(tree.root, null, 0,  moveLimit);
		
		//tree.printSolutions();
		return tree.getSolutions();
	}
	
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
	 * 
	 * @param x - Starting X location
	 * @param y - Starting Y location
	 * @return A LinkedList of NavigationNodes for each direction that resulted in moving to a
	 * new tile. If there was no direction that resulted in a new destination, the LinkedList will
	 * be empty.
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
	 * 
	 * @param x - Starting X location
	 * @param y - Starting Y location
	 * @param d - Direction to move in
	 * @return If it is possible to move to a new tile, this method returns a NavigationNode
	 * containing information of the move. Otherwise, null is returned.
	 */
	private NavigationNode findChild(int x, int y, Direction d){
		
		if(this.map.isEnd(x, y)) return null;
		
		int newX = x;
		int newY = y;
		
		if(d == Direction.UP && this.map.isTile(newX, newY - 1)){
			while(this.map.getTileType(newX, newY - 1) != IceMap.Tile.SOLID){ // Floor or ice?
				newY--;	
				if(this.map.getTileType(newX, newY - 1) == IceMap.Tile.FLOOR){
					newY--;	break;
				}
			}
		}
		
		if(d == Direction.DOWN && this.map.isTile(newX, newY + 1)){
			while(this.map.getTileType(newX, newY + 1) != IceMap.Tile.SOLID){ 
				newY++;	
				if(this.map.getTileType(newX, newY + 1) == IceMap.Tile.FLOOR){
					newY++;	break;
				}
			}
		}
		
		if(d == Direction.LEFT && this.map.isTile(newX - 1, newY)){
			while(this.map.getTileType(newX - 1, newY) != IceMap.Tile.SOLID){
				newX--;	
				if(this.map.getTileType(newX - 1, newY) == IceMap.Tile.FLOOR){
					newX--;break;
				}
			}
		}

		if(d == Direction.RIGHT && this.map.isTile(newX + 1, newY)){
			while(this.map.getTileType(newX + 1, newY) != IceMap.Tile.SOLID){
				newX++;	
				if(this.map.getTileType(newX + 1, newY) == IceMap.Tile.FLOOR){
					newX++;	break;
				}
			}
		}


		
		if((newX != x || newY != y) && !this.visitedTiles.contains(newX + "," + newY) /*newX != x || newY != y*/){
		
			NavigationNode node = new NavigationNode();
			node.x = newX;
			node.y = newY;
			node.direction = d;
			
			
			if(this.map.isEnd(newX, newY)){
				node.isEnd = true;
			}
			else if(this.trackVisitedTiles){
				this.visitedTiles.add(newX + "," + newY); // We skip adding the end node to visited tiles so we find multiple solutions
			}
			
			//System.out.println("("+x+","+y+")" + "-> " + d.toString() + " -> (" + node.x + "," + node.y + ")");
			
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
