package edu.iastate.cs472.proj2;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * An object of this class holds data about a game of checkers.
 * It knows what kind of piece is on each square of the checkerboard.
 * Note that RED moves "up" the board (i.e. row number decreases)
 * while BLACK moves "down" the board (i.e. row number increases).
 * Methods are provided to return lists of available legal moves.
 */
public class CheckersData {

  /*  The following constants represent the possible contents of a square
      on the board.  The constants RED and BLACK also represent players
      in the game. */

    static final int
            EMPTY = 0,
            RED = 1,
            RED_KING = 2,
            BLACK = 3,
            BLACK_KING = 4;


    int[][] board;  // board[r][c] is the contents of row r, column c.


    /**
     * Constructor.  Create the board and set it up for a new game.
     */
    CheckersData() {
        board = new int[8][8];
        setUpGame();
    }

    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_YELLOW = "\u001B[33m";

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < board.length; i++) {
            int[] row = board[i];
            sb.append(8 - i).append(" ");
            for (int n : row) {
                if (n == 0) {
                    sb.append(" ");
                } else if (n == 1) {
                    sb.append(ANSI_RED + "R" + ANSI_RESET);
                } else if (n == 2) {
                    sb.append(ANSI_RED + "K" + ANSI_RESET);
                } else if (n == 3) {
                    sb.append(ANSI_YELLOW + "B" + ANSI_RESET);
                } else if (n == 4) {
                    sb.append(ANSI_YELLOW + "K" + ANSI_RESET);
                }
                sb.append(" ");
            }
            sb.append(System.lineSeparator());
        }
        sb.append("  a b c d e f g h");

        return sb.toString();
    }

    /**
     * Set up the board with checkers in position for the beginning
     * of a game.  Note that checkers can only be found in squares
     * that satisfy  row % 2 == col % 2.  At the start of the game,
     * all such squares in the first three rows contain black squares
     * and all such squares in the last three rows contain red squares.
     */
    void setUpGame() {
        // Set up the board with pieces BLACK, RED, and EMPTY
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                if (row % 2 != col % 2) {
                    // Valid checker positions
                    if (row < 3) {
                        board[row][col] = BLACK;
                    } else if (row > 4) {
                        board[row][col] = RED;
                    } else {
                        board[row][col] = EMPTY;
                    }
                } else {
                    board[row][col] = EMPTY;
                }
            }
        }
    }


    /**
     * Return the contents of the square in the specified row and column.
     */
    int pieceAt(int row, int col) {
        return board[row][col];
    }


    /**
     * Make the specified move.  It is assumed that move
     * is non-null and that the move it represents is legal.
     *
     * Make a single move or a sequence of jumps
     * recorded in rows and cols.
     *
     */
    void makeMove(CheckersMove move) {
        int l = move.rows.size();
        for(int i = 0; i < l-1; i++)
            makeMove(move.rows.get(i), move.cols.get(i), move.rows.get(i+1), move.cols.get(i+1));
    }


    /**
     * Make the move from (fromRow,fromCol) to (toRow,toCol).  It is
     * assumed that this move is legal.  If the move is a jump, the
     * jumped piece is removed from the board.  If a piece moves to
     * the last row on the opponent's side of the board, the
     * piece becomes a king.
     *
     * @param fromRow row index of the from square
     * @param fromCol column index of the from square
     * @param toRow   row index of the to square
     * @param toCol   column index of the to square
     */
    void makeMove(int fromRow, int fromCol, int toRow, int toCol) {
        // Move the piece from (fromRow,fromCol) to (toRow,toCol)
        int piece = board[fromRow][fromCol];
        board[fromRow][fromCol] = EMPTY;
        board[toRow][toCol] = piece;
        
        // If this is a jump, remove the captured piece
        if (Math.abs(fromRow - toRow) == 2) {
            int jumpedRow = (fromRow + toRow) / 2;
            int jumpedCol = (fromCol + toCol) / 2;
            board[jumpedRow][jumpedCol] = EMPTY;
        }
        
        // Crown the piece if it reaches the king's row
        if (toRow == 0 && piece == RED) {
            board[toRow][toCol] = RED_KING;
        } else if (toRow == 7 && piece == BLACK) {
            board[toRow][toCol] = BLACK_KING;
        }
    }

    /**
     * Return an array containing all the legal CheckersMoves
     * for the specified player on the current board.  If the player
     * has no legal moves, null is returned.  The value of player
     * should be one of the constants RED or BLACK; if not, null
     * is returned.  If the returned value is non-null, it consists
     * entirely of jump moves or entirely of regular moves, since
     * if the player can jump, only jumps are legal moves.
     *
     * @param player color of the player, RED or BLACK
     */
    CheckersMove[] getLegalMoves(int player) {
        if (player != RED && player != BLACK) {
            return null;
        }
        
        ArrayList<CheckersMove> moves = new ArrayList<CheckersMove>();
        
        // First, check for jumps (they are mandatory)
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                int piece = board[row][col];
                if (piece == player || piece == player + 1) { // player or player_king
                    CheckersMove[] jumps = getLegalJumpsFrom(player, row, col);
                    if (jumps != null) {
                        moves.addAll(Arrays.asList(jumps));
                    }
                }
            }
        }
        
        // If there are jumps available, return only jumps (mandatory)
        if (moves.size() > 0) {
            CheckersMove[] moveArray = new CheckersMove[moves.size()];
            return moves.toArray(moveArray);
        }
        
        // No jumps available, so check for regular moves
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                int piece = board[row][col];
                if (piece == player || piece == player + 1) { // player or player_king
                    // Check all four diagonal directions
                    if (canMove(player, row, col, row - 1, col - 1)) {
                        moves.add(new CheckersMove(row, col, row - 1, col - 1));
                    }
                    if (canMove(player, row, col, row - 1, col + 1)) {
                        moves.add(new CheckersMove(row, col, row - 1, col + 1));
                    }
                    if (canMove(player, row, col, row + 1, col - 1)) {
                        moves.add(new CheckersMove(row, col, row + 1, col - 1));
                    }
                    if (canMove(player, row, col, row + 1, col + 1)) {
                        moves.add(new CheckersMove(row, col, row + 1, col + 1));
                    }
                }
            }
        }
        
        if (moves.size() == 0) {
            return null;
        } else {
            CheckersMove[] moveArray = new CheckersMove[moves.size()];
            return moves.toArray(moveArray);
        }
    }
    
    /**
     * Helper method to check if a regular move is valid.
     */
    private boolean canMove(int player, int fromRow, int fromCol, int toRow, int toCol) {
        // Check bounds
        if (toRow < 0 || toRow >= 8 || toCol < 0 || toCol >= 8) {
            return false;
        }
        
        // Check if destination is empty
        if (board[toRow][toCol] != EMPTY) {
            return false;
        }
        
        int piece = board[fromRow][fromCol];
        
        // Regular pieces can only move in one direction
        if (piece == RED && toRow > fromRow) {
            return false; // RED moves up (decreasing row)
        }
        if (piece == BLACK && toRow < fromRow) {
            return false; // BLACK moves down (increasing row)
        }
        
        return true;
    }


    /**
     * Return a list of the legal jumps that the specified player can
     * make starting from the specified row and column.  If no such
     * jumps are possible, null is returned.  The logic is similar
     * to the logic of the getLegalMoves() method.
     *
     * Note that each CheckerMove may contain multiple jumps. 
     * Each move returned in the array represents a sequence of jumps 
     * until no further jump is allowed.
     *
     * @param player The player of the current jump, either RED or BLACK.
     * @param row    row index of the start square.
     * @param col    col index of the start square.
     */
    CheckersMove[] getLegalJumpsFrom(int player, int row, int col) {
        ArrayList<CheckersMove> jumps = new ArrayList<CheckersMove>();
        getLegalJumpsFromRecursive(player, row, col, new CheckersMove(), jumps, copyBoard());
        
        if (jumps.size() == 0) {
            return null;
        } else {
            CheckersMove[] jumpArray = new CheckersMove[jumps.size()];
            return jumps.toArray(jumpArray);
        }
    }
    
    /**
     * Recursive helper to find all jump sequences from a position.
     */
    private void getLegalJumpsFromRecursive(int player, int row, int col, 
                                            CheckersMove currentMove, 
                                            ArrayList<CheckersMove> allMoves,
                                            int[][] tempBoard) {
        boolean foundJump = false;
        
        // Try all four diagonal jump directions
        int[][] directions = {{-2, -2}, {-2, 2}, {2, -2}, {2, 2}};
        
        for (int[] dir : directions) {
            int toRow = row + dir[0];
            int toCol = col + dir[1];
            int jumpedRow = row + dir[0] / 2;
            int jumpedCol = col + dir[1] / 2;
            
            if (canJump(player, row, col, toRow, toCol, tempBoard)) {
                foundJump = true;
                
                // Make a copy of the current move and add this jump
                CheckersMove newMove = currentMove.clone();
                if (newMove.rows.size() == 0) {
                    newMove.addMove(row, col);
                }
                newMove.addMove(toRow, toCol);
                
                // Create a temporary board to track this jump sequence
                int[][] newBoard = copyBoardArray(tempBoard);
                int piece = newBoard[row][col];
                newBoard[row][col] = EMPTY;
                newBoard[toRow][toCol] = piece;
                newBoard[jumpedRow][jumpedCol] = EMPTY;
                
                // Crown if reaching king's row
                if (toRow == 0 && piece == RED) {
                    newBoard[toRow][toCol] = RED_KING;
                } else if (toRow == 7 && piece == BLACK) {
                    newBoard[toRow][toCol] = BLACK_KING;
                }
                
                // Store current board state
                int[][] savedBoard = this.board;
                this.board = newBoard;
                
                // Recursively find more jumps
                getLegalJumpsFromRecursive(player, toRow, toCol, newMove, allMoves, newBoard);
                
                // Restore board
                this.board = savedBoard;
            }
        }
        
        // If no more jumps found, add the current move sequence
        if (!foundJump && currentMove.rows.size() > 0) {
            allMoves.add(currentMove);
        }
    }
    
    /**
     * Helper method to check if a jump is valid.
     */
    private boolean canJump(int player, int fromRow, int fromCol, int toRow, int toCol, int[][] tempBoard) {
        // Check bounds
        if (toRow < 0 || toRow >= 8 || toCol < 0 || toCol >= 8) {
            return false;
        }
        
        // Check if destination is empty
        if (tempBoard[toRow][toCol] != EMPTY) {
            return false;
        }
        
        // Check the jumped position
        int jumpedRow = (fromRow + toRow) / 2;
        int jumpedCol = (fromCol + toCol) / 2;
        int jumpedPiece = tempBoard[jumpedRow][jumpedCol];
        
        // Must jump over an opponent's piece
        if (jumpedPiece == EMPTY) {
            return false;
        }
        if (player == RED && (jumpedPiece != BLACK && jumpedPiece != BLACK_KING)) {
            return false;
        }
        if (player == BLACK && (jumpedPiece != RED && jumpedPiece != RED_KING)) {
            return false;
        }
        
        int piece = tempBoard[fromRow][fromCol];
        
        // Regular pieces can only jump in one direction
        if (piece == RED && toRow > fromRow) {
            return false; // RED moves up
        }
        if (piece == BLACK && toRow < fromRow) {
            return false; // BLACK moves down
        }
        
        return true;
    }
    
    /**
     * Create a copy of the board.
     */
    private int[][] copyBoard() {
        int[][] copy = new int[8][8];
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                copy[i][j] = board[i][j];
            }
        }
        return copy;
    }
    
    /**
     * Create a copy of a board array.
     */
    private int[][] copyBoardArray(int[][] source) {
        int[][] copy = new int[8][8];
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                copy[i][j] = source[i][j];
            }
        }
        return copy;
    }

}
