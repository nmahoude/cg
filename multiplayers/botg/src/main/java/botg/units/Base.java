package botg.units;

import trigonometry.Point;

public class Base {
  public Point pos;
  public int unitId;
  
  public int range;
  
  public boolean inRangeForAttack(Base opp) {
    return inRangeForAttack(opp.pos);
  }
  
  public boolean inRangeForAttack(Point pos) {
    return this.range * this.range > pos.squareDistance(pos);
  }

  
  public double dist(Base other) {
    return pos.distTo(other.pos);
  }

  public boolean inRange(Base base, int range) {
    return dist(base) <= range;
  }

}
