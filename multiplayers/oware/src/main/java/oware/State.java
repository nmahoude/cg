package oware;

import fast.read.FastReader;

public class State {
	int[] seeds = new int[12];
	int[] score = new int[2];
	
	public void read(FastReader in) {
		for (int i = 0; i < 12; i++) {
			seeds[i] = in.nextInt();
		}

		System.err.println("After reading state : ");
		debug();
		System.err.println("**********************");
	}

	public void copy(State model) {
		System.arraycopy(model.seeds, 0, seeds, 0, 12);
		score[0] = model.score[0];
		score[1] = model.score[1];
	}
	
	public void playHouse(int player, int index) {
		int currentSeeds = seeds[index];
		if (currentSeeds == 0) {
			score[player] = -999;
		}
		seeds[index] = 0;
		
		while (currentSeeds > 0) {
			index++;
			if (index == 12) index = 0;
			if (seeds[index] == 12) {
				continue;
			} else {
				seeds[index]++;
				currentSeeds--;
			}
		}
		
		int captured = 0;
		if (player == 0) {
			while (index > 5 && index < 12 && (seeds[index] == 2 || seeds[index] == 3)) {
				captured += seeds[index];
				seeds[index] = 0;
				index--;
			}
			
			if (seeds[6]==0 && seeds[7] == 0 && seeds[8]==0 && seeds[9] == 0 && seeds[10] == 0 && seeds[11] == 0) {
				score[0] = -999;
				score[1] = 999;
			} else {
				score[0] += captured;
			}
			
		} else {
			while (index > 0 && index < 6 && (seeds[index] == 2 || seeds[index] == 3)) {
				captured += seeds[index];
				seeds[index] = 0;
				index--;
			}

			if (seeds[0]==0 && seeds[1] == 0 && seeds[2]==0 && seeds[3] == 0 && seeds[4] == 0 && seeds[5] == 0) {
				score[0] = 999;
				score[1] = -999;
			} else {
				score[1] += captured;
			}
		}
		
	}
	
	public int win() {
		if (score[0] >= 25) return 0;
		else if (score[1] >= 25) return 1;
		else return -1;
	}

	public void debug() {
		for (int i=6;i<12;i++) {
			System.err.print(String.format("%3d", seeds[i]));
		}
		System.err.println();

		for (int i=0;i<6;i++) {
			System.err.print(String.format("%3d", seeds[i]));
		}
		System.err.println();
		System.err.println("Scores : "+score[0]+" "+score[1]);
	}
	
}
