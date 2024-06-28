package coif_old.actions;

import coif_old.Pos;

public class TrainAction extends Action {
  int level;
  Pos pos;
  
  public TrainAction(int level, Pos pos) {
    this.level = level;
    this.pos = pos;
  }
  
  @Override
  public String toString() {
    return "TRAIN "+level+" "+pos.x+" "+pos.y+";";
  }

}
