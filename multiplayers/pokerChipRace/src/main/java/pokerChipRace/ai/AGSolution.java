package pokerChipRace.ai;

import pokerChipRace.GameState;
import pokerChipRace.Player;
import pokerChipRace.entities.Entity;
import trigonometry.Vector;

public class AGSolution {
  public static final int DEPTH = 5;
  public static double patience[] = new double[DEPTH];
  static {
    for (int i = 0; i < DEPTH; i++) {
      patience[i] = Math.pow(0.6, i);
    }
  }

  double angles[] = new double[6]; // <0 == WAIT, else [0;2*PI]
  private int chipCount;

  public Feature features[] = new Feature[DEPTH];
  public static FeatureWeight weights = new FeatureWeight();

  public double energy;
  
  public AGSolution(int chipCount) {
    this.chipCount = chipCount;
    for (int i=0;i<DEPTH;i++) {
      features[i] = new Feature(); // TODO use a cache
    }
  }

  public void randomize() {
    for (int i = 0; i < chipCount; i++) {
      angles[i] = getRandomGene();
    }
  }

  private double getRandomGene() {
    if (Player.rand.nextDouble() < 0.8) {
      return -1; // WAIT
    } else {
      return 2 * Math.PI * Player.rand.nextDouble(); // real angle between 0 and
                                                     // 2PI
    }
  }

  public void crossOver(AGSolution parent1, AGSolution parent2) {
    for (int i = 0; i < chipCount; i++) {
      if (Player.rand.nextBoolean()) {
        angles[i] = parent1.angles[i];
      } else {
        angles[i] = parent2.angles[i];
      }
    }
  }

  public void mutate() {
    for (int i = 0; i < chipCount; i++) {
      if (Player.rand.nextInt(100) < 10) {
        angles[i] = getRandomGene();
      }
    }
  }

  public Vector angleToDir(int index) {
    if (angles[index] < 0)
      return null;

    return new Vector(10 * Math.cos(angles[index]), 10 * Math.sin(angles[index]));
  }

  public void applyActions(GameState state, int turn) {
    for (int index = 0; index < chipCount; index++) {
      Entity entity = state.myChips.elements[index];

      if (turn != 0) {
        entity.targetx = -100;
        entity.targety = -100;
      } else {
        Vector dir = angleToDir(index);
        if (dir == null) {
          entity.targetx = -100;
          entity.targety = -100;
        } else {
          entity.targetx = entity.x + dir.vx;
          entity.targety = entity.y + dir.vy;
        }
      }
    }
  }

  public void calculateIntermediateEnergy(GameState state, int turn) {
    features[turn].calculateIntermadiaryFeatures(state);
  }

  public void calculateFinalEnergy(GameState state) {
    energy = 0.0;
    for (int i=0;i<DEPTH;i++) {
      energy += patience[i]*features[i].applyWeights(weights);
    }
  }

  public void init() {
    energy = 0;
  }

  public void debug() {
    System.err.println("Energy is " + energy);

    for (int i=0;i<DEPTH;i++) {
      System.err.println("* turn "+i);
      features[i].debugFeature(weights);
    }
    
    for (int i = 0; i < chipCount; i++) {
      System.err.print("i: ");
      System.err.print("" + angles[i] + ", ");
      System.err.println();
    }

  }
}
