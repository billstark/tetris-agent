package tetris;


public class Player {

    // The game state object
    private State state;

    //parameters for fitness
    private long totalPileHeight;
    private int maxPileHeight;
    private double averagePileHeight;
    
    private long totalHoleNumber;
    private int maxHoleNumber;
    private double averageHoleNumber;
    
    private long totalRowTransition;
    private int maxRowTransition;
    private double averageRowTransition;
    
    private long totalColumnTransition;
    private int maxColumnTransition;
    private double averageColumnTransition;
  
    private long round;

    /**
     * Constructor
     */
    public Player() {
        this.state = new State();
        
        this.totalPileHeight = 0;
        this.maxPileHeight = 0;
        this.averagePileHeight = 0;
        
        this.totalHoleNumber = 0;
        this.maxHoleNumber = 0;
        this.averageHoleNumber = 0;
        
        this.totalRowTransition = 0;
        this.maxRowTransition = 0;
        this.averageRowTransition = 0;
        
        this.totalColumnTransition = 0;
        this.maxColumnTransition = 0;
        this.averageColumnTransition = 0;
        
        this.round = 0;
    }

    /**
     * The function that executes a game play
     * @param maxTurn Number of turns that the play terminates when reached. 0 or negative input for no limit. 
     */
    public void play(int maxLinesCleared) {

        // Loop until the game is not lost
        while (!state.lost && (maxLinesCleared <= 0 || state.getRowsCleared() < maxLinesCleared)) {

            // Some dumb value for initial best move
            int bestMove = -1;
            double bestScore = Integer.MIN_VALUE;
            NewHeuristic stateEvaluator = null;
            // For every possible legal move, we evaluate the score of that
            // move using the parameters from weight
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
                
                stateEvaluator = new NewHeuristic(currentBoard, state.getTop(), currentTop, testCleared);

                double score = stateEvaluator.getScore(getWeights());

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
            updateParametersForFitness(stateEvaluator);
        }
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

    public int getLinesCleared() {
        return state.getRowsCleared();
    }

    public double fitnessEvaluation() {
        return state.getRowsCleared()
                + 1.0*(maxPileHeight - averagePileHeight) / maxPileHeight * 500
                + 1.0*(maxHoleNumber - averageHoleNumber) / maxHoleNumber * 500
                + 1.0*(maxRowTransition  - averageRowTransition) / maxRowTransition * 500
                + 1.0*(maxColumnTransition - averageColumnTransition) / maxColumnTransition * 500;
    }

    private void updateParametersForFitness(NewHeuristic stateEvaluator) {
        round++;
        
        int currentPileHeight = stateEvaluator.getPileHeight();
        totalPileHeight += currentPileHeight;
        if(maxPileHeight < currentPileHeight)
        	maxPileHeight = currentPileHeight;
        averagePileHeight = 1.0*totalPileHeight/round;
        
        int currentHoleNumber = stateEvaluator.getHoleCount();
        totalHoleNumber += currentHoleNumber;
        if(maxHoleNumber < currentHoleNumber)
        	maxHoleNumber = currentHoleNumber;
        averageHoleNumber = 1.0*totalHoleNumber/round;
        
        int currentRowTransition = stateEvaluator.getRowTransition();
        totalRowTransition += currentRowTransition;
        if(maxRowTransition < currentRowTransition)
        	maxRowTransition = currentRowTransition;
        averageRowTransition = 1.0*totalRowTransition/round;
        
        int currentColumnTransition = stateEvaluator.getColumnTransition();
        totalColumnTransition += currentColumnTransition;
        if(maxColumnTransition < currentColumnTransition)
        	maxColumnTransition = currentColumnTransition;
        averageColumnTransition = 1.0*totalColumnTransition/round;
    }
    
    protected double[] getWeights() {
        return new double[7];
    }
}
