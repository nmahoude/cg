package bantas;

import java.util.Scanner;

public class MinimaxTest {


	public static void main(String[] args) {
		String input = "" 
				  + "1 1 1 1 1 1 0  \r\n"
				  + "1 1 1 0 0 0 0  \r\n"
				  + "0 1 1 0 0   1 1\r\n"
				  + "1 0 0 0 0 0   1\r\n"
				  + "0 1 1 0 1 0   1\r\n"
				  + "0 1 1 1 0 0 0  \r\n"
				  + "1 1 0 1 0 0   1\r\n"
				  + "0 0 1 1 0 0 0  ";
		
		
		State state = new State();
		State.ME = 1;
		State.OPP = 2;
		state.read(new Scanner(input));
		
		Minimax max = new Minimax();
		
		System.err.println("Warm up");
		for (int i=0;i<200;i++) {
			max.think(state);
		}
		
		System.err.println("Start ! ");
		for (int i=0;i<500;i++) {
			max.think(state);
		}
		
	}
	
}
