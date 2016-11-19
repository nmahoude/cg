package stc3.mcts;

import stc2.Simulation;

public class ScoreHeuristics {

  public double get(Simulation simulator, Node node) {
    return 
        -1.0*simulator.groupsCount[1]
        +0.0*simulator.groupsCount[2]
        +0.0*simulator.groupsCount[3];
  }

}
