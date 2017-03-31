package gitc.entities;

import java.util.List;
import java.util.Scanner;

import gitc.GameState;
import gitc.ag.AGSolution;

public class Factory extends Entity {
  // game constant
  public int[] distances;
  
  // turn constant (no need to backup)
  public int[] unitsReadyToFight  = { 0, 0 };
  public int[] future = new int[AGSolution.SIMULATION_DEPTH];
  public boolean isFront;
  public int unitsNeededCount;
  public int unitsNeededAt;
  public int unitsDisposable;
  public boolean isUnderAttack;
  public Factory nearestEnnemyFactory;

  // simulation variables (need to backup)
  public int units;
  public int productionRate; // 0->3
  public int disabled = 0;
  public int[] unitsInTransit = { 0, 0 };
  public double influence;
  public boolean bombIncomming = false;
  public int distanceToFront;
  
  public int b_units;
  public int b_productionRate; // 0->3
  public int b_disabled = 0;
  public int[] b_unitsInTransit = { 0, 0 };
  public double b_influence;
  public boolean b_bombIncomming;
  public int b_distanceToFront;

  

  @Override
  public String toString() {
    return "Factory("+id+") player="+playerId+" prod="+productionRate+" units="+units + (disabled>0 ? "DISABLED" :"");
  }
  
  
  // backup
  public void backup() {
    super.backup();
    b_units = units;
    b_productionRate = productionRate;
    b_disabled = disabled;
    b_unitsInTransit[0] = unitsInTransit[0];
    b_unitsInTransit[1] = unitsInTransit[1];
    b_influence = influence;
    b_bombIncomming = bombIncomming;
    b_distanceToFront = distanceToFront;
  }
  public void restore() {
    super.restore();
    units = b_units;
    productionRate = b_productionRate;
    disabled = b_disabled;
    unitsInTransit[0] = b_unitsInTransit[0];
    unitsInTransit[1] = b_unitsInTransit[1];
    influence = b_influence;
    bombIncomming = b_bombIncomming;
    distanceToFront = b_distanceToFront;
  }
  
  public Factory(int id, int factoriesCount) {
    super(id, null);
    distances = new int[factoriesCount];
    distances[id] = 0; // own distance
  }
  
  public void setupDistance(Factory toFactory, int distance) {
    distances[toFactory.id] = distance;
  }
  
  /**
   * Score who have the influence on this factory
   * @param troops
   * @return
   */
  public double calculateInfluence(List<Troop> troops) {
    // current units
    double totalUnits = units;
    double currentUnitsInfluence = units * (this.owner == GameState.me ? 1.0 : -1.0);
    
    // incomming troops
    for (Troop troop : troops) {
      if (troop.destination == this) {
        double local = troop.units / troop.remainingTurns;
        currentUnitsInfluence += (troop.owner == GameState.me ? 1.0 : -1.0) * local;
        totalUnits += Math.abs(local);
      }
    }
    
    // neighbors factory
    for (Factory factory : GameState.factories) {
      if (factory != this && factory.owner != null) {
        double local = factory.units / factory.getDistanceTo(this);
        currentUnitsInfluence += (factory.owner == GameState.me ? 1.0 : -1.0) * local;
        totalUnits += Math.abs(local);
      }
    }
    
    this.influence = currentUnitsInfluence / totalUnits;
    return influence;
  }
  
  @SuppressWarnings("unused")
  public void read(Scanner in) {
    readPlayer(in.nextInt());
    units = in.nextInt();
    productionRate = in.nextInt();
    disabled = in.nextInt();
    int unused2 = in.nextInt();
  }

  public void clear() {
    unitsInTransit[0] = unitsInTransit[1] = 0;
    isFront = true;
    bombIncomming = false;
  }

  public String tddOutput() {
    return ".f(new FB().id("+id+").player("+playerId+").units("+units+").prod("+productionRate+").disabled("+disabled+").build())";
  }

  public void addTroop(Troop troop) {
    unitsInTransit[troop.owner.id] += troop.units;
  }

  public int getDistanceTo(Factory toFactory) {
    return distances[toFactory.id];
  }

  public int getCurrentProductionRate() {
    return (disabled == 0) ? this.productionRate : 0;
  }
  
  public int neededUnit() {
    int neededUnits = unitsInTransit[GameState.opp.id] - this.units;
    return neededUnits <= 0 ? 0 : neededUnits;
  }
  
  public boolean isUnderAttackBy(Owner attacker) {
    if (owner == attacker) {
      return false;
    }
    if (unitsInTransit[attacker.id] > 0) {
      return true;
    }
    return false;
  }
  
  public void calculateFront() {
    if (owner == null) {
      isFront = false;
      return;
    }

    isFront = true;
    isUnderAttack = unitsInTransit[owner.getEnemy().id] > 0;
    if (isUnderAttack) {
      // combat is coming, we are not the back of the army anymore
      return;
    }

    Factory closestOpp = getClosestEnemyFactory();
    if (closestOpp == null) {
      return;
    }
    int distanceToClosest = this.getDistanceTo(closestOpp);
    // find  a closest Ally planet
    for (Factory factory : GameState.factories) {
      if (factory == this) continue; // don't check ourself
      
      if (factory.owner == this.owner && factory.getDistanceTo(closestOpp) < distanceToClosest) {
        // check that we are not on the other 'side' of the oppFactory
        if (this.getDistanceTo(factory) < factory.getDistanceTo(closestOpp)) {
          isFront = false;
          return;
        }
      }
    }
  }
  
  public Factory getClosestEnemyFactory() {
    Factory closest = null;
    int minDistance = 1_000;
    Owner enemy = owner.getEnemy();
    for (Factory factory : GameState.factories) {
      if (factory.owner != enemy) continue;
      
      int distance = factory.getDistanceTo(this);
      if (closest == null || distance < minDistance) {
        closest = factory;
        minDistance = distance;
      }
    }
    return closest;
  }
  
  public Factory getClosestAlliedFront() {
    int minDistance = 1_000_000;
    Factory closestFactory = null;
    
    for (Factory factory : GameState.factories) {
      if (factory.owner != this.owner) continue;
      if (!factory.isFront) continue;
      
      int distance = factory.getDistanceTo(this);
      if (distance < minDistance) {
        minDistance = distance;
        closestFactory = factory;
      }
    }
    return closestFactory;
  }
}
