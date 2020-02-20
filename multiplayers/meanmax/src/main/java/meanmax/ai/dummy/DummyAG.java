package meanmax.ai.dummy;

import meanmax.Game;
import meanmax.Player;
import meanmax.entities.Doof;
import meanmax.entities.Reaper;
import meanmax.entities.Wreck;
import meanmax.simulation.Action;
import trigo.Position;

/**
 * Straightforward ai
 */
public class DummyAG {
  private static final int NO_TARGET = -500_000;

  public Action[] actions = new Action[3];
  {
    actions[0] = new Action();
    actions[1] = new Action();
    actions[2] = new Action();
  }

  public void think(Player player) {
    dummyReaper(actions[0], player);
    dummyDestroyer(actions[1], player);
    dummyDoof(actions[2], player);
  }

  public void dummyReaper(Action action, Player player) {
    Position.random(action.target, Game.MAP_RADIUS);
    double minDist = Double.POSITIVE_INFINITY;
    Wreck closestWreck = null;
    for (int i=0;i<Game.wrecks_FE;i++) {
      Wreck wreck = Game.wrecks[i];
      if (wreck.dead) continue;
      double dist2 = player.reaper.distance2(wreck);
      if (dist2 < minDist) {
        minDist = dist2;
        closestWreck = wreck;
      }
    }
    if (closestWreck != null) {
      // go to closest wreck
      action.target.x = closestWreck.position.x - 1.5 * player.reaper.speed.vx;
      action.target.y = closestWreck.position.y - 1.5 * player.reaper.speed.vy;
      if (minDist < 1000*1000) {
        action.thrust = 100;
      } else if (minDist < 2000*2000) {
        action.thrust = 250;
      } else {
        action.thrust = 300;
      }
    } else {
      // go to destroyer
      action.thrust = 0;
      action.target.x = player.destroyer.position.x - 1.5 * player.reaper.speed.vx;
      action.target.y = player.destroyer.position.y - 1.5 * player.reaper.speed.vy;
      action.thrust = 300;
    }
  }

  /*
   * Go to nearest tanker
   */
  public void dummyDestroyer(Action action, Player player) {
    action.thrust = 0;
//    double bestScore = Double.NEGATIVE_INFINITY;
//    Tanker best = null;
//    for (int i=0;i<Game.tankers_FE;i++) {
//      Tanker tanker = Game.tankers[i];
//      if (tanker.dead) continue;
//      double score = 1.0 -  player.destroyer.distance(tanker);
//      if (score > bestScore) {
//        bestScore = score;
//        best = tanker;
//      }
//    }
//    if (best != null) {
//      action.target.x = best.position.x - 1.5 * player.destroyer.speed.vx;
//      action.target.y = best.position.y - 1.5 * player.destroyer.speed.vy;
//      action.thrust = 300;
//    } else {
//      action.thrust = 0;
//    }
  }
  public void dummyDoof(Action action, Player player) {
    action.thrust = 0;// TODO no doof

    // check if we can oil 
    if (player.rage >= 3000) {
      // check if enemy reaper are near a wreck but not our reaper, if so cast the oil
      for (int w=0;w<Game.wrecks_FE;w++) {
        Wreck wreck = Game.wrecks[w];
        if (wreck.dead) continue;
        if (wreck.water > 3) continue; // don't spoil oil on almost empty wrecks
        if (wreck.distance2(player.doof) > 2000*2000) continue; // can't cast it
        if (wreck.isInDoofSkill()) continue; // already doofed
        boolean mineNear = false;
        boolean enemyNear = false;
        for (Player p : Game.players) {
          if (p.reaper.isInRange(wreck, wreck.radius+500)) {
            if (p == player) {
              mineNear = true;
            } else {
              enemyNear = true;
            }
          }
        }
        if (mineNear == false && enemyNear == true) {
          action.target.x = wreck.position.x;
          action.target.y = wreck.position.y;
          action.thrust = -1;
          return; // stop here
        }
      }
    } 

    // default is to go to nearest reaper 
    doofToCloserReaper(action, player);
  }

  private void doofToCloserReaper(Action action, Player player) {
    Doof doof = player.doof;
    Reaper closest = null;
    double bestDist = Double.MAX_VALUE;
    for (Player p: Game.players) {
      if (p == player) continue;
      double dist = p.reaper.distance2(doof);
      if (dist < bestDist) {
        bestDist = dist;
        closest = p.reaper;
      }
    }
    action.target.x = closest.position.x - 1.5 * doof.speed.vx;
    action.target.y = closest.position.y - 1.5 * doof.speed.vy;
    action.thrust = 300;
  }
  
  public void output() {
    actions[0].output();
    actions[1].output();
    actions[2].output();
  }

  public void compareEx() {
    // TODO Auto-generated method stub
    
  }

  public void saveExpected() {
    // TODO Auto-generated method stub
    
  }

}
