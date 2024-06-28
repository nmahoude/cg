package spring2021;

import java.util.Scanner;

import spring2021.ai.EvaluationNext;
import spring2021.ai.bs.BSNode;
import spring2021.ai.bs.BeamSearch;

public class Player {
	private static final BeamSearch BEAM_SEARCH = new BeamSearch();
	private static final boolean DEBUG_INPUT = true;
	private static final boolean DEBUG_NEXT_TURN = false;
	private static final boolean DEBUG_AI = false;
	public static final boolean DEBUG_BESTPATH = false;
	
	// Constant to alter bot behavior
	public static final long MAX_TIME = 90; // TODO 100 ms !
	public static final int PERCENT_ACTIONS_WITH_SEEDS = 5;
	public static final int GROW_UNTIL_DAY = 12;
	public static final int MAX_WAIT = 6;
	private static final boolean DEBUG_FUTURE = true;
	private static final boolean DEBUG_STATS = true;
	public static final EvaluationNext evaluator = new EvaluationNext();


	public static int turn;
	public static long start;

	public int cells[][];
	public int width;
	public int height;

	public State state = new State();

	public Action[] bestActions = new Action[100];
	public static int bestActionsFE;

	public static void main(String[] args) {
		Scanner in = new Scanner(System.in);
		new Player().play(in);
	}

	public void play(Scanner in) {
		readGlobal(in);
		while (true) {
			readTurn(in);
			think();
		}
	}

	public void think() {
		if (turn == 1) {
			// Warmup
			start += 600;
		}
		
		
		// THINK THINK THINK THINK THINK
		Action action = thinkBS();
		// THINK THINK THINK THINK THINK

		if (DEBUG_NEXT_TURN) {
			System.err.println("Current state is ");
			state.debugInfos();
			
			System.err.println("next turn evaluation : "+action);
			State tmp = new State();
			tmp.copyFrom(state);
			if (action != Action.WAIT) {
				Simulator.simulate(tmp, action, 0);
			} else {
				Simulator.endTurn(tmp);
			}
			tmp.debugInfos();
		}
		System.err.println("Perf : "+(System.currentTimeMillis() - Player.start)+" ms");
		
		System.out.println(action);
	}

	public void readGlobal(Scanner in) {
		turn = 0;

		int numberOfCells = in.nextInt(); // 37
		if (DEBUG_INPUT)
			System.err.println("^" + numberOfCells);
		for (int i = 0; i < numberOfCells; i++) {
			int index = in.nextInt();
			int richness = in.nextInt();
			if (DEBUG_INPUT) System.err.print("^" + index + " " + richness + " ");

			State.richness[index] = richness;

			Cell current = Cell.cells[index];
			for (int n=0;n<6;n++) {
				int neighIndex = in.nextInt();
				if (DEBUG_INPUT) System.err.print(neighIndex+" ");
				current.neighbors[n] = neighIndex != -1 ? Cell.cells[neighIndex] : Cell.WALL;
			}
			System.err.println();
			
		}
		Cell.precalculate();
		SunMap.precalculate();
	}

	public void readTurn(Scanner in) {
		turn++;
		System.err.println("Turn : "+turn);
		state.init();

		int day = in.nextInt(); // the game lasts 24 days: 0-23
		Player.start = System.currentTimeMillis();

//		if (turn == 1) {
//			Player.start += 900;
//		}
		
		System.err.println("day : "+day+" turn : "+turn);
		int nutrients = in.nextInt(); // the base score you gain from the next COMPLETE action
		int sun = in.nextInt(); // your sun points
		int score = in.nextInt(); // your current score
		int oppSun = in.nextInt(); // opponent's sun points
		int oppScore = in.nextInt(); // opponent's score
		boolean oppIsWaiting = in.nextInt() != 0; // whether your opponent is asleep until the next day
		int numberOfTrees = in.nextInt(); // the current amount of trees

		state.nutrients = nutrients;
		state.day = day;
		state.sun[0] = sun;
		state.score[0] = score;
		state.sun[1] = oppSun;
		state.score[1] = oppScore;
		state.oppIsWaiting = oppIsWaiting;

		if (DEBUG_INPUT)
			System.err.println("^" + day + " " + nutrients + " " + sun + " " + score + " " + oppSun + " " + oppScore + " "
					+ (oppIsWaiting ? 1 : 0) + " " + numberOfTrees);

		for (int i = 0; i < numberOfTrees; i++) {
			int cellIndex = in.nextInt(); // location of this tree
			int size = in.nextInt(); // size of this tree: 0-3
			boolean isMine = in.nextInt() != 0; // 1 if this is your tree
			boolean isDormant = in.nextInt() != 0; // 1 if this tree is dormant
			state.trees[cellIndex] = size;
			state.treesCount[5*(isMine?0:1)+size]++;
			
			if (isMine) {
				state.currentTotalRichness += State.richness[cellIndex];
				state.setMine(cellIndex);
			} else {
				state.unsetMine(cellIndex);
			}
			
			if (isDormant) {
				state.setDormant(cellIndex);
			}
			if (DEBUG_INPUT)
				System.err.println("^" + cellIndex + " " + size + " " + (isMine ? 1 : 0) + " " + (isDormant ? 1 : 0));

		}

		int numberOfPossibleMoves = in.nextInt();
		if (in.hasNextLine()) {
			in.nextLine();
		}
		for (int i = 0; i < numberOfPossibleMoves; i++) {
			in.nextLine();
		}
		if (DEBUG_INPUT)
			System.err.println("^0 "); // don't print possible actions, it is bullshit

		updateForbidenCells();

//		simulateOpponent();
//		evaluateSituation();
		
		if (DEBUG_STATS) {
			System.err.println("***********************");
			System.err.println("       STATS");
			int count1 = 0, count2 = 0;
			for (int i=0;i<4;i++) {
				System.err.println(String.format("T%d => %2d - %2d", i, state.treesCount[0*5+i], state.treesCount[1*5+i]));
				count1+=state.treesCount[0*5+i];
				count2+=state.treesCount[1*5+i];
			}
			System.err.println("-----------------------");
			System.err.println(String.format("Tot=> %2d - %2d", count1, count2));
			
			System.err.println("***********************");
		}
		
	}

