package codeBusters.entities;

import codeBusters.P;
import codeBusters.Player;

public class Ghost extends Entity {
  public static final Ghost noGhost = new Ghost();
  
  public State state = State.START;
  public int energy = 40;
  public int bustersOnIt;

  public Ghost() {
  	position = P.NOWHERE;
	}
  
  public boolean isReleaseInBase() {
    return position.dist2(Player.myBase) < Player.BASE_RANGE_2 || position.dist2(Player.hisBase) < Player.BASE_RANGE_2;
  }
  
  @Override
  public String toString() {
  	return String.format("Ghost[%d] : %s @%s", id, state, position );
  }
}
