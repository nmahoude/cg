package meanmax.ai.eval;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

import java.util.Locale;
import java.util.Scanner;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import meanmax.Game;
import meanmax.ai.ag.AGSolution;
import meanmax.ai.eval.EvalOrigin;
import meanmax.entities.Entity;
import meanmax.entities.Wreck;
import meanmax.simulation.Action;
import meanmax.simulation.Simulation;
import trigo.Position;
import trigo.Speed;

public class EvalOriginTest {
  static Entity entities[] = new Entity[100];
  Simulation simulation;
  Action actions[] = new Action[9];
  EvalOrigin eval;
  private int currentAction;
  
  @Before 
  public void setup() {
    Game.DEBUG_INPUT = false;
    Game.turnInit();
    
    eval = new EvalOrigin();
    simulation = new Simulation();
    for (int i=0;i<9;i++) {
      actions[i] = new Action();
    }
  }

  @Test
  public void isDefended_staticAndCenter() throws Exception {
    readAllEntitiesOut();
    Game.players[1].doof.position = new Position(1000, 1000);
    Game.players[1].doof.speed = new Speed(0.0, 0.0);
    
    read(9,4,-1,5.0,650.0,/**/1000.0,1000.0/**/,0.0,0.0,5,5);

    EvalOrigin eval = new EvalOrigin();
    
    assertThat(eval.isDefended((Wreck)Game.entities[9]), is(true));
  }
  
  @Test
  public void isDefended_speedLimitAndCenter() throws Exception {
    readAllEntitiesOut();
    Game.players[1].doof.position = new Position(1000, 1000);
    Game.players[1].doof.speed = new Speed(-150.0, 150.0);
    
    read(9,4,-1,5.0,650.0,/**/1000.0,1000.0/**/,0.0,0.0,5,5);

    EvalOrigin eval = new EvalOrigin();
    
    assertThat(eval.isDefended((Wreck)Game.entities[9]), is(true));
  }

