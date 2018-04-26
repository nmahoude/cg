package coderoyale2.structures;

import coderoyale2.Player;
import coderoyale2.units.Site;

public class Structure {
  public static final int KNIGHT = 0;
  public static final int ARCHER = 1;
  public static final int GIANT = 2;
  
  public static final int MINE= 0;
  public static final int TOWER = 1;
  public static final int BARRACK = 2;
  
  public static final Structure NONE = new Structure(null);
  public final Site attachedTo;
  
  public int owner;
  public int type;
  public int subtype;
  
  private int _owner;
  
  public enum StructureType {
    NONE,
    BARRACK_KNIGHT,
    BARRACK_ARCHER,
    BARRACK_GIANT,
    TOWER,
    MINE
  }
  public Structure(Site site) {
    owner = -1;
    this.attachedTo = site;
  }
  
  public void backup() {
    _owner = owner;
  }
  public void restore() {
    owner = _owner;
  }

  public final boolean isMine() {
    return type == MINE;
  }

  public boolean isBarrack() {
    return type == BARRACK;
  }

}
