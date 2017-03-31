package gitc.simulation.actions;

public abstract class Action {
  public final ActionType type;

  Action(ActionType type) {
    this.type = type;
  }

  public abstract String output() ;
}
