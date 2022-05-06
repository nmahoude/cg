package spring2022;

public class Future {

  private static final int FUTURE_MAX = 25;
  State[] states = new State[FUTURE_MAX];
  
  public Future() {
    for (int i=0;i<FUTURE_MAX;i++) {
      states[i] = new State();
    }
  }
  
  public void calculate(State originalState) {
    states[0].copyFrom(originalState);
    
    for (int i=1;i<FUTURE_MAX;i++) {
      states[i].copyFrom(states[i-1]);
      
      for (int u= 0;u<states[i].unitsFE;u++) {
        Unit unit = states[i].fastUnits[u];

        if (unit.isDead()) continue;
        unit.pos.add(unit.speed);
        
        if (unit.isInRange(State.myBase, State.BASE_TARGET_DIST)) {
          unit.pos.addAndSnap(0, 0); // reput in map
          unit.speed.alignTo(unit.pos, State.myBase, State.MOB_MOVE);
        } else if (unit.isInRange(State.oppBase, State.BASE_TARGET_DIST)) {
          unit.pos.addAndSnap(0, 0); // reput in map
          unit.speed.alignTo(unit.pos, State.oppBase, State.MOB_MOVE);
        } else if (!unit.pos.insideOfMap()) {
          unit.health = 0;
        }
      }
    }
  }

  public State get(int depth) {
    if (depth >= FUTURE_MAX) {
      System.err.println("WARNING asking for future @ "+depth+" can only give @ "+(FUTURE_MAX-1));
      return states[FUTURE_MAX-1];
    } else {
      return states[depth];
    }
  }
  
}
