package c4l.molecule;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import c4l.GameState;
import c4l.entities.MoleculeType;

public class MoleculeOptimizerNode {
  public static final int STORAGE = 3;
  public static final int EXPERTISE = 4;
  public static final int AVAILABLE = 5;
  public static final int SUM_NOT_NULL = 6;
  public static final int LAST = 7;
  public static final int WIDTH = GameState.MOLECULE_TYPE+1;
  public static final int HEALTH = GameState.MOLECULE_TYPE; 
  
  private static Map<Integer, Double> memoization = new HashMap<>();
  private static int memoDecal[] = new int[] { 1, 8, 64, 512, 4096, 32768 };
  
  
  public int values[] = new int[WIDTH*LAST];
  public int freeStorage;
  
  public MoleculeType pickedMolecule;
  public List<MoleculeOptimizerNode> children = new ArrayList<>();
  public double score;

  public void start() {
    memoization.clear();
    
    if (freeStorage == 0) {
      return;
    }
    updateSumNotNull();
    
    for (int i=0;i<GameState.MOLECULE_TYPE;i++) {
      if (values[WIDTH*AVAILABLE+i]>0 && values[WIDTH*SUM_NOT_NULL+i] > 0) {
        MoleculeOptimizerNode node = new MoleculeOptimizerNode();
        score = Math.max(score, node.applyTurn(this, MoleculeType.values()[i]));
        children.add(node);
      }
    }
  }

  public Integer encodeState() {
    int total = 0;
    for (int i=0;i<GameState.MOLECULE_TYPE;i++) {
      total += memoDecal[i] * values[WIDTH*AVAILABLE + i];
    }
    return new Integer(total);
  }
  /**
   * row indicating how many sample need this molecule
   */
  private void updateSumNotNull() {
    for (int i=0;i<GameState.MOLECULE_TYPE;i++) {
      values[SUM_NOT_NULL*WIDTH+i] = values[0*WIDTH+i] > 0 ? 1 : 0
                                    + values[1*WIDTH+i] > 0 ? 1 : 0
                                    + values[2*WIDTH+i] > 0 ? 1 : 0;
          ;
    }
  }

  public double applyTurn(MoleculeOptimizerNode from, MoleculeType type) {
    pickedMolecule = type;
    System.arraycopy(from.values, 0, values, 0, values.length);
    
    values[WIDTH*STORAGE + pickedMolecule.index]++;
    values[WIDTH*AVAILABLE + pickedMolecule.index]--;
    freeStorage = from.freeStorage-1;
    
    Integer key = encodeState();
    Double value = memoization.get(key);
    if (value != null) {
      return value.doubleValue();
    }
    
    score = 0;
    if (freeStorage == 0) {
      double endScore = getScore();
      memoization.put(key, endScore);
      return endScore;
    } else {
      for (int i=0;i<GameState.MOLECULE_TYPE;i++) {
        if (values[WIDTH*AVAILABLE + i]>0 && values[WIDTH*SUM_NOT_NULL+i] > 0) {
          MoleculeOptimizerNode node = new MoleculeOptimizerNode();
          score += node.applyTurn(this, MoleculeType.values()[i]);
          children.add(node);
        }
      }
    }
    memoization.put(key, score);
    return score;
  }

  /** 
   * score the state
   * @return
   */
  private double getScore() {
    int possibilities[][] = new int[][] {
        {0, 1, 2}, {0, 2, 1}, 
        {1, 0, 2}, {1, 2, 0}, 
        {2, 0, 1}, {2, 1, 0}};
    
    double score = 0;
    for (int i=0;i<possibilities.length;i++) {
      score = Math.max(score, getScoreForOnePossibility(possibilities[i]));
    }
    return score;
  }

  private double getScoreForOnePossibility(int[] order) {
    int[] localStorage = new int[GameState.MOLECULE_TYPE];
    System.arraycopy(values, WIDTH*STORAGE, localStorage, 0, GameState.MOLECULE_TYPE);
    
    double score = 0;
    for (int i=0;i<order.length;i++) {
      for (int j=0;j<GameState.MOLECULE_TYPE;j++) {
        if (values[WIDTH*order[i]+j] > localStorage[j]) {
          return score;
        } else {
          localStorage[j] -= values[WIDTH*order[i]+j];
        }
      }
      score += values[WIDTH*order[i]+HEALTH];
    }
    return score;// all the three samples are filled here, pretty good :)
  }
  
  public void createStorage(int[] storage) {
    freeStorage = 10;
    for (int i=0;i<5;i++) {
      this.values[STORAGE*WIDTH + i] = storage[i];
      freeStorage-=storage[i];
    }    
  }
  
  public void createExpertise(int[] xp) {
    for (int i=0;i<5;i++) {
      this.values[EXPERTISE*WIDTH + i] = xp[i];
    }    
  }

  public void createAvailable(int[] available) {
    for (int i=0;i<5;i++) {
      this.values[AVAILABLE*WIDTH + i] = available[i];
    }    
  }

  public void createSample(int index, int[] costs, int health) {
    for (int i=0;i<5;i++) {
      this.values[index*WIDTH + i] = costs[i];
    }
    this.values[index*WIDTH + HEALTH] = health;
  }

  public MoleculeOptimizerNode getBestChild() {
    double bestScore = Double.NEGATIVE_INFINITY;
    MoleculeOptimizerNode best = null;
    for (MoleculeOptimizerNode node : children) {
      if (node.score  > bestScore) {
        bestScore = node.score;
        best = node;
      }
    }
    return best;
  }

}
