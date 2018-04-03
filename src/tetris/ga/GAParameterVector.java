package tetris.ga;
import java.util.Random;

public class GAParameterVector implements Comparable<GAParameterVector> {
    private final Random R_GENERATOR = new Random();
    public int fitness = 0;
    
    public static final int NUM_WEIGHTS = 13;

    public double[] weight = new double[NUM_WEIGHTS];

    /**
     * Randomly initialize weight.
     */
    public GAParameterVector() {
        for (int i=0; i<NUM_WEIGHTS; i++) {
            weight[i] = R_GENERATOR.nextDouble() * 2 - 1;
        }
        normalize();
    }

    /**
     * Initialize weight from data.
     */
    public GAParameterVector(double[] weight) {
        this.weight = weight;
        normalize();
    }

    /**
     * Normalizes NUM_WEIGHTS-dimension vector `weight`.
     */
    private void normalize() {
        double sum_sqr = 0;
        for (int i=0; i<NUM_WEIGHTS; i++) {
            sum_sqr += weight[i] * weight[i];
        }
        double normalization_factor = Math.sqrt(sum_sqr);
        for (int i=0; i<NUM_WEIGHTS; i++) {
            weight[i] /= normalization_factor;
        }
    }

    /**
     * Attempts mutation on a component of vector `weight`.
     */
    private void attempt_mutate() {
        double chance = R_GENERATOR.nextDouble();
        if (chance < GAConfig.MUTATION_CHANCE) {
            mutate();
        }
    }

    /**
     * Mutates a component of vector `weight`.
     */
    private void mutate() {
        int idx = R_GENERATOR.nextInt(NUM_WEIGHTS);
        double amount = (R_GENERATOR.nextDouble() * 2 - 1) * GAConfig.MUTATION_AMOUNT_MAX;
        weight[idx] += amount;
        normalize();
    }

    /**
     * Creates a crossover using two parameter vectors
     * @param vec1 first parent vector
     * @param vec2 second parent vector
     * @return
     */
    public static GAParameterVector crossover(GAParameterVector vec1, GAParameterVector vec2) {
        double[] child_weights = new double[NUM_WEIGHTS];
        for (int i=0; i<NUM_WEIGHTS; i++) {
            child_weights[i] = vec1.weight[i] * vec1.fitness + vec2.weight[i] * vec2.fitness;
        }
        GAParameterVector child = new GAParameterVector(child_weights);
        child.attempt_mutate();
        return child;
    }

    @Override
    public int compareTo(GAParameterVector o) {
        return this.fitness - o.fitness;
    }
}
