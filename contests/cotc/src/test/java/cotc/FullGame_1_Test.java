package cotc;

import static cotc.entities.Action.FASTER;
import static cotc.entities.Action.FIRE;
import static cotc.entities.Action.PORT;
import static cotc.entities.Action.SLOWER;
import static cotc.entities.Action.STARBOARD;
import static cotc.entities.Action.WAIT;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Before;
import org.junit.Test;

import cotc.entities.Action;
import cotc.entities.CannonBall;
import cotc.entities.EntityType;
import cotc.entities.Ship;
import cotc.game.Simulation;
import cotc.utils.Coord;

public class FullGame_1_Test {
  GameState state;
  private int actionIndex;

  @Before
  public void setup() {
    state = new GameState();
    state.teams[0] = new Team(0);
    state.teams[1] = new Team(1);
  }

  @Test
  /**
    mineCount=8
    seed=729280310
    barrelCount=19
    shipsPerPlayer=3
   */
  public void round1() {
    Simulation simulation = new Simulation(state);
    // ** TURN1
    readEntity(0, SHIP, 1, 1, 4, 0, 100, 1);
    readEntity(2, SHIP, 8, 6, 1, 0, 100, 1);
    readEntity(4, SHIP, 16, 4, 4, 0, 100, 1);
    readEntity(1, SHIP, 1, 19, 2, 0, 100, 0);
    readEntity(3, SHIP, 8, 14, 5, 0, 100, 0);
    readEntity(5, SHIP, 16, 16, 2, 0, 100, 0);
    readEntity(6, MINE, 6, 7, 0, 0, 0, 0);
    readEntity(7, MINE, 6, 13, 0, 0, 0, 0);
    readEntity(8, MINE, 4, 5, 0, 0, 0, 0);
    readEntity(9, MINE, 4, 15, 0, 0, 0, 0);
    readEntity(10, MINE, 18, 9, 0, 0, 0, 0);
    readEntity(11, MINE, 18, 11, 0, 0, 0, 0);
    readEntity(12, MINE, 9, 9, 0, 0, 0, 0);
    readEntity(13, MINE, 9, 11, 0, 0, 0, 0);
    readEntity(15, BARREL, 13, 7, 10, 0, 0, 0);
    readEntity(16, BARREL, 13, 13, 10, 0, 0, 0);
    readEntity(17, BARREL, 11, 9, 13, 0, 0, 0);
    readEntity(18, BARREL, 11, 11, 13, 0, 0, 0);
    readEntity(19, BARREL, 12, 4, 16, 0, 0, 0);
    readEntity(20, BARREL, 12, 16, 16, 0, 0, 0);
    readEntity(21, BARREL, 3, 10, 14, 0, 0, 0);
    readEntity(22, BARREL, 17, 4, 20, 0, 0, 0);
    readEntity(23, BARREL, 17, 16, 20, 0, 0, 0);
    readEntity(25, BARREL, 1, 9, 10, 0, 0, 0);
    readEntity(26, BARREL, 1, 11, 10, 0, 0, 0);
    readEntity(27, BARREL, 15, 7, 11, 0, 0, 0);
    readEntity(28, BARREL, 15, 13, 11, 0, 0, 0);
    readEntity(31, BARREL, 8, 3, 19, 0, 0, 0);
    readEntity(32, BARREL, 8, 17, 19, 0, 0, 0);
    readEntity(33, BARREL, 19, 2, 11, 0, 0, 0);
    readEntity(34, BARREL, 19, 18, 11, 0, 0, 0);
    readEntity(35, BARREL, 19, 9, 11, 0, 0, 0);
    readEntity(36, BARREL, 19, 11, 11, 0, 0, 0);
    state.backup();
    resetActions();
    // ** nmahoude:
    doAction(FASTER);
    doAction(FASTER);
    doAction(FASTER);
    // ** Royale:
    doAction(STARBOARD);
    doAction(PORT);
    doAction(FASTER);
    
        simulation.playOneTurn(); // ** TURN2
    checkEntity(0, SHIP, 1, 2, 4, 1, 99, 1);
    checkEntity(2, SHIP, 8, 5, 1, 1, 99, 1);
    checkEntity(4, SHIP, 15, 5, 4, 1, 99, 1);
    checkEntity(1, SHIP, 1, 19, 1, 0, 99, 0);
    checkEntity(3, SHIP, 8, 14, 0, 0, 99, 0);
    checkEntity(5, SHIP, 15, 15, 2, 1, 99, 0);
    checkEntity(6, MINE, 6, 7, 0, 0, 0, 0);
    checkEntity(8, MINE, 4, 5, 0, 0, 0, 0);
    checkEntity(10, MINE, 18, 9, 0, 0, 0, 0);
    checkEntity(12, MINE, 9, 9, 0, 0, 0, 0);
    checkEntity(16, BARREL, 13, 13, 10, 0, 0, 0);
    checkEntity(15, BARREL, 13, 7, 10, 0, 0, 0);
    checkEntity(18, BARREL, 11, 11, 13, 0, 0, 0);
    checkEntity(17, BARREL, 11, 9, 13, 0, 0, 0);
    checkEntity(20, BARREL, 12, 16, 16, 0, 0, 0);
    checkEntity(19, BARREL, 12, 4, 16, 0, 0, 0);
    checkEntity(21, BARREL, 3, 10, 14, 0, 0, 0);
    checkEntity(23, BARREL, 17, 16, 20, 0, 0, 0);
    checkEntity(22, BARREL, 17, 4, 20, 0, 0, 0);
    checkEntity(26, BARREL, 1, 11, 10, 0, 0, 0);
    checkEntity(25, BARREL, 1, 9, 10, 0, 0, 0);
    checkEntity(28, BARREL, 15, 13, 11, 0, 0, 0);
    checkEntity(27, BARREL, 15, 7, 11, 0, 0, 0);
    checkEntity(32, BARREL, 8, 17, 19, 0, 0, 0);
    checkEntity(31, BARREL, 8, 3, 19, 0, 0, 0);
    checkEntity(34, BARREL, 19, 18, 11, 0, 0, 0);
    checkEntity(33, BARREL, 19, 2, 11, 0, 0, 0);
    checkEntity(36, BARREL, 19, 11, 11, 0, 0, 0);
    checkEntity(35, BARREL, 19, 9, 11, 0, 0, 0);
    resetActions(); // 2289
    // ** nmahoude:
    doAction(WAIT);
    doAction(WAIT);
    doAction(WAIT);
    // ** Royale:
    doAction(FASTER);
    doAction(FASTER);
    doAction(FASTER);

        simulation.playOneTurn(); // ** TURN3
    checkEntity(0, SHIP, 0, 3, 4, 1, 98, 1);
    checkEntity(2, SHIP, 9, 4, 1, 1, 98, 1);
    checkEntity(4, SHIP, 15, 6, 4, 1, 98, 1);
    checkEntity(1, SHIP, 2, 18, 1, 1, 98, 0);
    checkEntity(3, SHIP, 9, 14, 0, 1, 98, 0);
    checkEntity(5, SHIP, 14, 13, 2, 2, 98, 0);
    checkEntity(6, MINE, 6, 7, 0, 0, 0, 0);
    checkEntity(8, MINE, 4, 5, 0, 0, 0, 0);
    checkEntity(10, MINE, 18, 9, 0, 0, 0, 0);
    checkEntity(12, MINE, 9, 9, 0, 0, 0, 0);
    checkEntity(16, BARREL, 13, 13, 10, 0, 0, 0);
    checkEntity(15, BARREL, 13, 7, 10, 0, 0, 0);
    checkEntity(18, BARREL, 11, 11, 13, 0, 0, 0);
    checkEntity(17, BARREL, 11, 9, 13, 0, 0, 0);
    checkEntity(20, BARREL, 12, 16, 16, 0, 0, 0);
    checkEntity(19, BARREL, 12, 4, 16, 0, 0, 0);
    checkEntity(21, BARREL, 3, 10, 14, 0, 0, 0);
    checkEntity(23, BARREL, 17, 16, 20, 0, 0, 0);
    checkEntity(22, BARREL, 17, 4, 20, 0, 0, 0);
    checkEntity(26, BARREL, 1, 11, 10, 0, 0, 0);
    checkEntity(25, BARREL, 1, 9, 10, 0, 0, 0);
    checkEntity(28, BARREL, 15, 13, 11, 0, 0, 0);
    checkEntity(27, BARREL, 15, 7, 11, 0, 0, 0);
    checkEntity(32, BARREL, 8, 17, 19, 0, 0, 0);
    checkEntity(31, BARREL, 8, 3, 19, 0, 0, 0);
    checkEntity(34, BARREL, 19, 18, 11, 0, 0, 0);
    checkEntity(33, BARREL, 19, 2, 11, 0, 0, 0);
    checkEntity(36, BARREL, 19, 11, 11, 0, 0, 0);
    checkEntity(35, BARREL, 19, 9, 11, 0, 0, 0);
    resetActions(); // 2816
    // ** nmahoude:
    doAction(PORT);
    doAction(PORT);
    doAction(WAIT);
    // ** Royale:
    doAction(FASTER);
    doAction(FASTER);
    doAction(PORT);
        simulation.playOneTurn(); 
        // ** TURN4
    checkEntity(0, SHIP, 0, 4, 5, 1, 97, 1);
    checkEntity(2, SHIP, 9, 3, 2, 1, 97, 1);
    checkEntity(4, SHIP, 14, 7, 4, 1, 97, 1);
    checkEntity(1, SHIP, 3, 16, 1, 2, 97, 0);
    checkEntity(3, SHIP, 11, 14, 0, 2, 97, 0);
    checkEntity(5, SHIP, 13, 11, 3, 2, 97, 0);
    checkEntity(6, MINE, 6, 7, 0, 0, 0, 0);
    checkEntity(8, MINE, 4, 5, 0, 0, 0, 0);
    checkEntity(10, MINE, 18, 9, 0, 0, 0, 0);
    checkEntity(16, BARREL, 13, 13, 10, 0, 0, 0);
    checkEntity(15, BARREL, 13, 7, 10, 0, 0, 0);
    checkEntity(18, BARREL, 11, 11, 13, 0, 0, 0);
    checkEntity(17, BARREL, 11, 9, 13, 0, 0, 0);
    checkEntity(20, BARREL, 12, 16, 16, 0, 0, 0);
    checkEntity(19, BARREL, 12, 4, 16, 0, 0, 0);
    checkEntity(21, BARREL, 3, 10, 14, 0, 0, 0);
    checkEntity(23, BARREL, 17, 16, 20, 0, 0, 0);
    checkEntity(22, BARREL, 17, 4, 20, 0, 0, 0);
    checkEntity(26, BARREL, 1, 11, 10, 0, 0, 0);
    checkEntity(25, BARREL, 1, 9, 10, 0, 0, 0);
    checkEntity(28, BARREL, 15, 13, 11, 0, 0, 0);
    checkEntity(27, BARREL, 15, 7, 11, 0, 0, 0);
    checkEntity(32, BARREL, 8, 17, 19, 0, 0, 0);
    checkEntity(31, BARREL, 8, 3, 19, 0, 0, 0);
    checkEntity(34, BARREL, 19, 18, 11, 0, 0, 0);
    checkEntity(33, BARREL, 19, 2, 11, 0, 0, 0);
    checkEntity(36, BARREL, 19, 11, 11, 0, 0, 0);
    checkEntity(35, BARREL, 19, 9, 11, 0, 0, 0);
    resetActions(); // 2685
    // ** nmahoude:
    doAction(FASTER);
    doAction(SLOWER);
    doAction(SLOWER);
    // ** Royale:
    doAction(STARBOARD);
    doAction(PORT);
    doAction(SLOWER);
        simulation.playOneTurn(); // ** TURN5
    checkEntity(0, SHIP, 1, 6, 5, 2, 96, 1);
    checkEntity(2, SHIP, 9, 3, 2, 0, 96, 1);
    checkEntity(4, SHIP, 14, 7, 4, 0, 96, 1);
    checkEntity(1, SHIP, 4, 14, 0, 2, 96, 0);
    checkEntity(3, SHIP, 13, 14, 1, 2, 100, 0);
    checkEntity(5, SHIP, 12, 11, 3, 1, 100, 0);
    checkEntity(6, MINE, 6, 7, 0, 0, 0, 0);
    checkEntity(8, MINE, 4, 5, 0, 0, 0, 0);
    checkEntity(10, MINE, 18, 9, 0, 0, 0, 0);
    checkEntity(15, BARREL, 13, 7, 10, 0, 0, 0);
    checkEntity(17, BARREL, 11, 9, 13, 0, 0, 0);
    checkEntity(20, BARREL, 12, 16, 16, 0, 0, 0);
    checkEntity(19, BARREL, 12, 4, 16, 0, 0, 0);
    checkEntity(21, BARREL, 3, 10, 14, 0, 0, 0);
    checkEntity(23, BARREL, 17, 16, 20, 0, 0, 0);
    checkEntity(22, BARREL, 17, 4, 20, 0, 0, 0);
    checkEntity(26, BARREL, 1, 11, 10, 0, 0, 0);
    checkEntity(25, BARREL, 1, 9, 10, 0, 0, 0);
    checkEntity(28, BARREL, 15, 13, 11, 0, 0, 0);
    checkEntity(27, BARREL, 15, 7, 11, 0, 0, 0);
    checkEntity(32, BARREL, 8, 17, 19, 0, 0, 0);
    checkEntity(31, BARREL, 8, 3, 19, 0, 0, 0);
    checkEntity(34, BARREL, 19, 18, 11, 0, 0, 0);
    checkEntity(33, BARREL, 19, 2, 11, 0, 0, 0);
    checkEntity(36, BARREL, 19, 11, 11, 0, 0, 0);
    checkEntity(35, BARREL, 19, 9, 11, 0, 0, 0);
    resetActions(); // 2834
    // ** nmahoude:
    doAction(STARBOARD);
    doAction(WAIT);
    doAction(STARBOARD);
    // ** Royale:
    doAction(FASTER);
    doAction(FASTER);
    doAction(STARBOARD);
        simulation.playOneTurn(); // ** TURN6
    checkEntity(0, SHIP, 2, 8, 4, 2, 100, 1);
    checkEntity(2, SHIP, 9, 3, 2, 0, 95, 1);
    checkEntity(4, SHIP, 14, 7, 3, 0, 100, 1);
    checkEntity(1, SHIP, 6, 14, 0, 2, 95, 0);
    checkEntity(3, SHIP, 14, 12, 1, 2, 99, 0);
    checkEntity(5, SHIP, 11, 11, 2, 1, 99, 0);
    checkEntity(6, MINE, 6, 7, 0, 0, 0, 0);
    checkEntity(8, MINE, 4, 5, 0, 0, 0, 0);
    checkEntity(10, MINE, 18, 9, 0, 0, 0, 0);
    checkEntity(17, BARREL, 11, 9, 13, 0, 0, 0);
    checkEntity(20, BARREL, 12, 16, 16, 0, 0, 0);
    checkEntity(19, BARREL, 12, 4, 16, 0, 0, 0);
    checkEntity(21, BARREL, 3, 10, 14, 0, 0, 0);
    checkEntity(23, BARREL, 17, 16, 20, 0, 0, 0);
    checkEntity(22, BARREL, 17, 4, 20, 0, 0, 0);
    checkEntity(26, BARREL, 1, 11, 10, 0, 0, 0);
    checkEntity(28, BARREL, 15, 13, 11, 0, 0, 0);
    checkEntity(32, BARREL, 8, 17, 19, 0, 0, 0);
    checkEntity(31, BARREL, 8, 3, 19, 0, 0, 0);
    checkEntity(34, BARREL, 19, 18, 11, 0, 0, 0);
    checkEntity(33, BARREL, 19, 2, 11, 0, 0, 0);
    checkEntity(36, BARREL, 19, 11, 11, 0, 0, 0);
    checkEntity(35, BARREL, 19, 9, 11, 0, 0, 0);
    resetActions(); // 2702
    // ** nmahoude:
    doAction(WAIT);
    doAction(PORT);
    doAction(cotc.entities.Action.MINE);
    // ** Royale:
    doAction(cotc.entities.Action.MINE);
    doAction(PORT);
    doAction(STARBOARD);
        simulation.playOneTurn(); // ** TURN7
    checkEntity(0, SHIP, 1, 10, 4, 2, 99, 1);
    checkEntity(2, SHIP, 9, 3, 3, 0, 100, 1);
    checkEntity(4, SHIP, 14, 7, 3, 0, 99, 1);
    checkEntity(1, SHIP, 8, 14, 0, 2, 94, 0);
    checkEntity(3, SHIP, 15, 10, 2, 2, 98, 0);
    checkEntity(5, SHIP, 11, 10, 1, 1, 100, 0);
    checkEntity(6, MINE, 6, 7, 0, 0, 0, 0);
    checkEntity(10, MINE, 18, 9, 0, 0, 0, 0);
    checkEntity(37, MINE, 16, 7, 0, 0, 0, 0);
    checkEntity(38, MINE, 4, 14, 0, 0, 0, 0);
    checkEntity(20, BARREL, 12, 16, 16, 0, 0, 0);
    checkEntity(19, BARREL, 12, 4, 16, 0, 0, 0);
    checkEntity(21, BARREL, 3, 10, 14, 0, 0, 0);
    checkEntity(23, BARREL, 17, 16, 20, 0, 0, 0);
    checkEntity(22, BARREL, 17, 4, 20, 0, 0, 0);
    checkEntity(26, BARREL, 1, 11, 10, 0, 0, 0);
    checkEntity(28, BARREL, 15, 13, 11, 0, 0, 0);
    checkEntity(32, BARREL, 8, 17, 19, 0, 0, 0);
    checkEntity(34, BARREL, 19, 18, 11, 0, 0, 0);
    checkEntity(33, BARREL, 19, 2, 11, 0, 0, 0);
    checkEntity(36, BARREL, 19, 11, 11, 0, 0, 0);
    checkEntity(35, BARREL, 19, 9, 11, 0, 0, 0);
    resetActions(); // 2873
    // ** nmahoude:
    doAction(PORT);
    doAction(FASTER);
    doAction(WAIT);
    // ** Royale:
    doAction(FASTER);
    doAction(FASTER);
    doAction(FASTER);
        simulation.playOneTurn(); // ** TURN8
    checkEntity(0, SHIP, 0, 12, 5, 2, 98, 1);
    checkEntity(2, SHIP, 8, 3, 3, 1, 99, 1);
    checkEntity(4, SHIP, 14, 7, 3, 0, 98, 1);
    checkEntity(1, SHIP, 10, 14, 0, 2, 93, 0);
    checkEntity(3, SHIP, 14, 9, 2, 0, 97, 0);
    checkEntity(5, SHIP, 12, 8, 1, 2, 99, 0);
    checkEntity(6, MINE, 6, 7, 0, 0, 0, 0);
    checkEntity(8, MINE, 4, 5, 0, 0, 0, 0);
    checkEntity(10, MINE, 18, 9, 0, 0, 0, 0);
    checkEntity(37, MINE, 16, 7, 0, 0, 0, 0);
    checkEntity(38, MINE, 4, 14, 0, 0, 0, 0);
    checkEntity(20, BARREL, 12, 16, 16, 0, 0, 0);
    checkEntity(19, BARREL, 12, 4, 16, 0, 0, 0);
    checkEntity(21, BARREL, 3, 10, 14, 0, 0, 0);
    checkEntity(23, BARREL, 17, 16, 20, 0, 0, 0);
    checkEntity(22, BARREL, 17, 4, 20, 0, 0, 0);
    checkEntity(26, BARREL, 1, 11, 10, 0, 0, 0);
    checkEntity(28, BARREL, 15, 13, 11, 0, 0, 0);
    checkEntity(32, BARREL, 8, 17, 19, 0, 0, 0);
    checkEntity(34, BARREL, 19, 18, 11, 0, 0, 0);
    checkEntity(33, BARREL, 19, 2, 11, 0, 0, 0);
    checkEntity(36, BARREL, 19, 11, 11, 0, 0, 0);
    checkEntity(35, BARREL, 19, 9, 11, 0, 0, 0);
    resetActions(); // 2967
    // ** nmahoude:
    doAction(cotc.entities.Action.MINE);
    doAction(SLOWER);
    doAction(STARBOARD);
    // ** Royale:
    doAction(PORT);
    doAction(FIRE, 14, 7);
    doAction(SLOWER);
        simulation.playOneTurn(); // ** TURN9
    checkEntity(0, SHIP, 1, 14, 5, 2, 97, 1);
    checkEntity(2, SHIP, 8, 3, 3, 0, 98, 1);
    checkEntity(4, SHIP, 14, 7, 2, 0, 97, 1);
    checkEntity(1, SHIP, 12, 14, 1, 2, 92, 0);
    checkEntity(3, SHIP, 14, 9, 2, 0, 96, 0);
    checkEntity(5, SHIP, 12, 7, 1, 1, 98, 0);
    checkEntity(6, MINE, 6, 7, 0, 0, 0, 0);
    checkEntity(9, MINE, 4, 15, 0, 0, 0, 0);
    checkEntity(8, MINE, 4, 5, 0, 0, 0, 0);
    checkEntity(10, MINE, 18, 9, 0, 0, 0, 0);
    checkEntity(37, MINE, 16, 7, 0, 0, 0, 0);
    checkEntity(38, MINE, 4, 14, 0, 0, 0, 0);
    checkEntity(39, CANNONBALL, 14, 7, 3, 1, 0, 0);
    checkEntity(20, BARREL, 12, 16, 16, 0, 0, 0);
    checkEntity(19, BARREL, 12, 4, 16, 0, 0, 0);
    checkEntity(21, BARREL, 3, 10, 14, 0, 0, 0);
    checkEntity(23, BARREL, 17, 16, 20, 0, 0, 0);
    checkEntity(22, BARREL, 17, 4, 20, 0, 0, 0);
    checkEntity(26, BARREL, 1, 11, 10, 0, 0, 0);
    checkEntity(28, BARREL, 15, 13, 11, 0, 0, 0);
    checkEntity(32, BARREL, 8, 17, 19, 0, 0, 0);
    checkEntity(34, BARREL, 19, 18, 11, 0, 0, 0);
    checkEntity(33, BARREL, 19, 2, 11, 0, 0, 0);
    checkEntity(36, BARREL, 19, 11, 11, 0, 0, 0);
    checkEntity(35, BARREL, 19, 9, 11, 0, 0, 0);
    resetActions(); // 2536
    // ** nmahoude:
    doAction(cotc.entities.Action.MINE);
    doAction(cotc.entities.Action.MINE);
    doAction(FASTER);
    // ** Royale:
    doAction(STARBOARD);
    doAction(PORT);
    doAction(FIRE, 14, 7);
        simulation.playOneTurn(); // ** TURN10
    checkEntity(0, SHIP, 2, 16, 5, 2, 96, 1);
    checkEntity(2, SHIP, 8, 3, 3, 0, 97, 1);
    checkEntity(4, SHIP, 14, 7, 2, 0, 46, 1);
    checkEntity(1, SHIP, 13, 12, 0, 2, 91, 0);
    checkEntity(3, SHIP, 14, 9, 3, 0, 95, 0);
    checkEntity(5, SHIP, 12, 7, 1, 0, 97, 0);
    checkEntity(6, MINE, 6, 7, 0, 0, 0, 0);
    checkEntity(9, MINE, 4, 15, 0, 0, 0, 0);
    checkEntity(8, MINE, 4, 5, 0, 0, 0, 0);
    checkEntity(10, MINE, 18, 9, 0, 0, 0, 0);
    checkEntity(37, MINE, 16, 7, 0, 0, 0, 0);
    checkEntity(38, MINE, 4, 14, 0, 0, 0, 0);
    checkEntity(40, MINE, 0, 12, 0, 0, 0, 0);
    checkEntity(41, MINE, 10, 3, 0, 0, 0, 0);
    checkEntity(39, CANNONBALL, 14, 7, 3, 0, 0, 0);
    checkEntity(42, CANNONBALL, 14, 7, 5, 2, 0, 0);
    checkEntity(20, BARREL, 12, 16, 16, 0, 0, 0);
    checkEntity(19, BARREL, 12, 4, 16, 0, 0, 0);
    checkEntity(21, BARREL, 3, 10, 14, 0, 0, 0);
    checkEntity(23, BARREL, 17, 16, 20, 0, 0, 0);
    checkEntity(22, BARREL, 17, 4, 20, 0, 0, 0);
    checkEntity(26, BARREL, 1, 11, 10, 0, 0, 0);
    checkEntity(28, BARREL, 15, 13, 11, 0, 0, 0);
    checkEntity(32, BARREL, 8, 17, 19, 0, 0, 0);
    checkEntity(34, BARREL, 19, 18, 11, 0, 0, 0);
    checkEntity(33, BARREL, 19, 2, 11, 0, 0, 0);
    checkEntity(36, BARREL, 19, 11, 11, 0, 0, 0);
    checkEntity(35, BARREL, 19, 9, 11, 0, 0, 0);
    resetActions(); // 2954
    // ** nmahoude:
    doAction(PORT);
    doAction(WAIT);
    doAction(FASTER);
    // ** Royale:
    doAction(STARBOARD);
    doAction(STARBOARD);
    doAction(FASTER);
        simulation.playOneTurn(); // ** TURN11
    checkEntity(0, SHIP, 3, 18, 0, 2, 95, 1);
    checkEntity(2, SHIP, 8, 3, 3, 0, 96, 1);
    checkEntity(4, SHIP, 14, 7, 2, 0, 45, 1);
    checkEntity(1, SHIP, 15, 12, 5, 2, 100, 0);
    checkEntity(3, SHIP, 14, 9, 2, 0, 94, 0);
    checkEntity(5, SHIP, 12, 7, 1, 0, 96, 0);
    checkEntity(6, MINE, 6, 7, 0, 0, 0, 0);
    checkEntity(9, MINE, 4, 15, 0, 0, 0, 0);
    checkEntity(8, MINE, 4, 5, 0, 0, 0, 0);
    checkEntity(10, MINE, 18, 9, 0, 0, 0, 0);
    checkEntity(37, MINE, 16, 7, 0, 0, 0, 0);
    checkEntity(38, MINE, 4, 14, 0, 0, 0, 0);
    checkEntity(41, MINE, 10, 3, 0, 0, 0, 0);
    checkEntity(42, CANNONBALL, 14, 7, 5, 1, 0, 0);
    checkEntity(20, BARREL, 12, 16, 16, 0, 0, 0);
    checkEntity(19, BARREL, 12, 4, 16, 0, 0, 0);
    checkEntity(21, BARREL, 3, 10, 14, 0, 0, 0);
    checkEntity(23, BARREL, 17, 16, 20, 0, 0, 0);
    checkEntity(22, BARREL, 17, 4, 20, 0, 0, 0);
    checkEntity(26, BARREL, 1, 11, 10, 0, 0, 0);
    checkEntity(32, BARREL, 8, 17, 19, 0, 0, 0);
    checkEntity(34, BARREL, 19, 18, 11, 0, 0, 0);
    checkEntity(33, BARREL, 19, 2, 11, 0, 0, 0);
    checkEntity(36, BARREL, 19, 11, 11, 0, 0, 0);
    checkEntity(35, BARREL, 19, 9, 11, 0, 0, 0);
    resetActions(); // 2955
    // ** nmahoude:
    doAction(cotc.entities.Action.MINE);
    doAction(PORT);
    doAction(FASTER);
    // ** Royale:
    doAction(STARBOARD);
    doAction(FASTER);
    doAction(FASTER);
        simulation.playOneTurn(); // ** TURN12
    checkEntity(0, SHIP, 5, 18, 0, 2, 94, 1);
    checkEntity(2, SHIP, 8, 3, 4, 0, 95, 1);
    checkEntity(1, SHIP, 16, 14, 4, 2, 99, 0);
    checkEntity(3, SHIP, 14, 8, 2, 1, 93, 0);
    checkEntity(5, SHIP, 12, 7, 1, 0, 95, 0);
    checkEntity(7, MINE, 6, 13, 0, 0, 0, 0);
    checkEntity(6, MINE, 6, 7, 0, 0, 0, 0);
    checkEntity(9, MINE, 4, 15, 0, 0, 0, 0);
    checkEntity(8, MINE, 4, 5, 0, 0, 0, 0);
    checkEntity(38, MINE, 4, 14, 0, 0, 0, 0);
    checkEntity(41, MINE, 10, 3, 0, 0, 0, 0);
    checkEntity(42, CANNONBALL, 14, 7, 5, 0, 0, 0);
    checkEntity(20, BARREL, 12, 16, 16, 0, 0, 0);
    checkEntity(19, BARREL, 12, 4, 16, 0, 0, 0);
    checkEntity(21, BARREL, 3, 10, 14, 0, 0, 0);
    checkEntity(23, BARREL, 17, 16, 20, 0, 0, 0);
    checkEntity(22, BARREL, 17, 4, 20, 0, 0, 0);
    checkEntity(26, BARREL, 1, 11, 10, 0, 0, 0);
    checkEntity(32, BARREL, 8, 17, 19, 0, 0, 0);
    checkEntity(34, BARREL, 19, 18, 11, 0, 0, 0);
    checkEntity(33, BARREL, 19, 2, 11, 0, 0, 0);
    checkEntity(36, BARREL, 19, 11, 11, 0, 0, 0);
    checkEntity(35, BARREL, 19, 9, 11, 0, 0, 0);
    checkEntity(43, BARREL, 14, 7, 30, 0, 0, 0);
    resetActions(); // 3944
    // ** nmahoude:
    doAction(PORT);
    doAction(WAIT);
    // ** Royale:
    doAction(STARBOARD);
    doAction(STARBOARD);
    doAction(FIRE, 1, 11);
        simulation.playOneTurn(); // ** TURN13
    checkEntity(0, SHIP, 7, 18, 1, 2, 93, 1);
    checkEntity(2, SHIP, 8, 3, 4, 0, 94, 1);
    checkEntity(1, SHIP, 15, 16, 3, 2, 98, 0);
    checkEntity(3, SHIP, 14, 8, 1, 0, 100, 0);
    checkEntity(5, SHIP, 12, 7, 1, 0, 94, 0);
    checkEntity(7, MINE, 6, 13, 0, 0, 0, 0);
    checkEntity(6, MINE, 6, 7, 0, 0, 0, 0);
    checkEntity(9, MINE, 4, 15, 0, 0, 0, 0);
    checkEntity(8, MINE, 4, 5, 0, 0, 0, 0);
    checkEntity(38, MINE, 4, 14, 0, 0, 0, 0);
    checkEntity(41, MINE, 10, 3, 0, 0, 0, 0);
    checkEntity(20, BARREL, 12, 16, 16, 0, 0, 0);
    checkEntity(19, BARREL, 12, 4, 16, 0, 0, 0);
    checkEntity(21, BARREL, 3, 10, 14, 0, 0, 0);
    checkEntity(23, BARREL, 17, 16, 20, 0, 0, 0);
    checkEntity(22, BARREL, 17, 4, 20, 0, 0, 0);
    checkEntity(26, BARREL, 1, 11, 10, 0, 0, 0);
    checkEntity(32, BARREL, 8, 17, 19, 0, 0, 0);
    checkEntity(34, BARREL, 19, 18, 11, 0, 0, 0);
    checkEntity(33, BARREL, 19, 2, 11, 0, 0, 0);
    checkEntity(36, BARREL, 19, 11, 11, 0, 0, 0);
    checkEntity(35, BARREL, 19, 9, 11, 0, 0, 0);
    resetActions(); // 3850
    // ** nmahoude:
    doAction(WAIT);
    doAction(STARBOARD);
    // ** Royale:
    doAction(STARBOARD);
    doAction(STARBOARD);
    doAction(FASTER);
        simulation.playOneTurn(); // ** TURN14
    checkEntity(0, SHIP, 8, 16, 1, 2, 92, 1);
    checkEntity(2, SHIP, 8, 3, 3, 0, 93, 1);
    checkEntity(1, SHIP, 13, 16, 2, 2, 100, 0);
    checkEntity(3, SHIP, 14, 8, 0, 0, 99, 0);
    checkEntity(5, SHIP, 13, 6, 1, 1, 93, 0);
    checkEntity(7, MINE, 6, 13, 0, 0, 0, 0);
    checkEntity(6, MINE, 6, 7, 0, 0, 0, 0);
    checkEntity(9, MINE, 4, 15, 0, 0, 0, 0);
    checkEntity(8, MINE, 4, 5, 0, 0, 0, 0);
    checkEntity(13, MINE, 9, 11, 0, 0, 0, 0);
    checkEntity(38, MINE, 4, 14, 0, 0, 0, 0);
    checkEntity(41, MINE, 10, 3, 0, 0, 0, 0);
    checkEntity(19, BARREL, 12, 4, 16, 0, 0, 0);
    checkEntity(21, BARREL, 3, 10, 14, 0, 0, 0);
    checkEntity(23, BARREL, 17, 16, 20, 0, 0, 0);
    checkEntity(22, BARREL, 17, 4, 20, 0, 0, 0);
    checkEntity(26, BARREL, 1, 11, 10, 0, 0, 0);
    checkEntity(32, BARREL, 8, 17, 19, 0, 0, 0);
    checkEntity(34, BARREL, 19, 18, 11, 0, 0, 0);
    checkEntity(33, BARREL, 19, 2, 11, 0, 0, 0);
    checkEntity(36, BARREL, 19, 11, 11, 0, 0, 0);
    checkEntity(35, BARREL, 19, 9, 11, 0, 0, 0);
    resetActions(); // 3907
    // ** nmahoude:
    doAction(STARBOARD);
    doAction(cotc.entities.Action.MINE);
    // ** Royale:
    doAction(STARBOARD);
    doAction(cotc.entities.Action.MINE);
    doAction(cotc.entities.Action.MINE);
        simulation.playOneTurn(); // ** TURN15
    checkEntity(0, SHIP, 9, 14, 0, 2, 91, 1);
    checkEntity(2, SHIP, 8, 3, 3, 0, 92, 1);
    checkEntity(1, SHIP, 12, 14, 1, 2, 99, 0);
    checkEntity(3, SHIP, 14, 8, 0, 0, 98, 0);
    checkEntity(5, SHIP, 13, 5, 1, 1, 92, 0);
    checkEntity(7, MINE, 6, 13, 0, 0, 0, 0);
    checkEntity(6, MINE, 6, 7, 0, 0, 0, 0);
    checkEntity(9, MINE, 4, 15, 0, 0, 0, 0);
    checkEntity(8, MINE, 4, 5, 0, 0, 0, 0);
    checkEntity(13, MINE, 9, 11, 0, 0, 0, 0);
    checkEntity(12, MINE, 9, 9, 0, 0, 0, 0);
    checkEntity(38, MINE, 4, 14, 0, 0, 0, 0);
    checkEntity(41, MINE, 10, 3, 0, 0, 0, 0);
    checkEntity(19, BARREL, 12, 4, 16, 0, 0, 0);
    checkEntity(21, BARREL, 3, 10, 14, 0, 0, 0);
    checkEntity(23, BARREL, 17, 16, 20, 0, 0, 0);
    checkEntity(22, BARREL, 17, 4, 20, 0, 0, 0);
    checkEntity(26, BARREL, 1, 11, 10, 0, 0, 0);
    checkEntity(32, BARREL, 8, 17, 19, 0, 0, 0);
    checkEntity(34, BARREL, 19, 18, 11, 0, 0, 0);
    checkEntity(33, BARREL, 19, 2, 11, 0, 0, 0);
    checkEntity(36, BARREL, 19, 11, 11, 0, 0, 0);
    checkEntity(35, BARREL, 19, 9, 11, 0, 0, 0);
    resetActions(); // 3539
    // ** nmahoude:
    doAction(WAIT);
    doAction(FASTER);
    // ** Royale:
    doAction(PORT);
    doAction(STARBOARD);
    doAction(STARBOARD);
        simulation.playOneTurn(); // ** TURN16
    checkEntity(0, SHIP, 11, 14, 0, 2, 90, 1);
    checkEntity(2, SHIP, 7, 3, 3, 1, 91, 1);
    checkEntity(1, SHIP, 13, 12, 2, 2, 98, 0);
    checkEntity(3, SHIP, 14, 8, 5, 0, 97, 0);
    checkEntity(5, SHIP, 14, 4, 0, 1, 91, 0);
    checkEntity(7, MINE, 6, 13, 0, 0, 0, 0);
    checkEntity(6, MINE, 6, 7, 0, 0, 0, 0);
    checkEntity(8, MINE, 4, 5, 0, 0, 0, 0);
    checkEntity(13, MINE, 9, 11, 0, 0, 0, 0);
    checkEntity(12, MINE, 9, 9, 0, 0, 0, 0);
    checkEntity(41, MINE, 10, 3, 0, 0, 0, 0);
    checkEntity(19, BARREL, 12, 4, 16, 0, 0, 0);
    checkEntity(21, BARREL, 3, 10, 14, 0, 0, 0);
    checkEntity(23, BARREL, 17, 16, 20, 0, 0, 0);
    checkEntity(22, BARREL, 17, 4, 20, 0, 0, 0);
    checkEntity(26, BARREL, 1, 11, 10, 0, 0, 0);
    checkEntity(32, BARREL, 8, 17, 19, 0, 0, 0);
    checkEntity(34, BARREL, 19, 18, 11, 0, 0, 0);
    checkEntity(33, BARREL, 19, 2, 11, 0, 0, 0);
    checkEntity(36, BARREL, 19, 11, 11, 0, 0, 0);
    checkEntity(35, BARREL, 19, 9, 11, 0, 0, 0);
    resetActions(); // 3855
    // ** nmahoude:
    doAction(STARBOARD);
    doAction(PORT);
    // ** Royale:
    doAction(cotc.entities.Action.MINE);
    doAction(FASTER);
    doAction(FASTER);
        simulation.playOneTurn(); // ** TURN17
    checkEntity(0, SHIP, 13, 14, 5, 2, 64, 1);
    checkEntity(2, SHIP, 6, 3, 4, 1, 90, 1);
    checkEntity(1, SHIP, 12, 10, 2, 2, 97, 0);
    checkEntity(3, SHIP, 14, 9, 5, 1, 96, 0);
    checkEntity(5, SHIP, 16, 4, 0, 2, 100, 0);
    checkEntity(6, MINE, 6, 7, 0, 0, 0, 0);
    checkEntity(8, MINE, 4, 5, 0, 0, 0, 0);
    checkEntity(13, MINE, 9, 11, 0, 0, 0, 0);
    checkEntity(41, MINE, 10, 3, 0, 0, 0, 0);
    checkEntity(19, BARREL, 12, 4, 16, 0, 0, 0);
    checkEntity(21, BARREL, 3, 10, 14, 0, 0, 0);
    checkEntity(23, BARREL, 17, 16, 20, 0, 0, 0);
    checkEntity(26, BARREL, 1, 11, 10, 0, 0, 0);
    checkEntity(32, BARREL, 8, 17, 19, 0, 0, 0);
    checkEntity(34, BARREL, 19, 18, 11, 0, 0, 0);
    checkEntity(33, BARREL, 19, 2, 11, 0, 0, 0);
    checkEntity(36, BARREL, 19, 11, 11, 0, 0, 0);
    checkEntity(35, BARREL, 19, 9, 11, 0, 0, 0);
    resetActions(); // 3620
    // ** nmahoude:
    doAction(PORT);
    doAction(cotc.entities.Action.MINE);
    // ** Royale:
    doAction(FASTER);
    doAction(cotc.entities.Action.MINE);
    doAction(STARBOARD);
        simulation.playOneTurn(); // ** TURN18
    checkEntity(0, SHIP, 14, 16, 0, 2, 63, 1);
    checkEntity(2, SHIP, 6, 4, 4, 1, 89, 1);
    checkEntity(1, SHIP, 11, 8, 2, 2, 96, 0);
    checkEntity(3, SHIP, 15, 10, 5, 1, 95, 0);
    checkEntity(5, SHIP, 18, 4, 5, 2, 99, 0);
    checkEntity(6, MINE, 6, 7, 0, 0, 0, 0);
    checkEntity(8, MINE, 4, 5, 0, 0, 0, 0);
    checkEntity(41, MINE, 10, 3, 0, 0, 0, 0);
    checkEntity(46, MINE, 7, 1, 0, 0, 0, 0);
    checkEntity(19, BARREL, 12, 4, 16, 0, 0, 0);
    checkEntity(21, BARREL, 3, 10, 14, 0, 0, 0);
    checkEntity(23, BARREL, 17, 16, 20, 0, 0, 0);
    checkEntity(26, BARREL, 1, 11, 10, 0, 0, 0);
    checkEntity(32, BARREL, 8, 17, 19, 0, 0, 0);
    checkEntity(34, BARREL, 19, 18, 11, 0, 0, 0);
    checkEntity(33, BARREL, 19, 2, 11, 0, 0, 0);
    checkEntity(36, BARREL, 19, 11, 11, 0, 0, 0);
    checkEntity(35, BARREL, 19, 9, 11, 0, 0, 0);
    resetActions(); // 3583
    // ** nmahoude:
    doAction(WAIT);
    doAction(FASTER);
    // ** Royale:
    doAction(STARBOARD);
    doAction(FASTER);
    doAction(cotc.entities.Action.MINE);
        simulation.playOneTurn(); // ** TURN19
    checkEntity(0, SHIP, 16, 16, 0, 2, 82, 1);
    checkEntity(2, SHIP, 5, 6, 4, 2, 88, 1);
    checkEntity(1, SHIP, 10, 6, 1, 2, 95, 0);
    checkEntity(3, SHIP, 16, 12, 5, 2, 94, 0);
    checkEntity(5, SHIP, 19, 6, 5, 2, 98, 0);
    checkEntity(6, MINE, 6, 7, 0, 0, 0, 0);
    checkEntity(8, MINE, 4, 5, 0, 0, 0, 0);
    checkEntity(11, MINE, 18, 11, 0, 0, 0, 0);
    checkEntity(46, MINE, 7, 1, 0, 0, 0, 0);
    checkEntity(19, BARREL, 12, 4, 16, 0, 0, 0);
    checkEntity(21, BARREL, 3, 10, 14, 0, 0, 0);
    checkEntity(26, BARREL, 1, 11, 10, 0, 0, 0);
    checkEntity(32, BARREL, 8, 17, 19, 0, 0, 0);
    checkEntity(34, BARREL, 19, 18, 11, 0, 0, 0);
    checkEntity(33, BARREL, 19, 2, 11, 0, 0, 0);
    checkEntity(36, BARREL, 19, 11, 11, 0, 0, 0);
    checkEntity(35, BARREL, 19, 9, 11, 0, 0, 0);
    resetActions(); // 3628
    // ** nmahoude:
    doAction(PORT);
    doAction(PORT);
    // ** Royale:
    doAction(STARBOARD);
    doAction(SLOWER);
    doAction(FIRE, 19, 11);
        simulation.playOneTurn(); // ** TURN20
    checkEntity(0, SHIP, 18, 16, 1, 2, 81, 1);
    checkEntity(2, SHIP, 4, 8, 5, 2, 87, 1);
    checkEntity(1, SHIP, 11, 4, 0, 2, 100, 0);
    checkEntity(3, SHIP, 16, 13, 5, 1, 93, 0);
    checkEntity(5, SHIP, 20, 8, 5, 2, 97, 0);
    checkEntity(7, MINE, 6, 13, 0, 0, 0, 0);
    checkEntity(6, MINE, 6, 7, 0, 0, 0, 0);
    checkEntity(8, MINE, 4, 5, 0, 0, 0, 0);
    checkEntity(11, MINE, 18, 11, 0, 0, 0, 0);
    checkEntity(48, CANNONBALL, 19, 11, 5, 2, 0, 0);
    checkEntity(21, BARREL, 3, 10, 14, 0, 0, 0);
    checkEntity(26, BARREL, 1, 11, 10, 0, 0, 0);
    checkEntity(32, BARREL, 8, 17, 19, 0, 0, 0);
    checkEntity(34, BARREL, 19, 18, 11, 0, 0, 0);
    checkEntity(33, BARREL, 19, 2, 11, 0, 0, 0);
    checkEntity(36, BARREL, 19, 11, 11, 0, 0, 0);
    checkEntity(35, BARREL, 19, 9, 11, 0, 0, 0);
    resetActions(); // 3668
    // ** nmahoude:
    doAction(WAIT);
    doAction(STARBOARD);
    // ** Royale:
    doAction(FIRE, 18, 11);
    doAction(PORT);
    doAction(STARBOARD);
        simulation.playOneTurn(); // ** TURN21
    checkEntity(0, SHIP, 19, 14, 1, 2, 80, 1);
    checkEntity(2, SHIP, 5, 10, 4, 2, 86, 1);
    checkEntity(1, SHIP, 13, 4, 0, 2, 99, 0);
    checkEntity(3, SHIP, 17, 14, 0, 1, 92, 0);
    checkEntity(5, SHIP, 21, 10, 4, 2, 96, 0);
    checkEntity(7, MINE, 6, 13, 0, 0, 0, 0);
    checkEntity(6, MINE, 6, 7, 0, 0, 0, 0);
    checkEntity(9, MINE, 4, 15, 0, 0, 0, 0);
    checkEntity(8, MINE, 4, 5, 0, 0, 0, 0);
    checkEntity(11, MINE, 18, 11, 0, 0, 0, 0);
    checkEntity(10, MINE, 18, 9, 0, 0, 0, 0);
    checkEntity(13, MINE, 9, 11, 0, 0, 0, 0);
    checkEntity(12, MINE, 9, 9, 0, 0, 0, 0);
    checkEntity(38, MINE, 4, 14, 0, 0, 0, 0);
    checkEntity(48, CANNONBALL, 19, 11, 5, 1, 0, 0);
    checkEntity(49, CANNONBALL, 18, 11, 1, 4, 0, 0);
    checkEntity(21, BARREL, 3, 10, 14, 0, 0, 0);
    checkEntity(26, BARREL, 1, 11, 10, 0, 0, 0);
    checkEntity(32, BARREL, 8, 17, 19, 0, 0, 0);
    checkEntity(34, BARREL, 19, 18, 11, 0, 0, 0);
    checkEntity(33, BARREL, 19, 2, 11, 0, 0, 0);
    checkEntity(36, BARREL, 19, 11, 11, 0, 0, 0);
    checkEntity(35, BARREL, 19, 9, 11, 0, 0, 0);
    resetActions(); // 3046
    // ** nmahoude:
    doAction(WAIT);
    doAction(cotc.entities.Action.MINE);
    // ** Royale:
    doAction(FASTER);
    doAction(FIRE, 19, 14);
    doAction(FIRE, 18, 11);
        simulation.playOneTurn(); // ** TURN22
    checkEntity(0, SHIP, 19, 14, 1, 0, 79, 1);
    checkEntity(2, SHIP, 4, 12, 4, 2, 85, 1);
    checkEntity(1, SHIP, 15, 4, 0, 2, 98, 0);
    checkEntity(3, SHIP, 17, 14, 0, 0, 91, 0);
    checkEntity(5, SHIP, 21, 10, 4, 0, 95, 0);
    checkEntity(7, MINE, 6, 13, 0, 0, 0, 0);
    checkEntity(6, MINE, 6, 7, 0, 0, 0, 0);
    checkEntity(9, MINE, 4, 15, 0, 0, 0, 0);
    checkEntity(11, MINE, 18, 11, 0, 0, 0, 0);
    checkEntity(10, MINE, 18, 9, 0, 0, 0, 0);
    checkEntity(38, MINE, 4, 14, 0, 0, 0, 0);
    checkEntity(40, MINE, 0, 12, 0, 0, 0, 0);
    checkEntity(48, CANNONBALL, 19, 11, 5, 0, 0, 0);
    checkEntity(49, CANNONBALL, 18, 11, 1, 3, 0, 0);
    checkEntity(50, CANNONBALL, 19, 14, 3, 1, 0, 0);
    checkEntity(51, CANNONBALL, 18, 11, 5, 2, 0, 0);
    checkEntity(21, BARREL, 3, 10, 14, 0, 0, 0);
    checkEntity(26, BARREL, 1, 11, 10, 0, 0, 0);
    checkEntity(32, BARREL, 8, 17, 19, 0, 0, 0);
    checkEntity(34, BARREL, 19, 18, 11, 0, 0, 0);
    checkEntity(33, BARREL, 19, 2, 11, 0, 0, 0);
    checkEntity(35, BARREL, 19, 9, 11, 0, 0, 0);
    resetActions(); // 3502
    // ** nmahoude:
    doAction(FASTER);
    doAction(PORT);
    // ** Royale:
    doAction(STARBOARD);
    doAction(STARBOARD);
    doAction(FASTER);
        simulation.playOneTurn(); // ** TURN23
    checkEntity(0, SHIP, 19, 14, 1, 0, 28, 1);
    checkEntity(2, SHIP, 3, 14, 5, 2, 84, 1);
    checkEntity(1, SHIP, 17, 4, 5, 2, 97, 0);
    checkEntity(3, SHIP, 17, 14, 5, 0, 90, 0);
    checkEntity(5, SHIP, 21, 10, 4, 0, 94, 0);
    checkEntity(7, MINE, 6, 13, 0, 0, 0, 0);
    checkEntity(9, MINE, 4, 15, 0, 0, 0, 0);
    checkEntity(11, MINE, 18, 11, 0, 0, 0, 0);
    checkEntity(10, MINE, 18, 9, 0, 0, 0, 0);
    checkEntity(38, MINE, 4, 14, 0, 0, 0, 0);
    checkEntity(40, MINE, 0, 12, 0, 0, 0, 0);
    checkEntity(49, CANNONBALL, 18, 11, 1, 2, 0, 0);
    checkEntity(50, CANNONBALL, 19, 14, 3, 0, 0, 0);
    checkEntity(51, CANNONBALL, 18, 11, 5, 1, 0, 0);
    checkEntity(21, BARREL, 3, 10, 14, 0, 0, 0);
    checkEntity(26, BARREL, 1, 11, 10, 0, 0, 0);
    checkEntity(32, BARREL, 8, 17, 19, 0, 0, 0);
    checkEntity(34, BARREL, 19, 18, 11, 0, 0, 0);
    checkEntity(33, BARREL, 19, 2, 11, 0, 0, 0);
    checkEntity(35, BARREL, 19, 9, 11, 0, 0, 0);
    resetActions(); // 3675
    // ** nmahoude:
    doAction(WAIT);
    doAction(PORT);
    // ** Royale:
    doAction(FIRE, 1, 11);
    doAction(PORT);
    doAction(FASTER);
        simulation.playOneTurn(); // ** TURN24
    checkEntity(0, SHIP, 19, 14, 1, 0, 27, 1);
    checkEntity(2, SHIP, 4, 16, 0, 2, 83, 1);
    checkEntity(1, SHIP, 18, 6, 5, 2, 96, 0);
    checkEntity(3, SHIP, 17, 14, 0, 0, 89, 0);
    checkEntity(5, SHIP, 20, 11, 4, 1, 93, 0);
    checkEntity(7, MINE, 6, 13, 0, 0, 0, 0);
    checkEntity(9, MINE, 4, 15, 0, 0, 0, 0);
    checkEntity(10, MINE, 18, 9, 0, 0, 0, 0);
    checkEntity(38, MINE, 4, 14, 0, 0, 0, 0);
    checkEntity(49, CANNONBALL, 18, 11, 1, 1, 0, 0);
    checkEntity(51, CANNONBALL, 18, 11, 5, 0, 0, 0);
    checkEntity(21, BARREL, 3, 10, 14, 0, 0, 0);
    checkEntity(26, BARREL, 1, 11, 10, 0, 0, 0);
    checkEntity(32, BARREL, 8, 17, 19, 0, 0, 0);
    checkEntity(34, BARREL, 19, 18, 11, 0, 0, 0);
    checkEntity(33, BARREL, 19, 2, 11, 0, 0, 0);
    checkEntity(35, BARREL, 19, 9, 11, 0, 0, 0);
    resetActions(); // 3794
    // ** nmahoude:
    doAction(PORT);
    doAction(cotc.entities.Action.MINE);
    // ** Royale:
    doAction(PORT);
    doAction(FIRE, 19, 14);
    doAction(STARBOARD);
        simulation.playOneTurn(); // ** TURN25
    checkEntity(0, SHIP, 19, 14, 2, 0, 26, 1);
    checkEntity(2, SHIP, 6, 16, 0, 2, 82, 1);
    checkEntity(1, SHIP, 19, 8, 0, 2, 100, 0);
    checkEntity(3, SHIP, 17, 14, 0, 0, 88, 0);
    checkEntity(5, SHIP, 20, 11, 3, 0, 92, 0);
    checkEntity(7, MINE, 6, 13, 0, 0, 0, 0);
    checkEntity(9, MINE, 4, 15, 0, 0, 0, 0);
    checkEntity(10, MINE, 18, 9, 0, 0, 0, 0);
    checkEntity(38, MINE, 4, 14, 0, 0, 0, 0);
    checkEntity(52, MINE, 2, 16, 0, 0, 0, 0);
    checkEntity(49, CANNONBALL, 18, 11, 1, 0, 0, 0);
    checkEntity(53, CANNONBALL, 19, 14, 3, 1, 0, 0);
    checkEntity(21, BARREL, 3, 10, 14, 0, 0, 0);
    checkEntity(26, BARREL, 1, 11, 10, 0, 0, 0);
    checkEntity(32, BARREL, 8, 17, 19, 0, 0, 0);
    checkEntity(34, BARREL, 19, 18, 11, 0, 0, 0);
    checkEntity(33, BARREL, 19, 2, 11, 0, 0, 0);
    resetActions(); // 3932
    // ** nmahoude:
    doAction(STARBOARD);
    doAction(STARBOARD);
    // ** Royale:
    doAction(STARBOARD);
    doAction(STARBOARD);
    doAction(FASTER);
        simulation.playOneTurn(); // ** TURN26
    checkEntity(2, SHIP, 8, 16, 5, 2, 100, 1);
    checkEntity(1, SHIP, 21, 8, 5, 2, 99, 0);
    checkEntity(3, SHIP, 17, 14, 5, 0, 87, 0);
    checkEntity(5, SHIP, 19, 11, 3, 1, 91, 0);
    checkEntity(7, MINE, 6, 13, 0, 0, 0, 0);
    checkEntity(9, MINE, 4, 15, 0, 0, 0, 0);
    checkEntity(13, MINE, 9, 11, 0, 0, 0, 0);
    checkEntity(38, MINE, 4, 14, 0, 0, 0, 0);
    checkEntity(53, CANNONBALL, 19, 14, 3, 0, 0, 0);
    checkEntity(21, BARREL, 3, 10, 14, 0, 0, 0);
    checkEntity(26, BARREL, 1, 11, 10, 0, 0, 0);
    checkEntity(34, BARREL, 19, 18, 11, 0, 0, 0);
    checkEntity(33, BARREL, 19, 2, 11, 0, 0, 0);
    checkEntity(54, BARREL, 19, 14, 25, 0, 0, 0);
    resetActions(); // 4761
    // ** nmahoude:
    doAction(PORT);
    // ** Royale:
    doAction(STARBOARD);
    doAction(PORT);
    doAction(FASTER);
        simulation.playOneTurn(); // ** TURN27
    checkEntity(2, SHIP, 9, 18, 0, 2, 99, 1);
    checkEntity(1, SHIP, 22, 10, 4, 2, 98, 0);
    checkEntity(3, SHIP, 17, 14, 0, 0, 86, 0);
    checkEntity(5, SHIP, 17, 11, 3, 2, 90, 0);
    checkEntity(7, MINE, 6, 13, 0, 0, 0, 0);
    checkEntity(21, BARREL, 3, 10, 14, 0, 0, 0);
    checkEntity(26, BARREL, 1, 11, 10, 0, 0, 0);
    checkEntity(34, BARREL, 19, 18, 11, 0, 0, 0);
    checkEntity(33, BARREL, 19, 2, 11, 0, 0, 0);
    checkEntity(54, BARREL, 19, 14, 25, 0, 0, 0);
    resetActions(); // 4713
    // ** nmahoude:
    doAction(PORT);
    // ** Royale:
    doAction(STARBOARD);
    doAction(FASTER);
    doAction(FIRE, 9, 18);
        simulation.playOneTurn(); // ** TURN28
    checkEntity(2, SHIP, 11, 18, 1, 2, 98, 1);
    checkEntity(1, SHIP, 21, 12, 3, 2, 97, 0);
    checkEntity(3, SHIP, 18, 14, 0, 1, 100, 0);
    checkEntity(5, SHIP, 15, 11, 3, 2, 89, 0);
    checkEntity(21, BARREL, 3, 10, 14, 0, 0, 0);
    checkEntity(26, BARREL, 1, 11, 10, 0, 0, 0);
    checkEntity(34, BARREL, 19, 18, 11, 0, 0, 0);
    checkEntity(33, BARREL, 19, 2, 11, 0, 0, 0);
    resetActions(); // 5025
    // ** nmahoude:
    doAction(PORT);
    // ** Royale:
    doAction(cotc.entities.Action.MINE);
    doAction(STARBOARD);
    doAction(FASTER);
        simulation.playOneTurn(); // ** TURN29
    checkEntity(2, SHIP, 12, 16, 2, 2, 97, 1);
    checkEntity(1, SHIP, 19, 12, 3, 2, 96, 0);
    checkEntity(3, SHIP, 19, 14, 5, 1, 99, 0);
    checkEntity(5, SHIP, 13, 11, 3, 2, 88, 0);
    checkEntity(13, MINE, 9, 11, 0, 0, 0, 0);
    checkEntity(21, BARREL, 3, 10, 14, 0, 0, 0);
    checkEntity(26, BARREL, 1, 11, 10, 0, 0, 0);
    checkEntity(34, BARREL, 19, 18, 11, 0, 0, 0);
    checkEntity(33, BARREL, 19, 2, 11, 0, 0, 0);
    resetActions(); // 3995
    // ** nmahoude:
    doAction(PORT);
    // ** Royale:
    doAction(FASTER);
    doAction(STARBOARD);
    doAction(STARBOARD);
        simulation.playOneTurn(); // ** TURN30
    checkEntity(2, SHIP, 11, 14, 3, 2, 96, 1);
    checkEntity(1, SHIP, 17, 12, 3, 2, 95, 0);
    checkEntity(3, SHIP, 19, 15, 4, 1, 98, 0);
    checkEntity(5, SHIP, 11, 11, 2, 2, 87, 0);
    checkEntity(7, MINE, 6, 13, 0, 0, 0, 0);
    checkEntity(13, MINE, 9, 11, 0, 0, 0, 0);
    checkEntity(12, MINE, 9, 9, 0, 0, 0, 0);
    checkEntity(21, BARREL, 3, 10, 14, 0, 0, 0);
    checkEntity(26, BARREL, 1, 11, 10, 0, 0, 0);
    checkEntity(34, BARREL, 19, 18, 11, 0, 0, 0);
    checkEntity(33, BARREL, 19, 2, 11, 0, 0, 0);
    resetActions(); // 4321
    // ** nmahoude:
    doAction(STARBOARD);
    // ** Royale:
    doAction(FASTER);
    doAction(PORT);
    doAction(FASTER);
        simulation.playOneTurn(); // ** TURN31
    checkEntity(2, SHIP, 9, 14, 2, 2, 95, 1);
    checkEntity(1, SHIP, 15, 12, 3, 2, 94, 0);
    checkEntity(3, SHIP, 19, 16, 5, 1, 97, 0);
    checkEntity(5, SHIP, 10, 9, 2, 2, 86, 0);
    checkEntity(7, MINE, 6, 13, 0, 0, 0, 0);
    checkEntity(9, MINE, 4, 15, 0, 0, 0, 0);
    checkEntity(13, MINE, 9, 11, 0, 0, 0, 0);
    checkEntity(12, MINE, 9, 9, 0, 0, 0, 0);
    checkEntity(38, MINE, 4, 14, 0, 0, 0, 0);
    checkEntity(21, BARREL, 3, 10, 14, 0, 0, 0);
    checkEntity(26, BARREL, 1, 11, 10, 0, 0, 0);
    checkEntity(34, BARREL, 19, 18, 11, 0, 0, 0);
    checkEntity(33, BARREL, 19, 2, 11, 0, 0, 0);
    resetActions(); // 5128
    // ** nmahoude:
    doAction(PORT);
    // ** Royale:
    doAction(STARBOARD);
    doAction(STARBOARD);
    doAction(SLOWER);
        simulation.playOneTurn(); // ** TURN32
    checkEntity(2, SHIP, 8, 12, 3, 2, 94, 1);
    checkEntity(1, SHIP, 13, 12, 2, 2, 93, 0);
    checkEntity(3, SHIP, 19, 17, 4, 1, 100, 0);
    checkEntity(5, SHIP, 10, 8, 2, 1, 85, 0);
    checkEntity(7, MINE, 6, 13, 0, 0, 0, 0);
    checkEntity(6, MINE, 6, 7, 0, 0, 0, 0);
    checkEntity(9, MINE, 4, 15, 0, 0, 0, 0);
    checkEntity(13, MINE, 9, 11, 0, 0, 0, 0);
    checkEntity(12, MINE, 9, 9, 0, 0, 0, 0);
    checkEntity(38, MINE, 4, 14, 0, 0, 0, 0);
    checkEntity(21, BARREL, 3, 10, 14, 0, 0, 0);
    checkEntity(26, BARREL, 1, 11, 10, 0, 0, 0);
    checkEntity(33, BARREL, 19, 2, 11, 0, 0, 0);
    resetActions(); // 5318
    // ** nmahoude:
    doAction(SLOWER);
    // ** Royale:
    doAction(PORT);
    doAction(STARBOARD);
    doAction(PORT);
        simulation.playOneTurn(); // ** TURN33
    checkEntity(2, SHIP, 7, 12, 3, 1, 93, 1);
    checkEntity(1, SHIP, 12, 10, 3, 2, 92, 0);
    checkEntity(3, SHIP, 19, 18, 3, 1, 99, 0);
    checkEntity(5, SHIP, 9, 7, 3, 1, 84, 0);
    checkEntity(7, MINE, 6, 13, 0, 0, 0, 0);
    checkEntity(6, MINE, 6, 7, 0, 0, 0, 0);
    checkEntity(9, MINE, 4, 15, 0, 0, 0, 0);
    checkEntity(13, MINE, 9, 11, 0, 0, 0, 0);
    checkEntity(12, MINE, 9, 9, 0, 0, 0, 0);
    checkEntity(38, MINE, 4, 14, 0, 0, 0, 0);
    checkEntity(21, BARREL, 3, 10, 14, 0, 0, 0);
    checkEntity(26, BARREL, 1, 11, 10, 0, 0, 0);
    checkEntity(33, BARREL, 19, 2, 11, 0, 0, 0);
    resetActions(); // 5151
    // ** nmahoude:
    doAction(FASTER);
    // ** Royale:
    doAction(SLOWER);
    doAction(FASTER);
    doAction(STARBOARD);
        simulation.playOneTurn(); // ** TURN34
    checkEntity(2, SHIP, 5, 12, 3, 2, 92, 1);
    checkEntity(1, SHIP, 11, 10, 3, 1, 91, 0);
    checkEntity(3, SHIP, 17, 18, 3, 2, 98, 0);
    checkEntity(5, SHIP, 8, 7, 2, 1, 83, 0);
    checkEntity(7, MINE, 6, 13, 0, 0, 0, 0);
    checkEntity(6, MINE, 6, 7, 0, 0, 0, 0);
    checkEntity(9, MINE, 4, 15, 0, 0, 0, 0);
    checkEntity(13, MINE, 9, 11, 0, 0, 0, 0);
    checkEntity(38, MINE, 4, 14, 0, 0, 0, 0);
    checkEntity(40, MINE, 0, 12, 0, 0, 0, 0);
    checkEntity(52, MINE, 2, 16, 0, 0, 0, 0);
    checkEntity(21, BARREL, 3, 10, 14, 0, 0, 0);
    checkEntity(26, BARREL, 1, 11, 10, 0, 0, 0);
    checkEntity(33, BARREL, 19, 2, 11, 0, 0, 0);
    resetActions(); // 4680
    // ** nmahoude:
    doAction(SLOWER);
    // ** Royale:
    doAction(FASTER);
    doAction(STARBOARD);
    doAction(STARBOARD);
        simulation.playOneTurn(); // ** TURN35
    checkEntity(2, SHIP, 4, 12, 3, 1, 91, 1);
    checkEntity(1, SHIP, 9, 10, 3, 2, 90, 0);
    checkEntity(3, SHIP, 15, 18, 2, 2, 97, 0);
    checkEntity(5, SHIP, 8, 6, 1, 1, 82, 0);
    checkEntity(7, MINE, 6, 13, 0, 0, 0, 0);
    checkEntity(6, MINE, 6, 7, 0, 0, 0, 0);
    checkEntity(9, MINE, 4, 15, 0, 0, 0, 0);
    checkEntity(38, MINE, 4, 14, 0, 0, 0, 0);
    checkEntity(40, MINE, 0, 12, 0, 0, 0, 0);
    checkEntity(52, MINE, 2, 16, 0, 0, 0, 0);
    checkEntity(21, BARREL, 3, 10, 14, 0, 0, 0);
    checkEntity(26, BARREL, 1, 11, 10, 0, 0, 0);
    checkEntity(33, BARREL, 19, 2, 11, 0, 0, 0);
    resetActions(); // 5123
    // ** nmahoude:
    doAction(SLOWER);
    // ** Royale:
    doAction(SLOWER);
    doAction(FIRE, 4, 12);
    doAction(STARBOARD);
        simulation.playOneTurn(); // ** TURN36
    checkEntity(2, SHIP, 4, 12, 3, 0, 90, 1);
    checkEntity(1, SHIP, 8, 10, 3, 1, 89, 0);
    checkEntity(3, SHIP, 14, 16, 2, 2, 96, 0);
    checkEntity(5, SHIP, 8, 5, 0, 1, 81, 0);
    checkEntity(7, MINE, 6, 13, 0, 0, 0, 0);
    checkEntity(6, MINE, 6, 7, 0, 0, 0, 0);
    checkEntity(9, MINE, 4, 15, 0, 0, 0, 0);
    checkEntity(38, MINE, 4, 14, 0, 0, 0, 0);
    checkEntity(40, MINE, 0, 12, 0, 0, 0, 0);
    checkEntity(52, MINE, 2, 16, 0, 0, 0, 0);
    checkEntity(21, BARREL, 3, 10, 14, 0, 0, 0);
    checkEntity(26, BARREL, 1, 11, 10, 0, 0, 0);
    checkEntity(33, BARREL, 19, 2, 11, 0, 0, 0);
    resetActions(); // 5062
    // ** nmahoude:
    doAction(STARBOARD);
    // ** Royale:
    doAction(FIRE, 3, 10);
    doAction(FIRE, 18, 18);
    doAction(FASTER);
        simulation.playOneTurn(); // ** TURN37
    checkEntity(2, SHIP, 4, 12, 2, 0, 89, 1);
    checkEntity(1, SHIP, 7, 10, 3, 1, 88, 0);
    checkEntity(3, SHIP, 13, 14, 2, 2, 95, 0);
    checkEntity(5, SHIP, 10, 5, 0, 2, 80, 0);
    checkEntity(7, MINE, 6, 13, 0, 0, 0, 0);
    checkEntity(6, MINE, 6, 7, 0, 0, 0, 0);
    checkEntity(9, MINE, 4, 15, 0, 0, 0, 0);
    checkEntity(38, MINE, 4, 14, 0, 0, 0, 0);
    checkEntity(40, MINE, 0, 12, 0, 0, 0, 0);
    checkEntity(52, MINE, 2, 16, 0, 0, 0, 0);
    checkEntity(55, CANNONBALL, 3, 10, 1, 2, 0, 0);
    checkEntity(56, CANNONBALL, 18, 18, 3, 3, 0, 0);
    checkEntity(21, BARREL, 3, 10, 14, 0, 0, 0);
    checkEntity(26, BARREL, 1, 11, 10, 0, 0, 0);
    checkEntity(33, BARREL, 19, 2, 11, 0, 0, 0);
    resetActions(); // 4957
    // ** nmahoude:
    doAction(PORT);
    // ** Royale:
    doAction(STARBOARD);
    doAction(FASTER);
    doAction(STARBOARD);
        simulation.playOneTurn(); // ** TURN38
    checkEntity(2, SHIP, 4, 12, 3, 0, 88, 1);
    checkEntity(1, SHIP, 6, 10, 2, 1, 87, 0);
    checkEntity(3, SHIP, 12, 12, 2, 2, 94, 0);
    checkEntity(5, SHIP, 12, 5, 5, 2, 79, 0);
    checkEntity(7, MINE, 6, 13, 0, 0, 0, 0);
    checkEntity(6, MINE, 6, 7, 0, 0, 0, 0);
    checkEntity(9, MINE, 4, 15, 0, 0, 0, 0);
    checkEntity(38, MINE, 4, 14, 0, 0, 0, 0);
    checkEntity(40, MINE, 0, 12, 0, 0, 0, 0);
    checkEntity(52, MINE, 2, 16, 0, 0, 0, 0);
    checkEntity(55, CANNONBALL, 3, 10, 1, 1, 0, 0);
    checkEntity(56, CANNONBALL, 18, 18, 3, 2, 0, 0);
    checkEntity(21, BARREL, 3, 10, 14, 0, 0, 0);
    checkEntity(26, BARREL, 1, 11, 10, 0, 0, 0);
    checkEntity(33, BARREL, 19, 2, 11, 0, 0, 0);
    resetActions(); // 5147
    // ** nmahoude:
    doAction(WAIT);
    // ** Royale:
    doAction(SLOWER);
    doAction(FASTER);
    doAction(STARBOARD);
        simulation.playOneTurn(); // ** TURN39
    checkEntity(2, SHIP, 4, 12, 3, 0, 87, 1);
    checkEntity(1, SHIP, 6, 10, 2, 0, 86, 0);
    checkEntity(3, SHIP, 11, 10, 2, 2, 93, 0);
    checkEntity(5, SHIP, 13, 7, 4, 2, 78, 0);
    checkEntity(7, MINE, 6, 13, 0, 0, 0, 0);
    checkEntity(6, MINE, 6, 7, 0, 0, 0, 0);
    checkEntity(9, MINE, 4, 15, 0, 0, 0, 0);
    checkEntity(38, MINE, 4, 14, 0, 0, 0, 0);
    checkEntity(40, MINE, 0, 12, 0, 0, 0, 0);
    checkEntity(52, MINE, 2, 16, 0, 0, 0, 0);
    checkEntity(55, CANNONBALL, 3, 10, 1, 0, 0, 0);
    checkEntity(56, CANNONBALL, 18, 18, 3, 1, 0, 0);
    checkEntity(26, BARREL, 1, 11, 10, 0, 0, 0);
    checkEntity(33, BARREL, 19, 2, 11, 0, 0, 0);
    resetActions(); // 4560
    // ** nmahoude:
    doAction(cotc.entities.Action.MINE);
    // ** Royale:
    doAction(STARBOARD);
    doAction(SLOWER);
    doAction(FASTER);
        simulation.playOneTurn(); // ** TURN40
    checkEntity(2, SHIP, 4, 12, 3, 0, 86, 1);
    checkEntity(1, SHIP, 6, 10, 1, 0, 85, 0);
    checkEntity(3, SHIP, 10, 9, 2, 1, 92, 0);
    checkEntity(5, SHIP, 12, 9, 4, 2, 77, 0);
    checkEntity(7, MINE, 6, 13, 0, 0, 0, 0);
    checkEntity(6, MINE, 6, 7, 0, 0, 0, 0);
    checkEntity(9, MINE, 4, 15, 0, 0, 0, 0);
    checkEntity(38, MINE, 4, 14, 0, 0, 0, 0);
    checkEntity(40, MINE, 0, 12, 0, 0, 0, 0);
    checkEntity(52, MINE, 2, 16, 0, 0, 0, 0);
    checkEntity(57, MINE, 6, 12, 0, 0, 0, 0);
    checkEntity(56, CANNONBALL, 18, 18, 3, 0, 0, 0);
    checkEntity(26, BARREL, 1, 11, 10, 0, 0, 0);
    checkEntity(33, BARREL, 19, 2, 11, 0, 0, 0);
    resetActions(); // 5220
    // ** nmahoude:
    doAction(cotc.entities.Action.MINE);
    // ** Royale:
    doAction(FASTER);
    doAction(SLOWER);
    doAction(SLOWER);
        simulation.playOneTurn(); // ** TURN41
    checkEntity(2, SHIP, 4, 12, 3, 0, 85, 1);
    checkEntity(1, SHIP, 6, 9, 1, 1, 84, 0);
    checkEntity(3, SHIP, 10, 9, 2, 0, 91, 0);
    checkEntity(5, SHIP, 12, 10, 4, 1, 76, 0);
    checkEntity(7, MINE, 6, 13, 0, 0, 0, 0);
    checkEntity(6, MINE, 6, 7, 0, 0, 0, 0);
    checkEntity(9, MINE, 4, 15, 0, 0, 0, 0);
    checkEntity(38, MINE, 4, 14, 0, 0, 0, 0);
    checkEntity(40, MINE, 0, 12, 0, 0, 0, 0);
    checkEntity(52, MINE, 2, 16, 0, 0, 0, 0);
    checkEntity(57, MINE, 6, 12, 0, 0, 0, 0);
    checkEntity(26, BARREL, 1, 11, 10, 0, 0, 0);
    checkEntity(33, BARREL, 19, 2, 11, 0, 0, 0);
    resetActions(); // 4657
    // ** nmahoude:
    doAction(FASTER);
    // ** Royale:
    doAction(FASTER);
    doAction(STARBOARD);
    doAction(PORT);
        simulation.playOneTurn(); // ** TURN42
    checkEntity(2, SHIP, 3, 12, 3, 1, 84, 1);
    checkEntity(1, SHIP, 7, 7, 1, 2, 83, 0);
    checkEntity(3, SHIP, 10, 9, 1, 0, 90, 0);
    checkEntity(5, SHIP, 11, 11, 5, 1, 75, 0);
    checkEntity(7, MINE, 6, 13, 0, 0, 0, 0);
    checkEntity(9, MINE, 4, 15, 0, 0, 0, 0);
    checkEntity(38, MINE, 4, 14, 0, 0, 0, 0);
    checkEntity(40, MINE, 0, 12, 0, 0, 0, 0);
    checkEntity(52, MINE, 2, 16, 0, 0, 0, 0);
    checkEntity(57, MINE, 6, 12, 0, 0, 0, 0);
    checkEntity(26, BARREL, 1, 11, 10, 0, 0, 0);
    checkEntity(33, BARREL, 19, 2, 11, 0, 0, 0);
    resetActions(); // 4974
    // ** nmahoude:
    doAction(STARBOARD);
    // ** Royale:
    doAction(STARBOARD);
    doAction(FASTER);
    doAction(PORT);
        simulation.playOneTurn(); // ** TURN43
    checkEntity(2, SHIP, 2, 12, 2, 1, 93, 1);
    checkEntity(1, SHIP, 8, 5, 0, 2, 82, 0);
    checkEntity(3, SHIP, 11, 8, 1, 1, 89, 0);
    checkEntity(5, SHIP, 12, 12, 0, 1, 74, 0);
    checkEntity(7, MINE, 6, 13, 0, 0, 0, 0);
    checkEntity(9, MINE, 4, 15, 0, 0, 0, 0);
    checkEntity(38, MINE, 4, 14, 0, 0, 0, 0);
    checkEntity(40, MINE, 0, 12, 0, 0, 0, 0);
    checkEntity(52, MINE, 2, 16, 0, 0, 0, 0);
    checkEntity(57, MINE, 6, 12, 0, 0, 0, 0);
    checkEntity(33, BARREL, 19, 2, 11, 0, 0, 0);
    resetActions(); // 5336
    // ** nmahoude:
    doAction(FASTER);
    // ** Royale:
    doAction(FASTER);
    doAction(STARBOARD);
    doAction(FASTER);
        simulation.playOneTurn(); // ** TURN44
    checkEntity(2, SHIP, 1, 10, 2, 2, 92, 1);
    checkEntity(1, SHIP, 10, 5, 0, 2, 81, 0);
    checkEntity(3, SHIP, 11, 7, 0, 1, 88, 0);
    checkEntity(5, SHIP, 14, 12, 0, 2, 73, 0);
    checkEntity(38, MINE, 4, 14, 0, 0, 0, 0);
    checkEntity(40, MINE, 0, 12, 0, 0, 0, 0);
    checkEntity(33, BARREL, 19, 2, 11, 0, 0, 0);
    resetActions(); // 4768
    // ** nmahoude:
    doAction(STARBOARD);
    // ** Royale:
    doAction(STARBOARD);
    doAction(STARBOARD);
    doAction(PORT);
        simulation.playOneTurn(); // ** TURN45
    checkEntity(2, SHIP, 0, 8, 1, 2, 91, 1);
    checkEntity(1, SHIP, 12, 5, 5, 2, 80, 0);
    checkEntity(3, SHIP, 12, 7, 5, 1, 87, 0);
    checkEntity(5, SHIP, 16, 12, 1, 2, 72, 0);
    checkEntity(40, MINE, 0, 12, 0, 0, 0, 0);
    checkEntity(33, BARREL, 19, 2, 11, 0, 0, 0);
    resetActions(); // 5148
    // ** nmahoude:
    doAction(SLOWER);
    // ** Royale:
    doAction(FASTER);
    doAction(FASTER);
    doAction(PORT);
        simulation.playOneTurn(); // ** TURN46
    checkEntity(2, SHIP, 0, 7, 1, 1, 90, 1);
    checkEntity(1, SHIP, 13, 7, 5, 2, 79, 0);
    checkEntity(3, SHIP, 13, 9, 5, 2, 86, 0);
    checkEntity(5, SHIP, 17, 10, 2, 2, 71, 0);
    checkEntity(8, MINE, 4, 5, 0, 0, 0, 0);
    checkEntity(40, MINE, 0, 12, 0, 0, 0, 0);
    checkEntity(33, BARREL, 19, 2, 11, 0, 0, 0);
    resetActions(); // 5334
    // ** nmahoude:
    doAction(FASTER);
    // ** Royale:
    doAction(STARBOARD);
    doAction(STARBOARD);
    doAction(FASTER);
        simulation.playOneTurn(); // ** TURN47
    checkEntity(2, SHIP, 1, 5, 1, 2, 89, 1);
    checkEntity(1, SHIP, 14, 9, 4, 2, 78, 0);
    checkEntity(3, SHIP, 14, 11, 4, 2, 85, 0);
    checkEntity(5, SHIP, 16, 8, 2, 2, 70, 0);
    checkEntity(8, MINE, 4, 5, 0, 0, 0, 0);
    checkEntity(33, BARREL, 19, 2, 11, 0, 0, 0);
    resetActions(); // 5334
    // ** nmahoude:
    doAction(STARBOARD);
    // ** Royale:
    doAction(STARBOARD);
    doAction(STARBOARD);
    doAction(PORT);
        simulation.playOneTurn(); // ** TURN48
    checkEntity(2, SHIP, 2, 3, 0, 2, 88, 1);
    checkEntity(1, SHIP, 13, 11, 3, 2, 77, 0);
    checkEntity(3, SHIP, 13, 13, 3, 2, 84, 0);
    checkEntity(5, SHIP, 15, 6, 3, 2, 69, 0);
    checkEntity(8, MINE, 4, 5, 0, 0, 0, 0);
    checkEntity(33, BARREL, 19, 2, 11, 0, 0, 0);
    resetActions(); // 5133
    // ** nmahoude:
    doAction(STARBOARD);
    // ** Royale:
    doAction(STARBOARD);
    doAction(FIRE, 22, 10);
    doAction(FASTER);
        simulation.playOneTurn(); // ** TURN49
    checkEntity(2, SHIP, 4, 3, 5, 2, 87, 1);
    checkEntity(1, SHIP, 11, 11, 2, 2, 76, 0);
    checkEntity(3, SHIP, 11, 13, 3, 2, 83, 0);
    checkEntity(5, SHIP, 13, 6, 3, 2, 68, 0);
    checkEntity(6, MINE, 6, 7, 0, 0, 0, 0);
    checkEntity(8, MINE, 4, 5, 0, 0, 0, 0);
    checkEntity(46, MINE, 7, 1, 0, 0, 0, 0);
    checkEntity(33, BARREL, 19, 2, 11, 0, 0, 0);
    resetActions(); // 5235
    // ** nmahoude:
    doAction(STARBOARD);
    // ** Royale:
    doAction(STARBOARD);
    doAction(STARBOARD);
    doAction(FIRE, 19, 2);
        simulation.playOneTurn(); // ** TURN50
    checkEntity(2, SHIP, 5, 5, 4, 2, 86, 1);
    checkEntity(1, SHIP, 10, 9, 1, 2, 75, 0);
    checkEntity(3, SHIP, 9, 13, 2, 2, 82, 0);
    checkEntity(5, SHIP, 11, 6, 3, 2, 67, 0);
    checkEntity(6, MINE, 6, 7, 0, 0, 0, 0);
    checkEntity(8, MINE, 4, 5, 0, 0, 0, 0);
    checkEntity(46, MINE, 7, 1, 0, 0, 0, 0);
    checkEntity(58, CANNONBALL, 19, 2, 5, 4, 0, 0);
    checkEntity(33, BARREL, 19, 2, 11, 0, 0, 0);
    resetActions(); // 5062
    // ** nmahoude:
    doAction(WAIT);
    // ** Royale:
    doAction(STARBOARD);
    doAction(FIRE, 18, 5);
    doAction(FASTER);
        simulation.playOneTurn(); // ** TURN51
    checkEntity(2, SHIP, 4, 7, 4, 2, 85, 1);
    checkEntity(1, SHIP, 11, 7, 0, 2, 74, 0);
    checkEntity(3, SHIP, 8, 11, 2, 2, 81, 0);
    checkEntity(5, SHIP, 9, 6, 3, 2, 66, 0);
    checkEntity(6, MINE, 6, 7, 0, 0, 0, 0);
    checkEntity(8, MINE, 4, 5, 0, 0, 0, 0);
    checkEntity(57, MINE, 6, 12, 0, 0, 0, 0);
    checkEntity(58, CANNONBALL, 19, 2, 5, 3, 0, 0);
    checkEntity(33, BARREL, 19, 2, 11, 0, 0, 0);
    resetActions(); // 4948
    // ** nmahoude:
    doAction(SLOWER);
    // ** Royale:
    doAction(STARBOARD);
    doAction(STARBOARD);
    doAction(STARBOARD);
        simulation.playOneTurn(); // ** TURN52
    checkEntity(2, SHIP, 4, 8, 4, 1, 84, 1);
    checkEntity(1, SHIP, 13, 7, 5, 2, 73, 0);
    checkEntity(3, SHIP, 7, 9, 1, 2, 80, 0);
    checkEntity(5, SHIP, 7, 6, 2, 2, 65, 0);
    checkEntity(7, MINE, 6, 13, 0, 0, 0, 0);
    checkEntity(6, MINE, 6, 7, 0, 0, 0, 0);
    checkEntity(8, MINE, 4, 5, 0, 0, 0, 0);
    checkEntity(57, MINE, 6, 12, 0, 0, 0, 0);
    checkEntity(58, CANNONBALL, 19, 2, 5, 2, 0, 0);
    checkEntity(33, BARREL, 19, 2, 11, 0, 0, 0);
    resetActions(); // 5284
    // ** nmahoude:
    doAction(WAIT);
    // ** Royale:
    doAction(SLOWER);
    doAction(FIRE, 6, 12);
    doAction(FASTER);
        simulation.playOneTurn(); // ** TURN53
    checkEntity(2, SHIP, 3, 9, 4, 1, 83, 1);
    checkEntity(1, SHIP, 14, 8, 5, 1, 72, 0);
    checkEntity(3, SHIP, 8, 7, 1, 2, 79, 0);
    checkEntity(5, SHIP, 6, 4, 2, 2, 64, 0);
    checkEntity(7, MINE, 6, 13, 0, 0, 0, 0);
    checkEntity(6, MINE, 6, 7, 0, 0, 0, 0);
    checkEntity(8, MINE, 4, 5, 0, 0, 0, 0);
    checkEntity(38, MINE, 4, 14, 0, 0, 0, 0);
    checkEntity(40, MINE, 0, 12, 0, 0, 0, 0);
    checkEntity(57, MINE, 6, 12, 0, 0, 0, 0);
    checkEntity(58, CANNONBALL, 19, 2, 5, 1, 0, 0);
    checkEntity(59, CANNONBALL, 6, 12, 3, 2, 0, 0);
    checkEntity(33, BARREL, 19, 2, 11, 0, 0, 0);
    resetActions(); // 3501
    // ** nmahoude:
    doAction(FASTER);
    // ** Royale:
    doAction(STARBOARD);
    doAction(STARBOARD);
    doAction(STARBOARD);
        simulation.playOneTurn(); // ** TURN54
    checkEntity(2, SHIP, 2, 11, 4, 2, 82, 1);
    checkEntity(1, SHIP, 14, 9, 4, 1, 71, 0);
    checkEntity(3, SHIP, 9, 5, 0, 2, 78, 0);
    checkEntity(5, SHIP, 5, 2, 1, 2, 63, 0);
    checkEntity(7, MINE, 6, 13, 0, 0, 0, 0);
    checkEntity(9, MINE, 4, 15, 0, 0, 0, 0);
    checkEntity(38, MINE, 4, 14, 0, 0, 0, 0);
    checkEntity(40, MINE, 0, 12, 0, 0, 0, 0);
    checkEntity(52, MINE, 2, 16, 0, 0, 0, 0);
    checkEntity(57, MINE, 6, 12, 0, 0, 0, 0);
    checkEntity(58, CANNONBALL, 19, 2, 5, 0, 0, 0);
    checkEntity(59, CANNONBALL, 6, 12, 3, 1, 0, 0);
    resetActions(); // 3748
    // ** nmahoude:
    doAction(WAIT);
    // ** Royale:
    doAction(STARBOARD);
    doAction(STARBOARD);
    doAction(STARBOARD);
        simulation.playOneTurn(); // ** TURN55
    checkEntity(2, SHIP, 1, 13, 4, 2, 81, 1);
    checkEntity(1, SHIP, 14, 10, 3, 1, 70, 0);
    checkEntity(3, SHIP, 11, 5, 5, 2, 77, 0);
    checkEntity(5, SHIP, 6, 0, 0, 2, 62, 0);
    checkEntity(7, MINE, 6, 13, 0, 0, 0, 0);
    checkEntity(9, MINE, 4, 15, 0, 0, 0, 0);
    checkEntity(38, MINE, 4, 14, 0, 0, 0, 0);
    checkEntity(40, MINE, 0, 12, 0, 0, 0, 0);
    checkEntity(52, MINE, 2, 16, 0, 0, 0, 0);
    checkEntity(59, CANNONBALL, 6, 12, 3, 0, 0, 0);
    resetActions(); // 5405
    // ** nmahoude:
    doAction(PORT);
    // ** Royale:
    doAction(FASTER);
    doAction(FIRE, 1, 13);
    doAction(STARBOARD);
        simulation.playOneTurn(); // ** TURN56
    checkEntity(2, SHIP, 0, 15, 5, 2, 80, 1);
    checkEntity(1, SHIP, 12, 10, 3, 2, 69, 0);
    checkEntity(3, SHIP, 12, 7, 5, 2, 76, 0);
    checkEntity(5, SHIP, 8, 0, 5, 2, 61, 0);
    checkEntity(9, MINE, 4, 15, 0, 0, 0, 0);
    checkEntity(38, MINE, 4, 14, 0, 0, 0, 0);
    checkEntity(40, MINE, 0, 12, 0, 0, 0, 0);
    checkEntity(52, MINE, 2, 16, 0, 0, 0, 0);
    resetActions(); // 5202
    // ** nmahoude:
    doAction(WAIT);
    // ** Royale:
    doAction(SLOWER);
    doAction(STARBOARD);
    doAction(FASTER);
        simulation.playOneTurn(); // ** TURN57
    checkEntity(2, SHIP, 1, 17, 5, 2, 79, 1);
    checkEntity(1, SHIP, 11, 10, 3, 1, 68, 0);
    checkEntity(3, SHIP, 13, 9, 4, 2, 75, 0);
    checkEntity(5, SHIP, 9, 2, 5, 2, 60, 0);
    checkEntity(9, MINE, 4, 15, 0, 0, 0, 0);
    checkEntity(38, MINE, 4, 14, 0, 0, 0, 0);
    checkEntity(40, MINE, 0, 12, 0, 0, 0, 0);
    checkEntity(52, MINE, 2, 16, 0, 0, 0, 0);
    resetActions(); // 4770
    // ** nmahoude:
    doAction(PORT);
    // ** Royale:
    doAction(FASTER);
    doAction(FIRE, 5, 2);
    doAction(FASTER);
        simulation.playOneTurn(); // ** TURN58
    checkEntity(2, SHIP, 2, 19, 0, 2, 78, 1);
    checkEntity(1, SHIP, 9, 10, 3, 2, 67, 0);
    checkEntity(3, SHIP, 12, 11, 4, 2, 74, 0);
    checkEntity(5, SHIP, 10, 4, 5, 2, 59, 0);
    checkEntity(9, MINE, 4, 15, 0, 0, 0, 0);
    checkEntity(38, MINE, 4, 14, 0, 0, 0, 0);
    checkEntity(52, MINE, 2, 16, 0, 0, 0, 0);
    resetActions(); // 4798
    // ** nmahoude:
    doAction(WAIT);
    // ** Royale:
    doAction(FASTER);
    doAction(STARBOARD);
    doAction(PORT);
        simulation.playOneTurn(); // ** TURN59
    checkEntity(2, SHIP, 4, 19, 0, 2, 77, 1);
    checkEntity(1, SHIP, 7, 10, 3, 2, 66, 0);
    checkEntity(3, SHIP, 11, 13, 3, 2, 73, 0);
    checkEntity(5, SHIP, 11, 6, 0, 2, 58, 0);
    checkEntity(9, MINE, 4, 15, 0, 0, 0, 0);
    checkEntity(38, MINE, 4, 14, 0, 0, 0, 0);
    checkEntity(52, MINE, 2, 16, 0, 0, 0, 0);
    resetActions(); // 5159
    // ** nmahoude:
    doAction(WAIT);
    // ** Royale:
    doAction(FIRE, 8, 14);
    doAction(STARBOARD);
    doAction(PORT);
        simulation.playOneTurn(); // ** TURN60
    checkEntity(2, SHIP, 6, 19, 0, 2, 76, 1);
    checkEntity(1, SHIP, 5, 10, 3, 2, 65, 0);
    checkEntity(3, SHIP, 9, 13, 2, 2, 72, 0);
    checkEntity(5, SHIP, 13, 6, 1, 2, 57, 0);
    checkEntity(9, MINE, 4, 15, 0, 0, 0, 0);
    checkEntity(38, MINE, 4, 14, 0, 0, 0, 0);
    checkEntity(60, CANNONBALL, 8, 14, 1, 2, 0, 0);
    resetActions(); // 5180
    // ** nmahoude:
    doAction(WAIT);
    // ** Royale:
    doAction(STARBOARD);
    doAction(FIRE, 7, 15);
    doAction(STARBOARD);
        simulation.playOneTurn(); // ** TURN61
    checkEntity(2, SHIP, 8, 19, 0, 2, 75, 1);
    checkEntity(1, SHIP, 3, 10, 2, 2, 64, 0);
    checkEntity(3, SHIP, 8, 11, 2, 2, 71, 0);
    checkEntity(5, SHIP, 14, 4, 0, 2, 56, 0);
    checkEntity(60, CANNONBALL, 8, 14, 1, 1, 0, 0);
    checkEntity(61, CANNONBALL, 7, 15, 3, 2, 0, 0);
    resetActions(); // 5046
    // ** nmahoude:
    doAction(WAIT);
    // ** Royale:
    doAction(STARBOARD);
    doAction(STARBOARD);
    doAction(STARBOARD);
        simulation.playOneTurn(); // ** TURN62
    checkEntity(2, SHIP, 10, 19, 0, 2, 74, 1);
    checkEntity(1, SHIP, 2, 8, 1, 2, 63, 0);
    checkEntity(3, SHIP, 7, 9, 1, 2, 70, 0);
    checkEntity(5, SHIP, 16, 4, 5, 2, 55, 0);
    checkEntity(60, CANNONBALL, 8, 14, 1, 0, 0, 0);
    checkEntity(61, CANNONBALL, 7, 15, 3, 1, 0, 0);
    resetActions(); // 3478
    // ** nmahoude:
    doAction(WAIT);
    // ** Royale:
    doAction(STARBOARD);
    doAction(STARBOARD);
    doAction(PORT);
        simulation.playOneTurn(); // ** TURN63
    checkEntity(2, SHIP, 12, 19, 0, 2, 73, 1);
    checkEntity(1, SHIP, 3, 6, 0, 2, 62, 0);
    checkEntity(3, SHIP, 8, 7, 0, 2, 69, 0);
    checkEntity(5, SHIP, 17, 6, 0, 2, 54, 0);
    checkEntity(61, CANNONBALL, 7, 15, 3, 0, 0, 0);
    resetActions(); // 4021
    // ** nmahoude:
    doAction(WAIT);
    // ** Royale:
    doAction(FASTER);
    doAction(STARBOARD);
    doAction(STARBOARD);
        simulation.playOneTurn(); // ** TURN64
    checkEntity(2, SHIP, 14, 19, 0, 2, 72, 1);
    checkEntity(1, SHIP, 5, 6, 0, 2, 61, 0);
    checkEntity(3, SHIP, 10, 7, 5, 2, 68, 0);
    checkEntity(5, SHIP, 19, 6, 5, 2, 53, 0);
    resetActions(); // 4589
    // ** nmahoude:
    doAction(PORT);
    // ** Royale:
    doAction(FASTER);
    doAction(FASTER);
    doAction(STARBOARD);
        simulation.playOneTurn(); // ** TURN65
    checkEntity(2, SHIP, 16, 19, 1, 2, 71, 1);
    checkEntity(1, SHIP, 7, 6, 0, 2, 60, 0);
    checkEntity(3, SHIP, 11, 9, 5, 2, 67, 0);
    checkEntity(5, SHIP, 20, 8, 4, 2, 52, 0);
    resetActions(); // 4824
    // ** nmahoude:
    doAction(SLOWER);
    // ** Royale:
    doAction(STARBOARD);
    doAction(STARBOARD);
    doAction(STARBOARD);
        simulation.playOneTurn(); // ** TURN66
    checkEntity(2, SHIP, 17, 18, 1, 1, 70, 1);
    checkEntity(1, SHIP, 9, 6, 5, 2, 59, 0);
    checkEntity(3, SHIP, 12, 11, 4, 2, 66, 0);
    checkEntity(5, SHIP, 19, 10, 3, 2, 51, 0);
    resetActions(); // 5302
    // ** nmahoude:
    doAction(WAIT);
    // ** Royale:
    doAction(FASTER);
    doAction(STARBOARD);
    doAction(FIRE, 17, 18);
        simulation.playOneTurn(); // ** TURN67
    checkEntity(2, SHIP, 17, 17, 1, 1, 69, 1);
    checkEntity(1, SHIP, 10, 8, 5, 2, 58, 0);
    checkEntity(3, SHIP, 11, 13, 3, 2, 65, 0);
    checkEntity(5, SHIP, 17, 10, 3, 2, 50, 0);
    checkEntity(62, CANNONBALL, 17, 18, 5, 4, 0, 0);
    resetActions(); // 5477
    // ** nmahoude:
    doAction(SLOWER);
    // ** Royale:
    doAction(FASTER);
    doAction(STARBOARD);
    doAction(STARBOARD);
        simulation.playOneTurn(); // ** TURN68
    checkEntity(2, SHIP, 17, 17, 1, 0, 68, 1);
    checkEntity(1, SHIP, 11, 10, 5, 2, 57, 0);
    checkEntity(3, SHIP, 9, 13, 2, 2, 64, 0);
    checkEntity(5, SHIP, 15, 10, 2, 2, 49, 0);
    checkEntity(62, CANNONBALL, 17, 18, 5, 3, 0, 0);
    resetActions(); // 4448
    // ** nmahoude:
    doAction(FASTER);
    // ** Royale:
    doAction(PORT);
    doAction(FASTER);
    doAction(FASTER);
        simulation.playOneTurn(); // ** TURN69
    checkEntity(2, SHIP, 18, 16, 1, 1, 67, 1);
    checkEntity(1, SHIP, 12, 12, 0, 2, 56, 0);
    checkEntity(3, SHIP, 8, 11, 2, 2, 63, 0);
    checkEntity(5, SHIP, 14, 8, 2, 2, 48, 0);
    checkEntity(62, CANNONBALL, 17, 18, 5, 2, 0, 0);
    resetActions(); // 5033
    // ** nmahoude:
    doAction(FASTER);
    // ** Royale:
    doAction(PORT);
    doAction(STARBOARD);
    doAction(PORT);
        simulation.playOneTurn(); // ** TURN70
    checkEntity(2, SHIP, 19, 14, 1, 2, 66, 1);
    checkEntity(1, SHIP, 14, 12, 1, 2, 55, 0);
    checkEntity(3, SHIP, 7, 9, 1, 2, 62, 0);
    checkEntity(5, SHIP, 13, 6, 3, 2, 47, 0);
    checkEntity(10, MINE, 18, 9, 0, 0, 0, 0);
    checkEntity(62, CANNONBALL, 17, 18, 5, 1, 0, 0);
    resetActions(); // 4318
    // ** nmahoude:
    doAction(WAIT);
    // ** Royale:
    doAction(PORT);
    doAction(STARBOARD);
    doAction(FASTER);
        simulation.playOneTurn(); // ** TURN71
    checkEntity(2, SHIP, 20, 12, 1, 2, 65, 1);
    checkEntity(1, SHIP, 15, 10, 2, 2, 54, 0);
    checkEntity(3, SHIP, 8, 7, 0, 2, 61, 0);
    checkEntity(5, SHIP, 11, 6, 3, 2, 46, 0);
    checkEntity(10, MINE, 18, 9, 0, 0, 0, 0);
    checkEntity(62, CANNONBALL, 17, 18, 5, 0, 0, 0);
    resetActions(); // 5253
    // ** nmahoude:
    doAction(PORT);
    // ** Royale:
    doAction(FIRE, 18, 8);
    doAction(FASTER);
    doAction(FASTER);
        simulation.playOneTurn(); // ** TURN72
    checkEntity(2, SHIP, 21, 10, 2, 2, 64, 1);
    checkEntity(1, SHIP, 14, 8, 2, 2, 53, 0);
    checkEntity(3, SHIP, 10, 7, 0, 2, 60, 0);
    checkEntity(5, SHIP, 9, 6, 3, 2, 45, 0);
    checkEntity(10, MINE, 18, 9, 0, 0, 0, 0);
    checkEntity(63, CANNONBALL, 18, 8, 1, 2, 0, 0);
    resetActions(); // 5414
    // ** nmahoude:
    doAction(SLOWER);
    // ** Royale:
    doAction(PORT);
    doAction(FASTER);
    doAction(SLOWER);
        simulation.playOneTurn(); // ** TURN73
    checkEntity(2, SHIP, 20, 9, 2, 1, 63, 1);
    checkEntity(1, SHIP, 13, 6, 3, 2, 52, 0);
    checkEntity(3, SHIP, 11, 7, 0, 0, 59, 0);
    checkEntity(5, SHIP, 8, 6, 3, 1, 44, 0);
    checkEntity(10, MINE, 18, 9, 0, 0, 0, 0);
    checkEntity(37, MINE, 16, 7, 0, 0, 0, 0);
    checkEntity(63, CANNONBALL, 18, 8, 1, 1, 0, 0);
    resetActions(); // 4867
    // ** nmahoude:
    doAction(FASTER);
    // ** Royale:
    doAction(FIRE, 15, 8);
    doAction(FASTER);
    doAction(SLOWER);
        simulation.playOneTurn(); // ** TURN74
    checkEntity(2, SHIP, 19, 7, 2, 2, 62, 1);
    checkEntity(1, SHIP, 11, 6, 3, 2, 51, 0);
    checkEntity(3, SHIP, 12, 7, 0, 1, 58, 0);
    checkEntity(5, SHIP, 8, 6, 3, 0, 43, 0);
    checkEntity(10, MINE, 18, 9, 0, 0, 0, 0);
    checkEntity(37, MINE, 16, 7, 0, 0, 0, 0);
    checkEntity(47, MINE, 17, 2, 0, 0, 0, 0);
    checkEntity(63, CANNONBALL, 18, 8, 1, 0, 0, 0);
    checkEntity(64, CANNONBALL, 15, 8, 1, 2, 0, 0);
    resetActions(); // 4450
    // ** nmahoude:
    doAction(PORT);
    // ** Royale:
    doAction(PORT);
    doAction(STARBOARD);
    doAction(FASTER);
        simulation.playOneTurn(); // ** TURN75
    checkEntity(2, SHIP, 18, 5, 3, 2, 61, 1);
    checkEntity(1, SHIP, 10, 6, 4, 0, 50, 0);
    checkEntity(3, SHIP, 13, 7, 5, 1, 57, 0);
    checkEntity(5, SHIP, 7, 6, 3, 1, 42, 0);
    checkEntity(10, MINE, 18, 9, 0, 0, 0, 0);
    checkEntity(37, MINE, 16, 7, 0, 0, 0, 0);
    checkEntity(47, MINE, 17, 2, 0, 0, 0, 0);
    checkEntity(64, CANNONBALL, 15, 8, 1, 1, 0, 0);
    resetActions(); // 4852
    // ** nmahoude:
    doAction(WAIT);
    // ** Royale:
    doAction(SLOWER);
    doAction(FIRE, 14, 5);
    doAction(FASTER);
        simulation.playOneTurn(); // ** TURN76
    checkEntity(2, SHIP, 16, 5, 3, 2, 60, 1);
    checkEntity(1, SHIP, 10, 6, 4, 0, 49, 0);
    checkEntity(3, SHIP, 14, 8, 5, 1, 56, 0);
    checkEntity(5, SHIP, 5, 6, 3, 2, 41, 0);
    checkEntity(10, MINE, 18, 9, 0, 0, 0, 0);
    checkEntity(37, MINE, 16, 7, 0, 0, 0, 0);
    checkEntity(47, MINE, 17, 2, 0, 0, 0, 0);
    checkEntity(64, CANNONBALL, 15, 8, 1, 0, 0, 0);
    checkEntity(65, CANNONBALL, 14, 5, 3, 2, 0, 0);
    resetActions(); // 4774
    // ** nmahoude:
    doAction(STARBOARD);
    // ** Royale:
    doAction(FASTER);
    doAction(SLOWER);
    doAction(PORT);
        simulation.playOneTurn(); // ** TURN77
    checkEntity(2, SHIP, 14, 5, 2, 2, 59, 1);
    checkEntity(1, SHIP, 9, 7, 4, 1, 48, 0);
    checkEntity(3, SHIP, 14, 8, 5, 0, 55, 0);
    checkEntity(5, SHIP, 3, 6, 4, 2, 40, 0);
    checkEntity(37, MINE, 16, 7, 0, 0, 0, 0);
    checkEntity(41, MINE, 10, 3, 0, 0, 0, 0);
    checkEntity(44, MINE, 12, 8, 0, 0, 0, 0);
    checkEntity(47, MINE, 17, 2, 0, 0, 0, 0);
    checkEntity(65, CANNONBALL, 14, 5, 3, 1, 0, 0);
    resetActions(); // 4535
    // ** nmahoude:
    doAction(WAIT);
    // ** Royale:
    doAction(FASTER);
    doAction(FASTER);
    doAction(PORT);
        simulation.playOneTurn(); // ** TURN78
    checkEntity(2, SHIP, 13, 3, 2, 2, 58, 1);
    checkEntity(1, SHIP, 8, 9, 4, 2, 47, 0);
    checkEntity(3, SHIP, 14, 9, 5, 1, 54, 0);
    checkEntity(5, SHIP, 2, 8, 5, 2, 39, 0);
    checkEntity(37, MINE, 16, 7, 0, 0, 0, 0);
    checkEntity(41, MINE, 10, 3, 0, 0, 0, 0);
    checkEntity(44, MINE, 12, 8, 0, 0, 0, 0);
    checkEntity(47, MINE, 17, 2, 0, 0, 0, 0);
    checkEntity(65, CANNONBALL, 14, 5, 3, 0, 0, 0);
    resetActions(); // 4575
    // ** nmahoude:
    doAction(cotc.entities.Action.MINE);
    // ** Royale:
    doAction(PORT);
    doAction(FASTER);
    doAction(PORT);
        simulation.playOneTurn(); // ** TURN79
    checkEntity(2, SHIP, 12, 1, 2, 2, 57, 1);
    checkEntity(1, SHIP, 7, 11, 5, 2, 46, 0);
    checkEntity(3, SHIP, 15, 11, 5, 2, 53, 0);
    checkEntity(5, SHIP, 3, 10, 0, 2, 38, 0);
    checkEntity(41, MINE, 10, 3, 0, 0, 0, 0);
    checkEntity(46, MINE, 7, 1, 0, 0, 0, 0);
    checkEntity(47, MINE, 17, 2, 0, 0, 0, 0);
    checkEntity(66, MINE, 14, 5, 0, 0, 0, 0);
    resetActions(); // 4548
    // ** nmahoude:
    doAction(PORT);
    // ** Royale:
    doAction(PORT);
    doAction(STARBOARD);
    doAction(FASTER);
        simulation.playOneTurn(); // ** TURN80
    checkEntity(2, SHIP, 12, 0, 3, 0, 56, 1);
    checkEntity(1, SHIP, 8, 13, 0, 2, 45, 0);
    checkEntity(3, SHIP, 16, 13, 4, 2, 52, 0);
    checkEntity(5, SHIP, 5, 10, 0, 2, 37, 0);
    checkEntity(41, MINE, 10, 3, 0, 0, 0, 0);
    checkEntity(46, MINE, 7, 1, 0, 0, 0, 0);
    checkEntity(66, MINE, 14, 5, 0, 0, 0, 0);
    resetActions(); // 5142
    // ** nmahoude:
    doAction(FASTER);
    // ** Royale:
    doAction(PORT);
    doAction(STARBOARD);
    doAction(FASTER);
        simulation.playOneTurn(); // ** TURN81
    checkEntity(2, SHIP, 11, 0, 3, 1, 55, 1);
    checkEntity(1, SHIP, 10, 13, 1, 2, 44, 0);
    checkEntity(3, SHIP, 15, 15, 3, 2, 51, 0);
    checkEntity(5, SHIP, 7, 10, 0, 2, 36, 0);
    checkEntity(41, MINE, 10, 3, 0, 0, 0, 0);
    checkEntity(46, MINE, 7, 1, 0, 0, 0, 0);
    resetActions(); // 4078
    // ** nmahoude:
    doAction(PORT);
    // ** Royale:
    doAction(STARBOARD);
    doAction(STARBOARD);
    doAction(FASTER);
        simulation.playOneTurn(); // ** TURN82
    checkEntity(2, SHIP, 10, 0, 4, 1, 54, 1);
    checkEntity(1, SHIP, 11, 11, 0, 2, 43, 0);
    checkEntity(3, SHIP, 13, 15, 2, 2, 50, 0);
    checkEntity(5, SHIP, 9, 10, 0, 2, 35, 0);
    checkEntity(41, MINE, 10, 3, 0, 0, 0, 0);
    checkEntity(46, MINE, 7, 1, 0, 0, 0, 0);
    resetActions(); // 4882
    // ** nmahoude:
    doAction(FASTER);
    // ** Royale:
    doAction(STARBOARD);
    doAction(FASTER);
    doAction(FIRE, 2, 2);
        simulation.playOneTurn(); // ** TURN83
    checkEntity(2, SHIP, 9, 2, 4, 2, 53, 1);
    checkEntity(1, SHIP, 13, 11, 5, 2, 42, 0);
    checkEntity(3, SHIP, 12, 13, 2, 2, 49, 0);
    checkEntity(5, SHIP, 11, 10, 0, 2, 34, 0);
    checkEntity(6, MINE, 6, 7, 0, 0, 0, 0);
    checkEntity(41, MINE, 10, 3, 0, 0, 0, 0);
    checkEntity(46, MINE, 7, 1, 0, 0, 0, 0);
    resetActions(); // 4990
    // ** nmahoude:
    doAction(STARBOARD);
    // ** Royale:
    doAction(STARBOARD);
    doAction(FIRE, 5, 20);
    doAction(PORT);
        simulation.playOneTurn(); // ** TURN84
    checkEntity(2, SHIP, 8, 4, 3, 2, 52, 1);
    checkEntity(1, SHIP, 14, 13, 4, 2, 41, 0);
    checkEntity(3, SHIP, 11, 11, 2, 2, 48, 0);
    checkEntity(5, SHIP, 13, 10, 1, 2, 33, 0);
    checkEntity(6, MINE, 6, 7, 0, 0, 0, 0);
    checkEntity(8, MINE, 4, 5, 0, 0, 0, 0);
    checkEntity(12, MINE, 9, 9, 0, 0, 0, 0);
    checkEntity(41, MINE, 10, 3, 0, 0, 0, 0);
    checkEntity(46, MINE, 7, 1, 0, 0, 0, 0);
    resetActions(); // 5150
    // ** nmahoude:
    doAction(STARBOARD);
    // ** Royale:
    doAction(STARBOARD);
    doAction(FIRE, 2, 19);
    doAction(PORT);
        simulation.playOneTurn(); // ** TURN85
    checkEntity(2, SHIP, 6, 4, 2, 2, 51, 1);
    checkEntity(1, SHIP, 13, 15, 3, 2, 40, 0);
    checkEntity(3, SHIP, 10, 9, 2, 2, 47, 0);
    checkEntity(5, SHIP, 14, 8, 2, 2, 32, 0);
    checkEntity(6, MINE, 6, 7, 0, 0, 0, 0);
    checkEntity(8, MINE, 4, 5, 0, 0, 0, 0);
    checkEntity(41, MINE, 10, 3, 0, 0, 0, 0);
    checkEntity(46, MINE, 7, 1, 0, 0, 0, 0);
    resetActions(); // 5203
    // ** nmahoude:
    doAction(SLOWER);
    // ** Royale:
    doAction(STARBOARD);
    doAction(FIRE, 11, 3);
    doAction(PORT);
        simulation.playOneTurn(); // ** TURN86
    checkEntity(2, SHIP, 5, 3, 2, 1, 50, 1);
    checkEntity(1, SHIP, 11, 15, 2, 2, 39, 0);
    checkEntity(3, SHIP, 9, 7, 2, 2, 46, 0);
    checkEntity(5, SHIP, 13, 6, 3, 2, 31, 0);
    checkEntity(6, MINE, 6, 7, 0, 0, 0, 0);
    checkEntity(8, MINE, 4, 5, 0, 0, 0, 0);
    checkEntity(41, MINE, 10, 3, 0, 0, 0, 0);
    checkEntity(46, MINE, 7, 1, 0, 0, 0, 0);
    checkEntity(67, CANNONBALL, 11, 3, 3, 3, 0, 0);
    resetActions(); // 5057
    // ** nmahoude:
    doAction(cotc.entities.Action.MINE);
    // ** Royale:
    doAction(STARBOARD);
    doAction(PORT);
    doAction(FASTER);
        simulation.playOneTurn(); // ** TURN87
    checkEntity(2, SHIP, 5, 2, 2, 1, 49, 1);
    checkEntity(1, SHIP, 10, 13, 1, 2, 38, 0);
    checkEntity(3, SHIP, 8, 5, 3, 2, 45, 0);
    checkEntity(5, SHIP, 11, 6, 3, 2, 30, 0);
    checkEntity(6, MINE, 6, 7, 0, 0, 0, 0);
    checkEntity(8, MINE, 4, 5, 0, 0, 0, 0);
    checkEntity(46, MINE, 7, 1, 0, 0, 0, 0);
    checkEntity(68, MINE, 6, 5, 0, 0, 0, 0);
    checkEntity(67, CANNONBALL, 11, 3, 3, 2, 0, 0);
    resetActions(); // 5672
    // ** nmahoude:
    doAction(PORT);
    // ** Royale:
    doAction(PORT);
    doAction(PORT);
    doAction(PORT);
        simulation.playOneTurn(); // ** TURN88
    checkEntity(2, SHIP, 4, 1, 3, 1, 48, 1);
    checkEntity(1, SHIP, 11, 11, 2, 2, 37, 0);
    checkEntity(3, SHIP, 6, 5, 4, 2, 19, 0);
    checkEntity(5, SHIP, 9, 6, 4, 2, 29, 0);
    checkEntity(8, MINE, 4, 5, 0, 0, 0, 0);
    checkEntity(46, MINE, 7, 1, 0, 0, 0, 0);
    checkEntity(67, CANNONBALL, 11, 3, 3, 1, 0, 0);
    resetActions(); // 5382
    // ** nmahoude:
    doAction(cotc.entities.Action.MINE);
    // ** Royale:
    doAction(FASTER);
    doAction(FIRE, 14, 1);
    doAction(FASTER);
        simulation.playOneTurn(); // ** TURN89
    checkEntity(2, SHIP, 3, 1, 3, 1, 47, 1);
    checkEntity(1, SHIP, 10, 9, 2, 2, 36, 0);
    checkEntity(3, SHIP, 5, 7, 4, 2, 18, 0);
    checkEntity(5, SHIP, 8, 8, 4, 2, 28, 0);
    checkEntity(8, MINE, 4, 5, 0, 0, 0, 0);
    checkEntity(46, MINE, 7, 1, 0, 0, 0, 0);
    checkEntity(67, CANNONBALL, 11, 3, 3, 0, 0, 0);
    resetActions(); // 4884
    // ** nmahoude:
    doAction(PORT);
    // ** Royale:
    doAction(FASTER);
    doAction(PORT);
    doAction(PORT);
        simulation.playOneTurn(); // ** TURN90
    checkEntity(2, SHIP, 2, 1, 4, 1, 46, 1);
    checkEntity(1, SHIP, 9, 7, 2, 2, 35, 0);
    checkEntity(3, SHIP, 4, 9, 5, 2, 17, 0);
    checkEntity(5, SHIP, 7, 10, 5, 2, 27, 0);
    checkEntity(8, MINE, 4, 5, 0, 0, 0, 0);
    checkEntity(46, MINE, 7, 1, 0, 0, 0, 0);
    resetActions(); // 5122
    // ** nmahoude:
    doAction(cotc.entities.Action.MINE);
    // ** Royale:
    doAction(FIRE, 1, 0);
    doAction(PORT);
    doAction(PORT);
        simulation.playOneTurn(); // ** TURN91
    checkEntity(2, SHIP, 2, 2, 4, 1, 45, 1);
    checkEntity(1, SHIP, 8, 5, 2, 2, 34, 0);
    checkEntity(3, SHIP, 5, 11, 0, 2, 16, 0);
    checkEntity(5, SHIP, 8, 12, 0, 2, 26, 0);
    checkEntity(8, MINE, 4, 5, 0, 0, 0, 0);
    resetActions(); // 4954
    // ** nmahoude:
    doAction(WAIT);
    // ** Royale:
    doAction(PORT);
    doAction(PORT);
    doAction(PORT);
        simulation.playOneTurn(); // ** TURN92
    checkEntity(2, SHIP, 1, 3, 4, 1, 44, 1);
    checkEntity(1, SHIP, 7, 3, 3, 2, 33, 0);
    checkEntity(3, SHIP, 7, 11, 1, 2, 15, 0);
    checkEntity(5, SHIP, 10, 12, 1, 2, 25, 0);
    checkEntity(8, MINE, 4, 5, 0, 0, 0, 0);
    resetActions(); // 5146
    // ** nmahoude:
    doAction(PORT);
    // ** Royale:
    doAction(FASTER);
    doAction(FASTER);
    doAction(STARBOARD);
        simulation.playOneTurn(); // ** TURN93
    checkEntity(2, SHIP, 1, 4, 5, 1, 43, 1);
    checkEntity(1, SHIP, 5, 3, 3, 2, 32, 0);
    checkEntity(3, SHIP, 8, 9, 1, 2, 14, 0);
    checkEntity(5, SHIP, 11, 10, 0, 2, 24, 0);
    checkEntity(8, MINE, 4, 5, 0, 0, 0, 0);
    resetActions(); // 5215
    // ** nmahoude:
    doAction(WAIT);
    // ** Royale:
    doAction(SLOWER);
    doAction(STARBOARD);
    doAction(STARBOARD);
        simulation.playOneTurn(); // ** TURN94
    checkEntity(2, SHIP, 1, 5, 5, 1, 42, 1);
    checkEntity(1, SHIP, 4, 3, 3, 1, 31, 0);
    checkEntity(3, SHIP, 9, 7, 0, 2, 13, 0);
    checkEntity(5, SHIP, 13, 10, 5, 2, 23, 0);
    checkEntity(8, MINE, 4, 5, 0, 0, 0, 0);
    resetActions(); // 5116
    // ** nmahoude:
    doAction(cotc.entities.Action.MINE);
    // ** Royale:
    doAction(PORT);
    doAction(FASTER);
    doAction(STARBOARD);
        simulation.playOneTurn(); // ** TURN95
    checkEntity(2, SHIP, 2, 6, 5, 1, 41, 1);
    checkEntity(1, SHIP, 3, 3, 4, 1, 30, 0);
    checkEntity(3, SHIP, 11, 7, 0, 2, 12, 0);
    checkEntity(5, SHIP, 14, 12, 4, 2, 22, 0);
    checkEntity(6, MINE, 6, 7, 0, 0, 0, 0);
    checkEntity(8, MINE, 4, 5, 0, 0, 0, 0);
    checkEntity(69, MINE, 0, 3, 0, 0, 0, 0);
    resetActions(); // 4948
    // ** nmahoude:
    doAction(STARBOARD);
    // ** Royale:
    doAction(PORT);
    doAction(STARBOARD);
    doAction(STARBOARD);
        simulation.playOneTurn(); // ** TURN96
    checkEntity(2, SHIP, 2, 7, 4, 1, 40, 1);
    checkEntity(1, SHIP, 3, 4, 5, 1, 29, 0);
    checkEntity(3, SHIP, 13, 7, 5, 2, 11, 0);
    checkEntity(5, SHIP, 13, 14, 3, 2, 21, 0);
    checkEntity(6, MINE, 6, 7, 0, 0, 0, 0);
    checkEntity(8, MINE, 4, 5, 0, 0, 0, 0);
    checkEntity(40, MINE, 0, 12, 0, 0, 0, 0);
    checkEntity(69, MINE, 0, 3, 0, 0, 0, 0);
    resetActions(); // 5390
    // ** nmahoude:
    doAction(PORT);
    // ** Royale:
    doAction(FASTER);
    doAction(STARBOARD);
    doAction(FASTER);
        simulation.playOneTurn(); // ** TURN97
    checkEntity(2, SHIP, 2, 8, 5, 1, 39, 1);
    checkEntity(1, SHIP, 4, 6, 5, 2, 28, 0);
    checkEntity(3, SHIP, 14, 9, 4, 2, 10, 0);
    checkEntity(5, SHIP, 11, 14, 3, 2, 20, 0);
    checkEntity(6, MINE, 6, 7, 0, 0, 0, 0);
    checkEntity(8, MINE, 4, 5, 0, 0, 0, 0);
    checkEntity(40, MINE, 0, 12, 0, 0, 0, 0);
    checkEntity(69, MINE, 0, 3, 0, 0, 0, 0);
    resetActions(); // 5231
    // ** nmahoude:
    doAction(WAIT);
    // ** Royale:
    doAction(PORT);
    doAction(STARBOARD);
    doAction(STARBOARD);
        simulation.playOneTurn(); // ** TURN98
    checkEntity(2, SHIP, 2, 9, 5, 1, 38, 1);
    checkEntity(1, SHIP, 5, 8, 0, 2, 27, 0);
    checkEntity(3, SHIP, 13, 11, 3, 2, 9, 0);
    checkEntity(5, SHIP, 9, 14, 2, 2, 19, 0);
    checkEntity(6, MINE, 6, 7, 0, 0, 0, 0);
    checkEntity(8, MINE, 4, 5, 0, 0, 0, 0);
    checkEntity(38, MINE, 4, 14, 0, 0, 0, 0);
    checkEntity(40, MINE, 0, 12, 0, 0, 0, 0);
    resetActions(); // 5435
    // ** nmahoude:
    doAction(PORT);
    // ** Royale:
    doAction(PORT);
    doAction(PORT);
    doAction(PORT);
        simulation.playOneTurn(); // ** TURN99
    checkEntity(2, SHIP, 3, 10, 0, 1, 37, 1);
    checkEntity(1, SHIP, 7, 8, 1, 2, 26, 0);
    checkEntity(3, SHIP, 11, 11, 4, 2, 8, 0);
    checkEntity(5, SHIP, 8, 12, 3, 2, 18, 0);
    checkEntity(7, MINE, 6, 13, 0, 0, 0, 0);
    checkEntity(6, MINE, 6, 7, 0, 0, 0, 0);
    checkEntity(9, MINE, 4, 15, 0, 0, 0, 0);
    checkEntity(8, MINE, 4, 5, 0, 0, 0, 0);
    checkEntity(38, MINE, 4, 14, 0, 0, 0, 0);
    checkEntity(40, MINE, 0, 12, 0, 0, 0, 0);
    resetActions(); // 5400
    // ** nmahoude:
    doAction(STARBOARD);
    // ** Royale:
    doAction(STARBOARD);
    doAction(FASTER);
    doAction(SLOWER);
        simulation.playOneTurn(); // ** TURN100
    checkEntity(2, SHIP, 4, 10, 5, 1, 36, 1);
    checkEntity(1, SHIP, 8, 6, 0, 2, 25, 0);
    checkEntity(3, SHIP, 10, 13, 4, 2, 7, 0);
    checkEntity(5, SHIP, 7, 12, 3, 1, 17, 0);
    checkEntity(7, MINE, 6, 13, 0, 0, 0, 0);
    checkEntity(6, MINE, 6, 7, 0, 0, 0, 0);
    checkEntity(9, MINE, 4, 15, 0, 0, 0, 0);
    checkEntity(8, MINE, 4, 5, 0, 0, 0, 0);
    checkEntity(38, MINE, 4, 14, 0, 0, 0, 0);
    checkEntity(40, MINE, 0, 12, 0, 0, 0, 0);
    resetActions(); // 5144
    // ** nmahoude:
    doAction(WAIT);
    // ** Royale:
    doAction(FASTER);
    doAction(FASTER);
    doAction(STARBOARD);
        simulation.playOneTurn(); // ** TURN101
    checkEntity(2, SHIP, 4, 10, 5, 0, 35, 1);
    checkEntity(1, SHIP, 10, 6, 0, 2, 24, 0);
    checkEntity(3, SHIP, 9, 15, 4, 2, 6, 0);
    checkEntity(5, SHIP, 7, 12, 2, 0, 16, 0);
    checkEntity(7, MINE, 6, 13, 0, 0, 0, 0);
    checkEntity(6, MINE, 6, 7, 0, 0, 0, 0);
    checkEntity(9, MINE, 4, 15, 0, 0, 0, 0);
    checkEntity(8, MINE, 4, 5, 0, 0, 0, 0);
    checkEntity(38, MINE, 4, 14, 0, 0, 0, 0);
    checkEntity(40, MINE, 0, 12, 0, 0, 0, 0);
    resetActions(); // 4368
    // ** nmahoude:
    doAction(WAIT);
    // ** Royale:
    doAction(STARBOARD);
    doAction(STARBOARD);
    doAction(FIRE, 4, 10);
        simulation.playOneTurn(); // ** TURN102
    checkEntity(2, SHIP, 4, 10, 5, 0, 34, 1);
    checkEntity(1, SHIP, 12, 6, 5, 2, 23, 0);
    checkEntity(3, SHIP, 8, 17, 3, 2, 5, 0);
    checkEntity(5, SHIP, 7, 12, 2, 0, 15, 0);
    checkEntity(7, MINE, 6, 13, 0, 0, 0, 0);
    checkEntity(6, MINE, 6, 7, 0, 0, 0, 0);
    checkEntity(9, MINE, 4, 15, 0, 0, 0, 0);
    checkEntity(8, MINE, 4, 5, 0, 0, 0, 0);
    checkEntity(38, MINE, 4, 14, 0, 0, 0, 0);
    checkEntity(40, MINE, 0, 12, 0, 0, 0, 0);
    checkEntity(70, CANNONBALL, 4, 10, 5, 2, 0, 0);
    resetActions(); // 7366
    // ** nmahoude:
    doAction(FASTER);
    // ** Royale:
    doAction(FASTER);
    doAction(STARBOARD);
    doAction(FASTER);
        simulation.playOneTurn(); // ** TURN103
    checkEntity(2, SHIP, 4, 11, 5, 1, 33, 1);
    checkEntity(1, SHIP, 13, 8, 5, 2, 22, 0);
    checkEntity(3, SHIP, 6, 17, 2, 2, 4, 0);
    checkEntity(5, SHIP, 6, 11, 2, 1, 14, 0);
    checkEntity(7, MINE, 6, 13, 0, 0, 0, 0);
    checkEntity(6, MINE, 6, 7, 0, 0, 0, 0);
    checkEntity(9, MINE, 4, 15, 0, 0, 0, 0);
    checkEntity(13, MINE, 9, 11, 0, 0, 0, 0);
    checkEntity(38, MINE, 4, 14, 0, 0, 0, 0);
    checkEntity(40, MINE, 0, 12, 0, 0, 0, 0);
    checkEntity(52, MINE, 2, 16, 0, 0, 0, 0);
    checkEntity(70, CANNONBALL, 4, 10, 5, 1, 0, 0);
    resetActions(); // 5607
    // ** nmahoude:
    doAction(WAIT);
    // ** Royale:
    doAction(STARBOARD);
    doAction(FIRE, 4, 11);
    doAction(FIRE, 9, 11);
        simulation.playOneTurn(); // ** TURN104
    checkEntity(2, SHIP, 5, 12, 5, 1, 32, 1);
    checkEntity(1, SHIP, 14, 10, 4, 2, 21, 0);
    checkEntity(3, SHIP, 5, 15, 2, 2, 3, 0);
    checkEntity(5, SHIP, 6, 10, 2, 1, 13, 0);
    checkEntity(7, MINE, 6, 13, 0, 0, 0, 0);
    checkEntity(6, MINE, 6, 7, 0, 0, 0, 0);
    checkEntity(9, MINE, 4, 15, 0, 0, 0, 0);
    checkEntity(13, MINE, 9, 11, 0, 0, 0, 0);
    checkEntity(38, MINE, 4, 14, 0, 0, 0, 0);
    checkEntity(40, MINE, 0, 12, 0, 0, 0, 0);
    checkEntity(52, MINE, 2, 16, 0, 0, 0, 0);
    checkEntity(70, CANNONBALL, 4, 10, 5, 0, 0, 0);
    checkEntity(71, CANNONBALL, 4, 11, 3, 3, 0, 0);
    checkEntity(72, CANNONBALL, 9, 11, 5, 2, 0, 0);
    resetActions(); // 5229
    // ** nmahoude:
    doAction(FASTER);
    // ** Royale:
    doAction(STARBOARD);
    doAction(SLOWER);
    doAction(FASTER);
        simulation.playOneTurn(); // ** TURN105
    checkEntity(2, SHIP, 6, 14, 5, 2, 31, 1);
    checkEntity(1, SHIP, 13, 12, 3, 2, 20, 0);
    checkEntity(3, SHIP, 5, 14, 2, 1, 2, 0);
    checkEntity(5, SHIP, 5, 8, 2, 2, 12, 0);
    checkEntity(7, MINE, 6, 13, 0, 0, 0, 0);
    checkEntity(9, MINE, 4, 15, 0, 0, 0, 0);
    checkEntity(13, MINE, 9, 11, 0, 0, 0, 0);
    checkEntity(38, MINE, 4, 14, 0, 0, 0, 0);
    checkEntity(52, MINE, 2, 16, 0, 0, 0, 0);
    checkEntity(71, CANNONBALL, 4, 11, 3, 2, 0, 0);
    checkEntity(72, CANNONBALL, 9, 11, 5, 1, 0, 0);
    resetActions(); // 5190
    // ** nmahoude:
    doAction(cotc.entities.Action.MINE);
    // ** Royale:
    doAction(SLOWER);
    doAction(FASTER);
    doAction(PORT);
        simulation.playOneTurn(); // ** TURN106
    checkEntity(2, SHIP, 7, 16, 5, 2, 30, 1);
    checkEntity(1, SHIP, 12, 12, 3, 1, 19, 0);
    checkEntity(3, SHIP, 4, 12, 2, 2, 1, 0);
    checkEntity(5, SHIP, 4, 6, 3, 2, 11, 0);
    checkEntity(7, MINE, 6, 13, 0, 0, 0, 0);
    checkEntity(9, MINE, 4, 15, 0, 0, 0, 0);
    checkEntity(38, MINE, 4, 14, 0, 0, 0, 0);
    checkEntity(52, MINE, 2, 16, 0, 0, 0, 0);
    checkEntity(73, MINE, 5, 12, 0, 0, 0, 0);
    checkEntity(71, CANNONBALL, 4, 11, 3, 1, 0, 0);
    checkEntity(72, CANNONBALL, 9, 11, 5, 0, 0, 0);
    resetActions(); // 5409
    // ** nmahoude:
    doAction(PORT);
    // ** Royale:
    doAction(STARBOARD);
    doAction(FASTER);
    doAction(PORT);
        simulation.playOneTurn(); // ** TURN107
    checkEntity(2, SHIP, 8, 18, 0, 2, 29, 1);
    checkEntity(1, SHIP, 11, 12, 2, 1, 18, 0);
    checkEntity(5, SHIP, 2, 6, 4, 2, 10, 0);
    checkEntity(7, MINE, 6, 13, 0, 0, 0, 0);
    checkEntity(9, MINE, 4, 15, 0, 0, 0, 0);
    checkEntity(71, CANNONBALL, 4, 11, 3, 0, 0, 0);
    resetActions(); // 6466
    // ** nmahoude:
    doAction(PORT);
    // ** Royale:
    doAction(PORT);
    doAction(PORT);
        simulation.playOneTurn(); // ** TURN108
    checkEntity(2, SHIP, 10, 18, 1, 2, 28, 1);
    checkEntity(1, SHIP, 10, 11, 3, 1, 17, 0);
    checkEntity(5, SHIP, 1, 8, 5, 2, 9, 0);
    resetActions(); // 6542
    // ** nmahoude:
    doAction(STARBOARD);
    // ** Royale:
    doAction(PORT);
    doAction(PORT);
        simulation.playOneTurn(); // ** TURN109
    checkEntity(2, SHIP, 11, 16, 0, 2, 27, 1);
    checkEntity(1, SHIP, 9, 11, 4, 1, 16, 0);
    checkEntity(5, SHIP, 2, 10, 0, 2, 8, 0);
    resetActions(); // 6473
    // ** nmahoude:
    doAction(WAIT);
    // ** Royale:
    doAction(FIRE, 13, 12);
    doAction(FASTER);
        simulation.playOneTurn(); // ** TURN110
    checkEntity(2, SHIP, 13, 16, 0, 2, 26, 1);
    checkEntity(1, SHIP, 9, 12, 4, 1, 15, 0);
    checkEntity(5, SHIP, 4, 10, 0, 2, 7, 0);
    checkEntity(74, CANNONBALL, 13, 12, 1, 2, 0, 0);
    resetActions(); // 6275
    // ** nmahoude:
    doAction(PORT);
    // ** Royale:
    doAction(PORT);
    doAction(FASTER);
        simulation.playOneTurn(); // ** TURN111
    checkEntity(2, SHIP, 15, 16, 1, 2, 25, 1);
    checkEntity(1, SHIP, 8, 13, 5, 1, 14, 0);
    checkEntity(5, SHIP, 6, 10, 0, 2, 6, 0);
    checkEntity(74, CANNONBALL, 13, 12, 1, 1, 0, 0);
    resetActions(); // 6456
    // ** nmahoude:
    doAction(PORT);
    // ** Royale:
    doAction(PORT);
    doAction(FIRE, 11, 17);
        simulation.playOneTurn(); // ** TURN112
    checkEntity(2, SHIP, 16, 14, 2, 2, 24, 1);
    checkEntity(1, SHIP, 9, 14, 0, 1, 13, 0);
    checkEntity(5, SHIP, 8, 10, 0, 2, 5, 0);
    checkEntity(10, MINE, 18, 9, 0, 0, 0, 0);
    checkEntity(74, CANNONBALL, 13, 12, 1, 0, 0, 0);
    checkEntity(75, CANNONBALL, 11, 17, 5, 4, 0, 0);
    resetActions(); // 6317
    // ** nmahoude:
    doAction(SLOWER);
    // ** Royale:
    doAction(PORT);
    doAction(PORT);
        simulation.playOneTurn(); // ** TURN113
    checkEntity(2, SHIP, 15, 13, 2, 1, 23, 1);
    checkEntity(1, SHIP, 10, 14, 1, 1, 12, 0);
    checkEntity(5, SHIP, 10, 10, 1, 2, 4, 0);
    checkEntity(10, MINE, 18, 9, 0, 0, 0, 0);
    checkEntity(75, CANNONBALL, 11, 17, 5, 3, 0, 0);
    resetActions(); // 6732
    // ** nmahoude:
    doAction(WAIT);
    // ** Royale:
    doAction(FASTER);
    doAction(FIRE, 13, 11);
        simulation.playOneTurn(); // ** TURN114
    checkEntity(2, SHIP, 15, 12, 2, 1, 22, 1);
    checkEntity(1, SHIP, 11, 12, 1, 2, 11, 0);
    checkEntity(5, SHIP, 11, 8, 1, 2, 3, 0);
    checkEntity(10, MINE, 18, 9, 0, 0, 0, 0);
    checkEntity(37, MINE, 16, 7, 0, 0, 0, 0);
    checkEntity(44, MINE, 12, 8, 0, 0, 0, 0);
    checkEntity(75, CANNONBALL, 11, 17, 5, 2, 0, 0);
    checkEntity(76, CANNONBALL, 13, 11, 5, 2, 0, 0);
    resetActions(); // 7385
    // ** nmahoude:
    doAction(STARBOARD);
    // ** Royale:
    doAction(FASTER);
    doAction(FIRE, 10, 12);
        simulation.playOneTurn(); // ** TURN115
    checkEntity(2, SHIP, 14, 11, 1, 1, 21, 1);
    checkEntity(1, SHIP, 12, 10, 1, 2, 10, 0);
    checkEntity(5, SHIP, 12, 6, 1, 2, 2, 0);
    checkEntity(10, MINE, 18, 9, 0, 0, 0, 0);
    checkEntity(37, MINE, 16, 7, 0, 0, 0, 0);
    checkEntity(44, MINE, 12, 8, 0, 0, 0, 0);
    checkEntity(75, CANNONBALL, 11, 17, 5, 1, 0, 0);
    checkEntity(76, CANNONBALL, 13, 11, 5, 1, 0, 0);
    resetActions(); // 7560
    // ** nmahoude:
    doAction(PORT);
    // ** Royale:
    doAction(SLOWER);
    doAction(PORT);
        simulation.playOneTurn(); // ** TURN116
    checkEntity(2, SHIP, 15, 10, 2, 1, 20, 1);
    checkEntity(1, SHIP, 12, 9, 1, 1, 9, 0);
    checkEntity(5, SHIP, 13, 4, 2, 2, 1, 0);
    checkEntity(10, MINE, 18, 9, 0, 0, 0, 0);
    checkEntity(37, MINE, 16, 7, 0, 0, 0, 0);
    checkEntity(44, MINE, 12, 8, 0, 0, 0, 0);
    checkEntity(66, MINE, 14, 5, 0, 0, 0, 0);
    checkEntity(75, CANNONBALL, 11, 17, 5, 0, 0, 0);
    checkEntity(76, CANNONBALL, 13, 11, 5, 0, 0, 0);
    resetActions(); // 8777
    // ** nmahoude:
    doAction(STARBOARD);
    // ** Royale:
    doAction(PORT);
    doAction(cotc.entities.Action.MINE);
        simulation.playOneTurn(); // ** TURN117
    checkEntity(2, SHIP, 14, 9, 1, 1, 19, 1);
    checkEntity(1, SHIP, 13, 8, 2, 1, 8, 0);
    checkEntity(10, MINE, 18, 9, 0, 0, 0, 0);
    checkEntity(12, MINE, 9, 9, 0, 0, 0, 0);
    checkEntity(37, MINE, 16, 7, 0, 0, 0, 0);
    checkEntity(44, MINE, 12, 8, 0, 0, 0, 0);
    checkEntity(66, MINE, 14, 5, 0, 0, 0, 0);
    checkEntity(77, MINE, 14, 6, 0, 0, 0, 0);
    resetActions(); // 8732
    // ** nmahoude:
    doAction(WAIT);
    // ** Royale:
    doAction(FIRE, 14, 3);
        simulation.playOneTurn(); // ** TURN118
    checkEntity(2, SHIP, 15, 8, 1, 1, 18, 1);
    checkEntity(1, SHIP, 12, 7, 2, 1, 7, 0);
    checkEntity(10, MINE, 18, 9, 0, 0, 0, 0);
    checkEntity(37, MINE, 16, 7, 0, 0, 0, 0);
    checkEntity(44, MINE, 12, 8, 0, 0, 0, 0);
    checkEntity(66, MINE, 14, 5, 0, 0, 0, 0);
    checkEntity(77, MINE, 14, 6, 0, 0, 0, 0);
    checkEntity(78, CANNONBALL, 14, 3, 1, 2, 0, 0);
    resetActions(); // 9337
    // ** nmahoude:
    doAction(FASTER);
    // ** Royale:
    doAction(PORT);
        simulation.playOneTurn(); // ** TURN119
    checkEntity(2, SHIP, 16, 6, 1, 2, 17, 1);
    checkEntity(1, SHIP, 12, 6, 3, 1, 6, 0);
    checkEntity(10, MINE, 18, 9, 0, 0, 0, 0);
    checkEntity(37, MINE, 16, 7, 0, 0, 0, 0);
    checkEntity(44, MINE, 12, 8, 0, 0, 0, 0);
    checkEntity(47, MINE, 17, 2, 0, 0, 0, 0);
    checkEntity(66, MINE, 14, 5, 0, 0, 0, 0);
    checkEntity(77, MINE, 14, 6, 0, 0, 0, 0);
    checkEntity(78, CANNONBALL, 14, 3, 1, 1, 0, 0);
    resetActions(); // 8436
    // ** nmahoude:
    doAction(STARBOARD);
    // ** Royale:
    doAction(SLOWER);
        simulation.playOneTurn(); // ** TURN120
    checkEntity(2, SHIP, 17, 4, 0, 2, 16, 1);
    checkEntity(1, SHIP, 12, 6, 3, 0, 5, 0);
    checkEntity(10, MINE, 18, 9, 0, 0, 0, 0);
    checkEntity(37, MINE, 16, 7, 0, 0, 0, 0);
    checkEntity(47, MINE, 17, 2, 0, 0, 0, 0);
    checkEntity(66, MINE, 14, 5, 0, 0, 0, 0);
    checkEntity(77, MINE, 14, 6, 0, 0, 0, 0);
    checkEntity(78, CANNONBALL, 14, 3, 1, 0, 0, 0);
    resetActions(); // 8216
    // ** nmahoude:
    doAction(PORT);
    // ** Royale:
    doAction(FIRE, 17, 10);
        simulation.playOneTurn(); // ** TURN121
    checkEntity(2, SHIP, 19, 4, 1, 2, 15, 1);
    checkEntity(1, SHIP, 12, 6, 3, 0, 4, 0);
    checkEntity(10, MINE, 18, 9, 0, 0, 0, 0);
    checkEntity(37, MINE, 16, 7, 0, 0, 0, 0);
    checkEntity(47, MINE, 17, 2, 0, 0, 0, 0);
    checkEntity(66, MINE, 14, 5, 0, 0, 0, 0);
    checkEntity(79, CANNONBALL, 17, 10, 1, 4, 0, 0);
    resetActions(); // 9238
    // ** nmahoude:
    doAction(PORT);
    // ** Royale:
    doAction(FASTER);
        simulation.playOneTurn(); // ** TURN122
    checkEntity(2, SHIP, 20, 2, 2, 2, 14, 1);
    checkEntity(1, SHIP, 11, 6, 3, 1, 3, 0);
    checkEntity(47, MINE, 17, 2, 0, 0, 0, 0);
    checkEntity(79, CANNONBALL, 17, 10, 1, 3, 0, 0);
    resetActions(); // 10637
    // ** nmahoude:
    doAction(PORT);
    // ** Royale:
    doAction(FIRE, 14, 2);
        simulation.playOneTurn(); // ** TURN123
    checkEntity(2, SHIP, 19, 0, 3, 2, 13, 1);
    checkEntity(1, SHIP, 10, 6, 3, 1, 2, 0);
    checkEntity(47, MINE, 17, 2, 0, 0, 0, 0);
    checkEntity(79, CANNONBALL, 17, 10, 1, 2, 0, 0);
    checkEntity(80, CANNONBALL, 14, 2, 1, 3, 0, 0);
    resetActions(); // 12185
    // ** nmahoude:
    doAction(PORT);
    // ** Royale:
    doAction(FASTER);
        simulation.playOneTurn(); // ** TURN124
    checkEntity(2, SHIP, 17, 0, 4, 2, 12, 1);
    checkEntity(1, SHIP, 8, 6, 3, 2, 1, 0);
    checkEntity(47, MINE, 17, 2, 0, 0, 0, 0);
    checkEntity(66, MINE, 14, 5, 0, 0, 0, 0);
    checkEntity(79, CANNONBALL, 17, 10, 1, 1, 0, 0);
    checkEntity(80, CANNONBALL, 14, 2, 1, 2, 0, 0);
    resetActions(); // 14992
    // ** nmahoude:
    doAction(PORT);
    // ** Royale:
    doAction(FIRE, 12, 8);
  }

