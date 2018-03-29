package tetris.pso;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.PrintWriter;
import java.util.Random;

public class PSOTrainer {

	private final int NUM_OF_ITERATIONS = 500;
	private final int NUM_OF_GAMES_PER_ITER = 10;
	private final int MAX_LINES_CLEARED = 30000;
	private final String INPUT_FILE_NAME = "particles-input.txt";
	private final String OUTPUT_FILE_NAME = "particles-output.txt";
	

	private final String ENCODING_FORM = "UTF-8";

	// A random number generator
	private final Random R_GENERATOR = new Random();

	// An array that is to store the particles
	private Particle[] particles;

	// An array that is to store the best fitness for each particle
	private double[] fitnesses;

	//The output is 25 best games of all the iterations
	private double[] bestLinesCleared;

	public static void main(String[] args) {
		PSOTrainer trainer = new PSOTrainer();

		trainer.initializeParticles();
//		trainer.initializeParticlesFromPreviousResult();

		long startTime = System.currentTimeMillis();
		trainer.start();
		long endTime = System.currentTimeMillis();
		System.out.println(endTime - startTime);
	}

	/**
	 * Initializes all the particles, together with their fitness
	 */
	private void initializeParticles() {

		try {
			PrintWriter writer = new PrintWriter(INPUT_FILE_NAME, ENCODING_FORM);
			// Initializes particles and fitness array
			particles = new Particle[Particle.POPULATION_SIZE];
			fitnesses = new double[Particle.POPULATION_SIZE];
			bestLinesCleared = new double[Particle.POPULATION_SIZE];

			for (int i = 0; i < Particle.POPULATION_SIZE; i++) {

				// For now we just randomly assign values as initial positions
				// After a while we could use trained data and constantly improve
				double[] position = new double[Particle.NUM_OF_ATTRIBUTES];
				for (int j = 0; j < Particle.NUM_OF_ATTRIBUTES; j++) {
					double number = R_GENERATOR.nextDouble() * 2 - 1;
					position[j] = number;
				}

				// Create new particles
				String[] positionString = new String[position.length];
				particles[i] = new Particle(position, i);

				// Writes the initial value
				for (int j = 0; j < positionString.length; j++) { positionString[j] = Double.toString(position[j]); }
				writer.println(String.join(" ", positionString));
			}
			writer.close();
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}

	private void initializeParticlesFromPreviousResult() {
		particles = new Particle[Particle.POPULATION_SIZE];
		fitnesses = new double[Particle.POPULATION_SIZE];
		bestLinesCleared = new double[Particle.POPULATION_SIZE];
		double[][] recordedWeights = new double[Particle.POPULATION_SIZE][Particle.NUM_OF_ATTRIBUTES];
		double[][] recordedVelocity = new double[Particle.POPULATION_SIZE][Particle.NUM_OF_ATTRIBUTES];
		double[] recordedNoise = new double[Particle.POPULATION_SIZE];
		
		try {
			BufferedReader br = new BufferedReader(new FileReader(OUTPUT_FILE_NAME));
			
			for (int i = 0; i < recordedWeights.length; i++) {
				String line = br.readLine();
				System.out.println(line);
				String[] weightString = line.split(" ");
				for (int j = 0; j < recordedWeights[i].length; j++) {
					recordedWeights[i][j] = Double.parseDouble(weightString[j]);
				}
			}
			
			for (int i = 0; i < recordedVelocity.length; i++) {
				String line = br.readLine();
				String[] velocityString = line.split(" ");
				for (int j = 0; j < recordedVelocity[i].length; j++) {
					recordedVelocity[i][j] = Double.parseDouble(velocityString[j]);
				}
			}
			
			for (int i = 0; i < recordedNoise.length; i++) {
				String line = br.readLine();
				recordedNoise[i] = Double.parseDouble(line);
			}
			
			for (int i = 0; i < particles.length; i++) {
				particles[i] = new Particle(recordedWeights[i], recordedVelocity[i], recordedNoise[i], i);
			}
			br.close();
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}

	/**
	 * Starts the training
	 */
	private void start() {
		for (int i = 0; i < NUM_OF_ITERATIONS; i++) {
			System.out.println("Running iteration " + i);
			runAnIteration();
			updatePositions();
		}
		writeWeightsToFile();
	}

	/**
	 * Runs one iteration. Basically, what should be done in one iteration:
	 * for every particle:
	 * 1. play the game until die (for initial condition this is reasonable)
	 * 2. uses fitness function to evaluate the particle
	 * 3. update the fitness of the current position of the particle.
	 * Note: particle update will return its current individual best fitness.
	 */
	private void runAnIteration() {
		
		for (int i = 0; i < particles.length; i++) {
			int totalCleared = 0;
			int totalNumOfHoles = 0;
			int mostNumOfHoles = Integer.MIN_VALUE;
			int maxHeight = Integer.MIN_VALUE;
			double totalAverageHeight = 0;
			
			for (int j = 0; j < NUM_OF_GAMES_PER_ITER; j++) {
				ParticlePlayer player = new ParticlePlayer(particles[i]);
				player.play(MAX_LINES_CLEARED);
				int numOfHoles = player.getNumOfHoles();
				int linesCleared = player.getLinesCleared();
				double averageHeight = player.getAverageHeight();
				
				totalCleared += linesCleared;
				totalNumOfHoles += numOfHoles;
				totalAverageHeight += averageHeight;
				
				mostNumOfHoles = Math.max(player.getNumOfHoles(), mostNumOfHoles);
				maxHeight = Math.max(player.getMaxHeight(), maxHeight);				
			}
			double fitness = calculateFitness(totalCleared, totalNumOfHoles, mostNumOfHoles, maxHeight, totalAverageHeight);
			fitnesses[i] = fitness;
			particles[i].updateFitness(fitness);

			if(bestLinesCleared[i] < totalCleared * 1.0 / NUM_OF_GAMES_PER_ITER){
				bestLinesCleared[i] = totalCleared * 1.0 / NUM_OF_GAMES_PER_ITER;
			}
		}
	}
	
	private double calculateFitness(int totalCleared, int totalHoles, int mostHoles, int maxHeight, double totalAverageHeight) {
		double averageCleared = totalCleared * 1.0 / NUM_OF_GAMES_PER_ITER;
		double averageHoles = totalHoles * 1.0 / NUM_OF_GAMES_PER_ITER;
		double averageHeight = totalAverageHeight / NUM_OF_GAMES_PER_ITER;
		
		return averageCleared + (mostHoles - averageHoles) * 500 + (maxHeight - averageHeight) * 500;
	}

	/**
	 * Writes the weights to files for future use
	 */
	private void writeWeightsToFile() {

		try {
			PrintWriter writer = new PrintWriter(OUTPUT_FILE_NAME, ENCODING_FORM);

			for (int i = 0; i < particles.length; i++) {
				Particle particle = particles[i];
				double[] weights = particle.getBestPosition();
				String[] weightsString = new String[weights.length];
				for (int j = 0; j < weights.length; j++) { weightsString[j] = Double.toString(weights[j]); }
				writer.println(String.join(" ", weightsString));
			}
			
			for (int i = 0; i < particles.length; i++) {
				Particle particle = particles[i];
				double[] velocities = particle.getBestVelocity();
				String[] velocityString = new String[velocities.length];
				for (int j = 0; j < velocities.length; j++) { velocityString[j] = Double.toString(velocities[j]); }
				writer.println(String.join(" ", velocityString));
			}
			
			for (int i = 0; i < particles.length; i++) {
				Particle particle = particles[i];
				String noiseFactorString = Double.toString(particle.getNoiseFactor());
				writer.println(noiseFactorString);
			}
			
			writer.println("Best scores: ");
			for (int i = 0; i < particles.length; i++) {
				writer.println(bestLinesCleared[i]);
			}

			writer.close();
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}

	}

	/**
	 * Updates the positions of the particles.
	 * The basic idea is that for every particle we need to update its position
	 * according to the formula provided. In this case we need its current best
	 * position, which is stored in particles themselves. We also need the best
	 * position within it "neighborhood". Then we could get the neighbors' best
	 * position according to the `fitness` array.
	 */
	private void updatePositions() {
		for (int i = 0; i < particles.length; i++) {
			Particle particle = particles[i];
			int[] neighbors = particle.getNeighbors();

			// Here is not good SE practice since we assumes that there will always be
			// neighbors, which is not the case. but for our project it does not really
			// matter
			// We initialize some dumb best index and best value first
			int bestNeighbor = 0;
			double bestNeighborFitness = fitnesses[bestNeighbor];

			// for each neighbor of the current particle, we find its fitness
			// and updates best one so that we can update particle's velocity and position
			for (int j = 1; j < neighbors.length; j++) {
				if (fitnesses[neighbors[j]] > bestNeighborFitness) {
					bestNeighbor = neighbors[j];
					bestNeighborFitness = fitnesses[neighbors[j]];
				}
			}

			// do updates
			particle.updateVelocity(particles[bestNeighbor].getPosition(), bestNeighborFitness);
			particle.updatePosition();
		}
	}
}
