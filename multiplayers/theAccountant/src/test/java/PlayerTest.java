import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Before;
import org.junit.Test;

public class PlayerTest {

  
  public static class EnemyTest {
    
    private Player.GameEngine engine;
    @Before
    public void setup() {
      engine = new Player.GameEngine();
    }
    @Test
    public void within2000unitWolffIsDead() throws Exception {
      Player.Enemy e1 = new Player.Enemy(engine);
      e1.p = new Player.P(0,0);
      
      Player.Wolff w = new Player.Wolff(engine);
      w.p = new Player.P(2000, 0);
      
      assertThat(e1.checkForDeath(w.p), is (true));
    }
    @Test
    public void above2000isNotDeath() throws Exception {
      Player.Enemy e1 = new Player.Enemy(engine);
      e1.p = new Player.P(0,0);
      
      Player.Wolff w = new Player.Wolff(engine);
      w.p = new Player.P(2001, 0);
      
      assertThat(e1.checkForDeath(w.p), is (false));
    }
    
    @Test
    public void distanceToOneShot() throws Exception {
      Player.Enemy e1 = new Player.Enemy(engine);
      e1.p = new Player.P(0,0);
      e1.lifePoints = 10;
      
      assertThat(e1.distanceToOneShot(), is (2594));
    }
    
    @Test
    public void distanceToOneShot_When_1LF() throws Exception {
      Player.Enemy e1 = new Player.Enemy(engine);
      e1.p = new Player.P(0,0);
      e1.lifePoints = 1;
      
      assertThat(e1.distanceToOneShot(), is (17677));
    }
    
    @Test
    public void enemyReachesDPInTurns_just() throws Exception {
      Player.Enemy e1 = new Player.Enemy(engine);
      e1.p = new Player.P(0,0);
      
      Player.Wolff w = new Player.Wolff(engine);
      w.p = new Player.P(2001, 0);
      
      Player.DataPoint dp = new Player.DataPoint(new Player.P(2000,0));
      engine.dataPoints.add(dp);
      
      e1.updateTurnToReachTarget();
      
      assertThat(e1.turnToReachTarget, is (4));
    }
    
    @Test
    public void enemyReachesDPInTurns_middle() throws Exception {
      Player.Enemy e1 = new Player.Enemy(engine);
      e1.p = new Player.P(0,0);
      
      Player.Wolff w = new Player.Wolff(engine);
      w.p = new Player.P(2001, 0);
      
      Player.DataPoint dp = new Player.DataPoint(new Player.P(2250,0));
      engine.dataPoints.add(dp);
      
      e1.updateTurnToReachTarget();
      
      assertThat(e1.turnToReachTarget, is (5));
    }

    @Test
    public void shoot_near() throws Exception {
      Player.Enemy e1 = new Player.Enemy(engine);
      e1.lifePoints = 100;
      e1.p = new Player.P(0,0);
      
      Player.Wolff w = new Player.Wolff(engine);
      w.p = new Player.P(2000, 0);
      
      w.damage(e1);
      
      assertThat(e1.lifePoints, is (100-14));
    }
    
    @Test
    public void shoot_far() throws Exception {
      Player.Enemy e1 = new Player.Enemy(engine);
      e1.lifePoints = 100;
      e1.p = new Player.P(0,0);
      
      Player.Wolff w = new Player.Wolff(engine);
      w.p = new Player.P(6000, 0);
      
      w.damage(e1);
      
      assertThat(e1.lifePoints, is (100-4));
    }
  }
  
  public static class AITest {
    private Player.GameEngine engine;
    @Before
    public void setup() {
      engine = new Player.GameEngine();
      Player.gameEngineMain = engine;
    }
    @Test
    public void aiTest1() throws Exception {
      Player.DataPoint dp = new Player.DataPoint(new Player.P(8000, 4500));
      engine.dataPoints.add(dp);
      
      Player.Enemy e1 = new Player.Enemy(engine);
      e1.p = new Player.P(0,0);
      e1.lifePoints = 40;
      engine.enemies.add(e1);
      
      Player.Wolff w = new Player.Wolff(engine);
      w.p = new Player.P(2000, 0);
      engine.wolff = w;
      
      Player.AI ai = new Player.AI();
      
      ai.doYourStuff();
      
    }
  }
}
