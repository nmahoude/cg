package pokerChipRace.ai;

import pokerChipRace.GameState;
import pokerChipRace.Player;
import pokerChipRace.entities.Entity;
import trigonometry.Vector;

public class AGSolution {
  public static final int DEPTH = 7;
  public static double patience[] = new double[DEPTH];
  static {
    for (int i = 0; i < DEPTH; i++) {
      patience[i] = Math.pow(0.6, i);
    }
  }

  double angles[] = new double[6*DEPTH]; // <0 == WAIT, else [0;2*PI]
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

  public void clear() {
    energy = 0;
    for (int i=0;i<DEPTH;i++) {
      features[i].clear();
    }
  }

  public void copy(AGSolution copy) {
    clear();
    this.energy = copy.energy;
    this.chipCount = copy.chipCount;
    
    for (int i = 0; i < angles.length; i++) {
      this.angles[i] = copy.angles[i];
    }
    for (int i = 0; i< DEPTH;i++) {
      features[i].copy(copy.features[i]);
    }
  }
  
  public void randomize() {
    for (int i = 0; i < chipCount; i++) {
      for (int d=0;d<DEPTH;d++) {
        angles[6*i+d] = getRandomGene();
      }
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
        for (int d=0;d<DEPTH;d++) {
          angles[6*i+d] = parent1.angles[6*i+d];
        }
      } else {
        for (int d=0;d<DEPTH;d++) {
          angles[6*i+d] = parent2.angles[6*i+d];
        }
      }
    }
  }

  public void mutate() {
    for (int i = 0; i < chipCount; i++) {
      for (int d=0;d<DEPTH;d++) {
        if (Player.rand.nextInt(100) < 50) {
          double decal = Player.rand.nextDouble(-0.1, 0.1);
          angles[6*i+d] += decal;
          if (angles[6*i+d] <0) angles[6*i+d] += 6.28 ;
        }
      }
    }
  }

  public Vector angleToDir(int turn, int index) {
    if (angles[6*index+turn] < 0)
      return null;

    return new Vector(10 * Math.cos(angles[6*index+turn]), 10 * Math.sin(angles[6*index+turn]));
  }

  public void applyActions(GameState state, int turn) {
    for (int index = 0; index < chipCount; index++) {
      Entity entity = state.myChips.elements[index];

      Vector dir = angleToDir(turn,index);
      if (dir == null) {
        entity.targetx = -100;
        entity.targety = -100;
      } else {
        entity.targetx = entity.x + dir.vx;
        entity.targety = entity.y + dir.vy;
      }
    }
  }

  public void calculateIntermediateEnergy(GameState state, int turn) {
    features[turn].calculateIntermadiaryFeatures(state);
  }

  public void calculateFinalEnergy(GameState state) {
    energy = 0.0;
    // check for finish state
    if (!isFinishedState(state)) {
      for (int i=0;i<DEPTH;i++) {
        energy += patience[i]*features[i].applyWeights(weights);
      }
    }
  }

  private boolean isFinishedState(GameState state) {
    boolean playerHasChips[] = new boolean[4];
    for (int i=0;i<state.entityFE;i++) {
      Entity entity = state.chips[i];
      if (entity.owner == -1) break;
      if (entity.isDead()) continue;
      playerHasChips[entity.owner] = true;
    }
    
    int playerWithChips = 0;
    for (int i=0;i<4;i++) {
      if (playerHasChips[i]) playerWithChips++;
    }
    if (playerWithChips == 1) {
      if ((playerHasChips[state.myId])) {
        energy = Double.POSITIVE_INFINITY;
      } else {
        energy = Double.NEGATIVE_INFINITY;
      }
      return true;
    }
    return false;
  }

  public void debug() {
    System.err.println("Energy = " + energy);

    for (int i=0;i<DEPTH;i++) {
      System.err.println("* turn "+i);
      features[i].debugFeature(weights);
    }
    
    for (int i = 0; i < chipCount; i++) {
      System.err.print("for "+i+ " angles= ");
      for (int d=0;d<DEPTH;d++) {
        System.err.printf("%f ,", angles[6*i+d]);
      }
      System.err.println();
    }

  }


}
