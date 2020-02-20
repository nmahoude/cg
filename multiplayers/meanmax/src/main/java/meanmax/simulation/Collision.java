package meanmax.simulation;

import meanmax.Game;
import meanmax.entities.Entity;
import meanmax.entities.Tanker;

public class Collision {
  public static final Collision NO_COLLISION;
  static {
    NO_COLLISION = new Collision();
    NO_COLLISION.update(2.0, null);
  }

  public final void update (double t, Entity entity) {
    this.t = t;
    this.a = entity;
    this.b = null;
  }

  
  public final void update(double t, Entity a, Entity b) {
    this.t =t;
    this.a = a;
    this.b = b;
  }


  public double t;
  public Entity a;
  public Entity b;
  
  Tanker getDeadTankerInCollision() {
    if (a.type == Game.DESTROYER && b.type == Game.TANKER && b.mass < Game.REAPER_SKILL_MASS_BONUS) {
      return (Tanker) b;
    }

    if (b.type == Game.DESTROYER && a.type == Game.TANKER && a.mass < Game.REAPER_SKILL_MASS_BONUS) {
      return (Tanker) a;
    }

    return null;
  }
}
