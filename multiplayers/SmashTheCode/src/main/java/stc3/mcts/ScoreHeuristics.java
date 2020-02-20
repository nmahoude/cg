package stc3.mcts;

import stc2.Simulation;

public class ScoreHeuristics {

  public double getConstructionScore(Simulation simulator, Node node) {
    return 
        simulator.points+
        columnsScore(simulator, node)+
        groupsCountScore(simulator);
  }

  private double columnsScore(Simulation simulator, Node node) {
    return 
        -0.1*node.board.getColHeight(0)
        +0.1*node.board.getColHeight(1)
        +0.1*node.board.getColHeight(2)
        +0.1*node.board.getColHeight(3)
        +0.1*node.board.getColHeight(4)
        -0.1*node.board.getColHeight(5)
        ;
  }

  private double groupsCountScore(Simulation simulator) {
    return -1.0*simulator.groupsCount[1]
    +1.0*simulator.groupsCount[2]
    +2.0*simulator.groupsCount[3];
  }

  public int getPoints(Simulation simulator, Node node) {
    return simulator.points / 48;
  }
}
