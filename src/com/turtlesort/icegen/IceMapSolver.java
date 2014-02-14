package com.turtlesort.icegen;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;

import com.turtlesort.icegen.IceMap.Tile;

/**
 * Finds solutions for IceMaps. A valid solution is any sequence of moves that would lead a player from the
 * starting tile to the ending tile.
 */
public class IceMapSolver {

	/**
	 * The only valid directions a player can move on an IceMap.
	 */
	public static enum Direction {
		UP, DOWN, LEFT, RIGHT
	}
	
	private IceMap map;
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
	 * @param trackVisitedTiles If true, solutions that share intermediate tiles
	 * as other solutions are ignored.
	 * @return A linked list of solutions. Each solution is an array of moves represented
	 * as directions to take. The list is sorted in ascending order according to the number of moves per solution. The
	 * first solution (at index 0) always has the least moves. If there are no solutions then
	 * the linked list will be empty.
	 */
	public LinkedList<NavigationNode[]> solve(int moveLimit){
		
		this.visitedTiles = new HashSet<String>();
		
		NavigationTree tree = new NavigationTree(this.map.getStartX(), this.map.getStartY());
		
		this.findSolution(tree.getRoot(), 0,  moveLimit);
		
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
	private void findSolution(NavigationNode node, int depth, int limit){
		
		if(++depth > limit) return;
		
		for(NavigationNode child : this.findChildren(node.getDestinationX(), node.getDestinationY())){
			node.addChild(child);
		}
		
		String nodeString = node.getDestinationX() + "," + node.getDestinationY();
		this.visitedTiles.add(nodeString);
		
		Iterator<NavigationNode> i = node.getChildren();
		
		while(i.hasNext()){
			this.findSolution(i.next(), depth, limit);
		}
		
		this.visitedTiles.remove(nodeString);
	}
	
	/**
	 * Given a starting position, checks whether it is possible to move either up, down, left, or right
	 * to a new tile.
	 * @param x - The x-coordinate of the tile serving as the parent node
	 * @param y - The y-coordinate of the tile serving as the parent node
	 */
	private LinkedList<NavigationNode> findChildren(int x, int y){
		
		NavigationNode[] possibleNodes = new NavigationNode[Direction.values().length];
		
		int i = 0;
		for(Direction d : Direction.values()){
			possibleNodes[i++] = findChild(x, y, d);
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
	 * @param x - The x-coordinate of the tile serving as the parent node
	 * @param y - The y-coordinate of the tile serving as the parent node
	 * @param d - The direction to move in
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
			node.setDestinationCoordinates(newX, newY);
			node.setDirection(d);
			
			
			if(this.map.isEnd(newX, newY)){
				node.markAsEnd(true);
			}
			
			return node;
		}
		
		return null;
		
	}
	
}
