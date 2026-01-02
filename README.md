# Checkers AI Agent - Project 2

## Overview
This project implements an AI agent capable of playing checkers (English draughts) against a human player. The implementation includes three different game-playing strategies:

1. **Alpha-Beta Pruning Agent** - Uses minimax algorithm with alpha-beta pruning
2. **Monte Carlo Tree Search (MCTS) Agent** - Uses MCTS with UCB1 selection
3. **Hybrid Agent** - Randomly chooses between the above two strategies for each move

### Compilation
```bash
cd /path/to/repo
javac -d . *.java
```

### Running the Game
```bash
java edu.iastate.cs472.proj2.Checkers
```

When prompted, enter:
- `1` for Alpha-Beta pruning agent
- `2` for MCTS agent
- `3` for Hybrid (random) agent

## Game Rules

### Basic Rules
- Red (human) always moves first
- Pieces move diagonally on dark squares
- Regular pieces move forward only
- Kings (crowned pieces) can move in any diagonal direction

### Jumping
- Jumping over opponent pieces is **mandatory** when available
- Multiple jumps must be completed in a single turn
- The game automatically detects and requires all possible jumps

### Winning
- A player wins when the opponent has no legal moves
- This occurs when all opponent pieces are captured or blocked

## Implementation Details

### Alpha-Beta Pruning
- **Search Depth**: 8 plies (4 full moves)
- **Evaluation Function**:
  - Material: 0.3 points per regular piece, 0.5 per king
  - Position: Bonus for piece advancement and center control
  - Mobility: 0.02 points per available move
  - All scores normalized using tanh for stability

### Monte Carlo Tree Search
- **Iterations**: 3000 per move
- **Exploration Constant**: √2 (1.414...)
- **Selection**: UCB1 formula: wins/visits + C * √(ln(parent_visits)/visits)
- **Simulation**: Uniform random selection of legal moves
- **Draw Handling**: 0.5 points added to all nodes in path
- **Best Move**: Selected based on highest visit count (most robust)

### Performance Considerations
- Both are competitive against human players
- Hybrid agent provides variety while maintaining strong play

## References

- Russell & Norvig, "Artificial Intelligence: A Modern Approach" (4th Edition)
- English Draughts Rules: https://en.wikipedia.org/wiki/English_draughts
- UCB1 Algorithm: Kocsis & Szepesvári (2006)
- Alpha-Beta Pruning: Knuth & Moore (1975)
