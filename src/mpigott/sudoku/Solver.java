package mpigott.sudoku;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;

/**
 * Solves a Sudoku {@link Puzzle}.
 *
 * @author  Mike Pigott
 * @version 1.0
 */
public class Solver {

	public static class Move {

		public Move(int r, int c, int v) {
			row = r;
			col = c;
			value = v;
		}

		public int getRow() {
			return row;
		}

		public int getCol() {
			return col;
		}

		public int getValue() {
			return value;
		}

		private int value;
		private int row;
		private int col;
	}

	public Solver(Puzzle puzzle) {
		this.puzzle = puzzle;

		this.numbersInUseByRow = new BitSet[9];
		this.numbersInUseByCol = new BitSet[9];
		this.numbersInUseByBlk = new BitSet[9];
		for (int i = 0; i < 9; ++ i) {
			this.numbersInUseByRow[i] = new BitSet(9);
			this.numbersInUseByCol[i] = new BitSet(9);
			this.numbersInUseByBlk[i] = new BitSet(9);
		}

		this.moves = new ArrayList<Move>();

		// Set which values are in use by block, row, and column.
		int value = 0;
		for (int r = 0; r < 9; ++r) {
			for (int c = 0; c < 9; ++c) {
				value = this.puzzle.getValue(r, c);
				if (value > 0) {
					this.numbersInUseByRow[r].set(value - 1);
					this.numbersInUseByCol[c].set(value - 1);
					this.numbersInUseByBlk[getBlockNumber(r, c)].set(value - 1);
				}
			}
		}
	}

	public Puzzle getSolvedPuzzle() {
		return puzzle;
	}

	public List<Move> getMovesToSolve() {
		return moves;
	}

	public boolean solve() {
		return solve(0, 0);
	}

	private static int getBlockNumber(int row, int col) {
		return (row / 3) * 3 + (col / 3);
	}

	private boolean solve(int row, int col) {
		int r = row, c = col;
		boolean found = false;

		// Find the next unsolved square.
		for (; r < 9; ++r) {
			for (; c < 9; ++c) {
				if (puzzle.getValue(r, c) == 0) {
					found = true;
					break;
				}
			}

			if (found) {
				break;
			} else {
				c = 0;
			}
		}

		// If we reached the end of the puzzle, then we won!
		if (r == 9) {
			if (puzzle.getValue(8,  8) != 0) {
				return true;
			} else {
				return false;
			}
		}

		// Find a candidate that will fit that square.
		int candidate = 0;
		boolean solved = false;
		while ((candidate = getNextCandidate(r, c, candidate)) != 0) {
			int b = getBlockNumber(r, c);

			moves.add(new Move(r, c, candidate));

			numbersInUseByRow[r].set(candidate - 1);
			numbersInUseByCol[c].set(candidate - 1);
			numbersInUseByBlk[b].set(candidate - 1);

			puzzle.setValue(r, c, candidate);

			// Attempt to solve the puzzle.
			solved = solve(r, c);

			if (!solved) {
				// This value did not succeed.  Undo the move, and try the next candidate.
				moves.remove(moves.size() - 1);

				numbersInUseByRow[r].clear(candidate - 1);
				numbersInUseByCol[c].clear(candidate - 1);
				numbersInUseByBlk[b].clear(candidate - 1);
			} else {
				// We solved it!
				break;
			}
		}

		// If not solved, clear the value at the square.
		if (!solved) {
			puzzle.setValue(r, c, 0);
		}

		// No solution with this set.
		return solved;
	}

	private int getNextCandidate(int row, int col, int prevCandidate) {
		if ((row < 0) || (row > 8) || (col < 0) || (col > 8)) {
			throw new IllegalArgumentException("Row {" + row + "} and column {" + col + "} must be in the range [0, 8].");
		}

		final int blk = getBlockNumber(row, col);

		for (int candidate = prevCandidate + 1; candidate < 10; ++candidate) {
			if (!numbersInUseByRow[row].get(candidate - 1)
					&& !numbersInUseByCol[col].get(candidate - 1)
					&& !numbersInUseByBlk[blk].get(candidate - 1)) {
				return candidate;
			}
		}

		return 0;
	}

	private Puzzle puzzle;
	private BitSet[] numbersInUseByRow;
	private BitSet[] numbersInUseByCol;
	private BitSet[] numbersInUseByBlk;
	private ArrayList<Move> moves;
}
