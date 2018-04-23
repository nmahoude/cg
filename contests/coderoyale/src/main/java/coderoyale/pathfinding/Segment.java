package coderoyale.pathfinding;

import trigonometry.Point;

public class Segment {
  Point from;
  Point to;
  double length;

  public Segment(Point from, Point to) {
    this.from = from;
    this.to = to;
    length = from.distTo(to);
  }
  @Override
  public String toString() {
    return String.format("s=(%.0f,%.0f)->(%.0f,%.0f)", from.x, from.y, to.x, to.y);
  }
}
