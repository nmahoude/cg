package pokerChipRace.ai;

import pokerChipRace.GameState;
import pokerChipRace.entities.Entity;

public class Feature {
  private static int featuresIndex=0;
  public static final int MY_BIGGEST_MASS = featuresIndex++;
  public static final int MY_TOTAL_MASS = featuresIndex++;
  public static final int ALL_OTHER_TOTAL_MASS = featuresIndex++;
  public static final int DIST_TO_SMALLER_ENTITIES = featuresIndex++;
  public static final int DIST_TO_BIGGER_ENTITIES = featuresIndex++;
  public static final int DIST_BETWEEN_MINE = featuresIndex++;
  public static final int DIST_CLOSEST_SMALLER = featuresIndex++;
  public static final int DIST_CLOSEST_BIGGER = featuresIndex++;
  public static final int SPEED = featuresIndex++;
  public static final int LAST = featuresIndex;

  public double features[] = new double[LAST];
  public static final String[] debugFeatures= {
      "my biggest mass ",
      "my total mass   ",
      "all other mass  ",
      "dist2 small e   ",
      "dist2 big e     ",
      "dist btwn mine  ",
      "mdist 2 smaller ",
      "mdist 2 bigger  ",
      "speed           ",
      "last            ",
  };

  private static final double RADIUS_SECUTIRY_MARGIN = 0.9;
  

  public void clear() {
    for (int i=0;i<LAST;i++) {
      features[i] = 0;
    }
  }

  public void calculateIntermadiaryFeatures(GameState state) {
    
    distanceBetweenMyChips(state);
    calculateMasses(state);
    calculateDistanceWithOtherChips(state);    
    calculateSpeed(state);
  }

  private void calculateSpeed(GameState state) {
    features[SPEED] = 0;

    for (int index = 0; index < state.myChips.length; index++) {
      Entity entity = state.myChips.elements[index];
      if (entity.isDead()) continue;
      features[SPEED] += entity.vx*entity.vx + entity.vy+entity.vy;
    }
  }

  private void calculateDistanceWithOtherChips(GameState state) {
    for (int index = 0; index < state.myChips.length; index++) {
      Entity entity = state.myChips.elements[index];
      if (entity.isDead()) continue;

      double minDistSmaller = Double.POSITIVE_INFINITY;
      double minDistBigger  = Double.POSITIVE_INFINITY;
      for (int o= 0; o < state.entityFE; o ++) {
        Entity other = state.chips[o];
        if (other.owner == entity.owner) continue;
        
        double dist = (other.x-entity.x)*(other.x-entity.x) + (other.y-entity.y)*(other.y-entity.y);
        if (other.radius < entity.radius && dist < minDistSmaller ) {
          minDistSmaller = dist;
        }
        if (other.radius > entity.radius && dist < minDistBigger) {
          minDistBigger = dist;
        }
        
        // don't test for exact size as opponents will feed the other entity to swallow us
        
        // TODO WTF HERE, need to get a good score from distance 
        if (other.radius * RADIUS_SECUTIRY_MARGIN > entity.radius) {
          features[DIST_TO_BIGGER_ENTITIES] += dist / state.chips.length;
        } else {
          features[DIST_TO_SMALLER_ENTITIES] += dist / state.chips.length;
        }
      }
      if (features[DIST_TO_BIGGER_ENTITIES] > 0) {
        features[DIST_TO_BIGGER_ENTITIES]  = 1.0 / features[DIST_TO_BIGGER_ENTITIES]; 
      } else {
        features[DIST_TO_BIGGER_ENTITIES] = 0.0;
      }
      if (features[DIST_TO_SMALLER_ENTITIES] > 0) {
        features[DIST_TO_SMALLER_ENTITIES] = 1.0 / features[DIST_TO_SMALLER_ENTITIES];
      } else {
        features[DIST_TO_SMALLER_ENTITIES] = 0.0;
      }
      
      features[DIST_CLOSEST_SMALLER] = Double.isFinite(minDistSmaller) ? minDistSmaller : 0;
      features[DIST_CLOSEST_BIGGER] = Double.isFinite(minDistBigger) ? minDistBigger : 0;
      
    }
  }

  private void calculateMasses(GameState state) {
    double biggest = 0.0;
    for (int index = 0; index < state.entityFE; index++) {
      Entity entity = state.chips[index];
      if (entity.isDead())  continue;
      if (entity.owner == -1) break;

      if (entity.owner == state.myId) {
        features[MY_TOTAL_MASS]+= entity.mass;
        if (entity.mass > biggest) {
          biggest = entity.mass;
        }
      } else {
        features[ALL_OTHER_TOTAL_MASS] += entity.mass;
      }
      features[MY_BIGGEST_MASS] = biggest;
    }
  }

  private void distanceBetweenMyChips(GameState state) {
    int dist2 = 0;
    for (int i=0;i<state.myChips.length;i++) {
      Entity one = state.myChips.elements[i];
      if (one.isDead()) continue;
      for (int j=i+1;j<state.myChips.length;j++) {
        Entity two = state.myChips.elements[j];
        dist2+= (one.x-two.x)*(one.x-two.x) + (one.y-two.y)*(one.y-two.y);
      }
    }
    features[DIST_BETWEEN_MINE] = dist2;
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
    System.err.printf("%s = %.3f * %.2f = %.2f\n",
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

  public void copy(Feature feature) {
    for (int i=0;i<LAST;i++) {
      features[i] = feature.features[i];
    }    
  }

}
