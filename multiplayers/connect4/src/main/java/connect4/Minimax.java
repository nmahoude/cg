package connect4;

public class Minimax {

	private static final int MAX_DEPTH = 6;

    public int think(State root) {
        StateCache.reset();
        root.childsFE = 0;
        long start = System.currentTimeMillis();
        ZobristHash.clear();
        collisions = 0;

        int bestCol = -1;
        double bestScore = Double.NEGATIVE_INFINITY;
        for (int col : columnsOrder) {
            if (!root.canPutOn(col)) {
                continue;
            }

            root.put(col, true);
            double score = alphaBeta(root, Integer.MIN_VALUE, Integer.MAX_VALUE, false, MAX_DEPTH-1);
            System.err.println("New way "+col+" => "+score);
            if (score > bestScore) {
            	bestScore = score;
            	bestCol = col;
            }
            root.remove(col, true);
            
        }
        
        long end = System.currentTimeMillis();
        System.err.println("Minimax time : " + (end - start));
        System.err.println("Detected collision : " + collisions);
        return bestCol;
    }

    int columnsOrder[] = new int[] { 4, 5, 3, 6, 2, 7, 1, 8, 0 };

    private int collisions;

    public double alphaBeta(State node, double alpha, double beta, boolean maximizingScore, int depth) {
        if (node.end() || depth == 0) {
            return evaluate(node, depth);
        }
        double bestScore = maximizingScore ? Integer.MIN_VALUE : Integer.MAX_VALUE;
        for (int cc = 0; cc < 9; cc++) {
            int col = columnsOrder[cc];
            if (!node.canPutOn(col)) {
                continue;
            }
            
            node.put(col, maximizingScore);
            long zobrist = node.zobrist;
            Position existingPos = ZobristHash.contains(node.zobrist);
            double score;
            if (existingPos != null) {
                collisions++;
                score = existingPos.score;
            } else {
                score = alphaBeta(node, alpha, beta, !maximizingScore, depth - 1);
                ZobristHash.add(zobrist, node.mine, node.opp, score);
            }
            node.remove(col, maximizingScore);
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
		if (node.winner == 0) {
			return 100_000.0 + depth;
		} else if (node.winner == 1) {
			return -100_000.0 - depth;
		} else if (node.winner == 2) {
			return 0.0;
		} else {
			return evaluateNonFinishedBoard(node); 
		}
	}
	
	ThreatAnalyser threatAnalyser = new ThreatAnalyser();
	
	private double evaluateNonFinishedBoard(State node) {
        threatAnalyser.analyse(node.mine, node.opp);

        double score = 0.0;

        double hisCoeff;
        if (Player.attack) {
        	hisCoeff = 0.9;
        } else {
        	hisCoeff = 1.1;
        }
        score += 10.0 * (threatAnalyser.myThreats[3] - hisCoeff*threatAnalyser.oppThreats[3]); 
        score += 1.0 * (threatAnalyser.myThreats[2] - hisCoeff*threatAnalyser.oppThreats[2]);
        score += 0.1 * (threatAnalyser.myThreats[1] - hisCoeff*threatAnalyser.oppThreats[1]);

        // check double threat vertically
        for (int x=0;x<9;x++) {
        	for (int y = 0;y<6;y++) {
        		long mask = 0b11L << (7*x+y);
        		if ((threatAnalyser.myThreatMask[3] & mask) == mask) {
        			score += 100.0;
        			break;
        		}
        		if ((threatAnalyser.oppThreatMask[3] & mask) == mask) {
        			score -= 100.0;
        			break;
        		}
        	}
        }
        
        
        return score;
	}

}
