
public class PlayerSkeleton {

	private final double[] WEIGHT = {
			0.6816800738276745,
			0.5588151488560493,
			-3.1031901911592934,
			-4.0657302433854685,
			-0.9294522666278708,
			-0.9983263248003504,
			-0.4915038312098661,
			-3.2275968857355286, 
			0.008856427717670508,
			-0.03773914898487388, 
			-2.596685029585629, 
			-3.7290379638324547, 
			1.645615379270798
	};
	
	public int pickMove(State s, int[][] legalMoves) {
		int bestMove = -1;
		double maxScore = Integer.MIN_VALUE;
		
		// For every possible legal move, we evaluate the score of that
		// move using the parameters from particle object
		for (int i = 0; i < s.legalMoves().length; i++) {

			// Gets orientation and slot of the current move
			int orientation = legalMoves[i][State.ORIENT];
			int slot = legalMoves[i][State.SLOT];

			// Have a copy of the current game board
			int[][] currentBoard = new int[s.getField().length][];
			for (int j = 0; j < currentBoard.length; j++) { currentBoard[j] = s.getField()[j].clone(); }
			
			double score = testMoveInExpectimax(s, orientation, slot, s.getNextPiece(), currentBoard, s.getTop().clone());
			if(score > maxScore) {
				maxScore = score;
				bestMove = i;
			}
		}

		if(bestMove == -1)
			return 0;

		return bestMove;
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
	 
	
	/**
	 * The method is to compute the heuristic
	 * @param state
	 * @param orient
	 * @param slot
	 * @param nextPiece
	 * @param gameBoard
	 * @param top
	 * @return
	 */
	private double testMoveInExpectimax(State state, int orient, int slot, int nextPiece, int[][] gameBoard,int[] top) {
		/*
		 * make the first move
		 * get the number of rows cleared
		 * the state, game board and top are modified as well
		 */
		int rowsCleared = makeMove(state, orient, slot, nextPiece, gameBoard, top, 1);
		 
	    double totalScore = 0;
	    int successfulMove = 0;
	    //test next move after this move is made
	    for(int nextNextPiece = 0; nextNextPiece < State.N_PIECES; nextNextPiece++) {
	    	double secondMaxScore = Integer.MIN_VALUE;
	     		
	    	for(int i=0;i<State.legalMoves[nextNextPiece].length;i++) {
	    		// Gets orientation and slot of the current move
	    		int nextOrientation = State.legalMoves[nextNextPiece][i][State.ORIENT];
	    		int nextSlot = State.legalMoves[nextNextPiece][i][State.SLOT];
					
	    		// Have a copy of the current game board
	    		int[][] currentBoard = new int[gameBoard.length][];
	    		for (int j = 0; j < currentBoard.length; j++) { currentBoard[j] = gameBoard[j].clone(); }
				
	    		double score = testMove(state, nextOrientation, nextSlot, nextNextPiece, currentBoard, top.clone(), top.clone(), 2);
					
	    		//max player chooses the maximum value
	    		if(score > secondMaxScore) secondMaxScore = score;
	    	}
	 		
	    	if(secondMaxScore > Integer.MIN_VALUE) {
	    		successfulMove++;
	    		totalScore += secondMaxScore;
	    	}
	    }
	    
	    Heuristic currentHeuristic = new Heuristic(gameBoard, state.getTop(), top, rowsCleared);
	    if(successfulMove == 0)
	    	return Integer.MIN_VALUE;
	    return 1.0*totalScore/successfulMove + currentHeuristic.getScore(WEIGHT);
	}
	 
	/**
	 * 
	 * @param state
	 * @param orient
	 * @param slot
	 * @param nextPiece
	 * @param gameBoard
	 * @param top
	 * @param lastTop
	 * @param turnNumber
	 * @return
	 */
	private double testMove(State state, int orient, int slot, int nextPiece, int[][] gameBoard, int[] top, int[] lastTop, int turnNumber) {
		int rowsCleared = makeMove(state, orient, slot, nextPiece, gameBoard, top, turnNumber);;
		Heuristic stateEvaluator = new Heuristic(gameBoard, lastTop, top, rowsCleared);
		return stateEvaluator.getScore(WEIGHT);
	 }
	 
	/**
	 * 
	 * @param state
	 * @param orient
	 * @param slot
	 * @param nextPiece
	 * @param gameBoard
	 * @param top
	 * @param turnNumber
	 * @return
	 */
	private int makeMove(State state, int orient, int slot, int nextPiece, int[][] gameBoard, int[] top, int turnNumber) {
		int height = top[slot] - State.getpBottom()[nextPiece][orient][0];

		for (int c = 1; c < State.getpWidth()[nextPiece][orient]; c++) {
			height = Math.max(height, top[slot + c] - State.getpBottom()[nextPiece][orient][c]);
		}

		// check if game ended.
		if (height + State.getpHeight()[nextPiece][orient] >= State.ROWS) { return Integer.MIN_VALUE; }

		// for each column in the piece - fill in the appropriate blocks
		for (int i = 0; i < State.getpWidth()[nextPiece][orient]; i++) {

			// from bottom to top of brick
			for (int h = height + State.getpBottom()[nextPiece][orient][i]; h < height + State.getpTop()[nextPiece][orient][i]; h++) {
				gameBoard[h][i + slot] = state.getTurnNumber() + turnNumber;
			}
		}

		// adjust top
		for (int c = 0; c < State.getpWidth()[nextPiece][orient]; c++) {
			top[slot + c] = height + State.getpTop()[nextPiece][orient][c];
		}
		 
		int rowsCleared = 0;

		//check for full rows - starting at the top
		for (int r = height + State.getpHeight()[nextPiece][orient] - 1; r >= height; r--) {
			//check all columns in the row
			boolean full = true;
			for (int c = 0; c < State.COLS; c++) {
				if (gameBoard[r][c] == 0) {
					full = false;
					break;
				}
			}

			//if the row was full - remove it and slide above stuff down
			if (full) {
				rowsCleared++;

				//for each column
				for (int c = 0; c < State.COLS; c++) {

					//slide down all bricks
					for (int i = r; i < top[c]; i++) {
						gameBoard[i][c] = gameBoard[i + 1][c];
					}
					//lower the top
					top[c]--;
					while (top[c] >= 1 && gameBoard[top[c] - 1][c] == 0) { top[c]--; }
				}
			}
		}
		return rowsCleared;
	}
	
	class Heuristic {

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
		
		public Heuristic(int[][]field, int[] lastTop, int[] currentTop, int rowsCleared) {
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
		
		
		public int getColumnTransition(){
			return columnTransitions;
		}
		
		public int getRowTransition(){
			return rowTransitions;
		}
		
		public int getPileHeight(){
			return pileHeight;
		}
		
		public int getHoleCount(){
			return holeCount;
		}
	}
}
