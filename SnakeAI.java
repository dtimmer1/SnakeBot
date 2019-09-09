package SnakeBot;

public class SnakeAI { // weights of each layer of the neural net are taken as a matrix; and the feeding forward process works as a series of matrix multiplications
	class Layer { //defined as an inner class to allow for free addition of more layers
		public double[] input;
		public double[][] weights;
		
		public Layer(double[][] weights) {
			this.weights = weights;
		}
		
		public double[] feedForward(double[] input) { //multiplies two matrices to create a column vector
			double[] inputWithBias = new double[input.length +1];
			for(int i = 0; i < input.length; i++) inputWithBias[i] = input[i];
			inputWithBias[input.length] = 1; 
			double[] output = new double[weights.length];
			for(double[] w : weights) if(w.length != inputWithBias.length) throw new IllegalArgumentException("cannot multiply matrices");
			for(int i = 0; i < weights.length; i++) {
				for(int j = 0; j < weights[i].length; j++) {
					output[i] += weights[i][j]*inputWithBias[j];
				}
				output[i] = (1/(1 + Math.pow(Math.E, -1*output[i]))); //normalizes values from 0 to 1
			}
			return output;
		}
	}

	
	private Layer[] layers;
	private double[][][] allWeights;
	private BrainPanel brain;
	
	public SnakeAI(double[][][] weights, BrainPanel brain) {
		allWeights = weights;
		this.brain = brain;
		layers = new Layer[3];
		layers[0] = new Layer(allWeights[0]);
		layers[1] = new Layer(allWeights[1]);
		layers[2] = new Layer(allWeights[2]);
	}
	
	public double[][][] getAllWeights(){
		return allWeights;
	}
	
	public Layer[] getLayers() {
		return layers;
	}
	
	public double[] runFull(double[] input) { //takes an input vector and through matrix multiplication returns an output vector that is handled by game logic
		double[] activations = input;
		layers = getLayers();
		for(int i = 0; i < layers.length; i++) {
			activations = layers[i].feedForward(activations);
			brain.setNodeValue(activations, i+1);
		}
		return activations;
	}
}
