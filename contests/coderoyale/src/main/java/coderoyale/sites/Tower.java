package coderoyale.sites;

import coderoyale.Pos;
import coderoyale.units.Queen;

public class Tower extends Structure {
  public int life;
  public int attackRadius;

  public Tower(Site site) {
    super(site);
    this.type = Structure.TOWER;
  }

  public boolean protects(Queen me, int extraRadius) {
    return protects(me.pos, extraRadius);
  }

  public boolean protects(Pos pos, int extraRadius) {
    return (attachedTo.pos.dist(pos) < extraRadius + attackRadius);
  }
}
