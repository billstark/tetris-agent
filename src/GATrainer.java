import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class GATrainer {
    private GAParameterVector[] vectorPopulation;
    
    
    public static void main(String[] args) {
        int num_rounds = 1;
        long startTime = System.currentTimeMillis();
        for (int i = 0; i < num_rounds; i++) {
            GATrainer trainer = new GATrainer();
//            trainer.vectorPopulation = GATrainerUtils.createInitialVectorPopulation();
            trainer.vectorPopulation = GATrainerUtils.readVectorPopulation();
            trainer.start();
        }
        
        long endTime = System.currentTimeMillis();
        System.out.println("Training time: " + (endTime - startTime) / 1000 + " seconds, " + num_rounds + " round(s) finished.");
    }
    
    /**
     * Starts the training
     */
    private void start() {
        int population_size = GAConfig.POPULATION_SIZE;
        
        // Run an iteration for every vector
        for (int i = 0; i < population_size; i++) {
            System.out.println("Iteration of vector " + i);
            runIteration(i);
        }
        
        // Create a number of child vectors with high-fitnessed parents
        int num_children = (int) (GAConfig.PORTION_CHILDREN_REPLACEMENT * population_size);
        int parent_batch_count = (int) (GAConfig.PORTION_PARENT_BATCH * population_size);
        List<GAParameterVector> parent_vec_list = Arrays.asList(vectorPopulation);
        List<GAParameterVector> child_batch = new ArrayList<GAParameterVector>();
        
        for (int i = 0; i < num_children; i++) {
            Collections.shuffle(parent_vec_list);
            List<GAParameterVector> parent_batch = parent_vec_list.subList(0, parent_batch_count);
            GAParameterVector fittest_parent_1 = parent_batch.get(0);
            GAParameterVector fittest_parent_2 = parent_batch.get(0);
            
            // Choosing the best parent_1
            for (int j = 0; j < parent_batch_count; j++) {
                GAParameterVector parent = parent_batch.get(j);
                if (parent.fitness > fittest_parent_1.fitness) {
                    fittest_parent_1 = parent;
                }
            }
            
            // Choosing the best parent_2
            for (int j = 0; j < parent_batch_count; j++) {
                GAParameterVector parent = parent_batch.get(j);
                if (parent.fitness > fittest_parent_2.fitness && parent != fittest_parent_1) {
                    fittest_parent_2 = parent;
                }
            }
            if (fittest_parent_1.fitness < 20) {
                System.out.println("Start");
                for (int k=0; k<parent_batch.size(); k++) {
                    System.out.println(parent_batch.get(k).fitness);
                }
                System.out.println("End");
            }
            
            child_batch.add(GAParameterVector.crossover(fittest_parent_1, fittest_parent_2));
        }
        
        // Sort parents, replace the worst parents with newly produced children
        Collections.sort(parent_vec_list);
        System.out.println("Best fitness in parents: " + parent_vec_list.get(population_size-1).fitness);
        GAParameterVector[] new_population = new GAParameterVector[population_size];
        int i = 0;
        for (; i < num_children; i++) {
            new_population[i] = child_batch.get(i);
        }
        for (; i < population_size; i++) {
            new_population[i] = parent_vec_list.get(i);
        }
        
        // Write to output
        GATrainerUtils.writeToOutput(new_population);
        
        // Test best parent's fitness
        GAPlayer player = new GAPlayer(vectorPopulation[population_size-1]);
        player.play(0);
        double test_fitness = player.fundamentalFitnessEvaluation();
        System.out.println("Test maximum fitness: " + test_fitness);
        
        // Average fitness
        double total_fitness = 0;
        for (GAParameterVector vec: vectorPopulation) {
            total_fitness += vec.fitness;
        }
        System.out.println("Test average fitness: " + total_fitness / population_size);
    }
    
    /**
     * Runs an iteration for a vector indexed `i`
     * @param i index of vector in population
     */
    private void runIteration(int i) {
        GAParameterVector vector = vectorPopulation[i];
        vector.fitness = 0;
        int num_games = GAConfig.NUM_GAMES_IN_ITERATION;
        int max_num_moves = GAConfig.NUM_MOVES_IN_GAME;
        for (int j = 0; j < num_games; j++) {
            GAPlayer player = new GAPlayer(vector);
            player.play(max_num_moves);
            double fitness = player.fundamentalFitnessEvaluation();
            vector.fitness += fitness;
        }
    }
}
