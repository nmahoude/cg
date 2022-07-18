package bender_ep4;

import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;

public class AStar {

  private static final int MAX_STEPS_AT_INIT = 3000;

	static class AStarNode {
  	Pos pos;
  	int distanceEval;
  	
  	public AStarNode(Pos pos) {
  		this.pos = pos;
		}
  	
  	@Override
  	public String toString() {
  		return ""+pos;
  	}
  }

  AStarNode[] nodes = new AStarNode[21*21];
  
  AStarNode[] cameFrom = new AStarNode[21*21];
  int[] gScore = new int[21*21];
  int[] fScore = new int[21*21];
  long[] castables = new long[21*21];
  long[] possibleSpells = new long[21*21];
  
  PriorityQueue<AStarNode> openSet = new PriorityQueue<>((o1, o2) -> Integer.compare(fScore[o1.distanceEval], fScore[o2.distanceEval]));

  public AStar() {
  	for (int i=0;i<21*21;i++) {
  		Pos pos = Pos.fromOffset(i);
  		nodes[i] = new AStarNode(pos);
  	}
  	
  }
  
  static final int[][] dirs = {{1,0}, {-1,0},{0,1}, {1,0}};

	private int currentMaxDepth;
  
  public List<Pos> compute(State state, Pos start, Pos target) {
  	init(start, target);
    
    while(!openSet.isEmpty()) {
      AStarNode current = openSet.poll();
      if (current.pos == target) {
      	currentMaxDepth = gScore[current.pos.offset];
      	continue;
      }
      
      if (fScore[current.pos.offset] > currentMaxDepth ) {
      	return solutions(target);
      }

      for (int[] decal : dirs) {
      	Pos next = current.pos.decal(decal[0], decal[1]);
      	int nextOffset = next.offset;
				if (State.grid[nextOffset] == 1) continue;
      	
    		int tentativeGScore = gScore[current.pos.offset] + 1;
    		if (tentativeGScore < gScore[nextOffset]) {
      		gScore[nextOffset] = tentativeGScore;
          fScore[nextOffset] = gScore[nextOffset] + next.manhattanDist(target);
					cameFrom[nextOffset] = current;
          // force reinsertion with correct order
					openSet.remove(nodes[nextOffset]);
					openSet.add(nodes[nextOffset]);
    		}
      	
      }
    }      
    
    return solutions(target);
  }

	private void init(Pos start, Pos target) {
		currentMaxDepth = MAX_STEPS_AT_INIT;

		for (int i=0;i<cameFrom.length;i++) {
      gScore[i] = Integer.MAX_VALUE;
      fScore[i] = Integer.MAX_VALUE;
    }		

		openSet.clear();
    openSet.add(nodes[start.offset]);
    
    gScore[start.offset] = 0;
    fScore[start.offset] = start.manhattanDist(target);

	}

	private List<Pos> solutions(Pos target) {
		System.err.println("Computing solution ! ");
		System.err.println("Came from target is " + cameFrom[target.offset]);
		
		List<Pos> path = new ArrayList<>();
		path.add(target);
		
		AStarNode current = cameFrom[target.offset];
		while (current != null) {
			path.add(0, current.pos);
			current = cameFrom[current.pos.offset];
		}
		return path;
	}

}
