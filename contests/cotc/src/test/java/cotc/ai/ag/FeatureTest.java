package cotc.ai.ag;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Before;
import org.junit.Test;

import cotc.GameSituationTest;
import cotc.GameState;
import cotc.Team;
import cotc.entities.EntityType;

public class FeatureTest {
GameState state;
  
  @Before
  public void setup() {
    state = new GameState();
    state.teams[0] = new Team(0);
    state.teams[1] = new Team(1);
  }
  
 
  EntityType SHIP = EntityType.SHIP;
  EntityType MINE = EntityType.MINE;
  EntityType BARREL = EntityType.BARREL;
  EntityType CANNONBALL= EntityType.CANNONBALL;
  private void readEntity(int entityId, EntityType entityType, int x, int y, int arg1, int arg2, int arg3, int arg4) {
    GameSituationTest.readEntity(state, entityId, entityType, x, y, arg1, arg2, arg3, arg4);
  }

}
