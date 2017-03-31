package gitc.simulation.actions;

import gitc.entities.Factory;

public class UpgradeAction extends Action {

  public UpgradeAction(Factory src) {
    super(ActionType.UPGRADE);
    this.src = src;
  }
  
  public final Factory src;

  @Override
  public String output() {
    return "INC "+src.id;
  }  
}
