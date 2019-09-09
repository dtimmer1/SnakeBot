package SnakeBot;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

import javax.swing.JPanel;

public class Game extends JPanel { //all the functionality of the game handled here
	static final long serialVersionUID = 0; //does nothing for the code, just put in so Eclipse was happy with the completed inheritance
	private final int WIDTH = 200;
	private final int HEIGHT = 200;
	private final int GRID_UNIT = 10;
	private final int MAX_LENGTH = WIDTH * HEIGHT / GRID_UNIT * GRID_UNIT; 
	private final int RAND_X = (int) (WIDTH / GRID_UNIT) - 2;
	private final int RAND_Y = (int) (HEIGHT / GRID_UNIT) - 2;
	private Population pop;
	private SnakeAI snake;
	private GUI gui;
	CountDownLatch wait = new CountDownLatch(1);

	public final int[] x = new int[MAX_LENGTH];
	public final int[] y = new int[MAX_LENGTH];

	private int foodX;
	private int foodY;
	private int size;
	private int score;
	private int steps;
	private int intermediateSteps;

	private boolean alive = false;
	boolean firstEvo = true;
	private boolean moveLeft = false;
	private boolean moveRight = true;
	private boolean moveUp = false;
	private boolean moveDown = false;

	public int getLength() {
		return size;
	}

	public void kill() {
		alive = false;
	}

	public boolean isAlive() {
		return alive;
	}

	public void setAI(SnakeAI s) {
		this.snake = s;
	}
	
	public SnakeAI getAI() {
		return snake;
	}

	public int getScore() {
		return score;
	}

	public Game(GUI gui) {
		this.gui = gui;
		buildGame();
	}

	private void buildGame() {
		setFocusable(true);
		setBackground(Color.BLACK);
		setPreferredSize(new Dimension(WIDTH, HEIGHT));
	}

	public void startGame() {
		if (gui.getState().humanPlaying())
			addKeyListener(new SnakeListener());
		size = 1;
		steps = 0;
		intermediateSteps = 0;
		x[0] = HEIGHT / 2;
		y[0] = HEIGHT / 2;
		placeFood();
		alive = true;
	}

	private void placeFood() {
		int posX = (int) (Math.random() * RAND_X);
		int posY = (int) (Math.random() * RAND_Y);
		foodX = posX * GRID_UNIT;
		foodY = posY * GRID_UNIT;
		for (int i = size; i > 0; i--) {
			if (x[i - 1] == foodX && y[i - 1] == foodY)
				placeFood();
		}
	}

	public void update() {
		if (!alive)
			startGame();
		alive = true;
		if (alive) {
			updateScore();
			checkFood();
			checkCollision();
			move();
		}
	}

	private void updateScore() {
		score = (int)Math.pow(size, 3)*steps;
	}

	private void checkFood() {
		if (x[0] == foodX && y[0] == foodY) {
			intermediateSteps = 0;
			size++;
			placeFood();
		}
	}

	private void move() {
		if (!gui.getState().humanPlaying()) {
			double[] input = setInput();
			gui.getBrain().setNodeValue(input, 0);
			double[] output = snake.runFull(input);
			double max = 0;
			for (int i = 0; i < output.length; i++) {
				if (output[i] > max)
					max = output[i];
			}
			if (output[0] == max && !moveRight) {
				moveLeft = true;
				moveRight = false;
				moveUp = false;
				moveDown = false;
			}
			if (output[1] == max && !moveLeft) {
				moveLeft = false;
				moveRight = true;
				moveUp = false;
				moveDown = false;
			}
			if (output[2] == max && !moveDown) {
				moveLeft = false;
				moveRight = false;
				moveUp = true;
				moveDown = false;
			}
			if (output[3] == max && !moveUp) {
				moveLeft = false;
				moveRight = false;
				moveUp = false;
				moveDown = true;
			}
		}
		for (int i = size - 1; i > 0; i--) {
			x[i] = x[(i - 1)];
			y[i] = y[(i - 1)];
		}

		if (moveLeft)
			x[0] -= GRID_UNIT;
		if (moveRight)
			x[0] += GRID_UNIT;
		if (moveUp)
			y[0] -= GRID_UNIT;
		if (moveDown)
			y[0] += GRID_UNIT;
		steps++;
		intermediateSteps++;
	}

