package cotc;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import cotc.entities.Barrel;
import cotc.entities.CannonBall;
import cotc.entities.Entity;
import cotc.entities.Mine;
import cotc.entities.Ship;

public class Player {
  private static final int FIRE_COOLDOWN = 4;

  static List<Ship> myShips = new ArrayList<>();
  static List<Ship> otherShips = new ArrayList<>();
  static List<CannonBall> balls = new ArrayList<>();
  static List<Mine> mines = new ArrayList<>();
  static List<Barrel> barrels = new ArrayList<>();

  public static void main(String args[]) {
    Scanner in = new Scanner(System.in);
    int round = 0;
    // game loop
    while (true) {
      round++;
      clearRound();

      int myShipCount = in.nextInt(); // the number of remaining ships
      int entityCount = in.nextInt(); // the number of entities (e.g. ships,
                                      // mines or cannonballs)

      for (int i = 0; i < entityCount; i++) {
        int entityId = in.nextInt();
        String entityType = in.next();
        int x = in.nextInt();
        int y = in.nextInt();
        int arg1 = in.nextInt();
        int arg2 = in.nextInt();
        int arg3 = in.nextInt();
        int arg4 = in.nextInt();

        switch (entityType) {
          case "SHIP":
            Ship ship = null;
            if (arg4 == 1) {
              ship = getEntity(myShips, entityId);
            } else {
              ship = getEntity(otherShips, entityId);
            }
            if (ship == null) {
              ship = new Ship(entityId, x, y, arg1 /*orientation*/, arg4 /*owner*/);
              if (ship.owner == 1) {
                myShips.add(ship);
              } else {
                otherShips.add(ship);
              }
            }
            ship.update(x, y, arg1 /*orientation*/, arg2 /*speed*/, arg3 /*stock of rum*/, arg4 /*owner*/);
            break;
          case "BARREL":
            Barrel barrel = new Barrel  (entityId, x, y, arg1 /*rum in barrel*/);
            barrels.add(barrel);
            break;
          case "CANNONBALL":
            Ship sender = getEntity(myShips, arg1);
            if (sender == null) {
              sender = getEntity(otherShips, arg1);
            }
            CannonBall ball = new CannonBall(entityId, x, y, sender /*sender entityId*/, arg2 /*turns*/);
            balls.add(ball);
            break;
          case "MINE":
            Mine mine = new Mine(entityId, x, y);
            mines.add(mine);
            break;
        }
      }
      for (int i = 0; i < myShipCount; i++) {
        Ship ship = myShips.get(i);
        System.err.println("Ship "+i +" with playerId "+ship.owner);
        if (ship.cannonCooldown > 0)
          ship.cannonCooldown--;

        Ship enemy = getClosestEnnemy(ship);

        if (enemy != null && ship.health > 30 && ship.cannonCooldown == 0) {
          int distanceToTarget = enemy.position.distanceTo(ship.position);
          if (distanceToTarget < 7) {
            int travelTime = 1 + Math.round(ship.position.distanceTo(ship.position) / 3);
            // fire on other ship
            int deltax = 0;
            int deltay = 0;
            int speed = enemy.speed;
            switch (enemy.orientation) {
              case 0:
                deltax = travelTime;
                break;
              case 1:
                deltax = travelTime / 2;
                deltay = -travelTime / 2;
                break;
              case 2:
                deltax = -travelTime / 2;
                deltay = -travelTime / 2;
                break;
              case 3:
                deltax = -travelTime;
                break;
              case 4:
                deltax = -travelTime / 2;
                deltay = +travelTime / 2;
                break;
              case 5:
                deltax = travelTime / 2;
                deltay = travelTime / 2;
                break;
            }

            System.err.println("Fire on enemy "+enemy.id +" of "+ enemy.owner);
            System.out.println("FIRE " + (enemy.position.x + speed * deltax) + " " + (enemy.position.y + speed * deltay));
            ship.cannonCooldown = FIRE_COOLDOWN;
            continue;
          }
        }

        if (barrels.isEmpty()) {
          // wander
          if (barrels.isEmpty()) {
            System.err.println("no more barels, save the rhum ");
          }
          if (i == 0) {
            System.out.println("MOVE 11 10");
          } else if (i == 1) {
            System.out.println("MOVE 0 0");
          } else if (i == 2) {
            System.out.println("MOVE 23 21");
          }
        } else {
          Barrel barrel = getClosestBarrel(ship);
          System.out.println("MOVE " + barrel.position.x + " " + barrel.position.y);
        }
      }
    }
  }

  private static Barrel getClosestBarrel(Ship ship) {
    int bestDist = Integer.MAX_VALUE;
    Barrel best = null;
    for (Barrel barrel : barrels) {
      int dist = barrel.position.distanceTo(ship.position);
      if (dist < bestDist) {
        bestDist = dist;
        best = barrel;
      }
    }
    return best;
  }

  private static Ship getClosestEnnemy(Ship me) {
    int bestDist = Integer.MAX_VALUE;
    Ship best = null;
    for (Ship other : otherShips) {
      if (other.health == 0) continue;
      int dist = other.position.distanceTo(me.position);
      if (dist < bestDist) {
        bestDist = dist;
        best = other;
      }
    }
    return best;
  }

  private static <T> T getEntity(List<Ship> myShips2, int entityId) {
    for (Entity entity : myShips2) {
      if (entity.id == entityId) {
        return (T) entity;
      }
    }
    return null;
  }

  private static void clearRound() {
    // kill all ships ! (will be revive in the update process)
    for (Ship ship : myShips) {
      ship.health = 0;
    }
    for (Ship ship : otherShips) {
      ship.health = 0;
    }
    
    balls.clear();
    mines.clear();
    barrels.clear();
  }
}
