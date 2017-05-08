package pokerChipRace.ai;

import pokerChipRace.GameState;
import pokerChipRace.entities.Entity;

public class Feature {
  
  public static final int MY_TOTAL_RADIUS = 0;
  public static final int ALL_OTHER_TOTAL_RADIUS = 1;
  public static final int DIST_TO_SMALLER_ENTITIES = 2;
  public static final int DIST_TO_BIGGER_ENTITIES = 3;
  public static final int LAST = 4;

  public double features[] = new double[LAST];
  public static final String[] debugFeatures= {
      "my radius       ",
      "all other radius",
      "dist2 small e   ",
      "dist2 big e     ",
      "last            ",
  };

  private static final double RADIUS_SECUTIRY_MARGIN = 0.9;
  

  public void calculateIntermadiaryFeatures(GameState state) {
    for (int index = 0; index < state.entityFE; index++) {
      Entity entity = state.chips[index];
      if (entity.isDead())  continue;
      if (entity.owner == -1) break;

      if (entity.owner == state.myId) {
        features[MY_TOTAL_RADIUS]+= entity.radius;
      } else {
        features[ALL_OTHER_TOTAL_RADIUS] += entity.radius;
      }
    }
    
    for (int index = 0; index < state.myChips.length; index++) {
      Entity entity = state.myChips.elements[index];
      if (entity.isDead()) continue;
      
      for (int o= 0; o < state.entityFE; o ++) {
        Entity other = state.chips[o];
        double dist = (other.x-entity.x)*(other.x-entity.x) + (other.y-entity.y)*(other.y-entity.y);
        // don't test for exact size as opponents will feed the other entity to swallow us
        if (other.radius * RADIUS_SECUTIRY_MARGIN > entity.radius) {
          features[DIST_TO_BIGGER_ENTITIES] += dist / state._entityFE;
        } else {
          features[DIST_TO_SMALLER_ENTITIES] += dist / state._entityFE;
        }
      }
      features[DIST_TO_BIGGER_ENTITIES]  = 1.0 / features[DIST_TO_BIGGER_ENTITIES]; 
      features[DIST_TO_SMALLER_ENTITIES] = 1.0 / features[DIST_TO_SMALLER_ENTITIES];
    }    
  }
  
  public void calculateFinalFeatures(GameState state) {

  }

  public void reset() {
    for (int i=0;i<LAST;i++) {
      features[i] = 0;
    }
  }
  
  public void debugFeature(FeatureWeight weight) {
    for (int i=0;i<LAST;i++) {
      debugFeature(weight, i);
    }
  }

  private void debugFeature(FeatureWeight weight, int i) {
    System.err.printf("%s = %.0f * %.2f = %.2f\n",
        debugFeatures[i], features[i], weight.weights[i],
        features[i]*weight.weights[i]
        );
  }
    
  public double applyWeights(FeatureWeight weights) {
    double total = 0;
    for (int i=0;i<LAST;i++) {
      total += weights.weights[i] * features[i];
    }
    return total;
  }
}
