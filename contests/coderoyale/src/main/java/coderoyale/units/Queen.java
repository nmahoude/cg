package coderoyale.units;

import java.util.function.Supplier;

import coderoyale.sites.Site;

public class Queen extends Unit {
  public int gold;
  public int touchedSite;
  
  public static class Action {
    public Action then(Supplier<Boolean> action) {
      boolean result = action.get();
      if (result == true) {
        return new NoDo();
      } else {
        return this;
      }
    }
    public boolean end() {
      return false;
    }
  }
  static class NoDo extends Action {
    @Override
    public Action then(Supplier<Boolean> action) {
      return this;
    }
    public boolean end() {
      return true;
    }
  }
  
  private boolean canBuild(Site site) {
    return site.isInRange(this);
  }

  public Action action(Supplier<Boolean> action) {
    return new Action().then(action);
  }
  
  public Action moveTo(Site site) {
    if (this.canBuild(site)) {
      return new Action();
    } else {
      site.moveTo();
      return new NoDo();
    }
  }

  public int closest(Site site1, Site site2) {
    return Double.compare(this.pos.dist(site1.pos), this.pos.dist(site2.pos));
  }

}
