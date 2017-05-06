package pokerChipRace.ai;

import pokerChipRace.GameState;
import pokerChipRace.Player;
import pokerChipRace.entities.Entity;
import trigonometry.Vector;

public class AGSolution {
  public static final int DEPTH = 5;
  public static double patience[] = new double[DEPTH];
  static {
    for (int i=0;i<DEPTH;i++) {
      patience[i] = Math.pow(0.5, (i+1));
    }
  }
  
  double angles[] = new double[6]; // <0 == WAIT, else [0;2*PI]
  private int chipCount;
  public double energy;
  
  public AGSolution(int chipCount) {
    this.chipCount = chipCount;
  }
  
  public void randomize() {
    for (int i=0;i<chipCount;i++) {
      angles[i] = getRandomGene();
    }
  }
  private double getRandomGene() {
    if (Player.rand.nextDouble() < 0.3) {
      return -1; // WAIT
    } else {
      return 2*Math.PI * Player.rand.nextDouble(); // real angle between 0 and 2PI
    }
  }

  public void crossOver(AGSolution parent1, AGSolution parent2) {
    for (int i=0;i<chipCount;i++) {
      if (Player.rand.nextBoolean()) {
        angles[i] = parent1.angles[i];
      } else {
        angles[i] = parent2.angles[i];
      }
    }
  }
  
  public void mutate() {
    for (int i=0;i<chipCount;i++) {
      if (Player.rand.nextInt(100) < 10) {
        angles[i] = getRandomGene();
      }
    }
  }
  
  public Vector angleToDir(int turn, int index) {
    if (turn !=0) return null;
    double angle = angles[index];
    if (angle <0) return null;
    
    return new Vector(10 * Math.cos(angle), 10 * Math.sin(angle));
  }

  public void applyActions(GameState state, int turn) {
    for (int index=0;index<chipCount;index++) {
      Entity entity = state.myChips.elements[index];

      Vector dir = angleToDir(turn, index);
      if (dir == null || turn != 0) {
        entity.targetx = -100;
        entity.targety = -100;
      } else {
        entity.targetx = entity.x + dir.vx;
        entity.targety = entity.y + dir.vy;
      }
    }
  }

  public void calculateSubEnergy(GameState state, int turn) {
    for (int index=0;index<state.entityFE;index++) {
      Entity entity = state.chips[index];
      if (entity.isDead()) continue;
      if (entity.owner == -1) break;
      
      if (entity.owner == state.myId) {
        energy += patience[turn] * entity.radius;
      } else {
        energy -= (patience[turn] * entity.radius) / (state.playerCount-1);
      }
    }
  }

  public void calculateFinalEnergy(GameState state) {
    for (int index=0;index<state.entityFE;index++) {
      Entity entity = state.chips[index];
      if (entity.isDead()) continue;
      if (entity.owner == -1) break;
      
      if (entity.owner == state.myId) {
        //energy += entity.radius;
        
        // check distance to biggest entity
//        double good = 0;
//        double bad = 0;
//        int count = 0;
//        for (int otherI=0;otherI<state.entityFE;otherI++) {
//          Entity other = state.chips[otherI];
//          if (other.isDead()) continue;
//          if (other.owner == state.myId) continue;
//          
//          double dist2 = (entity.x-other.x)*(entity.x-other.x) + (entity.y-other.y)*(entity.y-other.y);
//          if (other.radius > entity.radius) {
//            bad += dist2;
//          } else {
//            good += dist2;
//          }
//          count++;
//        }
//        if (count != 0) {
//          energy += 0.00001*good / count;
//          energy -= 0.00001*bad / count;
//        }
      } else {
        //energy -= entity.radius;
      }
    }
  }

  public void init() {
    energy = 0;
  }

  public void debug() {
    System.err.println("Energy is "+energy);

    for (int i=0;i<chipCount;i++) {
      System.err.print("i: ");
      System.err.print(""+angles[i]+", "); 
      System.err.println();
    }
    
  }
}
