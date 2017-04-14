package cotc.entities;

import java.util.ArrayList;
import java.util.List;

import cotc.game.Damage;
import cotc.utils.Coord;

public class Mine extends Entity {
  private static final int MINE_VISIBILITY_RANGE = 5;
  private static final int MINE_DAMAGE = 25;
  private static final int NEAR_MINE_DAMAGE = 10;

  public Mine(int entityId, int x, int y) {
    super(EntityType.MINE, entityId, x, y);
    
    // no args
  }
  
  public String toPlayerString(int playerIdx) {
    return toPlayerString(0, 0, 0, 0);
}
  public List<Damage> explode(List<Ship> ships, boolean force) {
    List<Damage> damage = new ArrayList<>();
    Ship victim = null;

    for (Ship ship : ships) {
        if (position.equals(ship.bow()) || position.equals(ship.stern()) || position.equals(ship.position)) {
            damage.add(new Damage(this.position, MINE_DAMAGE, true));
            ship.damage(MINE_DAMAGE);
            victim = ship;
        }
    }

    if (force || victim != null) {
        if (victim == null) {
            damage.add(new Damage(this.position, MINE_DAMAGE, true));
        }

        for (Ship ship : ships) {
            if (ship != victim) {
                Coord impactPosition = null;
                if (ship.stern().distanceTo(position) <= 1) {
                    impactPosition = ship.stern();
                }
                if (ship.bow().distanceTo(position) <= 1) {
                    impactPosition = ship.bow();
                }
                if (ship.position.distanceTo(position) <= 1) {
                    impactPosition = ship.position;
                }

                if (impactPosition != null) {
                    ship.damage(NEAR_MINE_DAMAGE);
                    damage.add(new Damage(impactPosition, NEAR_MINE_DAMAGE, true));
                }
            }
        }
    }

    return damage;
}
}
