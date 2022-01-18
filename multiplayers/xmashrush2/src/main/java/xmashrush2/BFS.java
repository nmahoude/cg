package xmashrush2;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BFS {
	static class AStarNode {

		private int offset;

		public AStarNode(int offset) {
			this.offset = offset;
		}
	}

	public AStarNode[] nodes = new AStarNode[49];
	public AStarNode[] cameFrom = new AStarNode[49];
	public int[] gScore = new int[49];

	List<AStarNode> openSet = new ArrayList<>();
	private State state;

	public BFS() {
		for (int i = 0; i < 49; i++) {
			nodes[i] = new AStarNode(i);
		}
	}

	public void process(State state, Pos start) {
		process(state, Arrays.asList(start), 20);
	}

	public void process(State state, List<Pos> start) {
		process(state, start, 20);
	}

	static int dirMask[] = new int[] { 0b0001, 0b0010, 0b0100, 0b1000};
	static int invDirMask[] = new int[] { 0b0100, 0b1000, 0b0001, 0b0010};
	
	public void process(State state, Pos start, int maxDepth) {
		process(state, Arrays.asList(start), maxDepth);
	}
	
	public void process(State state, List<Pos> start, int maxDepth) {
		this.state = state; // hold temporary

		init(start);

		while (!openSet.isEmpty()) {
			AStarNode current = openSet.remove(0);

			Pos currentCell = Pos.from(current.offset);
			for (int dir = 0; dir < 4; dir++) {
				Pos nextCell = currentCell.getVisitableNeighbor(dir);
				if (nextCell != Pos.WALL) {
					// check if we can move from start to end
					if ((state.cells[currentCell.offset] & dirMask[dir]) == 0
							|| (state.cells[nextCell.offset] & invDirMask[dir]) == 0) continue;
					
					
					int tentativeGScore = gScore[current.offset] + 1;
					if (tentativeGScore < gScore[nextCell.offset] && tentativeGScore < maxDepth) {
						gScore[nextCell.offset] = tentativeGScore;
						cameFrom[nextCell.offset] = current;
						// force reinsertion with correct order
						openSet.remove(nodes[nextCell.offset]);
						openSet.add(nodes[nextCell.offset]);
					}
				}
			}
		}
	}

	private void init(List<Pos> startingCells) {
		for (int i = 0; i < cameFrom.length; i++) {
			gScore[i] = Integer.MAX_VALUE;
		}

		openSet.clear();
		for (Pos p : startingCells) {
			openSet.add(nodes[p.offset]);
			cameFrom[p.offset] = null;
			gScore[p.offset] = 0;
		}


	}

	private List<Pos> reconstructPath(AStarNode current) {
		List<Pos> actions = new ArrayList<>();

		AStarNode prev;
		actions.add(Pos.from(current.offset));
		while (cameFrom[current.offset] != null) {
			prev = cameFrom[current.offset];
			actions.add(0, Pos.from(prev.offset));
			current = prev;
		}
		return actions;
	}

	public List<Pos> reconstructPathTo(int index) {
		List<Pos> actions = new ArrayList<>();

		AStarNode prev;
		actions.add(Pos.from(index));
		while (cameFrom[index] != null) {
			prev = cameFrom[index];
			actions.add(0, Pos.from(prev.offset));
			index = prev.offset;
		}
		return actions;
	}

	
	public void debugReachablePos() {
		System.err.println("I can reach ");
		for (int i=0;i<49;i++) {
			if (gScore[i] != Integer.MAX_VALUE) {
				System.err.print(Pos.from(i)+" , ");
			}
		}
		System.err.println();
	}
}
