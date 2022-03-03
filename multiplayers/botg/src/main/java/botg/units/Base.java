package botg.units;

import trigonometry.Point;

public class Base {
  public Point pos;
  public int unitId;
  
  public int range;
  
  public boolean inRangeForAttack(Base opp) {
    return this.range * this.range > pos.squareDistance(opp.pos);
  }
  
  public double dist(Base other) {
    return pos.distTo(other.pos);
  }

  public boolean inRange(Base base, int range) {
    return dist(base) <= range;
  }

}
