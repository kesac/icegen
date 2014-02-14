package com.turtlesort.icegen;

import java.util.Iterator;
import java.util.LinkedList;

/**
 * <p>
 * A NavigationTree is used internally by IceMapSolver to describe sequences of player moves on an IceMap.
 * The root node represents the starting tile of the map. Each subsequent child node of the tree
 * represents a move in one of four directions: up, down, left, or right. Each node in the tree also
 * stores the map coordinates the player will arrive at when executing the move.
 * </p>
 * <p>
 * Properties of a NavigationTree to be aware of:
 * <ul>
	 * <li>
		 * The depth of any node represents the number of moves the player needs to make to reach
		 * that specific tile.
	 * </li>
	 * <li>
		 * IceMapSolver avoids sequences of player moves that contain cycles. Thus, any path from
		 * the root to any leaf describes a sequence that visits a unique set of map tiles. 
	 * </li>
 * </ul>
 *</p>
 */
public class NavigationTree {

	private NavigationNode root;
	
	/**
	 * Constructor.
	 * @param x - x-coordinate of the root node (should be x-coordinate of starting tile)
	 * @param y - y-coordinate of the root node (should be y-coordinate of starting tile)
	 */
	public NavigationTree(int x, int y){
		this.root = new NavigationNode();
		this.root.setDestinationCoordinates(x, y);
		this.root.setDirection(null);
	}
	
	/**
	 * @return The root node of this NavigationTree
	 */
	public NavigationNode getRoot(){
		return this.root;
	}
	
	/**
	 * @return A LinkedList of NavigationNode arrays. Each array is ordered and is a path from
	 * the root to a leaf in the tree whose destination is the ending tile of the map.
	 * 
	 * This makes each array effectively a set of moves that a player can start executing
	 * from the starting tile to arrive at the ending tile of an IceMap if index 0
	 * is skipped.
	 */
	public LinkedList<NavigationNode[]> getSolutions(){
		LinkedList<NavigationNode[]> solutions = new LinkedList<NavigationNode[]>();
		
		this.getSolutionRecursive(solutions, new LinkedList<NavigationNode>(), root);
		
		return solutions;
	}
	
	private void getSolutionRecursive(LinkedList<NavigationNode[]> solutions, LinkedList<NavigationNode> stack, NavigationNode node){
		
		if(node.getDirection() != null){
			stack.addLast(node);
		}
		
		if(node.totalChildren() == 0){
			if(node.isEnd()){
				NavigationNode[] solution = new NavigationNode[stack.size()];
				
				
				for(int i = 0; i < stack.size(); i++){
					solution[i] = stack.get(i);
				}
				solutions.add(solution);
			}
		}
		else{
			
			Iterator<NavigationNode> i = node.getChildren();
			
			while(i.hasNext()){
				this.getSolutionRecursive(solutions, stack, i.next());
			}
			
		}
		
		if(node.getDirection() != null){
			stack.removeLast();
		}
	}
	
}
