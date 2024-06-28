package spring2021;

import java.util.Scanner;

import spring2021.ai.EvaluationNext;
import spring2021.ai.bs.BeamSearch;

public class ActionsComparator {

	private static Player player;
	private static EvaluationNext evaluator = new EvaluationNext();
	
	public static void main(String[] args) {
		player = new Player();
		readGlobal(player);
		readTurn(player);

//		findActions();
//		debugSeeds();
		 compareActions();
	
		//	compare1stActionScore();
		
	}

	private static void findActions() {
		Player.start = System.currentTimeMillis()+200;
		beamSearch.think(player.state, 6);
		beamSearch.debugActions();
	}

	static BeamSearch beamSearch = new BeamSearch();
	
	private static void compare1stActionScore() {
		System.out.println("//////////////////////////////////////////");
		State state1 = new State();
		state1.copyFrom(player.state);

		Player.start = System.currentTimeMillis()+200;
		beamSearch.think(state1, 5);
		System.err.println("Score1 = "+beamSearch.bestFitnesse);
		beamSearch.debugActions();
	
		State state2 = new State();
		state2.copyFrom(player.state);
		
		Simulator.simulate(state2, Action.complete(28), 0);
		Player.start = System.currentTimeMillis()+200;
		beamSearch.think(state2, 5);
		System.err.println("Score2 = "+beamSearch.bestFitnesse);
		beamSearch.debugActions();
		
	}

	private static void compareActions() {
		System.err.println("First Actions ");
		System.err.println("******************");
		State state1 = new State();
		state1.copyFrom(player.state);
		double score1 = applyActions(state1, "COMPLETE 2;GROW 7;SEED 11 2;COMPLETE 4;COMPLETE 31;WAIT;COMPLETE 11;COMPLETE 34;GROW 2;SEED 27 4;WAIT;GROW 2;WAIT;GROW 4;SEED 2 11;GROW 29;WAIT;COMPLETE 29;WAIT;");
		
		System.err.println("Second Actions ");
		System.err.println("******************");
		State state2 = new State();
		state2.copyFrom(player.state);
		double score2 = applyActions(state2, "COMPLETE 2;GROW 7;SEED 11 2;COMPLETE 4;WAIT;COMPLETE 31;COMPLETE 11;COMPLETE 34;GROW 2;SEED 27 4;WAIT;GROW 2;WAIT;GROW 4;SEED 2 11;GROW 29;WAIT;COMPLETE 29;WAIT;");
		
		System.err.println("******************");
		System.err.println(String.format("Score 1 = %f",score1));
		System.err.println(String.format("Score 2 = %f",score2));
		System.err.println("******************");
	}
	
	private static void debugSeeds() {
		for (int i=0;i<37;i++) {

			int size = player.state.trees[i];
			if (size == -1) continue; // nothing here
			if (!player.state.isMine(i)) continue;
			if (player.state.isDormant(i)) continue;
			
			for (int s = 0; s < Cell.distanceIndexesFE[i][size]; s++) {
				State state = new State();
				state.copyFrom(player.state);

				int index2 = Cell.distanceIndexes[i][s];
				if (state.trees[index2] >= 0)	continue; // already something
				if (State.forbidenSeedCells[index2]) continue; // not allowd to seed here
				if (State.richness[index2] == 0) continue; // TODO remove is we are sure of cells we try

				Action seedAction = Action.seed(i, index2);
				Simulator.simulate(state, seedAction, 0);
				Simulator.simulate(state, Action.WAIT, 0);
				
				double score = evaluator.evaluate(state);
				System.err.println(""+seedAction+" => "+score);
			}
			

		}
	}

	private static double applyActions(State state, String actions) {
		String actionsStr[] = actions.split(";");
		double score = 0.0;
		
		int i = 0;
		for (String a : actionsStr) {
			i++;
			Action action = Action.fromString(a);
			Simulator.simulate(state, action, 0);
			if (action == Action.WAIT) {
				double currentScore = evaluator.evaluate(state);

				score = score * 1.05 + currentScore;
				System.err.println(">>>> Score of day  "+state.day+" "+currentScore+" total = "+score);
				evaluator.debug(state);
			}
		}
		return score;
	}

