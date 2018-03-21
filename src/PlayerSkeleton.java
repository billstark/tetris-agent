import java.util.Arrays;

public class PlayerSkeleton {
	
	private final double fai = 0.72; //inertia weight
	private final double c1 = 1.42;	 //cognitive term
	private final double c2 = 1.42;	 //social term
	private final double vmax = 0.5; //velocity max
	
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
	 * weight of line cleared
	 * weight of number of holes
	 * weight of total weight of holes
	 * weight of sum of adjacent column height difference
	 * weight of landing height
	 * weight of total height
	 * weight of range of height
	 */
	private double[] weights;
	
	public Particle(double[] initialWeights) {
		this.setWeights(initialWeights);
	}

	public double[] getWeights() {
		return weights;
	}

	public void setWeights(double[] weights) {
		this.weights = weights;
	}
}

class Heuristics {
	private int lineCleared;
	
	private int numberOfHoles;
	
	private int totalWeightOfHoles;
	
	private int sumOfAdjacentColumnHeightDifference;
	
	private int landingHeight; //The row number of the lowest unoccupied cell that a shape placement will occupy
	
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
			if(thisState.getTop()[column] != nextState.getTop()[column]) {
				if(nextState.getTop()[column] - 1 < this.landingHeight) {
					//TODO
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
		return 1.0 * (lineCleared * p.getWeights()[0]
				+ numberOfHoles *  p.getWeights()[1]
				+ totalWeightOfHoles *  p.getWeights()[2]
				+ sumOfAdjacentColumnHeightDifference *  p.getWeights()[3]
				+ landingHeight *  p.getWeights()[4]
				+ totalHeight *  p.getWeights()[5]
				+ rangeOfHeight *  p.getWeights()[6]);
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