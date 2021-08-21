package pr.explore;

import java.util.ArrayDeque;
import java.util.HashSet;
import java.util.Queue;
import java.util.Set;

import pr.Cell;

public class Explorer {
	Set<Cell> visited = new HashSet<>();
	Queue<ExplorerNode> toVisit = new ArrayDeque<>();

	public ExplorerNode exploreFrom(Cell origin) {
		System.err.println("Origin is "+origin);
		visited.clear();
		toVisit.clear();
		
		ExplorerNode root = new ExplorerNode(origin);
		toVisit.add(root);
		visited.add(origin);
		
		while(!toVisit.isEmpty()) {
			ExplorerNode current = toVisit.poll();
			
			int added = 0;
			for (Cell n : current.cell.neighbors) {
				if (n.isForbiden()) continue;
				if (visited.contains(n)) continue;
				visited.add(n);
				added++;
				
				ExplorerNode child = new ExplorerNode(n);
				current.childs.add(child);
				child.parent = current;
				toVisit.add(child);
			}
			
			if (added == 0 && current.cell.platinum == -1) {
				current.podsNeeded = 1;
				current.backtrack();
				//System.err.println("Backtracking from "+current.cell+" to "+current.baseCell());
			}
		}
		
		return root;
	}
}
