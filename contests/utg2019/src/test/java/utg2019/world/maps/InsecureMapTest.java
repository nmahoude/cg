package utg2019.world.maps;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import trigonometryInt.Point;
import utg2019.world.MapCell;
import utg2019.world.World;

public class InsecureMapTest {

  private InsecureMap map;
  private World current;
  private World old;

  @BeforeClass
  public static void setupClass() {
    Point.init(30, 15);
  }

  @Before
  public void setup() {
    map = new InsecureMap();
    current = new World();
    old = new World();

    whenHeIsNotDangerous();
  }
  
  @Test
  public void moveSinceLastFrame_SoNoThreat() throws Exception {
    setOldPos(11, 10);
    setNewPos(10,10);
    
    map.update(old, current);
    
    assertThat(map.dangerous[0]).isFalse();
  }

  @Test
  public void dontMoveOnCol0_becomeAThreat() throws Exception {
    setOldPos(0,0);
    setNewPos(0,0);

    map.update(old, current);
    
    assertThat(map.dangerous[0]).isTrue();
  }
  
  @Test
  public void moveOnCol0_StillNotAThreat() throws Exception {
    setOldPos(0,0);
    setNewPos(0,4);

    map.update(old, current);
    
    assertThat(map.dangerous[0]).isFalse();
  }
  
  @Test
  public void threatAndMove_StayAThreat() throws Exception {
    whenHeIsDangerous();
    setOldPos(10,4);
    setNewPos(12,6);

    map.update(old, current);
    
    assertThat(map.dangerous[0]).isTrue();
  }

  @Test
  public void threatAndMoveBackTo0_NoMoreThreat() throws Exception {
    whenHeIsDangerous();
    setOldPos(10,4);
    setNewPos(0,0);

    map.update(old, current);
    
    assertThat(map.dangerous[0]).isFalse();
  }
  
  @Test
  public void threatAndDigAlone_noMoreThreat() throws Exception {
    whenHeIsDangerous();
    setOldPos(10,4);
    setNewPos(10,4);
    
    setNewHole(11, 4);
    
    map.update(old, current);
    
    assertThat(map.dangerous[0]).isFalse();
  }

  @Test
  public void threatAndDigUnderButAnotherRedNear_StillThreat() throws Exception {
    whenHeIsDangerous();
    setOldPos(10,4);
    setNewPos(10,4);
    old.teams[1].robots[1].pos = Point.get(10, 4);
    current.teams[1].robots[1].pos = Point.get(10, 4);
    
    setNewHole(10,4);
    
    map.update(old, current);
    
    assertThat(map.dangerous[0]).isTrue();
  }

  @Test
  public void threatAndDigUnderButAnotherRedNearButDidMove_NoThreat() throws Exception {
    whenHeIsDangerous();
    setOldPos(10,4);
    setNewPos(10,4);
    old.teams[1].robots[1].pos = Point.get(7, 4);
    current.teams[1].robots[1].pos = Point.get(10, 4);
    
    setNewHole(10,4);
    
    map.update(old, current);
    
    assertThat(map.dangerous[0]).isFalse();
  }

  @Test
  public void threatAndDigNeighborButSomeoneNearEnoughButCouldntDoIt_NoMoreThreat() throws Exception {
    whenHeIsDangerous();
    setOldPos(10,4);
    setNewPos(10,4);
    
    old.teams[1].robots[1].pos = Point.get(10,4);
    current.teams[1].robots[1].pos = Point.get(10,6); // move so could not have dig
    
    setNewHole(10,5);
    
    map.update(old, current);
    
    assertThat(map.dangerous[0]).isFalse();
    assertThat(holesAroundAreThreat(current, 10,4)).isTrue();
  }

  private boolean holesAroundAreThreat(World current, int x, int y) {
    Point pos = Point.get(x, y);
    for (MapCell neighbor : World.mapCells[pos.offset].neighborsAndSelf) {
      int offset = neighbor.pos.offset;
      if (!current.hasHole(offset) && map.insecure[offset] != 0) return false;
      if (current.hasHole(offset) && map.insecure[offset] == 0) return false;
    }
    return true;
  }

