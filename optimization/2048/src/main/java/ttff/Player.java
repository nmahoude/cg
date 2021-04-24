package ttff;

import java.util.Arrays;
import java.util.Scanner;
import java.util.concurrent.ThreadLocalRandom;

public class Player {
	static State state = new State();
	static Node node = new Node();
	
	public static void main(String args[]) {
		Scanner in = new Scanner(System.in);

		// game loop
		while (true) {
			state.currentSeed = in.nextInt(); // needed to predict the next spawns
			System.err.println(state.currentSeed);
			
			int currentScore = in.nextInt();
			state.score = currentScore;
			node.score = currentScore;
			node.bits = 0;
			
			int mask = 0;
			for (int y = 0; y < 4; y++) {
				for (int x = 0; x < 4; x++) {
					int cell = in.nextInt();
					state.cells[x + 4*y] = cell;
					if (cell != 0) {
						int value = (int)(Math.log(cell) / Math.log(2));
						int decal = (3-x)+4*(3-y);
						System.err.println("Value @ "+x+","+y+" is "+value+" with decal "+decal);
						
						node.bits += (long)value << (4*decal);
						System.err.println("new bits : "+node.bits);
					}
					mask++;
				}
			}
			System.err.println("Current state");
			state.debug();
			node.debug();

			double bestScore = Double.NEGATIVE_INFINITY;
			Node bestNode = null;

			for (int dir=0;dir<4;dir++) {
				Node child = new Node();
				child.dir(dir, node);
				double score = score(child);
				System.err.println(""+child.dirFromParent+" {"+child.score+"} =====> "+score);
				if (child.bits != node.bits && score > bestScore) {
					System.err.println("new best :)");
					bestScore = score;
					bestNode = child;
				}
			}			
			
			if (bestNode != null) {
				System.out.println(bestNode.dirFromParent);
			} else {
				throw new RuntimeException("dont know what to do");
			}
			
		}
	}

	private static double score(Node child) {
		return child.score;
	}

	private static String getBestDirFromState(double bestScore, String bestDir) {
		double score;
		State bestState = state;
		State up = state.up();
//			System.err.println("up ");
//			up.debug();
		if ((score = up.eval()-3) > bestScore) {
			bestDir = "U";
			bestScore = score;
			bestState = up;
		}
		
		State down = state.down();
//			System.err.println("down");
//			down.debug();
		if ((score = down.eval()-1) > bestScore) {
			bestDir = "D";
			bestScore = score;
			bestState = down;
		}
		State right = state.right();
//			System.err.println("right");
//			right.debug();
		if ((score = right.eval()-2) > bestScore) {
			bestDir = "R";
			bestScore = score;
			bestState = right;

		}
		State left = state.left();
//			System.err.println("Left");
//			left.debug();
		if ((score = left.eval()) > bestScore) {
			bestDir = "L";
			bestScore = score;
			bestState = left;

		}
		
		if (false && bestState.score == state.score) {
			System.err.println("Random");
			bestDir = Arrays.asList("D", "U", "L", "R").get(ThreadLocalRandom.current().nextInt(4));
		} else {
			System.err.println("next score is "+bestScore+" instead of "+state.score);
			
			bestState.predict();
			
			
		}
		return bestDir;
	}
}