	public static void readGlobal(Player player) {
		String global = "^37\r\n"
				+ "^0 3 1 2 3 4 5 6 \r\n"
				+ "^1 3 7 8 2 0 6 18 \r\n"
				+ "^2 3 8 9 10 3 0 1 \r\n"
				+ "^3 3 2 10 11 12 4 0 \r\n"
				+ "^4 3 0 3 12 13 14 5 \r\n"
				+ "^5 3 6 0 4 14 15 16 \r\n"
				+ "^6 3 18 1 0 5 16 17 \r\n"
				+ "^7 2 19 20 8 1 18 36 \r\n"
				+ "^8 2 20 21 9 2 1 7 \r\n"
				+ "^9 2 21 22 23 10 2 8 \r\n"
				+ "^10 2 9 23 24 11 3 2 \r\n"
				+ "^11 2 10 24 25 26 12 3 \r\n"
				+ "^12 2 3 11 26 27 13 4 \r\n"
				+ "^13 2 4 12 27 28 29 14 \r\n"
				+ "^14 2 5 4 13 29 30 15 \r\n"
				+ "^15 2 16 5 14 30 31 32 \r\n"
				+ "^16 2 17 6 5 15 32 33 \r\n"
				+ "^17 2 35 18 6 16 33 34 \r\n"
				+ "^18 2 36 7 1 6 17 35 \r\n"
				+ "^19 1 -1 -1 20 7 36 -1 \r\n"
				+ "^20 1 -1 -1 21 8 7 19 \r\n"
				+ "^21 1 -1 -1 22 9 8 20 \r\n"
				+ "^22 1 -1 -1 -1 23 9 21 \r\n"
				+ "^23 0 22 -1 -1 24 10 9 \r\n"
				+ "^24 1 23 -1 -1 25 11 10 \r\n"
				+ "^25 1 24 -1 -1 -1 26 11 \r\n"
				+ "^26 1 11 25 -1 -1 27 12 \r\n"
				+ "^27 1 12 26 -1 -1 28 13 \r\n"
				+ "^28 1 13 27 -1 -1 -1 29 \r\n"
				+ "^29 1 14 13 28 -1 -1 30 \r\n"
				+ "^30 1 15 14 29 -1 -1 31 \r\n"
				+ "^31 1 32 15 30 -1 -1 -1 \r\n"
				+ "^32 0 33 16 15 31 -1 -1 \r\n"
				+ "^33 1 34 17 16 32 -1 -1 \r\n"
				+ "^34 1 -1 35 17 33 -1 -1 \r\n"
				+ "^35 1 -1 36 18 17 34 -1 \r\n"
				+ "^36 1 -1 19 7 18 35 -1 "
				;
		player.readGlobal(new Scanner(global.replaceAll("\\^", "")));
	}
	
	public static void readTurn(Player player) {
		String input="^14 16 17 20 10 61 0 17\r\n"
				+ "^0 2 0 0\r\n"
				+ "^2 3 1 0\r\n"
				+ "^4 3 1 0\r\n"
				+ "^6 1 1 0\r\n"
				+ "^7 0 1 0\r\n"
				+ "^8 2 0 0\r\n"
				+ "^10 0 0 0\r\n"
				+ "^11 3 1 0\r\n"
				+ "^14 3 0 0\r\n"
				+ "^17 3 0 0\r\n"
				+ "^21 2 1 0\r\n"
				+ "^25 2 0 0\r\n"
				+ "^27 2 1 0\r\n"
				+ "^29 2 1 0\r\n"
				+ "^31 3 1 0\r\n"
				+ "^34 3 1 0\r\n"
				+ "^36 3 0 0\r\n"
				+ "^0 ";
		player.readTurn(new Scanner(input.replaceAll("\\^", "")));
	}

}
