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
					this.totalWeightOfHoles += (State.ROWS - 1 - row);
				}
			}
		}	
		
		this.sumOfAdjacentColumnHeightDifference = 0;
		for(int column = 1; column < State.COLS; column++) {
			this.sumOfAdjacentColumnHeightDifference += Math.abs(currentTop[column]-currentTop[column-1]);
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
	
	public double getTotalHeuristic(Particle p) {
		return 1.0 * (lineCleared * p.getPosition()[0]
				+ numberOfHoles * p.getPosition()[1]
				+ totalWeightOfHoles * p.getPosition()[2]
				+ sumOfAdjacentColumnHeightDifference *  p.getPosition()[3]
				+ landingHeight *  p.getPosition()[4]
				+ totalHeight *  p.getPosition()[5]
				+ rangeOfHeight *  p.getPosition()[6]);
	}

	public double getTotalHeuristic(int[] weight) {
		return 1.0 * (lineCleared * weight[0]
				+ numberOfHoles * weight[1]
				+ totalWeightOfHoles * weight[2]
				+ sumOfAdjacentColumnHeightDifference * weight[3]
				+ landingHeight * weight[4]
				+ totalHeight * weight[5]
				+ rangeOfHeight * weight[6]);
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