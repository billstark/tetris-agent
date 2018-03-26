import java.util.Random;

public class GAParameterVector {
    private final Random R_GENERATOR = new Random();

    private double[] weight = new double[7];

    /**
     * Randomly initialize weight.
     */
    public GAParameterVector() {
        for (int i=0; i<7; i++) {
            weight[i] = R_GENERATOR.nextDouble();
        }
        normalize();
    }
    
    /**
     * Initialize weight from data.
     */
    public GAParameterVector(double[] weight) {
        this.weight = weight; 
//        normalize();
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
    public void attempt_mutate() {
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
}
