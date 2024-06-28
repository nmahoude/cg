package cultistwars;

public class Unit {

  public int unitId;
  public int unitType;
  public int hp;
  public int x;
  public int y;
  public Pos pos;
  
  public int owner;
  public static final int CULT_LEADER_TYPE = 1;
	public static final int CULTIST = 0;

  public void read(int unitId, int unitType, int hp, int x, int y, int owner) {
    this.unitId = unitId;
    this.unitType = unitType;
    this.hp = hp;
    this.x = x;
    this.y = y;
    this.pos = Pos.get(x, y);
    this.owner = owner;
  }

}
