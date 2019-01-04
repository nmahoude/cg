package marsLander.ai;

import marsLander.Player;

public class AGSolution {
  public static final int DEPTH = 100;
  public int values[][] = new int[DEPTH][2]; // -15, +15 // -1, 0, +1
  public double score;
  public int fuel = 0;

  public void randomize() {
    score = 0.0;
    for (int i=0;i<DEPTH;i++) {
      randomAtDepth(i);
    }
  }

  private void randomAtDepth(int d) {
    double rand = Player.rand.nextDouble();
    if (rand < 0.2) {
      values[d][0] = 0;
    } else {
      values[d][0] = (int)(Player.rand.nextInt(30)-15);
    }
    
    rand = Player.rand.nextDouble();
    if (rand < 0.2) {
      values[d][1] = 0;
    } else {
      values[d][1] = (int)(Player.rand.nextInt(3)-1);
    }
  }

  public void crossover(AGSolution parent1, AGSolution parent2) {
    score = 0.0;
    
    for (int i=0;i<DEPTH;i++) {
      if (Player.rand.nextDouble() > 0.95) {
        randomAtDepth(i);
      } else {
        double ratio = 1.0 + (1.0 * Player.rand.nextDouble() - 0.5);
        double BmoinsA = 0.5 * (parent2.values[i][0] - parent1.values[i][0]);
        values[i][0] = (int)(parent1.values[i][0] + ratio * BmoinsA);
        
        ratio = Player.rand.nextDouble();
        if (ratio > 0.9) {
          values[i][1] = 1;
        } else if (ratio > 0.9 / 2) {
          values[i][1] = parent1.values[i][1];
        } else {
          values[i][1] = parent2.values[i][1];
        }
      }
    }
  }

  public void crossover2(AGSolution parent1, AGSolution parent2) {
    score = 0.0;
    
    for (int i=0;i<DEPTH;i++) {
      if (Player.rand.nextDouble() > 0.95) {
        randomAtDepth(i);
      } else {
        double ratio = 1.0 + (1.0 * Player.rand.nextDouble() - 0.5);
        double BmoinsA = 0.5 * (parent2.values[i][0] - parent1.values[i][0]);
        values[i][0] = (int)(parent1.values[i][0] + ratio * BmoinsA);
        
        ratio = Player.rand.nextDouble();
        if (ratio > 0.9) {
          values[i][1] = 1;
        } else if (ratio > 0.9 / 2) {
          values[i][1] = parent1.values[i][1];
        } else {
          values[i][1] = parent2.values[i][1];
        }
      }
    }
  }

  
  public void copyWithdecal(AGSolution parent) {
    for (int i=0;i<DEPTH-1;i++) {
      values[i][0] = parent.values[i+1][0];
      values[i][1] = parent.values[i+1][1];
    }    
  }

  public void copyFrom(AGSolution model) {
    for (int i=0;i<DEPTH;i++) {
      values[i][0] = model.values[i][0];
      values[i][1] = model.values[i][1];
    }    
  }
  
}
