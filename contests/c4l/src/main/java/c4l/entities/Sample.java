package c4l.entities;

import java.util.Comparator;

import c4l.GameState;

public class Sample {
  public static final Comparator<Sample> orderByHealthDecr = new Comparator<Sample>() {
      @Override
      public int compare(Sample o1, Sample o2) {
        return Integer.compare(o2.health, o1.health);
      }
    };

  public static int ENTITY_COUNT = 0;

  public MoleculeType expertise;
  public int health;
  public int[] costs;
  public int totalCost = 0;
  public int id;

  int rank;
  PlayerData discoveredBy;

  public int[] gain = new int[GameState.MOLECULE_TYPE];

  public Sample(int[] cost, int life, MoleculeType moleculeGained) {
    this.expertise = moleculeGained;
    this.health = life;
    this.costs = cost;
    
    totalCost = 0;
    for (int i=0;i<GameState.MOLECULE_TYPE;i++) {
      totalCost += costs[i];
    }
    if (isDiscovered()) {
      gain[moleculeGained.index] = 1;
    }
  }

  public boolean isDiscovered() {
    return health != -1;

  }

  public Sample clone() {
    return new Sample(costs, health, expertise);
  }

  public void debug() {
    System.err.print("createSample("+id+", new int[]{");
    for (int i=0;i<GameState.MOLECULE_TYPE;i++) {
      System.err.print(""+costs[i]);
      if (i != GameState.MOLECULE_TYPE-1) {
        System.err.print(", ");
      } else {
        System.err.print("},");
      }
    }
    System.err.println(""+health+");");
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

  public static Comparator<Sample> roiASC() {
    return new Comparator<Sample>() {
      @Override
      public int compare(Sample o1, Sample o2) {
        return Double.compare(o1.roi(), o2.roi());
      }
    };
  }
  public static Comparator<? super Sample> roiDESC() {
    return new Comparator<Sample>() {
      @Override
      public int compare(Sample o1, Sample o2) {
        return Double.compare(o2.roi(), o1.roi());
      }
    };
  }

  /**
   * return the 'ROI' of a sample
   * -> health / needed molecules
   * -> expertise given
   * 
   * @return
   */
  public double roi() {
    return health / totalCost;
  }

  public static Comparator<? super Sample> moleculeNeededASC(final Robot me) {
    return new Comparator<Sample>() {
      @Override
      public int compare(Sample o1, Sample o2) {
        return Integer.compare(o1.neededMoleculesFor(me), o2.neededMoleculesFor(me));
      }
    };
  }

  protected int neededMoleculesFor(Robot me) {
    int total = 0;
    for (int i=0;i<GameState.MOLECULE_TYPE;i++) {
      total += Math.max(0, costs[i]-me.storage[i]-me.expertise[i]);
    }
    
    return total;
  }
}