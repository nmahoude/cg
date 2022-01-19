package xmashrush2;

import fast.read.FastReader;
import xmashrush2.ai.MoveAI;
import xmashrush2.ai.PushAI;
import xmashrush2.ai.PushTreeAI;

public class Player {

	State currentState = new State();
	State lastState = new State();
	
	MoveAI moveAI = new MoveAI();
	PushAI pushAI = new PushAI();
	private long start;
	private PushAction lastPushAction;
	public static PushAction forbiddenAction;
	
	public static void main(String args[]) {
		PushTreeAI.resetCache();
		
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
				if (lastState.hasSameCells(currentState)) {
					if (currentState.agents[0].score > currentState.agents[1].score) {
						System.err.println("State didn't change but I'm ahead, so let's continue");
					} else {
						forbiddenAction = lastPushAction;
						System.err.println("State didn't change, forbidden action is now "+forbiddenAction);
					}
				} else {
					forbiddenAction = null;
				}
				
				lastState.copyFrom(currentState);
				pushAI.think(currentState);
				pushAI.output();
				lastPushAction = pushAI.action;
				
				System.err.println("Push - think in "+(System.currentTimeMillis() - start));
			}
			
		}
	}
}
