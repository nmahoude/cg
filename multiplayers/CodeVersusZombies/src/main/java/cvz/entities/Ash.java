package cvz.entities;

import trigonometry.Point;

public class Ash extends Human {

  public Ash() {
    super(-1);
  }

  public void move(Point p) {
    this.p = p;
  }
}
