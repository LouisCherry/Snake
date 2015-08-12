package edu.just.main;

import javax.swing.JFrame;

import edu.just.jpanel.SnakePanel;
/**
 * 
 * @author PSX
 *
 */
public class StartPlay extends JFrame{
	public StartPlay(){
		this.setSize(800, 600);
		this.setTitle("贪吃蛇");
		this.setResizable(false);
		SnakePanel snake=new SnakePanel();
		snake.runSnake();
		this.addKeyListener(snake);
		this.add(snake);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setVisible(true);
	}
	public static void main(String[] args) {
		new StartPlay();
	}
}
