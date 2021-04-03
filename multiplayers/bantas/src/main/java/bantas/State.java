package bantas;

import java.util.Scanner;

public class State {
	public int myId;
	int minWidth = 0, maxWidth = 8;		
	int minHeight = 0, maxHeight = 8;
	
	int cells[][]=  new int[8][8];
	int totalMines;
	int totalOpp;

	
	void copy(State from) {
		this.myId = from.myId;
		
		for (int y=0;y<8;y++) {
			for (int x=0;x<8;x++) {
				this.cells[x][y] = from.cells[x][y];
			}
		}
		this.minWidth = from.minWidth;
		this.maxWidth = from.maxWidth;
		this.minHeight = from.minHeight;
		this.maxHeight = from.maxHeight;
		this.totalMines = from.totalMines;
		this.totalOpp = from.totalOpp;
	}
	
	public void reset() {
		minWidth = 0; 
		maxWidth = 8;		
		minHeight = 0; 
		maxHeight = 8;
		
		totalMines = 0;
		totalOpp = 0;

	}

	public void read(Scanner in) {
		reset();
		
		for (int y = 0; y < 8; y++) {
			String line = in.nextLine(); // string describing tiles of a line of the grid containing values: 0 or 1: player with this id, '-': empty, 'x': hole
		
			if (line.equals("x x x x x x x x")) {
				if (minHeight == y) {
					minHeight =y+1;
				} else if (maxHeight == 8) {
					maxHeight = y-1;
				}
			}
			
			for (int x=0;x<8;x++) {
				char c = line.charAt(2*x);
				if (c == '0' || c == '1') {
					if (c == '0') totalMines++;
					if (c == '1') totalOpp++;
					cells[x][y] = (c-'0')+1; // 1 ou 2
				} else if (c == 'x'){
					cells[x][y] = -1; // void
				} else {
					cells[x][y] = 0; // empty;
				}
			}
		}
		
		for (int x=0;x<8;x++) {
			boolean wall = true;
			for (int y=0;y<8;y++) {
				if (cells[x][y] != -1) {
					wall = false;
					break;
				}
			}
			if (wall) {
				if (minWidth == x) {
					minWidth = x+1;
				} else if (maxWidth == 8){
					maxWidth = x-1;
				}
			}
		}
		
		if (minWidth == -1) minWidth = 0;
		if (minHeight == -1) minHeight = 0;
		if (maxWidth == 8) maxWidth = 7;
		if (maxHeight == 8) maxHeight = 7;
		System.err.println("Mines : "+minWidth+" / "+minHeight);
		System.err.println("Maxes : "+maxWidth+" / "+maxHeight);

		this.print();
		
	}
	
	public int countMines() {
		return count(1);
	}
	
	public int countOpp() {
		return count(2);
	}

	private int count(int playerId) {
		int count = 0;
		for (int y = 0; y < 8; y++) {
			for (int x=0;x<8;x++) {
				if (cells[x][y] == playerId) count++;
			}
		}
		return count;
	}
	
	public void print() {
		for (int y = 0; y < 8; y++) {
			for (int x=0;x<8;x++) {
				switch(cells[x][y]) {
				case -1 : System.err.print("x "); break;
				case 0 : System.err.print("  "); break;
				case 1 : System.err.print("1 "); break;
				case 2 : System.err.print("2 "); break;
				}
			}
			System.err.println();
		}
	}
	
	
	public void pushUp() {
		for (int x=minWidth;x<=maxWidth;x++) {
			int next = 0;
			for (int y=maxHeight;y>=minHeight;y--) {
				int cell = cells[x][y];
				if (next > 0) {
					cells[x][y] = next;
					next = cell;
				} else {
					if (cell == myId) {
						cells[x][y] = 0;
						next = cell;
					} else {
						next = 0;
					}
				}
			}
		}
	}
	
	public void pushDown() {
		for (int x=minWidth;x<=maxWidth;x++) {
			int next = 0;
			for (int y=minHeight;y<=maxHeight;y++) {
				int cell = cells[x][y];
				if (next > 0) {
					cells[x][y] = next;
					next = cell;
				} else {
					if (cell == myId) {
						cells[x][y] = 0;
						next = cell;
					} else {
						next = 0;
					}
				}
			}
		}
	}
	
	public void pushRight() {
		for (int y=minHeight;y<=maxHeight;y++) {
			int next = 0;
			for (int x=minWidth;x<=maxWidth;x++) {
				int cell = cells[x][y];
				if (next > 0) {
					cells[x][y] = next;
					next = cell;
				} else {
					if (cell == myId) {
						cells[x][y] = 0;
						next = cell;
					} else {
						next = 0;
					}
				}
			}
		}
	}
	
	public void pushLeft() {
		for (int y=minHeight;y<=maxHeight;y++) {
			int next = 0;
			for (int x=maxWidth;x>=minWidth;x--) {
				int cell = cells[x][y];
				if (next > 0) {
					cells[x][y] = next;
					next = cell;
				} else {
					if (cell == myId) {
						cells[x][y] = 0;
						next = cell;
					} else {
						next = 0;
					}
				}
			}
		}
	}
}
