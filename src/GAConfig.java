public class GAConfig {
    // Population of parameter vectors
    public static final int POPULATION = 1000;
    
    // Number of games run in each iteration
    public static final int NUM_GAMES_IN_ITERATION = 100;
    
    // Number of moves performed in each game
    public static final int NUM_MOVES_IN_GAME = 500;
    
    // Portion of population chosen for selecting the two fittest parents
    public static final double PORTION_FOR_CHOOSING_FITTEST = 0.1;
    
    // Portion of population to be replaced for better population after each iteration 
    public static final double PORTION_FOR_REPLACEMENT = 0.3;
    
    // Chance of mutation for each newly produced vector
    public static final double MUTATION_CHANCE = 0.05;
    
    // Maximum amount of mutation for a component in the vector to be mutated  
    public static final double MUTATION_AMOUNT_MAX = 0.2;
}
