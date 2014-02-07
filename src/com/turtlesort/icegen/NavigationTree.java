package com.turtlesort.icegen;

import java.util.LinkedList;

public class NavigationTree {

	public NavigationNode root;
	
	public NavigationTree(int x, int y){
		this.root = new NavigationNode();
		this.root.x = x;
		this.root.y = y;
		this.root.direction = null;
	}
	
	public LinkedList<NavigationNode[]> getSolutions(){
		LinkedList<NavigationNode[]> solutions = new LinkedList<NavigationNode[]>();
		
		this.getSolutionRecursive(solutions, new LinkedList<NavigationNode>(), root);
		
		return solutions;
	}
	
	private void getSolutionRecursive(LinkedList<NavigationNode[]> solutions, LinkedList<NavigationNode> stack, NavigationNode node){
		
		if(node.direction != null){
			//stack.addLast(node.direction);
			stack.addLast(node);
		}
		
		if(node.children.size() == 0){
			if(node.isEnd){
				NavigationNode[] solution = new NavigationNode[stack.size()];
				
				
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
