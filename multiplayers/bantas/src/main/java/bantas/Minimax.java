package bantas;


public class Minimax {
	private static final int MAX_DEPTH = 9;
	private static State tmp = new State();
	
    public Dir think(State root) {
        long start = System.currentTimeMillis();
        collisions = 0;
        StateCache.reset();
        

        Dir bestDir = null;
        double bestScore = Double.NEGATIVE_INFINITY;
        for (Dir dir : Dir.values()) {
        	
        	State tmp = StateCache.get();
        	tmp.copyFrom(root);
        	tmp.push(dir);
        	System.err.println("Debug state after 1st push : ");
        	tmp.print();
        	System.err.println("pCounts : "+tmp.pCount[1]+" / "+tmp.pCount[2]);
        	
        	
            double score = alphaBeta(tmp, Integer.MIN_VALUE, Integer.MAX_VALUE, false, MAX_DEPTH-1);
            System.err.println("New way "+dir+" => "+score);
            if (score > bestScore) {
            	bestScore = score;
            	bestDir = dir;
            }
            
        }
        
        long end = System.currentTimeMillis();
        System.err.println("Minimax time : " + (end - start));
        System.err.println("Detected collision : " + collisions);
        return bestDir;
    }

    int columnsOrder[] = new int[] { 4, 5, 3, 6, 2, 7, 1, 8, 0 };

    private int collisions;

    public double alphaBeta(State parent, double alpha, double beta, boolean maximizingScore, int depth) {
        if (parent.gameOver() || depth == 0 || (Player.turn + (MAX_DEPTH-depth) == 200)) {
            return evaluate(parent, depth);
        }
        
        double bestScore = maximizingScore ? Integer.MIN_VALUE : Integer.MAX_VALUE;
        for (Dir dir : Dir.values()) {
        	State node = StateCache.get();
        	node.copyFrom(parent);
        	node.push(dir);
        	
            double score;
            score = alphaBeta(node, alpha, beta, !maximizingScore, depth - 1);

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

	private double evaluate(State parent, int depth) {
		
		if (parent.pCount[State.ME] == 0) {
			return Double.NEGATIVE_INFINITY;
		}
		if (parent.pCount[State.OPP] == 0) {
			return Double.POSITIVE_INFINITY;
		}
		return 1.0 * ( parent.pCount[State.ME] - parent.pCount[State.OPP]);
	}
}
