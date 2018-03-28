import java.io.BufferedReader;
import java.io.FileReader;
import java.io.PrintWriter;
import java.util.Random;

public class PSOTrainer {

	private final int NUM_OF_ITERATIONS = 50000;	private final String INPUT_FILE_NAME = "particles-input.txt";
	private final String OUTPUT_FILE_NAME = "particles-output.txt";

	private final String ENCODING_FORM = "UTF-8";

	// A random number generator
	private final Random R_GENERATOR = new Random();

	// An array that is to store the particles
	private Particle[] particles;

	// An array that is to store the best fitness for each particle
	private double[] fitnesses;

	//The output is 25 best games of all the iterations
	private long[] bestLinesCleared;
	private double[][] bestWeight;

	public static void main(String[] args) {
		PSOTrainer trainer = new PSOTrainer();

//		trainer.initializeParticles();
		trainer.initializeParticlesFromPreviousResult();

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

			bestLinesCleared = new long[Particle.POPULATION_SIZE];
			bestWeight = new double[Particle.POPULATION_SIZE][Particle.NUM_OF_ATTRIBUTES];

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

				bestLinesCleared[i] = 0;

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
		bestLinesCleared = new long[Particle.POPULATION_SIZE];
		bestWeight = new double[Particle.POPULATION_SIZE][Particle.NUM_OF_ATTRIBUTES];
		try {
			BufferedReader br = new BufferedReader(new FileReader(OUTPUT_FILE_NAME));
			String line = br.readLine();
			int index = 0;
			while (line != null) {
				String[] weightString = line.split(" ");
				double[] position = new double[Particle.NUM_OF_ATTRIBUTES];
				for (int i = 0; i < position.length; i++) {
					position[i] = Double.parseDouble(weightString[i]);
				}
				particles[index] = new Particle(position, index);
				bestLinesCleared[index] = 0;
				line = br.readLine();
				index++;
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
			ParticlePlayer player = new ParticlePlayer(particles[i]);
			player.play(0);
			double fitness = player.thirdfitnessEvaluation();
			fitnesses[i] = particles[i].updateFitness(fitness);

			if(bestLinesCleared[i] < player.getLinesCleared()){
				bestLinesCleared[i] = player.getLinesCleared();
				bestWeight[i] = player.getParticlePostion().clone();
			}
		}
	}

	/**
	 * Writes the weights to files for future use
	 */
	private void writeWeightsToFile() {

		try {
			PrintWriter writer = new PrintWriter(OUTPUT_FILE_NAME, ENCODING_FORM);

			for (int i = 0; i < particles.length; i++) {
//				Particle particle = particles[i];
//				double[] weights = particle.getPosition();
//				String[] weightsString = new String[weights.length];
//				for (int j = 0; j < weights.length; j++) { weightsString[j] = Double.toString(weights[j]); }
				String[] weighString = new String[bestWeight[i].length];
				for(int j = 0; j < bestWeight[i].length;j++)
					weighString[j] = Double.toString(bestWeight[i][j]);
				writer.println(String.join(" ", weighString) + " " + bestLinesCleared[i]);
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
			int bestNeighbor = -1;
			double bestNeighborFitness = (double) Integer.MIN_VALUE;

			// for each neighbor of the current particle, we find its fitness
			// and updates best one so that we can update particle's velocity and position
			for (int j = 0; j < neighbors.length; j++) {
				if (fitnesses[neighbors[j]] > bestNeighborFitness) {
					bestNeighbor = neighbors[j];
					bestNeighborFitness = fitnesses[neighbors[j]];
				}
			}

			// do updates
			particle.updateVelocity(particles[bestNeighbor].getPosition());
			particle.updatePosition();
		}
	}
}
