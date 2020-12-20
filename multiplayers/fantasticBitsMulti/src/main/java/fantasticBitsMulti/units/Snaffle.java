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
  public Collision wallCollision(double from) {
    if (carrier != null || dead) {
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

    double t;
    Wall wall;
    
    if (tx < ty) {
      wall = horizontalWall;
      t = tx + from;
    } else {
      wall = verticalWall;
      t = ty + from;
    }

    if (t <= 0.0 || t > 1.0) {
      return null;
    }


    return Simulation.collisionsCache[Simulation.collisionsCacheFE++].update(t, this, wall);
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
    if (u.type == EntityType.WALL) {
      if (Player.DEBUG_SIM) {
        System.err.println("Snaffle "+id+" bounce with wall @ "+position );
      }
      if (position.y >= 2050.0 && position.y <= 5450.0) {
        dead = true;

        Player.state.goal(position.x);
      } else {
        super.bounce(u);
      }
    } else if (u.type == EntityType.WIZARD) {
      Wizard target = (Wizard) u;
      if (target.snaffle == null && target.turnsBeforeGrabbingAgain == 0 && !dead && carrier == null) {
        target.grabSnaffle(this);
      }
    } else {
      super.bounce(u);
    }
  }

  @Override
  public void move(double t) {
    if (!dead && carrier == null) {
      super.move(t);
    }
  }

  @Override
  public void end() {
    if (dead) return;
    if (carrier == null) {
      super.end();
    } else {
      this.vx = carrier.vx;
      this.vy = carrier.vy;
      this.position = carrier.position;
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
