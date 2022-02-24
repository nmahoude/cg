package dab2;

import dab.Edge;

public class SimpleAI {

	
	private int score(State parent) {
		State state = new State();
		

		int bestScore = Integer.MIN_VALUE;
		
        for (int x=0;x<8;x++) {
			for (int y=0;y<7;y++) {
				if (!parent.canSetVerticalEdge(x, y)) continue;
				
				state.copyFrom(parent);
				int base = state.setVerticalEdge(x, y);
				if (base > 0) {
					base = base + score(state);
				}
				if (base > bestScore) {
					bestScore = base;
				}
			}
        }
        
		for (int y=0;y<8;y++) {
			for (int x=0;x<7;x++) {
				if (!parent.canSetHorizontalEdge(x, y)) continue;
				
				state.copyFrom(parent);
				int base = state.setHorizontalEdge(x, y);
				if (base > 0) {
					base = base + score(state);
				}
				if (base > bestScore) {
					bestScore = base;
				}
			}
		}

		return bestScore;
	}

	public void think(State root) {
		State state = new State();
        int bestScore = 0;
        boolean vertical = false;
        int bestX=-1, bestY=-1;
        
        for (int x=0;x<8;x++) {
			for (int y=0;y<7;y++) {
				if (!root.canSetVerticalEdge(x, y)) continue;

				state.copyFrom(root);
				int score = state.setVerticalEdge(x, y);
				if (score > 0) score+= score(state);
				
				if (score > bestScore) {
					bestScore = score;
					vertical = true;
					bestX = x;
					bestY = y;
				}
			}
        }

		for (int y=0;y<8;y++) {
			for (int x=0;x<7;x++) {
				if (!root.canSetHorizontalEdge(x, y)) continue;
				
				state.copyFrom(root);
				int score = state.setHorizontalEdge(x, y);
				if (score > 0) score+= score(state);
				
				if (score > bestScore) {
					bestScore = score;
					vertical = true;
					bestX = x;
					bestY = y;
				}
			}
		}

		if (bestScore == 0) {
			thinkOld(root);
		} else {
			output(vertical, bestX, bestY);
		}
	}
	
	public void thinkOld(State state) {

        double bestScore = Double.NEGATIVE_INFINITY;
        boolean vertical = false;
        int bestX=-1, bestY=-1;
        
        for (int x=0;x<8;x++) {
			for (int y=0;y<7;y++) {
				if (!state.canSetVerticalEdge(x, y)) continue;
				
				int edgeCount0, edgeCount1;
				if (x==0) {
					edgeCount0 = state.edgeCount(0, y);
					edgeCount1 = -1;
				} else if (x == 7) {
					edgeCount0 = state.edgeCount(6, y);
					edgeCount1 = -1;
				} else {
					edgeCount0 = state.edgeCount(x-1, y);
					edgeCount1 = state.edgeCount(x, y);
				}
				int score = evaluate(edgeCount0, edgeCount1);
				
				
				if (y == 0) {
					System.err.println(x+" "+y);
					System.err.println("Edges  :"+edgeCount0+"  / "+edgeCount1);
					System.err.println(" score = "+score);
				}
				if (score > bestScore) {
					bestScore=  score;
					vertical = true;
					bestX = x;
					bestY = y;
				}
			}
		}
		
		for (int y=0;y<8;y++) {
			for (int x=0;x<7;x++) {
				if (!state.canSetHorizontalEdge(x, y)) continue;

				int edgeCount0, edgeCount1;
				if (y==0) {
					edgeCount0 = state.edgeCount(x, 0);
					edgeCount1 = -1;
				} else if (y == 7) {
					edgeCount0 = state.edgeCount(x, 6);
					edgeCount1 = -1;
				} else {
					edgeCount0 = state.edgeCount(x, y-1);
					edgeCount1 = state.edgeCount(x, y);
				}
				int score = evaluate(edgeCount0, edgeCount1);
				if (score > bestScore) {
					bestScore=  score;
					vertical = false;
					bestX = x;
					bestY = y;
				}
			}
		}
     
		output(vertical, bestX, bestY);
	}

	private void output(boolean vertical, int bestX, int bestY) {
		System.err.println("Best is "+bestX+" "+bestY+" "+vertical);
		
		if (vertical) {
			char letter;
			char number = (char)('1'+bestY);
			char dir;
			if (bestX == 0) {
				letter = 'A';
				dir = 'L';
			} else {
				letter = (char)('A'+(bestX-1));
				dir = 'R';
			}
			System.out.println(""+letter+number+" " +dir);
		} else {
			char letter = (char)('A'+bestX);
			char number;
			char dir;
			if (bestY == 0) {
				number = '1';
				dir = 'B';
			} else {
				number = (char)('1'+(bestY-1));
				dir = 'T';
			}
			
			System.out.println(""+letter+number+" " +dir);
		}
	}
	
	private int evaluate(int edgeCount0, int edgeCount1) {
		if (edgeCount1 == -1) {
			if (edgeCount0 == 3)
				return 100;
			else if (edgeCount0 == 2)
				return -100;
			else
				return 0;
		}

		int score = 0;

		if ((edgeCount0 == 3 && edgeCount1 == 2) || (edgeCount0 == 2 && edgeCount1 == 3)) {
			score = 10_000;
		} else if ((edgeCount0 == 3 && edgeCount1 == 3) || (edgeCount0 == 3 & edgeCount1 == 3)) {
			score = 5_000;
		} else if (edgeCount0 == 3 || edgeCount1 == 3) {
			score = 100;
		} else if (edgeCount0 == 2 || edgeCount1 == 2) {
			score = -100;
		}

		return score;
	}
}
