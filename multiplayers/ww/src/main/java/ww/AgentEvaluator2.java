package ww;

import ww.paths.AccessibleCellsCalculator;
import ww.paths.InfluenceMap;
import ww.paths.Voronoi;
import ww.sim.Simulation;

public class AgentEvaluator2 {
  private static final double[] elevationScore = new double[] { 1,  2 , 3, 4      };
  static AgentEvaluator2 ae = new AgentEvaluator2(null, null);

  GameState state;
  Agent agent;
  
  AgentEvaluator2(GameState state, Agent agent) {
    this.state = state;
    this.agent = agent;
  }
  
  public static double score(GameState state) {
    double score = 0.0;
    ae.state = state;
    
    score += AgentEvaluator2.score(state.agents[0]);
    score += AgentEvaluator2.score(state.agents[1]);
    score -= AgentEvaluator2.score(state.agents[2]);
    score -= AgentEvaluator2.score(state.agents[3]);
    
    score += 2.0 * voronoi(state);
    return score;    
  }
  
  public static double score(Agent agent) {
    if (agent.position == Point.unknown) return 0.0;
    
    ae.agent = agent;
    
    double score = 2 * agent.score
        -1_000.0 * ae.blocked()
        ;
    
    return score;
  }


  private double blocked() {
    int currentHeight = agent.cell.height;
    for (int i = 0; i < Dir.LENGTH; i++) {
      Cell checkCell = agent.cell.neighbors[i];
      int height = checkCell.height;
      if (height != 4 && height<=currentHeight+1) return 0.0;
    }
    return 1.0;
  }

  private static double influenceMap(GameState state) {
    InfluenceMap map[] = new InfluenceMap[4];
    for (int i=0;i<4;i++) {
      map[i] = new InfluenceMap();
      if (!state.agents[i].inFogOfWar()) {
        map[i].calculateInfluence(state, state.agents[i]);
      }
    }
    double score = 0;
    for (int y=0;y<GameState.size;y++) {
      for (int x=0;x<GameState.size;x++) {
        double cellValue = 1.0;
        for (int i=0;i<4;i++) {
          score += cellValue * map[i].influenceMap[x][y];
        }
      }
    }    
    return score;
  }

  private static double voronoi2(GameState state) {
    double score = 0.0;
    Voronoi v = new Voronoi();
    int cells[];
    if (!state.agents[2].inFogOfWar()) {
      cells = v.voronoi2(state, state.agents[0], state.agents[2]);
      score += (cells[0]-cells[1]);
      cells = v.voronoi2(state, state.agents[1], state.agents[2]);
      score += (cells[0]-cells[1]);
    }
    if (!state.agents[3].inFogOfWar()) {
      cells = v.voronoi2(state, state.agents[0], state.agents[3]);
      score += (cells[0]-cells[1]);
      cells = v.voronoi2(state, state.agents[1], state.agents[3]);
      score += (cells[0]-cells[1]);
    }
    if (Player.DEBUG_SCORING) {
      System.err.println("Voronoi : "+ cells);
    }
    return score;
  }

  private static double voronoi(GameState state) {
    double score = 0.0;
    Voronoi v = new Voronoi();
    int cells[] = v.voronoi4(state);
    score += (cells[0]+cells[1]-cells[2]-cells[3]);
    if (Player.DEBUG_SCORING) {
      System.err.println("Voronoi : "+ cells);
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
    for (int i = 0; i < Dir.LENGTH; i++) {
        Cell checkCell = agent.cell.neighbors[i];
        int height = checkCell.height;
        if (height != 4 && height <= agent.cell.height + 1) {
            score += elevationScore[height]; //(1 + checkCell.height) * (1 + checkCell.height);
        }
    }
    return score;
}

  private int elevation() {
    return agent.cell.height;
  }

  /**
   * Big malus if we put ourself in a pushed position with a high cliff
   */
  double dangerousCliffs() {
    double score = 0.0;

    for (int i=0;i<4;i++) {
      if (agent.id  == i) continue;
      Agent other = state.agents[i];
      if (agent.isFriendly(other)) continue;
      if (other.inFogOfWar()) continue;
      if (!other.position.inRange(1, agent.position)) continue;
      
      Dir dir1 = other.cell.dirTo(agent.cell);
      for (Dir dir2  : dir1.pushDirections()) {
        Cell pushCell = agent.cell.get(dir2);
        if (pushCell.agent == null && pushCell.height < agent.cell.height-1) {
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
    int manhattanDistance = Math.abs(agent.position.x - GameState.size / 2) + Math.abs(agent.position.y - GameState.size / 2);
    return  -1.0*manhattanDistance; // malus if we are far from center
  }
}
