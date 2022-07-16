package blockthespreadingfire;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import fast.read.FastReader;

public class Player {

	private static final State STATE = new State();
	private static final ThreadLocalRandom random = ThreadLocalRandom.current();
	private static final boolean DEBUG = false;
	
	public static void main(String args[]) {
		FastReader in = new FastReader(System.in);
		new Player().play(in);
	}

	private void play(FastReader in) {
		STATE.readGlobal(in);

		System.err.println("calulating burn bfs ...");
		BFS bfs = new BFS();
		bfs.calculate(STATE.grid, State.fireStart, State.houseFireDuration, State.treeFireDuration, State.fireStart, Integer.MAX_VALUE);
		if (DEBUG) {
			bfs.debug();
		}
		System.err.println("Done");
		
		BFS distBfs = new BFS();

		long start = System.currentTimeMillis();

		int bestScore =  -1;
		List<Pos> bestBoundary = new ArrayList<>();
		
		while (System.currentTimeMillis() - start < 4900) {
		//for (int tries=0;tries<1000;tries++) {
		
			Pos startPos = Pos.from(1+random.nextInt(State.width-2), 1+random.nextInt(State.height-2));
			int maxRadius = Math.max(Math.max(State.width-startPos.x-1, startPos.x-1), Math.max(State.height-startPos.y-1, startPos.y-1));
			int radius = 1+random.nextInt(maxRadius);
			if (STATE.grid[startPos.offset] == State.SAFE) continue;
			if (startPos == State.fireStart) continue;
			
			
			if (DEBUG) {
				System.err.println("Test : "+startPos + " r="+radius);
			}
			distBfs.calculate(STATE.grid, startPos, 1, 1, State.fireStart, radius);
			if (distBfs.valueInBoundary == -1) continue;
			
			// get the boundary
			int score = distBfs.fireInBoundary ? ( bfs.valueInBoundary - distBfs.valueInBoundary - distBfs.valueOnBoundary) : distBfs.valueInBoundary;
			distBfs.boundary.sort((p1, p2) -> Integer.compare(bfs.dist[p1.offset], bfs.dist[p2.offset]));
			if (DEBUG) {
				System.err.println("=> score = "+score);
				System.err.println("Boundary  :"+distBfs.boundary);
				System.err.println(" fire in the bfs ? "+distBfs.fireInBoundary);
				System.err.println("BFS : ");
				distBfs.debug();
			}
			int time = 0;
			for (Pos p : distBfs.boundary) {
				if (bfs.dist[p.offset] <= time) {
					score = -1;
					break;
				}
				time += STATE.grid[p.offset] == State.HOUSE ? State.houseTreatmentDuration : State.treeTreatmentDuration;
			}
			if (DEBUG && score == -1) {
				System.err.println(" ==>> But fire will reach before :(");
			}
			
			if (score > bestScore) {
				bestScore= score;
				bestBoundary.clear();
				bestBoundary.addAll(distBfs.boundary);
			}
		}
		

		System.err.println("Best score is "+bestScore);
		System.err.println("Best boundary is "+bestBoundary);
		
		
		// game loop
		while (true) {
			STATE.readTurn(in);
			
			if (STATE.cooldown != 0 || bestBoundary.isEmpty()) {
				System.out.println("WAIT"); 
			} else {
				Pos pos = bestBoundary.remove(0);
				System.out.println(""+pos.x+" "+pos.y);
			}
		}	
	}
}
