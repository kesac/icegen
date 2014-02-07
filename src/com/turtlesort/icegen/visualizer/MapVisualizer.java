package com.turtlesort.icegen.visualizer;

import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Toolkit;

import javax.swing.JFrame;

import com.turtlesort.icegen.IceMap;

public class MapVisualizer extends JFrame {

	private static final long serialVersionUID = 1L;
	private static final Color BACKGROUND_COLOR = new Color(200,200,200);
	
	private IceMap map;

	public MapVisualizer(IceMap map) {

		this.map = map;
		this.setTitle("Map: " + map.getName());
		
	    this.setSize(600, 600);
	    this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	    
	    Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
	    this.setLocation(screen.width/2 - this.getWidth()/2, screen.height/2 - this.getHeight()/2);
	    
	}
	
	public void paint(Graphics g) {

		g.setColor(BACKGROUND_COLOR);
		g.fillRect(0, 0, this.getWidth(), this.getHeight());
		
		Container contentPane = this.getContentPane();
		
		int canvasWidth = contentPane.getWidth();
		int canvasHeight = contentPane.getHeight();
		
		int tileWidth = canvasWidth / map.getWidth();
		int tileHeight = canvasHeight / map.getHeight();
		
		for(int x = 0; x < map.getWidth(); x++){
			for(int y = 0; y < map.getHeight(); y++){
				
				IceMap.Tile tile = map.getTileType(x, y);
				
				g.setColor(Color.BLACK);
				
				if(map.isStart(x, y)){
					g.setColor(Color.GREEN);	
				}
				else if(map.isEnd(x, y)){
					g.setColor(Color.RED);	
				}
				else if(tile == IceMap.Tile.FLOOR){
					g.setColor(Color.LIGHT_GRAY);	
				}
				else if(tile == IceMap.Tile.ICE){
					g.setColor(Color.WHITE);	
				}
				
			    g.fillRect(
			    		(this.getWidth()-contentPane.getWidth())/2 + (x * tileWidth) + x, 
			    		(this.getHeight()-contentPane.getHeight())/2 + (y * tileHeight)+ y,
			    		tileWidth, 
			    		tileHeight
			    );		
			}
		}
		
	    
	}
	
	
	
}
