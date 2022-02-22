package bantas;


public class Minimax {
	private static final int MAX_DEPTH = 10;
	
    public Dir think(State root) {
        long start = System.currentTimeMillis();
        Dir bestDir = think2(root);

        long end = System.currentTimeMillis();
        System.err.println("Minimax time : " + (end - start));
        
        if (Player.turn == 11) {
//        	debug(root);
        }
        
        
        return bestDir;
    }

	private void debug(State root) {
		System.err.println("********************");
        System.err.println("Debugging Minimax ! ");
        System.err.println("********************");
        State tmp = StateCache.get();
        tmp.copyFrom(root);
        System.err.println("Initial state : ");
        tmp.print();
        tmp.push(Dir.UP);
        System.err.println("State after push");
        tmp.print();
        Dir best = think2(tmp);
        System.err.println("Next best dir is " + best);
	}

	Dir[] dirs = new Dir[] { Dir.RIGHT, Dir.UP, Dir.LEFT, Dir.DOWN };
	private Dir think2(State root) {
		StateCache.reset();
        

        Dir bestDir = null;
        double bestScore = Double.NEGATIVE_INFINITY;
        for (Dir dir : dirs) {
        	
        	State tmp = StateCache.get();
        	tmp.copyFrom(root);
        	tmp.push(dir);
        	//System.err.println("Debug state after 1st push : ");
        	//tmp.print();
        	//System.err.println("pCounts : "+tmp.pCount[1]+" / "+tmp.pCount[2]);
        	
    		double score = alphaBeta(tmp, Integer.MIN_VALUE, Integer.MAX_VALUE, tmp.turn == State.ME, MAX_DEPTH-1);
            System.err.println(""+dir+" => "+score);
            if (score > bestScore) {
            	bestScore = score;
            	bestDir = dir;
            }
            
        }
		return bestDir;
	}

    public double alphaBeta(State parent, double alpha, double beta, boolean maximizingScore, int depth) {
        if (parent.gameOver() || depth == 0 || (Player.turn + (MAX_DEPTH-depth) == 100)) {
            return evaluate(parent, depth);
        }
        
        double bestScore = maximizingScore ? Integer.MIN_VALUE : Integer.MAX_VALUE;
        for (Dir dir : dirs) {
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

	private double evaluate(State state, int depth) {
		
		if (state.pCount[State.OPP] == 0) {
            return 1_000_000.0+depth;
        }
        if (state.pCount[State.ME] == 0) {
            return -1_000_000.0-depth -state.pCount[State.OPP];
        }
		
		double[] positionalScore = new double[3];
		double[] clusterScore = new double[3];
		int centerX = Math.abs(state.maxWidth-state.minWidth) / 2;
		int centerY = Math.abs(state.maxHeight-state.minHeight) / 2;
		
		for (int y=state.minHeight;y<=state.maxHeight;y++) {
			for (int x=state.minWidth;x<=state.maxWidth;x++) {
				int distToCenter = Math.abs(x-centerX)+Math.abs(y-centerY);
				int cell = state.cells[x+8*y];
				positionalScore[cell] += 8-distToCenter;
				
				if (x>0 && state.cells[x-1 + 8 * y] == cell) clusterScore[cell]++;
				if (x<7 && state.cells[x+1 + 8 * y] == cell) clusterScore[cell]++;
				if (y>0 && state.cells[x + 8 * (y-1)] == cell) clusterScore[cell]++;
				if (y<7 && state.cells[x + 8 * (y+1)] == cell) clusterScore[cell]++;
				
			}
		}
		
		
		


		double score = 1.0 * ( state.pCount[State.ME] - state.pCount[State.OPP]);
		score += 3.0 * (positionalScore[State.ME] / state.pCount[State.ME] - positionalScore[State.OPP] / state.pCount[State.OPP]);
		
		score += 0.1 * (clusterScore[State.ME] / state.pCount[State.ME] - clusterScore[State.OPP] / state.pCount[State.OPP]);
		return score;
	}
}
