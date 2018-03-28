
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
	
	public double[] getParticlePostion(){
        return particle.getPosition();
    }

	@Override
    protected double[] getWeights() {
        return particle.getPosition();
    }

}
