package mpigott.sudoku;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * Implementation of a Sudoku solver.
 *
 * @author  Mike Pigott
 * @version 1.0
 */
public class Sudoku {

	/**
	 * 
	 *
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		//List<Puzzle> puzzles = loadPuzzlesFromUrl("");
		List<Puzzle> puzzles = loadPuzzlesFromFile("resources/easy50.txt"); // http://norvig.com/easy50.txt
		System.out.println("Loaded " + puzzles.size() + " easy puzzles.");

		int numSolved = solvePuzzles(puzzles);
		System.out.println("Solved " + numSolved + " of " + puzzles.size() + " easy puzzles.");

		puzzles = loadPuzzlesFromUrl("http://magictour.free.fr/top95");
		System.out.println("Loaded " + puzzles.size() + " hard puzzles.");
		numSolved = solvePuzzles(puzzles);
		System.out.println("Solved " + numSolved + " of " + puzzles.size() + " hard puzzles.");
	}

	private static int solvePuzzles(List<Puzzle> puzzles) {
		int numSolved = 0;
		int puzzleNum = 1;

		for (Puzzle puzzle : puzzles) {
			Solver solver = new Solver(puzzle);

			boolean solved = solver.solve();
			if (solved) {
				try {
					checkPuzzle(solver.getSolvedPuzzle());
					++numSolved;
				} catch (IllegalStateException ise) {
					System.out.println("Solver produced invalid result for puzzle " + puzzleNum + ": " + ise.getMessage());
					printPuzzle(solver.getSolvedPuzzle());
				}
			} else {
				System.out.println("Puzzle " + puzzleNum + " not solved!");
			}

			++puzzleNum;
		}

		return numSolved;
	}

	private static List<Puzzle> loadPuzzlesFromFile(String file) throws IOException {
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader(file));
			return loadPuzzles(reader);
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException ioe) {
					ioe.printStackTrace();
				}
			}
		}
	}

	private static List<Puzzle> loadPuzzlesFromUrl(String url) throws IOException {
		BufferedReader reader = null;
		try {
			URL javaUrl = new URL(url);
			reader = new BufferedReader(new InputStreamReader(javaUrl.openStream()));
			return loadPuzzles(reader);
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException ioe) {
					ioe.printStackTrace();
				}
			}
		}
	}

	private static List<Puzzle> loadPuzzles(BufferedReader reader) throws IOException {
		ArrayList<Puzzle> puzzles = new ArrayList<Puzzle>();

		String line = null;

		int r = 0;
		Puzzle puzzle = new Puzzle();
		while ((line = reader.readLine()) != null) {
			if ((line.length() > 7) && (line.length() <= 9)) {
				// Format of http://norvig.com/easy50.txt
				if ((line.length() > 0) && (line.charAt(0) != '=')) {
					for (int c = 0; c < line.length(); ++c) {
						puzzle.setValue(r, c, (line.charAt(c) - 48));
					}
					++r;
				} else {
					r = 0;
					puzzles.add(puzzle);
					puzzle = new Puzzle();
				}
			} else if (line.length() == 81) {
				// Format of http://magictour.free.fr/top95
				for (r = 0; r < 9; ++r) {
					for (int c = 0; c < 9; ++c) {
						char value = line.charAt(r * 9 + c);
						if (value != '.') {
							puzzle.setValue(r, c, value - 48);
						}
					}
				}
				puzzles.add(puzzle);
				puzzle = new Puzzle();
			}
		}

		if (!puzzle.isEmpty()) {
			puzzles.add(puzzle);
		}

		return puzzles;
	}

	private static void printPuzzle(Puzzle puzzle) {
		for (int row = 0; row < 9; ++row) {
			for (int col = 0; col < 9; ++col) {
				System.out.print(puzzle.getValue(row, col));
			}
			System.out.println();
		}
		System.out.println("=========");
	}

	private static void checkPuzzle(Puzzle solvedPuzzle) {
		HashSet<Integer> currSet = new HashSet<Integer>(); 

		// Check by row
		for (int r = 0; r < 9; ++r) {
			for (int c = 0; c < 9; ++c) {
				int value = solvedPuzzle.getValue(r, c);
				if ((value < 1) || (value > 9)) {
					throw new IllegalStateException("Value " + value + " (row: " + r + ", col: " + c + ") must be in the range [1, 9]");
				} else if (currSet.contains(value)) {
					throw new IllegalStateException("Value " + value + " (row: " + r + ", col: " + c + ") was already used in this row.");
				}
				currSet.add(value);
			}
			currSet.clear();
		}

		// Check by column
		for (int c = 0; c < 9; ++c) {
			for (int r = 0; r < 9; ++r) {
				int value = solvedPuzzle.getValue(r, c);
				if ((value < 1) || (value > 9)) {
					throw new IllegalStateException("Value " + value + " (row: " + r + ", col: " + c + ") must be in the range [1, 9]");
				} else if (currSet.contains(value)) {
					throw new IllegalStateException("Value " + value + " (row: " + r + ", col: " + c + ") was already used in this column.");
				}
				currSet.add(value);
			}
			currSet.clear();
		}

		// Check by block.
		for (int b = 0; b < 8; ++b) {
			for (int r = (b / 3 * 3); r < (b / 3 * 3 + 3); ++r) {
				for (int c = b % 3 * 3; c < b % 3 * 3 + 3; ++c) {
					int value = solvedPuzzle.getValue(r, c);
					if ((value < 1) || (value > 9)) {
						throw new IllegalStateException("Value " + value + " (block: " + b + ", row: " + r + ", col: " + c + ") must be in the range [1, 9]");
					} else if (currSet.contains(value)) {
						throw new IllegalStateException("Value " + value + " (block: " + b + ", row: " + r + ", col: " + c + ") was already used in this block.");
					}
					currSet.add(value);
				}
			}
			currSet.clear();
		}
	}
}
