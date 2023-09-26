package oldcvz;

public class Human {

  public Human(int id) {
    this.id = id;
  }

  public final int id;

  public final Point p = new Point(0,0);

  public boolean dead;

  public void init() {
    dead = true;
  }

  public void update(int humanX, int humanY) {
    p.copyFrom(humanX, humanY);
    dead = false;
  }

  public void copy(Human model) {
    p.copyFrom(model.p);
    dead = model.dead;
  }

  public Zombie getCloserZombie(Zombie[] zombies) {
    Zombie bestZ = null;
    double bestDist = Double.MAX_VALUE;
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