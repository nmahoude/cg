package bender_ep4;

import java.util.List;

import fast.read.FastReader;

public class Player {
	static final State state = new State();
	
	public static void main(String args[]) {
		FastReader in = new FastReader(System.in);

		state.read(in);

		AStar2 astar = new AStar2();
		List<Pos> path = astar.compute(state, State.start, State.target);
		System.err.println("Path : ");
		for (Pos p : path) {
			System.err.print(p+" -> ");
		}
		
		String output="";
		for (int i=1;i<path.size();i++) {
			output+=directionFrom(path.get(i-1), path.get(i));
		}
		
		System.out.println(output);
	}

	private static String directionFrom(Pos from, Pos to) {
		if (to.x - from.x == 1) return "R";
		if (to.x - from.x == -1) return "L";
		
		if (to.y - from.y == 1) return "D";
		if (to.y - from.y == -1) return "U";
		
		return null;
	}
}
