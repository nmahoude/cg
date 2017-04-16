package cotc.entities;

import java.util.List;

public class Mine extends Entity {
  private static final int MINE_DAMAGE = 25;
  private static final int NEAR_MINE_DAMAGE = 10;

  public Mine(int entityId, int x, int y) {
    super(EntityType.MINE, entityId, x, y);

    // no args
  }

  public String toPlayerString(int playerIdx) {
    return toPlayerString(0, 0, 0, 0);
  }

  public boolean explode(List<Ship> ships, boolean force) {
    Ship victim = null;
    boolean exploded = false;

    for (Ship ship : ships) {
      if (position == ship.bow() || position == ship.stern() || position == ship.position) {
        exploded = true;
        ship.damage(MINE_DAMAGE);
        victim = ship;
      }
    }

    if (force || victim != null) {
      if (victim == null) {
        exploded = true;
      }

      for (Ship ship : ships) {
        if (ship != victim) {
          boolean impact = false;
          int distanceToCenter = ship.position.distanceTo(position);
          // TODO check that this is sufficient for no mine collision
          if (distanceToCenter > 3) continue; 
          
          if (distanceToCenter <= 1) {
            impact = true;
          } else if (ship.stern().distanceTo(position) <= 1) {
            impact = true;
          } else if (ship.bow().distanceTo(position) <= 1) {
            impact = true;
          }

          if (impact) {
            ship.damage(NEAR_MINE_DAMAGE);
            exploded = true;
          }
        }
      }
    }

    return exploded;
  }
}
