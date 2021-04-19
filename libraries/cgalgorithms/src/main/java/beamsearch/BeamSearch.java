package beamsearch;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import beamsearch.support.Action;
import beamsearch.support.Agent;
import beamsearch.support.State;
import support.Player;

public class BeamSearch {
  
	private static final int SEARCH_MAX_DURATION = 40;
	private static final int TIMEOUT_CHECK_MASK = (1 << 9) - 1 ; // change this valuedepending on node calculation cost

	private static final int MAX_DEPTH = 101; // maximum depth 
	private static final int MAX_NODES = 350_000; // maximum nodes, total
	public static final int K = 500; // maximum nodes in a layer (can temporarly go higher with expand)


	
	public static BSNode[] nodes = new BSNode[MAX_NODES];
  public static int[] layerNodesFE = new int[MAX_DEPTH];
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
  public static int currentDepth;
  
  public String think(State state, Agent agent, int maxDepth) {
    BeamSearch.state = state;
    BeamSearch.agent = agent;
    
    // TODO Updated dirtily on the BSNode for better performance
    bestFitnesse = Double.NEGATIVE_INFINITY;
    bestNode = null;

    makeRootNode(state);
    nodes[0] = root;
    
    int head = 0;
    int tail = 1;
    layerNodesFE[0] = 0;
    
    currentDepth = 0;
    int nodesCount = 0;
    while (true) {
      layerNodesFE[currentDepth+1] = tail;

      currentDepth++;
      if (currentDepth > maxDepth) break; // don't go to far if he finishes
      
      int newHead = tail;
      int newTail = tail;

      while (head < tail) {
        newTail = nodes[head].expand(state, nodes, newTail);
        head++;
        
        nodesCount++;
        if ((nodesCount & TIMEOUT_CHECK_MASK) == 0) {
          if (System.currentTimeMillis() - Player.start > 40 ) {
            break;
          }
        }
      } // full layer is computed
      
      if (newTail - newHead > K) {
        // sort & take best
        Arrays.parallelSort(nodes, newHead, newTail); // uses TimSort
        newTail = newHead+K;
      } else {
      	// no need to sort if we did'nt reach the maximum per layer
      }

      if (System.currentTimeMillis() - Player.start > SEARCH_MAX_DURATION || currentDepth >= maxDepth) {
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
        System.err.println(action.debug(state, agent));
      }
    }

    return firstNode.fromAction.output(state);
  }
  
	private void makeRootNode(State state) {
    root.parent = root;

    throw new RuntimeException("   // make root node from state & agent");
  }
}

