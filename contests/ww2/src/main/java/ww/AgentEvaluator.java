package ww;

import ww.sim.Simulation;

public class AgentEvaluator {
  GameState state;
  Agent agent;
  
  public static boolean debug = false;
  private static int FEATURE_END = 0;
  public static final int FEATURE_SCORE = FEATURE_END++;
  public static final int FEATURE_POSITION = FEATURE_END++;
  public static final int FEATURE_ELEVATION = FEATURE_END++;
  public static final int FEATURE_NEIGHBOURS = FEATURE_END++;
  public static final int FEATURE_ACTIONS = FEATURE_END++;
  public static final int FEATURE_CLIFF = FEATURE_END++;
  public static final int FEATURE_ACCESSIBLE_CELLS = FEATURE_END++;
  public static final int FEATURE_POTENTIAL_CELLS = FEATURE_END++;
  double features[] = new double [FEATURE_END];
  static String[] featuresString = new String[]{
      "SCORE",
      "POSITION",
      "ELEVATION",
      "NEIGHBORS",
      "ACTIONS",
      "CLIFF",
      "ACCESSIBLE CELLS",
      "POTENTIAL CELLS"
  };
  
  AgentEvaluator(GameState state, Agent agent) {
    this.state = state;
    this.agent = agent;
  }
  
  public static double score(GameState state) {
    double score = 0.0;
    score += AgentEvaluator.score(state, state.agents[0]);
    score += AgentEvaluator.score(state, state.agents[1]);
    score -= AgentEvaluator.score(state, state.agents[2]);
    score -= AgentEvaluator.score(state, state.agents[3]);
    
    return score;    
  }

  public static double score(GameState state, Agent agent) {
    if (agent.inFogOfWar()) return 0.0;
    
    AgentEvaluator ae = new AgentEvaluator(state, agent);

    ae.features[FEATURE_SCORE] = 20.0 * agent.score;
    ae.features[FEATURE_POSITION] = 1.0 * ae.position();
    ae.features[FEATURE_ELEVATION] = 50.0 * ae.elevation();
    ae.features[FEATURE_NEIGHBOURS] = 1.0 * ae.neighbouringElevation();
    ae.features[FEATURE_ACTIONS] =1.0 * ae.countActions();
    ae.features[FEATURE_CLIFF] = 1.0 * ae.dangerousCliffs();
    ae.features[FEATURE_ACCESSIBLE_CELLS] = 1.0 * ae.accessibleCells();
    ae.features[FEATURE_POTENTIAL_CELLS] = 0.0 ;//* ae.potentialCells();
    
    if (debug) {
      System.err.println("Scores for agent "+agent.id);
      for (int i=0;i<FEATURE_END;i++) {
        System.err.println("    "+featuresString[i]+" = "+ae.features[i]);
      }
    }
    
    double score = 0.0;
    for (int i=0;i<FEATURE_END;i++) {
      score += ae.features[i];
    }
    return score;
  }

  private double potentialCells() {
    int count = AccessibleCellsCalculator.countWithoutLevel(state, agent);
    return count;
  }

  private double accessibleCells() {
    int count = AccessibleCellsCalculator.count(state, agent);
    if (count == 0) {
      return -10_000.0;
    }else {
      return count;
    }
//    if (count > 0)
//      return AccessibleCellsCalculator.countWithoutLevel(state, agent);
//    else {
//      return -10_000; // big malus
//    }
  }

  private double neighbouringElevation() {
    double score = 0.0;
    for (int i=0;i<Dir.LENGTH;i++) {
      Cell checkCell = agent.cell.neighbors[i];
      if (checkCell.isValid() && checkCell.height <= agent.cell.height +1 ) {
        score += (1+checkCell.height) * (1+checkCell.height);
      }
    }
    return score;
  }

  private int elevation() {
    return agent.cell.height * agent.cell.height;
  }

  /**
   * Big malus if we put ourself in a pushed position with a high cliff
   */
  double dangerousCliffs() {
    double score = 0.0;
    Cell current = agent.cell;
    for (Dir dir : Dir.getValues()) {
      if (!current.get(dir).isThreat(agent)) continue;
      for (Dir push : dir.inversePushDirections()) {
        Cell pushCell = current.get(push);
        if (pushCell.agent == null && pushCell.height < current.height-1) {
          score -= 1.0; // malus
        }
      }
    }
    return score;
  }

  double countActions() {
    int possibleActionsCount = Simulation.getPossibleActionsCount(state, agent);
    if (possibleActionsCount == 0) {
      return -100_000;
    }
    return possibleActionsCount;
  }

  double position() {
    int manhattanDistance = Math.abs(agent.position.x - GameState.size/2)
        +Math.abs(agent.position.y - GameState.size/2);
    return  -1.0*manhattanDistance; // malus if we are far from center
  }
}
