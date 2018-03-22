import java.util.Arrays;
import java.util.Random;

public class PlayerSkeleton {
	
	public final static double INERTIA_WEIGHT = 0.72;		//inertia weight
	public final static double COGNITIVE_TERM = 1.42;	 	//cognitive term
	public final static double SOCIAL_TERM = 1.42;	 	//social term
	public final static double MAX_VELOCITY = 0.5; 		//velocity max
	
	//implement this function to have a working system
	public int pickMove(State s, int[][] legalMoves) {
		
		return 0;
	}
	
	public static void main(String[] args) {
		State s = new State();
		new TFrame(s);
		PlayerSkeleton p = new PlayerSkeleton();
		while(!s.hasLost()) {
			s.makeMove(p.pickMove(s,s.legalMoves()));
			s.draw();
			s.drawNext(0,0);
			try {
				Thread.sleep(300);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		System.out.println("You have completed "+s.getRowsCleared()+" rows.");
	}
	
}

class Particle {
	/*
	 * The position is the weight in the heuristic
	 * weight of line cleared
	 * weight of number of holes
	 * weight of total weight of holes
	 * weight of sum of adjacent column height difference
	 * weight of landing height
	 * weight of total height
	 * weight of range of height
	 */
	private double[] position; 
	private double[] velocity;
	
	public Particle(double[] initialPosition, double[] initialVelocity) {
		setPosition(initialPosition);
		setVelocity(initialVelocity);
	}

	public double[] getPosition() {
		return position;
	}

	public void setPosition(double[] position) {
		this.position = position;
	}

	public double[] getVelocity() {
		return velocity;
	}

	public void setVelocity(double[] velocity) {
		this.velocity = velocity;
	}
	
	public void updateVelocityAndPosition(double[] bestIndividualPostion, double[] bestSwarmPosition){
		Random r = new Random();
		
		for(int i=0;i<position.length;i++){
			velocity[i] = velocity[i] * PlayerSkeleton.INERTIA_WEIGHT 
					+ PlayerSkeleton.COGNITIVE_TERM * r.nextDouble() * (bestIndividualPostion[i] - position[i])
					+ PlayerSkeleton.SOCIAL_TERM * r.nextDouble() * (bestSwarmPosition[i] - position[i]);
			
			position[i] += velocity[i];
		}
	}
	
}

class Heuristics {
	private int lineCleared;
	
	private int numberOfHoles;
	
	private int totalWeightOfHoles;
	
	private int sumOfAdjacentColumnHeightDifference;
	
	 //The row number of the lowest unoccupied cell that a shape placement will occupy
	private int landingHeight;
	
	private int totalHeight;
	
	private int rangeOfHeight;
	
	
	public Heuristics(State thisState, State nextState) {
		this.lineCleared = nextState.getRowsCleared();
		
		this.numberOfHoles = 0;
		this.totalWeightOfHoles = 0;
		for(int column = 0; column < State.COLS; column++) {
			for(int row = nextState.getTop()[column]-1; row >= 0; row--) {
				if(nextState.getField()[row][column] == 0){
					this.numberOfHoles++;
					this.totalWeightOfHoles += (State.ROWS - 1 - row);
				}
			}
		}	
		
		this.sumOfAdjacentColumnHeightDifference = 0;
		for(int column = 1; column < State.COLS; column++) {
			this.sumOfAdjacentColumnHeightDifference += Math.abs(nextState.getTop()[column]-nextState.getTop()[column-1]);
		}

		
		this.landingHeight = State.ROWS - 1;
		for(int column = 0; column < State.COLS; column++) {
			double topBeforeClear = nextState.getTop()[column] + nextState.getRowsCleared() - thisState.getRowsCleared();
			if(thisState.getTop()[column] != topBeforeClear) {
				if(thisState.getTop()[column]< this.landingHeight) {
					this.landingHeight = thisState.getTop()[column];
				}
			}
		}
		
		this.totalHeight = 0;
		for(int column = 0; column < State.COLS; column++) {
			this.totalHeight += nextState.getTop()[column];
		}
		
		this.rangeOfHeight = Arrays.stream(nextState.getTop()).max().getAsInt() -  Arrays.stream(nextState.getTop()).min().getAsInt();
	}
	
	public double getTotalHeuristic(Particle p) {
		return 1.0 * (lineCleared * p.getPosition()[0]
				+ numberOfHoles *  p.getPosition()[1]
				+ totalWeightOfHoles *  p.getPosition()[2]
				+ sumOfAdjacentColumnHeightDifference *  p.getPosition()[3]
				+ landingHeight *  p.getPosition()[4]
				+ totalHeight *  p.getPosition()[5]
				+ rangeOfHeight *  p.getPosition()[6]);
	}
	
	public int getLineCleared() {
		return lineCleared;
	}
	
	public void setLineCleared(int lineCleared) {
		this.lineCleared = lineCleared;
	}

	public int getNumberOfHoles() {
		return numberOfHoles;
	}

	public void setNumberOfHoles(int numberOfHoles) {
		this.numberOfHoles = numberOfHoles;
	}

	public int getTotalHeightOfHoles() {
		return totalWeightOfHoles;
	}

	public void setTotalHeightOfHoles(int totalWeightOfHoles) {
		this.totalWeightOfHoles = totalWeightOfHoles;
	}

	public int getSumOfAdjacentColumnHeightDifference() {
		return sumOfAdjacentColumnHeightDifference;
	}

	public void setSumOfAdjacentColumnHeightDifference(int sumOfAdjacentColumnHeightDifference) {
		this.sumOfAdjacentColumnHeightDifference = sumOfAdjacentColumnHeightDifference;
	}

	public int getLandingHeight() {
		return landingHeight;
	}

	public void setLandingHeight(int landingHeight) {
		this.landingHeight = landingHeight;
	}

	public int getTotalHeight() {
		return totalHeight;
	}

	public void setTotalHeight(int totalHeight) {
		this.totalHeight = totalHeight;
	}
	
	public int getRangeOfHeight() {
		return rangeOfHeight;
	}

	public void setRangeOfHeight(int rangeOfHeight) {
		this.rangeOfHeight = rangeOfHeight;
	}
}