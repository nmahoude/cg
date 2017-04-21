package cotc.ai.minimax;

import org.junit.Before;
import org.junit.Test;

import cotc.GameSituationTest;
import cotc.GameState;
import cotc.Team;
import cotc.entities.Barrel;
import cotc.entities.CannonBall;
import cotc.entities.EntityType;
import cotc.entities.Mine;
import cotc.entities.Ship;

public class MiniMaxTest {
  GameState state;
  
  @Before
  public void setup() {
    state = new GameState();
    state.teams[0] = new Team(0);
    state.teams[1] = new Team(1);
  }
  
  @Test
  public void shipExplodeOnMine() {
    readEntity(0,SHIP,14,14,3,1,100,1);
    readEntity(1,SHIP,15,5,5,0,100,0);
    readEntity(5,MINE,11,15,0,0,0,0);
    readEntity(10,MINE,6,3,0,0,0,0);
    readEntity(47,CANNONBALL,18,10,3,1,0,0);
    readEntity(49,CANNONBALL,15,12,3,3,0,0);
    state.backup();

    MiniMax minimax = new MiniMax();
    minimax.setState(state);
    minimax.evolve();
  
  }
  
  EntityType SHIP = EntityType.SHIP;
  EntityType MINE = EntityType.MINE;
  EntityType BARREL = EntityType.BARREL;
  EntityType CANNONBALL= EntityType.CANNONBALL;
  private void readEntity(int entityId, EntityType entityType, int x, int y, int arg1, int arg2, int arg3, int arg4) {
    GameSituationTest.readEntity(state, entityId, entityType, x, y, arg1, arg2, arg3, arg4);
  }
  
  public static void readEntity(GameState state, int entityId, EntityType entityType, int x, int y, int arg1, int arg2, int arg3, int arg4) {
    arg4 = 1-arg4; // arg4 == 1 -> owner = 0 !
    switch (entityType) {
      case SHIP:
        Ship ship = new Ship(entityId, x, y, arg1 /*orientation*/, arg4 /*owner*/);
        ship.update(x, y, arg1 /*orientation*/, arg2 /*speed*/, arg3 /*stock of rum*/, arg4 /*owner*/);
        state.updateShip(ship); // add in ships and shipsAlive of teams
        break;
      case BARREL:
        Barrel barrel = new Barrel  (entityId, x, y, arg1 /*rum in barrel*/);
        state.barrels.add(barrel);
        break;
      case CANNONBALL:
        Ship sender = state.getShip(state.ships, arg1);
        CannonBall ball = new CannonBall(entityId, x, y, sender /*sender entityId*/, arg2 /*turns*/);
        state.cannonballs.add(ball);
        break;
      case MINE:
        Mine mine = new Mine(entityId, x, y);
        state.mines.add(mine);
        break;
    }
  }
}
