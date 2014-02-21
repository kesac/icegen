package com.turtlesort.icegen.solvergui;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;
import javax.swing.UIManager;

@SuppressWarnings("serial")
public class SolverWindow extends JFrame {

	private SolverCanvas canvas;
	
	public SolverWindow(){
		this.canvas = new SolverCanvas();
		this.initWindow();
	}
	
	private void initWindow(){

		// Build the window we will display the solution
		this.setTitle("Solver GUI 0.1");
		this.setSize(800, 600);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		try {
			
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		this.initMenuBar();
		
		// Center the window
		Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
		this.setLocation(screen.width/2 - this.getWidth()/2, screen.height/2 - this.getHeight()/2);

		this.add(this.canvas);		
	}
	
	/**
	 * TODO: Replace with lambdas when they become available
	 */
	private void initMenuBar(){
		
		JMenuBar menuBar = new JMenuBar();
		
		JMenu fileMenu = new JMenu("File");
		fileMenu.setMnemonic(KeyEvent.VK_F);
		
		JMenuItem openItem = new JMenuItem("Open File...");
		JMenuItem reloadItem = new JMenuItem("Reload File");
		JMenuItem closeItem = new JMenuItem("Close");
		
		openItem.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				
			}
		});
		
		reloadItem.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				
			}
		});
		
		closeItem.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				
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
		JCheckBoxMenuItem pruneItem = new JCheckBoxMenuItem("Prune Solution Set");
		JMenuItem moveLimitItem = new JMenuItem("Adjust Move Limit");

		nextItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_EQUALS, ActionEvent.CTRL_MASK));
		previousItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_MINUS, ActionEvent.CTRL_MASK));
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

}
