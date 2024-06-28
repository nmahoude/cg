package utg2019.world.maps;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import trigonometryInt.Point;
import utg2019.Player;
import utg2019.sim.Action;
import utg2019.sim.Order;
import utg2019.world.MapCell;
import utg2019.world.World;
import utg2019.world.entity.Robot;

public class Oracle {
  public static final int UNKNWON_ORE = -1;

  public int potentialOre[] = new int[World.WIDTH*World.HEIGHT];
  boolean checkForOreOnNextTurn[] = new boolean[5];

  // work / volatile
  Action possibilities[][] = new Action[19200][5];
  int pFE = 0;
  
  public Oracle() {
    for (int i=0;i<World.MAX_OFFSET;i++) {
      potentialOre[i] = UNKNWON_ORE; 
    }
  }
  
  public void prepareForNextTurn(World current, Action[] nextActions) {
    updateCheckForOre(current, nextActions);
  }

  public void preTurn(World current, Action[] lastActions) {
    updatePotentielOreMap(current, lastActions);
  }
  
  public void removeOreObviouslyDiggedNextTurn(World current) {
    List<Point> willBeDiggedByMe = new ArrayList<>();
    Robot robot;
    for (int r=0;r<5;r++) {
      robot = current.teams[0].robots[r];
      if (!robot.isDead()) {
        for (MapCell mc : World.mapCells[robot.pos.offset].neighborsAndSelf) {
          if (current.isCurrentlyKnown(mc.pos.offset) && current.getOre(mc.pos) > 0) {
            willBeDiggedByMe.add(mc.pos);
            current.setOre(mc.pos, Math.max(0, current.getOre(mc.pos)-1));
          }
        }
      }
      robot = current.teams[1].robots[r];
      if (!robot.isDead()) {
        for (MapCell mc : World.mapCells[robot.pos.offset].neighborsAndSelf) {
          if (current.isCurrentlyKnown(mc.pos.offset) && current.getOre(mc.pos) > 0) {
            current.setOre(mc.pos, Math.max(0, current.getOre(mc.pos)-1));
          }
        }
      }
    }
    // now put back our future ore 
    for (Point p : willBeDiggedByMe) {
      current.setOre(p, Math.min(3, current.getOre(p)+1));
    }
  }

  private void updateCheckForOre(World current, Action[] nextActions) {
    for (int i=0;i<5;i++) {
      checkForOreOnNextTurn[i] = false;
      
      Robot robot = current.teams[0].robots[i];
      if (robot.isDead()) continue;
      if (nextActions[i].order == Order.DIG) checkForOreOnNextTurn[i] = true;
    }
  }
  
  private void updatePotentielOreMap(World current, Action[] lastActions) {
    for (int i=0;i<5;i++) {
      Robot robot = current.teams[0].robots[i];
      if (robot.isDead()) continue;
      if (lastActions[i].order != Order.DIG) continue;
      
      Point digged = lastActions[i].pos;
      if (checkForOreOnNextTurn[i] && robot.hasOre()) {
        if (potentialOre[digged.offset] == -1) {
          potentialOre[digged.offset] = 3-1; // we don't know, so go with 3 minus one because we just dig it
        } else {
          potentialOre[digged.offset] = Math.max(0,  potentialOre[digged.offset]-1); // reduce one ore, but not under 0
        }
      } else {
        potentialOre[digged.offset] = 0;
      }
    }
    
    for (int i=0;i<World.MAX_OFFSET;i++) {
      if (current.isCurrentlyKnown(i)) {
        potentialOre[i] = current.getOre(i);
      }
    }
    
    if (Player.DEBUG_POTENTIAL_ORE_MAP ) {
      debugPotentialOreMap();
    }
  }
  
  public void debugPotentialOreMap() {
    debugMap("potentil ore", offset -> potentielOreDebugItem(offset));
  }
  
  public static void debugMap(String title, Function<Integer,String> fn) {
    System.err.println();
    System.err.println(title);
    System.err.print("  ");
    for (int x = 0; x < 30; x++) {
      System.err.print((char)('0'+(x%10)));
    }
    System.err.println();
    System.err.println();
    
    for (int y = 0; y < 15; y++) {
      System.err.print((char)('0'+ (y%10))+" ");
      
      for (int x = 0; x < 30; x++) {
        System.err.print(fn.apply(x+y*30));
      }
      System.err.println();
    }
  }
  
  private String potentielOreDebugItem(int offset) {
    int value = potentialOre[offset];
    if (value == -1) return "?";
    return ""+(char)('0'+value);
  }

  public void init() {
    for (int i=0;i<5;i++) {
      checkForOreOnNextTurn[i] = true; // all robots starts without ore
    }
  }
}
