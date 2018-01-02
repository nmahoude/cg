package codeBusters.entities;

import codeBusters.Player;

public class Ghost extends Entity {
  public static final Ghost noGhost = new Ghost();
  
  public State state = State.UNKNOWN;
  public int energy = 40;
  public int bustersOnIt;

  public boolean isReleaseInBase() {
    return position.dist2(Player.myBase) < Player.BASE_RANGE_2 || position.dist2(Player.hisBase) < Player.BASE_RANGE_2;
  }
}
