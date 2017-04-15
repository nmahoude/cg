package cotc;

import java.util.Random;
import java.util.Scanner;

import cotc.entities.Barrel;
import cotc.entities.CannonBall;
import cotc.entities.Mine;
import cotc.entities.Ship;

public class Player {
  private static final int FIRE_COOLDOWN = 4;

  static Random rand = new Random();
  static GameState state;
  
  public static void main(String args[]) {
    state = new GameState();
    
    Scanner in = new Scanner(System.in);
    int round = 0;
    // game loop
    while (true) {
      round++;
      state.initRound();

      readState(in);
      
      //state.backup();
      
      doDirectAction();
    }
  }

  private static void readState(Scanner in) {
    state.shipCount = in.nextInt();
    int entityCount = in.nextInt();


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
            ship = state.getShip(state.myShips, entityId);
          } else {
            ship = state.getShip(state.otherShips, entityId);
          }
          if (ship == null) {
            ship = new Ship(entityId, x, y, arg1 /*orientation*/, arg4 /*owner*/);
            state.ships.add(ship);
            if (ship.owner == 1) {
              state.myShips.add(ship);
            } else {
              state.otherShips.add(ship);
            }
          }
          ship.update(x, y, arg1 /*orientation*/, arg2 /*speed*/, arg3 /*stock of rum*/, arg4 /*owner*/);
          break;
        case "BARREL":
          Barrel barrel = new Barrel  (entityId, x, y, arg1 /*rum in barrel*/);
          state.barrels.add(barrel);
          break;
        case "CANNONBALL":
          Ship sender = state.getShip(state.myShips, arg1);
          if (sender == null) {
            sender = state.getShip(state.otherShips, arg1);
          }
          CannonBall ball = new CannonBall(entityId, x, y, sender /*sender entityId*/, arg2 /*turns*/);
          state.cannonballs.add(ball);
          break;
        case "MINE":
          Mine mine = new Mine(entityId, x, y);
          state.mines.add(mine);
          break;
      }
    }
  }

  private static void doDirectAction() {
    for (int i = 0; i < state.shipCount; i++) {
      Ship ship = state.myShips.get(i);
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

      if (state.barrels.isEmpty()) {
        // wander
        if (state.barrels.isEmpty()) {
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
        if (ship.health < 50) {
          Barrel barrel = getClosestBarrel(ship);
          System.out.println("MOVE " + barrel.position.x + " " + barrel.position.y);
        } else {
          System.out.println("MOVE " + rand.nextInt(23) + " " + rand.nextInt(21));
        }
      }
    }
  }

  private static Barrel getClosestBarrel(Ship ship) {
    int bestDist = Integer.MAX_VALUE;
    Barrel best = null;
    for (Barrel barrel : state.barrels) {
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
    for (Ship other : state.otherShips) {
      if (other.health == 0) continue;
      int dist = other.position.distanceTo(me.position);
      if (dist < bestDist) {
        bestDist = dist;
        best = other;
      }
    }
    return best;
  }
}
