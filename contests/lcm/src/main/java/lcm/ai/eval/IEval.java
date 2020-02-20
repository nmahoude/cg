package lcm.ai.eval;

import lcm.State;

public interface IEval {

  double eval(State state, boolean debug);

}