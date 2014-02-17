package com.turtlesort.icegen.visualizer;


import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import com.turtlesort.icegen.IceMap;

@SuppressWarnings("serial")

/**
 * TODO: Does it make sense to handle TMX files and generators here?
 */
public class InteractiveVisualizer extends SolutionVisualizer{

	
	public InteractiveVisualizer(IceMap map) {
		super(map);
		
		final IceMap iceMap = map;
		canvas.addMouseListener(new MouseListener(){

			@Override
			public void mouseClicked(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void mouseEntered(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void mouseExited(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void mousePressed(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				if(e.getSource() == canvas){
					int tileX = pixelToTileX(e.getX());
					int tileY = pixelToTileY(e.getY());
					
					if(!iceMap.isTile(tileX, tileY)) return;
					
					IceMap.Tile tile = iceMap.getTileType(tileX, tileY); 
					
					int button = e.getButton();
					
					if((e.getModifiers() & MouseEvent.CTRL_MASK) == MouseEvent.CTRL_MASK && button == MouseEvent.BUTTON1){
						iceMap.setTileType(iceMap.getStartX(), iceMap.getStartY(), IceMap.Tile.ICE);
						iceMap.setStartTile(tileX, tileY);
						iceMap.setTileType(tileX, tileY, IceMap.Tile.FLOOR);
					}
					else if((e.getModifiers() & MouseEvent.CTRL_MASK) == MouseEvent.CTRL_MASK && button == MouseEvent.BUTTON3){
						iceMap.setTileType(iceMap.getStartX(), iceMap.getStartY(), IceMap.Tile.ICE);
						iceMap.setEndTile(tileX, tileY);
						iceMap.setTileType(tileX, tileY, IceMap.Tile.FLOOR);
					}
					else if(tile == IceMap.Tile.ICE && button == MouseEvent.BUTTON1){
						iceMap.setTileType(tileX, tileY, IceMap.Tile.SOLID);
					}
					else if(tile == IceMap.Tile.SOLID && button == MouseEvent.BUTTON1){
						iceMap.setTileType(tileX, tileY, IceMap.Tile.ICE);
					}
					else if(tile == IceMap.Tile.ICE && button == MouseEvent.BUTTON3){
						iceMap.setTileType(tileX, tileY, IceMap.Tile.FLOOR);
					}
					else if(tile == IceMap.Tile.FLOOR && button == MouseEvent.BUTTON3){
						iceMap.setTileType(tileX, tileY, IceMap.Tile.ICE);
					}
					
					
					repaint();
					
				}
			}
			
		});
		
	}


	private int pixelToTileX(int x){
		return x/tileWidth;
	}
	
	private int pixelToTileY(int y){
		return y/tileHeight;
	}

}
