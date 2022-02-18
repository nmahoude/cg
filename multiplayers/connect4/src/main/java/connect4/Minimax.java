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
		
		double value = alphaBeta(root, Integer.MIN_VALUE, Integer.MAX_VALUE, true, MAX_DEPTH);
		long end = System.currentTimeMillis();
		System.err.println("Minimax time : " + (end - start));
		System.err.println("Detected collision : "+collisions);
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

			if (depth == MAX_DEPTH) {
				currentForbidenScore = Integer.MAX_VALUE;
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
				if (depth == MAX_DEPTH) {
					System.err.println("Score for col "+col+" is "+score);
					if (score > bestScore) {
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

        double hisCoeff = 1.5;
        score += 5.0 * (threatAnalyser.myThreats[3] - hisCoeff*threatAnalyser.oppThreats[3]); 
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
