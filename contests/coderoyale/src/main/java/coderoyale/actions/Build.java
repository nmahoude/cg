package coderoyale.actions;

import javax.print.CancelablePrintJob;

import coderoyale.sites.Site;
import coderoyale.units.Queen;

public class Build extends SiteAction {
  private BuildType type;

  public enum BuildType {
    TOWER("TOWER"),
    KNIGHT("BARRACKS-KNIGHT"),
    GIANT("BARRACKS-GIANT"),
    MINE("MINE");
    
    String type;
    BuildType(String t) {
      type = t;
    }
    @Override
    public String toString() {
      return type;
    }
  }
  
  
  public Build(Site site, BuildType type) {
    super(site);
    this.type = type;
  }
  
  @Override
  public void doAction(Queen me) {
    if (!site.isInRange(me) || site.isNotBuildable(me)) {
      failure();
    } else {
      System.out.println("BUILD " + site.id + " " + type);
    }
  }

}
