
public class GAPlayer {
    // The vector that is used as a player
    private GAParameterVector vector;
    
    // The game state object
    private State state;
    
    private long linesCleared;
    
    // Parameters for fitness
    private long totalHeight;
    private double averageHeight;
    private long totalHole;
    private double averageHole;
    private long round;
    
    /**
     * Constructor, takes in a vector
     * @param vector
     */
    public GAPlayer(GAParameterVector vector) {
        this.vector = vector;
        this.state = new State();
        this.linesCleared = 0;
    }
    
    /**
     * The function that executes a game play
     */
    public void play(int maxTurn) {
        
        // Loop until the game is not lost
        while (!state.lost && (maxTurn <= 0 || state.getTurnNumber() < maxTurn)) {
            
         // Some dumb value for initial best move
            int bestMove = -1;
            double bestScore = Integer.MIN_VALUE;

            // For every possible legal move, we evaluate the score of that
            // move using the parameters from vector object
            for (int i = 0; i < state.legalMoves().length; i++) {

                // Gets orientation and slot of the current move
                int orientation = state.legalMoves()[i][State.ORIENT];
                int slot = state.legalMoves()[i][State.SLOT];

                // Have a copy of the current game board
                int[][] currentBoard = new int[state.getField().length][];
                for (int j = 0; j < currentBoard.length; j++) { currentBoard[j] = state.getField()[j].clone(); }

                // Have a copy of the current top
                int[] currentTop = state.getTop().clone();

                // Test this move (maybe later can change `Heuristic` to a static class)
                int testCleared = testMove(orientation, slot, state.getNextPiece(), currentBoard, currentTop);
                if (testCleared < 0) { continue; }
                Heuristic stateEvaluator = new Heuristic(currentBoard, state.getTop(), currentTop, testCleared);

                double score = stateEvaluator.getTotalHeuristic(vector.weight);

                // Updates best score and best move
                if (score > bestScore) {
                    bestScore = score;
                    bestMove = i;
                }
            }

            if (bestMove == -1) {
                state.makeMove(0);
                continue;
            }

            // lets the state to make the best move.
            state.makeMove(bestMove);
            updateParametersForFitness();
        }

        linesCleared = state.getRowsCleared();
    }

    /**
     * A simple fitness evaluation, just the number of lines cleared. This will
     * allow vectors to be pulled to correct direction quickly at the beginning.
     * And since we are allowing vectors to run until die at the beginning,
     * it would be a good evaluation factor
     * @return
     */
    public double fundamentalFitnessEvaluation() {
        return (double) state.getRowsCleared();
    }

    /**
     * This is an fitness evaluation in later stage (when two games run some time
     * and they are still not finished, we can early stop them and add some more
     * evaluation factors to evaluate them)
     * Just a small optimization
     *
     * @return a double number that represents the fitness of the vector
     */
    public double mediumFitnessEvaluation() {

        // Gets the number of lines cleared
        int linesCleared = state.getRowsCleared();

        // Gets the game board and game top
        int[][] gameBoard = state.getField();
        int[] top = state.getTop();

        return linesCleared + calculateHeightFactor(top) + calculateHoleFactor(gameBoard);
    }

    /**
     * Calculates the score brought by low ranged height given the
     * top of the game board
     * @param top
     * @return the score for evaluating game height
     */
    private double calculateHeightFactor(int[] top) {
        int total = 0;
        int highest = Integer.MIN_VALUE;
        for (int i = 0; i < top.length; i++) {
            if (highest < top[i]) {
                highest = top[i];
            }
            total = total + top[i];
        }

        // returns (highest - mean) / highest * 500
        return ((highest - (double) (total * 1.0 / top.length)) / highest)  * 500;
    }

    /**
     * Calculates the score brought by number of holes
     * Currently I just used a simple model, which is
     * NUM_OF_HOLES * -500. This might be improved.
     * @param board the game board
     * @return the score
     */
    private double calculateHoleFactor(int[][] board) {
        int count = 0;

        // For each column
        for (int i = 0; i < State.COLS; i++) {

            // For each position in the column
            for (int j = 0; j < State.ROWS - 1; j++) {

                // If it is not 0, just continue
                if (board[j][i] != 0) { continue; }

                // If it is 0, and there exists some block above
                // increase the count
                for (int k = j + 1; k < State.ROWS; k++) {
                    if (board[j][i] == 0 && board[k][i] != 0) {
                        count++;
                        break;
                    }
                }
            }
        }
        return -count * 50;
    }

    /**
     * This function gives a test move, which means that it will give
     * data about how the game will look like after you make a specific move
     * @param orient
     * @param slot
     * @param nextPiece
     * @param gameBoard a 2D array that stores the state of the current game board
     *      and will also store the game board after you make the move
     * @param top an array that contains the height of each column. It will also got
     *      updated after execution
     * @return the number of lines cleared
     *
     * Note: never mind i just copied this from `State`, I think it should be correct
     */
    private int testMove(int orient, int slot, int nextPiece, int[][] gameBoard, int[] top) {

        int height = state.getTop()[slot] - State.getpBottom()[nextPiece][orient][0];

        for (int c = 1; c < State.getpWidth()[nextPiece][orient]; c++) {
            height = Math.max(height, state.getTop()[slot + c] - State.getpBottom()[nextPiece][orient][c]);
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

    public long getLinesCleared() {
        return linesCleared;
    }

    public double thirdfitnessEvaluation() {
        return state.getRowsCleared()
                + 1.0*(totalHeight - averageHeight) / totalHeight * 500
                + 1.0*(totalHole - averageHole) / totalHole * 500;
    }

    private void updateParametersForFitness() {
        round++;

        int currentTotalHeight = 0;
        int currentHole = 0;
        for(int i = 0; i < State.COLS; i++) {
            currentTotalHeight += state.getTop()[i];
            for(int j=state.getTop()[i]; j >= 0 ; j--) {
                if(state.getField()[j][i] == 0) currentHole++;
            }
        }

        totalHeight += currentTotalHeight;
        totalHole += currentHole;
        averageHeight = 1.0*totalHeight/round;
        averageHole = 1.0*totalHole/round;
    }
}
