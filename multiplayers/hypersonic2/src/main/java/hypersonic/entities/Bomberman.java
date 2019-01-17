package hypersonic.entities;

import hypersonic.State;
import hypersonic.utils.P;

public class Bomberman {
  
  public P position;
  public int owner;
  public int bombsLeft;
  public int currentRange;
  public boolean isDead = false;
  public int points = 0;
  public int bombCount = 0;
  
  public Bomberman(final int owner, final P position, final int bombsLeft, final int currentRange) {
    this.owner= owner;
    this.position = position;
    this.bombsLeft = bombsLeft;
    this.currentRange = currentRange;
  }

  public void move(State board, P p) {
    if (board.canWalkOn(p)) {
      board.walkOn(this, p);
    }
  }

  public void copyFrom(Bomberman model) {
    this.owner = model.owner;
    this.position = model.position;
    this.bombsLeft = model.bombsLeft;
    this.currentRange = model.currentRange;
    this.points = model.points;
    this.isDead = model.isDead;
    this.bombCount = model.bombCount;
  }
  
  @Override
  public String toString() {
    return "Bomberman ["+System.identityHashCode(this)+"] ("+owner+"): pos="+position+" bLeft:"+bombsLeft+" cRange:"+currentRange +" isDead:"+isDead;
  }
}
