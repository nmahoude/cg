package utg2019.sim;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import trigonometryInt.Point;
import utg2019.world.World;
import utg2019.world.entity.Robot;

public class SimulationTest {
  private Simulation sim;
  private World world;
  private Robot robot;

  @BeforeClass
  public static void initClassSetup() {
    Point.init(30, 15);
  }

  @Before
  public void setup() {
    sim = new Simulation();
    world = new World();
    robot = world.teams[1].robots[0];
  }
  
  @Test
  public void wait_returnTheSameWorld() throws Exception {
    World result = sim.sim(1, 0, world, Action.doWait());
    
    assertThat(result).isEqualTo(result);
  }
  
  @Test
  public void simpleMove_moveRobot() throws Exception {
    Simulation sim = new Simulation();
    World world = new World();
    
    World result = sim.sim(1, 0, world, Action.move(Point.get(10, 10)));
    
    assertThat(result.teams[1].robots[0].pos).isEqualTo(Point.get(10, 10));
  }

  @Test
  public void move_on_0_0_with_ore__scoreForTeam() throws Exception {
    robot.t_ore = 100;
    
    World result = sim.sim(1, 0, world, Action.move(Point.get(0, 0)));
    Robot resultRobot = result.teams[1].robots[0];
    
    assertThat(resultRobot.pos).isEqualTo(Point.get(0, 0));
    assertThat(resultRobot.t_ore).isEqualTo(0);
    assertThat(result.teams[1].score).isEqualTo(1);
  }
  
  @Test
  public void dig_on_1_0_shouldCreateAHole() throws Exception {
    World result = sim.sim(1, 0, world, Action.dig(Point.get(1, 0)));
    
    assertThat(result.hasHole(Point.get(1, 0))).isTrue();
  }
  
  @Test
  public void dig_on_radar_dont_destroys_it() throws Exception {
    world.teams[0].robots[0].pos = Point.get(0, 0);
    world.putRadar(Point.get(1,0));
    
    World result = sim.sim(0, 0, world, Action.dig(Point.get(1, 0)));
    
    assertThat(result.hasRadar(Point.get(1, 0))).isTrue();
  }
  
  @Test
  public void dig_on_1_0_with_ore_shouldGiveRobotAnOre() throws Exception {
    world.setOre(Point.get(1,0), 3);
    world.setCurrentlyKnown(Point.get(1,0));
    
    World result = sim.sim(1, 0, world, Action.dig(Point.get(1, 0)));
    Robot resultRobot = result.teams[1].robots[0];
    
    assertThat(result.hasHole(Point.get(1, 0))).isTrue();
    assertThat(result.getOre(Point.get(1, 0))).isEqualTo(2);
    assertThat(resultRobot.t_ore).isEqualTo(100);
  }

  @Test
  public void requestMine_shouldLoadMineOnRobot() throws Exception {
    World result = sim.sim(0, 0, world, Action.request(Item.TRAP));
    
    assertThat(result.teams[0].robots[0].t_mine).isEqualTo(100);
    assertThat(result.teams[0].trapCooldown).isEqualTo(5);
  }
  
  @Test
  public void requestMineWhenStillOnCoolDown_doNothing() throws Exception {
    world.teams[0].trapCooldown = 3;
    World result = sim.sim(0, 0, world, Action.request(Item.TRAP));
    
    assertThat(result.teams[0].robots[0].t_mine).isEqualTo(0);
    assertThat(result.teams[0].trapCooldown).isEqualTo(3);
  }
  
  @Test
  public void requestRadar_shouldLoadRadarOnRobot() throws Exception {
    World result = sim.sim(0, 0, world, Action.request(Item.RADAR));
    
    assertThat(result.teams[0].robots[0].t_radar).isEqualTo(100);
    assertThat(result.teams[0].radarCooldown).isEqualTo(5);
  }
  @Test
  public void requestRadarStillOnCoolDown_shoulddoNothing() throws Exception {
    world.teams[0].radarCooldown = 4;
    World result = sim.sim(0, 0, world, Action.request(Item.RADAR));
    
    assertThat(result.teams[0].robots[0].t_radar).isEqualTo(0);
    assertThat(result.teams[0].radarCooldown).isEqualTo(4);
  }
  
  @Test
  public void explosions_digOnTrap_killRobot() throws Exception {
    moveRobot(0,0, 9, 10);
    putTrapOn(10,10);
    
    World result = sim.sim(0, 0, world, Action.dig(10,10));
    
    assertThat(result.teams[0].robots[0].isDead()).isEqualTo(true);
  }

  @Test
  public void explosions_digOnTrap_killNeighborsRobot() throws Exception {
    moveRobot(0,0, 9, 10);
    moveRobot(0,1, 11, 10);
    putTrapOn(10,10);
    
    World result = sim.sim(0, 0, world, Action.dig(10,10));
    
    assertThat(result.teams[0].robots[1].isDead()).isEqualTo(true);
  }

  @Test
  public void explosions_digOnTrap_triggersNearbyTraps() throws Exception {
    moveRobot(0,0, 9, 10);
    putTrapOn(10,10);
    putTrapOn(11,10);
    
    World result = sim.sim(0, 0, world, Action.dig(10,10));
    
    assertThat(result.teams[0].robots[1].isDead()).isEqualTo(true);
    assertThat(result.hasTrap(Point.get(11, 10))).isEqualTo(false);
  }
  
  @Test
  public void explosions_digOnTrap_triggersNearbyTrapsAndKillRobots() throws Exception {
    moveRobot(0,0, 9, 10);
    moveRobot(1,0, 12, 10);
    putTrapOn(10,10);
    putTrapOn(11,10);
    
    World result = sim.sim(0, 0, world, Action.dig(10,10));
    
    assertThat(result.teams[0].robots[1].isDead()).isEqualTo(true);
    assertThat(result.teams[1].robots[0].isDead()).isEqualTo(true);
  }
  
  @Test
  public void explosions_explodingTrap_shouldCreateHolesAround() throws Exception {
    moveRobot(0,0, 9, 10);
    putTrapOn(10,10);
    
    World result = sim.sim(0, 0, world, Action.dig(10,10));
    
    assertThat(result.teams[0].robots[1].isDead()).isEqualTo(true);
    assertThat(result.hasHole(Point.get(10,10))).isEqualTo(true);
    assertThat(result.hasHole(Point.get(11,10).offset)).isEqualTo(true);
    assertThat(result.hasHole(Point.get(9,10).offset)).isEqualTo(true);
    assertThat(result.hasHole(Point.get(10,11).offset)).isEqualTo(true);
    assertThat(result.hasHole(Point.get(10,9).offset)).isEqualTo(true);
  }
  
  private void moveRobot(int teamIdx, int robotIdx, int x, int y) {
    world.teams[teamIdx].robots[robotIdx].pos = Point.get(x, y);
  }

  private void putTrapOn(int x, int y) {
    int offset = Point.get(x, y).offset;
    world.setHole(offset);
    world.putTrap(offset);
  }
}