  private void readAllEntitiesOut() {
    double OUT = 1_000_000;
    read(0,0,0,0.5,400.0,OUT,OUT,0.0,0.0,-1,-1);
    read(1,1,0,1.5,400.0,OUT,OUT,0.0,0.0,-1,-1);
    read(2,2,0,1.0,400.0,OUT,OUT,0.0,0.0,-1,-1);
    read(3,0,1,0.5,400.0,OUT,OUT,0.0,0.0,-1,-1);
    read(4,1,1,1.5,400.0,OUT,OUT,0.0,0.0,-1,-1);
    read(5,2,1,1.0,400.0,OUT,OUT,0.0,0.0,-1,-1);
    read(6,0,2,0.5,400.0,OUT,OUT,0.0,0.0,-1,-1);
    read(7,1,2,1.5,400.0,OUT,OUT,0.0,0.0,-1,-1);
    read(8,2,2,1.0,400.0,OUT,OUT,0.0,0.0,-1,-1);
  }
  
  
  @Test
  public void nearestWreckIsOiled() throws Exception {
    read(0,0,1,0.5,400.0,-1369.0,-1561.0,106.0,-395.0,-1,-1);
    read(1,1,1,1.5,400.0,-2180.0,-205.0,-195.0,-238.0,-1,-1);
    read(2,2,1,1.0,400.0,135.0,-2192.0,-123.0,-314.0,-1,-1);
    read(3,0,0,0.5,400.0,976.0,-2010.0,492.0,-717.0,-1,-1);
    read(4,1,0,1.5,400.0,-873.0,-2550.0,20.0,-345.0,-1,-1);
    read(5,2,0,1.0,400.0,-381.0,552.0,-600.0,-15.0,-1,-1);
    read(6,0,2,0.5,400.0,1208.0,-3666.0,437.0,-1533.0,-1,-1);
    read(7,1,2,1.5,400.0,-2021.0,-1018.0,-126.0,-82.0,-1,-1);
    read(8,2,2,1.0,400.0,1940.0,-1230.0,-159.0,52.0,-1,-1);
    read(43,3,-1,4.5,600.0,-5630.0,-5698.0,-116.0,-118.0,4,4);
    read(55,3,-1,5.5,700.0,-3876.0,3145.0,-105.0,85.0,6,6);
    read(63,3,-1,5.5,700.0,2445.0,694.0,-31.0,11.0,6,6);
    read(65,3,-1,5.5,850.0,1507.0,1952.0,-68.0,42.0,6,9);
    read(67,3,-1,3.0,800.0,3215.0,-641.0,-148.0,-87.0,1,8);
    read(70,3,-1,3.0,800.0,-1722.0,5245.0,79.0,-241.0,1,8);
    read(72,3,-1,3.0,700.0,-2751.0,-5636.0,115.0,235.0,1,6);
    read(75,3,-1,3.0,850.0,-7577.0,1753.0,296.0,-69.0,1,9);
    read(76,3,-1,3.0,600.0,6837.0,-3150.0,-276.0,127.0,1,4);
    read(44,4,-1,-1.0,750.0,1047.0,-5003.0,0.0,0.0,1,-1);
    read(62,4,-1,-1.0,800.0,4741.0,-1212.0,0.0,0.0,3,-1);
    read(71,4,-1,-1.0,750.0,1036.0,-3391.0,0.0,0.0,7,-1);
    read(73,4,-1,-1.0,800.0,-2450.0,-946.0,0.0,0.0,8,-1);
    read(74,4,-1,-1.0,800.0,-1634.0,-3169.0,0.0,0.0,8,-1);
    read(77,6,-1,-1.0,1000.0,1036.0,-3391.0,0.0,0.0,2,-1);
    
    assertThat(eval.isHarvestable((Wreck) Game.entities[20], Game.players[0].reaper), is(false));
  }
  @Test
  public void evalTest1() throws Exception {
    read(0,0,0,0.5,400.0,-2055.0,4204.0,12.0,-21.0,-1,-1);
    read(1,1,0,1.5,400.0,-483.0,4325.0,19.0,-19.0,-1,-1);
    read(2,2,0,1.0,400.0,3327.0,2692.0,-7.0,-225.0,-1,-1);
    read(3,0,1,0.5,400.0,-2238.0,-4364.0,312.0,-365.0,-1,-1);
    read(4,1,1,1.5,400.0,-3555.0,-2565.0,-28.0,37.0,-1,-1);
    read(5,2,1,1.0,400.0,-3967.0,1331.0,220.0,-47.0,-1,-1);
    read(6,0,2,0.5,400.0,4718.0,278.0,15.0,480.0,-1,-1);
    read(7,1,2,1.5,400.0,4219.0,-1778.0,137.0,-31.0,-1,-1);
    read(8,2,2,1.0,400.0,726.0,-4159.0,-147.0,170.0,-1,-1);
    read(9,3,-1,3.0,650.0,4086.0,6975.0,-172.0,-293.0,1,5);
    read(10,3,-1,3.0,650.0,-8083.0,50.0,340.0,-2.0,1,5);
    read(11,3,-1,3.0,650.0,3998.0,-7025.0,-168.0,296.0,1,5);
    read(12,3,-1,3.0,850.0,-2554.0,7879.0,105.0,-324.0,1,9);
    read(13,3,-1,3.0,850.0,-5547.0,-6151.0,228.0,252.0,1,9);
    read(14,3,-1,3.0,850.0,8101.0,-1728.0,-332.0,71.0,1,9);
    read(15,3,-1,3.0,600.0,-113.0,8032.0,5.0,-340.0,1,4);
    read(16,3,-1,3.0,600.0,-6899.0,-4115.0,292.0,174.0,1,4);
    read(17,3,-1,3.0,600.0,7013.0,-3918.0,-297.0,166.0,1,4);

    EvalOrigin eval = new EvalOrigin();
    eval.eval();
    eval.debug();
  }
  
  private void resetActions() {
    currentAction = 0;
  }

  private void parseAction(String action) {
    actions[currentAction] = Action.parse(entities[currentAction++], action);
  }

  
  static void read(int unitId, int unitType, int playerId, double mass, double radius, double x, double y, double vx, double vy, int extra, int extra2) {
    Scanner in = new Scanner(""+unitId+" "+unitType+" "+playerId+" "+mass+" "+(int)radius+" "+(int)x+" "+(int)y+" "+(int)vx+" "+(int)vy+" "+extra+" "+extra2);
    in.useLocale(Locale.ENGLISH);
    entities[unitId] = Game.readOneUnit(in);
    Game.backup(); // we backup the number of entities each turn ...
  }

}
