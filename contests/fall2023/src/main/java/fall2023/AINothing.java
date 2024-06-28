package fall2023;

public class AINothing {
  Action[] actions = new Action[] { new Action(), new Action(), new Action(), new Action() };
  int[] blipTarget = new int[] { -1, -1 };
  private State state;

  public Action[] think(State state) {
    this.state = state;

    for (Drone d: state.myDrones) {
      actions[d.id].dx = 0;
      actions[d.id].dy = 0;
      actions[d.id].lamp = false;
    }
    
    return actions;
    
    
  }
}
