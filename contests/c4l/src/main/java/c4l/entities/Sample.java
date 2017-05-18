package c4l.entities;

import java.util.Arrays;
import java.util.Comparator;

import c4l.GameState;
import c4l.Order;

public class Sample {

  public static int ENTITY_COUNT = 0;
  
  public MoleculeType expertise;
  public int health;
  public int[] costs;
  public int fullCost = 0;
  public int id;

  int rank;
  PlayerData discoveredBy;

  public int[] gain = new int[GameState.MOLECULE_TYPE];

  public Sample(int id, int[] cost, int life, MoleculeType moleculeGained) {
    this.id = id;
    this.expertise = moleculeGained;
    this.health = life;
    this.costs = cost;
    
    fullCost = 0;
    for (int i=0;i<GameState.MOLECULE_TYPE;i++) {
      fullCost += costs[i];
    }
    if (isDiscovered()) {
      gain[moleculeGained.index] = 1;
    }
  }

  public boolean isDiscovered() {
    return health != -1;

  }

  public Sample clone() {
    return new Sample(id, costs, health, expertise);
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
    System.err.print(""+health+");");
    for (int i=0;i<GameState.MOLECULE_TYPE;i++) {
      if (gain[i] > 0) {
        System.err.print(""+i+",");
      }
    }
    System.err.println(");");
  }

  /**
   * return how many molecules are needed with 0 xp & 0 storage
   * @return
   */
  public int fullCost() {
    return fullCost;
  }

  
  /**
   * Return how many molecules are needed to complete the sample 
   * @param me
   * @return
   */
  public int relativeCost(Robot me) {
    return Arrays.stream(relativeCostByMolecule(me)).sum();
  }
  
  public int[] relativeCostByMolecule(Robot me) {
    int[] result = new int[GameState.MOLECULE_TYPE];
    for (int i=0;i<GameState.MOLECULE_TYPE;i++) {
      result[i]= Math.max(0, costs[i]-me.storage[i]-me.expertise[i]);
    }
    return result;
  }
  
  /*
   * compare on ROI (return on investissment)
   */
  public static Comparator<Sample> roiSorter(Order order) {
    return new Comparator<Sample>() {
      @Override
      public int compare(Sample o1, Sample o2) {
        return (order == Order.DESC ? -1 : 1) * Double.compare(o1.roi(), o2.roi());
      }
    };
  }
  /**
   * return the 'ROI' of a sample
   * -> health / needed molecules
   * -> expertise given
   * 
   */
  public double roi() {
    return health / fullCost;
  }

  /**
   * Sort by EXTRA (cost- (me.sortage + me.xp)) molecules needed to complete the Sample
   */
  public static Comparator<? super Sample> moleculeNeededSorter(final Robot me, Order order) {
    return new Comparator<Sample>() {
      @Override
      public int compare(Sample o1, Sample o2) {
        return (order == Order.DESC ? -1 : 1) * Integer.compare(o1.relativeCost(me), o2.relativeCost(me));
      }
    };
  }

  /**
   * return the real points won by this sample (ie : health + science project)
   */
  public static Comparator<? super Sample> pointsWonSorter(GameState state, Robot me, Order order) {
    return new Comparator<Sample>() {
      @Override
      public int compare(Sample o1, Sample o2) {
        int score1 = o1.health + (state.distanceToScienceProjects(me, o1.gain) == 0 ? 50 : 0);
        int score2 = o2.health + (state.distanceToScienceProjects(me, o2.gain) == 0 ? 50 : 0);
        return (order == Order.DESC ? -1 : 1) * Integer.compare(score1, score2);
      }
    };
  }

  /**
   * sorter for only health (direct points)
   */
  public static Comparator<? super Sample> healthSorter(GameState state, Robot me, Order order) {
    return new Comparator<Sample>() {
      @Override
      public int compare(Sample o1, Sample o2) {
        return (order == Order.DESC ? -1 : 1) * Integer.compare(o1.health, o2.health);
      }
    };
  }

  @Override
  public String toString() {
    StringBuffer output = new StringBuffer();
    output.append("createSample("+id+", new int[]{");
    for (int i=0;i<GameState.MOLECULE_TYPE;i++) {
      output.append(""+costs[i]);
      if (i != GameState.MOLECULE_TYPE-1) {
        output.append(", ");
      } else {
        output.append("}, ");
      }
    }
    output.append(""+health+", ");
    for (int i=0;i<GameState.MOLECULE_TYPE;i++) {
      if (gain[i] > 0) {
        output.append(""+i+",");
      }
    }
    output.append(");");
    return output.toString();
 }
}