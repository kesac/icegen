package com.turtlesort.icegen;

import java.util.LinkedList;
import com.turtlesort.icegen.IceMapSolver.Direction;

public class NavigationTree {

	public NavigationNode root;
	
	public NavigationTree(int x, int y){
		this.root = new NavigationNode();
		this.root.x = x;
		this.root.y = y;
		this.root.direction = null;
	}
	
	public LinkedList<Direction[]> getSolutions(){
		LinkedList<Direction[]> solutions = new LinkedList<Direction[]>();
		
		this.getSolutionRecursive(solutions, new LinkedList<Direction>(), root);
		
		return solutions;
	}
	
	private void getSolutionRecursive(LinkedList<Direction[]> solutions, LinkedList<Direction> stack, NavigationNode node){
		
		if(node.direction != null){
			stack.addLast(node.direction);
		}
		
		if(node.children.size() == 0){
			if(node.isEnd){
				Direction[] solution = new Direction[stack.size()];
				
				
				for(int i = 0; i < stack.size(); i++){
					solution[i] = stack.get(i);
				}
				solutions.add(solution);
			}
		}
		else{
			for(NavigationNode child : node.children){
				this.getSolutionRecursive(solutions, stack, child);	
			}
		}
		
		if(node.direction != null){
			stack.removeLast();
		}
	}
	
	
	public void printSolutions(){
		this.printSolutionRescursive("", root);
	}
	
	private void printSolutionRescursive(String prefix, NavigationNode node){
		if(node.children.size() == 0){
			if(node.isEnd){
				System.out.println(prefix + node.toString() + "[SOLVED]");	
			}
			else{
				//System.out.println(prefix + node.toString());
			}
			
		}
		else{
			for(NavigationNode child : node.children){
				printSolutionRescursive(prefix + node.toString(), child);	
			}
		}
	}
	
}
