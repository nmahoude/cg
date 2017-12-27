package trigo;

import meanmax.Game;

public class Position {
  public double x;
  public double y;
  
  public Position() {
  }
  public Position(double x, double y) {
    this.x = x;
    this.y = y;
  }
  
  public final double dist2(Position p) {
    return (p.x-x)*(p.x-x) + (p.y-y)*(p.y-y);
  }
  public final double dist(Position p) {
    return Math.sqrt((p.x-x)*(p.x-x) + (p.y-y)*(p.y-y));
  }
  public final double length2() {
    return x * x + y * y;
  }
  public final double length() {
    return Math.sqrt(x * x + y * y);
  }

  public void copyFrom(Position position) {
    this.x = position.x;
    this.y = position.y;
  }
  public static void random(Position p, double radius) {
    p.x = radius * 2.0 * (0.5 - Game.random.nextDouble());
    p.y = radius * 2.0 * (0.5 - Game.random.nextDouble());
  }
  public static void randomCircle(Position p, double radius) {
    double angle = Math.PI * 2.0 * Game.random.nextDouble();
    p.x = radius * Math.cos(angle);
    p.y = radius * Math.sin(angle);
  }

  @Override
  public String toString() {
    return String.format("(%.2f, %.2f)", x, y);
  }

  public String toOutput() {
    return String.format("%.0f %.0f", x, y);
  }

}
