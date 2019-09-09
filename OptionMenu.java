package SnakeBot;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPanel;

public class OptionMenu extends JPanel { //the top options bar
	static final long serialVersionUID = 0; //does nothing for the code, just put in so Eclipse was happy with the completed inheritance
	private GUI gui;
	private JMenuItem load = new JMenuItem("Load Brain");
	private JMenuItem evolve = new JMenuItem("Evolve Network");
	private JMenuItem play = new JMenuItem("Play Snake");
	
	public OptionMenu(GUI gui) {
		this.gui = gui;
	}
	JMenu loadMenu() {
		JMenu loading = new JMenu("Load");	
		load.addActionListener(e -> {
			gui.setState(States.ONE_NET);
			gui.setDelay(50);
			gui.loadFile();
			gui.update();
		});
		loading.add(load);
		return loading;
	}
	JMenu evoMenu() {
		JMenu evo = new JMenu("Evolve");
		evolve.addActionListener(e -> {
			gui.setState(States.EVOLVING);
			gui.setDelay(1);
			gui.update();
		});
		evo.add(evolve);
		return evo;
	}
	JMenu playMenu() {
		JMenu playing = new JMenu("Play");
		play.addActionListener(e -> {
			gui.setState(States.HUMAN_PLAYING);
			gui.setDelay(50);
			gui.update();
		});
		playing.add(play);
		return playing;
	}

}

