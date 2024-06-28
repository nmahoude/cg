package spring2022;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import spring2022.ai.TriAction;

public class ControlOracle {
  public static final int MAX = 600;

  boolean[] isControlled = new boolean[MAX]; 
  Pos[] positions = new Pos[MAX];
  int[] vx =  new int[MAX];
  int[] vy = new int[MAX];
  
  Map<Integer, List<Pos>> targets = new HashMap<>();
  Map<Integer, List<Pos>> nextTargets = new HashMap<>();
  
  {
    for (int i=0;i<MAX;i++) {
      positions[i] = new Pos();
    }
  }
  
  public void update(TriAction action) {
    // System.err.println("CONTROL ORACLE UPDATE");
    for (Entry<Integer, List<Pos>> entry : targets.entrySet()) {
      isControlled[entry.getKey()] = false;
    }
    targets.clear();
    
    targets.putAll(nextTargets);
    // System.err.println("next target "+nextTargets +" => "+targets);
    
    nextTargets.clear();
    for (int h=0;h<3;h++) {
      if (action.actions[h].type == Action.TYPE_CONTROL) {
        List<Pos> registeredPositions = nextTargets.get(action.actions[h].targetEntity);
        if (registeredPositions == null) {
          registeredPositions = new ArrayList<>();
          nextTargets.put(action.actions[h].targetEntity, registeredPositions);
        }
        // System.err.println("Registering control for next turn : "+action.actions[h].targetEntity+" @ "+action.actions[h].target);
        registeredPositions.add(new Pos(action.actions[h].target));
      }
    }
    // System.err.println("next target "+nextTargets);
    
  }

  public void calculateActiveControls(State state) {
    System.err.println("CONTROL ORACLE CALCULATE ACTIVE CONTROLS");
    System.err.println("Size of targets is "+targets.size());
    for (Entry<Integer, List<Pos>> entry : targets.entrySet()) {
      
      int id = entry.getKey();
      List<Pos> positions = entry.getValue();

      // System.err.println("Applying control from prev turn : "+id+" @ "+positions);

      Unit unit = state.findUnitById(id);
      if (unit == null) {
        // System.err.println("Oracle warning : can't find back unit "+id);
        continue;
      }

      updatePos(id, unit, positions);
    }
    
  }

  void updatePos(int id, Unit unit, List<Pos> positions) {
    double dx = 0;
    double dy = 0;
    for (Pos pos : positions) {
      
      double dist = Math.max(State.MOB_MOVE, unit.pos.dist(pos));
      if (dist != 0) {
        dx += State.MOB_MOVE * (pos.x - unit.pos.x) / dist;
        dy += State.MOB_MOVE * (pos.y - unit.pos.y) / dist;
      }
    }
    dx /= positions.size();
    dy /= positions.size();
    
    
    this.isControlled[id] = true;
    this.positions[id].copyFrom(unit.pos);
    this.positions[id].addAndSnap(dx, dy);
    this.vx[id] = (int)dx;
    this.vy[id] = (int)dy;
    
//    System.err.println("MOB "+id+" is controlled this turn, ");
//    System.err.println("next pos is "+this.positions[id]);
//    System.err.println("next speed is "+vx[id]+", "+vy[id]);
  }
  
  
}
