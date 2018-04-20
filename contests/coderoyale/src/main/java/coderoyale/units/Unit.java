package coderoyale.units;

import coderoyale.Pos;

public class Unit {
  private static final int KNIGHT = 0; 
  private static final int ARCHER = 1;
  
  public int owner;
  public Pos pos = new Pos();
  public int type;
  public int health;
  
  public boolean isKnight() {
    return type == Unit.KNIGHT;
  }
  
}
