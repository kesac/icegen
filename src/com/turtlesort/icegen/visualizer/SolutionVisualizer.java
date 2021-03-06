package com.turtlesort.icegen.visualizer;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;
import java.io.File;
import java.util.LinkedList;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JFrame;
import javax.swing.JPanel;

import com.turtlesort.icegen.IceMap;
import com.turtlesort.icegen.IceMapSolver;
import com.turtlesort.icegen.NavigationNode;
import com.turtlesort.icegen.generators.IceMapGenerator;

/**
 * Draws an IceMap in a JFrame, finds a solution to the map with the least amount of moves, then
 * animates the sequence of moves in the solution.
 */
@SuppressWarnings("serial")
public class SolutionVisualizer extends JFrame {

	
	private static final int REPAINT_DELAY = 100; // Milliseconds; the smaller the number the faster the solution gets painted
	private static final BasicStroke SOLUTION_LINE = new BasicStroke(10);
	private static final Color BACKGROUND_COLOR = new Color(200,200,200);
	private static final Color GLASS_COLOR = new Color(0, 0, 0, 125);
	private static final Font MESSAGE_FONT = new Font("Arial", Font.PLAIN, 40);
	private static final Font INFO_FONT = new Font("Arial", Font.PLAIN, 18);
	private static final String RELOAD_MESSAGE = "Reloading map and resolving...";
	private static final String UNSOLVABLE_MESSAGE = "No solution exists!";
	
	private static final int MOVE_LIMIT = 20;
	private static final boolean PRUNE_SOLUTION_SET = true;
	
	protected IceMap map;
	protected JPanel canvas;
	protected int tileWidth;
	protected int tileHeight;
	
	private File sourceFile;
	private long sourceLastModified;
	private IceMapGenerator generator;

	protected int displayedSolution;
	protected LinkedList<NavigationNode[]> allSolutions;
	
	private Timer timer;
	private TimerTask solutionIterator;
	private int solutionStep;
	
	private Container contentPane;
	private boolean isReloadingMap;

	/**
	 * @param generator - An IceMapGenerator that will create IceMaps to display.
	 */
	public SolutionVisualizer(IceMapGenerator generator){
		this(generator.generate());
		this.generator = generator;
	}
	
	/**
	 * @param file - A Tiled JSON file representing the IceMap to solve and display
	 */
	public SolutionVisualizer(File file){
		this(IceMap.parseTMXFile(file));
		this.sourceFile = file;
		this.sourceLastModified = file.lastModified();
	}
	
	/**
	 * @param map - The IceMap to solve and display
	 */
	public SolutionVisualizer(IceMap map) {

		this.map = map;
		
		// Get a solution to the given IceMap
		IceMapSolver solver = new IceMapSolver(map);
		LinkedList<NavigationNode[]> solutions = solver.solve(MOVE_LIMIT, PRUNE_SOLUTION_SET);
		
		if(solutions.size() > 0){
			this.displayedSolution = 0;
		}
		
		this.allSolutions = solutions;

		this.initWindow();
		this.restartRepaintTimer();
		
		this.addKeyListener(new KeyAdapter(){
			@Override
			public void keyReleased(KeyEvent e) {
				if(KeyEvent.VK_R == e.getKeyCode()){
					reloadMap();
				}
				else if(e.getKeyChar() == '='){
					displayedSolution++;
					if(displayedSolution + 1 >= allSolutions.size()){
						displayedSolution = 0;
					}
					restartRepaintTimer();
				}
				else if(e.getKeyChar() == '-'){
					displayedSolution--;
					if(displayedSolution - 1 < 0){
						displayedSolution = allSolutions.size() - 1;
					}
					restartRepaintTimer();
				}
			}
		});
		
		this.addWindowFocusListener(new WindowFocusListener(){

			@Override
			public void windowGainedFocus(WindowEvent arg0) {
				if(sourceFile != null && sourceFile.lastModified() > sourceLastModified){
					reloadMap();
				}
			}

			@Override
			public void windowLostFocus(WindowEvent arg0) {}
			
		});
	}
	

	protected void reloadMap(){
		
		if(!this.isReloadingMap){
			this.isReloadingMap = true;
			this.repaint();
			this.setTitle("Reloading file...");	
			
			// We reload in another thread so our file loading and map solving
			// doesn't block the reloading message painted on the screen.
			this.timer.schedule(new TimerTask(){

				@Override
				public void run() {
					
					IceMap oldMap = map;
					
					// If we read from a file in the first place and if that file's
					// last modified time stamp changed, reread the file again
					if(sourceFile != null && sourceFile.lastModified() > sourceLastModified){
						// Read the file again
						map = IceMap.parseTMXFile(sourceFile);
						sourceLastModified = sourceFile.lastModified();
					}
					else if(generator != null){
						map = generator.generate();
					}

					if(oldMap != map || (sourceFile == null && generator == null)){
						
						// Resolve the IceMap
						IceMapSolver solver = new IceMapSolver(map);
						LinkedList<NavigationNode[]> solutions = solver.solve(MOVE_LIMIT, PRUNE_SOLUTION_SET);

						if(solutions.size() > 0){
							displayedSolution = 0;
							allSolutions = solutions;
						}
						else{
							displayedSolution = -1;
							allSolutions = null;
						}

						// Reset the dimensions of the window, don't center because user might of moved it somewhere else
						setSize(50*map.getWidth(), 50*map.getHeight());
					}
					
					// Restart the repaint timer
					restartRepaintTimer();
					
					setTitle("Map: " + map.getName() + " (Press R to reload file)");
					isReloadingMap = false;
				}
				
			}, 0);
			
		}
	}
	
