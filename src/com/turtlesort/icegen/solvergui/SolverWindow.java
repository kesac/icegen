package com.turtlesort.icegen.solvergui;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;
import java.io.File;
import java.util.LinkedList;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.KeyStroke;
import javax.swing.UIManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileFilter;

import com.turtlesort.icegen.IceMap;
import com.turtlesort.icegen.IceMapSolver;
import com.turtlesort.icegen.NavigationNode;

/**
 * Draws an IceMap in a JFrame, finds a solution to the map with the least amount of moves, then
 * animates the sequence of moves in the solution.
 */
@SuppressWarnings("serial")
public class SolverWindow extends JFrame {

	private static final String WINDOW_TITLE = "Solver GUI 0.1";
	private static final String RELOAD_MESSAGE = "Reloading map and resolving...";
	private static final String UNSOLVABLE_MESSAGE = "No solution exists!";
	
	private static final Font MESSAGE_FONT = new Font("Arial", Font.PLAIN, 40);
	private static final Font INFO_FONT = new Font("Arial", Font.PLAIN, 18);
	
	private static final BasicStroke SOLUTION_LINE = new BasicStroke(10);
	private static final Color BACKGROUND_COLOR = new Color(200,200,200);
	private static final Color GLASS_COLOR = new Color(0, 0, 0, 125);
	
	private static final int ANIMATION_DELAY = 100; // Milliseconds; the smaller the number the faster the solution gets painted

	private JFileChooser fileChooser;
	private File sourceFile;
	private long sourceLastModified;					// The larger the number the more recent the last modified timestamp
	
	private IceMap map;
	private JPanel canvas;
	private int tileWidth;								// In pixels; recalculated every repaint (as the window size can change)
	private int tileHeight;								// In pixels; recalculated every repaint

	private int displayedSolution;						// Value will be -1 if there are no solutions
	private LinkedList<NavigationNode[]> allSolutions;	// Will be empty if there are no solutions

	private Timer timer;
	private TimerTask solutionIterator;
	private int solutionStep;
	private int moveLimit;
	private boolean pruneSolutionSet;
	private boolean isReloadingMap;
	
	private JLabel moveLimitLabel;
	private JSlider moveLimitSlider;
	private JPanel moveLimitPanel;

	/**
	 * Constructor. Builds the window and initializes the internal state. You need to call setVisible() to make
	 * it appear on the screen after instantiation. By default, no map is displayed when launched.
	 */
	public SolverWindow() {

		this.initWindow();
		this.initMenuBar();
		this.initMoveLimitDialogWindow();
		
		this.timer = new Timer();
		this.pruneSolutionSet = true;
		this.moveLimit = 20;
		
		// Initialize the file dialog window. Ensure that only one .tmx file can be
		// selected at a time
		this.fileChooser = new JFileChooser();
		this.fileChooser.setMultiSelectionEnabled(false);
		this.fileChooser.setFileFilter(new FileFilter(){
			@Override
			public boolean accept(File f) {
				return f.getName().endsWith(".tmx");
			}

			@Override
			public String getDescription() {
				return ".tmx (Tiled map)";
			}
		});

		// Check if the file was modified when window focus is regained. If it has
		// been modified, reload the file and re-solve it. This is a convenience feature
		// for users who have Tiled and this application running side-by-side.
		this.addWindowFocusListener(new WindowFocusListener(){

			@Override
			public void windowGainedFocus(WindowEvent arg0) {
				if(sourceFile != null && sourceFile.lastModified() > sourceLastModified){
					reloadFile();
				}
			}

			@Override
			public void windowLostFocus(WindowEvent arg0) {}

		});

	}

	private void initWindow(){

		this.setTitle(WINDOW_TITLE);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setSize(600,600);
		
		// Delegate the paint method in our canvas to methods in this class
		this.canvas = new JPanel(){
			public void paintComponent(Graphics g){
				super.paintComponent(g);
				draw(g);
			}
		};

		this.add(this.canvas);
		
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		// Center the window
		Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
		this.setLocation(screen.width/2 - this.getWidth()/2, screen.height/2 - this.getHeight()/2);

	}

