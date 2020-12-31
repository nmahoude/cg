package xmasrush.ai.move;

import java.util.ArrayList;
import java.util.List;

import xmasrush.Cell;
import xmasrush.Pos;
import xmasrush.State;
import xmasrush.ai.push.Direction;
import xmasrush.find.AStar;

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
