package edu.iastate.cs472.proj2;

/**
 * 
 * @author Hrishikesh Uchake
 *
 */

/**
 * This class implements the Monte Carlo tree search method to find the best
 * move at the current state.
 */
public class MonteCarloTreeSearch extends AdversarialSearch {
    
    private static final double EXPLORATION_CONSTANT = Math.sqrt(2); // C = √2 (theoretically optimal)
    private static final int ITERATIONS = 3000; // Number of MCTS iterations
    private java.util.Random random = new java.util.Random();

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
        // which is an 2D array of the following integers defined below:
    	// 
        // 0 - empty square,
        // 1 - red man
        // 2 - red king
        // 3 - black man
        // 4 - black king
        System.out.println(board);
        System.out.println();

        // Create root node with current state
        MCNode root = new MCNode(copyBoard(board), true); // BLACK's turn
        
        // Run MCTS iterations
        for (int i = 0; i < ITERATIONS; i++) {
            MCNode selectedNode = selection(root);
            MCNode expandedNode = expansion(selectedNode);
            double result = simulation(expandedNode);
            backpropagation(expandedNode, result);
        }
        
        // Select the best move based on visit count (most robust)
        return selectBestMove(root);
    }
    
    /**
     * Selection: Start at root and select successive child nodes using UCB1
     * until reaching a node that is not fully expanded or is terminal.
     */
    private MCNode selection(MCNode node) {
        while (!node.isTerminal()) {
            if (!node.isFullyExpanded()) {
                return node;
            }
            node = selectBestChild(node);
        }
        return node;
    }
    
    /**
     * Select the child with the highest UCB1 value.
     */
    private MCNode selectBestChild(MCNode node) {
        MCNode bestChild = null;
        double bestValue = Double.NEGATIVE_INFINITY;
        
        for (MCNode child : node.children) {
            double ucb1 = child.getUCB1(EXPLORATION_CONSTANT);
            if (ucb1 > bestValue) {
                bestValue = ucb1;
                bestChild = child;
            }
        }
        
        return bestChild;
    }
    
    /**
     * Expansion: If the node is not terminal, expand it by creating a new child node
     * for one of the unexplored moves.
     */
    private MCNode expansion(MCNode node) {
        if (node.isTerminal()) {
            return node;
        }
        
        int player = node.isBlackTurn ? CheckersData.BLACK : CheckersData.RED;
        CheckersMove[] legalMoves = node.state.getLegalMoves(player);
        
        // Find unexplored moves
        for (CheckersMove move : legalMoves) {
            boolean explored = false;
            for (MCNode child : node.children) {
                if (movesEqual(child.move, move)) {
                    explored = true;
                    break;
                }
            }
            
            if (!explored) {
                // Create new child node for this move
                CheckersData newState = copyBoard(node.state);
                newState.makeMove(move);
                MCNode newNode = new MCNode(newState, move, node, !node.isBlackTurn);
                node.children.add(newNode);
                return newNode;
            }
        }
        
        // Should not reach here if isFullyExpanded() works correctly
        return node;
    }
    
    /**
     * Simulation (Playout): From the given node, simulate a random game until terminal state.
     * Returns the result from BLACK's perspective: 1 for win, 0 for loss, 0.5 for draw.
     */
    private double simulation(MCNode node) {
        CheckersData simState = copyBoard(node.state);
        boolean currentTurn = node.isBlackTurn;
        int moveCount = 0;
        int maxMoves = 200; // Prevent infinite games
        
        while (moveCount < maxMoves) {
            int player = currentTurn ? CheckersData.BLACK : CheckersData.RED;
            CheckersMove[] moves = simState.getLegalMoves(player);
            
            if (moves == null) {
                // Current player has no legal moves - opponent wins
                return currentTurn ? 0.0 : 1.0;
            }
            
            // Random move selection
            CheckersMove randomMove = moves[random.nextInt(moves.length)];
            simState.makeMove(randomMove);
            
            currentTurn = !currentTurn;
            moveCount++;
        }
        
        // If we reach max moves, consider it a draw
        return 0.5;
    }
    
    /**
     * Backpropagation: Update all nodes on the path from the expanded node to the root
     * with the simulation result.
     */
    private void backpropagation(MCNode node, double result) {
        while (node != null) {
            node.visits++;
            // For BLACK nodes, add result directly
            // For RED nodes, add (1 - result) since they want opposite outcome
            if (node.isBlackTurn) {
                node.wins += result;
            } else {
                node.wins += (1.0 - result);
            }
            node = node.parent;
        }
    }
    
    /**
     * Select the best move from root's children based on visit count.
     */
    private CheckersMove selectBestMove(MCNode root) {
        MCNode bestChild = null;
        int maxVisits = -1;
        
        for (MCNode child : root.children) {
            if (child.visits > maxVisits) {
                maxVisits = child.visits;
                bestChild = child;
            }
        }
        
        return bestChild != null ? bestChild.move : null;
    }
    
    /**
     * Check if two moves are equal.
     */
    private boolean movesEqual(CheckersMove m1, CheckersMove m2) {
        if (m1 == null || m2 == null) {
            return m1 == m2;
        }
        
        if (m1.rows.size() != m2.rows.size() || m1.cols.size() != m2.cols.size()) {
            return false;
        }
        
        for (int i = 0; i < m1.rows.size(); i++) {
            if (!m1.rows.get(i).equals(m2.rows.get(i)) || !m1.cols.get(i).equals(m2.cols.get(i))) {
                return false;
            }
        }
        
        return true;
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
