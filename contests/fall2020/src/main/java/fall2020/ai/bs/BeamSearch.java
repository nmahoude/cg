package fall2020.ai.bs;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import fall2020.Agent;
import fall2020.Player;
import fall2020.State;
import fall2020.optimizer.Action;

public class BeamSearch {
  
	public static final int K = 500;
	private static final int MAX_NODES = 350_000;
  public static BSNode[] nodes = new BSNode[MAX_NODES];
  public static int[] nodesFE = new int[101];
  static {
    for (int i=0;i<MAX_NODES;i++) {
      nodes[i] = new BSNode();
    }
  }
  static double bestFitnesse;
  static BSNode bestNode;
  
  static State state;
  static Agent agent;
  static BSNode root = new BSNode();
  public static int maxDepth;
  public static int currentDepth;
  
  public String think(State state) {
    BeamSearch.state = state;
    agent = state.agents[0];
    
    bestFitnesse = Double.NEGATIVE_INFINITY;
    bestNode = null;

    makeRootNode(state);
    nodes[0] = root;
    
    int head = 0;
    int tail = 1;
    nodesFE[0] = 0;
    
    currentDepth = 0;
    int nodesCount = 0;
    while (true) {
      nodesFE[currentDepth+1] = tail;

      currentDepth++;
      if (currentDepth > maxDepth) break; // don't go to far if he finishes
      
      int newHead = tail;
      int newTail = tail;

      while (head < tail) {
        newTail = nodes[head].expand(state, nodes, newTail);
        head++;
        
        nodesCount++;
        if ((nodesCount & 511) == 0) {
          if (System.currentTimeMillis() - Player.start > 40 ) {
            break;
          }
        }
      } // on a traité toute une layer
      if (newTail - newHead > K) {
        // sort & take best
        Arrays.parallelSort(nodes, newHead, newTail); // uses TimSort
        newTail = newHead+K;
      }

      if (System.currentTimeMillis() - Player.start > 40 || currentDepth >= maxDepth) {
        break;
      }
      head = newHead;
      tail = newTail;
    }
		long end = System.currentTimeMillis();
    
    System.err.println("depth ="+currentDepth+ " in "+(end-Player.start)+" ms");

    // best is 1st !
    return outputResult(state);
  }

  private String outputResult(State state) {
    BSNode firstNode = bestNode;
    List<Action> actions = new ArrayList<>();
    while (firstNode.parent != root) {
      actions.add(0, firstNode.fromAction);
      firstNode = firstNode.parent;
    }
    actions.add(0, firstNode.fromAction);
    if (Player.DEBUG_BESTPATH) { 
      for (Action action : actions) {
        System.err.println(action.debug(state, state.agents[0]));
      }
    }

    if (state.agents[0].spellsFE < 10 && firstNode.fromAction.type != Action.LEARN) {
      return root.inv.learnActions[0].output(state);
    } else {
      return firstNode.fromAction.output(state);
    }
  }
  
  public void select(int head, int tail) {
    for (int i=head;i<head+K;i++) {
      double maxValue = nodes[i].fitnesse;
      for (int j=i+1;j<tail;j++) {
        if (nodes[j].fitnesse > maxValue) {
          maxValue = nodes[j].fitnesse;
          BSNode temp = nodes[i];
          nodes[i] = nodes[j];
          nodes[j] = temp;
        }
      }
    }
  }
  
  
  
	private void makeRootNode(State state) {
    root.parent = root;
    root.inv = agent.inv;
    root.castables = state.agents[0].castedSpells; // TODO always do 0 = is castable, 1 = already cast
    root.score = state.agents[0].score;
    
    root.bitSet1 = state.agents[0].knownSpells;
    root.bitSet1 |= 0b111111L << BSNode.RECIPE_BASE_MASK;
    
    root.brews = state.agents[0].brewedRecipe;
    root.fitnesse = 0;
    root.fromAction = root.inv.wait;
    root.spellsCost = 0b0101_0100_0011_0010_0001_0000; // 5 4 3 2 1 0
  }
}
