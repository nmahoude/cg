package bender_ep4;

import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;

public class AStar2 {

  private static final int MAX_STEPS_AT_INIT = 3000;

	static class AStarNode {
  	Pos pos;
  	int mask;
  	int trueDistance;
  	int predictionDistance;
  	AStarNode parent;
  	
  	int[] grid = new int[21*21];
  	
  	public AStarNode(AStarNode parent, Pos pos, int mask) {
  		this(parent.grid, pos, mask);
  		this.parent = parent;
		}
  	
  	public AStarNode(int[] grid, Pos pos, int mask) {
  		this.pos = pos;
  		this.mask = mask;
  		System.arraycopy(grid, 0, this.grid, 0, 21*21);
		}

		@Override
  	public String toString() {
  		return ""+pos+" ["+Integer.toBinaryString(mask)+"]";
  	}
  	
  	int distanceEval() {
  		return trueDistance + predictionDistance;
  	}
  }

  PriorityQueue<AStarNode> openSet = new PriorityQueue<>((a1, a2) -> Integer.compare(a1.distanceEval(), a2.distanceEval()));

  public AStar2() {
  }
  
  static final int[][] dirs = {{1,0}, {-1,0},{0,1}, {0, -1}};

	private int currentMaxDepth;
  
	
	AStarNode best = null;
	
  public List<Pos> compute(State state, Pos start, Pos target) {
  	state.debug();
  	
  	AStarNode startNode = new AStarNode(state.grid, start, state.switchState);
		openSet.clear();
  	openSet.add(startNode);
  	
    while(!openSet.isEmpty()) {
      AStarNode current = openSet.poll();
      
      System.err.println("Polling : "+current.pos+" m: "+current.mask);
      
      if (current.pos == target) {
      	currentMaxDepth = current.distanceEval();
      	System.err.println("Found a solution @depth "+current.distanceEval()+" "+current);
      	best = current;
      	continue;
      }
      
      if (current.distanceEval() > currentMaxDepth ) {
      	break; // on ne trouvera plus de meilleure solution
      }

      
      
      for (int[] decal : dirs) {
      	Pos next = current.pos.decal(decal[0], decal[1]);
      	int nextOffset = next.offset;
				
      	if (current.grid[nextOffset] == 1) continue; // wall
      	if (current.grid[nextOffset] == 2 && cawWalk(current.grid, next.decal(decal[0], decal[1]))) {
      		// TODO move garbage!
      		continue; // garbage
      	}
      	
				if (current.grid[nextOffset] >= 30) {
					int fieldIndex = current.grid[nextOffset]-30;
					int mask = 1 << fieldIndex;
					if ((current.mask & mask) != 0)	continue; // active field
				}

    		int tentativeGScore = current.trueDistance + 1;
    		if (tentativeGScore < 3000 ) { // TODO comment comparer avec un état sur lequel on est déjà passé ?

    			AStarNode newNode = new AStarNode(current.grid, next, 0);
    			
    			int mask;
    			if (current.grid[nextOffset] >= 10 && current.grid[nextOffset] <= 29) { // switch
    				int switchIndex = current.grid[nextOffset] - 10;
  	      	mask = (current.mask ^ (1 << switchIndex)) & 0b11111111111;
  	      } else {
  	      	mask = current.mask;
  	      }
    			
    			newNode.mask = mask;
    			
        	if (newNode.grid[nextOffset] == 2) {
        		newNode.grid[next.decal(decal[0], decal[0]).offset] = 2;
        		newNode.grid[next.offset] = 0;
        		continue; // garbage
        	}
    			
    			
    			
    			
					openSet.add(newNode);
    		}
      }
    }      
    
    return solutions(target);
  }

	private boolean cawWalk(int[] grid, Pos decal) {
		return  grid[decal.offset] != 1 
				&& grid[decal.offset] != 2
				&& decal != State.target;
	}

	private List<Pos> solutions(Pos target) {
		System.err.println("Computing solution ! ");
		System.err.println("Solution is "+best);
		
		List<Pos> path = new ArrayList<>();
		path.add(target);
		
		AStarNode current = best;
		while (current != null) {
			path.add(0, current.pos);
			System.err.println("Came from target " + current.parent);
			current = current.parent;
		}
		return path;
	}

}
