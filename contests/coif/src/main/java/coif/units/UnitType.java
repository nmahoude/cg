package coif.units;

public enum UnitType {
  SOLDIER_1(1, 2),
  SOLDIER_2(2, 3),
  SOLDIER_3(3, 3),
  
  HQ(-1, 1),
  MINE(-1, 1),
  TOWER(-1, 3);

  public final int neededLevelToKill;
  public final int level;
  
  private UnitType(int level, int neededLevelToKill) {
    this.level = level;
    this.neededLevelToKill = neededLevelToKill;
  }
  
  public static UnitType getFromLevel(int lvl) {
    switch (lvl) {
      case 1 : return SOLDIER_1;
      case 2 : return SOLDIER_2;
      case 3 : return SOLDIER_3;
      default:
        throw new RuntimeException("Unknown solder lvl : "+lvl);
    }
  }
  
  public static UnitType getFromBuilding(int value) {
    switch(value) {
      case 0: return HQ;
      case 1: return MINE;
      case 2: return TOWER;
      default: 
        throw new RuntimeException("Unknown building type : "+value);
    }
  }

}
