package lcm.ai.eval;

import java.util.Arrays;
import java.util.List;

import org.junit.After;
import org.junit.Before;

import lcm.BattleMode;
import lcm.State;
import lcm.cards.Card;
import lcm.sim.Action;

public class EvalTest extends BattleMode {
  public static final String NL = "\n\r";
  IEval eval1, eval2;
  boolean debug;
  @Before
  public void setup() {
    super.setup();
    
    debug = false;
    eval1 = new Eval5();
    eval2 = new Eval5();
  }
  
  @After
  public void tearDown() {
    if (debug) {
      if (eval1 instanceof Eval5) {
        Eval5.comparator((Eval5)eval1, (Eval5)eval2);
      } else if (eval1 instanceof Eval6) {
        Eval6.comparator((Eval6)eval1, (Eval6)eval2);
      } else if (eval1 instanceof Eval8) {
        Eval8.comparator((Eval8)eval1, (Eval8)eval2);
      }
    }
  }
  
  private void compareEvals() {
  }
  
  double doActions1(Action... actions) {
    return doActions1(false, actions);
  }
  double doActions1(boolean debug, Action... actions) {
    List<Action> a = Arrays.asList(actions);
    State result = sim.simulate(state, a);
    return eval1.eval(result, debug);
  }

  double doHisActions1(Action... actions) {
    return doHisActions1(false, actions);
  }
  double doHisActions1(boolean debug, Action... actions) {
    List<Action> a = Arrays.asList(actions);
    State result = sim.simulate(state, a, false);
    return eval1.eval(result, debug);
  }

  double doActions2(Action... actions) {
    return doActions2(false, actions);
  }
  double doActions2(boolean debug, Action... actions) {
    List<Action> a = Arrays.asList(actions);
    State result = sim.simulate(state, a);
    return eval2.eval(result, debug);
  }

  double doHisActions2(Action... actions) {
    return doHisActions2(false, actions);
  }
  double doHisActions2(boolean debug, Action... actions) {
    List<Action> a = Arrays.asList(actions);
    State result = sim.simulate(state, a, false);
    return eval2.eval(result, debug);
  }

  Action USE(int a, int b) {
    return use(a,b);
  }
  Action ATTACK(int a, int b) {
    return attack(a,b);
  }
  Action SUMMON(int a) {
    return summon(a);
  }
  Action SUMMON(int a, int b) {
    return summon(a,b);
  }
  Action use(int a, int b) {
    if (b == -1) return Action.use(state.card(a), Card.opponent);
    return Action.use(state.card(a), state.card(b));
  }
  Action summon(int a) {
    return Action.summon(state.card(a));
  }
  Action summon(int a, int b) {
    return summon(a);
  }

  Action attack(int a, int b) {
    if (b == -1) return Action.attack(state.card(a), Card.opponent);
    return Action.attack(state.card(a), state.card(b));
  }
}
