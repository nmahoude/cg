package xmasrush.find;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.PriorityQueue;

import xmasrush.Cell;
import xmasrush.Pos;
import xmasrush.State;

public class AStar {
  static class AStarNode {
  	
    private int offset;

    public AStarNode(int offset) {
      this.offset = offset;
		}
  }

  AStarNode[] nodes = new AStarNode[49];
  AStarNode[] cameFrom = new AStarNode[49];
  int[] gScore = new int[49];
  int[] fScore = new int[49];
  
  PriorityQueue<AStarNode> openSet = new PriorityQueue<>((o1, o2) -> Integer.compare(fScore[o1.offset], fScore[o2.offset]));
  private Pos goal;
  private State state;

  public AStar() {
  	for (int i=0;i<49;i++) {
  		nodes[i] = new AStarNode(i);
  	}
  }

  public List<Cell> process(State state, Cell start, Pos goal) {
    this.state = state; // hold temporary
    this.goal = goal; // hold temporary
    
    int maxDepth = 20;
    init(start, goal);
    
    
    while(!openSet.isEmpty()) {
      AStarNode current = openSet.poll();
      if (fScore[current.offset] > maxDepth ) {
      	return Collections.emptyList();
      }
      
      if (current.offset == goal.offset) {
        return reconstructPath(current);
      }
      
      Cell currentCell = state.cells[current.offset];
      for (int dir=0;dir<4;dir++) {
        Cell nextCell = currentCell.getVisitableNeighbor(dir);
        if (nextCell != Cell.WALL) {
          int tentativeGScore = gScore[current.offset] + 1;
          if (tentativeGScore < gScore[nextCell.pos.offset]) {
            gScore[nextCell.pos.offset] = tentativeGScore;
            fScore[nextCell.pos.offset] = gScore[nextCell.pos.offset] + /* h*/ currentCell.manhattan(nextCell);
            cameFrom[nextCell.pos.offset] = current;
            // force reinsertion with correct order
            openSet.remove(nodes[nextCell.pos.offset]);
            openSet.add(nodes[nextCell.pos.offset]);
          }
        }
      }      
    }
    return Collections.emptyList();
  }

	private void init(Cell startingCell, Pos goal) {
    openSet.clear();
    openSet.add(nodes[startingCell.pos.offset]);
    
    
    for (int i=0;i<cameFrom.length;i++) {
      gScore[i] = Integer.MAX_VALUE;
      fScore[i] = Integer.MAX_VALUE;
    }

    cameFrom[startingCell.pos.offset] = null;
    gScore[startingCell.pos.offset] = 0;
    fScore[startingCell.pos.offset] = startingCell.pos.manhattan(goal);
  }

	private List<Cell> reconstructPath(AStarNode current) {
	  List<Cell> actions = new ArrayList<>();
    
    AStarNode prev;
    actions.add(state.cells[current.offset]);
    while(cameFrom[current.offset]!= null) {
      prev = cameFrom[current.offset];
      actions.add(0, state.cells[prev.offset]);
      current = prev;
    }
    return actions;
	}

}
