package coderoyale.actions;

import coderoyale.sites.Site;
import coderoyale.units.Queen;

public class MoveToSite extends Action {
  Site site;
  
  public MoveToSite(Site site) {
    this.site = site;
  }
  
  public void doAction(Queen me) {
    if (site.isInRange(me)) {
      success();
    } else {
      System.out.println("MOVE " + site.pos);
    }
  }
}
