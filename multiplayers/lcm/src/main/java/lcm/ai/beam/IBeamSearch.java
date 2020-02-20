<<<<<<< Updated upstream
package lcm.ai.beam;

import java.util.List;

import lcm.State;
import lcm.ai.AI;
import lcm.sim.Action;

public interface IBeamSearch extends AI {

  int CHILD_SIZE = 1024;

  void think(State state);

  List<Action> getResultActions();

  void output(State state);

  BSNode getBestNode();

=======
package lcm.ai.beam;

import java.util.List;

import lcm.State;
import lcm.ai.AI;
import lcm.sim.Action;

public interface IBeamSearch extends AI {

  int CHILD_SIZE = 1024;

  void think(State state);

  List<Action> getResultActions();

  void output(State state);

  BSNode getBestNode();

>>>>>>> Stashed changes
}