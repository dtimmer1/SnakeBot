package SnakeBot;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Line2D;

import javax.swing.JPanel;

public class BrainPanel extends JPanel { //runs the network structure graphic
	static final long serialVersionUID = 0; //does nothing for the code, just put in so Eclipse was happy with the completed inheritance
	private Game game;
	private SnakeAI brain;
	private double[][] nodeValues = new double[4][];
	private boolean hasAI = false;
	
	public BrainPanel(Game game) {
		this.game = game;
		setPreferredSize(new Dimension(200, 400));
		setBackground(Color.BLACK);
	}
	
	public void setNodeValue(double[] values, int i) {
		this.nodeValues[i] = values;
	}
	
	public void setAI(boolean AI) {
		hasAI = AI;
	}
	
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		drawStuff(g);
	}
	
	public Color chooseLineColor(float d) {
		if(d < -1) d = -1;
		if(d > 1) d = 1;
		return (d <= 0) ? new Color(1, 1 + d, 1 + d, Math.abs(d)) : new Color(1 - d , 1 - d, 1, Math.abs(d));
	}
	
	public void drawStuff(Graphics g) {
		Graphics2D g2d = (Graphics2D) g;
		g2d.setStroke(new BasicStroke(1));

		if(!hasAI) {
			g2d.setColor(new Color((float)0.5, (float)1.0, (float)0.5));
			for(int i = 0; i < 23; i++) g2d.fillOval(28, 17+(16*i), 13, 13);
			for(int i = 0; i < 17; i++) {
				g2d.fillOval(69, 65+(16*i), 13, 13);
				g2d.fillOval(110, 65+(16*i), 13, 13);
			}
			for(int i = 0; i < 4; i++) g2d.fillOval(151, 170+(16*i), 13, 13);
			
		}
		
		else {
			brain = game.getAI();
			double[] alteredInput = nodeValues[0];
			for(int i = 0; i < alteredInput.length; i++) {
				alteredInput[i] = (1/(1 + Math.pow(Math.E, -0.15*alteredInput[i])))-.5;
				if(alteredInput[i] < 0) alteredInput[i] = 0;
			}
			for(int i = 0; i < 23; i++) {
				for(int j = 0; j < 16; j++) {
					g2d.setColor(chooseLineColor((float)brain.getAllWeights()[0][j][i]));
					g2d.draw(new Line2D.Double(34, 23+(16*i), 75, 71+(16*j)));
				}
			}
			for(int i = 0; i < 17; i++) {
				for(int j = 0; j < 16; j++) {
					g2d.setColor(chooseLineColor((float)brain.getAllWeights()[1][j][i]));
					g2d.draw(new Line2D.Double(75, 71+(16*i), 116, 71+(16*j)));
				}
			}
			for(int i = 0; i < 17; i++) {
				for(int j = 0; j < 4; j++) {
					g2d.setColor(chooseLineColor((float)brain.getAllWeights()[2][j][i]));
					g2d.draw(new Line2D.Double(116, 71+(16*i), 157, 176+(16*j)));
				}
			}
			for(int i = 0; i < 22; i++) {
				g2d.setColor(new Color((1 -(float)(alteredInput[i])), 1, 1 - (float)(alteredInput[i])));
				g2d.fillOval(28, 17+(16*i), 13, 13);
			}
			for(int i = 0; i < 16; i++) {
				g2d.setColor(new Color((1 -(float)nodeValues[1][i]), 1, 1 - (float)nodeValues[1][i]));
				g2d.fillOval(69, 65+(16*i), 13, 13);
				g2d.setColor(new Color((1 -(float)nodeValues[2][i]), 1, 1 - (float)nodeValues[2][i]));
				g2d.fillOval(110, 65+(16*i), 13, 13);
			}
			g2d.setColor(Color.GREEN);
			g2d.fillOval(28, 369, 13, 13);
			g2d.fillOval(69, 321, 13, 13);
			g2d.fillOval(110, 321, 13, 13);
			double max = 0;
			for(int i = 0; i < 4; i++) if(nodeValues[3][i] > max) max = nodeValues[3][i];
			for(int i = 0; i < 4; i++) {
				g2d.setColor((nodeValues[3][i] == max) ? Color.GREEN : Color.WHITE);
				g2d.fillOval(151, 170+(16*i), 13, 13);
			}
		}
	}
}
