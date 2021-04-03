package bantas;

import java.util.Scanner;

/**
 * Try to survive by not falling off
 **/
public class Player {

	public static void main(String args[]) {
		Scanner in = new Scanner(System.in);
		State state = new State();

		
		
		state.myId = in.nextInt() + 1; // Your id, 0 or 1
		int height = in.nextInt(); // height of the grid
		int width = in.nextInt(); // width of the grid
		if (in.hasNextLine()) {
			in.nextLine();
		}

		
		State copy = new State();
		// game loop
		while (true) {
			state.read(in);
			


			double bestScore = Double.NEGATIVE_INFINITY;
			int mines = 0;
			int opp = 0;

			String dir = "NOLOSE";
			double score;
			
			
			System.err.println("UP");
			copy.copy(state);
			copy.pushUp();
			copy.print();
			score = score(copy);
			if (score > bestScore) {
				bestScore= score;
				dir = "UP";
			}
			
			System.err.println("RIGHT");
			copy.copy(state);
			copy.pushRight();
			copy.print();
			score = score(copy);
			if (score > bestScore) {
				bestScore= score;
				dir = "RIGHT";
			}
			
			System.err.println("DOWN");
			copy.copy(state);
			copy.pushDown();
			copy.print();
			score = score(copy);
			if (score > bestScore) {
				bestScore= score;
				dir = "DOWN";
			}

			System.err.println("LEFT");
			copy.copy(state);
			copy.pushLeft();
			copy.print();
			score = score(copy);
			if (score > bestScore) {
				bestScore= score;
				dir = "LEFT";
			}


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
				
				if (state.cells[x][y] == 1) {
					score -= delta;
				} else if (state.cells[x][y] == 2) {
					score += delta;
				}
			}
		}
		
		
		return score + 0.01 * (countMines - countOpp);
	}
}