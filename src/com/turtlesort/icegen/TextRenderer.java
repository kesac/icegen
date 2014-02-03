package com.turtlesort.icegen;

public class TextRenderer {

	private IceMap map;
	
	public TextRenderer(IceMap map){
		this.map = map;
	}
	
	public void render(){
		
		for(int y = 0; y < this.map.getHeight(); y++){
			for(int x = 0; x < this.map.getWidth(); x++){
				
				IceMap.Tile tile = this.map.getTileType(x, y);
				
				if(tile == IceMap.Tile.ICE){
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
