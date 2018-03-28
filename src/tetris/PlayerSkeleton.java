package tetris;
import java.util.Arrays;
import java.util.Random;

public class PlayerSkeleton {

	private final double[] WEIGHT = {
			0.5932355194596699,
			-2.7547555434137565,
			-1.6606852034705946,
			-0.7976826453063799,
			-1.4581039072588022,
			-0.33838986761453727,
			0.30605751729505
	};

	//implement this function to have a working system
	public int pickMove(State s, int[][] legalMoves) {
		int bestMove = -1;
		double bestScore = Integer.MIN_VALUE;

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

			// Test this move (maybe later can change `Heuristic` to a static class)
			int testCleared = testMove(s, orientation, slot, s.getNextPiece(), currentBoard, currentTop);
			if (testCleared < 0) { continue; }
			Heuristic stateEvaluator = new Heuristic(currentBoard, s.getTop(), currentTop, testCleared);
			double score = stateEvaluator.getTotalHeuristic(WEIGHT);

			// Updates best score and best move
			if (score > bestScore) {
				bestScore = score;
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
				Thread.sleep(1);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		System.out.println("You have completed "+s.getRowsCleared()+" rows.");
	}

	private int testMove(State s, int orient, int slot, int nextPiece, int[][] gameBoard, int[] top) {

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
				gameBoard[h][i + slot] = s.getTurnNumber() + 1;
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