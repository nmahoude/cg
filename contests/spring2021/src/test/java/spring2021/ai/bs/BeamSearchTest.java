package spring2021.ai.bs;

import java.util.Scanner;

import spring2021.Player;

public class BeamSearchTest {
	Player player;
	
	public static void main(String[] args) {
		new BeamSearchTest().debug();
	}

	private void debug() {
		player = new Player();
		readGlobal(player);
		readTurn_oneTree(player);
		
		
		BeamSearch ai = new BeamSearch();

		Player.start = System.currentTimeMillis();
		Player.start = System.currentTimeMillis()+10_000_000;
		ai.think(player.state, 6);

		
	}
	
	public static void readTurn_oneTree(Player player) {
		String input="0 11 12 160 8 0 0 1\r\n"
				+ "0 1 1 0\r\n"
				+ "0 ";
		player.readTurn(new Scanner(input));
	}

	public static void readGlobal(Player player) {
		String global = "37\r\n"
				+ "0 3 1 2 3 4 5 6 \r\n"
				+ "1 0 7 8 2 0 6 18 \r\n"
				+ "2 3 8 9 10 3 0 1 \r\n"
				+ "3 3 2 10 11 12 4 0 \r\n"
				+ "4 0 0 3 12 13 14 5 \r\n"
				+ "5 3 6 0 4 14 15 16 \r\n"
				+ "6 3 18 1 0 5 16 17 \r\n"
				+ "7 2 19 20 8 1 18 36 \r\n"
				+ "8 2 20 21 9 2 1 7 \r\n"
				+ "9 2 21 22 23 10 2 8 \r\n"
				+ "10 2 9 23 24 11 3 2 \r\n"
				+ "11 2 10 24 25 26 12 3 \r\n"
				+ "12 2 3 11 26 27 13 4 \r\n"
				+ "13 2 4 12 27 28 29 14 \r\n"
				+ "14 2 5 4 13 29 30 15 \r\n"
				+ "15 2 16 5 14 30 31 32 \r\n"
				+ "16 2 17 6 5 15 32 33 \r\n"
				+ "17 2 35 18 6 16 33 34 \r\n"
				+ "18 2 36 7 1 6 17 35 \r\n"
				+ "19 1 -1 -1 20 7 36 -1 \r\n"
				+ "20 1 -1 -1 21 8 7 19 \r\n"
				+ "21 1 -1 -1 22 9 8 20 \r\n"
				+ "22 1 -1 -1 -1 23 9 21 \r\n"
				+ "23 1 22 -1 -1 24 10 9 \r\n"
				+ "24 1 23 -1 -1 25 11 10 \r\n"
				+ "25 0 24 -1 -1 -1 26 11 \r\n"
				+ "26 1 11 25 -1 -1 27 12 \r\n"
				+ "27 1 12 26 -1 -1 28 13 \r\n"
				+ "28 1 13 27 -1 -1 -1 29 \r\n"
				+ "29 1 14 13 28 -1 -1 30 \r\n"
				+ "30 1 15 14 29 -1 -1 31 \r\n"
				+ "31 1 32 15 30 -1 -1 -1 \r\n"
				+ "32 1 33 16 15 31 -1 -1 \r\n"
				+ "33 1 34 17 16 32 -1 -1 \r\n"
				+ "34 0 -1 35 17 33 -1 -1 \r\n"
				+ "35 1 -1 36 18 17 34 -1 \r\n"
				+ "36 1 -1 19 7 18 35 -1 "
				;
		player.readGlobal(new Scanner(global));
	}

}
