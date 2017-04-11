package csb.ai;

import csb.entities.Pod;

public interface AGScorer {

  public double score(Simulation simulation, Pod pod);
}
