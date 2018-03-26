import java.util.Random;

public class GAParameterVector implements Comparable<GAParameterVector> {
    private final Random R_GENERATOR = new Random();
    public int fitness = 0;

    public double[] weight = new double[7];

    /**
     * Randomly initialize weight.
     */
    public GAParameterVector() {
        for (int i=0; i<7; i++) {
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
     * Normalizes 7-dimension vector `weight`.
     */
    private void normalize() {
        double sum_sqr = 0;
        for (int i=0; i<7; i++) {
            sum_sqr += weight[i] * weight[i];
        }
        double normalization_factor = Math.sqrt(sum_sqr);
        for (int i=0; i<7; i++) {
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
        int idx = R_GENERATOR.nextInt(7);
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
        double[] child_weights = new double[7];
        for (int i=0; i<7; i++) {
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
