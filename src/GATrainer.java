
public class GATrainer {
    private GAParameterVector[] vectorPopulation;
    private double[] fitnesses = new double[GAConfig.POPULATION_SIZE];
    
    
    public static void main(String[] args) {
        GATrainer trainer = new GATrainer();
        
        trainer.vectorPopulation = GATrainerUtils.createInitialVectorPopulation();
//        trainer.vectorPopulation = GATrainerUtils.readVectorPopulation();
        
        long startTime = System.currentTimeMillis();
        trainer.start();
        long endTime = System.currentTimeMillis();
        System.out.println("Training time: " + (endTime - startTime));
    }
    
    /**
     * Starts the training
     */
    private void start() {
        int num_iterations = GAConfig.POPULATION_SIZE;
        for (int i = 0; i < num_iterations; i++) {
            System.out.println("Iteration of vector " + i);
            runIteration(i);
        }
    }
    
    private void runIteration(int i) {
        GAParameterVector vector = vectorPopulation[i];
        fitnesses[i] = 0;
        for (int j=0; j<GAConfig.NUM_GAMES_IN_ITERATION; j++) {
            GAPlayer player = new GAPlayer(vector);
            player.play();
            //TODO: update fitness etc
        }
    }
}