	/**
	 * TODO: Use lambdas when they become available
	 */
	private void initMenuBar(){

		JMenuBar menuBar = new JMenuBar();

		JMenu fileMenu = new JMenu("File");
		fileMenu.setMnemonic(KeyEvent.VK_F);

		JMenuItem openItem = new JMenuItem("Open File...");
		JMenuItem reloadItem = new JMenuItem("Reload File");
		JMenuItem closeItem = new JMenuItem("Close");

		final JFrame window = this;

		openItem.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				if(fileChooser.showOpenDialog(window) == JFileChooser.APPROVE_OPTION){
					sourceFile = fileChooser.getSelectedFile();
					reloadFile();
				}
			}
		});

		reloadItem.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				reloadFile();
			}
		});

		closeItem.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});

		openItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, ActionEvent.CTRL_MASK));
		reloadItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_R, ActionEvent.CTRL_MASK));

		fileMenu.add(openItem);
		fileMenu.add(reloadItem);
		fileMenu.addSeparator();
		fileMenu.add(closeItem);

		JMenu viewMenu = new JMenu("View");
		viewMenu.setMnemonic(KeyEvent.VK_V);

		JMenuItem nextItem = new JMenuItem("Next Solution");
		JMenuItem previousItem = new JMenuItem("Previous Solution");
		final JCheckBoxMenuItem pruneItem = new JCheckBoxMenuItem("Prune Solution Set");
		JMenuItem moveLimitItem = new JMenuItem("Adjust Move Limit");

		nextItem.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				if(map != null && displayedSolution != -1){
					showNextSolution();
				}
			}
		});
		
		previousItem.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				if(map != null && displayedSolution != -1){
					showPreviousSolution();
				}
			}
		});
		
		pruneItem.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				pruneSolutionSet = pruneItem.isSelected();
				if(map != null){
					reloadFile(true);
				}
			}
		});
		
		moveLimitItem.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				showMoveLimitDialog();
			}
		});
		
		nextItem.setAccelerator(KeyStroke.getKeyStroke('='));
		previousItem.setAccelerator(KeyStroke.getKeyStroke('-'));
		pruneItem.setSelected(true);

		viewMenu.add(nextItem);
		viewMenu.add(previousItem);
		viewMenu.add(pruneItem);
		viewMenu.addSeparator();
		viewMenu.add(moveLimitItem);

		menuBar.add(fileMenu);
		menuBar.add(viewMenu);

		this.setJMenuBar(menuBar);
	}

	private void initMoveLimitDialogWindow(){
		moveLimitPanel = new JPanel();
		moveLimitPanel.setLayout(new BorderLayout());
		
		moveLimitLabel = new JLabel("20");
		moveLimitSlider = new JSlider(JSlider.HORIZONTAL,1,30,20);
		moveLimitSlider.setMajorTickSpacing(3);
		moveLimitSlider.setMinorTickSpacing(1);
		moveLimitSlider.setPaintLabels(true);
		moveLimitSlider.setPaintTicks(true);
		
		moveLimitSlider.addChangeListener(new ChangeListener(){
			@Override
			public void stateChanged(ChangeEvent arg0) {
				moveLimitLabel.setText(String.valueOf(moveLimitSlider.getValue()));
			}
		});
		
		moveLimitPanel.add(moveLimitLabel, BorderLayout.EAST);
		moveLimitPanel.add(moveLimitSlider, BorderLayout.CENTER);
	}
	
	/*
	 * The repaint timer is responsible for animating the solution line on the canvas. This method
	 * restarts the timer.
	 */
	private void restartRepaintTimer(){

		// Setup a timer task that iterates through the moves of the solution

		if(this.map == null) return;

		if(this.solutionIterator != null){
			this.solutionIterator.cancel();
		}

		this.solutionStep = 0;
		this.solutionIterator = new TimerTask(){
			@Override
			public void run() {
				repaint();

				if(displayedSolution != -1 && solutionStep++ > allSolutions.get(displayedSolution).length){
					this.cancel();
				}
			}
		};
		this.timer.schedule(this.solutionIterator, 0, ANIMATION_DELAY);
	}

	private void reloadFile(){
		reloadFile(false);
	}
	
	/*
	 * This method will also re-solve the reloaded map file if it has been changed
	 * or if true has been given as an argument.
	 */
	private void reloadFile(final boolean forceResolve){

		if(!this.isReloadingMap){
			this.isReloadingMap = true;
			this.repaint();
			this.setTitle(WINDOW_TITLE + " - " + "Reloading file...");	

			// We reload in another thread so our file loading and map solving
			// doesn't block the reloading message painted on the screen.
			this.timer.schedule(new TimerTask(){

				@Override
				public void run() {

					IceMap oldMap = map;

					// If the file's last modified time stamp changed, reread the file again
					if(sourceFile != null && sourceFile.lastModified() > sourceLastModified){
						map = IceMap.parseTMXFile(sourceFile);
						sourceLastModified = sourceFile.lastModified();
					}

					// Resolve the IceMap if the time stamp changed or if explicitly told to do so
					if(oldMap != map || forceResolve){

						IceMapSolver solver = new IceMapSolver(map);
						LinkedList<NavigationNode[]> solutions = solver.solve(moveLimit, pruneSolutionSet);

						if(solutions.size() > 0){
							displayedSolution = 0;
							allSolutions = solutions;
						}
						else{
							displayedSolution = -1;
							allSolutions = null;
						}

					}

					// Restart the repaint timer
					restartRepaintTimer();

					setTitle(WINDOW_TITLE + " - " + sourceFile.getAbsolutePath());
					isReloadingMap = false;
				}

			}, 0);

		}
	}

	private void showNextSolution(){
		displayedSolution++;
		if(displayedSolution + 1 >= allSolutions.size()){
			displayedSolution = 0;
		}
		restartRepaintTimer();
	}
	
	private void showPreviousSolution(){
		displayedSolution--;
		if(displayedSolution - 1 < 0){
			displayedSolution = allSolutions.size() - 1;
		}
		restartRepaintTimer();
	}
	

	
	private void showMoveLimitDialog() {
		moveLimitSlider.setValue(moveLimit);
		moveLimitLabel.setText(String.valueOf(moveLimit));
		
		int status = JOptionPane.showOptionDialog(this, moveLimitPanel, "Choose move limit:", JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, null, null);
		if(status == JOptionPane.OK_OPTION){
			moveLimit = moveLimitSlider.getValue();
			reloadFile(true);
		}
	}
	
	private void draw(Graphics g) {

		if(this.map == null) return;

		g.setColor(BACKGROUND_COLOR);
		g.fillRect(0, 0, this.getWidth(), this.getHeight());

		Container contentPane = this.getContentPane();		
		int canvasWidth = contentPane.getWidth();
		int canvasHeight = contentPane.getHeight();

		this.tileWidth = canvasWidth / map.getWidth();
		this.tileHeight = canvasHeight / map.getHeight();

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

	/*
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

	/*
	 * Draws a line indicating the solution for the IceMap.
	 */
	private void drawSolution(Graphics g){

		if(this.displayedSolution == -1) return;

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

	// Utility methods
	private int tileToPixelX(int tileX){
		return (tileX * tileWidth) + tileX;
	}

	private int tileToPixelY(int tileY){
		return (tileY * tileHeight)+ tileY;
	}

}