  private void resetActions() {
    this.actionIndex = 0;
  }

  private void checkEntity(int id, EntityType type, int x, int y, int arg1, int arg2, int arg3, int arg4) {
    switch(type) {
      case SHIP:
        Ship ship = state.getShip(state.ships, id);
        assertThat(ship.position.x, is (x));
        assertThat(ship.position.y, is (y));
        assertThat(ship.orientation, is(arg1));
        assertThat(ship.speed, is(arg2));
        assertThat(ship.health, is(arg3));
        break;
      case CANNONBALL:
        CannonBall cb = null;
        for (int i=0;i<state.cannonballs.size();i++) {
          CannonBall c = state.cannonballs.get(i);
          if (c.position.x == x && c.position.y == y && c.ownerEntityId == arg1) {
            cb = c;
            break;
          }
        }
        assertThat(cb.position.x, is (x)); 
        assertThat(cb.position.y, is (y));
        assertThat(cb.ownerEntityId, is (arg1));
        assertThat(cb.remainingTurns, is (arg2));
    }
  }

  private void doAction(Action action) {
    Ship ship = getShipForAction();
    ship.action = action;
    actionIndex++;
  }
  private void doAction(Action fire, int x, int y) {
    Ship ship = getShipForAction();
    ship.action = fire;
    ship.target = Coord.get(x, y);
    actionIndex++;
  }

  private Ship getShipForAction() {
    Ship ship=null; 
    int alive1 = state.teams[0].shipsAlive.size();
    if (actionIndex<alive1) {
      ship = state.teams[0].shipsAlive.get(actionIndex);
    } else {
      ship = state.teams[1].shipsAlive.get(actionIndex-alive1);
    }
    return ship;
  }

  EntityType SHIP = EntityType.SHIP;
  EntityType MINE = EntityType.MINE;
  EntityType BARREL = EntityType.BARREL;
  EntityType CANNONBALL = EntityType.CANNONBALL;

  private void readEntity(int entityId, EntityType entityType, int x, int y, int arg1, int arg2, int arg3, int arg4) {
    GameSituationTest.readEntity(state, entityId, entityType, x, y, arg1, arg2, arg3, arg4);
  }
}
