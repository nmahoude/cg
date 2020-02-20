package coderoyale.actions;

import coderoyale.sites.Mine;
import coderoyale.sites.Site;
import coderoyale.units.Queen;

public class UpgradeMine extends SiteAction {
  private int max;

  public UpgradeMine(Site site, int max) {
    super(site);
    this.max = max;
  }

  @Override
  public void doAction(Queen me) {
    if (!site.isInRange(me) || !site.isAMine()) {
      failure();
    } else {
      Mine mine = (Mine)(site.structure);
      if (mine.incomeRate >= max || site.maxMined()) {
        success();
      } else {
        site.buildMine();
      }
    }
  }

}
