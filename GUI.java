package SnakeBot;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

import javax.swing.BorderFactory;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.border.Border;
import javax.swing.filechooser.FileNameExtensionFilter;

public class GUI { // has main method and manages everything
	private int DELAY = 1;
	private JFrame frame;
	private Game game;
	private OptionMenu oMenu;
	private BottomPanel bPanel;
	private BrainPanel brain;
	private boolean stopped = true;
	private States state;
	private Timer timer;
	private File loaded;


	public JFrame getFrame() {
		return frame;
	}

	public Game getGame() {
		return game;
	}
	
	public void setDelay(int d) {
		DELAY = d;
	}

	public BrainPanel getBrain() {
		return brain;
	}

	public States getState() {
		return state;
	}

	public void setState(States state) {
		this.state = state;
		state.enter();
	}

	public void createAndShowGUI() {
		frame = new JFrame("SnakeBot");
		setState(States.DEFAULT);
		game = new Game(this);
		frame.setLayout(new GridBagLayout());
		frame.setSize(410, 420);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		GridBagConstraints c = new GridBagConstraints();
		c.gridy = 0;
		frame.add(game, c);
		oMenu = new OptionMenu(this);
		JMenuBar menu = new JMenuBar();
		bPanel = new BottomPanel(this);
		brain = new BrainPanel(game);
		frame.setJMenuBar(menu);
		menu.add(oMenu.loadMenu());
		menu.add(oMenu.evoMenu());
		menu.add(oMenu.playMenu());
		c.gridy = 1;
		frame.add(bPanel, c);
		Border whiteline = BorderFactory.createMatteBorder(0, 5, 0, 0, Color.WHITE);
		c.gridheight = 2;
		c.gridx = 1;
		c.gridy = 0;
		frame.add(brain, c);
		brain.setBorder(whiteline);
		frame.setVisible(true);
		frame.pack();
	}

	public void loadFile() { //this might not navigate to the correct folder immediately but the brain files do come in the repo
		JFileChooser j = new JFileChooser("/Brain Files/");
		double[][][] AI = new double[3][][];
		AI[0] = new double[16][23];
		AI[1] = new double[16][17];
		AI[2] = new double[4][17];
		FileNameExtensionFilter filter = new FileNameExtensionFilter(
				"Snake Brains", "snake");
		j.setFileFilter(filter);
		int openOK = j.showOpenDialog(oMenu);

		if(openOK == JFileChooser.APPROVE_OPTION) {
			loaded = j.getSelectedFile();
		}
		if(openOK == JFileChooser.CANCEL_OPTION) {
			loaded = null;
		}
		if(loaded != null && loaded.exists()) {
			try(BufferedReader reader = new BufferedReader(new FileReader(loaded));){
				for(int a = 0; a < AI.length; a++) {
					for(int b = 0; b < AI[a].length; b++) {
						for(int c = 0; c < AI[a][b].length; c++) {
							AI[a][b][c] = Double.parseDouble(reader.readLine());
						}
					}
					game.setAI(new SnakeAI(AI, this.brain));
				}
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
	}

	public void update() {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				bPanel.update();
				game.repaint();
				brain.repaint();
			}
		});
	}

	public void flipStop() {
		stopped = !stopped;
		if(stopped && timer != null) {
			stopTimer();
		}
		else if(state == States.EVOLVING) new Thread(new Runnable() {
			public void run() {
				game.runEvolution();
			}
		}).start();
		else runNewThread();
	}

	public void runNewThread() { //due to Swing's handling of multithreading this roundabout way of running threads is necessary for repeated running of game simulations
		new Thread(new Runnable() {
			public void run() {
				game.runOneRound();
			}
		}).start();
	}
	public static void main(String[] args) {
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				GUI gui = new GUI();
				gui.createAndShowGUI();
			}
		});
	}

	public void startTimer() {
		timer = new Timer(DELAY, new Listener());
		timer.start();
	}

	public void stopTimer() {
		game.kill();
		brain.setAI(false);
		timer.stop();
	}

	class Listener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			if(state != States.HUMAN_PLAYING) brain.setAI(true);
			if(!game.isAlive()) game.startGame();
			game.update();
			update();
			if(!game.isAlive()) {
				timer.stop();
				game.wait.countDown();
			}
		}
	}	
}
