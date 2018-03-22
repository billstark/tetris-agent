import java.util.Arrays;
import java.util.Random;

public class PlayerSkeleton {	
	
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