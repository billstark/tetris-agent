import java.util.Arrays;

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

class Heuristics{
	private int lineCleared;
	
	private double weightOfLineCleared;
	
	private int numberOfHoles;
	
	private double weightOfNumberOfHoles;
	
	private int totalWeightOfHoles;
	
	private double weightOfTotalWeightOfHoles;
	
	private int sumOfAdjacentColumnHeightDifference;
	
	private double weightOfSumOfAdjacentColumnHeightDifference;
	
	private int landingHeight;
	
	private double weightOfLandingHeight;
	
	private int totalHeight;
	
	private double weightOfTotalHeight;
	
	private int rangeOfHeight;
	
	private double weightOfRangeOfHeight;
	
	
	public Heuristics(State thisState, State nextState) {
		this.lineCleared = nextState.getRowsCleared();
		
		this.numberOfHoles = 0;
		this.totalWeightOfHoles = 0;
		for(int column = 0; column < State.COLS; column++) {
			for(int row = nextState.getTop()[column]-1; row >= 0; row--) {
				if(nextState.getField()[row][column] == 0){
					this.numberOfHoles++;
					this.totalWeightOfHoles += (20 - row);
				}
			}
		}	
		
		this.sumOfAdjacentColumnHeightDifference = 0;
		for(int column = 1; column < State.COLS; column++) {
			this.sumOfAdjacentColumnHeightDifference += Math.abs(nextState.getTop()[column]-nextState.getTop()[column-1]);
		}
		
		//the height of bottom gird 
		this.landingHeight = State.ROWS;
		for(int column = 0; column < State.COLS; column++) {
			if(thisState.getTop()[column] != nextState.getTop()[column]) {
				if(nextState.getTop()[column] - 1 < this.landingHeight) {
					this.landingHeight = nextState.getTop()[column];
				}
			}
		}
		
		this.totalHeight = 0;
		for(int column = 0; column < State.COLS; column++) {
			this.totalHeight += nextState.getTop()[column];
		}
		
		this.rangeOfHeight = Arrays.stream(nextState.getTop()).max().getAsInt() -  Arrays.stream(nextState.getTop()).min().getAsInt();
}
	
	public void initializeWeight() {
		this.weightOfLineCleared = 0.5;
		this.weightOfNumberOfHoles = 0.5;
		this.weightOfTotalWeightOfHoles = 0.5;
		this.weightOfSumOfAdjacentColumnHeightDifference = 0.5;
		this.weightOfLandingHeight = 0.5;
		this.weightOfTotalHeight = 0.5;
		this.weightOfRangeOfHeight = 0.5;
	}
	
	public double getTotalHeuristic() {
		return 1.0 * (lineCleared * weightOfLineCleared
				+ numberOfHoles * weightOfNumberOfHoles
				+ totalWeightOfHoles * weightOfTotalWeightOfHoles
				+ sumOfAdjacentColumnHeightDifference * weightOfSumOfAdjacentColumnHeightDifference
				+ landingHeight * weightOfLandingHeight
				+ totalHeight * weightOfTotalHeight
				+ rangeOfHeight * weightOfRangeOfHeight);
	}
	
	public int getLineCleared() {
		return lineCleared;
	}
	
	public void setLineCleared(int lineCleared) {
		this.lineCleared = lineCleared;
	}
	
	public double getWeightOfLineCleared() {
		return weightOfLineCleared;
	}

	public void setWeightOfLineCleared(double weightOfLineCleared) {
		this.weightOfLineCleared = weightOfLineCleared;
	}

	public int getNumberOfHoles() {
		return numberOfHoles;
	}

	public void setNumberOfHoles(int numberOfHoles) {
		this.numberOfHoles = numberOfHoles;
	}

	public double getWeightOfNumberOfHoles() {
		return weightOfNumberOfHoles;
	}

	public void setWeightOfNumberOfHoles(double weightOfNumberOfHoles) {
		this.weightOfNumberOfHoles = weightOfNumberOfHoles;
	}

	public int getTotalHeightOfHoles() {
		return totalWeightOfHoles;
	}

	public void setTotalHeightOfHoles(int totalWeightOfHoles) {
		this.totalWeightOfHoles = totalWeightOfHoles;
	}

	public double getWeightsOfTotalWeightOfHoles() {
		return weightOfTotalWeightOfHoles;
	}

	public void setWeightsOfTotalHeightOfHoles(double weightOfTotalWeightOfHoles) {
		this.weightOfTotalWeightOfHoles = weightOfTotalWeightOfHoles;
	}

	public int getSumOfAdjacentColumnHeightDifference() {
		return sumOfAdjacentColumnHeightDifference;
	}

	public void setSumOfAdjacentColumnHeightDifference(int sumOfAdjacentColumnHeightDifference) {
		this.sumOfAdjacentColumnHeightDifference = sumOfAdjacentColumnHeightDifference;
	}

	public double getWeightOfSumOfAdjacentColumnHeightDifference() {
		return weightOfSumOfAdjacentColumnHeightDifference;
	}

	public void setWeightOfSumOfAdjacentColumnHeightDifference(double weightOfSumOfAdjacentColumnHeightDifference) {
		this.weightOfSumOfAdjacentColumnHeightDifference = weightOfSumOfAdjacentColumnHeightDifference;
	}

	public int getLandingHeight() {
		return landingHeight;
	}

	public void setLandingHeight(int landingHeight) {
		this.landingHeight = landingHeight;
	}

	public double getWeightOfLandingHeight() {
		return weightOfLandingHeight;
	}

	public void setWeightOfLandingHeight(double weightOfLandingHeight) {
		this.weightOfLandingHeight = weightOfLandingHeight;
	}

	public int getTotalHeight() {
		return totalHeight;
	}

	public void setTotalHeight(int totalHeight) {
		this.totalHeight = totalHeight;
	}

	public double getWeightOfTotalHeight() {
		return weightOfTotalHeight;
	}

	public void setWeightOfTotalHeight(double weightOfTotalHeight) {
		this.weightOfTotalHeight = weightOfTotalHeight;
	}

	public int getRangeOfHeight() {
		return rangeOfHeight;
	}

	public void setRangeOfHeight(int rangeOfHeight) {
		this.rangeOfHeight = rangeOfHeight;
	}

	public double getWeightOfRangeOfHeight() {
		return weightOfRangeOfHeight;
	}

	public void setWeightOfRangeOfHeight(double weightOfRangeOfHeight) {
		this.weightOfRangeOfHeight = weightOfRangeOfHeight;
	}
	
}