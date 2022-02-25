package botg;

public class Action {
  public static final Action CHOOSE_HULK = new Action("HULK");
  public static final Action WAIT = new Action("WAIT");
  public static final Action ATTACK_NEAREST_HERO = new Action("ATTACK_NEAREST HERO");
  
  private String actionStr;

  public Action(String actionStr) {
    this.actionStr = actionStr;
  }

  @Override
  public String toString() {
    return actionStr;
  }
}
