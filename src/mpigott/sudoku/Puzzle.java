package mpigott.sudoku;

/**
 * Represents the puzzle board itself.
 *
 * @author  Mike Pigott
 * @version 1.0
 */
public class Puzzle {

	public Puzzle() {
		cells = new int[81];
		for (int c = 0; c < 81; ++c) {
			cells[c] = 0;
		}
	}

	public int getValue(int row, int col) {
		if ((row < 0) || (row > 8) || (col < 0) || (col > 8)) {
			throw new IllegalArgumentException("Row and column must be in the range [0, 8].  Received row " + row + " and column " + col + ".");
		}
		return cells[(row * 9) + col];
	}

	public void setValue(int row, int col, int value) {
		if ((row < 0) || (row > 8) || (col < 0) || (col > 8) || (value < 0) || (value > 9)) {
			throw new IllegalArgumentException("Row and column must be in the range [0, 8].  Received row " + row + " and column " + col + ".  Value must be in the range [0, 9].  Received value of " + value);
		}
		cells[(row * 9) + col] = value;
	}

	public boolean isEmpty() {
		for (int i = 0; i < 81; ++i) {
			if (cells[i] > 0) {
				return false;
			}
		}

		return true;
	}

	private int[] cells;
}
