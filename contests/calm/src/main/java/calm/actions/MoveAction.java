package calm.actions;

import calm.state.Agent;
import calm.state.State;
import util.P;

public class MoveAction extends Action {

  public static MoveAction[] moves = new MoveAction[77];
  static {
    for (int i=0;i<77;i++) {
      moves[i] = new MoveAction(P.get(i));
    }
  }
  
  private P point;

  private MoveAction(P point) {
    super(null);
    this.point = point;
  }

  @Override
  public boolean prerequisites(State state, Agent agent) {
    return true; // points where we can move is calculated somewhere else
  }

  @Override
  protected void privateApplyEffect(State state, Agent agent) {
    if (state.agent1.pos == point && agent != state.agent1) {
      throw new RuntimeException("ARG on arrive sur la meme place");
    }
    if (state.agent2.pos == point && agent != state.agent2) {
      throw new RuntimeException("ARG on arrive sur la meme place");
    }
    agent.pos = point;
  }

  @Override
  public String describe() {
    return "Move to "+point;
  }
  
  @Override
  public void execute(State state, Agent agent) {
    System.out.println("MOVE "+point.out());
  }
  
}
