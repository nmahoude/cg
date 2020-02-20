package thales.physics;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

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
    Player.initTeamRegularDirection();
    
  }
  
  @Test
  public void fromScracth() throws Exception {
    Player.turn = 0;
    String state = "-1000 -1000\r\n" + 
        "-1000 -1000\r\n" + 
        "1042 742 -402 -265 0\r\n" + 
        "9110 5359 743 -190 1\r\n" + 
        "1936 1638 162 -216 0\r\n" + 
        "4912 7472 557 89 1";
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

  @Test
  public void wallShock() throws Exception {
    Player.turn = 1;
    String state = "1000 4101\r\n" + 
        "9000 5512\r\n" + 
        "9379 2095 249 -118 0\r\n" + 
        "998 4062 0 14 0\r\n" + 
        "1622 3202 73 444 0\r\n" + 
        "4788 1877 -689 169 0"; // <- this one
    Scanner sc = new Scanner(state);
    Player.readWorld(sc);
    
    String in = 
        "9881 1230 67\r\n" + 
        "-2 4062 0";
    
    updateUFOs(Player.teams[0], new Scanner(in));

    String in2  =
        "1000 4101 100\r\n" + 
        "1000 4101 100";
    updateUFOs(Player.teams[1], new Scanner(in2));
    
    simulation.move();
    
    String future = 
        "1000 4101\r\n" + 
        "9000 5512\r\n" + 
        "9538 1919 -254 -158 0\r\n" + 
        "960 4105 -128 110 0\r\n" + 
        "1676 3699 142 375 0\r\n" + 
        "4012 2097 -697 197 0";
    
    //assertTeam(new Scanner(future));
  }
  
  
  private void assertTeam(Scanner in) {
    assertThat(((int)Player.teams[0].flag.x), is(in.nextInt()));
    assertThat(((int)Player.teams[0].flag.y), is(in.nextInt()));

    assertThat(((int)Player.teams[1].flag.x), is(in.nextInt()));
    assertThat(((int)Player.teams[1].flag.y), is(in.nextInt()));
  
    for (int i=0;i<4;i++) {
      double x = in.nextInt();
      double y = in.nextInt();
      double vx = in.nextInt();
      double vy = in.nextInt();
      boolean flag = in.nextInt() == 1;
      
      UFO ufo = (UFO)Player.entities[i];
      assertThat(ufo.x, is(x));
      assertThat(ufo.y, is(y));
      assertThat(ufo.vx, is(vx));
      assertThat(ufo.vy, is(vy));
      assertThat(ufo.flag, is (flag));
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
