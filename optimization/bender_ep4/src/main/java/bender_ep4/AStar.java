package bender_ep4;

import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;

public class AStar {

  private static final int MAX_STEPS_AT_INIT = 3000;

	static class AStarNode {
  	Pos pos;
  	int mask;
  	int distanceEval;
  	//int[] grid = new int[21*21];
  	
  	public AStarNode(Pos pos, int mask) {
  		this.pos = pos;
  		this.mask = mask;
		}
  	
  	@Override
  	public String toString() {
  		return ""+pos+" ["+Integer.toBinaryString(mask)+"]";
  	}
  }

  AStarNode[] nodes = new AStarNode[21*21*2048];
  
  AStarNode[] cameFrom = new AStarNode[21*21*2048];
  int[] gScore = new int[21*21*2048];
  int[] fScore = new int[21*21*2048];
  long[] castables = new long[21*21*2048];
  long[] possibleSpells = new long[21*21*2048];
  
  PriorityQueue<AStarNode> openSet = new PriorityQueue<>((o1, o2) -> Integer.compare(fScore[o1.distanceEval], fScore[o2.distanceEval]));

  public AStar() {
  	for (int i=0;i<21*21;i++) {
  		for (int mask=0;mask<2048;mask++) { // mask of switches
  			Pos pos = Pos.fromOffset(i);
  			nodes[i + 21*21*mask] = new AStarNode(pos, mask);
  		}
  	}
  	
  }
  
  static final int[][] dirs = {{1,0}, {-1,0},{0,1}, {0, -1}};

	private int currentMaxDepth;
  
	
	AStarNode best = null;
	
  public List<Pos> compute(State state, Pos start, Pos target) {
  	init(state.switchState, start, target);
  	
  	state.debug();
  	
    while(!openSet.isEmpty()) {
      AStarNode current = openSet.poll();
      
      // System.err.println("Polling : "+current.pos+" m: "+current.mask);
      
      if (current.pos == target) {
      	currentMaxDepth = gScore[current.pos.offset+21*21*current.mask];
      	System.err.println("Found a solution @depth "+gScore[current.pos.offset+21*21*current.mask]+" "+current);
      	best = current;
      	continue;
      }
      
      if (fScore[current.pos.offset+21*21*current.mask] > currentMaxDepth ) {
      	break; // on ne trouvera plus de meilleure solution
      }

      
      
      for (int[] decal : dirs) {
      	Pos next = current.pos.decal(decal[0], decal[1]);
      	int nextOffset = next.offset;
				
      	if (state.grid[nextOffset] == 1) continue; // wall
      	if (state.grid[nextOffset] == 2) {
      		//continue; // garbage
      	}
      	
				if (state.grid[nextOffset] >= 30) {
					int fieldIndex = state.grid[nextOffset]-30;
					int mask = 1 << fieldIndex;
					if ((current.mask & mask) != 0)	continue; // active field
				}

    		int tentativeGScore = gScore[current.pos.offset + 21*21*current.mask] + 1;
    		if (tentativeGScore < gScore[nextOffset + 21*21*current.mask]) {
  	      
    			int mask;
    			if (state.grid[nextOffset] >= 10 && state.grid[nextOffset] <= 29) { // switch
    				int switchIndex = state.grid[nextOffset] - 10;
  	      	mask = (current.mask ^ (1 << switchIndex)) & 0b11111111111;
  	      } else {
  	      	mask = current.mask;
  	      }
    			
    			int newOffset = nextOffset+21*21*mask;
    			
					gScore[newOffset] = tentativeGScore;
          fScore[newOffset] = gScore[newOffset] + next.manhattanDist(target);
          nodes[newOffset].distanceEval = fScore[newOffset]; 
					cameFrom[newOffset] = current;
					
          // force reinsertion with correct order
					openSet.remove(nodes[newOffset]);
					openSet.add(nodes[newOffset]);
    		}
      }
    }      
    
    return solutions(target);
  }

	private void init(int mask, Pos start, Pos target) {
		currentMaxDepth = MAX_STEPS_AT_INIT;

		for (int i=0;i<cameFrom.length;i++) {
      gScore[i] = Integer.MAX_VALUE;
      fScore[i] = Integer.MAX_VALUE;
    }		

		openSet.clear();
    openSet.add(nodes[start.offset + 21*21*mask]);
    
    gScore[start.offset + 21*21*mask] = 0;
    fScore[start.offset + 21*21*mask] = start.manhattanDist(target);

	}

	private List<Pos> solutions(Pos target) {
		System.err.println("Computing solution ! ");
		System.err.println("Solution is "+best);
		
		List<Pos> path = new ArrayList<>();
		path.add(target);
		
		AStarNode current = best;
		while (current != null) {
			path.add(0, current.pos);
			System.err.println("Came from target " + cameFrom[current.pos.offset + 21*21*current.mask]);
			current = cameFrom[current.pos.offset + 21*21*current.mask];
		}
		return path;
	}

}
