package lcm.ai.beam;

import lcm.PlayerOld;
import lcm.ai.eval.IEval;
import lcm.sim.Action;
import lcm.sim.LegalActionGenerator;
import lcm.sim.Simulation;

public class BSLayer {
  private static final int MAX_LEGAL_ACTION = 50;
  private static Action legalActions[] = new Action[MAX_LEGAL_ACTION];
  private static int legalActionsFE;
  private static Simulation simulation = new Simulation();
  private static BSZobristTable zobristTable = new BSZobristTable();
  
  BSNode[] nodes = new BSNode[IBeamSearch.CHILD_SIZE];
  int nodesFE = 0;
  int nextIndexToEvict = 0;

  public static void initZobrist() {
    zobristTable.init();
  }
  public int expand(BSLayer childLayer, boolean maximizer, double limit, IEval eval) {
    boolean expandable = false;
    childLayer.nodesFE = 0;
    
    for (int n=0;n<nodesFE;n++) {
      BSNode node = nodes[n];
      if (node.isTerminal) {
        childLayer.addNodeWithEviction(node, maximizer);
        continue;
      }
      
      legalActionsFE = LegalActionGenerator.updateLegalActions(
          legalActions, node.state, Action.endTurn(), maximizer);

      for (int i = legalActionsFE - 1; i >= 0; i--) {
        Action action = legalActions[i];
        if (action == Action.endTurn()) {
          // transform the node in terminal node
          node.isTerminal = true;
          childLayer.addNodeWithEviction(node, maximizer);
          continue;
        }

        expandable = true;
        BSNode child = BSCache.pop();
        child.state.copyFrom(node.state);
        child.action = action;
        child.parent = node;
        
        simulation.simulate(child.state, action, maximizer);
        
        long hash = child.state.getHash();
        BSNode equivalent = zobristTable.get(hash);
        if (equivalent != null && equivalent.state.getHash() == hash) {
          continue;
        } else {
          if (equivalent == null) {
            zobristTable.put(hash, child);
          }
        }

        child.score = eval.eval(child.state, false);
        if (!maximizer && child.score < limit) {
          childLayer.nodesFE = 0;
          if (PlayerOld.DEBUG_BEAM) {
            System.err.println("**CUTOFF** . Limit was "+limit);
          }
          return -1;
        }
        childLayer.addNodeWithEviction(child, maximizer);

      }
    }
    
    return expandable ? childLayer.nodesFE : 0;
  }

  void addNodeWithEviction(BSNode node, boolean maximizer) {
    if (maximizer) {
      if (nodesFE < IBeamSearch.CHILD_SIZE) {
        nodes[nodesFE++] = node;
        nextIndexToEvict = node.score < nodes[nextIndexToEvict].score ? nodesFE-1 : nextIndexToEvict;
      } else if (node.score < nodes[nextIndexToEvict].score){
        return;
      } else {
        nodes[nextIndexToEvict] = node;
        double bestScore = Double.POSITIVE_INFINITY;
        int index = 0;
        for (int i=0;i<IBeamSearch.CHILD_SIZE;i++) {
          double score = nodes[i].score;
          if (score < bestScore) {
            bestScore = score;
            index = i;
          }
        }
        nextIndexToEvict = index;
      }
    } else {
      if (nodesFE < IBeamSearch.CHILD_SIZE) {
        nodes[nodesFE++] = node;
        nextIndexToEvict = node.score > nodes[nextIndexToEvict].score ? nodesFE-1 : nextIndexToEvict;
      } else if (node.score > nodes[nextIndexToEvict].score) {
        return;
      } else {
        nodes[nextIndexToEvict] = node;
        // find the biggest node which will be the next to evict
        double lowScore = Double.NEGATIVE_INFINITY;
        int index = 0;
        for (int i=0;i<IBeamSearch.CHILD_SIZE;i++) {
          double score = nodes[i].score;
          if (score > lowScore) {
            lowScore = score;
            index = i;
          }
        }
        nextIndexToEvict = index;
      }
    }
  }

  public BSNode bestNode() {
    int index = 0 ;
    double bestScore = nodes[0].score;
    for (int i=1;i<nodesFE;i++) {
      double score = nodes[i].score;
      if (score > bestScore) {
        bestScore = score;
        index = i;
      }
    }
    return nodes[index];
  }

  public BSNode worstNode() {
    int index = 0 ;
    double bestScore = nodes[0].score;
    for (int i=1;i<nodesFE;i++) {
      double score = nodes[i].score;
      if (score < bestScore) {
        bestScore = score;
        index = i;
      }
    }
    return nodes[index];
  }
}
