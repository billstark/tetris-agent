package tetris;

public class PlayerSkeleton {

	private final double[] WEIGHT = {
			1.1970115597151016,
			-0.6860168418731316,
			-0.7330712382807072,
			-1.5889448337710694,
			-0.25088491435447813,
			-0.15260870093226603,
			-0.1072981657226463,
			-0.9329225453068959,
			-0.018785370266244772,
			0.007229001225192391,
			-1.3826829895407082,
			-2.7828523911211263,
			1.0130535867332262
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

			// Have a copy of the current top
			int[] currentTop = s.getTop().clone();
			
			int testCleared = testMove(orientation, slot, s.getNextPiece(), currentBoard, currentTop,s);
            if (testCleared < 0) { continue; }
            
            NewHeuristic stateEvaluator = new NewHeuristic(currentBoard, s.getTop(), currentTop, testCleared );

            double score = stateEvaluator.getScore(WEIGHT);

            // Updates best score and best move
            if (score > maxScore) {
            	maxScore = score;
                bestMove = i;
            }
			//max player choose the maximum value
//			double averageScore = testMove(maxScore,s, orientation, slot, s.getNextPiece(), currentBoard, currentTop);
//			if(averageScore > maxScore) {
//				maxScore = averageScore;
//				bestMove = i;
//			}
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
				Thread.sleep(0);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		System.out.println("You have completed "+s.getRowsCleared()+" rows.");
	}
	
	
	private int testMove(int orient, int slot, int nextPiece, int[][] gameBoard, int[] top, State state) {

        int height = top[slot] - State.getpBottom()[nextPiece][orient][0];

        for (int c = 1; c < State.getpWidth()[nextPiece][orient]; c++) {
            height = Math.max(height, top[slot + c] - State.getpBottom()[nextPiece][orient][c]);
        }

        // check if game ended. If game ends, just give -1 as output
        if (height + State.getpHeight()[nextPiece][orient] >= State.ROWS) { return -1; }

        // for each column in the piece - fill in the appropriate blocks
        for (int i = 0; i < State.getpWidth()[nextPiece][orient]; i++) {

            // from bottom to top of brick
            for (int h = height + State.getpBottom()[nextPiece][orient][i]; h < height + State.getpTop()[nextPiece][orient][i]; h++) {
                gameBoard[h][i + slot] = state.getTurnNumber() + 1;
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
	
	 private double testMove(double maxScore, State state, int orient, int slot, int nextPiece, int[][] gameBoard,int[] top) {
	    	
 		int height = top[slot] - State.getpBottom()[nextPiece][orient][0];

     for (int c = 1; c < State.getpWidth()[nextPiece][orient]; c++) {
         height = Math.max(height, top[slot + c] - State.getpBottom()[nextPiece][orient][c]);
     }

     // check if game ended. If game ends, just give -1 as output
     if (height + State.getpHeight()[nextPiece][orient] >= State.ROWS) { return (double) Integer.MIN_VALUE; }

     // for each column in the piece - fill in the appropriate blocks
     for (int i = 0; i < State.getpWidth()[nextPiece][orient]; i++) {

         // from bottom to top of brick
         for (int h = height + State.getpBottom()[nextPiece][orient][i]; h < height + State.getpTop()[nextPiece][orient][i]; h++) {
             gameBoard[h][i + slot] = state.getTurnNumber() + 1;
         }
     }

     // adjust top
     for (int c = 0; c < State.getpWidth()[nextPiece][orient]; c++) {
         top[slot + c] = height + State.getpTop()[nextPiece][orient][c];
     }

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
     	
     double totalScore = 0;
     double minScore = Integer.MAX_VALUE;
     
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
				
				// Have a copy of the current top
				int[] currentTop = top.clone();
			
				double score = testMove(state, nextOrientation, nextSlot, nextNextPiece, currentBoard, currentTop, currentTop);
				
				totalScore += score;
				
				//max player chooses the minimum value
				if(score > secondMaxScore) secondMaxScore = score;
//				//prune
//				if(secondMaxScore > minScore) break;
			}
// 			if(secondMaxScore < minScore) minScore = secondMaxScore;
// 			if(minScore < maxScore) break;
 			totalScore += secondMaxScore;
     	}
     	
		return 1.0*totalScore/State.N_PIECES;
	}
	 
	 private double testMove(State state, int orient, int slot, int nextPiece, int[][] gameBoard, int[] top, int[] lastTop) {

	        int height = top[slot] - State.getpBottom()[nextPiece][orient][0];

	        for (int c = 1; c < State.getpWidth()[nextPiece][orient]; c++) {
	            height = Math.max(height, top[slot + c] - State.getpBottom()[nextPiece][orient][c]);
	        }

	        // check if game ended. If game ends, just give -1 as output
	        if (height + State.getpHeight()[nextPiece][orient] >= State.ROWS) { return -1; }

	        // for each column in the piece - fill in the appropriate blocks
	        for (int i = 0; i < State.getpWidth()[nextPiece][orient]; i++) {

	            // from bottom to top of brick
	            for (int h = height + State.getpBottom()[nextPiece][orient][i]; h < height + State.getpTop()[nextPiece][orient][i]; h++) {
	                gameBoard[h][i + slot] = state.getTurnNumber() + 2;
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

	        NewHeuristic stateEvaluator = new NewHeuristic(gameBoard, lastTop, top, rowsCleared);
	        double score = stateEvaluator.getScore(WEIGHT);
	        
	        return score;
	    }
}