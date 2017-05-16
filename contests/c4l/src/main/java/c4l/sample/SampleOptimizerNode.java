package c4l.sample;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import c4l.GameState;
import c4l.entities.MoleculeType;
import c4l.entities.Robot;
import c4l.entities.Sample;
import c4l.molecule.MoleculeComboInfo;
import c4l.molecule.MoleculeOptimizerNode;

public class SampleOptimizerNode {
  public static final int ID = 0;
  public static final int NEW = 1;
  public static final int LAST = 2;
  
  public static final int WIDTH = 3; 

  private static GameState state;
  private static Robot me;
  
  public static Map<Integer, SampleComboInfo> memoization = new HashMap<>();
  public static List<Sample> samplesPool =null;
  
  private static int memoDecal[] = new int[] { 001_000_000, 000_001_000, 000_000_001 }; // 999 samples in cloud MAX
  private static double patience[] = new double[30];
  static {
    for (int i=0;i<patience.length;i++) {
      patience[i] = Math.pow(0.8, i);
    }
  }
  
  public int values[] = new int[WIDTH*LAST];
  
  public Sample pickedSample;
  public List<SampleOptimizerNode> children = new ArrayList<>();
  public double score;
  public SampleComboInfo combo;

  public SampleOptimizerNode() {
    for (int i=0;i<3;i++) {
      values[WIDTH*ID+ i] = -1;
      values[WIDTH*NEW + i] = 0;
    }
  }
  
  @Override
  public String toString() {
    String output="";
    output+="Id : [";
    for (int j=0;j<WIDTH;j++) {
      output+=""+String.format("%3i", values[WIDTH*ID + j])+",";
    }
    output+="]);\n\r";
    
    output+="New: [";
    for (int j=0;j<WIDTH;j++) {
      output+=""+String.format("%3s", values[WIDTH*NEW + j] ==0 ? "N" : "Y")+",";
    }
    output+="]);\n\r";

    return output;
  }
  public void start (GameState state, Robot me) {
    this.state = state;
    this.me = me;
    
    memoization.clear();
    
    combo = getLocalCombo();
    score = combo.score;

    // condition to stop going further
    if (!canGoFurther()) {
      return;
    }
    
    // try to remove 
    for (int i=0;i<3;i++) {
      if (values[WIDTH*NEW+i] == -1) {
        SampleOptimizerNode node = new SampleOptimizerNode();
        SampleComboInfo info = node.applyTurn(0, this, null, i);
      if (info.score > score) {
        score = info.score;
        combo = info;
      }
      children.add(node);
      }
    }

    // try to add from samples
    int freeIndex = -1;
    for (int i=0;i<3;i++) {
      if (values[WIDTH*ID+i] == -1) {
        freeIndex = 1;
        break;
      }
    }
    if (freeIndex != -1) {
      for (Sample sample : samplesPool) {
        boolean found = false;
        for (int i=0;i<3;i++) {
          if (values[WIDTH*ID+i] == sample.id) found = true;
        }
        if (found) continue; // already in the list
        
        SampleOptimizerNode node = new SampleOptimizerNode();
        SampleComboInfo info = node.applyTurn(0, this, sample, freeIndex);
        if (info.score > score) {
          score = info.score;
          combo = info;
        }
        children.add(node);
      }
    }
  }

  private boolean canGoFurther() {
    return !(
        values[WIDTH*NEW +0]!=-1 && values[WIDTH*NEW +1]!=-1 && values[WIDTH*NEW+2]!= -1 
        && values[WIDTH*ID +0]!=-1 && values[WIDTH*ID +1] !=-1 && values[WIDTH*ID+2]!=-1);
  }

  public Integer encodeState() {
    int total = 0;
    for (int i=0;i<WIDTH;i++) {
      total += memoDecal[i] * values[WIDTH*ID+ i];
    }
    return new Integer(total);
  }

  public SampleComboInfo applyTurn(int depth, SampleOptimizerNode from, Sample addedSample, int index) {
    System.arraycopy(from.values, 0, values, 0, values.length);

    if (addedSample == null) {
      // removing
      values[WIDTH*ID + index] = -1;
      values[WIDTH*NEW + index] = 1;
    } else {
      values[WIDTH*ID + index] = samplesPool.indexOf(addedSample);//TODO optimize if needed
      values[WIDTH*NEW + index] = 1;
    }
    
    Integer key = encodeState();
    SampleComboInfo value = memoization.get(key);
    if (value != null) {
      combo = value;
      return value;
    }
    
    SampleComboInfo best = getLocalCombo();
    score = patience[depth] * best.score;
    
    if (!canGoFurther()) {
      memoization.put(key, best);
      combo = best;
      return best;
    } else {
   // try to remove 
      for (int i=0;i<3;i++) {
        if (values[WIDTH*NEW+i] == -1) {
          SampleOptimizerNode node = new SampleOptimizerNode();
          SampleComboInfo info = node.applyTurn(0, this, null, i);
        if (info.score > score) {
          score = info.score;
          combo = info;
        }
        children.add(node);
        }
      }

      // try to add from samples
      int freeIndex = -1;
      for (int i=0;i<3;i++) {
        if (values[WIDTH*ID+i] == -1) {
          freeIndex = 1;
          break;
        }
      }
      if (freeIndex != -1) {
        for (Sample sample : samplesPool) {
          boolean found = false;
          for (int i=0;i<3;i++) {
            if (values[WIDTH*ID+i] == sample.id) found = true;
          }
          if (found) continue; // already in the list
          
          SampleOptimizerNode node = new SampleOptimizerNode();
          SampleComboInfo info = node.applyTurn(0, this, sample, freeIndex);
          if (info.score > score) {
            score = info.score;
            combo = info;
          }
          children.add(node);
        }
      }
    }
    memoization.put(key, best);
    combo = best;
    return best;
  }

  /** 
   * get the best score of the current state
   * by trying every combination
   * @return
   */
  public SampleComboInfo getLocalCombo() {
    double bestScore = Double.NEGATIVE_INFINITY;
    SampleComboInfo best = new SampleComboInfo();
    best.score = calculateScore();
    return best;
  }


  // TODO check the different score factors 
  private double calculateScore() {
    double score = 0.0;
    for (int i=0;i<3;i++) {
      if (values[WIDTH*ID+i] == -1) continue; // no sample here
      Sample sample = samplesPool.get(values[WIDTH*ID+i]);
      if (me.canCompleteSampleAuto(sample)) {
        score += 2; // 2 points for autocomplete
        score += sample.roi();
        if (state.distanceToScienceProjects(me, sample.gain) == 0) { score += 50;} // oh ! that's good
      } else if (me.canCompleteSampleWithMoleculePool(state, sample)) {
        score += 1; // 1 point for completing the sample
        score += sample.roi();
        if (state.distanceToScienceProjects(me, sample.gain) == 0) { score += 5;}
      }
    }
    
    return score;
  }

  public void createCarriedSample(int index, Sample sample) {
    values[WIDTH*ID+index] = samplesPool.size();
    values[WIDTH*NEW+index] = -1;
    addSample(sample);
  }
  
  public void addSample(Sample sample) {
    samplesPool.add(sample);
  }

  public SampleOptimizerNode getBestChild() {
    double bestScore = Double.NEGATIVE_INFINITY;
    SampleOptimizerNode best = this;
    for (SampleOptimizerNode node : children) {
      if (node.score  > bestScore) {
        bestScore = node.score;
        best = node;
      }
    }
    return best;
  }

}
