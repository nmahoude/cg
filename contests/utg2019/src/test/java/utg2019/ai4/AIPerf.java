package utg2019.ai4;

import org.junit.Test;

import trigonometryInt.Point;
import utg2019.Player;
import utg2019.world.World;

public class AIPerf {

  
  private static final int TIME_DECAL = 10_000;

  @Test
  public void perf() throws Exception {
    Player.DEBUG_AI_CHOICE = true;
    
    Point.init(30, 15);
    World world = new World();
    Player.init();

    Player.start = System.currentTimeMillis() + TIME_DECAL;
    world.teams[0].robots[0].pos = Point.get(10, 0);
    world.teams[0].robots[1].pos = Point.get(10, 3);
    world.teams[0].robots[2].pos = Point.get(10, 6);
    world.teams[0].robots[3].pos = Point.get(10, 9);
    world.teams[0].robots[4].pos = Point.get(10, 12);
    
    AI ai = new AI();
    ai.init();
    
    for (int i=0;i<1000;i++) {
      ai.think(world);
    }
    
    System.err.println("Think in "+(System.currentTimeMillis()-Player.start+TIME_DECAL)+"ms");
  }
}
