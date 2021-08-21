package pr.explore;

import java.util.HashSet;
import java.util.Set;

import pr.Cell;

public class ExplorerNode {
	ExplorerNode parent;
	public Cell cell;
	public Set<ExplorerNode> childs = new HashSet<>();
	public int podsNeeded;
	
	public ExplorerNode(Cell origin) {
		cell = origin;
	}

	public void backtrack() {
		ExplorerNode current = this.parent;
		while (current != null) {
			current.podsNeeded++;
			if (current.parent != null && current.parent.parent == null) {
			}
			current = current.parent;
		}
	}

	public Cell baseCell() {
		ExplorerNode current = this.parent;
		while (current != null) {
			current.podsNeeded++;
			if (current.parent != null && current.parent.parent == null) {
				return current.cell;
			}
			current = current.parent;
		}
		return null;
	}
}
