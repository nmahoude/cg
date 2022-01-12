package xmashrush2;

import fast.read.FastReader;
import xmashrush2.ai.MoveAI;
import xmashrush2.ai.PushAI;

public class Player {

	State currentState = new State();
	MoveAI moveAI = new MoveAI();
	PushAI pushAI = new PushAI();
	private long start;
	
	public static void main(String args[]) {
		FastReader in = new FastReader(System.in);
	
		new Player().play(in);
	}
	
	public void play(FastReader in) {
		while (true) {
			currentState.read(in);
			start = System.currentTimeMillis();
			currentState.debugGrid();
			
			
			BFS bfs = new BFS();
			bfs.process(currentState, currentState.agents[0].pos);
			bfs.debugReachablePos();

			currentState.debugQuestItems();

			
			if (currentState.moveTurn() ) {
				moveAI.think(currentState);
				moveAI.output();
				System.err.println("Move - think in "+(System.currentTimeMillis() - start));
			} else {
				pushAI.think(currentState);
				pushAI.output();
				System.err.println("Push - think in "+(System.currentTimeMillis() - start));
			}
			
		}
	}
}
