package coif_old.actions;

import coif_old.Pos;

public class BuildTowerAction extends Action {
  Pos pos;
  
  public BuildTowerAction(Pos pos) {
    this.pos = pos;
  }
  
  @Override
  public String toString() {
    return "BUILD TOWER "+pos.x+" "+pos.y+";";
  }
}
