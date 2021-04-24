package ttff;

public class State {
	int cells[] = new int[16];
	int score;
	long currentSeed;
	
	public State up() {
		State state = new State();
		state.score = score;
		
		for (int x = 0; x < 4; x++) {
			int index = 0;
			int lastValue = -1;
			for (int y = 0; y < 4; y++) {
				if (cells[x + 4 * y] == 0) {
					// nothing
				} else {
					if (lastValue == cells[x + 4 * y]) {
						// add
						state.cells[x + 4 * (index - 1)] = lastValue * 2;
						state.score += lastValue * 2;
						lastValue = -1;
					} else {
						state.cells[x + 4 * index] = cells[x + 4 * y];
						lastValue = cells[x + 4 * y];
						index++;
					}
				}
			}
			for (; index < 4; index++) {
				state.cells[x + 4 * index] = 0;
			}
		}
		return state;
	}

	public State left() {
		State state = new State();
		state.score = score;
		for (int y = 0; y < 4; y++) {
			int index = 0;
			int lastValue = -1;
			for (int x = 0; x < 4; x++) {
				if (cells[x + 4 * y] == 0) {
					// nothing
				} else {
					if (lastValue == cells[x + 4 * y]) {
						// add
						state.cells[(index - 1) + 4 * y] = lastValue * 2;
						state.score += lastValue * 2;
						lastValue = -1;
					} else {
						state.cells[index + 4 * y] = cells[x + 4 * y];
						lastValue = cells[x + 4 * y];
						index++;
					}
				}
			}
			for (; index < 4; index++) {
				state.cells[index + 4 * y] = 0;
			}
		}
		return state;
	}

	public State down() {
		State state = new State();
		state.score = score;
		for (int x = 0; x < 4; x++) {
			int index = 3;
			int lastValue = -1;
			for (int y = 3; y >= 0; y--) {
				if (cells[x + 4 * y] == 0) {
					// nothing
				} else {
					if (lastValue == cells[x + 4 * y]) {
						// add
						state.cells[x + 4 * (index + 1)] = lastValue * 2;
						state.score += lastValue * 2;
						lastValue = -1;
					} else {
						state.cells[x + 4 * index] = cells[x + 4 * y];
						lastValue = cells[x + 4 * y];
						index--;
					}
				}
			}
			for (; index >= 0; index--) {
				state.cells[x + 4 * index] = 0;
			}
		}
		return state;
	}

	public State right() {
		State state = new State();
		state.score = score;
		for (int y = 0; y < 4; y++) {
			int index = 3;
			int lastValue = -1;
			for (int x = 3; x >= 0; x--) {
				if (cells[x + 4 * y] == 0) {
					// nothing
				} else {
					if (lastValue == cells[x + 4 * y]) {
						// add
						state.cells[(index + 1) + 4 * y] = lastValue * 2;
						state.score += lastValue * 2;
						lastValue = -1;
					} else {
						state.cells[index + 4 * y] = cells[x + 4 * y];
						lastValue = cells[x + 4 * y];
						index--;
					}
				}
			}
			for (; index >= 0; index--) {
				state.cells[index + 4 * y] = 0;
			}
		}
		return state;
	}

	public void debug() {
		System.err.println("Score = " + score + " , eval = " + eval());
		for (int y = 0; y < 4; y++) {
			for (int x = 0; x < 4; x++) {
				System.err.print(" " + cells[x + 4 * y]);
			}
			System.err.println();
		}
	}

	public double eval() {
		return 1000.0 * score + 100.0 * evalNeighbors();
	}

	private int evalNeighbors() {
		int corresponding = 0;

		for (int y = 0; y < 4; y++) {
			int last = cells[0 + 4 * y];
			for (int x = 1; x < 4; x++) {
				if (cells[x + 4 * y] == 0)
					continue;
				if (last == cells[x + 4 * y])
					corresponding++;
				last = cells[x + 4 * y];
			}
		}

		for (int x = 0; x < 4; x++) {
			int last = cells[x + 4 * 0];
			for (int y = 1; y < 4; y++) {
				if (cells[x + 4 * y] == 0)
					continue;

				if (last == cells[x + 4 * y])
					corresponding++;
				last = cells[x + 4 * y];
			}
		}

		return corresponding;
	}

	/**
	 * from the currentState & seed, predict where the next cell will be filled
	 */
	public void predict() {
		System.err.println("Current state");
		this.debug();

		// TODO optimize
		long freeCells = 0;
		for (int x = 0; x < 4; x++) {
			for (int y = 0; y < 4; y++) {
				if (cells[x + 4 * y] == 0)
					freeCells++;
			}
		}

		int spawnIndex = (int) (currentSeed % freeCells); // TODO quick find of the real location
		int value = (currentSeed & 0x10) == 0 ? 2 : 4;
		long seed = currentSeed * currentSeed % 50515093L; // TODO cache for all children

		System.err.println("Previsions ");
		System.err.println("It will be a " + value + " @ empty cell " + spawnIndex);
		System.err.println("next seed will be " + seed);

	}
}
