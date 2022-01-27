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
		
		int score = initState.calculateScore();
		System.err.println("Default Score is "+score);
		
		ai.think(initState);
		ai.output();
		
	}
}
