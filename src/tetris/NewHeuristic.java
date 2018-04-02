package tetris;

public class NewHeuristic {

	private int removedRows;
	private int pileHeight;
	private int holeCount;
	private int connectedHoleCount;
	private int altitudeDifference;
	private int maxWellDepth;
	private int sumOfWellDepth;
	private int landingHeight;
	private int blockCount;
	private int weightedBlockCount;
	private int rowTransitions;
	private int columnTransitions;
	private int erodedPieceCount;
	
	public NewHeuristic(int[][]field, int[] lastTop, int[] currentTop, int rowsCleared) {
		setRemovedRows(rowsCleared);
		setPileHeight(currentTop);
		setHoleCount(field, currentTop);
		setConnectedHoleCount(field, currentTop);
		setAltitudeDifference(currentTop);
		setMaxWellDepth(currentTop);
		setSumOfWellDepth(currentTop);
		setLandingHeight(rowsCleared, lastTop, currentTop);
		setBlockCount(field);
		setWeightedBlockCount(field);
		setRowTransition(field);
		setColumnTransition(field);
		setErodedPieceCount(lastTop, currentTop, rowsCleared);
	}
	
	public double getScore(double[] weight) {
		return removedRows * weight[0] +
				pileHeight * weight[1] +
				holeCount * weight[2] +
				connectedHoleCount * weight[3] +
				altitudeDifference * weight[4] +
				maxWellDepth * weight[5] +
				sumOfWellDepth * weight[6] +
				landingHeight * weight[7] +
				blockCount * weight[8] +
				weightedBlockCount * weight[9] +
				rowTransitions * weight[10] +
				columnTransitions * weight[11] +
				erodedPieceCount * weight[12];
	}
	
	private void setRemovedRows(int rowsCleared) {
		removedRows = rowsCleared;
	}
	
	private void setPileHeight(int[] currentTop) {
		int max = Integer.MIN_VALUE;
		for (int i = 0; i < currentTop.length; i++) { max = Math.max(max, currentTop[i]); }
		pileHeight = max;
	}
	
	private void setHoleCount(int[][] field, int[] currentTop) {
		int numOfHoles = 0;
		for(int column = 0; column < State.COLS; column++) {
			for(int row = currentTop[column] - 1; row >= 0; row--) {
				if(field[row][column] == 0) { numOfHoles++; }
			}
		}
		holeCount = numOfHoles;
	}
	
	private void setConnectedHoleCount(int[][] field, int[] currentTop) {
		int numOfHoles = 0;
		for(int column = 0; column < State.COLS; column++) {
			for(int row = currentTop[column] - 1; row >= 0; row--) {
				if(field[row][column] == 0) {
					while(row >= 0 && field[row][column] == 0) {
						row--;
					}
					row++;
					numOfHoles++;
				}
			}
		}
		connectedHoleCount = numOfHoles;
	}
	
	private void setAltitudeDifference(int[] currentTop) {
		int minHeight = Integer.MAX_VALUE;
		int maxHeight = Integer.MIN_VALUE;
		for (int i = 0; i < currentTop.length; i++) {
			minHeight = Math.min(minHeight, currentTop[i]);
			maxHeight = Math.max(maxHeight, currentTop[i]);
		}
		altitudeDifference = maxHeight - minHeight;
	}
	
	private void setMaxWellDepth(int[] currentTop) {
		int maxDepth = Integer.MIN_VALUE;
		for (int i = 0; i < currentTop.length - 1; i++) {
			maxDepth = Math.max(maxDepth, Math.abs(currentTop[i] - currentTop[i + 1]));
		}
		maxWellDepth = maxDepth;
	}
	
	private void setSumOfWellDepth(int[] currentTop) {
		int sumWellDepth = 0;
		for (int i = 0; i < currentTop.length - 1; i++) {
			sumWellDepth += Math.abs(currentTop[i] - currentTop[i + 1]);
		}
		sumOfWellDepth = sumWellDepth;
	}
	
	private void setLandingHeight(int linesCleared, int[] previousTop, int[] currentTop) {
		int height = State.ROWS - 1;
		for(int column = 0; column < State.COLS; column++) {
			double topBeforeClear = currentTop[column] + linesCleared;
			if(previousTop[column] != topBeforeClear) {
				height = Math.min(height, previousTop[column]);
			}
		}
	}
	
	private void setBlockCount(int[][] field) {
		int count = 0;
		for (int col = 0; col < State.COLS; col++) {
			for (int row = 0; row < State.ROWS; row++) {
				if (field[row][col] == 0) { continue; }
				count++;
			}
		}
		blockCount = count;
	}
	
	private void setWeightedBlockCount(int[][] field) {
		int total = 0;
		for (int col = 0; col < State.COLS; col++) {
			for (int row = 0; row < State.ROWS; row++) {
				if (field[row][col] == 0) { continue; }
				total += row;
			}
		}
		weightedBlockCount = total;
	}
	
	private void setRowTransition(int[][] field) {
		int count = 0;
		for (int row = 0; row < State.ROWS; row++) {
			boolean occupied = true;
			for (int col = 0; col < State.COLS; col++) {
				if ((field[row][col] > 0) != occupied) {
					occupied = !occupied;
					count++;
				}
			}
			if (!occupied) { count++; }
		}
		rowTransitions = count;
	}
	
	private void setColumnTransition(int[][] field) {
		int count = 0;
		for (int col = 0; col < State.COLS; col++) {
			boolean occupied = true;
			for (int row = 0; row < State.ROWS; row++) {
				if ((field[row][col] > 0) != occupied) {
					occupied = !occupied;
					count++;
				}
			}
		}
		columnTransitions = count;
	}
	
	private void setErodedPieceCount(int[] previousTop, int[] currentTop, int linesCleared) {
		int usedUnits = 0;
		for (int i = 0; i < State.COLS; i++) {
			int beforeCleared = currentTop[i] + linesCleared;
			if (beforeCleared == previousTop[i]) { continue; }
			usedUnits += Math.min(linesCleared, beforeCleared - previousTop[i]);
		}
		erodedPieceCount = usedUnits * linesCleared;
	}
	
}
