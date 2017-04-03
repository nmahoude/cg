package fantasticBitsMulti.units;

import fantasticBitsMulti.Player;
import fantasticBitsMulti.simulation.Collision;
import fantasticBitsMulti.simulation.Simulation;

public class Snaffle extends Unit {
  Wizard scarrier;
  boolean danger = false;
  
  public Snaffle() {
    super(EntityType.SNAFFLE, 150.0, 0.5, 0.75);
    carrier = null;
  }
  
  
  @Override
  public void update(int id, int x, int y, int vx, int vy, int state) {
    super.update(id, x, y, vx, vy, state);
    dead = false;
  }

  @Override
  public Collision collision(double from) {
    if (carrier == null || dead) {
      return null;
    }

    double tx = 2.0;
    double ty = tx;

    if (position.x + vx < 0.0) {
      tx =  -position.x/vx;
    } else if (position.x + vx > WIDTH) {
      tx = (WIDTH - position.x)/vx;
    }

    if (position.y + vy < radius) {
      ty = (radius - position.y)/vy;
    } else if (position.y + vy > HEIGHT - radius) {
      ty = (HEIGHT - radius - position.y)/vy;
    }

    int dir;
    double t;

    if (tx < ty) {
      dir = HORIZONTAL;
      t = tx + from;
    } else {
      dir = VERTICAL;
      t = ty + from;
    }

    if (t <= 0.0 || t > 1.0) {
      return null;
    }

    return Simulation.collisionsCache[Simulation.collisionsCacheFE++].update(t, this, dir);
  }

  @Override
  public Collision collision(Unit u, double from) {
    if (u.type == EntityType.WIZARD) {
      radius = -1.0;
      Collision result = super.collision(u, from);
      radius = 150.0;

      return result;
    } else {
      return super.collision(u, from);
    }
  }

  @Override
  public void bounce(Unit u) {
    if (u.type == EntityType.WIZARD) {
      Wizard target = (Wizard) u;
      if (target.snaffle == null && target.grab == 0 && !dead && carrier == null) {
        target.grabSnaffle(this);
      }
    } else {
      super.bounce(u);
    }
  }

  public void bounce(int dir) {
    if (dir == HORIZONTAL && position.y >= 2050.0 && position.y <= 5450.0) {
      dead = true;

      if (Player.myTeam == 0) {
        if (position.x > 8000) {
          Player.myScore += 1;
        } else {
          Player.hisScore += 1;
        }
      } else {
        if (position.x > 8000) {
          Player.hisScore += 1;
        } else {
          Player.myScore += 1;
        }
      }
    } else {
      super.bounce(dir);
    }
  }

  @Override
  public void move(double t) {
    if (!dead && carrier== null) {
      super.move(t);
    }
  }

  @Override
  public void end() {
    if (!dead && carrier == null) {
      super.end();
    }
  }

  @Override
  public void save() {
    super.save();
    scarrier = carrier;
  }

  @Override
  public void reset() {
    super.reset();
    carrier = scarrier;
    dead = false;
  }
 
  @Override
  public void print() {
    if (dead) {
      System.err.print("Snaffle " +id + " dead");
    } else {
      System.err.print("Snaffle " + id + " " + position + " " + vx + " " + vy + " " + speed() + " " + " | ");;
      if (carrier != null) {
        System.err.print("Carrier " + carrier.id + " | ");
      }
    }
    System.err.println("");
  }

}
