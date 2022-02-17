package connect4;

public class Minimax {

	private static final int MAX_DEPTH = 6;
	private int bestCol;
	public double[] forbidenScore = new double[10];
	public int[] forbidenCols = new int[10];
	public int forbidenColsFE;
	private double currentForbidenScore;

	
	public int think(State root) {
		StateCache.reset();
		bestCol = -1;
		root.childsFE = 0;
		forbidenColsFE = 0;
		
		
		
		long start = System.currentTimeMillis();
		ZobristHash.clear();
		collisions = 0;
		
		int value = alphaBeta(root, Integer.MIN_VALUE, Integer.MAX_VALUE, true, MAX_DEPTH);
		long end = System.currentTimeMillis();
		System.err.println("Minimax time : " + (end - start));
		System.err.println("Detected collision : "+collisions);
		return bestCol;
	}

	int columnsOrder[] = new int[] { 4, 5, 3, 6, 2, 7, 1, 8, 0 };
	private int collisions;

	public int alphaBeta(State node, int alpha, int beta, boolean maximizingScore, int depth) {
		if (node.end() || depth == 0) {
			return evaluate(node, depth);
		}

		int bestScore = maximizingScore ? Integer.MIN_VALUE : Integer.MAX_VALUE;
		for (int cc = 0; cc < 9; cc++) {
			int col = columnsOrder[cc];
			
			if (!node.canPutOn(col)) {
				continue;
			}

			if (depth == MAX_DEPTH) {
				currentForbidenScore = Integer.MAX_VALUE;
			}
			node.put(col, maximizingScore);
			
//			if (ZobristHash.contains(node.zobrist)) {
//				collisions++;
//				
//				node.remove(col, maximizingScore);
//				continue;
//			} else {
//				ZobristHash.add(node.zobrist);
//			}
			
			int score = alphaBeta(node, alpha, beta, !maximizingScore, depth - 1);
			node.remove(col, maximizingScore);
			

			if (maximizingScore) {
				if (depth == MAX_DEPTH) {
					System.err.println("Score for col "+col+" is "+score);
					if (score > 2000 && score > bestScore) {
						bestCol = col;
					} else if (score < -2000) {
						forbidenScore[forbidenColsFE] = score;
						forbidenCols[forbidenColsFE++] = col;
					}
				}
				
				bestScore = Math.max(bestScore, score);
				alpha = Math.max(alpha, bestScore);

				if (bestScore > beta)
					break;
			} else {
				if (score  < -5000) {
					currentForbidenScore = Math.min(currentForbidenScore, MAX_DEPTH - depth);
				}
				bestScore = Math.min(bestScore, score);
				beta = Math.min(beta, bestScore);

				if (bestScore < alpha)
					break;
			}
		}
		
		return bestScore;
	}

	private int evaluate(State node, int depth) {
		if (depth == 0) {
			return 0; // abandon
		}

		if (node.winner == 0) {
			return 10_000 + depth;
		} else if (node.winner == 1) {
			return -10_000 - depth;
		} else {
			return 0; // draw or not finished
		}
	}

}
