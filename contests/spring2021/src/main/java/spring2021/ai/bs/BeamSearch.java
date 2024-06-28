package spring2021.ai.bs;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import spring2021.Action;
import spring2021.Player;
import spring2021.State;

public class BeamSearch {

	private static final int TIMEOUT_CHECK_MASK = 512-1; // change this valuedepending on node calculation cost
	private static final Comparator<BSNode> comparator = new Comparator<BSNode>() {
		@Override
		public int compare(BSNode o1, BSNode o2) {
			if (o2.score < o1.score) return -1;
			else if (o2.score > o1.score) return 1; 
			else return 0;
		}
		
	};
	
	private static final int MAX_NODES = 500_000; // maximum nodes, total
	public static final int K = 1000; // maximum nodes in a layer (can temporarly go higher with expand)
	public static final int K_WAIT = 2000; // maximum nodes in a layer (can temporarly go higher with expand)

	public static BSNode[] cacheNodes = new BSNode[MAX_NODES];
	public static int currentCacheIndex = 0;
	
	public static BSNode[] nodes = new BSNode[MAX_NODES];
	public static BSNode[] waitingNodes = new BSNode[5*K+5*K_WAIT];
	public static int waitingNodesFE = 0;
	
	static {
		for (int i = 0; i < MAX_NODES; i++) {
			cacheNodes[i] = new BSNode();
		}
	}
	public static double bestFitnesse;
	public static BSNode bestNode;

	static State state;
	static BSNode root = new BSNode();
	public static int currentDepth;
	private int currentDay;
	
	public Action think(State state, int maxDayDepth) {
		BeamSearch.state = state;
		currentCacheIndex = 0;

		// TODO Updated dirtily on the BSNode for better performance
		bestFitnesse = Double.NEGATIVE_INFINITY;

		makeRootNode(state);
		nodes[0] = root;
		bestNode = root;

		int head = 0;
		int tail = 1;
		waitingNodesFE = 0;
		
		currentDay = state.day;
		int lastDay = Math.min(state.day + maxDayDepth, 24);
		
		currentDepth = 0;
		int nodesCount = 0;
		while (true) {
			currentDepth++;

			int newHead = tail;
			int newTail = tail;

			while (head < tail) {
				newTail = nodes[head].expand(newTail);
				head++;

				nodesCount++;
				if ((nodesCount & TIMEOUT_CHECK_MASK) == 0) {
          if (System.currentTimeMillis() - Player.start > Player.MAX_TIME) {
              return outputResult(state);
          }
				}
			} // full layer is computed
			
			// sort & take best of the waits
			if (waitingNodesFE > K_WAIT) {
				Arrays.sort(waitingNodes, 0, waitingNodesFE, comparator); // uses TimSort
				waitingNodesFE = K_WAIT;
			}
			
			// check time
			if (System.currentTimeMillis() - Player.start > Player.MAX_TIME) {
				return outputResult(state);
			}
			
			// check if all nodes are WAITING

			if (newTail == newHead) {
				currentDay++;
				// transfer waiting nodes into the queue
				System.arraycopy(waitingNodes, 0, nodes, newTail, waitingNodesFE);
				newTail+=waitingNodesFE;
				waitingNodesFE = 0;
			} else {
				if (newTail - newHead > K) {
					// cut the beam
					// sort the still acting nodes
					Arrays.sort(nodes, newHead, newTail, comparator ); // uses TimSort
					newTail = newHead + K;
				}
			}
			
			if (currentDay == lastDay) {
				break; // end search
			}
			
			if (System.currentTimeMillis() - Player.start > Player.MAX_TIME) {
				break;
			}
			head = newHead;
			tail = newTail;
		}

		// best is 1st !
		return outputResult(state);
	}

	private Action outputResult(State state) {
		System.err.println("Went to "+currentDay+" day with "+currentCacheIndex+" nodes ");

		BSNode firstNode = bestNode;

		if (Player.DEBUG_BESTPATH) {
			List<Action> actions = new ArrayList<>();
			while (firstNode.parent != root) {
				actions.add(0, firstNode.fromAction);
				firstNode = firstNode.parent;
			}
			actions.add(0, firstNode.fromAction);
			if (Player.DEBUG_BESTPATH) {
				for (Action action : actions) {
					System.err.println(action);
				}
			}
		} else {
			while (firstNode.parent != root) {
				firstNode = firstNode.parent;
			}
		}

		return firstNode.fromAction;
	}

	private void makeRootNode(State state) {
		root.parent = root;
		root.state.copyFrom(state);
		root.fromAction = null;
		root.score = 0.0;
		root.lastWaitScore = 0.0;
	}
	
	
	public void debugActions() {
		Action bestActions[] = new Action[100];
		// count the nodes
		BSNode current = bestNode;
		int d = 0;
		while (current.fromAction != null) {
			d++;
			current = current.parent;
		}
		d--;
		int bestActionFE = d;
		
		current = bestNode;
		while (current.fromAction != null) {
			bestActions[d--] = current.fromAction;
			current = current.parent;
		}
		
		System.err.println("Best actions ");
		for (int i = 0; i < bestActionFE; i++) {
			System.err.print(bestActions[i]+";");
		}
		System.err.println();
	}
}
