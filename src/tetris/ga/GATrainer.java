package tetris.ga;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class GATrainer {
    private GAParameterVector[] vectorPopulation;
    private static long startTime;
    private int numTrainsCompleted = 0;
    
    private static String getTimeString() {
        DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date();
        return "[" + formatter.format(date) + "]";
    }

    public static void main(String[] args) {
        int num_rounds = GAConfig.NUM_ROUNDS;
        int num_threads = GAConfig.NUM_THREADS;
        startTime = System.currentTimeMillis();
        for (int i = 0; i < num_rounds; i++) {
            System.out.println(GATrainer.getTimeString() + " Iteration " + i);
            GATrainer trainer = new GATrainer();
//            trainer.vectorPopulation = GATrainerUtils.createInitialVectorPopulation();
            trainer.vectorPopulation = GATrainerUtils.readVectorPopulation();
            trainer.start(i, num_rounds, num_threads);
        }

        long endTime = System.currentTimeMillis();
        System.out.println("Training time: " + (endTime - startTime) / 1000 / 60 + " minutes, " + num_rounds + " round(s) finished.");
    }

    /**
     * Starts the training
     */
    private void start(int current_round, int num_rounds, int num_threads) {
        int population_size = GAConfig.POPULATION_SIZE;

        Thread[] threads = new Thread[num_threads];
        int increment = vectorPopulation.length / num_threads;
        for(int i = 0; i < num_threads; i++) {
            threads[i] = new Thread(new Worker(i * increment, (i + 1) * increment, vectorPopulation, this));
            threads[i].start();
        }
        for(int i = 0; i < num_threads; i++) {
            try {
                threads[i].join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
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
        double best_result = 0;
        double total_result = 0;
        int num_test_rounds = 50;
        for (i = 0; i < num_test_rounds; i++) {
            GAPlayer player = new GAPlayer(vectorPopulation[population_size-1]);
            player.play(0);
            double test_fitness = player.fundamentalFitnessEvaluation();
            best_result = Math.max(best_result, test_fitness);
            total_result += test_fitness;
        }

        System.out.println("Test best result: " + best_result + ", test average result in " + num_test_rounds + " rounds: " + total_result / num_test_rounds);

        // Average fitness
        double total_fitness = 0;
        for (GAParameterVector vec: vectorPopulation) {
            total_fitness += vec.fitness;
        }
        System.out.println("Test average fitness: " + total_fitness / population_size);
    }
    
    public void roundComplete() {
        numTrainsCompleted ++;
        System.out.println(GATrainer.getTimeString() + " Completed " + numTrainsCompleted + " rounds.");
    }
}


class Worker implements Runnable {
    final private int minIndex;
    final private int maxIndex;
    final private GAParameterVector[] vectorPopulation;
    final private GATrainer master;

    public Worker(int minIndex, int maxIndex, GAParameterVector[] vectorPopulation, GATrainer master) {
        this.minIndex = minIndex;
        this.maxIndex = maxIndex;
        this.vectorPopulation = vectorPopulation;
        this.master = master;
    }

    public void run() {
        for(int i = minIndex; i < maxIndex; i++) {
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
            master.roundComplete();
        }
    }
}