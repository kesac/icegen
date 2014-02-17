package com.turtlesort.icegen;

import java.util.Iterator;
import java.util.LinkedList;

import com.turtlesort.icegen.IceMapSolver.Direction;

/**
 * Represents a node in a {@link NavigationTree}.
 */
public class NavigationNode {

	private int x;
	private int y;
	private Direction direction;
	private boolean isEnd;
	private LinkedList<NavigationNode> children;
	
	/**
	 * Constructor.
	 */
	public NavigationNode(){
		this.children = new LinkedList<NavigationNode>();
	}
	
	/**
	 * @return An iterator for this node's child nodes.
	 */
	public Iterator<NavigationNode> getChildren() {
		return children.iterator();
	}

	public void addChild(NavigationNode child){
		this.children.add(child);
	}
	
	public int totalChildren(){
		return this.children.size();
	}
	
	/**
	 * Sets the coordinates of the tile the player will arrive at
	 * if they move in this node's direction.
	 * @param x - x-coordinate of this node's destination tile
	 * @param y - y-coordinate of this node's destination tile
	 */
	public void setDestinationCoordinates(int x, int y){
		this.x = x;
		this.y = y;
	}
	
	/**
	 * @return x-coordinate of this node's destination tile
	 */
	public int getDestinationX() {
		return x;
	}

	/**
	 * @return y-coordinate of this node's destination tile
	 */
	public int getDestinationY() {
		return y;
	}

	/**
	 * @return The direction this node represents (a value from the enum IceMapSolver.Direction)
	 */
	public Direction getDirection() {
		return direction;
	}

	/**
	 * @param d - The direction this node represents (a value from the enum IceMapSolver.Direction)
	 */
	public void setDirection(Direction d) {
		this.direction = d;
	}

	/**
	 * @return True if this node represents a move to a map's ending tile, else false
	 */
	public boolean isEnd() {
		return isEnd;
	}

	/**
	 * @param isEnd - Specify true if this node represents a move to a map's ending tile. By default this is false.
	 */
	public void markAsEnd(boolean isEnd) {
		this.isEnd = isEnd;
	}
	
	public String toString(){
		
		if(direction == null)
			return "(" + x + "," + y + ")";
		
		else{
			return " -> (" + direction.toString().charAt(0) + ":" + x + "," + y + ")";
		}
	}

	public boolean equals(Object o){
		if(o instanceof NavigationNode){
			NavigationNode node = (NavigationNode)o;
			return node.x == this.x && node.y == this.y && node.direction == this.direction;
			
		}
		return false;
	}
	
}
