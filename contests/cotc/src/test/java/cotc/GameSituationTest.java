package cotc;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Before;
import org.junit.Test;

import cotc.ai.AISolution;
import cotc.ai.ag.AG;
import cotc.ai.ag.ShipActions;
import cotc.entities.Action;
import cotc.entities.Barrel;
import cotc.entities.CannonBall;
import cotc.entities.EntityType;
import cotc.entities.Mine;
import cotc.entities.Ship;
import cotc.game.Simulation;
import cotc.utils.Coord;

public class GameSituationTest {
  GameState state;
  
  @Before
  public void setup() {
    state = new GameState();
    state.teams[0] = new Team(0);
    state.teams[1] = new Team(1);
  }
  
  @Test
  public void shipExplodeOnMine() {
    readEntity(0,SHIP,14,14,3,2,28,1);
    readEntity(3,SHIP,15,5,5,1,76,0);
    readEntity(5,MINE,11,15,0,0,0,0);
    readEntity(10,MINE,6,3,0,0,0,0);
    readEntity(47,CANNONBALL,18,10,3,1,0,0);
    readEntity(49,CANNONBALL,15,12,3,3,0,0);
    state.backup();

    Simulation simulation = new Simulation(state);
    
    Ship myShip = state.teams[0].shipsAlive.get(0);
    myShip.action = Action.PORT;
    
    simulation.playOneTurn();

    assertThat(myShip.position, is(Coord.get(12,14)));
    assertThat(myShip.health, is(2));
  }
  
  @Test
  public void AIsendsShipsOnMine() throws Exception {
    readEntity(0,SHIP,4,5,1,0,100,1);
    readEntity(2,SHIP,16,4,4,0,100,1);
    readEntity(1,SHIP,4,15,5,0,100,0);
    readEntity(3,SHIP,16,16,2,0,100,0);
    readEntity(4,MINE,11,5,0,0,0,0);
    readEntity(6,MINE,7,5,0,0,0,0);
    readEntity(8,MINE,5,8,0,0,0,0);
    readEntity(10,MINE,6,3,0,0,0,0);
    readEntity(15,BARREL,18,15,13,0,0,0);
    readEntity(14,BARREL,18,5,13,0,0,0);
    readEntity(17,BARREL,6,13,14,0,0,0);
    readEntity(16,BARREL,6,7,14,0,0,0);
    readEntity(19,BARREL,20,12,20,0,0,0);
    readEntity(18,BARREL,20,8,20,0,0,0);
    readEntity(21,BARREL,18,17,15,0,0,0);
    readEntity(20,BARREL,18,3,15,0,0,0);
    readEntity(22,BARREL,15,10,17,0,0,0);
    readEntity(24,BARREL,20,14,10,0,0,0);
    readEntity(23,BARREL,20,6,10,0,0,0);
    readEntity(26,BARREL,4,18,10,0,0,0);
    readEntity(25,BARREL,4,2,10,0,0,0);
    readEntity(28,BARREL,18,13,18,0,0,0);
    readEntity(27,BARREL,18,7,18,0,0,0);
    readEntity(30,BARREL,14,13,13,0,0,0);
    readEntity(29,BARREL,14,7,13,0,0,0);
    readEntity(32,BARREL,17,13,15,0,0,0);
    readEntity(31,BARREL,17,7,15,0,0,0);
    state.backup();

    Simulation simulation = new Simulation(state);
    Ship myShip = state.teams[0].shipsAlive.get(0);
    myShip.action = Action.STARBOARD;
    simulation.playOneTurn();
    myShip.action = Action.FASTER;
    simulation.playOneTurn();
    myShip.action = Action.MINE;
    simulation.playOneTurn();

    assertThat(myShip.position, is(Coord.get(6,5)));
    assertThat(myShip.health, is(72));
  }
  
  @Test
  public void AISendsBoatOnMine_Reboot() throws Exception {
    readEntity(2,SHIP,14,15,0,2,34,1);
    readEntity(1,SHIP,11,7,5,1,61,0);
    readEntity(6,MINE,17,17,0,0,0,0);
    readEntity(10,MINE,13,17,0,0,0,0);
    readEntity(9,MINE,13,3,0,0,0,0);
    readEntity(79,MINE,5,1,0,0,0,0);
    readEntity(85,MINE,16,19,0,0,0,0);
    readEntity(94,CANNONBALL,12,9,1,1,0,0);
    state.backup();

    Simulation simulation = new Simulation(state);
    Ship myShip = state.teams[0].shipsAlive.get(0);
    
    Action actions[] = new Action[] { Action.STARBOARD, Action.SLOWER, Action.STARBOARD, Action.WAIT, Action.FASTER};
    for (Action action : actions) {
      myShip.action = action;
      simulation.playOneTurn();
    }

    assertThat(myShip.health, is(0));
  }
  
