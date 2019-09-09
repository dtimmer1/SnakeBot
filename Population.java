package SnakeBot;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

public class Population { //class where the genetic algorithm is handled
	public static final int POPULATION_SIZE = 1500;
	private Game game;
	public static final double MUTATION_CHANCE = .1;
	private int generation;
	private double[][][][] genes = new double[POPULATION_SIZE][3][][]; //four-dimensional array is sadly a necessity here: a single network is an array of 2D vectors and the population is an array of those
	Random randomGenerator = new Random();

	public Population(int gen, Game game) {
		generation = gen;
		this.game = game;
		for(double[][][] gene : genes) {
			gene[0] = new double[16][23];
			gene[1] = new double[16][17];
			gene[2] = new double[4][17];
		}
	}

	public int getGen() {
		return generation;
	}
	
	public Game getGame() {
		return game;
	}

	public double[][][][] getGenes() {
		return genes;
	}
	public void setGenes(double[][][][] newGenes) {
		this.genes = newGenes;
	}

	public void setRandomGenes() { //randomizes values for first generation
		for(int a = 0; a < genes.length; a++) {
			for(int b = 0; b < genes[a].length; b++) {
				for(int c = 0; c < genes[a][b].length; c++) {
					for(int d = 0; d < genes[a][b][c].length; d++) {
						genes[a][b][c][d] = (Math.random() < 0.5) ? Math.random() : Math.random() *-1;
					}
				}
			}
		}
	}

	public Population makeNextGeneration(Map<double[][][], Integer> fitnesses) { //builds new generation based on the genes of the top 10% of the last generation
		Population p = new Population(this.getGen() +1, this.getGame());
		double[][][][] newGenes = new double[POPULATION_SIZE][][][];
		List<Map.Entry<double[][][], Integer>> list = fitnesses.entrySet().stream().collect(Collectors.toList());
		Collections.sort(list, (e1, e2) -> (-1*e1.getValue().compareTo(e2.getValue()))); //sorts list from highest to lowest fitness to simplify the process
		System.out.println("- - - Generation " + this.generation + " - - -");
		System.out.println("Best: " + list.get(0).getValue());
		double ave = 0;
		for(int i : new ArrayList<Integer>(fitnesses.values())) ave += i;
		System.out.println("Average: " + ave/POPULATION_SIZE);
		ave = 0;
		for(int i = 0; i < POPULATION_SIZE/10; i++) ave += list.get(i).getValue();
		System.out.println("Average of top 10%: " + ave/(POPULATION_SIZE/10));
		try {
			createReadable(list.get(0).getKey());
		} catch(Exception e) {
			e.printStackTrace();
		}
		newGenes[0] = list.get(0).getKey();
		for(int i = 1; i < newGenes.length; i++) {
			newGenes[i] = crossOverAndMutate(selectParent(list), selectParent(list));
		}
		p.setGenes(newGenes);
		game.wait.countDown();
		return p;
	}
	
	public double[][][] selectParent(List<Map.Entry<double[][][], Integer>> list) { //chooses parents with a weighted randomness function, higher fitness --> higher probability
		int totalSum = 0;
		for(var e : list.subList(0, POPULATION_SIZE/10)) totalSum += e.getValue();
		int runningSum = 0;
		int rand = randomGenerator.nextInt(totalSum);
		for(var e : list.subList(0, POPULATION_SIZE/10)) {
			runningSum += e.getValue();
			if(runningSum > rand) return e.getKey();
		}
		return list.get(0).getKey();
	}

	public double[][][] crossOverAndMutate(double[][][] parent1, double[][][] parent2) { //constructs new child network with a combination of values from 2 parents
		double[][][] child = new double[3][][];
		child[0] = new double[16][23];
		child[1] = new double[16][17];
		child[2] = new double[4][17];
		int rand1 = randomGenerator.nextInt();
		int rand2 = randomGenerator.nextInt();
		for(int i = 0; i < child.length; i++) {
			for(int j = 0; j < child[i].length; j++) {
				for(int k = 0; k < child[i][j].length; k++) {
					child[i][j][k] = (j < rand1 || (j == rand1 && k < rand2)) ? parent1[i][j][k] : parent2[i][j][k];
				}
			}
		}
		
		return mutate(child);
	}
	
	public double[][][] mutate(double[][][] child) { // randomly changes random values to increase diversity
		double[][][] mutated = child;
		for(int i = 0; i < mutated.length; i++) {
			for(int j = 0; j < mutated[i].length; j++) {
				for(int k  = 0; k < mutated[i][j].length; k++) {
					if(Math.random() < MUTATION_CHANCE) mutated[i][j][k] += (Math.random() < 0.5) ? randomGenerator.nextGaussian()/5 : randomGenerator.nextGaussian()/-5;
				}
			}
		}
		return mutated;
	}
	
	public void createReadable(double[][][] best) throws IOException { //stores network weights for use later
		String fileName = "/Brain Files/generation" + generation + ".snake";
		File file = new File(fileName);
		file.createNewFile();
		BufferedWriter writer = new BufferedWriter(new FileWriter(file));
		for(int i = 0; i < best.length; i++) {
			for(int j = 0; j < best[i].length; j++) {
				for(int k = 0; k < best[i][j].length; k++) {
					writer.write(best[i][j][k] + "");
					writer.newLine();
				}
			}
		}
		writer.close();
	}
		
}
