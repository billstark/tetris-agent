package tetris.ga;
public class GAConfig {
    // Population of parameter vectors
    public static final int POPULATION_SIZE = 100;

    // Number of games run in each iteration
    public static final int NUM_GAMES_IN_ITERATION = 100;

    // Number of moves performed in each game
    public static final int NUM_MOVES_IN_GAME = 200000;

    // Portion of population chosen for selecting the two fittest parents
    public static final double PORTION_PARENT_BATCH = 0.1;

    // Portion of population to be replaced for better population after each iteration
    public static final double PORTION_CHILDREN_REPLACEMENT = 0.3;

    // Chance of mutation for each newly produced vector
    public static final double MUTATION_CHANCE = 0.05;

    // Maximum amount of mutation for a component in the vector to be mutated
    public static final double MUTATION_AMOUNT_MAX = 0.2;
    
    // Number of rounds to be scheduled
    public static final int NUM_ROUNDS = 1;
    
    // Number of threads to be created
    public static final int NUM_THREADS = 20;

    public static final String OUTPUT_FILE_NAME = "GA_train_output.txt";
    public static final String ENCODING_FORM = "UTF-8";
}