  @Test
  public void getACannonBall() throws Exception {
    readEntity(0,SHIP,11,5,0,0,89,1);
    readEntity(1,SHIP,1,9,2,2,86,0);
    readEntity(40,CANNONBALL,11,5,1,2,0,0);
    state.backup();

    Simulation simulation = new Simulation(state);
    Ship myShip = state.teams[0].shipsAlive.get(0);
    Ship hisShip = state.teams[1].shipsAlive.get(0);
    
    Action myActions[] = new Action[] { Action.WAIT, Action.WAIT};
    for (Action action : myActions) {
      myShip.action = action;
      simulation.playOneTurn();
    }
    assertThat(myShip.health, is(37));
  }
  
  @Test
  public void whyIsGoingOnMineTheBestMove() throws Exception {
    readEntity(0,SHIP,19,5,5,2,15,1);
    readEntity(2,SHIP,12,16,2,0,65,1);
    readEntity(4,SHIP,20,16,3,1,51,1);
    readEntity(1,SHIP,13,12,2,0,85,0);
    readEntity(3,SHIP,8,13,4,2,81,0);
    readEntity(5,SHIP,6,20,3,2,78,0);
    readEntity(7,MINE,21,18,0,0,0,0);
    readEntity(6,MINE,21,2,0,0,0,0);
    readEntity(9,MINE,8,17,0,0,0,0);
    readEntity(11,MINE,14,17,0,0,0,0);
    readEntity(30,MINE,20,2,0,0,0,0);
    readEntity(49,MINE,14,14,0,0,0,0);
    readEntity(64,MINE,11,14,0,0,0,0);
    readEntity(61,CANNONBALL,13,5,5,0,0,0);
    readEntity(62,CANNONBALL,11,5,3,0,0,0);
    readEntity(65,CANNONBALL,11,14,1,1,0,0);
    readEntity(66,CANNONBALL,11,14,3,1,0,0);
    readEntity(67,CANNONBALL,12,16,5,3,0,0);

    state.backup();

    Simulation simulation = new Simulation(state);
    Ship myShip = state.teams[0].shipsAlive.get(1);
    Ship hisShip = state.teams[1].shipsAlive.get(0);
    
    Action myActions[] = new Action[] { Action.WAIT};
    for (Action action : myActions) {
      myShip.action = action;
      simulation.playOneTurn();
    }
    assertThat(myShip.health, is(54));
  }
  
  @Test
  public void cannonBallWillHitMyShipAtCenter() throws Exception {
    readEntity(0,SHIP,6,7,0,1,83,1);
    readEntity(2,SHIP,6,17,2,2,50,1);
    readEntity(4,SHIP,0,0,4,0,25,1);
    readEntity(1,SHIP,8,7,4,1,46,0);
    readEntity(3,SHIP,2,10,0,2,84,0);
    readEntity(5,SHIP,7,13,0,1,81,0);
    readEntity(9,MINE,8,17,0,0,0,0);
    readEntity(8,MINE,8,3,0,0,0,0);
    readEntity(13,MINE,5,18,0,0,0,0);
    readEntity(12,MINE,5,2,0,0,0,0);
    readEntity(14,MINE,2,8,0,0,0,0);
    readEntity(36,MINE,10,7,0,0,0,0);
    readEntity(51,MINE,4,6,0,0,0,0);
    readEntity(63,MINE,3,7,0,0,0,0);
    readEntity(62,CANNONBALL,6,7,1,1,0,0);
    
    state.backup();

    Simulation simulation = new Simulation(state);
    Ship myShip = state.teams[0].shipsAlive.get(0);
    
    Action myActions[] = new Action[] { Action.WAIT};
    for (Action action : myActions) {
      myShip.action = action;
      simulation.playOneTurn();
    }
    assertThat(myShip.health, is(32));
    
  }
  
  
  @Test
  public void IASendMeOnMine() throws Exception {
    readEntity(0,SHIP,20,8,5,1,100,1);
    readEntity(1,SHIP,18,4,5,1,100,0);
    state.backup();

    AG ag = new AG();
    ag.setState(state);
    AISolution solution = ag.evolve(10000);
    
    ShipActions[] actions = solution.getActionsNew();
    assertThat(actions[0].actions[0].action, is (Action.MINE));
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
