package c4l.molecule;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import c4l.GameState;
import c4l.entities.Module;
import c4l.entities.MoleculeType;
import c4l.entities.Robot;

public class MoleculeOptimizerNode {
  private static final int SCORE_WHERE_WIN = 100_000;
  public static final int PICKED_MOLECULES = 3;
  public static final int EXPERTISE = 4;
  public static final int AVAILABLE = 5;
  public static final int INITIAL_STORAGE = 6;
  public static final int LAST = 7;
  
  public static final int WIDTH = GameState.MOLECULE_TYPE+2; // +1 = health, +1 xp
  public static final int HEALTH = GameState.MOLECULE_TYPE; 
  public static final int XPGAIN = GameState.MOLECULE_TYPE+1; 
  
  
  public static Map<Integer, MoleculeComboInfo> memoization = new HashMap<>();
  private static int memoDecal[] = new int[] { 10000, 01000, 00100, 00010, 00001 };
  private static double patience[] = new double[10];
  static {
    for (int i=0;i<patience.length;i++) {
      patience[i] = Math.pow(0.8, i);
    }
  }
  
  public int values[] = new int[WIDTH*LAST];
  public int freeStorage;
  
  public MoleculeType pickedMolecule;
  public List<MoleculeOptimizerNode> children = new ArrayList<>();
  public double score;
  
  public MoleculeComboInfo combo;
  public int ply;

  public MoleculeOptimizerNode() {
    for (int i=0;i<3*WIDTH;i++) {
      values[i] = 99;// all costs at 99 , so we cannot fullfill absent samples
    }
  }
  
  @Override
  public String toString() {
    String output="";
    for (int j=0;j<3;j++) {
      output+="createSample( [";
      for (int i=0;i<GameState.MOLECULE_TYPE;i++) {
        output+=""+values[WIDTH*j+i]+",";
      }
      output+="]);\n\r";
    }
    output+="picked([";
    for (int i=0;i<GameState.MOLECULE_TYPE;i++) {
      output+=""+values[WIDTH*PICKED_MOLECULES+i]+",";
    }
    output+="]);\n\r";
    output+="expertise([";
    for (int i=0;i<GameState.MOLECULE_TYPE;i++) {
      output+=""+values[WIDTH*EXPERTISE+i]+",";
    }
    output+="]);\n\r";
    output+="available([";
    for (int i=0;i<GameState.MOLECULE_TYPE;i++) {
      output+=""+values[WIDTH*AVAILABLE+i]+",";
    }
    output+="]);\n\r";
    
    return output;
  }
  public void start (Robot me) {
    memoization.clear();
    
    combo = getLocalCombo();
    score = combo.score;
    if (ply + Module.distance(me.target, Module.LABORATORY) + combo.infos.size() > 200) {
      score = -1;
      combo = new MoleculeComboInfo();
    }

    if (freeStorage == 0) {
      return;
    }
    
    for (int i=0;i<GameState.MOLECULE_TYPE;i++) {
      if (values[WIDTH*AVAILABLE+i]>0) {
        MoleculeOptimizerNode node = new MoleculeOptimizerNode();
        MoleculeComboInfo info = node.applyTurn(me, 0, this, MoleculeType.values()[i]);
        if (info.score > score) {
          score = info.score;
          combo = info;
        }
        children.add(node);
      }
    }
  }

  public Integer encodeState() {
    int total = 0;
    for (int i=0;i<GameState.MOLECULE_TYPE;i++) {
      total += memoDecal[i] * values[WIDTH*PICKED_MOLECULES+ i];
    }
    return new Integer(total);
  }
  /**
   * row indicating how many sample need this molecule
   */
  private int[] updateSumNotNull() {
    int sumNotNull[] = new int[GameState.MOLECULE_TYPE];
    for (int i=0;i<GameState.MOLECULE_TYPE;i++) {
      sumNotNull[i] = values[0*WIDTH+i] > 0 ? 1 : 0
                                    + values[1*WIDTH+i] > 0 ? 1 : 0
                                    + values[2*WIDTH+i] > 0 ? 1 : 0;
          ;
    }
    return sumNotNull;
  }

