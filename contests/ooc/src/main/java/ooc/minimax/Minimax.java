package ooc.minimax;

import ooc.P;
import ooc.Player;
import ooc.State;
import ooc.sim.Simulator;

public class Minimax {
	Simulator SIM = new Simulator(false);
	
	public Minimax() {
	  MMNode.resetCache();
	}
	
	public double searchMinimizing(State state, int oppInitLife) {
	  
		MMNode.resetCache();
		MMNode root = MMNode.pop();
		
		root.initFrom(state);
		// TODO mieux prendre en compte l'etat actuel
    root.life[0] = 6;
		root.life[1] = 6;
		root.torpedoDelai[0] = 0;
		root.torpedoDelai[1] = 6;
		
		MAX_DEPTH = 2;
		double result = minimax(root, 0, 1);
    return result;
	}
	
	
	
	int MAX_DEPTH = 6;
	MMNode best = null;
	double bestScore = Double.NEGATIVE_INFINITY;
	double bestScorePerDirection[] = new double[4];
	public double minimax(MMNode node, int depth, int maximizingPlayer) {
		
		if (depth == MAX_DEPTH || node.life[0] == 0 || node.life[1] == 0) {
/*
      if (node.life[0] == 0) {
		    System.err.println(String.format("%"+(depth+1)+"s", "")+" I'll die");
		  } else if (node.life[1] == 0) {
		    System.err.println(String.format("%"+(depth+1)+"s", "")+" Opp die");
		  }
*/
		  return node.life[0] <= 0 ? -10_000-depth : node.life[1] <= 0 ? +10_000-depth : 1000 * (node.life[0] - node.life[1]) - depth; // the sooner the better
		}
		
		if (maximizingPlayer == 0) {
			double value = Double.NEGATIVE_INFINITY;
			for (int d=0;d<4;d++) {
				P pos = node.pos[0].neighbors[d];
				if (Player.map.isIsland(pos) || node.trails[0].get(pos.o)) continue;
        /*if (Player.D_SEARCH && Player.turn >= Player.D_SEARCH_TURN) {
          System.err.println(String.format("%"+(depth+1)+"s", "")+"Me  @"+pos+" with dir "+d);
        }*/
				MMNode child = node.move(0, d, depth);
				
				value = Math.max(value, minimax(child, depth+1, 1));
				if (depth == 0) {
					bestScorePerDirection[d] = value;
					if (value > bestScore) {
					  bestScore = value;
					  best = child;
					}
				}
			}
			return value;
		} else {
			double value = Double.POSITIVE_INFINITY;
			for (int d=0;d<4;d++) {
				P pos = node.pos[1].neighbors[d];
				if (Player.map.isIsland(pos) || node.trails[1].get(pos.o)) continue;

				/*if (Player.D_SEARCH && Player.turn >= Player.D_SEARCH_TURN) {
				  System.err.println(String.format("%"+(depth+1)+"s", "")+"Opp @"+pos);
				}*/
				MMNode child = node.move(1, d, depth);

        value = Math.min(value, minimax(child, depth+1, 0));
			}
			return value;
		}
	}
	
}
