package bantas;

import java.util.Scanner;

/**
 * Try to survive by not falling off
 **/
public class Player {
	static int turn = 0;
	
	public static void main(String args[]) {
		Scanner in = new Scanner(System.in);
		State state = new State();

		
		StateCache.reset();
		
		state.ME = in.nextInt() + 1; // Your id, 0 or 1
		state.OPP = 3 - state.ME;
		
		int height = in.nextInt(); // height of the grid
		int width = in.nextInt(); // width of the grid
		if (in.hasNextLine()) {
			in.nextLine();
		}

		
		Minimax minimax = new Minimax();
		
		// game loop
		while (true) {
			state.read(in);
			turn++;
			System.err.println("Starting turn "+ turn);

			Dir dir = minimax.think(state);

			System.out.println(dir); // UP | RIGHT | DOWN | LEFT
		}
	}

	private static double score(State state) {
		int countMines = state.countMines();
		int countOpp = state.countOpp();
		
		if (countMines == 0) return Double.NEGATIVE_INFINITY;
		if (countOpp == 0) return Double.POSITIVE_INFINITY;
		
		double score = 0.0;
		
		double centerX = (state.maxWidth - state.minWidth) / 2.0;
		double centerY = (state.maxHeight- state.minHeight) / 2.0;
		
		
		for (int y=state.minHeight;y<=state.maxHeight;y++) {
			for (int x=state.minWidth;x<=state.maxWidth;x++) {
				double deltaX = Math.abs(x - centerX);
				double deltaY = Math.abs(y - centerY);
				double delta = deltaX+deltaY;
				
				if (state.cells[x+8*y] == 1) {
					score -= delta;
				} else if (state.cells[x+8*y] == 2) {
					score += delta;
				}
			}
		}
		
		
		return score + 0.01 * (countMines - countOpp);
	}
}