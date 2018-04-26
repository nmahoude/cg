package coderoyale;

import java.util.List;
import java.util.stream.Collectors;

import coderoyale.sites.Site;
import coderoyale.sites.Tower;
import coderoyale.units.Queen;
import coderoyale.units.Unit;

public class DamageEvaluator {

  
  /** find a solution to avoid damages ! */
  public void findFleeSolution() {

    /*
     * pour différents chemins, minimiser les dégats sur 5 tours
     * 
     */

    int noMovingDamage = evaluateDamageInFuture(Player.me.pos, Player.me.pos, true);
    System.err.println("Not moving  will deal "+noMovingDamage+ " damages");
    int bestDamage = noMovingDamage;
    if (noMovingDamage == 0) return;
    
    int bestAngle = 0;
    System.err.println("Simulation ...");
    for (int maxTurns=0;maxTurns<2;maxTurns++) {
      double length = 60 * (maxTurns+1); // déplacement max
      for (int i=0;i<8;i++) {
        Pos towards = new Pos();
        towards.x = (int) (Player.me.pos.x + length * Math.cos( 2.0 * Math.PI * i / 8));
        towards.y = (int) (Player.me.pos.y + length * Math.sin( 2.0 * Math.PI * i / 8));
        boolean collide = false;
        for (Site site : Player.allSites) {
          collide = (towards.x > 1920 || towards.x < 0 || towards.y <0 || towards.y > 1000);
          collide = collide || site.pos.dist(towards) < site.radius + Player.me.radius;
        }
        if (collide) {
          continue;
        }
        int damage = evaluateDamageInFuture(Player.me.pos, towards, false);
        System.err.println("Testing moving " + towards + " will deal "+damage+ " damages");
        if (damage < bestDamage) {
          bestDamage = damage;
          bestAngle = i;
        }
      }
    }
    
    if (bestDamage < noMovingDamage) {
      if (Player.me.health > 10           // don't flee too easily
          && Player.him.creeps.size() < 8 // avoid rushes
          && Player.me.health > noMovingDamage // avoid being killed ...
          ) return;
      
      // need to flee, check if we touch an empty site so we can build a tower to further help us
      
      if (Player.me.touchedSite != -1) {
        Site site = Player.getSite(Player.me.touchedSite);
        if (site.structure.owner == -1) {
          Player.me.moveTo(site).then(site::buildTower);
        }
      }
      Pos towards = new Pos();
      towards.x = (int) (Player.me.pos.x + 60.0 * Math.cos( 2.0 * Math.PI * bestAngle / 8));
      towards.y = (int) (Player.me.pos.y + 60.0 * Math.sin( 2.0 * Math.PI * bestAngle / 8));

      
      System.err.println("Flee .... noMovingDamage : " + noMovingDamage + " best damage : " + bestDamage);
      System.err.println("Flee towers "  + towards);
      Player.me.moveTo(towards);
    }
  }
  
  public int evaluateDamageInFuture(Pos queenTarget, Pos towards, boolean debug) {
    if (Player.him.creeps.size() == 0) return 0;
    for (Unit creep : Player.him.creeps) {
      creep.backup();
    }
    Player.me.backup();

    if (debug) {
      printInfo();
    }
    
    int totalDamage = 0;
    
    int damage = 0;
    for (int tour = 0;tour<5;tour++) {

      // move creeps
      for (Unit creep : Player.him.creeps) {
        if (creep.health <= 0) continue;
        moveKnightCreep(creep);
        creep.health -=1; // damage over time 
        if (creep.pos.dist(Player.me.pos) < creep.radius + Player.me.radius + 5) {
          damage += 1;
        } else {
        }
      }
      Player.me.moveTowards(towards);

      if (debug) {
        System.err.println("After turn " + tour);
        printInfo();
      }

      // act on towers
      for (Tower tower : Player.me.towers) {
        towerAct(tower);
      }
      for (Tower tower : Player.him.towers) {
        damage += towerAct(tower);
      }
      totalDamage += damage;
    }
    
    for (Unit creep : Player.him.creeps) {
      creep.restore();
    }
    Player.me.restore();
    return totalDamage;
  }

  private void printInfo() {
    System.err.println("My pos " + Player.me.pos);
    System.err.println("Near creeps pos");
    for (Unit creep : Player.him.creeps) {
      if (creep.pos.dist(Player.me.pos) < creep.radius + Player.me.radius + 100) {
        System.err.println("Creep "+creep.pos + " h:"+creep.health);
      }
    }
  }


  private int towerAct(Tower tower) {
    int damage = 0;
    Queen enemyQueen= tower.owner == 0 ? Player.him : Player.me;
    
    List<Unit> orderedCreeps = enemyQueen.creeps.stream()
        .sorted((u1, u2) -> Double.compare(u1.pos.dist2(tower.attachedTo.pos), u2.pos.dist2(tower.attachedTo.pos)))
        .filter(u -> u.health > 0)
        .collect(Collectors.toList());
    
    Unit closestEnemy = null;
    if (!orderedCreeps.isEmpty())  {
     closestEnemy = orderedCreeps.get(0);
    }
    
    if (closestEnemy != null 
        && closestEnemy.pos.dist(tower.attachedTo.pos) < tower.attackRadius) {
      damageCreep(tower, closestEnemy);
    } else if (closestEnemy == null 
        && enemyQueen.pos.dist(tower.attachedTo.pos) < tower.attackRadius) {
      damage = damageQueen(tower, enemyQueen);
    }
    // TODO prendre en comptele decay des tours ?
    // tower.health -= Constants.TOWER_MELT_RATE;
    // tower.attackRadius = (int)(Math.sqrt((tower.health * Constants.TOWER_COVERAGE_PER_HP + tower.area) / Math.PI));
    
    return damage; // damage to queen
  }
  
  
  private int damageQueen(Tower tower, Queen target) {
    Site obstacle = tower.attachedTo;
    double shotDistance = target.pos.dist(obstacle.pos) - obstacle.radius;
    double differenceFromMax = tower.attackRadius - shotDistance;
    int damage = (int)(Constants.TOWER_QUEEN_DAMAGE_MIN + (differenceFromMax / Constants.TOWER_QUEEN_DAMAGE_CLIMB_DISTANCE));
    target.health -= damage;
    return damage;
  }

  private void damageCreep(Tower tower, Unit target) {
    Site site = tower.attachedTo;
    double shotDistance = target.pos.dist(site.pos) - site.radius;
    double differenceFromMax = tower.attackRadius - shotDistance;
    int damage = (int)(Constants.TOWER_CREEP_DAMAGE_MIN + (differenceFromMax / Constants.TOWER_CREEP_DAMAGE_CLIMB_DISTANCE));
    target.health -= damage;
  }
  
  private void moveKnightCreep(Unit unit) {
    Queen enemyQueen = Player.me;
    
    // move toward enemy queen, if not yet in range
    if (unit.pos.dist(enemyQueen.pos) > unit.radius + enemyQueen.radius + 0) {
      double vx, vy;
      vx = enemyQueen.pos.x - unit.pos.x;
      vy = enemyQueen.pos.y - unit.pos.y;
      // resize to 3.0
      double length = Math.sqrt(vx*vx+vy*vy);
      
      double vcoeff;
      if (/*speed*/ 100 > length) {
      } else { 
        vcoeff = (/*speed*/100.0) / length; 
        vx = vcoeff * vx;
        vy = vcoeff * vy;
      }
      
      unit.pos.x = (int) (unit.pos.x + vx);
      unit.pos.y = (int) (unit.pos.y + vy);
    }
  }
}
