package coderoyale.sites;

public class Structure {
  public final static Structure NONE = new Structure(null);

  public static final int MINE= 0;
  public static final int TOWER = 1;
  public static final int BARRACK = 2;
  
  
  public static final int KNIGHT = 0;
  public static final int ARCHER = 0;
  public static final int GIANT = 2;

  public int type = -1;
  public int subtype = -1;
  
  public Site attachedTo;
  public int owner = -1;

  public Structure(Site site) {
    this.attachedTo = site;
  }

  public boolean isMine() {
    return owner == 0;
  }
}
