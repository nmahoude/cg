package cotc;

import java.util.Random;
import java.util.Scanner;

import cgcollections.arrays.FastArray;
import cotc.ai.ag.AG;
import cotc.ai.ag.AGSolution;
import cotc.ai.ag.Feature;
import cotc.entities.Action;
import cotc.entities.Barrel;
import cotc.entities.CannonBall;
import cotc.entities.Entity;
import cotc.entities.EntityType;
import cotc.entities.Mine;
import cotc.entities.Ship;
import cotc.game.Simulation;
import cotc.utils.Coord;

public class Player {
  private static boolean debugOutput = false;
  
  public static Random rand = new Random(System.currentTimeMillis());
  static GameState state;
  static FastArray<Ship> shipsRoundBackup = new FastArray<>(Ship.class, 6);
  static Coord[] lastSternPosition = new Coord[6];
  public static long startTime = 0;
  
  
  public static void main(String args[]) {
    state = new GameState();
    state.teams[0] = new Team(0);
    state.teams[1] = new Team(1);
    
    Scanner in = new Scanner(System.in);
    int round = 0;
    // game loop
    while (true) {
      round++;
      shipsRoundBackup.copyFrom(state.ships);
      state.initRound();
      readState(in);
      
      doMineDetection();
      
      Feature feature= new Feature();
      feature.calculateFeaturesFinal(state);
      //feature.debug();
      
      AG ag = new AG();
      ag.setState(state);
      AGSolution bestSol = (AGSolution)ag.evolve(startTime+ (round == 1 ? 800 : 44));
      //bestSol.feature.debugFeature(ag.weights);

      String[] output = bestSol.output();
      for (int s=0;s<state.teams[0].shipsAlive.length;s++) {
        Ship ship = state.teams[0].shipsAlive.elements[s];
        if (bestSol.actions.elements[0+s*AGSolution.DEPTH].action == Action.FIRE) {
          ship.cannonCooldown = Simulation.COOLDOWN_CANNON;
        } else if (bestSol.actions.elements[0+s*AGSolution.DEPTH].action == Action.MINE) {
          ship.mineCooldown = Simulation.COOLDOWN_MINE;
        }
        System.out.println(output[s]);
      }
    }
  }

  /**
   * Be aware of FOG of WAR !!!!!
   * ships can drop mines that we don't see !!!
   */
  private static void doMineDetection() {
    for (int s=0;s<state.ships.length;s++) {
      Ship ship = state.ships.elements[s];
      if (ship.owner == 1 && ship.mineCooldown > 0) {
        System.err.println("minecooldonw "+ship.mineCooldown);
      }
      Coord coord = lastSternPosition[ship.id];
      if (coord != null) {
        Entity entityAtCoord = state.getEntityAt(coord);
        if (entityAtCoord != null && entityAtCoord.type == EntityType.MINE) {
          ship.mineCooldown = Simulation.COOLDOWN_MINE-1;
          ship.b_mineCooldown = Simulation.COOLDOWN_MINE-1; // don't forget to update backup too, state is already saved
        }
      }
      // update for next turn
      if (ship.mineCooldown == 0 && ship.canDropBomb(state)) {
        Coord target = ship.stern().neighbor((ship.orientation + 3) % 6);
        lastSternPosition[ship.id] = target;
      } else {
        lastSternPosition[ship.id] = null;
      }
    }
  }
  
  private static void readState(Scanner in) {
    // free stuff ?
    state.shipCount = in.nextInt();
    int entityCount = in.nextInt();
    startTime =System.currentTimeMillis();

    for (int i = 0; i < entityCount; i++) {
      int entityId = in.nextInt();
      String entityType = in.next();
      int x = in.nextInt();
      int y = in.nextInt();
      int arg1 = in.nextInt();
      int arg2 = in.nextInt();
      int arg3 = in.nextInt();
      int arg4 = in.nextInt();

      if (debugOutput) {
        System.err.println("readEntity("+entityId+","+entityType+","+x+","+y+","+arg1+","+arg2+","+arg3+","+arg4+");");
      }
      arg4 = 1-arg4; // arg4 == 1 -> owner = 0 !
      
      switch (entityType) {
        case "SHIP":
          Ship ship = state.getShip(shipsRoundBackup, entityId);
          if (ship == null) {
            ship = new Ship(entityId, x, y, arg1 /*orientation*/, arg4 /*owner*/);
          }
          ship.update(x, y, arg1 /*orientation*/, arg2 /*speed*/, arg3 /*stock of rum*/, arg4 /*owner*/);

          state.updateShip(ship); // add in ships and shipsAlive of teams
          break;
        case "BARREL":
          Barrel barrel = new Barrel  (entityId, x, y, arg1 /*rum in barrel*/);
          state.barrels.add(barrel);
          break;
        case "CANNONBALL":
          Ship sender = state.getShip(state.ships, arg1);
          CannonBall ball = new CannonBall(entityId, x, y, sender /*sender entityId*/, arg2 /*turns*/);
          state.cannonballs.add(ball);
          break;
        case "MINE":
          Mine mine = new Mine(entityId, x, y);
          state.mines.add(mine);
          break;
      }
    }
    state.backup();
  }
}