	private void evaluateSituation() {
		State opp = new State();
		opp.copyFrom(state);
		opp.swap();
		
		double myScore = evaluator.evaluate(state);
		double hisScore = evaluator.evaluate(opp);
		System.err.println("----------------------------------------------");
		System.err.println("Situation evaluation :");
		System.err.println(String.format("me  : %10.2f", myScore));
		System.err.println("    vs");
		System.err.println(String.format("opp : %10.2f", hisScore));
		System.err.println("----------------------------------------------");
		
		System.err.println("My stats");
		evaluator.debug(state);
		System.err.println("His stats");
		evaluator.debug(opp);
		
		
	}

	private void simulateOpponent() {
		State opp = new State();
		opp.copyFrom(state);
		opp.swap();
		
		Action action = BEAM_SEARCH.think(opp, 2);
		System.err.println("Predicted action is "+action);
		Simulator.simulate(state, action, 1);
	}

	private void updateForbidenCells() {
		state.initForbidenSeedsCells();
    int forbidenRange = state.day < 10 ? 2 : 1;
		state.updateForbidenSeedsCells(forbidenRange);

		
//		state.initForbidenSeedsCells();
//		int forbidenRange = state.day < 9 ? 2 : 1;
//		state.updateForbidenSeedsCells(3);
//		
//		// check if some seeds are reachable
//		if (state.costToSeed(0) == 0 && state.day > 5) {
//			int count = 0;
//			for (int treeIndex = 0; treeIndex < 37; treeIndex++) {
//				
//				int size = state.trees[treeIndex];
//				if (size <= 0) continue;
//				if (!state.isMine(treeIndex)) continue;
//	      if (state.isDormant(treeIndex)) continue;
//				
//				for (int s = 0; s < Cell.distanceIndexesFE[treeIndex][size]; s++) {
//					int index = Cell.distanceIndexes[treeIndex][s];
//					if (state.trees[index] >= 0)	continue; // already something
//					if (State.forbidenSeedCells[index]) continue; // not allowd to seed here
//					if (State.richness[index] == 0) continue; // TODO remove is we are sure of cells we try
//					
//					count++;
//				}
//			}
//			
//			if (count == 0) {
//				System.err.println("Need to relax seeds ...");
//				forbidenRange = state.day < 9 ? 2 : 1;
//				state.updateForbidenSeedsCells(forbidenRange);
//			}
//		}
	}

	public Action thinkBS() {
		
		Action action = BEAM_SEARCH.think(state, MAX_WAIT);
		// count the nodes
		BSNode current = BEAM_SEARCH.bestNode;
		int d = 0;
		while (current.fromAction != null) {
			d++;
			current = current.parent;
		}
		d--;
		
		bestActionsFE = d;
		current = BEAM_SEARCH.bestNode;
		while (current.fromAction != null) {
			bestActions[d--] = current.fromAction;
			current = current.parent;
		}
		if (DEBUG_FUTURE) {
			debugFuture();
		}

		if (action.type != Action.COMPLETE) {
			// put complete 1st ...
			int i=0;
			while (bestActions[i] != Action.WAIT) {
				if (bestActions[i].type == Action.COMPLETE) {
					System.err.println("Replacing ! ");
					action = bestActions[i];
					break;
				}
				i++;
			}
		}
		
		
		return action;
	}
	
	private void debugFuture() {
		if (DEBUG_FUTURE) {
			System.err.println("Best actions ");
			for (int i = 0; i < bestActionsFE; i++) {
				System.err.print(bestActions[i]+";");
			}
			System.err.println();
		}
	}
}
