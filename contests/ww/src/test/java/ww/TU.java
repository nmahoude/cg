package ww;

import ww.sim.Move;

public class TU {
  public static Move getMove(int i, Dir dir1, Dir dir2) {
    Move move = new Move();
    move.id = i;
    move.dir1 = dir1;
    move.dir2 = dir2;
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
          state.setHeight(x, y, c-'0');
        }
      }
    }
    state.backup();
  }
  public static void setAgent(GameState state, int id, int x, int y) {
    state.agents[id].x = x;
    state.agents[id].y = y;
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
  
}
