package botg.units;

import botg.Pos;

public class Base {
  public Pos pos;
  public int unitId;
  
  public int range;
  
  public boolean inRangeForAttack(Base opp) {
    return this.range * this.range > pos.sqDist(opp.pos);
  }
  
  public int dist(Base other) {
    return pos.dist(other.pos);
  }

  public boolean inRange(Base base, int range) {
    return dist(base) <= range;
  }

}
