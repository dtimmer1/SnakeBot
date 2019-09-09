package SnakeBot;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class BottomPanel extends JPanel { //handles the start/stop button and the score
	static final long serialVersionUID = 0; //does nothing for the code, just put in so Eclipse was happy with the completed inheritance
	private GUI gui;
	private JButton playButton;
	private JLabel stateText;
	private JLabel sizeText;
	
	public BottomPanel(GUI gui) {
		this.gui = gui;
		setPreferredSize(new Dimension(200, 200));
		stateText = new JLabel("Mode: Default");
		stateText.setFont(new Font("Verdana",1,12));
		add(stateText);
		playButton = new JButton("Stop/Start");
		playButton.setBackground(Color.WHITE);
		playButton.setFocusable(false);
		playButton.addActionListener(e -> {if(gui.getState() != States.DEFAULT) gui.flipStop();});
		add(playButton);
		sizeText = new JLabel("Size: " + Integer.toString(gui.getGame().getLength()));
		sizeText.setFont(new Font("Verdana",1,12));
		add(sizeText);
	}
	
	public void update() {
		if(gui.getState() == States.DEFAULT) stateText.setText("Mode: Default");
		if(gui.getState() == States.ONE_NET) stateText.setText("Mode: Single AI");
		if(gui.getState() == States.EVOLVING) stateText.setText("Mode: Evolution");
		if(gui.getState() == States.HUMAN_PLAYING) stateText.setText("Mode: Playing");
		sizeText.setText("Size: " + Integer.toString(gui.getGame().getLength()));
	}
}
