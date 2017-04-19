package cotc;

import java.util.Random;
import java.util.Scanner;

import cotc.ai.ag.AG;
import cotc.ai.ag.AGSolution;
import cotc.ai.ag.Feature;
import cotc.entities.Barrel;
import cotc.entities.CannonBall;
import cotc.entities.Mine;
import cotc.entities.Ship;
import cotc.utils.Coord;

public class Player {
  private static boolean debugOutput = false;
  private static Coord coord = Coord.get(0, 0); // force Coord caches initialisation
  
  private static final int FIRE_COOLDOWN = 4;
  public static long startTime = 0;

  static Random rand = new Random();
  static GameState state;
  
  public static void main(String args[]) {
    state = new GameState();
    state.teams.add(new Team(0));
    state.teams.add(new Team(1));
    
    Scanner in = new Scanner(System.in);
    int round = 0;
    // game loop
    while (true) {
      round++;
      state.initRound();

      readState(in);
      // debugRumDomination();
      Feature feature= new Feature();
      feature.calculateFeatures(state);
      //feature.debug();
      
      //old AI (dummy with MOVE) doDirectAction();
      
      AG ag = new AG();
      ag.setState(state);
      AGSolution sol = (AGSolution)ag.evolve(startTime+ (round == 1 ? 800 : 44));
      if (debugOutput) {
        //sol.debugOutput();
      }
      
      String[] output = sol.output();
      for (int i=0;i<output.length;i++) {
        System.out.println(output[i]);
      }
    }
  }

  private static void debugRumDomination() {
    // debug rum domination
    BarrelDomination barrelDominitation = state.getBarrelDominitation();
    System.err.println("Rum dom : ");
    System.err.println("barrel0 : "+barrelDominitation.barrelCount0);
    System.err.println("barrel1 : "+barrelDominitation.barrelCount1);
    System.err.println("rum0 : "+barrelDominitation.rumCount0);
    System.err.println("rum1 : "+barrelDominitation.rumCount1);
  }

  private static void readState(Scanner in) {
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
          Ship ship = state.getShip(state.ships, entityId);
          if (ship == null) {
            ship = new Ship(entityId, x, y, arg1 /*orientation*/, arg4 /*owner*/);
            if (ship.owner == 0) {
              state.teams.get(0).ships.add(ship);
            } else {
              state.teams.get(1).ships.add(ship);
            }
          }
          ship.update(x, y, arg1 /*orientation*/, arg2 /*speed*/, arg3 /*stock of rum*/, arg4 /*owner*/);
          state.updateShip(ship); // add in ships and shipsAlive of teams
          break;
        case "BARREL":
          Barrel barrel = new Barrel  (entityId, x, y, arg1 /*rum in barrel*/);
          state.barrels.add(barrel);
          break;
        case "CANNONBALL":
          Ship sender = state.getShip(state.teams.get(0).ships, arg1);
          if (sender == null) {
            sender = state.getShip(state.teams.get(1).ships, arg1);
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
    state.backup();
  }
}
