package edu.iastate.cs472.proj2;

/**
 * 
 * @author Hrishikesh Uchake
 *
 */


/**
 * This class implements the Alpha-Beta pruning algorithm to find the best 
 * move at current state.
*/
public class AlphaBetaSearch extends AdversarialSearch {
    
    private static final int MAX_DEPTH = 8; // Search depth

    /**
     * The input parameter legalMoves contains all the possible moves.
     * It contains four integers:  fromRow, fromCol, toRow, toCol
     * which represents a move from (fromRow, fromCol) to (toRow, toCol).
     * It also provides a utility method `isJump` to see whether this
     * move is a jump or a simple move.
     * 
     * Each legalMove in the input now contains a single move
     * or a sequence of jumps: (rows[0], cols[0]) -> (rows[1], cols[1]) ->
     * (rows[2], cols[2]).
     *
     * @param legalMoves All the legal moves for the agent at current step.
     */
    public CheckersMove makeMove(CheckersMove[] legalMoves) {
        // The checker board state can be obtained from this.board,
        // which is a int 2D array. The numbers in the `board` are
        // defined as
        // 0 - empty square,
        // 1 - red man
        // 2 - red king
        // 3 - black man
        // 4 - black king
        System.out.println(board);
        System.out.println();

        CheckersMove bestMove = legalMoves[0];
        double bestValue = Double.NEGATIVE_INFINITY;
        
        // Try each legal move and find the best one using alpha-beta pruning
        for (CheckersMove move : legalMoves) {
            CheckersData tempBoard = copyBoard(board);
            tempBoard.makeMove(move);
            
            double value = minValue(tempBoard, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, 1);
            
            if (value > bestValue) {
                bestValue = value;
                bestMove = move;
            }
        }
        
        return bestMove;
    }
    
    /**
     * Max player (BLACK - AI) tries to maximize the score.
     */
    private double maxValue(CheckersData state, double alpha, double beta, int depth) {
        if (depth >= MAX_DEPTH || isTerminal(state)) {
            return evaluate(state);
        }
        
        CheckersMove[] moves = state.getLegalMoves(CheckersData.BLACK);
        if (moves == null) {
            return evaluate(state); // No legal moves, return evaluation
        }
        
        double value = Double.NEGATIVE_INFINITY;
        for (CheckersMove move : moves) {
            CheckersData tempBoard = copyBoard(state);
            tempBoard.makeMove(move);
            
            value = Math.max(value, minValue(tempBoard, alpha, beta, depth + 1));
            
            if (value >= beta) {
                return value; // Beta cutoff
            }
            alpha = Math.max(alpha, value);
        }
        
        return value;
    }
    
    /**
     * Min player (RED - human) tries to minimize the score.
     */
    private double minValue(CheckersData state, double alpha, double beta, int depth) {
        if (depth >= MAX_DEPTH || isTerminal(state)) {
            return evaluate(state);
        }
        
        CheckersMove[] moves = state.getLegalMoves(CheckersData.RED);
        if (moves == null) {
            return evaluate(state); // No legal moves, return evaluation
        }
        
        double value = Double.POSITIVE_INFINITY;
        for (CheckersMove move : moves) {
            CheckersData tempBoard = copyBoard(state);
            tempBoard.makeMove(move);
            
            value = Math.min(value, maxValue(tempBoard, alpha, beta, depth + 1));
            
            if (value <= alpha) {
                return value; // Alpha cutoff
            }
            beta = Math.min(beta, value);
        }
        
        return value;
    }
    
    /**
     * Check if the state is a terminal state.
     */
    private boolean isTerminal(CheckersData state) {
        CheckersMove[] blackMoves = state.getLegalMoves(CheckersData.BLACK);
        CheckersMove[] redMoves = state.getLegalMoves(CheckersData.RED);
        return blackMoves == null || redMoves == null;
    }
    
    /**
     * Evaluation function that returns a heuristic score for non-terminal states.
     * Positive values favor BLACK (AI), negative values favor RED (human).
     * 
     * Features:
     * 1. Material advantage: piece count difference
     * 2. King advantage: kings are more valuable
     * 3. Positional advantage: pieces closer to becoming kings
     * 4. Mobility: number of legal moves available
     */
    private double evaluate(CheckersData state) {
        CheckersMove[] blackMoves = state.getLegalMoves(CheckersData.BLACK);
        CheckersMove[] redMoves = state.getLegalMoves(CheckersData.RED);
        
        // Terminal state utilities
        if (blackMoves == null && redMoves == null) {
            return 0; // Draw
        }
        if (blackMoves == null) {
            return -1; // BLACK (AI) loses
        }
        if (redMoves == null) {
            return 1; // BLACK (AI) wins
        }
        
        double score = 0;
        
        // Count pieces and evaluate positions
        int blackMen = 0, blackKings = 0, redMen = 0, redKings = 0;
        double blackPosition = 0, redPosition = 0;
        
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                int piece = state.pieceAt(row, col);
                
                if (piece == CheckersData.BLACK) {
                    blackMen++;
                    // Encourage advancement (closer to row 7)
                    blackPosition += row * 0.01;
                } else if (piece == CheckersData.BLACK_KING) {
                    blackKings++;
                    // Kings in center are stronger
                    blackPosition += (1.0 - Math.abs(3.5 - row) / 3.5) * 0.02;
                    blackPosition += (1.0 - Math.abs(3.5 - col) / 3.5) * 0.02;
                } else if (piece == CheckersData.RED) {
                    redMen++;
                    // Encourage advancement (closer to row 0)
                    redPosition += (7 - row) * 0.01;
                } else if (piece == CheckersData.RED_KING) {
                    redKings++;
                    // Kings in center are stronger
                    redPosition += (1.0 - Math.abs(3.5 - row) / 3.5) * 0.02;
                    redPosition += (1.0 - Math.abs(3.5 - col) / 3.5) * 0.02;
                }
            }
        }
        
        // Material score (pieces and kings)
        score += (blackMen - redMen) * 0.3;
        score += (blackKings - redKings) * 0.5;
        
        // Positional score
        score += blackPosition - redPosition;
        
        // Mobility score (more moves = better)
        score += (blackMoves.length - redMoves.length) * 0.02;
        
        // Normalize score to [-1, 1] range for non-terminal states
        return Math.tanh(score);
    }
    
    /**
     * Create a deep copy of the board.
     */
    private CheckersData copyBoard(CheckersData original) {
        CheckersData copy = new CheckersData();
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                copy.board[i][j] = original.board[i][j];
            }
        }
        return copy;
    }

}
