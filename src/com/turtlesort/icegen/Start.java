package com.turtlesort.icegen;

import java.io.File;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;

import com.turtlesort.icegen.IceMapSolver.Direction;
import com.turtlesort.icegen.visualizer.MapVisualizer;
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
		
		
		//IceMap map = new IceMapGenerator(15,15,20,20).generate();
		
		System.out.println(map.getName());
		TextVisualizer r = new TextVisualizer(map);
		r.render();

		IceMapSolver s = new IceMapSolver(map);
		int moveLimit = 25;
		
		LinkedList<Direction[]> solutions = s.solve(moveLimit);

		Collections.sort(solutions, new Comparator<Direction[]>(){
			@Override
			public int compare(Direction[] arg0, Direction[] arg1) {
				return arg0.length - arg1.length;
			}
		});
		
		if(solutions.size() > 0){
			
			for(int i = 0; i < 5 && i < solutions.size(); i++){
				
					Direction[] solution = solutions.get(i);
					System.out.print("(S)" + solution.length);
					
					for(Direction d : solution){
						System.out.print(" -> " + d.toString().charAt(0));
					}
					System.out.println();
					
			}
		}
		else{
			System.out.println("No solutions equal to or under " + moveLimit + " moves");
		}

		MapVisualizer visualizer = new MapVisualizer(map);
		visualizer.setVisible(true);
		/**/
	}


}
