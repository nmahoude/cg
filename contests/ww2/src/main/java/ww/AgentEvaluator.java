package ww;

import ww.sim.Simulation;

public class AgentEvaluator {
  GameState state;
  Agent agent;
  
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
    
//    System.err.println("Evaluating for agent : "+agent.id);
//    if (agent == null) System.err.println("Agent is null");
//    if (ae == null) System.err.println("AE is null");
//    if (agent.cell == null) System.err.println("Agent.Cell is null");
    
    return 0.0
        + 1000.0 * agent.score
        + 1.0 * ae.position()
        + 50.0 * ae.elevation()
        + 1.0 * ae.neighbouringElevation()
        + 1.0 * ae.countActions()
        + 1.0 * ae.dangerousCliffs()
        + 1.0 * ae.accessibleCells()
        ;
  }

  private double accessibleCells() {
    if (AccessibleCellsCalculator.count(state, agent) > 0)
      return AccessibleCellsCalculator.countWithoutLevel(state, agent);
    else {
      return -10_000; // big malus
    }
  }

  private double neighbouringElevation() {
    double score = 0.0;
    for (Dir dir : Dir.values()) {
      Cell checkCell = agent.cell.get(dir);
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
    for (Dir dir : Dir.values()) {
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
