package coderoyale2.simulation;

import coderoyale2.Constants;
import coderoyale2.Player;
import coderoyale2.Point;
import coderoyale2.structures.Mine;
import coderoyale2.structures.Structure;
import coderoyale2.units.Creep;
import coderoyale2.units.Queen;
import coderoyale2.units.Site;
import coderoyale2.units.Unit;

public class Simulation {

  public void simulate(Action actions[]) {
    for (int i=0;i<10;i++) {
      simulate(actions[i]);
    }
  }
  
  public void simulate(Action action) {
    
    processPlayerActions(action);

    processCreeps();
    processStructures();
    processQueenGold();
    removeDeadCreeps();
    allEntitiesToIntLocation();
  }

  private void processQueenGold() {
  }

  private void processCreeps() {
    resolveCollisions();
    creepDealDamage();
    tearDownEnemyMines();
    damageCreeps();
    queenTearDownTowers();    
  }

  private void processPlayerActions(Action action) {
    double x = action.length * Math.cos(action.angle);
    double y = action.length * Math.sin(action.angle);
    
    Player.queens[0].location.x += x;
    Player.queens[0].location.y += y;
  }

  private void allEntitiesToIntLocation() {
    for (int i=0;i<Player.creepsFE;i++) {
      Unit unit = Player.all[i];
      unit.location.x = (int)unit.location.x;
      unit.location.y = (int)unit.location.y;
    }
  }

  private void removeDeadCreeps() {
    // nothing to do :)
  }

  private void processStructures() {
    for (int o=0;o<Player.numSites;o++) {
      Site closestObstacle = (Site)Player.all[o];
      closestObstacle.act();
    }
  }

  private void queenTearDownTowers() {
    for (int i=0;i<2;i++) {
      Queen queen = Player.queens[i];
      for (int o=0;o<Player.numSites;o++) {
        Site closestObstacle = (Site)Player.all[o];
        if (closestObstacle.structure == Structure.NONE) continue;
        if (closestObstacle.location.distanceTo(queen.location) >= closestObstacle.radius + queen.radius + Constants.TOUCHING_DELTA) {
          Structure struc = closestObstacle.structure;
          if ((struc.isMine() || struc.isBarrack()) && struc.owner != queen.owner) {
            closestObstacle.structure = Structure.NONE;
          }
        }
      }
    }
  }

  private void damageCreeps() {
    for (int i=Player.creepsStart;i<Player.creepsFE;i++) {
      Creep creep = (Creep)Player.all[i];
      if (creep.health <= 0) continue;
      creep.damage(1);
    }
  }

  private void tearDownEnemyMines() {
    // Tear down enemy mines
    for (int i=Player.creepsStart;i<Player.creepsFE;i++) {
      Creep creep = (Creep)Player.all[i];
      if (creep.health <= 0) continue;
      for (int o=0;o<Player.numSites;o++) {
        Site closestObstacle = (Site)Player.all[o];
        if (closestObstacle.structure == Structure.NONE) continue;
        
        if (closestObstacle.location.distanceTo(creep.location) >= closestObstacle.radius + creep.radius + Constants.TOUCHING_DELTA) {
          Structure struc = closestObstacle.structure;
          if (struc.isMine() && struc.owner != creep.owner) {
            closestObstacle.structure = Structure.NONE;
          }
        }
      }
    }
  }

  private void creepDealDamage() {
    for (int i=Player.creepsStart;i<Player.creepsFE;i++) {
      Creep creep = (Creep)Player.all[i];
      if (creep.health <= 0) continue;
      creep.dealDamage();
    }
  }

  private void resolveCollisions() {
    for (int t=0;t<5;t++) {
      for (int i=Player.creepsStart;i<Player.creepsFE;i++) {
        Creep creep = (Creep)Player.all[i];
        if (creep.health <= 0) continue;
        creep.move(1.0 / 5);
      }
      fixCollisions(1);
    }
  }

  private void fixCollisions(int loop) {
    for (int i=0;i<loop;i++) {
      if (!collisionCheck(0.0)) return;
    }
  }

  private boolean collisionCheck(double acceptableGap) {
    boolean hasCollision = false;
    // [  sites(numSites) .. queen0 queen1 creeps...]
    for (int i=0;i<Player.creepsFE;i++) {
      Unit u1 = Player.all[i];
      if (u1.health <= 0) continue;
      double radius = u1.radius;
      double clampDist = u1.mass == 0 ? Constants.OBSTACLE_GAP + radius : radius;
      u1.location.clampWithin(clampDist, Constants.WORLD_WIDTH - clampDist, clampDist, Constants.WORLD_HEIGHT - clampDist);
      
      for (int i2=i+1;i2<Player.creepsFE;i2++) {
        Unit u2 = Player.all[i2];
        if (u2.health <= 0) continue;
        if (u2 == u1) continue;
        boolean distant = u1.radius 
            + u2.radius 
            + acceptableGap 
            - u1.location.distanceTo(u2.location) <= 1e-6;
        if (!distant) {
          double d1, d2;
          if (u1.mass == 0 && u2.mass == 0 ) { d1 = d2 = 0.5; }
          else if (u1.mass == 0 ) { d1 = 0.0; d2 = 1.0; }
          else if (u2.mass == 0 ) { d1 = 1.0; d2 = 0.0;}
          else {
            d1 = u2.mass / (u1.mass + u2.mass);
            d2 = u1.mass / (u1.mass + u2.mass);
          }
          Point u1tou2 = new Point();
          u1tou2.x = u2.location.x - u1.location.x;
          u1tou2.y = u2.location.y - u1.location.y;
          u1.location.x -= d1 * u1tou2.x;
          u1.location.y -= d1 * u1tou2.y;

          u2.location.x += d2 * u1tou2.x;
          u2.location.y += d2 * u1tou2.y;
          
          hasCollision = true;
        }
      }
    }
    return hasCollision;
  }
}
