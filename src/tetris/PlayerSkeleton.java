package tetris;

public class PlayerSkeleton {

	private final double[] WEIGHT = {
	        0.48179933669554514,
            0.17644627028188448,
            -0.5137317157674215,
            -0.19417141271325553,
            -0.17415790148533303,
            -0.13431501249055217,
            0.005357479914635892,
            -0.23009808871158943,
            0.08940318162035248,
            -0.01911061633475509,
            -0.3168280168487193,
            -0.47423985368979776,
            0.011242494586231349
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
			
//			s.draw();
//			s.clearNext();
//			s.drawNext(slot, orientation);

			// Have a copy of the current game board
			int[][] currentBoard = new int[s.getField().length][];
			for (int j = 0; j < currentBoard.length; j++) { currentBoard[j] = s.getField()[j].clone(); }
			
//			double score = testMove(s, orientation, slot,  s.getNextPiece(), currentBoard, s.getTop().clone(), s.getTop().clone(), 1);
//			double score = testMoveInMinmax(maxScore, s, orientation, slot, s.getNextPiece(), currentBoard, s.getTop().clone());
			double score = testMoveInExpectimax( s, orientation, slot, s.getNextPiece(), currentBoard, s.getTop().clone());
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
		long best = Integer.MIN_VALUE;
		for(int i=0;i<5;i++) {
			State s = new State();
			PlayerSkeleton p = new PlayerSkeleton();
			long rowCleared = 0;
			while(!s.hasLost()) {
				s.makeMove(p.pickMove(s,s.legalMoves()));
				if(s.getRowsCleared()%1000 == 0 && rowCleared!=s.getRowsCleared()) {
					rowCleared = s.getRowsCleared();
					System.out.println(rowCleared);
				}
			}
			if(s.getRowsCleared() > best) best = s.getRowsCleared();
			System.out.println("Round "+ (i+1) +" completed "+s.getRowsCleared()+" rows.");
			}
		System.out.println("Best: "+ best+" rows.");
	}
	
	private double testMoveInMinmax(double maxScore, State state, int orient, int slot, int nextPiece, int[][] gameBoard,int[] top) {
		moveOneBlock(state, orient, slot, nextPiece, gameBoard, top, 1);
	    
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
			
				double score = testMove(state, nextOrientation, nextSlot, nextNextPiece, currentBoard, top.clone(), top.clone(), 2);
				
				//max player chooses the maximum value
				if(score > secondMaxScore) secondMaxScore = score;
				if(secondMaxScore > minScore) break;
			}
 			if(secondMaxScore < minScore) minScore = secondMaxScore;
 			if(minScore < maxScore) break;
     	}
     	return minScore;
	 }
	 
	private double testMoveInExpectimax(State state, int orient, int slot, int nextPiece, int[][] gameBoard,int[] top) {
		 int rowsCleared = moveOneBlock(state, orient, slot, nextPiece, gameBoard, top, 1);
		 
	     double totalScore = 0;
	     int success = 0;
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
 				 success++;
 				 totalScore += secondMaxScore;
 			 }
	     }
	     NewHeuristic currentHeuristic = new NewHeuristic(gameBoard, state.getTop(), top, rowsCleared);
	     if(success == 0)
	    	 return Integer.MIN_VALUE;
	     return 1.0*totalScore/success + currentHeuristic.getScore(WEIGHT);
	 }
	 
	private double testMove(State state, int orient, int slot, int nextPiece, int[][] gameBoard, int[] top, int[] lastTop, int turnNumber) {
	        int rowsCleared = moveOneBlock(state, orient, slot, nextPiece, gameBoard, top, turnNumber);;
	        NewHeuristic stateEvaluator = new NewHeuristic(gameBoard, lastTop, top, rowsCleared);
	        return stateEvaluator.getScore(WEIGHT);
	 }
	 
	private int moveOneBlock(State state, int orient, int slot, int nextPiece, int[][] gameBoard, int[] top, int turnNumber) {
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
	 
}