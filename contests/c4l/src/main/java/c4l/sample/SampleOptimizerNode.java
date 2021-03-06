package c4l.sample;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import c4l.GameState;
import c4l.entities.Robot;
import c4l.entities.Sample;
import c4l.molecule.MoleculeComboInfo;
import c4l.molecule.MoleculeOptimizerNode;

public class SampleOptimizerNode {

  private static GameState state;
  private static Robot me;
  
  public static Map<Integer, SampleOptimizerNode> memoization = new HashMap<>();
  public static List<Sample> samplesPool =null;
  
  private static int memoDecal[] = new int[] { 1_000_000, 1_000, 1 }; // 999 samples in cloud MAX
  public List<Sample> currentSamples = new ArrayList<>();
  
  public List<SampleOptimizerNode> children = new ArrayList<>();
  public double score;
  public SampleOptimizerNode bestChild;
  private static MoleculeOptimizerNode node;


  
  public SampleOptimizerNode() {
  }
  
  public void start (GameState state, Robot me) {
    SampleOptimizerNode.state = state;
    SampleOptimizerNode.me = me;
    
    // prepare the molecule optimizer to calculate score 
    node = new MoleculeOptimizerNode();
    node.createStorage(me.storage);
    node.createExpertise(me.expertise);
    node.createAvailable(state.availables);
    node.ply = state.ply;
    
    
    samplesPool = new ArrayList<>();
    samplesPool.addAll(state.availableSamples);
    currentSamples.addAll(me.carriedSamples);
    
    memoization.clear();
    
    score = getScore();
    bestChild = this;
    
    // condition to stop going further
    if (!canGoFurther()) {
      return;
    }
    
    nextPossibilities(0, state, me);
  }

  private void nextPossibilities(int depth, GameState state, Robot me) {
    // try to remove 
    for (Sample sample : currentSamples) {
      if (me.carriedSamples.indexOf(sample) != -1) {
        SampleOptimizerNode node = new SampleOptimizerNode();
        SampleOptimizerNode child = node.applyTurn(depth+1, this, sample, true);
        if (child.score > score) {
          score = child.score;
          bestChild = child;
        }
        children.add(node);
      }
    }
    // try to add from samples
    if (currentSamples.size() < 3) {
      for (Sample sample : state.availableSamples) {
        if (currentSamples.indexOf(sample) == -1) {
          SampleOptimizerNode node = new SampleOptimizerNode();
          SampleOptimizerNode child = node.applyTurn(depth+1, this, sample, false);
          if (child.score > score) {
            score = child.score;
            bestChild = child;
          }
          children.add(node);
        }
      }
    }
  }

  public Integer encodeState() {
    int total = 0;
    currentSamples.sort(new Comparator<Sample>() {
      @Override
      public int compare(Sample o1, Sample o2) {
        return Integer.compare(o1.id, o2.id);
      }
    });
    int i=0;
    for (Sample sample : currentSamples) {
      total += memoDecal[i++]*(sample.id+1);
    }
    return new Integer(total);
  }

  public SampleOptimizerNode applyTurn(int depth, SampleOptimizerNode from, Sample sample, boolean removed) {
    currentSamples.addAll(from.currentSamples);

    if (removed) {
      currentSamples.remove(sample);
    } else {
      currentSamples.add(sample);
    }
    
    Integer key = encodeState();
    SampleOptimizerNode value = memoization.get(key);
    if (value != null) {
      bestChild = value;
      return value;
    }
    
    //TODO is 0.5 a good candidate ?
    score = Math.max(0, getScore() - 0.5*depth); // one action take some time, but it is not irrevocable
    bestChild = this;
    
    if (!canGoFurther()) {
      memoization.put(key, this);
      return this;
    } else {
      nextPossibilities(depth+1, state, me);
    }
    
    memoization.put(key, bestChild);
    return bestChild;
  }

  private boolean canGoFurther() {
    for (Sample sample : currentSamples) {
      if (me.carriedSamples.indexOf(sample) != -1) return true;
    }
    return currentSamples.size() < 3;
  }

  /** 
   * get the best score of the current state
   * by trying every combination
   * @return
   */
  public double getScore() {
    return calculateScore();
  }

  private double calculateScore() {
    if (currentSamples.size() == 0 ) {
      score = 0.0;
      return score;
    }
    
    node.updateSamples(currentSamples);

    MoleculeComboInfo combo = node.getLocalCombo();
    int moleculeCount = combo.neededMoleculeToRealiseCombo();
    score = 1.0*combo.scoreRealized() / (moleculeCount+1);
    
    return score;
  }

  public SampleOptimizerNode getBestChild() {
    return bestChild;
  }
  @Override
  public String toString() {
    String output="currentSamples : ";
    for (Sample sample : currentSamples) {
      output+=""+sample.id+",";
    }
    
    return output;
  }
  
}
