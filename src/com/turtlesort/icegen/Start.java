package com.turtlesort.icegen;

import java.util.LinkedList;

import com.turtlesort.icegen.visualizer.SolutionVisualizer;
import com.turtlesort.icegen.visualizer.TextVisualizer;

public class Start {

	public static void main(String[] args){

		/*
		IceMap map = new IceMap(10,12);
		map.setStartTile(5, map.getHeight() - 1);
		map.setTileType(5, map.getHeight() - 1, IceMap.Tile.FLOOR);

		for(int i = 0; i < 3; i++){
			for(int j = 0; j < 3; j++){
				map.setTileType(i + map.getWidth()/2, j + map.getHeight()/2, IceMap.Tile.FLOOR);
			}
		}

		map.setEndTile(2, 0);
		map.setTileType(2, 0, IceMap.Tile.FLOOR);

		map.setTileType(0, 4, IceMap.Tile.SOLID);
		map.setTileType(map.getWidth()-1, 7, IceMap.Tile.SOLID);
		/**/

		String filePath = "maps/map1.json";
		IceMap map = IceMap.parseJSONFile(filePath);
		map.setName("maps/map1.json");
		
		// map = new IceMapGenerator(15,15,20,20).generate();
		// map.setName("Randomly Generated");
		
		System.out.println(map.getName());
		TextVisualizer r = new TextVisualizer(map);
		r.render();

		IceMapSolver s = new IceMapSolver(map);
		int moveLimit = 25;
		
		LinkedList<NavigationNode[]> solutions = s.solve(moveLimit);
		
		if(solutions.size() > 0){
			
			for(int i = 0; i < 5 && i < solutions.size(); i++){
				
					NavigationNode[] solution = solutions.get(i);
					System.out.print("(S)" + solution.length);
					
					for(NavigationNode d : solution){
						System.out.print(" -> " + d.direction.toString().charAt(0));
					}
					System.out.println();
					
			}
		}
		else{
			System.out.println("No solutions equal to or under " + moveLimit + " moves");
		}

		SolutionVisualizer visualizer = new SolutionVisualizer(map, solutions.get(0));
		visualizer.setVisible(true);
		/**/
	}


}
