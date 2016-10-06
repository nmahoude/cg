package theAccountant;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Before;
import org.junit.Test;

import trigonometry.Point;

public class PlayerTest {

  
  public static class EnemyTest {
    
    private GameEngine engine;
    @Before
    public void setup() {
      engine = new GameEngine();
    }
    @Test
    public void within2000unitWolffIsDead() throws Exception {
      Enemy e1 = new Enemy(engine);
      e1.p = new Point(0,0);
      
      Wolff w = new Wolff(engine);
      w.p = new Point(2000, 0);
      
      assertThat(e1.checkForDeath(w.p), is (true));
    }
    @Test
    public void above2000isNotDeath() throws Exception {
      Enemy e1 = new Enemy(engine);
      e1.p = new Point(0,0);
      
      Wolff w = new Wolff(engine);
      w.p = new Point(2001, 0);
      
      assertThat(e1.checkForDeath(w.p), is (false));
    }
    
    @Test
    public void distanceToOneShot() throws Exception {
      Enemy e1 = new Enemy(engine);
      e1.p = new Point(0,0);
      e1.lifePoints = 10;
      
      assertThat(e1.distanceToOneShot(), is (2594));
    }
    
    @Test
    public void distanceToOneShot_When_1LF() throws Exception {
      Enemy e1 = new Enemy(engine);
      e1.p = new Point(0,0);
      e1.lifePoints = 1;
      
      assertThat(e1.distanceToOneShot(), is (17677));
    }
    
    @Test
    public void enemyReachesDPInTurns_just() throws Exception {
      Enemy e1 = new Enemy(engine);
      e1.p = new Point(0,0);
      
      Wolff w = new Wolff(engine);
      w.p = new Point(2001, 0);
      
      DataPoint dp = new DataPoint(new Point(2000,0));
      engine.dataPoints.add(dp);
      
      e1.updateTurnToReachTarget();
      
      assertThat(e1.turnToReachTarget, is (4));
    }
    
    @Test
    public void enemyReachesDPInTurns_middle() throws Exception {
      Enemy e1 = new Enemy(engine);
      e1.p = new Point(0,0);
      
      Wolff w = new Wolff(engine);
      w.p = new Point(2001, 0);
      
      DataPoint dp = new DataPoint(new Point(2250,0));
      engine.dataPoints.add(dp);
      
      e1.updateTurnToReachTarget();
      
      assertThat(e1.turnToReachTarget, is (5));
    }

    @Test
    public void shoot_near() throws Exception {
      Enemy e1 = new Enemy(engine);
      e1.lifePoints = 100;
      e1.p = new Point(0,0);
      
      Wolff w = new Wolff(engine);
      w.p = new Point(2000, 0);
      
      w.damage(e1);
      
      assertThat(e1.lifePoints, is (100-14));
    }
    
    @Test
    public void shoot_far() throws Exception {
      Enemy e1 = new Enemy(engine);
      e1.lifePoints = 100;
      e1.p = new Point(0,0);
      
      Wolff w = new Wolff(engine);
      w.p = new Point(6000, 0);
      
      w.damage(e1);
      
      assertThat(e1.lifePoints, is (100-4));
    }
    
    @Test
    public void example2() throws Exception {
      Enemy e1 = new Enemy(engine);
      e1.id = 0;
      e1.lifePoints = 10;
      e1.p = new Point(3100,8000);
      engine.enemies.add(e1);

      Enemy e2 = new Enemy(engine);
      e1.id = 1;
      e2.lifePoints = 10;
      e2.p = new Point(14500, 8100);
      engine.enemies.add(e2);
      
      DataPoint dp1 = new DataPoint(new Point(950,7000));
      engine.dataPoints.add(dp1);

      DataPoint dp2 = new DataPoint(new Point(8000,7100));
      engine.dataPoints.add(dp2);
      
      
      Wolff w = new Wolff(engine);
      w.p = new Point(5000, 1000);
      engine.wolff = w;
      
      engine.lastCommand = new Shoot(e1);
      engine.playTurn();
      assertThat(e1.findNearestDataPoint(), is (dp1));
      assertThat(e1.p, is (new Point(2646, 7789)));
      engine.lastCommand = new Shoot(e1);
      engine.playTurn();
      assertThat(e1.p, is (new Point(2192, 7578)));
      engine.lastCommand = new Shoot(e1);
      engine.playTurn();
      assertThat(e1.p, is (new Point(1738,7367)));
      
      assertThat(e1.lifePoints, is (0));
    }
  }
  
  public static class AITest {
    private GameEngine engine;
    @Before
    public void setup() {
      engine = new GameEngine();
      Player.gameEngineMain = engine;
    }
    @Test
    public void aiTest1() throws Exception {
      DataPoint dp = new DataPoint(new Point(8000, 4500));
      engine.dataPoints.add(dp);
      
      Enemy e1 = new Enemy(engine);
      e1.p = new Point(0,0);
      e1.lifePoints = 40;
      engine.enemies.add(e1);
      
      Wolff w = new Wolff(engine);
      w.p = new Point(2000, 0);
      engine.wolff = w;
      
      Ai ai = new Ai(engine);
      
      ai.doYourStuff();
      
    }
  }
}
