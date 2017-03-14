package cvz.entities;

import trigonometry.Point;

public class Zombie {
  public Zombie(int id) {
    this.id = id;
  }
  
  public final int id;
  public Point p;
  public Point nextP;
  public boolean dead;
  
  public void init() {
    dead = true;
  }
  public void update(int zombieX, int zombieY, int zombieXNext, int zombieYNext) {
    p = new Point(zombieX,zombieY);
    nextP = new Point(zombieXNext,zombieYNext);
    dead = false;
  }
  public void copy(Zombie zombie) {
    p = zombie.p;
    nextP = zombie.nextP;
    dead = zombie.dead;
    
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
