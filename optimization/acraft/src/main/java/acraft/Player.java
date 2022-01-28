package acraft;

import java.util.Scanner;

import fast.read.FastReader;

/**
 * Auto-generated code below aims at helping you parse
 * the standard input according to the problem statement.
 **/
public class Player {
	State initState = new State();
	AIDFS ai = new AIDFS();
	
    public static void main(String args[]) {
        FastReader in = new FastReader(System.in);
        
        new Player().play(in);
    }

	private void play(FastReader in) {
		initState.read(in);
		initState.print();
		
		myHumanBestScore(initState, Pos.get(9, 4, Pos.DOWN), Pos.get(9, 2, Pos.UP));
		
		
		ai.think(initState);
		ai.output();
		
	}

	private void myHumanBestScore(State initState, Pos... positions) {
		for (Pos p : positions) {
			initState.applyArrow(p);
		}
		int score = initState.calculateScore();
		System.err.println("My best Score would be "+score);
		for (Pos p : positions) {
			initState.removeArrow(p);
		}

		
	}
}
