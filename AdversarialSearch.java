package edu.iastate.cs472.proj2;

/**
 * 
 * @author Hrishikesh Uchake
 *
 */

/**
 * This class is to be extended by the classes AlphaBetaSearch and MonteCarloTreeSearch.
 */
public abstract class AdversarialSearch {
    protected CheckersData board;

    // An instance of this class will be created in the Checkers.Board
    // It would be better to keep the default constructor.

    protected void setCheckersData(CheckersData board) {
        this.board = board;
    }
    
    /** 
     * 
     * @return an array of valid moves
     */
    protected CheckersMove[] legalMoves() {
    	// Get legal moves for BLACK (the AI player)
    	return board.getLegalMoves(CheckersData.BLACK); 
    }
	
    /**
     * Return a move returned from either the alpha-beta search or the Monte Carlo tree search.
     * 
     * @param legalMoves
     * @return CheckersMove 
     */
    public abstract CheckersMove makeMove(CheckersMove[] legalMoves);
}
