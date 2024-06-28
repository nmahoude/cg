package fall2022;

import java.util.List;

import fall2022.ai.AI;
import fall2022.ai.ai2.AI2;
import fall2022.sim.Sim;
import fast.read.FastReader;

public class Player {
	public static boolean DEBUG_OUPUT = true;
	
	public static State predictedNextState = new State();
	public State state = new State();
	public static long start;
	public static String message;
	public static int frontierIsProtected = 0;
	public static AI ai = new AI2();

	public static void main(String[] args) {
		FastReader in = new FastReader(System.in);
    new Player().play(in);
	}

	private void play(FastReader in) {
		state.readGlobal(in);
		
		while(true) {
			message = "";
			state.read(in);
			updatePredictions();
			
			think();
			long end = System.currentTimeMillis();
			System.err.println("Time : "+(end-start));
		}
	}


	private void think() {
		if (State.turn == 0) {
			 // faire chauffer la JVM
			for (int i=0;i<5;i++) {
				ai.think(state);
			}
		}
		
		
		List<Action> actions = ai.think(state);
		
		String command = "";
		predictedNextState.copyFrom(state);
		for (Action action : actions) {
			// System.err.println(action.debugString());
			command += action;
			predictedNextState.apply(action);
		}
		Sim.oneTurn(predictedNextState);
		
		command += "MESSAGE ";
		if (Logger.hasError) command+="🤯 ";
		if (Logger.hasWarning) command+="⚠ ";
		command +=(state.turn+1)+"|" + (System.currentTimeMillis()-start)+"ms;";
		
		if ("".equals(command)) {
			System.out.println("WAIT");
		} else {
			System.out.println(command);
		}

		Logger.reset();
	}

	public static int noAggressionMap[] = new int[Pos.MAX_OFFSET];
	private void updatePredictions() {
		for (Pos p : Pos.allMapPositions) {
			int o = p.o;
			if (state.o[o] == predictedNextState.o[o]
					&& !state.isNeutral(p)
					&& state.u[o] == predictedNextState.u[o] 
					&& state.u[o] > 0) {
				noAggressionMap[o]++;
				//System.err.println("No aggression on "+p+" for "+noAggressionMap[o]);
			} else {
				noAggressionMap[o] = 0;
			}
		}
	}

}
