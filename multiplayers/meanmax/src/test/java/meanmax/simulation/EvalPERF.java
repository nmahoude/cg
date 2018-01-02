package meanmax.simulation;

import org.junit.Before;
import org.junit.Test;

import meanmax.Game;
import meanmax.ai.eval.Eval;
import meanmax.ai.eval.EvalV2;
import meanmax.entities.Entity;

public class EvalPERF {
  static Entity entities[] = new Entity[100];
  Eval eval = new EvalV2();
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
    SimulationTest.read(0,0,0,0.5,400,-2464.0,4384.0,-748.0,-411.0,-1,-1);
    SimulationTest.read(1,1,0,1.5,400,-2251.0,3496.0,-213.0,-453.0,-1,-1);
    SimulationTest.read(2,2,0,1.0,400,4655.0,620.0,-154.0,-232.0,-1,-1);
    SimulationTest.read(3,0,1,0.5,400,-1361.0,302.0,-265.0,-104.0,-1,-1);
    SimulationTest.read(4,1,1,1.5,400,-63.0,317.0,-177.0,-28.0,-1,-1);
    SimulationTest.read(5,2,1,1.0,400,-1615.0,-488.0,289.0,-64.0,-1,-1);
    SimulationTest.read(6,0,2,0.5,400,-1042.0,-1290.0,457.0,-167.0,-1,-1);
    SimulationTest.read(7,1,2,1.5,400,461.0,-725.0,-178.0,-68.0,-1,-1);
    SimulationTest.read(8,2,2,1.0,400,-629.0,-289.0,-246.0,150.0,-1,-1);
    SimulationTest.read(10,3,-1,4.0,700,-2487.0,-1280.0,-66.0,-37.0,3,6);
    SimulationTest.read(11,3,-1,4.0,700,2247.0,-1415.0,-127.0,80.0,3,6);
    SimulationTest.read(13,3,-1,3.0,750,5242.0,1684.0,-201.0,-15.0,1,7);
    
    Game.backup();

    prepareActions();
    final int ITERATIONS = 1_000_000;
    for (int i=0;i<3;i++) {
      simulation.simulate(actions[i]);
      for (int loop=0;loop<ITERATIONS;loop++) {
        eval.eval();
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
