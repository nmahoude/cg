package lcm.ai.eval;

import lcm.State;

public class InvEval implements IEval {

  private IEval eval;

  public InvEval(IEval eval) {
    this.eval = eval;
  }
  
  @Override
  public double eval(State state, boolean debug) {
    return  - eval.eval(state, debug);
  }

}
