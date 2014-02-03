package com.turtlesort.icegen;

import java.util.LinkedList;

import com.turtlesort.icegen.IceMapSolver.Direction;

public class NavigationNode {

	public LinkedList<NavigationNode> children;
	public int x;
	public int y;
	public Direction direction;
	public boolean isEnd;
	
	public NavigationNode(){
		this.children = new LinkedList<NavigationNode>();
	}
	
	public String toString(){
		
		if(direction == null)
			return "(" + x + "," + y + ")";
		
		else{
			return " -> (" + direction.toString().charAt(0) + ":" + x + "," + y + ")";
		}
	}

	/*
	public boolean equals(Object other){
		if(other instanceof NavigationNode){
			NavigationNode node = (NavigationNode) other;
			return x == node.x && y == node.y && direction == node.direction;
		}
		else{
			return false;
		}
	}/**/

	
}
