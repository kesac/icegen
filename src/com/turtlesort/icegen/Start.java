package com.turtlesort.icegen;

import java.util.LinkedList;

import com.turtlesort.icegen.IceMapSolver.Direction;

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

		IceMap map = new IceMapGenerator(15,15,20,20).generate();
		
		TextRenderer r = new TextRenderer(map);
		r.render();

		IceMapSolver s = new IceMapSolver(map);

		LinkedList<Direction[]> solutions = s.solve(5);

		if(solutions.size() > 0){
			
			for(Direction[] solution : solutions){
				//if(solution.length > 7){
					System.out.print("(S)");
					for(Direction d : solution){
						System.out.print(" -> " + d.toString());
					}
					System.out.println();
				//}

			}
		}
		else{
			System.out.println("No solutions");
		}


	}


}
