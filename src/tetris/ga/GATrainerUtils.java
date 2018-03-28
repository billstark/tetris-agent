package tetris.ga;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;

public class GATrainerUtils {
    public static GAParameterVector[] createInitialVectorPopulation() {
        int population_size = GAConfig.POPULATION_SIZE;
        GAParameterVector[] population = new GAParameterVector[population_size];
        for (int i=0; i<population_size; i++) {
            population[i] = new GAParameterVector();
        }
        return population;
    }

    public static GAParameterVector[] readVectorPopulation() {
        int population_size = GAConfig.POPULATION_SIZE;
        GAParameterVector[] population = new GAParameterVector[population_size];
        try {
            BufferedReader br = new BufferedReader(new FileReader(GAConfig.OUTPUT_FILE_NAME));
            for (int i=0; i<population_size; i++) {
                String[] weight_strings = br.readLine().split(" ");
                double[] weight = new double[7];
                for (int j=0; j<7; j++) {
                    weight[j] = Double.parseDouble(weight_strings[j]);
                }
                population[i] = new GAParameterVector(weight);
            }
            br.close();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        return population;
    }
    
    public static void writeToOutput(GAParameterVector[] vectors) {
        try {
            PrintWriter writer = new PrintWriter(GAConfig.OUTPUT_FILE_NAME, GAConfig.ENCODING_FORM);
            for (int i = 0; i < vectors.length; i++) {
                GAParameterVector vector = vectors[i];
                double[] weight = vector.weight;
                String[] weightsString = new String[weight.length];
                for (int j = 0; j < weight.length; j++) { weightsString[j] = Double.toString(weight[j]); }
                writer.println(String.join(" ", weightsString) + " " + vector.fitness);
            }
            writer.close();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
