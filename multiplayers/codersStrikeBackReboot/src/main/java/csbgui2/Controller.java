package csbgui2;

import csb.entities.CheckPoint;
import csb.entities.Pod;

public abstract class Controller {
  Pod[] pods;
  CheckPoint[] checkPoints;
  
  public abstract void init();
  public abstract void update();
}
