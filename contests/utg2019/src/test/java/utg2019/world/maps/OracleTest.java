package utg2019.world.maps;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import trigonometryInt.Point;
import utg2019.sim.Action;
import utg2019.world.World;

public class OracleTest {

  private World current;
  private World old;
  private Oracle oracle;
  
  private static Action emptyActions[] = new Action[5];
  @BeforeClass
  public static void setupClass() {
    Point.init(30, 15);
    for (int i=0;i<5;i++) {
      emptyActions[i] = Action.doWait();
    }
  }

  @Before
  public void setup() {
    oracle = new Oracle();
    current = new World();
    old = new World();
  }

  @Test
  public void preturn() throws Exception {
    oracle.preTurn(current, null);
  }

  @Test
  public void potentialOre_NegOneAtFirst() {
    assertThat(oracle.potentialOre[Point.get(10, 10).offset]).isEqualTo(Oracle.UNKNWON_ORE);
  }
  
  @Test
  public void potentialOre_simpleDigWithOreResult_shouldSetOrePotentialTo2AndDecrementItOnEachDig() throws Exception {
    old.teams[0].robots[0].pos = Point.get(10, 10);
    current.teams[0].robots[0].pos = Point.get(10, 10);
    
    Action[] lastActions = new Action[]{ Action.dig(Point.get(10,10)), Action.doWait(), Action.doWait(), Action.doWait(), Action.doWait()};
    oracle.prepareForNextTurn(old, lastActions);  

    current.teams[0].robots[0].t_ore = 100;
    oracle.preTurn(current, lastActions);
    
    assertThat(oracle.potentialOre[Point.get(10, 10).offset]).isEqualTo(2);
    
    oracle.preTurn(current, lastActions);
    assertThat(oracle.potentialOre[Point.get(10, 10).offset]).isEqualTo(1);

    oracle.preTurn(current, lastActions);
    assertThat(oracle.potentialOre[Point.get(10, 10).offset]).isEqualTo(0);

    oracle.preTurn(current, lastActions);
    assertThat(oracle.potentialOre[Point.get(10, 10).offset]).isEqualTo(0);
  }

  @Test
  public void potentialOre_potentialAt0IfDigDidntResultInOre() throws Exception {
    old.teams[0].robots[0].pos = Point.get(10, 10);
    current.teams[0].robots[0].pos = Point.get(10, 10);
    
    Action[] lastActions = new Action[]{ Action.dig(Point.get(10,10)), Action.doWait(), Action.doWait(), Action.doWait(), Action.doWait()};
    oracle.prepareForNextTurn(old, lastActions);  

    current.teams[0].robots[0].t_ore = 0;
    oracle.preTurn(current, lastActions);
    assertThat(oracle.potentialOre[Point.get(10, 10).offset]).isEqualTo(0);
  }
  
  @Test
  public void potentialOre_potentialAt0IfDigDidntResultInOre_afterSuccessfulDig() throws Exception {
    old.teams[0].robots[0].pos = Point.get(10, 10);
    current.teams[0].robots[0].pos = Point.get(10, 10);
    
    Action[] lastActions = new Action[]{ Action.dig(Point.get(10,10)), Action.doWait(), Action.doWait(), Action.doWait(), Action.doWait()};
    oracle.prepareForNextTurn(old, lastActions);  

    current.teams[0].robots[0].t_ore = 100;
    oracle.preTurn(current, lastActions);
    assertThat(oracle.potentialOre[Point.get(10, 10).offset]).isEqualTo(2);

    current.teams[0].robots[0].t_ore = 0;
    oracle.preTurn(current, lastActions);
    assertThat(oracle.potentialOre[Point.get(10, 10).offset]).isEqualTo(0);
  }
}
