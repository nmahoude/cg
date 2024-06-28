package utg2019.world.maps;

import trigonometryInt.Point;
import utg2019.Player;
import utg2019.sim.Action;
import utg2019.sim.Order;
import utg2019.world.MapCell;
import utg2019.world.World;
import utg2019.world.entity.Robot;

public class TrapAdvisor {
  Point history[][] = new Point[5][201];
  
  public int potentialTraps[] = new int[World.WIDTH*World.HEIGHT];
  public boolean canTransportMine[] = new boolean[5];
  
  public int myPotentialTraps[] = new int[World.WIDTH*World.HEIGHT];
  
  
  public void update(World old, World current, Action myActions[]) {
    updateHistory(current);

    updateMyPotentialTraps(old, current, myActions);
    
    // update non threat cells
    for (int offset=0;offset<World.MAX_OFFSET;offset++) {
      if (old.isCurrentlyKnown(offset) && current.isCurrentlyKnown(offset)) {
        if (old.getOre(offset) != current.getOre(offset)) {
          potentialTraps[offset] = 0;
        }
      }
    }
    
    int newHolesThisTurn[] = new int[World.MAX_OFFSET];
    for (int i=0;i<5;i++) {
      // my actions
      if (myActions[i].order == Order.DIG) {
        potentialTraps[myActions[i].pos.offset] = 0; 
        if (mayHaveBeenDigged(old, current, myActions[i].pos)) { // digged
          newHolesThisTurn[myActions[i].pos.offset]+=1;
        }
      }
      
      // his actions
      Robot oldRobot = old.teams[1].robots[i];
      Robot newRobot = current.teams[1].robots[i];
      if (newRobot.pos == Point.Invalid) continue;
      if (oldRobot.pos != newRobot.pos) continue;
      
      MapCell mc = World.mapCells[newRobot.pos.offset];
      for (MapCell neighbor : mc.neighborsAndSelf) {
        if (  mayHaveBeenDigged(old, current, neighbor.pos)) {
          newHolesThisTurn[neighbor.pos.offset]+=1;
        }
      }
    }

    // count the robots that go in the col 0,
    // if score is not the number of robots, then a robot was not having ore and no-threat is not assured
    int expectedScore = old.teams[1].score;
    for (int i=0;i<5;i++) {
      Robot oldRobot = old.teams[1].robots[i];
      Robot newRobot = current.teams[1].robots[i];
      if (oldRobot.pos == Point.Invalid || newRobot.pos == Point.Invalid) continue;
      if (newRobot.pos.x == 0 && oldRobot.pos.x != 0) {
        expectedScore++;
      }
    }
    boolean resetThreat = current.teams[1].score == expectedScore;
    Player.debug(Player.DEBUG_POTENTIAL_TRAPS_REFLEXION, "Reset Threat is "+resetThreat);

    for (int i=0;i<5;i++) {
      Robot oldRobot = old.teams[1].robots[i];
      Robot newRobot = current.teams[1].robots[i];
      Player.debug(Player.DEBUG_POTENTIAL_TRAPS_REFLEXION, "Checking for robot "+i+" old="+oldRobot.pos+" new="+newRobot.pos);
      if (newRobot.pos == Point.Invalid) continue;
      
      if (oldRobot.pos != newRobot.pos) {
        Player.debug(Player.DEBUG_POTENTIAL_TRAPS_REFLEXION, "  it's a move");
        if (newRobot.pos.x == 0 && oldRobot.pos.x != 0) {
          if (resetThreat) {
            Player.debug(Player.DEBUG_POTENTIAL_TRAPS_REFLEXION, "Reset dangerous for "+i);
            canTransportMine[i] = false;
          } else {
            Player.debug(Player.DEBUG_POTENTIAL_TRAPS_REFLEXION, "Reset can't be done for "+i+" score is not matching ...");
          }
        }
        continue;
      } else {
        Player.debug(Player.DEBUG_POTENTIAL_TRAPS_REFLEXION, "  it's a STAY");
        if (oldRobot.pos.x == 0 && newRobot.pos.x == 0) {
          Point directRight = Point.get(1, newRobot.pos.y);
          if (current.hasHole(directRight.offset)) {
            if (newHolesThisTurn[directRight.offset] == 1) {
              // only one robot could have dig, so its him and he can be dangerous anymore
              Player.debug(Player.DEBUG_POTENTIAL_TRAPS_REFLEXION, "    but There is a hole that only one robot could have dig ...");
              potentialTraps[directRight.offset] = canTransportMine[i] ? 1 : 0;
              canTransportMine[i] = false;
            } else if (canTransportMine[i]){
              canTransportMine[i] = true;
              potentialTraps[directRight.offset] += 1;
            } else {
              canTransportMine[i] = true;
            }
          } else {
            Player.debug(Player.DEBUG_POTENTIAL_TRAPS_REFLEXION, "    It was on first col ... set dangerous for "+i);
            canTransportMine[i] = true;
          }
          continue;
        } else if (canTransportMine[i]) {
          Player.debug(Player.DEBUG_POTENTIAL_TRAPS_REFLEXION, "     set holes around as dangerous");
          MapCell mc = World.mapCells[newRobot.pos.offset];
          Point onlyOneDig = Point.Invalid;
          for (MapCell neighbor : mc.neighborsAndSelf) {
            if (newHolesThisTurn[neighbor.pos.offset] == 1) {
              onlyOneDig = neighbor.pos;
              break;
            }
          }
          
          if (onlyOneDig != Point.Invalid) {
            Player.debug(Player.DEBUG_POTENTIAL_TRAPS_REFLEXION, "     There is only one dig @ "+onlyOneDig+" set trap here");
            potentialTraps[onlyOneDig.offset] += 1;
            canTransportMine[i] = false;
            
          } else {
            for (MapCell neighbor : mc.neighborsAndSelf) {
    //          System.err.println("    Checking "+neighbor.pos+" for hole because dangerous");
              if (current.hasHole(neighbor.pos.offset)) {
                if (old.hasTrap(neighbor.pos.offset) && current.hasTrap(neighbor.pos.offset)) {
                  Player.debug(Player.DEBUG_POTENTIAL_TRAPS_REFLEXION, "won't set insecure "+neighbor.pos+" => still my trap");
                } else if (old.hasRadar(neighbor.pos.offset) && current.hasRadar(neighbor.pos.offset)) {
                  Player.debug(Player.DEBUG_POTENTIAL_TRAPS_REFLEXION, "won't set insecure "+neighbor.pos+" => still my radar");
                } else {
                  Player.debug(Player.DEBUG_POTENTIAL_TRAPS_REFLEXION, "setting insecure "+neighbor.pos);
                  potentialTraps[neighbor.pos.offset] += 1;
                }
              }
            }
          }
        } else {
          Player.debug(Player.DEBUG_POTENTIAL_TRAPS_REFLEXION, "    but was not dangerous");
        }
      }
    }
  }

