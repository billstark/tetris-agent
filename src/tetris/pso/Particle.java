package tetris.pso;
import java.util.ArrayList;
import java.util.Random;

class Particle {	
	/*
	 * The position is the weight in the heuristic
	 * weight of line cleared
	 * weight of number of holes
	 * weight of total weight of holes
	 * weight of sum of adjacent column height difference
	 * weight of landing height
	 * weight of total height
	 * weight of range of height
	 */
	public static final int POPULATION_SIZE = 25;
	public static final int NUM_OF_ATTRIBUTES = 7;
	
	private final double SCALE_FACTOR = 0.75;
	private final Random R_GENERATOR = new Random();
			
	private double[] position;
	private double[] bestPosition;
	private double[] velocity;
	
	public int id;
	
	// Stores the "neighbors" of the current particle
	private ArrayList<Integer> neighbors;
	
	// Stores the fitness of the current particle. Initially should be
	// not really fit.
	private double fitness = Integer.MIN_VALUE;
	
	// Defines the constants for PSO velocity updates
	private final double INERTIA_WEIGHT = 0.72;		
	private final double COGNITIVE_TERM = 1.42;	 	
	private final double SOCIAL_TERM = 1.42;	 	
	private final double VELOCITY_BOUND = 0.5;
	
	/**
	 * Constructor of the particle.
	 * This will take in a randomly chosen position vector
	 * It will generate a random initial velocity for initialization
	 * 
	 * @param initialPosition
	 */
	public Particle(double[] initialPosition, int id) {
		initializeNeighbours(id);
		initializePosition(initialPosition);
		initializeVelocity();
		this.id = id;
	}
	
	/**
	 * Add the "neighbors" of the current particle. The "neighbors"
	 * is defined as a particle that is "located" at top, right, 
	 * bottom, left of the current particle (using index for measurement)
	 * 
	 * @param id the id of the current particle
	 */
	private void initializeNeighbours(int id) {
		neighbors = new ArrayList<Integer>();
		int edgeLength = (int) Math.sqrt(POPULATION_SIZE);
		if (id >= edgeLength) { neighbors.add(id - edgeLength); }
		if ((id + 1) % edgeLength != 0) { neighbors.add(id + 1); }
		if (id < POPULATION_SIZE - edgeLength) { neighbors.add(id + edgeLength); }
		if (id % edgeLength != 0) { neighbors.add(id - 1); }
	}
	
	/**
	 * Initializes the velocity.
	 * The initial velocity will be limited within [3/4 min bound, 3/4 max bound]
	 */
	private void initializeVelocity() {
		velocity = new double[NUM_OF_ATTRIBUTES];
		for (int i = 0; i < velocity.length; i++) {
			velocity[i] = (R_GENERATOR.nextDouble() - VELOCITY_BOUND) * SCALE_FACTOR;
		}
	}

	/**
	 * Initialized the position with given position
	 * @param position
	 */
	public void initializePosition(double[] position) {
		this.position = position.clone();
	}

	
	/**
	 * Updates the velocity of the current particle according to cognitive and social
	 * factors.
	 * @param bestSwarmPosition an array that contains the position of the best position in
	 * 		the swarm society
	 */
	public void updateVelocity(double[] bestSwarmPosition) {
		for (int i = 0; i < NUM_OF_ATTRIBUTES; i++) {
			double r1 = R_GENERATOR.nextDouble();
			double r2 = R_GENERATOR.nextDouble();
			
			double cognitiveVelocity = COGNITIVE_TERM * r1 * (bestPosition[i] - position[i]);
			double socialVelocity = SOCIAL_TERM * r2 * (bestSwarmPosition[i] - position[i]);
			
			velocity[i] = velocity[i] * INERTIA_WEIGHT + cognitiveVelocity + socialVelocity;
			sanitizeVelocity();
		}
	}
	
	/**
	 * This is to limit the velocity within a certain range.
	 * The range is defined as [-VELOCITY_BOUND,VELOCITY_BOUND] 
	 */
	private void sanitizeVelocity() {
		for (int i = 0; i < NUM_OF_ATTRIBUTES; i++) {
			if (velocity[i] > VELOCITY_BOUND) { velocity[i] = VELOCITY_BOUND; }
			if (velocity[i] < -VELOCITY_BOUND) { velocity[i] = -VELOCITY_BOUND; }
		}
	}
	
	/**
	 * Updates the position according to the velocity.
	 */
	public void updatePosition() {
		for (int i = 0; i < NUM_OF_ATTRIBUTES; i++) {
			position[i] = position[i] + velocity[i];
		}
	}
	
	/**
	 * Updates the fitness as well as the corresponding individual 
	 * best position for this particle
	 * 
	 * @param newFitness
	 * @return the best fitness of the current particle
	 */
	public double updateFitness(double newFitness) {
		if (newFitness <= fitness) { return fitness; }
		fitness = newFitness;
		bestPosition = position.clone();
		return fitness;
	}
	
	/**
	 * Gets the neighbors of the particle 
	 * 
	 * @return an integer array containing the neighbors
	 */
	public int[] getNeighbors() {
		int[] result = new int[neighbors.size()];
		for (int i = 0; i < neighbors.size(); i++) { result[i] = neighbors.get(i); }
		return result;
	}

	public double[] getVelocity() {
		return velocity;
	}

	public double[] getPosition() {
		return position;
	}
	
}