  @Test
  public void threatAndDigNeighborButSomeoneNearEnough_StillThreat() throws Exception {
    whenHeIsDangerous();
    setOldPos(10,4);
    setNewPos(10,4);
    
    old.teams[1].robots[1].pos = Point.get(10,6);
    current.teams[1].robots[1].pos = Point.get(10,6); // stay at the same spot, near enough to disturb
    
    // X
    // R1
    // X
    // R0
    // => R1 action is only dig up, so he cannot dig down, so R0 should have done it !
    
    setNewHole(10,5);
    
    map.update(old, current);
    
    assertThat(map.dangerous[0]).isTrue();
  }

  @Test
  public void threatAndStayButNoHolesAround_StillThreat() throws Exception {
    whenHeIsDangerous();
    setOldPos(10,4);
    setNewPos(10,4);
    
    map.update(old, current);
    
    assertThat(map.dangerous[0]).isTrue();
  }

  // TODO NO threat anymore => on sait que la case est piegée !
  
  @Test
  public void threatAndStay_myRadarDisappearAndNobodyElseCouldDestroyIt_NoThreat() throws Exception {
    whenHeIsDangerous();
    setOldPos(10,4);
    setNewPos(10,4);
    
    setMyRadar(10,5); // old radar & hole
    current.removeRadar(Point.get(10, 5)); // destroy radar

    map.update(old, current);
    
    assertThat(map.dangerous[0]).isFalse();
    assertThat(map.insecure[Point.get(10, 5).offset]).isEqualTo(1); // TODO should be 100, the robots destroy my radar 
  }

  @Test
  public void threatAndStay_myRadarDisappearButSomeRobotCouldHaveTearIt_StillThreat() throws Exception {
    whenHeIsDangerous();
    setOldPos(10,4);
    setNewPos(10,4);
    
    current.teams[1].robots[1].pos = Point.get(10,6);
    
    setMyRadar(10,5); // old radar & hole
    current.removeRadar(Point.get(10, 5));

    map.update(old, current);
    
    assertThat(map.dangerous[0]).isTrue();
  }
  
  @Test
  public void threatAndStayButHolesAroundHaveMyMines_StillThreat() throws Exception {
    whenHeIsDangerous();
    setOldPos(10,4);
    setNewPos(10,4);
    
    setMyMine(11,4);
    
    map.update(old, current);
    
    assertThat(map.dangerous[0]).isTrue();
  }

  @Test
  public void threatAndStayButHolesAroundHaveMyRadar_StillThreat() throws Exception {
    whenHeIsDangerous();
    setOldPos(10,4);
    setNewPos(10,4);
    
    setMyMine(11,4);
    setMyRadar(10,5);
    
    map.update(old, current);
    
    assertThat(map.dangerous[0]).isTrue();
    assertThat(map.insecure[Point.get(11, 4).offset]).isEqualTo(0);
    assertThat(map.insecure[Point.get(10, 5).offset]).isEqualTo(0);
  }
  
  @Test
  public void insecure_whenStayOn_1_0_andHoleOn_2_0_then2_0_insecureButNotOthers() throws Exception {
    whenHeIsDangerous();
    setOldPos(1, 0);
    setNewPos(1, 0);
    setNewHole(2,0);
    
    map.update(old, current);
    
    assertThat(map.insecure[Point.get(1, 0).offset]).isEqualTo(0);// stay secure because no hole
    assertThat(map.insecure[Point.get(2, 0).offset]).isEqualTo(InsecureMap.INSECURE_OFFSET);
  }
  
  @Test
  public void stayWithOneOldHolesAndANewHole_onlyNewHoleIsInsecure() throws Exception {
    whenHeIsDangerous();
    setOldPos(5, 5);
    setNewPos(5, 5);
    
    setOldHole(5,6);
    setNewHole(6,5);
    
    map.update(old, current);
    
    assertThat(map.insecure[Point.get(5,6).offset]).isEqualTo(0);// stay secure because old hole & new hole
    assertThat(map.insecure[Point.get(6,5).offset]).isEqualTo(InsecureMap.INSECURE_OFFSET);
  }
  
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
    map.dangerous[0] = true;
  }
  private void whenHeIsNotDangerous() {
    map.dangerous[0] = false;
  }
  

}
