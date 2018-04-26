package coderoyale2.structures;

import coderoyale2.units.Site;

public class Mine extends Structure {

  public int incomeRate;
  private int _incomeRate;

  public Mine(Site site) {
    super(site);
  }
  public void backup() {
    super.backup();
    _incomeRate = incomeRate;
  }
  public void restore() {
    super.restore();
    incomeRate = _incomeRate;
  }
  
}
