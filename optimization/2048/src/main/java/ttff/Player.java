package ttff;

import java.util.Scanner;

public class Player {
	static Node node = new Node();
	
	public static void main(String args[]) {
		Scanner in = new Scanner(System.in);

		// game loop
		while (true) {
			int seed = in.nextInt();
			System.err.println("currentSeed = " +seed);
			
			int currentScore = in.nextInt();
			node.score = currentScore;
			node.seed = seed;
			node.bits = 0;
			
			int mask = 0;
			for (int y = 0; y < 4; y++) {
				for (int x = 0; x < 4; x++) {
					int cell = in.nextInt();
					if (cell != 0) {
						int value = (int)(Math.log(cell) / Math.log(2));
						int decal = (3-x)+4*(3-y);
//						System.err.println("Value @ "+x+","+y+" is "+value+" with decal "+decal);
						
						node.bits += (long)value << (4*decal);
//						System.err.println("new bits : "+node.bits);
					}
					mask++;
				}
			}
			System.err.println("Current state");
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
				bestNode.predict(node);
				
				
				System.out.println(bestNode.dirFromParent);
			} else {
				throw new RuntimeException("dont know what to do");
			}
			
		}
	}

	private static double score(Node child) {
		return child.score;
	}

}
