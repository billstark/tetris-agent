# To setup the development environment
You could download the project using git clone and then use eclipse to add the project in.
To be more specific, File -> Open projects from file systems

# Pre-defined classes

### TFrame and TLabel
This is just used for visualise the game play (you can actually play the game)

### State
I just listed down some important functions

#### Getting game info

  * **hasLost()** return true if lost, false otherwise

  * **getField()** This returns a 20x10 2D array that contains the current state of the board.
  0 refers to no occupation while other values refers to the turn when the block is filled.

  * **getNextPiece()** returns the ID (0-6) of the piece you are about to play.

  * **getRowsCleared()** returns the number of lines cleared.

  * **legalMoves()** all possible legal moves of the "next" piece. This is represented by
  a Nx2 array, where N is the number of possible moves, and for each row, there is an
  array with 2 elements. The first is SLOT (left most column of the piece, like x-coordinate)
  and the second is ORIENTATION.

#### Actions

  * **makeMove(int move)** or **makeMove(int[] move)**. In this case, if we just specify
  an integer, it will pick the legal move in `legalMoves` (an array) with `move` as index.
  If we take array as input, it will use (SLOT, ORIENTATION) as move directly

#### Drawing related

  * **draw()** draws the board.

  * **drawNext()** draws the next piece above the board

  * **clearNext()** clears the drawing of the next piece so it can be drawn in a different slot/orientation
