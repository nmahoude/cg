package hypersonic.ai.beamSearch;

import hypersonic.Move;
import hypersonic.State;

public class BeamNode {
  State state = new State();
  Move moveToHere;
  double cumulativeScore;
  BeamNode parent;
  
  int firstChildIndex = -1;
  int movesFE = -1;
  
}
