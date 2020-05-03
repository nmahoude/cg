package calm.astar;

import java.util.PriorityQueue;

import calm.Item;
import calm.Player;
import calm.State;

public class AStar {

	
	public AStarNode find(State state, Item goal) {
		
		
		AStarNode root = new AStarNode();
		root.init(state);
		
		PriorityQueue<AStarNode> toVisit = new PriorityQueue<AStarNode>((o1, o2) -> Integer.compare(o1.getTotalTurns(), o2.getTotalTurns()));
		toVisit.add(root);

		AStarNode best = null;
		while (!toVisit.isEmpty()) {
			AStarNode current = toVisit.poll();
			
			if (current.getCurrentTurns() > 100) {
			  return null;
			}
			if (best != null && best.getTotalTurns() < current.getTotalTurns()) {
				continue;
			}
			if (Player.DEBUG_ASTAR) System.err.println("Visiting "+current.debug());
			current.find(goal);

			for (int i=0;i<current.childsFE;i++) {
				AStarNode child = current.childs[i];
				
				if (child.isFinished()) {
					if (Player.DEBUG_ASTAR) System.err.println("**current best "+child.debug());
					best = child;
				}
				if (best == null || best.getTotalTurns() > child.getTotalTurns()) {
					toVisit.add(child);
				}
			}
			
		}
		
		System.err.println("Result best = "+best.debug());
		return best;
	}
}
