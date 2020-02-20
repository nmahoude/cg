package stc;

import stc.Game;

public class GameTest {

  public static void setNextBlocks(Game game, String... blocks) {
    int index = 0;
    for (String block : blocks) {
      game.nextBalls[index]  = block.charAt(0)-'0';
      game.nextBalls2[index] = block.charAt(1)-'0';
      index++;
    }
  }

}
