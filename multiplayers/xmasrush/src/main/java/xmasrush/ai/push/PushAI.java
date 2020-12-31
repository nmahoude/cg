package xmasrush.ai.push;

import java.util.List;

import xmasrush.Cell;
import xmasrush.State;
import xmasrush.find.AStar;
import xmasrush.find.BFS;
import xmasrush.sim.Simulator;

public class PushAI {
  Simulator sim = new Simulator();
  AStar astar = new AStar();
  BFS bfs = new BFS();
  PushAction bestAction = null;
  
  public void think(State state) {
    double bestScore = Double.NEGATIVE_INFINITY;
    bestAction = null;
    for (PushAction action : PushAction.actions) {
      sim.apply(state, action, null);

      System.err.println("Trying "+action);
      
      bfs.process(state, state.getCellOf(state.agents[0]));
      int count = 0;
      int qItems = 0;
      for (int i=0;i<49;i++) {
        if (bfs.gScore[i] != Integer.MAX_VALUE) {
          count++;
          
          Cell currentCell = state.cells[i];
          for (int q=0;q<state.agents[0].questItemsFE;q++) {
            if (state.agents[0].questItems[q] == currentCell.pos) {
              System.err.println("Can reach qItems "+q+" @"+currentCell);
              qItems++;
            }
          }
        }
      }
      System.err.println(" qItems: "+qItems+", cell count: "+count);
      if (qItems > 0) {
        state.debugGrid();
      }
      double score = 100.0* qItems + count;
      if (score > bestScore) {
        bestScore = score;
        bestAction = action;
      }
//      boolean hasFoundOne = false;
//      for (int i=0;i<state.agents[0].questItemsFE;i++) {
//        List<Cell> path = astar.process(state, state.getCellOf(state.agents[0]), state.agents[0].questItems[i]);
//        if (!path.isEmpty()) {
//          System.err.println("I have a path, which is is "+path);
//          bestAction = action;
//          hasFoundOne = true;
//        }
//      }
//      if (hasFoundOne) {
//        System.err.println("In simulation");
//        state.debugGrid();
//      }
      sim.unapply(state, action, null);
//      if (hasFoundOne) {
//        System.err.println("after undo");
//        state.debugGrid();
//      }
    }
    
    
  }

  public void output() {
    if (bestAction == null) {
      System.out.println("PUSH 0 RIGHT");
    } else {
      System.out.println(bestAction);
    }
  }
}
