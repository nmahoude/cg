package fantasticBits.fb.ai;

import fantasticBits.fb.state.Bludger;
import fantasticBits.fb.state.Entity;
import fantasticBits.fb.state.GameState;
import fantasticBits.fb.state.Wizard;
import trigonometry.Point;

public class Move {
  public static final int FULL_THRUST = 150;
  public static final int FULL_POWER = 500;

  public int x;
  public int y;
  public int thrust;
  public Type type;
  public Wizard from;
  public Entity target;
  
  private Move(Point position) {
    type = Type.MOVE;
    x = (int)position.x;
    y = (int)position.y;
    thrust = FULL_THRUST;
  }

  public String toString(boolean inversed) {
    int x = this.x;
    String t = "";
//    if (target != null) {
//      t = " (t->"+target.entityId+")";
//    }
    switch(type) {
    case MOVE:
    case THROW:
      return type.name()+" "+(inversed ? GameState.MAX_X-x : x) +" "+y+" "+thrust + t;
    case ACCIO:
    case FLIPENDO:
    case OBLIVIATE:
    case PETRIFICUS:
      return type.name()+" "+x + t;
    default:
      return "Not implemented : "+type;
    }
  }

  public static Move moveTo(Point goal, Entity target) {
    Move move = new Move(goal);
    move.target = target;
    move.type = Type.MOVE;
    move.thrust = FULL_THRUST;
    return move;
  }
  
  public static Move moveTo(Point goal, Entity target, int thrust) {
    Move move = new Move(goal);
    move.target = target;
    move.type = Type.MOVE;
    move.thrust = thrust;
    return move;
  }

  public static Move throwTo(Wizard wizard, Point goal) {
    Move move = new Move(goal);
    move.from = wizard;
    move.type = Type.THROW;
    move.thrust = FULL_POWER;
    move.target = null;
    return move;
  }

  public static Move castObliviate(Bludger b) {
    Move move = new Move(Point.ZERO);
    move.type = Type.OBLIVIATE;
    move.target = b;
    move.x = b.entityId;
    return move;
  }

  public static Move castAccio(Entity e) {
    Move move = new Move(Point.ZERO);
    move.type = Type.ACCIO;
    move.target = e;
    move.x = e.entityId;
    return move;
  }

  public static Move castFlipendo(Entity e) {
    Move move = new Move(Point.ZERO);
    move.type = Type.FLIPENDO;
    move.target = e;
    move.x = e.entityId;
    return move;
  }

  public static Move castPetrificus(Entity e) {
    Move move = new Move(Point.ZERO);
    move.type = Type.PETRIFICUS;
    move.target = e;
    move.x = e.entityId;
    return move;
  }

}