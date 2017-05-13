package c4l.entities;

import c4l.GameState;

public class Sample {
  public static int ENTITY_COUNT = 0;

  public MoleculeType expertise;
  public int health;
  public int[] costs;
  public int totalCost = 0;
  public int id;

  int rank;
  PlayerData discoveredBy;

  public Sample(int[] cost, int life, MoleculeType gain) {
    this.expertise = gain;
    this.health = life;
    this.costs = cost;
    
    totalCost = 0;
    for (int i=0;i<GameState.MOLECULE_TYPE;i++) {
      totalCost += costs[i];
    }
  }

  public boolean isDiscovered() {
    return health != -1;

  }

  public Sample clone() {
    return new Sample(costs, health, expertise);
  }

  public String getGainChar() {
    return (expertise == null) ? "0" : expertise.name();
  }

  public void debug() {
    System.err.print("Sample : "+id+" , costs : ");
    for (int i=0;i<GameState.MOLECULE_TYPE;i++) {
      System.err.print(" "+costs[i]);
    }
    System.err.println(" h: "+health+" r:"+rank);
  }

  public int totalNeededMolecules() {
    return totalCost;
  }

  public double score(Robot me, GameState state) {
    int needed = 0;
    for (int i=0;i<GameState.MOLECULE_TYPE;i++) {
      needed += Math.max(0, costs[i] - me.storage[i] - me.expertise[i]);
    }
    return 1.0 * health / needed;
  }
}