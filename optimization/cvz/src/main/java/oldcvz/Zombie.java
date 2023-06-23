package oldcvz;

public class Zombie {

  public Zombie(int id) {
    this.id = id;
  }

  public final int id;

  public Point p;

  public Point nextP;

  public boolean dead;

  public boolean deadThisTurn;

  public void init() {
    dead = true;
  }

  public void update(int zombieX, int zombieY, int zombieXNext, int zombieYNext) {
    p = new Point(zombieX, zombieY);
    nextP = new Point(zombieXNext, zombieYNext);
    dead = false;
    deadThisTurn = false;
  }

  public void copy(Zombie model) {
    p = model.p;
    nextP = model.nextP;
    dead = model.dead;
    deadThisTurn = model.deadThisTurn;
  }

  public Human findTarget(Ash ash, Human[] humans) {
    Human nearest = ash;
    double minDist = ash.p.squareDistance(p);
    for (Human h : humans) {
      double dist = h.p.squareDistance(p);
      if (dist < minDist) {
        minDist = dist;
        nearest = h;
      }
    }
    return nearest;
  }
}