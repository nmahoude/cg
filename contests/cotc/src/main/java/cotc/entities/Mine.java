package cotc.entities;

import java.util.List;

import cotc.utils.Coord;

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
                Coord impactPosition = null;
                Coord stern = ship.stern();
                Coord bow = ship.bow();
                if (stern.distanceTo(position) <= 1) {
                    impactPosition = stern;
                }
                if (bow.distanceTo(position) <= 1) {
                    impactPosition = bow;
                }
                if (ship.position.distanceTo(position) <= 1) {
                    impactPosition = ship.position;
                }

                if (impactPosition != null) {
                    ship.damage(NEAR_MINE_DAMAGE);
                    exploded = true;
                }
            }
        }
    }

    return exploded;
}
}
