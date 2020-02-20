package ww;

import java.util.List;
import java.util.Scanner;
import java.util.Set;

import ww.sim.Move;

public class TU {
  public static Move getMove(Agent agent, Dir dir1, Dir dir2) {
    Move move = new Move(agent);
    move.dir1 = dir1;
    move.dir2 = dir2;
    return move;
  }

  public static void fillAllPossiblePositions(GameState state, Set<Point> pos) {
    pos.clear();
    for (int y=0;y<GameState.size;y++) {
      for (int x=0;x<GameState.size;x++) {
        Cell cell = state.grid.get(x,y);
        if (!cell.isValid()) continue;
        pos.add(cell.position);
      }      
    }
  }

  public static void fillAllPossiblePositionsWith(Set<Point> pos, List<Point> points) {
    pos.clear();
    pos.addAll(points);
  }
  
  public static Move getPush(Agent agent, Dir dir1, Dir dir2) {
    Move move = getMove(agent, dir1, dir2);
    move.isPush =true;
    return move;
  }

  public static void setHeights(GameState state, String... rows) {
    state.grid.reset();
    for (int y=0;y<rows.length;y++) {
      for (int x=0;x<rows.length;x++) {
        char c = rows[y].charAt(x);
        if (c == '.') {
          state.grid.setHole(x, y);
        } else {
          state.grid.setHeight(x, y, c-'0');
        }
      }
    }
  }
  
  public static void setAgent(GameState state, int id, int x, int y) {
    state.agents[id].position = Point.get(x, y);
    if (x != -1) {
      state.agents[id].cell = state.grid.get(x, y);
      state.grid.get(x, y).agent = state.agents[id];
    } else {
      state.agents[id].cell = Cell.InvalidCell;
    }
  }

  public static long toLong(String... row) {
    long result = 0L;
    for (int y=0;y<8;y++) {
      for (int x=0;x<8;x++) {
        if (row[y].charAt(x) == '1') {
          result |=  0b1L << x+y*8;
        }
      }
    }
    return result;
  }

  public static void setHeights(GameState state, int size, String... rows) {
    GameState.size = size;
    state.readInit(new Scanner("" + GameState.size + " 2"));
    setHeights(state, rows);
  }

  public static GameState createEmptyGameState(int size) {
    GameState state = new GameState();
    TU.setHeights(state, size, 
        "000000",
        "000000",
        "000000",
        "000000",
        "000000",
        "000000");
    return state;
  }

  public static GameState createFromGameState(GameState previous) {
    GameState state = createEmptyGameState(previous.size);
    previous.copyTo(state);
    return state;
  }

  public static void setAgents(GameState previous, Point... points) {
    for (int i=0;i<4;i++) {
      TU.setAgent(previous, i,points[i].x,points[i].y);
    }
  }
  
}
