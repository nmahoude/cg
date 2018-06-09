package thales.physics;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.Scanner;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import thales.Player;
import thales.Team;
import thales.UFO;


public class SimulationTest {
  static Simulation simulation = new Simulation();
  
  @BeforeClass
  public static void globalSetup() {
    Player.DEBUG_OUTPUT = false;
  }

  @Before
  public void setup() {
    Player.turn = 10;
  }
  
  @Test
  public void fromScracth() throws Exception {
    Player.turn = 0;
    String state = "1000 2374\r\n" + 
        "9000 5626\r\n" + 
        "4664 5325 168 268 0\r\n" + 
        "922 2330 -447 170 0\r\n" + 
        "1739 2407 -290 39 0\r\n" + 
        "4615 2897 -280 -211 0";
    Scanner sc = new Scanner(state);
    Player.readWorld(sc);
    
    String in = 
        "1489 2888 63\r\n" + 
        "524 4419 81";
    
    updateUFOs(Player.teams[0], new Scanner(in));

    simulation.move();
    
    String future = "1000 1799\r\n" + 
        "9000 6201\r\n" + 
        "559 2523 52 21 0\r\n" + 
        "502 5419 1 -72 0\r\n" + 
        "9400 2492 -89 -7 0\r\n" + 
        "9408 5460 -82 -35 0";
    
    // TODO assertTeam(new Scanner(future));
  }

  private void assertTeam(Scanner in) {
    in.nextInt();
    in.nextInt();

    in.nextInt();
    in.nextInt();
  
    for (int i=0;i<2;i++) {
      double x = in.nextInt();
      double y = in.nextInt();
      double vx = in.nextInt();
      double vy = in.nextInt();
      int flag = in.nextInt();
      
      assertThat(Player.entities[0].x, is(x));
      assertThat(Player.entities[0].y, is(y));
      assertThat(Player.entities[0].vx, is(vx));
      assertThat(Player.entities[0].vy, is(vy));
    }
    
  }

  private void updateUFOs(Team team, Scanner in) {
    for (int i=0;i<2;i++) {
      double tx = in.nextInt();
      double ty = in.nextInt();
      double thrust = in.nextInt();
      
      UFO ufo = team.ufos[i];
      ufo.applyTarget(tx, ty, thrust);
      
    }
  }
}
