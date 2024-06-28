package spring2023;

import spring2023.map.Map;

public class Action {
  static Action actions[] = new Action[Map.MAX_CELLS];
  
  static {
    for (int i=0;i<Map.MAX_CELLS;i++) {
      actions[i] = new Action(i);
    }
  }
  
  public static void reset() {
    for (int i=0;i<Map.MAX_CELLS;i++) {
      actions[i].strength = 0;
    }
  }
  
  final int index;
  int strength;
  
  public Action(int index) {
    this.index = index;
    this.strength = 0;
  }

  public String debugString() {
    return "BEACON "+index+" "+strength;
  }

  public static Action get(int i) {
    return actions[i];
  }

  public Action withStrength(int s) {
    this.strength = s;
    return this;
  }
}
