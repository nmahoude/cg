package csb.ai;

import csb.entities.Pod;

public class ZeroScorer implements AGScorer{

  @Override
  public double score(Simulation simulation, Pod pod) {
    return 0;
  }

}
