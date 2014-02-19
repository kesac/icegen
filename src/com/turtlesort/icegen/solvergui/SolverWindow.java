package com.turtlesort.icegen.solvergui;

import java.awt.Dimension;
import java.awt.Toolkit;

import javax.swing.JFrame;

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

		// Center the window
		Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
		this.setLocation(screen.width/2 - this.getWidth()/2, screen.height/2 - this.getHeight()/2);


		this.add(this.canvas);		
	}

}
