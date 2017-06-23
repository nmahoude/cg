package ww;

import java.util.Scanner;

public class Player {
  static GameState state = new GameState();

  public static void main(String args[]) {
    Scanner in = new Scanner(System.in);

    state.readInit(in);

    int round = 0;
    int s = 0;
    // game loop
    while (true) {
      state.readRound(in);
      round++;

      String best[] = new String[state.unitsPerPlayer];
      for (int i = 0; i < 2; i++) {
        int unitX = state.unitX[i];
        int unitY = state.unitY[i];

        int level = state.grid[unitX][unitY];
        int blockLevel = -1;
        for (Dir dir : Dir.values()) {
          for (Dir dirBlock : Dir.values()) {
            int dirX = unitX+dir.dx;
            int dirY = unitY+dir.dy;
            int blockX = dirX+dirBlock.dx;
            int blockY = dirY+dirBlock.dy;

            if (!state.isValid(dirX, dirY)) continue;
            if (!state.isValid(blockX, blockY)) continue;
            if (state.isOccupied(i, dirX, dirY)) continue;
            if (state.isOccupied(i, blockX, blockY)) continue;
            
            if (state.grid[dirX][dirY] - state.grid[unitX][unitY] > 1 ) continue;
            
            if (best[i] == null || state.grid[dirX][dirY] >= level) {
              if ( best[i] != null && state.grid[blockX][blockY] == 3) {
                continue;
              } else {
                level = state.grid[dirX][dirY];
                blockLevel = state.grid[blockX][blockY];
                best[i]="MOVE&BUILD "+i+" "+dir.toString()+" "+dirBlock.toString();
              }
            }
          }
        }
        System.err.println("for "+i+" pos = "+unitX+" "+unitY+" best is "+best[i]);

      }

      int firstPlayerToPlay = s%2;
      if (best[firstPlayerToPlay] != null) {
        System.out.println(best[firstPlayerToPlay]);
      } else {
        System.out.println(best[1-firstPlayerToPlay]);
      }
      s++;
    }
  }
}