package utg2019.ai4;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import trigonometryInt.Point;
import utg2019.RobotBuilder;
import utg2019.sim.Action;
import utg2019.world.World;
import utg2019.world.entity.Robot;
import utg2019.world.maps.TrapAdvisor;

public class GoalInfoTest {

  
  private World current;
  private World old;
  private TrapAdvisor trapAdvisor;
  
  private static Action emptyActions[] = new Action[5];
  private GoalInfo goalInfo;
  private Robot robot;
  private Action[] actions;
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
    
    goalInfo = new GoalInfo();
    robot = current.teams[0].robots[0];
    actions = new Action[50];
  }
  
  @Test
  public void actionsMoveToBase_tropLoin_doitAllerEnLigneDroite() throws Exception {
    RobotBuilder.robot(0, 0).stayedAt(Point.get(10, 5));
    goalInfo.goal = Goal.MOVE_TO_BASE;
    
    int fe = goalInfo.getMicroActions(current, robot, actions);
    
    assertThat(fe).isEqualTo(1);
    assertThat(actions[0]).isEqualTo(Action.move(Point.get(6, 5)));
  }

  @Test
  public void actionsMoveToBase_tropLoinAvecTarget_doitAvoirPlusieursChoix() throws Exception {
    RobotBuilder.robot(0, 0).stayedAt(Point.get(10, 5));
    Point target = Point.get(10, 4);
    
    int fe = GoalInfo.actionsToMoveToBase2(robot, target, actions, 0);
    
    assertThat(fe).isEqualTo(2);
    assertThat(actions).contains(Action.move(Point.get(6, 5)));
    assertThat(actions).contains(Action.move(Point.get(7, 4)));
  }

  @Test
  public void actionsMoveToBase_assezPretTarget_doitAvoirUnSeulChoix() throws Exception {
    RobotBuilder.robot(0, 0).stayedAt(Point.get(2, 5));
    Point target = Point.get(10, 1);
    
    int fe = GoalInfo.actionsToMoveToBase2(robot, target, actions, 0);
    
    assertThat(fe).isEqualTo(1);
    assertThat(actions).contains(Action.move(Point.get(0, 3)));
  }
  
  @Test
  public void actionsMoveToBase_justePasAssez_doitAllerEnLigneDroite() throws Exception {
    RobotBuilder.robot(0, 0).stayedAt(Point.get(5, 5));
    goalInfo.goal = Goal.MOVE_TO_BASE;
    
    int fe = goalInfo.getMicroActions(current, robot, actions);
    
    assertThat(fe).isEqualTo(1);
    assertThat(actions[0]).isEqualTo(Action.move(Point.get(1, 5)));
  }

  
  @Test
  public void actionsMoveToBase_justeAssez_doitAllerEnLigneDroite() throws Exception {
    RobotBuilder.robot(0, 0).stayedAt(Point.get(4, 5));
    goalInfo.goal = Goal.MOVE_TO_BASE;
    
    int fe = goalInfo.getMicroActions(current, robot, actions);
    
    assertThat(fe).isEqualTo(1);
    assertThat(actions[0]).isEqualTo(Action.move(Point.get(0, 5)));
  }

  @Test
  public void actionsMoveToBase_uneCasePlusPret_doitAllerEnLigneDroite() throws Exception {
    RobotBuilder.robot(0, 0).stayedAt(Point.get(3, 5));
    goalInfo.goal = Goal.MOVE_TO_BASE;
    
    int fe = GoalInfo.actionsToMoveToBase2(robot, Point.Invalid, actions, 0);
    
    assertThat(fe).isEqualTo(3);
    assertThat(actions).contains(Action.move(Point.get(0, 4)));
    assertThat(actions).contains(Action.move(Point.get(0, 5)));
    assertThat(actions).contains(Action.move(Point.get(0, 6)));
  }
  
  @Test
  public void moveToTarget_tooFarSameLineOnlyOneSolution() throws Exception {
    RobotBuilder.robot(0, 0).stayedAt(Point.get(3, 5));
    
    int fe = GoalInfo.actionsToDigOnTarget(robot, Point.get(29, 5), actions, 0);
    
    assertThat(fe).isEqualTo(1);
    assertThat(actions).contains(Action.move(Point.get(7, 5)));
  }

  @Test
  public void moveToTarget_canGoOnNeighborPositionToo() throws Exception {
    RobotBuilder.robot(0, 0).stayedAt(Point.get(4, 5));
    
    int fe = GoalInfo.actionsToDigOnTarget(robot, Point.get(2, 5), actions, 0);
    
    assertThat(fe).isEqualTo(5);
    assertThat(actions).contains(Action.move(Point.get(2, 4)),
                                  Action.move(Point.get(2, 5)),
                                  Action.move(Point.get(2, 6)),
                                  Action.move(Point.get(1, 5)),
                                  Action.move(Point.get(3, 5)));
  }

  @Test
  public void moveToTarget_tooFarButNotOnSameLine_multipleSolutions() throws Exception {
    RobotBuilder.robot(0, 0).stayedAt(Point.get(3, 5));
    
    int fe = GoalInfo.actionsToDigOnTarget(robot, Point.get(29, 8), actions, 0);
    
    assertThat(fe).isEqualTo(4);
    assertThat(actions).contains(Action.move(Point.get(7, 5)));
    assertThat(actions).contains(Action.move(Point.get(6, 6)));
    assertThat(actions).contains(Action.move(Point.get(5, 7)));
    assertThat(actions).contains(Action.move(Point.get(4, 8)));
  }
}
