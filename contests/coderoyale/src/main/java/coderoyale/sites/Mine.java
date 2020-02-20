package coderoyale.sites;

public class Mine extends Structure {

  public int incomeRate;

  public Mine(Site site) {
    super(site);
    type = Structure.MINE;
  }

  public boolean hasGold() {
    return attachedTo.hasGold();
  }

}
