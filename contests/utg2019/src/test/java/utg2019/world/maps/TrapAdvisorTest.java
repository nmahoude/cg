package utg2019.world.maps;

import static org.assertj.core.api.Assertions.assertThat;
import static utg2019.RobotBuilder.robot;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import trigonometryInt.Point;
import utg2019.RobotBuilder;
import utg2019.sim.Action;
import utg2019.world.MapCell;
import utg2019.world.World;

public class TrapAdvisorTest {

  
  private World current;
  private World old;
  private TrapAdvisor trapAdvisor;
  
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
    trapAdvisor = new TrapAdvisor();
    current = new World();
    old = new World();
    
    RobotBuilder.old = old;
    RobotBuilder.current = current;
    RobotBuilder.trapAdvisor = trapAdvisor;
  }

  @Test
  public void moveAnywhereSinceLastFrame_SoNoThreat() throws Exception {
    setOldPos(11, 10);
    setNewPos(10,10);
    
    trapAdvisor.update(old, current, emptyActions);
    
    assertThat(trapAdvisor.canTransportMine[0]).isFalse();
  }
  
  @Test
  public void moveOnCol0ButWasNoThreat_SoNoThreat() throws Exception {
    setOldPos(0, 10);
    setNewPos(0, 13);
    
    trapAdvisor.update(old, current, emptyActions);
    
    assertThat(trapAdvisor.canTransportMine[0]).isFalse();
  }
  
  @Test
  public void dontMoveOnCol0_becomeAThreat() throws Exception {
    setOldPos(0,0);
    setNewPos(0,0);

    trapAdvisor.update(old, current, emptyActions);
    
    assertThat(trapAdvisor.canTransportMine[0]).isTrue();
  }
  
  @Test
  public void dontMoveOnCol0HoleInFrontButNotNew_becomeAThreat() throws Exception {
    robot(1, 0).stayedAt(Point.get(0, 9));
    robot(0, 0).stayedAt(Point.get(0, 9)).digAt(Point.get(1,9));
    setOldHole(1, 9);
    
    Action actions[] = createActions();
    actions[0] = Action.dig(1,9);
    trapAdvisor.update(old, current, actions);
    
    assertThat(trapAdvisor.canTransportMine[0]).isTrue();
  }
  
  @Test
  public void threatAndMove_StayAThreat() throws Exception {
    whenHeIsDangerous();
    setOldPos(10,4);
    setNewPos(12,6);

    trapAdvisor.update(old, current, emptyActions);
    
    assertThat(trapAdvisor.canTransportMine[0]).isTrue();
  }
  
  @Test
  public void threatAndMoveBackTo0_and_TeamScore_NoMoreThreat() throws Exception {
    robot(1 ,0).isDangerous()
               .move().from(Point.get(4, 0)).to(Point.get(0, 0));

    old.teams[1].score = 9;
    current.teams[1].score = 10;
    
    trapAdvisor.update(old, current, emptyActions);
    
    assertThat(trapAdvisor.canTransportMine[0]).isFalse();
  }

  @Test
  public void dangerBottomButTwoSimultaneousDigsOnAbove_trapIsSetOnBottom() throws Exception {
    robot(1 ,0).isDangerous()
                .stayedAt(Point.get(15, 10)).and().digAt(Point.get(15, 11));
    
    Point falseTrapDig = Point.get(15, 9);
    
    robot(0,0).stayedAt(falseTrapDig).and().dig();
    robot(0,1).stayedAt(falseTrapDig).and().dig();
    
    
    old.setOre(falseTrapDig, 2);
    old.setCurrentlyKnown(falseTrapDig);
    old.setHole(falseTrapDig);
    current.setOre(falseTrapDig, 0);
    current.setCurrentlyKnown(falseTrapDig);
    current.setHole(falseTrapDig);
    
    
    Action[] actions = createActions();
    actions[0] = Action.dig(Point.get(15, 9));
    actions[1] = Action.dig(Point.get(15, 9));
    
    trapAdvisor.update(old, current, actions);
    
    assertThat(trapAdvisor.canTransportMine[0]).isFalse();
    assertThat(trapAdvisor.potentialTraps[falseTrapDig.offset]).isEqualTo(0);
    assertThat(trapAdvisor.potentialTraps[Point.get(15, 11).offset]).isEqualTo(1);
  }
  
  @Test
  public void dangerousCellWithIncreasingOre_shouldNotBeDangerousAnymore() throws Exception {
    Point unDiggingPoint = Point.get(10, 10);

    trapAdvisor.potentialTraps[unDiggingPoint.offset] = 1;
    old.setCurrentlyKnown(unDiggingPoint);
    old.setOre(unDiggingPoint, 1);

    current.setCurrentlyKnown(unDiggingPoint);
    current.setOre(unDiggingPoint, 2);
    
    trapAdvisor.update(old, current, emptyActions);
    
    assertThat(trapAdvisor.potentialTraps[Point.get(10, 10).offset]).isEqualTo(0);
  }

  @Test
  public void digOnFirstTuurn_shouldNotBeDangerousAndHoleIsEmpty() throws Exception {
    robot(1 ,0).stayedAt(Point.get(0, 5)).and().digAt(Point.get(1, 5));

    trapAdvisor.update(old, current, emptyActions);
    
    assertThat(trapAdvisor.potentialTraps[Point.get(1,5).offset]).isEqualTo(0);
    assertThat(trapAdvisor.canTransportMine[0]).isEqualTo(false);
  }

  
  @Test
  public void threatAndMoveBackTo0_but_TeamNoScore_NoMoreThreat() throws Exception {
    robot(1 ,0).isDangerous()
               .move().from(Point.get(4, 0)).to(Point.get(0, 0));

    old.teams[1].score = 9;
    current.teams[1].score = 9;
    
    trapAdvisor.update(old, current, emptyActions);
    
    assertThat(trapAdvisor.canTransportMine[0]).isTrue();
  }

  @Test
  public void threatAndDigAlone_noMoreThreat() throws Exception {
    robot(1 ,0).isDangerous()
               .stayedAt(Point.get(10, 4)) ;
    
    setNewHole(11, 4);
    
    trapAdvisor.update(old, current, emptyActions);
    
    assertThat(trapAdvisor.canTransportMine[0]).isFalse();
  }
  
  @Test
  public void threatAndDigUnderButAnotherRedNear_StillThreat() throws Exception {
    robot(1 ,0).isDangerous()
                .stayedAt(Point.get(10, 4)) ;

    robot(1 ,1).stayedAt(Point.get(10, 4)) ;

    setNewHole(10,4);
    
    trapAdvisor.update(old, current, emptyActions);
    
    assertThat(trapAdvisor.canTransportMine[0]).isTrue();
  }

  @Test
  public void threatAndDigUnderButAnotherRedNearButDidMove_NoThreat() throws Exception {
    robot(1 ,0).isDangerous()
               .stayedAt(Point.get(10, 4))
               .digAt(Point.get(10, 4));

    robot(1 ,1)
              .move()
              .from(Point.get(7,4))
              .to(Point.get(10,4));
    
    setNewHole(10,4);
    
    trapAdvisor.update(old, current, emptyActions);
    
    assertThat(trapAdvisor.canTransportMine[0]).isFalse();
  }

  @Test
  public void threatAndDigNeighborButSomeoneNearEnoughButCouldntDoIt_NoMoreThreat() throws Exception {
    robot(1 ,0).isDangerous()
               .digAt(Point.get(10, 4));
    
    robot(1, 1).isDangerous()
               .move()
               .from(Point.get(10,4))
               .to(Point.get(10,6));
    
    trapAdvisor.update(old, current, emptyActions);
    
    assertThat(trapAdvisor.canTransportMine[0]).isFalse();
    assertThat(holesAroundAreThreat(current, 10,4)).isTrue();
  }



  private boolean holesAroundAreThreat(World current, int x, int y) {
    Point pos = Point.get(x, y);
    for (MapCell neighbor : World.mapCells[pos.offset].neighborsAndSelf) {
      int offset = neighbor.pos.offset;
      if (!current.hasHole(offset) && trapAdvisor.potentialTraps[offset] != 0) return false;
      if (current.hasHole(offset) && trapAdvisor.potentialTraps[offset] == 0) return false;
    }
    return true;
  }

  @Test
  public void robotOnCol0_And_new_hole_on_col_1_isDangerous() throws Exception {
    robot(1,0).isDangerous().stayedAt(Point.get(0, 4)).and().digAt(Point.get(1, 4));
    
    trapAdvisor.update(old, current, emptyActions);
    
    assertThat(trapAdvisor.potentialTraps[Point.get(1, 4).offset]).isEqualTo(1);
    assertThat(trapAdvisor.canTransportMine[0]).isFalse();
  }
  
  @Test
  public void threatAndDigNeighborButSomeoneNearEnough_StillThreat() throws Exception {
    robot(1,0).isDangerous().stayedAt(Point.get(10, 4));
    robot(1,1).stayedAt(Point.get(10, 6));
    
    setNewHole(10,5);
    
    trapAdvisor.update(old, current, emptyActions);
    
    assertThat(trapAdvisor.canTransportMine[0]).isTrue();
  }

  @Test
  public void threatAndStayButNoHolesAround_StillThreat() throws Exception {
    robot(1,0).isDangerous().stayedAt(Point.get(10, 4));
    
    trapAdvisor.update(old, current, emptyActions);
    
    assertThat(trapAdvisor.canTransportMine[0]).isTrue();
  }

  @Test
  @Ignore
  public void threatAndStay_myRadarDisappearAndNobodyElseCouldDestroyIt_NoThreat() throws Exception {
    robot(1,0).isDangerous().stayedAt(Point.get(10, 4));
    
    setMyRadar(10,5); // old radar & hole
    current.removeRadar(Point.get(10, 5));

    trapAdvisor.update(old, current, emptyActions);
    
    assertThat(trapAdvisor.canTransportMine[0]).isFalse();
    assertThat(trapAdvisor.potentialTraps[Point.get(10, 5).offset]).isEqualTo(1); // TODO should be 100, the robots destroy my radar 
  }

  @Test
  public void threatAndStay_myRadarDisappearButSomeRobotCouldHaveTearIt_StillThreat() throws Exception {
    robot(1,0).isDangerous().stayedAt(Point.get(10, 4));
    robot(1,1).stayedAt(Point.get(10, 6));
    
    trapAdvisor.canTransportMine[1] = true; // other is dangerous too
    
    setMyRadar(10,5); // old radar & hole
    current.removeRadar(Point.get(10, 5));

    trapAdvisor.update(old, current, emptyActions);
    
    assertThat(trapAdvisor.canTransportMine[0]).isTrue();
  }
  
  @Test
  public void threatAndStayButHolesAroundHaveMyMines_StillThreat() throws Exception {
    robot(1,0).isDangerous().stayedAt(Point.get(10, 4));
    
    setOldHole(11, 4);
    setMyMine(11,4);
    
    trapAdvisor.update(old, current, emptyActions);
    
    assertThat(trapAdvisor.canTransportMine[0]).isTrue();
  }

  @Test
  public void threatAndStayButHolesAroundHaveMyRadar_StillThreat() throws Exception {
    robot(1,0).isDangerous().stayedAt(Point.get(10, 4));
    
    setMyMine(11,4);
    setMyRadar(10,5);
    
    trapAdvisor.update(old, current, emptyActions);
    
    assertThat(trapAdvisor.canTransportMine[0]).isTrue();
    
    assertThat(trapAdvisor.potentialTraps[Point.get(11, 4).offset]).isEqualTo(0);
    assertThat(trapAdvisor.potentialTraps[Point.get(10, 5).offset]).isEqualTo(0);
  }
  
  @Test
  public void insecure_whenStayOn_1_0_andHoleOn_2_0_then2_0_insecureButNotOthers() throws Exception {
    robot(1,0).isDangerous().stayedAt(Point.get(1, 0)).and().digAt(Point.get(2, 0));
    
    trapAdvisor.update(old, current, emptyActions);
    
    assertThat(trapAdvisor.potentialTraps[Point.get(1, 0).offset]).isEqualTo(0);// stay secure because no hole
    assertThat(trapAdvisor.potentialTraps[Point.get(2, 0).offset]).isEqualTo(InsecureMap.INSECURE_OFFSET);
  }
  
  @Test
  public void stayWithOneOldHolesAndANewHole_onlyNewHoleIsInsecure() throws Exception {
    robot(1,0).isDangerous().stayedAt(Point.get(5,5));
    
    setOldHole(5,6);
    setNewHole(6,5);
    
    trapAdvisor.update(old, current, emptyActions);
    
    assertThat(trapAdvisor.potentialTraps[Point.get(5,6).offset]).isEqualTo(0);// stay secure because old hole & new hole
    assertThat(trapAdvisor.potentialTraps[Point.get(6,5).offset]).isEqualTo(InsecureMap.INSECURE_OFFSET);
  }
  
  @Test
  public void dig_shouldResetDangerousStateOfCell() throws Exception {
    // cell was empty or it has exploded 
    setDangerous(Point.get(7, 8));
    
    Action actions[] = createActions();
    actions[0] = Action.dig(Point.get(7, 8));

    trapAdvisor.update(old, current, actions);
    
    assertThat(trapAdvisor.potentialTraps[Point.get(7, 8).offset]).isEqualTo(0);// stay secure because old hole & new hole
  }

  private void setDangerous(Point point) {
    trapAdvisor.potentialTraps[point.offset] = 1;
  }
  
  @Test
  public void dangerousRobotsWith2HolesButOneByMe_otherHoleIsTheOnlyOneDangerous() throws Exception {
    robot(1,0).isDangerous().stayedAt(Point.get(5,5));
    
    // 2 new holes
    setNewHole(6,5);
    setNewHole(5,6);
    
    // but I dig one
    Action actions[] = createActions();
    actions[0] = Action.dig(Point.get(5, 6));
    
    trapAdvisor.update(old, current, emptyActions);
    
    assertThat(trapAdvisor.potentialTraps[Point.get(5, 6).offset]).isEqualTo(0);
    assertThat(trapAdvisor.potentialTraps[Point.get(6, 5).offset]).isEqualTo(1);
  }
  

  @Test
  public void twoRobotsOneDangerous_OneHole_shouldBeDangerous() throws Exception {
    robot(1,0).isDangerous().stayedAt(Point.get(5,5));
    robot(1,1).stayedAt(Point.get(7,5));

    setNewHole(6, 5); // hole in between
    
    trapAdvisor.update(old, current, emptyActions);
    
    assertThat(trapAdvisor.potentialTraps[Point.get(6, 5).offset]).isEqualTo(1);
  }
  
  @Test
  public void twoRobotsOneDangerous_twoHoles_OneShouldBeDangerous() throws Exception {
    robot(1,0).isDangerous().stayedAt(Point.get(5,5));
    robot(1,1).stayedAt(Point.get(7,5));
    
    setNewHole(6, 5); // hole in between
    setNewHole(7, 4); // another new hole that only the not dangerous could have done
    
    trapAdvisor.update(old, current, emptyActions);
    
    assertThat(trapAdvisor.potentialTraps[Point.get(6, 5).offset]).isEqualTo(1);
    assertThat(trapAdvisor.potentialTraps[Point.get(7, 4).offset]).isEqualTo(0);
  }

  @Test
  public void holeIsDangerousButSomeNonDangerousDigged_holeIsNotDangerousAnymore() throws Exception {
    robot(1,0).stayedAt(Point.get(10, 10));
    
    setOldHole(11, 10);
    trapAdvisor.potentialTraps[Point.get(11, 10).offset] = 1;
    
    // some ore has been digged
    old.setOre(Point.get(11, 10), 3);
    old.setCurrentlyKnown(Point.get(11, 10));
    current.setOre(Point.get(11, 10), 2);
    current.setCurrentlyKnown(Point.get(11, 10));
    
    trapAdvisor.update(old, current, emptyActions);
    
    assertThat(trapAdvisor.potentialTraps[Point.get(11, 10).offset]).isEqualTo(0);
  }

  
  @Test
  public void oneDangerousRobotAndAnOreDiggedThatWeKnowOf_otherHolesCantBeDangerous() throws Exception {
    robot(1,0).isDangerous().stayedAt(Point.get(10, 10));
    
    setOldHole(11, 10);
    setOldHole(10, 11);

    // some ore has been digged
    old.setOre(Point.get(11, 10), 3);
    old.setCurrentlyKnown(Point.get(11, 10));
    current.setOre(Point.get(11, 10), 2);
    current.setCurrentlyKnown(Point.get(11, 10));
    
    trapAdvisor.update(old, current, emptyActions);
    
    assertThat(trapAdvisor.potentialTraps[Point.get(11, 10).offset]).isEqualTo(1);
    assertThat(trapAdvisor.potentialTraps[Point.get(10, 11).offset]).isEqualTo(0);
  }

  @Test
  public void oneDangerousRobotAndAnOreDiggedThatWeDidntKnowOfOld_otherHolesCantBeDangerous() throws Exception {
    robot(1,0).isDangerous().stayedAt(Point.get(10, 10));
    
    setOldHole(11, 10);
    setOldHole(10, 11);

    // some ore has been digged
    old.setOre(Point.get(11, 10), 3);
    old.setCurrentlyKnown(Point.get(11, 10));
    current.setOre(Point.get(11, 10), 2);
    current.setCurrentlyKnown(Point.get(11, 10));
    
    trapAdvisor.update(old, current, emptyActions);
    
    assertThat(trapAdvisor.potentialTraps[Point.get(11, 10).offset]).isEqualTo(1);
    assertThat(trapAdvisor.potentialTraps[Point.get(10, 11).offset]).isEqualTo(0);
  }
  
  @Test
  public void oneDangerousRobotAndAnOreDigged_TrapIsInTheHole() throws Exception {
    robot(1,0).isDangerous().stayedAt(Point.get(10, 10));
    
    setOldHole(11, 10);
    setOldHole(10, 11);

    // some ore has been digged (and we know it)
    old.setOre(Point.get(11, 10), 3);
    current.setOre(Point.get(11, 10), 2);
    old.setCurrentlyKnown(Point.get(11, 10));
    current.setCurrentlyKnown(Point.get(11, 10));
    
    trapAdvisor.update(old, current, emptyActions);
    
    assertThat(trapAdvisor.potentialTraps[Point.get(11, 10).offset]).isEqualTo(1);
    assertThat(trapAdvisor.potentialTraps[Point.get(10, 11).offset]).isEqualTo(0);
    assertThat(trapAdvisor.canTransportMine[0]).isFalse();
  }
  
  // helper methods
  private void setOldHole(int x, int y) {
    old.setHole(Point.get(x, y));
    current.setHole(Point.get(x, y));
  }

  private void setNewHole(int x, int y) {
    current.setHole(Point.get(x, y));
  }

  private Point setNewPos(int x, int y) {
    return current.teams[1].robots[0].pos = Point.get(x, y);
  }

  private void setOldPos(int x, int y) {
    old.teams[1].robots[0].pos = Point.get(x, y);
  }
  
  private void setMyRadar(int x, int y) {
    setOldHole(x,y);
    old.putRadar(Point.get(x, y));
    current.putRadar(Point.get(x, y));
  }

  private void setMyMine(int x, int y) {
    setOldHole(x,y);
    old.putTrap(Point.get(x, y));
    current.putTrap(Point.get(x, y));
  }
  private void whenHeIsDangerous() {
    trapAdvisor.canTransportMine[0] = true;
  }
  private void whenHeIsNotDangerous() {
    trapAdvisor.canTransportMine[0] = false;
  }
  private void setSameSpot(int x, int y) {
    setOldPos(x,y);
    setNewPos(x,y);
  }
  private Action[] createActions() {
    Action a[] = new Action[5];
    for (int i=0;i<5;i++) {
      a[i] = Action.doWait();
    }
    return a;
  }

}
