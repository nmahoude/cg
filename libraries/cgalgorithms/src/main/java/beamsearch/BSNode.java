package beamsearch;

import beamsearch.support.Action;
import beamsearch.support.State;

public class BSNode implements Comparable<BSNode> {

	public BSNode parent;
	public Action fromAction;

	public int expand(State state, BSNode[] nodes, int oldTail) {
		/**
		 * expand from this node by adding child nodes into the nodes, return the newTail from oldTail
		 * 
		 * for each node, the new state must be computed and the fitness calculated
		 * 
		 * /!\ update the best Fitnesse from here  
		 */
		throw new RuntimeException("To implements");
	}

	@Override
	public int compareTo(BSNode other) {
		throw new RuntimeException("Implements comparaison with another node on the same level");
	}

}
