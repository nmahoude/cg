package gitc.entities;

import java.util.List;

public class MovableEntity extends Entity {
  public Factory source;
  public Factory destination; // can be -1 !
  public int remainingTurns; // can be -1
  public int units = 1;
  
  // backup
  public Factory b_source;
  public Factory b_destination; // can be -1 !
  public int b_remainingTurns; // can be -1
  public int b_units = 1;
  public void backup() {
    super.backup();
    b_source = source;
    b_destination = destination;
    b_remainingTurns = remainingTurns;
    b_units = units;
  }
  public void restore() {
    super.restore();
    source = b_source;
    destination = b_destination;
    remainingTurns = b_remainingTurns;
    units = b_units;
  }
  
  public MovableEntity(int id, Owner owner) {
    super(id, owner);
  }
  
  public <A extends MovableEntity> A findWithSameRouteInList(List<A> list) {
    for (A other : list) {
        if (other.source == this.source && other.destination == this.destination) {
            return other;
        }
    }
    return null;
  }
  public void move() {
    remainingTurns--;
  }
}
