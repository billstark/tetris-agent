package tetris.pso;

import tetris.Player;

public class ParticlePlayer extends Player {
	// The particle that is used as a player
	private Particle particle;
	
	/**
     * Constructor, takes in a particle
     * @param particle
     */
    public ParticlePlayer(Particle particle) {
        super();
        this.particle = particle;
    }

	@Override
    protected double[] getWeights() {
        return particle.getPosition();
    }

}
