package oware;


public class Minimax {
	private static final int MAX_DEPTH = 7;

	State fakeNode = new State();

	public int think(State root) {
		StateCache.reset();

		System.err.println("Current score : "+evaluate(root, MAX_DEPTH));
		long start = System.currentTimeMillis();

		int bestCol = -1;
		double bestScore = Double.NEGATIVE_INFINITY;
		State current = StateCache.get();
		for (int col=0;col<6;col++) {
			current.copy(root);
			current.playHouse(0, col);
			if( current.score[0] < 0) continue;
			double score = alphaBeta(current, Integer.MIN_VALUE, Integer.MAX_VALUE, false, MAX_DEPTH - 1);
			System.err.println("New way " + col + " => " + score);
			if (score > bestScore) {
				bestScore = score;
				bestCol = col;
			}
		}

		long end = System.currentTimeMillis();
		System.err.println("Minimax time : " + (end - start));
		return bestCol;
	}

	public double alphaBeta(State node, double alpha, double beta, boolean maximizingScore, int depth) {
		if (node.end() || depth == 0) {
			return evaluate(node, depth);
		}
		double bestScore = maximizingScore ? Integer.MIN_VALUE : Integer.MAX_VALUE;
		for (int cc = 0; cc < 9; cc++) {
			State current = StateCache.get();
			current.copy(node);
			int player = maximizingScore? 0 : 1;
			current.playHouse(player, cc);
			if (current.score[player] < 0 ) continue;
			
			double score = alphaBeta(current, alpha, beta, !maximizingScore, depth - 1);
			
			if (maximizingScore) {
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

	private double evaluate(State node, int depth) {
		if (node.score[0] >= 25) {
			return 100_000.0 + depth;
		} else if (node.score[1] >= 25) {
			return -100_000.0 - depth;
		} else {
			return estimate(node, 0) - 1.1 * estimate(node, 1);
			
		}
	}

	private double estimate(State node, int player) {
		if (node.score[player] >= 25) return 100_000;
		if (node.score[player] < 0) return -100_000;
		
		
		int score = 2 * node.score[player];
		
		int start, end;

		// own holes
		if (player == 0) {
			start = 0; end = 6;
		} else {
			start = 6; end = 12;
		}
		int totalSeeds = 0;
		for (int i=start;i<end;i++) {
			totalSeeds += node.seeds[i];
			if (node.seeds[i] >= 12) score += 2;
		}

		// other holes
		if (player == 0) {
			start = 6; end = 12;
		} else {
			start = 0; end = 6;
		}
		
		for (int i=start;i<end;i++) {
			if (node.seeds[i] == 0) score += 4;
			else if (node.seeds[i] == 1) score += 3;
			else if (node.seeds[i] == 2) score += 3;
		}
		
		return score + 0.1 * totalSeeds;
	}

}
