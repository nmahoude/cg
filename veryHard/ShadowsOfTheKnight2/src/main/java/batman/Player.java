package batman;

import java.util.Scanner;

public class Player {

	private static final boolean DEBUG = false;
	private static int W;
	private static int H;
	private static int horizontal[], vertical[];
	private static boolean foundHorizontal;
	private static boolean foundVertical;

	private static int foundX, foundY;
	
	public static void main(String args[]) {
		Scanner in = new Scanner(System.in);
		W = in.nextInt();
		H = in.nextInt();
		System.err.println("Building : "+W+" x "+H);
		horizontal = new int[W];
		vertical= new int[H];

		int N = in.nextInt(); // maximum number of turns before game over.

		int X0 = in.nextInt();
		int Y0 = in.nextInt();

		State state = new State(H,Y0);
		boolean foundY = false;
		boolean foundX = false;
		boolean findXFirst = false;
		
		int targetX = -1;
		int targetY = -1;
		
		
		// game loop
		while (true) {
			BombDir bombDir = BombDir.valueOf(in.next());

			if (!foundY) {
				state.apply(bombDir);
				state.debug();
				if (state.min == state.max) {
					System.err.println("Found Y @ "+state.min);
					foundY = true;
					targetY = state.min;
					state = new State(W, X0);
					System.err.println("Looking for X ");
					if (W == 1) {
						System.err.println("Already found X ! ");
						targetX = X0;
						foundX = true;
					} else {
						System.err.println("Going X axis from "+X0);
						state.debug();
						X0 = state.decide(BombDir.UNKNOWN);
					}
				} else {
					Y0 = state.decide(bombDir);
				}
			} else {
				state.apply(bombDir);
				state.debug();
				X0 = state.decide(bombDir);
				if (state.max == state.min) {
					System.err.println("found X ! @ "+state.min);
					targetX = state.min;
					foundX = true;
				}
			}
			
			if (foundX) {
				System.out.println("" + targetX + " " + targetY);
			} else {
				System.out.println("" + X0 + " " + Y0);
			}
		}
	}
}