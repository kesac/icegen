package com.turtlesort.icegen.visualizer;

import com.turtlesort.icegen.IceMap;
import com.turtlesort.icegen.IceMap.Tile;

public class TextVisualizer {

	private IceMap map;
	
	public TextVisualizer(IceMap map){
		this.map = map;
	}
	
	public void render(){
		
		for(int y = 0; y < this.map.getHeight(); y++){
			for(int x = 0; x < this.map.getWidth(); x++){
				
				IceMap.Tile tile = this.map.getTileType(x, y);
				if(this.map.isStart(x, y)){
					System.out.print('S');
				}
				else if(this.map.isEnd(x, y)){
					System.out.print('E');
				}
				else if(tile == IceMap.Tile.ICE){
					System.out.print('~');
				}
				else if(tile == IceMap.Tile.FLOOR){
					System.out.print('#');
				}
				else if(tile == IceMap.Tile.SOLID){
					System.out.print('@');
				}
				else{
					System.out.print('?');
				}
				System.out.print(' ');
			}	
			System.out.println();
		}
		
	}
	
}
