package edu.iastate.cs472.proj2;

import java.util.ArrayList;

/**
 * Node type for the Monte Carlo search tree.
 */
public class MCNode {
    CheckersData state;           // Game state at this node
    CheckersMove move;            // Move that led to this state (null for root)
    MCNode parent;                // Parent node
    ArrayList<MCNode> children;   // Child nodes
    int visits;                   // Number of times this node has been visited
    double wins;                  // Number of wins from this node (can be fractional for draws)
    boolean isBlackTurn;          // Whose turn it is at this state
    
    /**
     * Constructor for root node.
     */
    public MCNode(CheckersData state, boolean isBlackTurn) {
        this.state = state;
        this.move = null;
        this.parent = null;
        this.children = new ArrayList<MCNode>();
        this.visits = 0;
        this.wins = 0.0;
        this.isBlackTurn = isBlackTurn;
    }
    
    /**
     * Constructor for child node.
     */
    public MCNode(CheckersData state, CheckersMove move, MCNode parent, boolean isBlackTurn) {
        this.state = state;
        this.move = move;
        this.parent = parent;
        this.children = new ArrayList<MCNode>();
        this.visits = 0;
        this.wins = 0.0;
        this.isBlackTurn = isBlackTurn;
    }
    
    /**
     * Check if this node is fully expanded (all children have been created).
     */
    public boolean isFullyExpanded() {
        int player = isBlackTurn ? CheckersData.BLACK : CheckersData.RED;
        CheckersMove[] legalMoves = state.getLegalMoves(player);
        
        if (legalMoves == null) {
            return true; // Terminal node
        }
        
        return children.size() == legalMoves.length;
    }
    
    /**
     * Check if this is a terminal node (no legal moves available).
     */
    public boolean isTerminal() {
        int player = isBlackTurn ? CheckersData.BLACK : CheckersData.RED;
        CheckersMove[] legalMoves = state.getLegalMoves(player);
        return legalMoves == null;
    }
    
    /**
     * Get the UCB1 value for this node.
     */
    public double getUCB1(double explorationConstant) {
        if (visits == 0) {
            return Double.POSITIVE_INFINITY;
        }
        
        double exploitation = wins / visits;
        double exploration = explorationConstant * Math.sqrt(Math.log(parent.visits) / visits);
        
        return exploitation + exploration;
    }
}