	private void initWindow(){
		
		// Build the window we will display the solution
		this.setTitle("Map: " + map.getName() + " (Press R to reload file)");
		this.setSize(50*map.getWidth(), 50*map.getHeight());
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		// Center the window
		Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
		this.setLocation(screen.width/2 - this.getWidth()/2, screen.height/2 - this.getHeight()/2);
		
		
		// We briefly set the window visible so we can properly extract dimensions
		this.setVisible(true);
		
		this.canvas = new JPanel(){
			public void paintComponent(Graphics g){
				super.paintComponent(g);
				draw(g);
			}
		};
		
		this.add(this.canvas);

		this.contentPane = this.getContentPane();		
		int canvasWidth = contentPane.getWidth();
		int canvasHeight = contentPane.getHeight();

		this.tileWidth = canvasWidth / map.getWidth();
		this.tileHeight = canvasHeight / map.getHeight();
		this.setVisible(false);
	}
	
	protected void restartRepaintTimer(){

		// Setup a timer task that iterates through the moves of the solution
		
		if(this.timer == null){
			this.timer = new Timer();
		}
		
		if(this.solutionIterator != null){
			this.solutionIterator.cancel();
		}
		
		this.solutionStep = 0;
		this.solutionIterator = new TimerTask(){
			@Override
			public void run() {
				repaint();
				
				if(allSolutions == null || allSolutions.isEmpty()){
					this.cancel();
				}
				else if(displayedSolution != -1 && solutionStep++ > allSolutions.get(displayedSolution).length){
					this.cancel();
				}
			}
		};
		this.timer.schedule(this.solutionIterator, 0, REPAINT_DELAY);
	}

	public void draw(Graphics g) {
		
		g.setColor(BACKGROUND_COLOR);
		g.fillRect(0, 0, this.getWidth(), this.getHeight());

		this.drawMap(g);
		this.drawSolution(g);
		
		if(allSolutions != null){
			g.setColor(Color.YELLOW);
			g.setFont(INFO_FONT);
			g.drawString("Number of solutions: " + (allSolutions.size()), 5, 15);
			g.drawString("Currently displaying solution #" + (this.displayedSolution+1), 5, 35);
		}
		
		if(this.isReloadingMap){
			Graphics2D g2d = (Graphics2D)g;
			g2d.setColor(GLASS_COLOR);
			g.fillRect(0, 0, this.getWidth(), this.getHeight());
			
			FontMetrics metrics = this.getFontMetrics(MESSAGE_FONT);
			int messageWidth = metrics.stringWidth(RELOAD_MESSAGE);
			
			g.setColor(Color.WHITE);
			g.setFont(MESSAGE_FONT);
			g.drawString(RELOAD_MESSAGE, this.getWidth()/2 - messageWidth/2, this.getHeight()/2);
			
		}
		else if(this.displayedSolution == -1){
			Graphics2D g2d = (Graphics2D)g;
			g2d.setColor(GLASS_COLOR);
			g.fillRect(0, 0, this.getWidth(), this.getHeight());
			
			FontMetrics metrics = this.getFontMetrics(MESSAGE_FONT);
			int messageWidth = metrics.stringWidth(UNSOLVABLE_MESSAGE);
			
			g.setColor(Color.WHITE);
			g.setFont(MESSAGE_FONT);
			g.drawString(UNSOLVABLE_MESSAGE, this.getWidth()/2 - messageWidth/2, this.getHeight()/2);
			
		}

	}

	/**
	 * Draws each tile of the IceMap.
	 */
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

	/**
	 * Draws a line indicating the solution for the IceMap.
	 */
	private void drawSolution(Graphics g){
	
		if(this.displayedSolution == -1 || this.allSolutions.isEmpty()) return;
		
		Graphics2D g2d = (Graphics2D) g; 
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		
		NavigationNode[] currentSolution = this.allSolutions.get(this.displayedSolution);
		
		for(int i = 0; i < solutionStep && i < currentSolution.length; i++){
			NavigationNode move = currentSolution[i];
			
			// Draw the line from start tile to first move
			if(i == 0){
				g.setColor(Color.GRAY);
                g2d.setStroke(SOLUTION_LINE);
				g.drawLine(tileToPixelX(map.getStartX()) + tileWidth/2,
						tileToPixelY(map.getStartY()) + tileHeight/2,
						tileToPixelX(move.getDestinationX()) + tileWidth/2, 
						tileToPixelY(move.getDestinationY()) + tileHeight/2);
			}
			
			// Keep drawing lines between moves
			if(i != 0){
				NavigationNode previousMove = currentSolution[i-1];
				g.setColor(Color.GRAY);
                g2d.setStroke(SOLUTION_LINE);
				g.drawLine(tileToPixelX(previousMove.getDestinationX()) + tileWidth/2,
						tileToPixelY(previousMove.getDestinationY()) + tileHeight/2,
						tileToPixelX(move.getDestinationX()) + tileWidth/2, 
						tileToPixelY(move.getDestinationY()) + tileHeight/2);
			}
			
			// Draw the "head" of the line
			if(i == solutionStep - 1){
				g.setColor(Color.DARK_GRAY);
				g.fillOval(tileToPixelX(move.getDestinationX()) + 3, tileToPixelY(move.getDestinationY()) + 3, tileWidth - 6, tileHeight - 6);
			}
			
		}
		
	}
	
	private int tileToPixelX(int tileX){
		return (tileX * tileWidth) + tileX;
	}
	
	private int tileToPixelY(int tileY){
		return (tileY * tileHeight)+ tileY;
	}
	
}
