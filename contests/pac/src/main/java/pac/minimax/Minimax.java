package pac.minimax;

import java.util.List;

import pac.Player;
import pac.State;
import pac.agents.Pacman;
import pac.map.Pos;
import pac.simpleai.ColliderResolver;

public class Minimax {
  public final static int OPTIMISTIC = 0;
  public final static int NORMAL = 1;
  public final static int PESSIMISTIC = 2; /* std minimax, he will now what I did */
  

  private int maxDepth;
  private int strategy = PESSIMISTIC;
  
  public Minimax(int maxDepth) {
	  this.maxDepth = maxDepth;
    MMNode.resetCache();
	}
	
	public MMNode searchMinimizing(State state, int strategy, Pacman me, Pacman opp) {
		this.strategy = strategy;
		MMNode.resetCache();
		MMNode root = MMNode.pop();
		
		root.initFrom(state, me, opp);
		
		
		if (Player.debugMinimax()) System.err.println("Strategy is "+strategy);
		double result = minimax(root, 0, true);
		
		if (Player.debugMinimax()) {
  		System.err.println("*** Best move is ****");
  		debugBest(root);
		}
		
    return root;
	}
	
	private void debugBest(MMNode root) {
	  root.debug(0, true);
	  
	  System.err.println(root.getBestChild(0, true));
  }

	double minimax(MMNode node, int depth, boolean maximizingPlayer) {
		
		if (depth == maxDepth || node.pos[0][1] == Pos.INVALID || node.pos[1][1] == Pos.INVALID) {
		  
		  int score = (node.pos[0][1] == Pos.INVALID ? -10_000+depth : node.pos[1][1] == Pos.INVALID ? +10_000-depth : 0);
		  node.score = score;
      return score;
		}
		
		if (maximizingPlayer) {
			double value = Double.NEGATIVE_INFINITY;
			List<MMNode> childs = node.getChild(maximizingPlayer, depth);
	    for (MMNode child : childs) {
	      //System.err.println(child.toString());
	    	child.score = 0;
	    	double minimax = minimax(child, depth+1, false);
	    	
	    	if (minimax == Double.POSITIVE_INFINITY) {
	    		child.score= Double.NEGATIVE_INFINITY;
	    	} else {
    			value = Math.max(value, minimax);
	    	}
	    }
	    // node of minimizer
	    node.score = value;
			return value;
		} else {
			double value = Double.POSITIVE_INFINITY;
			double maxValue = Double.NEGATIVE_INFINITY;
			List<MMNode> childs = node.getChild(maximizingPlayer, depth);
			int meDead = 0;
			int allAlive = 0;
			int oppDead = 0;
			
	    for (MMNode child : childs) {
//	      System.err.println("  "+child.toString());
	    	// simulate
	    	boolean possible = simulate(node, child);
		    if (possible) {
		    	double minimax = minimax(child, depth+1, true);
		    	if (minimax < -1000) meDead++;
		    	else if (minimax > 1000) {
		    	  oppDead++;
		    	}
		    	else allAlive++;
		    	
		    	child.score = minimax;
	    		value = Math.min(value, minimax);
	    		maxValue = Math.max(maxValue,  minimax);
		    } else {
		    	child.score = 0;
		    }
			}
	    // node of maximizer
	    if (strategy == PESSIMISTIC) {
	    	// std minimax
	    } else if (strategy == NORMAL) {
	    	if (oppDead > 0 && meDead == 0) {
	    		if (allAlive == 0) {
	    			//value = bestMinimax;
	    		  value = maxValue;
	    		} else {
	    			value = maxValue - 4900;
	    		}
	    	}
	    } else /* OPTIMISTICK */ {
	    	if (oppDead == 0 && allAlive == 0 && meDead > 0) {
	    		value = -10000-depth;
	    	} else if (oppDead > 0 && allAlive == 0) {
    			value = 10000 - depth;
	    	} else if (oppDead > 0){
	    		value = 5000-depth;
	    	} else {
	    		// normal
	    	}
	    }
	    node.score = value;
	    
			return value;
		}
	}

  private boolean simulate(MMNode parent, MMNode child) {
    
    if (hasCollided(parent, child)) {
      if (child.type[0].canEat(child.type[1])) {
        child.pos[1][0] = Pos.INVALID; // kill opp
        child.pos[1][1] = Pos.INVALID; // kill opp
        return true;
      } else if (child.type[1].canEat(child.type[0])) {
        child.pos[0][0] = Pos.INVALID; // kill me
        child.pos[0][1] = Pos.INVALID; // kill me
        return true;
      } else {
        return false; // gérer les collisions
      }
    } else {
      // TODO :)
      return true;
    }
  }

  private boolean hasCollided(MMNode from, MMNode to) {
    return ColliderResolver.hasCollidedFull(to.pos[0], to.pos[1]);
  }
	
}
