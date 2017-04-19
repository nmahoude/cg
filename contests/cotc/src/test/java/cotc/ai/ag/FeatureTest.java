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
  
  @Test
  /*
  mineCount=8
  seed=823675928
  barrelCount=21
  shipsPerPlayer=3
  */
  public void Mobility() throws Exception {
    readEntity(0,SHIP,5,6,4,0,100,1);
    readEntity(2,SHIP,10,1,1,0,100,1);
    readEntity(4,SHIP,16,3,5,0,100,1);
    readEntity(1,SHIP,5,14,2,0,100,0);
    readEntity(3,SHIP,10,19,5,0,100,0);
    readEntity(5,SHIP,16,17,1,0,100,0);
    readEntity(6,MINE,2,6,0,0,0,0);
    readEntity(9,MINE,7,11,0,0,0,0);
    readEntity(8,MINE,7,9,0,0,0,0);
    readEntity(8,MINE,15,17,0,0,0,0);
    readEntity(10,MINE,7,4,0,0,0,0);
    readEntity(12,MINE,15,3,0,0,0,0);
    readEntity(15,BARREL,7,18,15,0,0,0);
    readEntity(14,BARREL,7,2,15,0,0,0);
    readEntity(17,BARREL,14,19,17,0,0,0);
    readEntity(16,BARREL,14,1,17,0,0,0);
    readEntity(19,BARREL,19,17,20,0,0,0);
    readEntity(18,BARREL,19,3,20,0,0,0);
    readEntity(22,BARREL,11,13,15,0,0,0);
    readEntity(21,BARREL,11,7,15,0,0,0);
    readEntity(24,BARREL,20,15,11,0,0,0);
    readEntity(23,BARREL,20,5,11,0,0,0);
    readEntity(25,BARREL,14,10,12,0,0,0);
    readEntity(27,BARREL,19,15,19,0,0,0);
    readEntity(26,BARREL,19,5,19,0,0,0);
    readEntity(30,BARREL,19,18,18,0,0,0);
    readEntity(29,BARREL,19,2,18,0,0,0);
    readEntity(32,BARREL,7,19,10,0,0,0);
    readEntity(31,BARREL,7,1,10,0,0,0);
    readEntity(35,BARREL,17,11,17,0,0,0);
    readEntity(34,BARREL,17,9,17,0,0,0);
    readEntity(38,BARREL,8,17,12,0,0,0);
    readEntity(37,BARREL,8,3,12,0,0,0);
    
    state.backup();

    Feature feature = new Feature();
    feature.calculateFeatures(state);
    
    assertThat(feature.features[Feature.MY_MOBILITY_FEATURE], is(10.0));
    assertThat(feature.features[Feature.HIS_MOBILITY_FEATURE], is(10.0));
  }
  
  EntityType SHIP = EntityType.SHIP;
  EntityType MINE = EntityType.MINE;
  EntityType BARREL = EntityType.BARREL;
  EntityType CANNONBALL= EntityType.CANNONBALL;
  private void readEntity(int entityId, EntityType entityType, int x, int y, int arg1, int arg2, int arg3, int arg4) {
    GameSituationTest.readEntity(state, entityId, entityType, x, y, arg1, arg2, arg3, arg4);
  }

}
