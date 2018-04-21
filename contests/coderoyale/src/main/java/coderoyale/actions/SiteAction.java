package coderoyale.actions;

import coderoyale.sites.Site;

public abstract class SiteAction extends Action {

  Site site;
  
  SiteAction(Site site) {
    this.site = site;
  }
}
