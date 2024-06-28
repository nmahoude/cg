package coif.ai;

import java.util.ArrayList;
import java.util.List;

import coif.Board;
import coif.Dir;
import coif.Player;
import coif.Pos;
import coif.State;
import coif.units.Unit;
import coif.units.UnitType;

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

  private Simulation sim;

  public QuickWinAI(Simulation sim, State state) {
      this.sim = sim;
      this.state = state;
  }

  public void think() {
      long start = System.currentTimeMillis();
      List<Pos> frontier = state.board.getFrontierIn(state.me.HQ, Board.P0_ACTIVE);
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
      System.err.println("QuickWin algo in " + (stop - start) + " ms.");
      if (dirsToHQ != null) {
          System.err.println("Found a quickwin ...");
          System.err.println("From " + initPos + " -> " + dirsToHQ);
          Pos current = initPos;
          for (Dir dir : dirsToHQ) {
              current = current.move(dir);
              UnitType type = getLevelToConquerCell(current);
              sim.trainUnit(type, current);
          }
      }
      return;
  }

  List<Dir> findQuickWinFromPos(Pos pos) {
      int board[] = new int[12 * 12];
      ArrayList<Dir> previous = new ArrayList<>();
      List<Dir> possibleDir = findDirs(pos, board, 0, previous);
      return possibleDir;
  }

  private List<Dir> findDirs(Pos pos, int[] board, int previousCost, List<Dir> previous) {
      for (Dir dir : Dir.values()) {
          Pos next = pos.move(dir);
          int index = next.x + 12 * next.y;
          if (!next.isValid())
              continue;
          int cellValue = state.board.cells[index].getStatut();
          // can't go through
          if (cellValue == Board.VOID)
              continue;
          // our own cell
          if (cellValue == Board.P0_ACTIVE)
              continue;
          // check if there is a unit at position (to find the price)
          int currentCost = previousCost + getCostToConquerCell(next);
          if (state.me.gold < currentCost) {
              // path too expensive
              continue;
          }
          if (board[next.index] != 0 && board[next.index] < currentCost)
              // more expensive than a previous path
              continue;
          board[next.index] = currentCost;
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
      // no solution
      return null;
  }

  UnitType getLevelToConquerCell(Pos next) {
      // lvl 3 to pass through tower defense
      Unit b = state.getAnyActiveTowerNearPos(next);
      if (b != null) {
          return UnitType.SOLDIER_3;
      }
      Unit unit = state.getUnitAtPos(next);
      if (unit != null) {
          if (unit.type.neededLevelToKill == 1)
              return UnitType.SOLDIER_1;
          else if (unit.type.neededLevelToKill == 2)
              return UnitType.SOLDIER_2;
          else if (unit.type.neededLevelToKill == 3)
              return UnitType.SOLDIER_3;
          else
              throw new RuntimeException("Unknown unit to kill " + unit);
      } else {
          // no unit
          return UnitType.SOLDIER_1;
      }
  }

  int getCostToConquerCell(Pos next) {
      UnitType lvl = getLevelToConquerCell(next);
      switch(lvl) {
          case SOLDIER_1:
              return 10;
          case SOLDIER_2:
              return 20;
          case SOLDIER_3:
              return 30;
          default:
              String output = "Unknown lvl " + lvl + " at pos " + next;
              System.out.println(output);
              throw new RuntimeException(output);
      }
  }
}