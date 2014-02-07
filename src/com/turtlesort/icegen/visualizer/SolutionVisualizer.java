package com.turtlesort.icegen.visualizer;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JFrame;
import javax.swing.JPanel;

import com.turtlesort.icegen.IceMap;
import com.turtlesort.icegen.NavigationNode;

/**
 * Still experimenting - this is a mess
 */
public class SolutionVisualizer extends JFrame{

	private static final BasicStroke BASIC_STROKE = new BasicStroke(10);
	private static final long serialVersionUID = 1L;
	private static final Color BACKGROUND_COLOR = new Color(200,200,200);

	private IceMap map;
	private NavigationNode[] bestSolution;
	
	private Timer timer;
	private TimerTask solutionIterator;
	private int solutionStep;
	
	private JPanel panel;
	private Container contentPane;
	private int tileWidth;
	private int tileHeight;

	public SolutionVisualizer(IceMap map, NavigationNode[] solution) {

		this.map = map;
		this.bestSolution = solution;
		this.setTitle("Map: " + map.getName());
		
		// Center the window
		this.setSize(50*map.getWidth(), 50*map.getHeight());
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
		this.setLocation(screen.width/2 - this.getWidth()/2, screen.height/2 - this.getHeight()/2);
		
		this.setVisible(true);
		
		@SuppressWarnings("serial")
		JPanel panel = new JPanel(){
			public void paintComponent(Graphics g){
				draw(g);
			}
		};
		
		this.add(panel);
		
		this.panel = new JPanel();
		this.contentPane = this.getContentPane();
		
		int canvasWidth = contentPane.getWidth();
		int canvasHeight = contentPane.getHeight();

		this.tileWidth = canvasWidth / map.getWidth();
		this.tileHeight = canvasHeight / map.getHeight();
		this.setVisible(false);
		
		// Setup a timer task that iterates through the moves of the solution
		this.timer = new Timer();
		this.solutionIterator = new TimerTask(){
			@Override
			public void run() {
				solutionStep++;
				repaint();
				if(solutionStep > bestSolution.length){
					this.cancel();
				}
				
			}
		};
		this.timer.schedule(this.solutionIterator, 0, 100);
		
		/**/

	}

	public void draw(Graphics g) {
		
		g.setColor(BACKGROUND_COLOR);
		g.fillRect(0, 0, this.getWidth(), this.getHeight());

		this.drawMap(g);
		this.drawSolution(g);

	}

	private void drawMap(Graphics g){

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
						tileToPixelX(x), 
						tileToPixelY(y),
						tileWidth, 
						tileHeight
						);		
			}
		}
	}


	
	private void drawSolution(Graphics g){
	
		Graphics2D g2d = (Graphics2D) g; 
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		
		for(int i = 0; i < solutionStep && i < bestSolution.length; i++){
			NavigationNode move = this.bestSolution[i];
			
			// Draw the line from start tile to first move
			if(i == 0){
				g.setColor(Color.GRAY);
                g2d.setStroke(BASIC_STROKE);
				g.drawLine(tileToPixelX(map.getStartX()) + tileWidth/2,
						tileToPixelY(map.getStartY()) + tileHeight/2,
						tileToPixelX(move.x) + tileWidth/2, 
						tileToPixelY(move.y) + tileHeight/2);
			}
			
			// Keep drawing lines between moves
			if(i != 0){
				NavigationNode previousMove = this.bestSolution[i-1];
				g.setColor(Color.GRAY);
                g2d.setStroke(BASIC_STROKE);
				g.drawLine(tileToPixelX(previousMove.x) + tileWidth/2,
						tileToPixelY(previousMove.y) + tileHeight/2,
						tileToPixelX(move.x) + tileWidth/2, 
						tileToPixelY(move.y) + tileHeight/2);
			}
			
			// Draw the "head" of the line
			if(i == solutionStep - 1){
				g.setColor(Color.DARK_GRAY);
				g.fillOval(tileToPixelX(move.x) + 3, tileToPixelY(move.y) + 3, tileWidth - 6, tileHeight - 6);
			}
			
		}
		
	}
	
	private int tileToPixelX(int tileX){
		return (-panel.getWidth()) + (tileX * tileWidth) + tileX;
	}
	
	private int tileToPixelY(int tileY){
		return (-panel.getHeight()) + (tileY * tileHeight)+ tileY;
	}
	
}
