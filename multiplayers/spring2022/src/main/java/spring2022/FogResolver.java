package spring2022;

public class FogResolver {
  public boolean seenUnits[] = new boolean[500];
  private Unit units[] = new Unit[500];
  {
    for (int i=0;i<500;i++) units[i] = new Unit();
  }
  
  
  private Unit[] fogUnitsCandidates = new Unit[500];
  private int fogUnitsCandidatesFE;

  private Unit[] activeUnitsInFog = new Unit[500];
  private int activeUnitsInFogFE;
  
  public void startOfturn(State state) {
    fogUnitsCandidatesFE = 0;
    
    for (int u= 0;u<state.unitsFE;u++) {
      Unit unit = state.fastUnits[u];

      // System.err.println("Adding in fog candidates : "+unit);
      units[unit.id].copyFrom(unit);
      
      fogUnitsCandidates[fogUnitsCandidatesFE++] = units[unit.id];
      
      if (! seenUnits[unit.id]) {
        // first time we see the unit, check if it is near a border, if saw add the symmetry
        if (unit.pos.x < 500 || unit.pos.x > State.WIDTH - 500
            || unit.pos.y < 500 || unit.pos.y > State.HEIGHT -500) {

          int id = unit.pos.y > State.HEIGHT / 2  ^ !Player.inversed? unit.id + 1: unit.id-1;
          
          System.err.println("Unit" +unit + "Near border, Adding unit "+id+" via symmetry ! ");
          if (seenUnits[id]) {
            System.err.println("   already saw "+id);
          } else {
            Unit symmetry = units[id];
            symmetry.copyFrom(unit);
            symmetry.id = id;
            symmetry.pos.inverse();
            symmetry.speed.inverse();
            fogUnitsCandidates[fogUnitsCandidatesFE++] =  symmetry;
            seenUnits[id] = true;
            System.err.println("Put symmetry in fog : "+symmetry);
          }
        }
      }
      
      seenUnits[unit.id] = true; 
    }
  }

  public void endOfRead(State state) {
    
    for (int i=0;i<fogUnitsCandidatesFE;i++) {
      Unit unit = state.findUnitById(fogUnitsCandidates[i].id);
      if (unit != null) {
        // remove from candidates
        fogUnitsCandidates[i] = fogUnitsCandidates[fogUnitsCandidatesFE-1];
        fogUnitsCandidatesFE--;
        i--;
      }
    }
    
    activeUnitsInFogFE = 0;
    for (int i=0;i<fogUnitsCandidatesFE;i++) {
      Unit unit = fogUnitsCandidates[i];
      // System.err.println("checking fog unit "+unit);
      
      playTurnInFog(unit);
      
      if (!unit.pos.insideOfMap()) {
        //System.err.println("    out of map - discarding");
        continue;
      }
      if (shouldSee(state, unit.pos)) {
        //System.err.println("    should see it, but no - discarding");
        continue;
      }
      
      if (unit.pos.isInRange(State.oppBase, State.BASE_KILL_RADIUS)) {
        continue;
      }
      //System.err.println("    keeping, still in fog");

      activeUnitsInFog[activeUnitsInFogFE++] = unit;
    }
  }

  private void playTurnInFog(Unit unit) {
    unit.pos.add(unit.speed);

    if (unit.isInRange(State.myBase, State.BASE_TARGET_DIST)) {
      unit.pos.addAndSnap(0, 0); // put back in map
      unit.speed.alignTo(unit.pos, State.myBase, State.MOB_MOVE);
    } else if (unit.isInRange(State.oppBase, State.BASE_TARGET_DIST)) {
      unit.pos.addAndSnap(0, 0); // put back in map
      unit.speed.alignTo(unit.pos, State.oppBase, State.MOB_MOVE);
    } else if (!unit.pos.insideOfMap()) {
      unit.health = 0;
    }
    
    if (unit.shieldLife > 0 ) unit.shieldLife--;
  }
 
  private boolean shouldSee(State state, Pos pos) {
    for (Hero hero : state.myHeroes) {
      if (hero.isInRange(pos, State.HERO_VIEW_RADIUS)) {
        return true;
      }
    }
    return false;
  }

  public void reinsertFoMobs(State state) {
    for (int i = 0; i < activeUnitsInFogFE; i++) {
      Unit unit = activeUnitsInFog[i];
      state.addFogMob(unit);
    }
  }

  public void debug() {
    if (State.DEBUG_INPUTS) {
      System.err.println("****************************");
      System.err.println("UNITS IN FOG - debug 'input'");
      System.err.println("****************************");
      System.err.println("^"+activeUnitsInFogFE);
      for (int i = 0; i < activeUnitsInFogFE; i++) {
        activeUnitsInFog[i].debugInput();
      }
    }
  }
}
