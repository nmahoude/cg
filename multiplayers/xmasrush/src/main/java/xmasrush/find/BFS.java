package xmasrush.find;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import xmasrush.Cell;
import xmasrush.State;

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
  	for (int i=0;i<49;i++) {
  		nodes[i] = new AStarNode(i);
  	}
  }

  public void process(State state, Cell start) {
    process(state, start, 20);
  }
  
  public void process(State state, Cell start, int maxDepth) {
    this.state = state; // hold temporary
    
    init(start);
    
    
    while(!openSet.isEmpty()) {
      AStarNode current = openSet.remove(0);
      
      Cell currentCell = state.cells[current.offset];
      for (int dir=0;dir<4;dir++) {
        Cell nextCell = currentCell.getVisitableNeighbor(dir);
        if (nextCell != Cell.WALL) {
          int tentativeGScore = gScore[current.offset] + 1;
          if (tentativeGScore < gScore[nextCell.pos.offset] && tentativeGScore < maxDepth) {
            gScore[nextCell.pos.offset] = tentativeGScore;
            cameFrom[nextCell.pos.offset] = current;
            // force reinsertion with correct order
            openSet.remove(nodes[nextCell.pos.offset]);
            openSet.add(nodes[nextCell.pos.offset]);
          }
        }
      }      
    }
  }

	private void init(Cell startingCell) {
    openSet.clear();
    openSet.add(nodes[startingCell.pos.offset]);
    
    
    for (int i=0;i<cameFrom.length;i++) {
      gScore[i] = Integer.MAX_VALUE;
    }

    cameFrom[startingCell.pos.offset] = null;
    gScore[startingCell.pos.offset] = 0;
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

  public List<Cell> reconstructPathTo(int index) {
    List<Cell> actions = new ArrayList<>();
    
    AStarNode prev;
    actions.add(state.cells[index]);
    while(cameFrom[index]!= null) {
      prev = cameFrom[index];
      actions.add(0, state.cells[prev.offset]);
      index = prev.offset;
    }
    return actions;
  }

}
