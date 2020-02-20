package coderoyale.sites;


public class Barrack extends Structure {
  public int turnBeforeTrain;
  
  public Barrack(Site site) {
    super(site);
    this.type = Structure.BARRACK;
  }

}
