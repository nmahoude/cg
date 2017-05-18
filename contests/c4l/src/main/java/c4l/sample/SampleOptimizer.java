package c4l.sample;

import java.util.ArrayList;
import java.util.List;

import c4l.GameState;
import c4l.entities.Robot;
import c4l.entities.Sample;
import c4l.entities.ScienceProject;

/**
 * Brute force optimizer (should pass)
 * @author nmahoude
 *
 */
public class SampleOptimizer {
  GameState state;
  Robot me;
  public List<Sample> samples = new ArrayList<>();
  
  public List<Sample> optimize(GameState state, Robot me) {
    this.state = state;
    this.me = me;
    
    samples.clear();
    for (Sample sample : state.availableSamples) {
      samples.add(sample);
    }
    for (Sample sample : me.carriedSamples) {
      if (!sample.isDiscovered()) continue;
      samples.add(sample);
    }
    
    List<Sample> bestSamples = new ArrayList<>();
    double bestScore = Double.NEGATIVE_INFINITY;
    
    for (Sample sample1 : samples) {
      for (Sample sample2 : samples) {

        for (Sample sample3 : samples) {
          List<Sample> currentSamples = new ArrayList<>();

          currentSamples.add(sample1);
          if (sample1 != sample2) {
            currentSamples.add(sample2);
          }
          if (sample3 != sample1 && sample3 != sample2) {
            currentSamples.add(sample3);
          }
          
          double score = calculateScore(currentSamples);
          if (score > bestScore) {
            bestScore = score;
            bestSamples = currentSamples;
          }
        }
      }
    }
    return bestSamples;
  }

  int storage[] = new int[GameState.MOLECULE_TYPE];
  int pickedMolecules[] = new int[GameState.MOLECULE_TYPE];
  int expertise[] = new int[GameState.MOLECULE_TYPE];
  int availables[] = new int[GameState.MOLECULE_TYPE];
  
  private double calculateScore(List<Sample> currentSamples) {
    init();
    int points = 0;
    int totalMoleculePicked = 0;
    
    for (Sample sample : currentSamples) {
      for (int j=0;j<GameState.MOLECULE_TYPE;j++) {
        int needed = sample.costs[j];
        if ( needed > availables[j]+storage[j]+expertise[j]) {
          return Double.NEGATIVE_INFINITY; // can't do it
        } else {
          if (expertise[j] >= needed) {
            needed = 0;
          } else {
            needed-= expertise[j];
          }
          if (storage[j] >= needed ) {
            storage[j] -= needed;
            needed = 0;
          } else {
            needed -= storage[j];
            storage[j] = 0;
          }
          if (availables[j] < needed) {
            return Double.NEGATIVE_INFINITY; // can't do it
          } else {
            totalMoleculePicked+=needed;
            pickedMolecules[j]+=needed;
            availables[j]-=needed;
            needed = 0;
          }
        }
      }
      expertise[sample.expertise.index]++;
      points+=sample.health;
    }
    
    points += getExpertisePoints();
    
    double score = 0.0;
    if (totalMoleculePicked ==0) {
      score += 1000;
      totalMoleculePicked++;
    }
    int actionToState = 0;
    for (Sample removed : me.carriedSamples) {
      if (currentSamples.indexOf(removed) == -1) {
        actionToState++;// action to remove the sample
      }
    }
    for (Sample added : currentSamples) {
      if (me.carriedSamples.indexOf(added) == -1) {
        actionToState++;
      }
    }
    if (actionToState+totalMoleculePicked==0) {
      return Double.MAX_VALUE;
    }
    
    score += 1.0*points / (actionToState + totalMoleculePicked);
    
    return score;
  }
  private int getExpertisePoints() {
    int xpPoints = 0;
    for (ScienceProject sp : state.scienceProjects) {
      if (sp.doneBy != -1) continue;
      boolean good = true;
      for (int i=0;i<GameState.MOLECULE_TYPE;i++) {
        if (expertise[i]<sp.expertiseNeeded[i]) {
          good = false;
          break;
        }
      }
      if (good)  {
        xpPoints+=50;
      }
    }
    return xpPoints;
  }
  private void init() {
    for (int i=0;i<GameState.MOLECULE_TYPE;i++) {
      storage[i] = me.storage[i];
      expertise[i] = me.expertise[i];
      pickedMolecules[i] = 0;
      availables[i] = state.availables[i];
    }
  }
}