	private double[] setInput() { //initializes neural net inputs on each game tick
		double[] input = new double[22];
		int[] dirs = new int[8];
		for(int i = 0; i < dirs.length; i ++) dirs[i] = WIDTH; // sets inputs to total width to allow for minimization
		input[0] = x[0];
		input[1] = y[0];
		input[2] = WIDTH - x[0];
		input[3] = WIDTH - y[0];
		input[4] = WIDTH - ((foodX == x[0] && foodY - y[0] > 0) ? foodY - y[0] : WIDTH); //next 8 steps set the distance from the head to the food in all 8 directions, if any distance is not present it is set to 0
		input[5] = WIDTH - ((foodX == x[0] && y[0] - foodY > 0) ? y[0] - foodY : WIDTH);
		input[6] = WIDTH - ((foodY == y[0] && foodX - x[0] > 0) ? foodX - x[0] : WIDTH);
		input[7] = WIDTH - ((foodY == y[0] && x[0] - foodX > 0) ? x[0] - foodX : WIDTH);
		input[8] = WIDTH - ((foodX - x[0] == foodY - y[0] && foodY - y[0] > 0) ? foodX - x[0] : WIDTH);
		input[9] = WIDTH - ((foodX - x[0] == foodY - y[0] && y[0] - foodY > 0) ? x[0] - foodX : WIDTH);
		input[10] = WIDTH - ((foodX - x[0] == -1*(foodY - y[0]) && foodX - x[0] > 0) ? foodX - x[0] : WIDTH);
		input[11] = WIDTH - ((foodX - x[0] == -1*(foodY - y[0]) && x[0] - foodX > 0) ? x[0] - foodX : WIDTH);
		for(int i = size; i > 0; i--) { //checks every tail coordinate to see which is closest to the head in all 8 directions
			if(x[i] == x[0] && 0 < y[i]-y[0] && y[i]-y[0] < dirs[0]) dirs[0] -= y[i]-y[0]; //sets distance to tail south
			if(x[i] == x[0] && 0 < y[0]-y[i] && y[0]-y[i] < dirs[0]) dirs[0] -= y[0]-y[i]; //sets distance north
			if(y[i] == y[0] && 0 < x[i]-x[0] && x[i]-x[0] < dirs[2]) dirs[2] -= x[i]-x[0]; //east
			if(y[i] == y[0] && 0 < x[0]-x[i] && x[0]-x[i] < dirs[3]) dirs[3] -= x[0]-x[i]; //west
			if(x[i]-x[0] == y[i]-y[0] && 0 < x[i]-x[0] && x[i]-x[0] < dirs[4]) dirs[4] -= x[i]-x[0]; //southeast
			if(x[i]-x[0] == y[i]-y[0] && 0 < x[0]-x[i] && x[0]-x[i] < dirs[5]) dirs[5] -= x[0]-x[i]; //northwest
			if(x[i]-x[0] == -1*(y[i]-y[0]) && 0 < x[i]-x[0] && x[i]-x[0] < dirs[6]) dirs[6] -= x[i]-x[0]; //northeast
			if(x[i]-x[0] == -1*(y[i]-y[0]) && 0 < x[0]-x[i] && x[0]-x[i] < dirs[7]) dirs[7] -= x[0]-x[i]; //southwest
		}
		for(int i = 0; i < dirs.length; i++) if(dirs[i] == WIDTH) dirs[i] = 0; //sets all unaltered values to 0
		for(int i = 12; i < 20; i++) input[i] = dirs[i-12];
		input[20] = intermediateSteps;
		input[21] = Math.sqrt(((foodX-x[0])*(foodX-x[0]))+((foodY-y[0])*(foodY-y[0])));
		return input;
	}
	private void checkCollision() {
		for (int i = size - 1; i > 0; i--) {
			if (x[0] == x[i] && y[0] == y[i])
				alive = false;
		}

		if (y[0] >= HEIGHT)
			alive = false;
		if (x[0] >= WIDTH)
			alive = false;
		if (y[0] < 0)
			alive = false;
		if (x[0] < 0)
			alive = false;
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		drawStuff(g);
	}

	private void drawStuff(Graphics g) {
		if (alive) {
			g.setColor(Color.GREEN);
			g.fillRect(foodX, foodY, GRID_UNIT, GRID_UNIT);
			for (int i = 0; i < size; i++) {
				g.setColor(Color.WHITE);
				g.fillRect(x[i], y[i], GRID_UNIT, GRID_UNIT);
			}
		} else if (gui.getState().singleRun())
			g.setColor(Color.BLACK);
	}

	public void runOneRound() {
		wait = new CountDownLatch(1);
		gui.startTimer();
		try {
			wait.await();
		} catch(Exception e) {e.printStackTrace();}
	}

	public void runEvolution() {
		int count = 0;
		int average = 0;
		pop = new Population(1, this);
		pop.setRandomGenes();
		while(count < 500) {
			Map<double[][][], Integer> scores = new HashMap<>();
			for(double[][][] gene : pop.getGenes()) {
				snake = new SnakeAI(gene, gui.getBrain());
				for(int i = 0; i < 3; i++) {
					runOneRound();
					average += score;
				}
				scores.put(gene, average/3);
				average = 0;
			}
			wait = new CountDownLatch(1);
			new Thread(new Runnable() {
				public void run() {
					pop = pop.makeNextGeneration(scores);
				}
			}).start();
			try {
				wait.await();
			} catch(Exception e) {e.printStackTrace();}
			count++;
		}
	}


	class SnakeListener extends KeyAdapter {
		@Override
		public void keyPressed(KeyEvent e) {
			int key = e.getKeyCode();
			if(key == KeyEvent.VK_LEFT && !moveRight) {
				moveLeft = true;
				moveUp = false;
				moveDown = false;
			}
			if(key == KeyEvent.VK_RIGHT && !moveLeft) {
				moveRight = true;
				moveUp = false;
				moveDown = false;
			}
			if(key == KeyEvent.VK_UP && !moveDown) {
				moveUp = true;
				moveLeft = false;
				moveRight = false;
			}
			if(key == KeyEvent.VK_DOWN && !moveUp) {
				moveDown = true;
				moveLeft = false;
				moveRight = false;
			}
		}
	}
}
