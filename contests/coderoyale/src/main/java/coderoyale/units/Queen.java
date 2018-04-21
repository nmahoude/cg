package coderoyale.units;

import java.util.function.Function;
import java.util.function.Supplier;

import coderoyale.sites.Site;

public class Queen extends Unit {
  public int gold;
  public int touchedSite;
  
  public static class Mover {
    public boolean and(Supplier<Boolean> action) {
      return action.get();
    }
  }
  static class NoDo extends Mover {
    @Override
    public boolean and(Supplier<Boolean> action) {
      return true;
    }
  }
  
  private boolean canBuild(Site site) {
    return site.isInRange(this);
  }

  public Mover moveTo(Site site) {
    if (this.canBuild(site)) {
      return new Mover();
    } else {
      site.moveTo();
      return new NoDo();
    }
  }

  public int closest(Site site1, Site site2) {
    return Double.compare(this.pos.dist(site1.pos), this.pos.dist(site2.pos));
  }

}
