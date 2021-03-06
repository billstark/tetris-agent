package tetris;
import java.util.Arrays;

class Heuristic {

	private int lineCleared;

	private int numberOfHoles;

	private int totalWeightOfHoles;

	private int sumOfAdjacentColumnHeightDifference;

	 //The row number of the lowest unoccupied cell that a shape placement will occupy
	private int landingHeight;

	private int totalHeight;

	private int rangeOfHeight;


	public Heuristic(int[][]currentFiled, int[] lastTop, int[] currentTop, int lineCleared) {
		this.lineCleared = lineCleared;

		this.numberOfHoles = 0;
		this.totalWeightOfHoles = 0;
		for(int column = 0; column < State.COLS; column++) {
			for(int row = currentTop[column]-1; row >= 0; row--) {
				if(currentFiled[row][column] == 0){
					this.numberOfHoles++;
					this.totalWeightOfHoles += (currentTop[column] - row);
				}
			}
		}

		this.sumOfAdjacentColumnHeightDifference = 0;
		for(int column = 1; column < State.COLS; column++) {
			this.sumOfAdjacentColumnHeightDifference += Math.pow(Math.abs(currentTop[column]-currentTop[column-1]), 2);
		}


		this.landingHeight = State.ROWS - 1;
		for(int column = 0; column < State.COLS; column++) {
			double topBeforeClear = currentTop[column] + lineCleared;
			if(lastTop[column] != topBeforeClear) {
				if(lastTop[column]< this.landingHeight) {
					this.landingHeight = lastTop[column];
				}
			}
		}

		this.totalHeight = 0;
		for(int column = 0; column < State.COLS; column++) {
			this.totalHeight += currentTop[column];
		}

		this.rangeOfHeight = Arrays.stream(currentTop).max().getAsInt() -  Arrays.stream(currentTop).min().getAsInt();
	}

	public double getTotalHeuristic(double[] weight) {
		return 1.0 * (lineCleared * weight[0]
				+ numberOfHoles * weight[1]
				+ sumOfAdjacentColumnHeightDifference * weight[2]		
				+ totalHeight * weight[3]);
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