  public MoleculeComboInfo applyTurn(Robot me, int depth, MoleculeOptimizerNode from, MoleculeType type) {
    ply = from.ply+1;
    
    pickedMolecule = type;
    System.arraycopy(from.values, 0, values, 0, values.length);
    
    values[WIDTH*PICKED_MOLECULES + pickedMolecule.index]++;
    values[WIDTH*AVAILABLE + pickedMolecule.index]--;
    freeStorage = from.freeStorage-1;
    
    Integer key = encodeState();
    MoleculeComboInfo value = memoization.get(key);
    if (value != null) {
      combo = value;
      return value;
    }
    
    MoleculeComboInfo best = getLocalCombo();
    score = patience[depth] * best.score;
    if (ply + Module.distance(me.target, Module.LABORATORY) + best.infos.size() > 200) {
      score = -1;
      best = new MoleculeComboInfo();
    }
    
    if (freeStorage == 0 || score < 0) {
      memoization.put(key, best);
      combo = best;
      return best;
    } else {
      for (int i=0;i<GameState.MOLECULE_TYPE;i++) {
        if (values[WIDTH*AVAILABLE + i]>0 ) {
          MoleculeOptimizerNode node = new MoleculeOptimizerNode();
          MoleculeComboInfo info = node.applyTurn(me, depth+1, this, MoleculeType.values()[i]);
          if (info.score > score) {
            score = info.score;
            best = info;
          }
          children.add(node);
        }
      }
    }
    memoization.put(key, best);
    combo = best;
    return best;
  }

  double getPercentCompletion() {
    int total = 0;
    int filled = 0;
    for (int  i=0;i<3;i++) {
      for (int j=0;j<GameState.MOLECULE_TYPE;j++) {
        total += values[WIDTH*i+j];
        filled += Math.min(values[WIDTH*i+j], 
                  (values[WIDTH*EXPERTISE+j] + values[WIDTH*INITIAL_STORAGE+ j]+values[WIDTH*PICKED_MOLECULES + j]));
      }
    }
    return 1.0 * filled / total;
  }

  /** 
   * get the best score of the current state
   * by trying every combination
   * @return
   */
  public MoleculeComboInfo getLocalCombo() {
    int possibilities[][] = new int[][] {
        {0, 1, 2}, {0, 2, 1}, 
        {1, 0, 2}, {1, 2, 0}, 
        {2, 0, 1}, {2, 1, 0}};
    
    double bestScore = Double.NEGATIVE_INFINITY;
    MoleculeComboInfo best = null;
    for (int i=0;i<possibilities.length;i++) {
      MoleculeComboInfo info = getScoreForOnePossibility(possibilities[i]);
      if (info.score > bestScore) {
        bestScore = info.score;
        best = info;
      }
    }
    return best;
  }

  private MoleculeComboInfo getScoreForOnePossibility(int[] order) {
    MoleculeComboInfo info = new MoleculeComboInfo();
    
    int[] initialStorage = new int[GameState.MOLECULE_TYPE];
    int[] pickedMolecules = new int[GameState.MOLECULE_TYPE];
    int[] xp = new int[GameState.MOLECULE_TYPE];
    
    System.arraycopy(values, WIDTH*INITIAL_STORAGE, initialStorage, 0, GameState.MOLECULE_TYPE);
    System.arraycopy(values, WIDTH*PICKED_MOLECULES, pickedMolecules, 0, GameState.MOLECULE_TYPE);
    System.arraycopy(values, WIDTH*EXPERTISE, xp, 0, GameState.MOLECULE_TYPE);
    
    for (int i=0;i<order.length;i++) {
      MoleculeInfo moleculeInfo = new MoleculeInfo();
      moleculeInfo.sampleIndex = order[i];
      moleculeInfo.health = values[order[i]*WIDTH + HEALTH];
      for (int j=0;j<GameState.MOLECULE_TYPE;j++) {
        int needed = Math.max(0, values[WIDTH*order[i]+j]/*cost*/ - xp[j]/*xp*/);
        if ( needed > initialStorage[j]+pickedMolecules[j]) {
          return info; // stop here we can't do the samples
        } else {
          if (initialStorage[j] >= needed) {
            initialStorage[j]-=needed;
            needed = 0;
          } else {
            // need to get some picked
            needed-= initialStorage[j];
            initialStorage[j] = 0;

            moleculeInfo.moleculesNeeded[j] = needed;
            pickedMolecules[j]-=needed;
            needed = 0;
          }
        }
      }
      // here, the sample is done
      info.addComplete(moleculeInfo);
      info.score += values[WIDTH*order[i]+HEALTH];
      int gainIndex = this.values[WIDTH*order[i]+XPGAIN];
      // TODO this test is only for tests as I want to simulate without gain, that is not good
      if (gainIndex != -1 ) {
        xp[gainIndex]++; // add expertise !
      }
    }
    
    return info;// all the three samples are filled here, pretty good :)
  }
  
  public void createStorage(int[] storage) {
    freeStorage = 10;
    for (int i=0;i<GameState.MOLECULE_TYPE;i++) {
      this.values[PICKED_MOLECULES*WIDTH + i] = 0;
      this.values[INITIAL_STORAGE*WIDTH + i] = storage[i];
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

  public void createSample(int index, int[] costs, int health, int xpIndex) {
    for (int i=0;i<5;i++) {
      this.values[index*WIDTH + i] = costs[i];
    }
    this.values[index*WIDTH + HEALTH] = health;
    this.values[index*WIDTH + XPGAIN]  = xpIndex;
  }

  public MoleculeComboInfo getBestChild() {
    return combo;
  }

}