  private void updateMyPotentialTraps(World old, World current, Action[] myActions) {
    if (Player.DEBUG_POTENTIAL_MY_TRAPS) {
      for (int i=0;i<5;i++) {
        System.err.println("[old] Robot #"+i+" is "+(old.teams[0].robots[i].isConsideredDangerous?"":" NOT ")+" dangerous");
      }
    }
    for (int i=0;i<5;i++) {
      if (old.teams[0].robots[i].pos.x == 0 && current.teams[0].robots[i].pos.x == 0) {
        current.teams[0].robots[i].isConsideredDangerous = true;
        continue;
      }
      if (!old.teams[0].robots[i].isConsideredDangerous) continue;

      if (myActions[i].order == Order.DIG) {
        Point pos = myActions[i].pos;
        int oldHoles = 0, newHoles = 0;
        for (MapCell neighbor : World.mapCells[pos.offset].neighborsAndSelf) {
          if (old.hasHole(neighbor.pos)) oldHoles++;
          if (current.hasHole(neighbor.pos)) newHoles++;
        }
        myPotentialTraps[pos.offset] = 1;
        if (oldHoles != newHoles) {
          current.teams[0].robots[i].isConsideredDangerous = false;
          if (Player.DEBUG_POTENTIAL_MY_TRAPS) System.err.println("Reseting robots "+i+" dangerousity");
        }
      }
    }
    if (Player.DEBUG_POTENTIAL_MY_TRAPS) {
      for (int i=0;i<5;i++) {
        System.err.println("[current] Robot #"+i+" is "+(current.teams[0].robots[i].isConsideredDangerous?"":" NOT ")+" dangerous");
      }
      debugMyPotentialTraps();
    }
  }

  public void debugMyPotentialTraps() {
    Oracle.debugMap("My pot Traps (as seen from enemy", offset -> myPotentialTraps[offset] == 0 ? " " : "O");
    
  }
  private boolean mayHaveBeenDigged(World old, World current, Point target) {
    return (current.hasHole(target) && !old.hasHole(target) ) // new hole 
        || (
            current.getOre(target) != old.getOre(target)
            && current.isCurrentlyKnown(target) 
            && old.isCurrentlyKnown(target)
            ) // digged
;
  }

  private void updateHistory(World current) {
    for (int i=0;i<5;i++) {
      history[i][Player.turn] = current.teams[1].robots[i].pos;
    }
  }

  public void debugDangerousity() {
    for (int i=0;i<5;i++) {
      System.err.println("Robot "+i+" is dangerous ? "+canTransportMine[i]);
    }    
  }  
  
  public void debugPotentialTraps() {
    System.err.print("  ");
    for (int x = 0; x < 30; x++) {
      System.err.print((char)('0'+(x%10)));
    }
    System.err.println();
    
    for (int y = 0; y < 15; y++) {
      System.err.print((char)('0'+ (y%10))+" ");
      
      for (int x = 0; x < 30; x++) {
        if (potentialTraps[x+y*30] > 0) {
          System.err.print("X");
        } else {
          System.err.print(".");
        }
      }
      System.err.println();
    }
  }

  public boolean isDangerous(Point target) {
    return potentialTraps[target.offset] > 0;
  }

  public void addOwnTrap(Point pos) {
    potentialTraps[pos.offset] = 1000; // my traps ! 
  }

}
