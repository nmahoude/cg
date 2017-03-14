package cvz.entities;

import trigonometry.Point;

public class Human {
  public Human(int id) {
    this.id = id;
  }
  public final int id;
  public Point p;
  public boolean dead;
  
  public void init() {
    dead = true;
  }
  public void update(int humanX, int humanY) {
    p = new Point(humanX,humanY);
    dead = false;
  }
  public void copy(Human human) {
    p = human.p;
    dead = human.dead;
  }
  public Zombie getCloserZombie(Zombie[] zombies) {
    Zombie bestZ = null;
    double bestDist = 16000*16000+9000*9000+1;
    for (Zombie z : zombies) {
      if (z.dead) continue;
      double dist = z.p.squareDistance(p);
      if (dist < bestDist) {
        bestDist = dist;
        bestZ = z;
      }
    }
    return bestZ;
  }
}
