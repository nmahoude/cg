package xmasrush.ai.move;

import java.util.ArrayList;
import java.util.List;

import xmasrush.Cell;
import xmasrush.Pos;
import xmasrush.State;
import xmasrush.ai.push.Direction;
import xmasrush.find.AStar;
import xmasrush.find.BFS;

public class MoveAI {
  MoveAction actions[] = new MoveAction[20];
  int actionsFE = 0;
  
  public MoveAI() {
    for (int i=0;i<20;i++) {
      actions[0] = MoveAction.PASS;
    }
  }
  
  List<Cell> bestPath = new ArrayList<>();
  AStar astar = new AStar();
  BFS bfs = new BFS();
  
  private State state;
  
  public void think(State state) {
    this.state = state;
    actionsFE = 0;
    bestPath.clear();
    
    MovePath mp = new MovePath();
    mp.currentCell = state.getCellOf(state.agents[0]);
    mp.steps = 0;
    mp.path.clear();
    mp.collectedItems = 0;
    for (int i=0;i<state.agents[0].questItemsFE;i++) {
      if (state.agents[0].questItems[i] == null) continue;
      mp.questItems.add(state.agents[0].questItems[i]);
    }    

    MovePath bestMp = findFullPath(mp);
    
    if (bestMp != mp) {
      System.err.println("Found a solution ! "+bestMp);
      
      bestPath.clear();
      bestPath.addAll(bestMp.path);
    }
    
    bfs.process(state, bestMp.currentCell, 20-bestMp.path.size());
    
    int bestCellIndex = -1;
    double bestScore = Double.NEGATIVE_INFINITY;
    for (int i=0;i<49;i++) {
      if (bfs.gScore[i] != Integer.MAX_VALUE) {
        
        Cell currentCell = state.cells[i];
        
        int dist = 0;
        int count = 0;
        for (int qi=0;qi<state.agents[0].questItemsFE;qi++) {
          count++;
          if (state.agents[0].questItems[qi] == null) continue;
          dist+=state.agents[0].questItems[qi].manhattan(currentCell.pos);
        }    
        
        double score = 1000.0
            - 1.0 * currentCell.pos.manhattan(State.Center)
            + 10 * currentCell.exitCount()
            - 0.1 * (count > 0 ? 1.0 * dist / count : 0)
            ;
        
        System.err.println(" MOVEAI - Looking for "+currentCell+" => score ="+score);
        if (score > bestScore) {
          bestScore = score;
          bestCellIndex = i;
        }
      }
    }
    System.err.println("Should go to "+state.cells[bestCellIndex]);
    if (state.cells[bestCellIndex] != bestMp.currentCell) {
      List<Cell> movePath = bfs.reconstructPathTo(bestCellIndex);
      if (bestPath.size() == 0) {
        bestPath.addAll(movePath);
      } else {
        bestPath.addAll(movePath.subList(1, movePath.size()-1));
      }
    }
    
    System.err.println("My path will be "+bestPath);
    
//    for (int i=0;i<state.agents[0].questItemsFE;i++) {
//      if (state.agents[0].questItems[i] == null) continue;
//      System.err.println("Looking for Qi :"+state.agents[0].questItems[i]);
//      List<Cell> path = astar.process(state, state.getCellOf(state.agents[0]), state.agents[0].questItems[i]);
//      if (!path.isEmpty()) {
//        bestPath.clear();
//        bestPath.addAll(path);
//        System.err.println("My Path is "+bestPath);
//      }
//    }

    for (int i=0;i<state.agents[1].questItemsFE;i++) {
      List<Cell> path = astar.process(state, state.getCellOf(state.agents[1]), state.agents[1].questItems[i]);
      System.err.println("his Path is "+path);
    }

  }
  
  private MovePath findFullPath(MovePath mp) {
    MovePath best = mp;
    double bestScore = Double.NEGATIVE_INFINITY;
    for (Pos goal : mp.questItems) {
      List<Cell> path = astar.process(state, mp.currentCell, goal);
      if (path.isEmpty()) continue;
      if (mp.steps + path.size() > 20) continue;
      
      MovePath newMP = MovePath.createFrom(mp, goal, path);
      MovePath child = findFullPath(newMP);
      double score = child.collectedItems * 1000 + (100-child.steps);
      if (score > bestScore) {
        bestScore = score;
        best = child;
      }
    }
    
    return best;
  }

  public void output() {
    if (bestPath.isEmpty()) {
      System.out.println(MoveAction.PASS);
    } else {
      System.out.print("MOVE ");
      for (int i=0;i<bestPath.size()-1;i++) {
        System.out.print(pathBetween(bestPath.get(i), bestPath.get(i+1)));
        if (i != bestPath.size()-2) {
          System.out.print(" ");
        }
      }
      System.out.println();
    }
  }

  private String pathBetween(Cell from, Cell to) {
    for (int i=0;i<4;i++) {
      if (from.neighbors[i] == to) {
        return Direction.values()[i].toString();
      }
    }
    return null;
  }
}
