package coif_old.ai;

import java.util.ArrayList;
import java.util.List;

import coif_old.Board;
import coif_old.Dir;
import coif_old.Player;
import coif_old.Pos;
import coif_old.State;
import coif_old.actions.TrainAction;
import coif_old.units.Unit;

/**
 * Check if we can go straigh to enemy HQ and so win the game
 * @author nmahoude
 *
 */
public class QuickWinAI {
  public boolean wannaPlay = false;
  private State state;
  private List<Dir> dirsToHQ;
  private Pos initPos;

  public QuickWinAI(State state) {
    this.state = state;
  }
  
  public void think() {
    long start = System.currentTimeMillis();
    List<Pos> frontier = state.board.getFrontierIn(state.me.HQ);
    
    if (Player.DEBUG_AI) {
      System.err.println("The out frontier : ");
      System.err.println(frontier);
    }
    
    for (Pos pos : frontier) {
      dirsToHQ = findQuickWinFromPos(pos);
      if (dirsToHQ != null) {
        initPos = pos;
        wannaPlay = true;
        break;
      }
    }

    long stop = System.currentTimeMillis();
    System.err.println("QuickWin algo in " + (stop-start)+ " ms.");
    return;
  }
  
  List<Dir> findQuickWinFromPos(Pos pos) {
    int board[] = new int[12*12];
    
    List<Dir> possibleDir = findDirs(pos, board, 0, new ArrayList<>());
    
    return possibleDir;
  }

  private List<Dir> findDirs(Pos pos, int[] board, int previousCost, List<Dir> previous) {
    for (Dir dir : Dir.values()) {
      Pos next = pos.move(dir);
      int index = next.x + 12 * next.y;
      
      if (!next.isValid()) continue;
      if (board[index] != 0) continue; // already visited
      int cellValue = state.board.board[index];
      if (cellValue == Board.VOID) continue; // can't go through
      if (cellValue == Board.P0_ACTIVE) continue; // our own cell
      
      // check if there is a unit at position (to find the price)
      int currentCost = previousCost + getCostToConquerCell(next);
      if (state.me.gold < currentCost) {
        continue; // path too expensive
      }
      if (board[next.index] !=0 
          && board[next.index] < currentCost) continue; // more expensive than a previous path

      board[next.index]= currentCost; 
      List<Dir> forNext = new ArrayList<>(previous);
      forNext.add(dir);
      if (next == state.opp.HQ) {
        // found it !
        return forNext;
      }
      List<Dir> result = findDirs(next, board, currentCost, forNext);
      if (result != null) {
        return result;
      }
    }
    
    return null; // no solution
  }

  public int getLevelToConquerCell(Pos next) {
    // lvl 3 to pass through tower defense
    Unit b = state.getAnyActiveTowerNearPos(next);
    if (b != null) {
      return 3;
    }

    Unit unit = state.getUnitAtPos(next);
    if (unit != null) {
      return unit.type.neededLevelToKill;
    }

    return 1;
  }
  
  int getCostToConquerCell(Pos next) {
    int lvl = getLevelToConquerCell(next);
    switch(lvl) {
    case 1 : return 10;
    case 2 : return 20;
    case 3 : return 30;
    default : 
      String output = "Unknown lvl " + lvl + " at pos "+ next;
      System.out.println(output);
      throw new RuntimeException(output);
    }
  }

  public void output() {
    
    if (Player.DEBUG_AI) {
      System.err.println("Quick win:");
      System.err.println("From "+initPos);
      System.err.println("Dirs : "+ dirsToHQ);
    }
    // recreate chain train
    // TODO not only level 1 ! keep the level needed for each cell or recalculate it
    Pos current = initPos;
    StringBuffer sb = new StringBuffer();
    for (Dir dir : dirsToHQ) {
      current = current.move(dir);
      int lvl = getLevelToConquerCell(current);
      sb.append(new TrainAction(lvl, current).toString());
    }
    System.out.println(sb.toString());
  }
}
