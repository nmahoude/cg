package lcm.ai.beam;

import java.util.List;

import lcm.State;
import lcm.sim.Action;

public interface IBeamSearch {

  int CHILD_SIZE = 1024;

  void think(State state);

  List<Action> getResultActions();

  void output(State state);

  BSNode getBestNode();

}