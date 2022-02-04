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
		int value = alphaBeta(root, Integer.MIN_VALUE, Integer.MAX_VALUE, true, MAX_DEPTH);
		long end = System.currentTimeMillis();
		System.err.println("Minimax time : " + (end - start));

		return bestCol;
	}

	int columnsOrder[] = new int[] { 4, 5, 3, 6, 2, 7, 1, 8, 0 };

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

			node.put(col, maximizingScore);
			int score = alphaBeta(node, alpha, beta, !maximizingScore, depth - 1);
			node.remove(col);

			if (maximizingScore) {
				if (depth == MAX_DEPTH) {
					if (score > 2000 && score > bestScore) {
						bestCol = col;
					} else if (score < -2000) {
						forbidenScore[forbidenColsFE] = currentForbidenScore;
						forbidenCols[forbidenColsFE++] = col;
					}
				}
				bestScore = Math.max(bestScore, score);
				alpha = Math.max(alpha, bestScore);

				if (bestScore > beta)
					break;
			} else {
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
			currentForbidenScore = Math.min(currentForbidenScore, MAX_DEPTH - depth);
			return -10_000;
		} else {
			return 0; // draw or not finished
		}
	}

}
