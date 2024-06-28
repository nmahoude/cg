package utg2019.ai4;

import static utg2019.RobotBuilder.robot;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import trigonometryInt.Point;
import utg2019.Player;
import utg2019.RobotBuilder;
import utg2019.sim.Action;
import utg2019.world.World;
import utg2019.world.maps.TrapAdvisor;

public class AITest {
  private World current;
  private World old;
  private TrapAdvisor trapAdvisor;
  
  private static Action emptyActions[] = new Action[5];
  private AI ai;
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
    
    Player.trapAdvisor = trapAdvisor;
    ai = new AI();
    ai.init();
    ai.world = current;
  }

  
  @Test
  public void potentialOreIsRemovedFromGoalObjectiveWhenThinking() throws Exception {
    
    robot(0, 0).stayedAt(Point.get(4, 0));
    robot(0, 1).stayedAt(Point.get(4, 0));
    robot(0, 2).stayedAt(Point.get(4, 0));
    robot(0, 3).stayedAt(Point.get(4, 0));
    robot(0, 4).stayedAt(Point.get(4, 0));
    
    Player.oracle.potentialOre[Point.get(4, 12).offset] = 2;
    
    ai.infos[0][ai.infosFE[0]++].createDig(current, current.teams[0].robots[0], Point.get(4, 12));
    ai.infos[1][ai.infosFE[1]++].createDig(current, current.teams[0].robots[1], Point.get(4, 12));
    ai.infos[2][ai.infosFE[2]++].createDig(current, current.teams[0].robots[2], Point.get(4, 12));
    ai.infos[3][ai.infosFE[3]++].createDig(current, current.teams[0].robots[3], Point.get(4, 12));
    ai.infos[4][ai.infosFE[4]++].createDig(current, current.teams[0].robots[4], Point.get(4, 12));
    
    World parent = ai.prepareWorldForGoalSelection(current);
    ai.selectGoalRecursive(0, 0.0, parent);
    
  }
}
