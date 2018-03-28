package tetris.ga;

import tetris.Player;

public class GAPlayer extends Player {
    // The vector that is used as a player
    private GAParameterVector vector;
    
    /**
     * Constructor, takes in a vector
     * @param vector
     */
    public GAPlayer(GAParameterVector vector) {
        super();
        this.vector = vector;
    }
    
    @Override
    protected double[] getWeights() {
        return vector.weight;
    }
}
