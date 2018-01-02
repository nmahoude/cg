package meanmax.simulation;

import org.junit.Before;
import org.junit.Test;

import meanmax.Game;
import meanmax.ai.eval.EvalOrigin;
import meanmax.entities.Entity;

public class SimulationPERF {
  static Entity entities[] = new Entity[100];
  EvalOrigin eval = new EvalOrigin();
  Simulation simulation;
  Action actions[][] = new Action[3][9];
  
  @Before 
  public void setup() {
    Game.DEBUG_INPUT = false;
    Game.turnInit();
    
    simulation = new Simulation();
    for (int j=0;j<3;j++) {
      actions[j] = new Action[9];
      for (int i=0;i<9;i++) {
        actions[j][i] = new Action();
      }
    }
  }
  
  @Test
  public void multipleSteps() throws Exception {
    SimulationTest.read(0,0,1,0.5,400.0,-1369.0,-1561.0,106.0,-395.0,-1,-1);
    SimulationTest.read(1,1,1,1.5,400.0,-2180.0,-205.0,-195.0,-238.0,-1,-1);
    SimulationTest.read(2,2,1,1.0,400.0,135.0,-2192.0,-123.0,-314.0,-1,-1);
    SimulationTest.read(3,0,0,0.5,400.0,976.0,-2010.0,492.0,-717.0,-1,-1);
    SimulationTest.read(4,1,0,1.5,400.0,-873.0,-2550.0,20.0,-345.0,-1,-1);
    SimulationTest.read(5,2,0,1.0,400.0,-381.0,552.0,-600.0,-15.0,-1,-1);
    SimulationTest.read(6,0,2,0.5,400.0,1208.0,-3666.0,437.0,-1533.0,-1,-1);
    SimulationTest.read(7,1,2,1.5,400.0,-2021.0,-1018.0,-126.0,-82.0,-1,-1);
    SimulationTest.read(8,2,2,1.0,400.0,1940.0,-1230.0,-159.0,52.0,-1,-1);
    SimulationTest.read(43,3,-1,4.5,600.0,-5630.0,-5698.0,-116.0,-118.0,4,4);
    SimulationTest.read(55,3,-1,5.5,700.0,-3876.0,3145.0,-105.0,85.0,6,6);
    SimulationTest.read(63,3,-1,5.5,700.0,2445.0,694.0,-31.0,11.0,6,6);
    SimulationTest.read(65,3,-1,5.5,850.0,1507.0,1952.0,-68.0,42.0,6,9);
    SimulationTest.read(67,3,-1,3.0,800.0,3215.0,-641.0,-148.0,-87.0,1,8);
    SimulationTest.read(70,3,-1,3.0,800.0,-1722.0,5245.0,79.0,-241.0,1,8);
    SimulationTest.read(72,3,-1,3.0,700.0,-2751.0,-5636.0,115.0,235.0,1,6);
    SimulationTest.read(75,3,-1,3.0,850.0,-7577.0,1753.0,296.0,-69.0,1,9);
    SimulationTest.read(76,3,-1,3.0,600.0,6837.0,-3150.0,-276.0,127.0,1,4);
    SimulationTest.read(44,4,-1,-1.0,750.0,1047.0,-5003.0,0.0,0.0,1,-1);
    SimulationTest.read(62,4,-1,-1.0,800.0,4741.0,-1212.0,0.0,0.0,3,-1);
    SimulationTest.read(71,4,-1,-1.0,750.0,1036.0,-3391.0,0.0,0.0,7,-1);
    SimulationTest.read(73,4,-1,-1.0,800.0,-2450.0,-946.0,0.0,0.0,8,-1);
    SimulationTest.read(74,4,-1,-1.0,800.0,-1634.0,-3169.0,0.0,0.0,8,-1);
    SimulationTest.read(77,6,-1,-1.0,1000.0,1036.0,-3391.0,0.0,0.0,2,-1);
    Game.backup();

    prepareActions();
    final int ITERATIONS = 100_000;
    for (int loop=0;loop<ITERATIONS;loop++) {
      for (int i=0;i<3;i++) {
        simulation.simulate(actions[i]);
      }
      Game.restore();
    }    
  }

  private void prepareActions() {
    int i=0;
    actions[0][i] = Action.parse(Game.entities[i++], "-2251 3496 300");
    actions[0][i] = Action.parse(Game.entities[i++], "-2487 -1280 300");
    actions[0][i] = Action.parse(Game.entities[i++], "WAIT");
    actions[0][i] = Action.parse(Game.entities[i++], "-2288 -1213 300");
    actions[0][i] = Action.parse(Game.entities[i++], "-2376 -1289 300");
    actions[0][i] = Action.parse(Game.entities[i++], "-874 -1393 300");
    actions[0][i] = Action.parse(Game.entities[i++], "-3010 -1150 300");
    actions[0][i] = Action.parse(Game.entities[i++], "-2375 -1249 300");
    actions[0][i] = Action.parse(Game.entities[i++], "-1380 48 300");

    i=0;
    actions[1][i] = Action.parse(Game.entities[i++], "-2474 2843 300");
    actions[1][i] = Action.parse(Game.entities[i++], "-2554 -1317 300");
    actions[1][i] = Action.parse(Game.entities[i++], "WAIT");
    actions[1][i] = Action.parse(Game.entities[i++], "-2120 -1384 300");
    actions[1][i] = Action.parse(Game.entities[i++], "-2494 -1339 300");
    actions[1][i] = Action.parse(Game.entities[i++], "-848 -2061 300");
    actions[1][i] = Action.parse(Game.entities[i++], "-2776 -788 300");
    actions[1][i] = Action.parse(Game.entities[i++], "-2352 -1273 300");
    actions[1][i] = Action.parse(Game.entities[i++], "-2081 150 300");
    
    i=0;
    actions[2][i] = Action.parse(Game.entities[i++], "-2511 2076 300");
    actions[2][i] = Action.parse(Game.entities[i++], "-2614 -1350 300");
    actions[2][i] = Action.parse(Game.entities[i++], "WAIT");
    actions[2][i] = Action.parse(Game.entities[i++], "0 0 0");
    actions[2][i] = Action.parse(Game.entities[i++], "-2456 -1330 300");
    actions[2][i] = Action.parse(Game.entities[i++], "-1372 -2713 300");
    actions[2][i] = Action.parse(Game.entities[i++], "-2539 -822 300");
    actions[2][i] = Action.parse(Game.entities[i++], "-2332 -1341 300");
    actions[2][i] = Action.parse(Game.entities[i++], "-2721 -228 300");
  }
}
