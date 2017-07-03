package ww.think;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ww.Agent;
import ww.GameState;
import ww.sim.Move;
import ww.sim.Simulation;

public class Think {
  boolean timeout = false;
  Map<NodePOC, NodePOC> transpositionMap = new HashMap<>();
  
  Simulation simulation;
  GameState state;
  Move bestMove = new Move(null);
  private int maxDepth;

  public Think(GameState state) {
    this.state = state;
    simulation = new Simulation(state);
  }

  public Move think(int maxDepth) {
    this.maxDepth = maxDepth;
    NodePOC.state = state;
    NodePOC node0 = new NodePOC(0);
    
    Node.testedNodes = 0;
    Node.hit = 0;
    // transpositionMap.clear();
    timeout = false;
    alphaBeta(node0, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, true, dontSee(state.agents[0]), dontSee(state.agents[1]));
    if (timeout) {
      bestMove.agent = null;
    }
    // System.err.println("A/B nodes : "+Node.testedNodes+" hit : "+Node.hit);
    return bestMove;
  }

  int dontSee(Agent agent) {
    return (!state.agents[2].inFogOfWar() && state.agents[2].position.inRange(1, agent.position))
         || (!state.agents[3].inFogOfWar() && state.agents[3].position.inRange(1, agent.position)) ? 0 : 1;
  }
  public double alphaBeta(NodePOC node, double alpha, double beta, boolean maximizingScore, int dontsee0, int dontsee1) {
    // timeout condition
    if (timeout) return 0.0;
    if (System.currentTimeMillis() - GameState.startTime > GameState.MAX_TIME) {
      timeout = true;
      return Double.NEGATIVE_INFINITY;
    }

//    NodePOC alreadyCalculated = transpositionMap.get(node);
//    if (alreadyCalculated != null) {
//      Node.hit++;
//      return alreadyCalculated.score;
//    }
    
    if (node.depth == maxDepth) {
      return node.evaluate(dontsee0, dontsee1);
    }

    double bestScore = maximizingScore ? Double.NEGATIVE_INFINITY : Double.POSITIVE_INFINITY;
    int validActions = 0;
    List<NodePOC> children = node.getChildren();
    for (NodePOC child : children) {
      if (child.move == null) {
        // no opponents known
        return alphaBeta(child, alpha, beta, !maximizingScore, dontsee0, dontsee1);
      } else {
        Node.testedNodes++;
        double score;
        
        simulation.simulate(child.move, child.transposition);
        //System.err.println(""+child.move + " => " + Arrays.toString(child.transposition));
        if (!child.move.isValid()) {
          continue;
        }
        validActions++;
        score = alphaBeta(child, alpha, beta, !maximizingScore, dontsee0+dontSee(state.agents[0]), dontsee1+dontSee(state.agents[1]));
        simulation.undo(child.move);
        
        child.score = score;
        //transpositionMap.put(child, child);
        if (maximizingScore) {
          if (score > bestScore) {
            bestScore = score;
            if (node.depth == 0) {
              child.move.copyTo(state, bestMove);
            }
          }
          alpha = Math.max(alpha, bestScore);
        } else {
          bestScore = Math.min(bestScore, score);
          beta = Math.min(beta, bestScore);
        }

        if (beta <= alpha) {
          break;
        }
      }
    }
    if (validActions == 0) {
      return node.evaluate(dontsee0, dontsee1);
    }
    return bestScore;
  }
}
