package coif_old.actions;

import coif_old.Pos;

public class MoveAction extends Action {

  private int unitId;
  private Pos pos;

  public MoveAction(int unitId, Pos pos) {
    this.unitId = unitId;
    this.pos = pos;
  }

  @Override
  public String toString() {
    return "MOVE " + unitId + " "+pos.x+" "+pos.y+";";
  }
}
