package dab2;

import dab.fast.FastReader;

public class State {
	private static final int ALL_MASKS = 0b1111;
	static final int TOP_MASK = 0b0001;
	static final int RIGHT_MASK = 0b0010;
	static final int BOTTOM_MASK = 0b0100;
	static final int LEFT_MASK = 0b1000;
	
	int cells[][] = new int[8][8];
	int scores[] = new int[2];
	
	public void read(FastReader in) {
		fillAll();
		
		scores[0] =  in.nextInt(); // The player's score.
	    scores[1] = in.nextInt(); // The opponent's score.
	    System.err.println(""+scores[0]+" "+scores[1]);
	    int numBoxes = in.nextInt(); // The number of playable boxes.
	    for (int i = 0; i < numBoxes; i++) {
	        String box = in.nextString(); // The ID of the playable box.
	        String sides = in.nextString(); // Playable sides of the box.

	        int x = box.charAt(0) - 'A';
	        int y = box.charAt(1) - '1';
	        
	        int mask = flagsToMask(sides);
			cells[x][y] = mask;
//			System.err.println(box+" "+sides+" => mask is "+mask);
	        
	    }
	}

	private void fillAll() {
		for (int y=7;y>=0;y--) {
			for (int x=0;x<8;x++) {
				cells[x][y] = ALL_MASKS;
			}
		}
	}

	private int flagsToMask(String sides) {
		int mask = ALL_MASKS;
		for (char c : sides.toCharArray()) {
			switch(c) {
			case 'T' : mask&=~TOP_MASK; break; 
			case 'R' : mask&=~RIGHT_MASK; break; 
			case 'B' : mask&=~BOTTOM_MASK; break; 
			case 'L' : mask&=~LEFT_MASK; break; 
			}
		}
		return mask;
	}

	private String maskToFlag(int mask) {
		String flag = "";
		if ((mask & LEFT_MASK) == LEFT_MASK) flag+="L";
		if ((mask & TOP_MASK) == TOP_MASK) flag+="T";
		if ((mask & BOTTOM_MASK) == BOTTOM_MASK) flag+="B";
		if ((mask & RIGHT_MASK) == RIGHT_MASK) flag+="R";
		
		return flag;
	}

	
	public void print() {
		for (int y=7;y>=0;y--) {
			for (int x=0;x<8;x++) {
				System.err.print(String.format("-%4s", maskToFlag(cells[x][y])));
			}
			System.err.println();
		}
	}

	
	public boolean canSetVerticalEdge(int x, int y) {
		if (x == 0) {
			return (cells[0][y] & LEFT_MASK ) == 0;
		} else if (x == 7) {
			return (cells[6][y] & RIGHT_MASK) == 0;
		} else {
			return (cells[x-1][y] & RIGHT_MASK) == 0;
		}
	}

	public boolean canSetHorizontalEdge(int x, int y) {
		if (y == 0) {
			return (cells[x][0] & BOTTOM_MASK) == 0;
		} else if (y == 7) {
			return (cells[x][6] & TOP_MASK) == 0;
		} else {
			return (cells[x][y-1] & TOP_MASK) == 0;
		}
	}

	
	/**
	 * return the points won by this edge
	 * 
	 * x=[0..8]
	 * y=[0..7]
	 * 
	 * warning, doesn't check if play is valid !
	 */
	public int setVerticalEdge(int x, int y) {
		if (x == 0) {
			cells[0][y] |=LEFT_MASK;
			if (cells[0][y] == ALL_MASKS) return 1; else return 0;
		} else if (x == 7) {
			cells[6][y] |=RIGHT_MASK;
			if (cells[6][y] == ALL_MASKS) return 1; else return 0;
		} else {
			int score = 0;
			cells[x-1][y] |=RIGHT_MASK;
			cells[x][y] |= LEFT_MASK;

			if (cells[x-1][y] == ALL_MASKS) score++;
			if (cells[x][y] == ALL_MASKS) score++;
			return score;
		}
	}
	
	/**
	 * return the points won by this edge
	 * 
	 * x=[0..7]
	 * y=[0..8]
	 * 
	 * warning, doesn't check if play is valid !
	 */
	public int setHorizontalEdge(int x, int y) {
		if (y == 0) {
			cells[x][0] |=BOTTOM_MASK;
			if (cells[x][0] == ALL_MASKS) return 1; else return 0;
		} else if (y == 7) {
			cells[x][6] |=TOP_MASK;
			if (cells[x][6] == ALL_MASKS) return 1; else return 0;
		} else {
			int score = 0;
			cells[x][y-1] |=TOP_MASK;
			cells[x][y] |= BOTTOM_MASK;

			if (cells[x][y-1] == ALL_MASKS) score++;
			if (cells[x][y] == ALL_MASKS) score++;
			return score;
		}
	}
	
	public int edgeCount(int x, int y) {
		return Integer.bitCount(cells[x][y]);
	}

	public void copyFrom(State model) {
		for (int y=0;y<7;y++) {
			for (int x=0;x<7;x++) {
				cells[x][y] = model.cells[x][y];
			}
		}
		scores[0] = model.scores[0];
		scores[1] = model.scores[1];
	}
}
