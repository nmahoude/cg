package coderoyale.sites;

import coderoyale.units.Queen;

public class Tower extends Structure {
  public int life;
  public int attackRadius;

  public Tower(Site site) {
    super(site);
    this.type = Structure.TOWER;
  }

  public boolean protects(Queen me, int extraRadius) {
    return (attachedTo.pos.dist(me.pos) < extraRadius + attackRadius);
  }
}
