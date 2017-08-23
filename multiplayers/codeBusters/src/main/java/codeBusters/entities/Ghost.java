package codeBusters.entities;

public class Ghost extends Entity {
  public static final Ghost noGhost = new Ghost();
  
  public State state = State.UNKNOWN;
}
