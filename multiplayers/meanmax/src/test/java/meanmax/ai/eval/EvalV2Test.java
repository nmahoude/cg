package meanmax.ai.eval;

import java.util.Locale;
import java.util.Scanner;

import org.junit.Before;
import org.junit.Test;

import meanmax.Game;
import meanmax.entities.Entity;
import meanmax.entities.Tanker;
import meanmax.simulation.Action;
import meanmax.simulation.Simulation;

public class EvalV2Test {

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
    for (int i = 0; i < 9; i++) {
      actions[i] = new Action();
    }
  }

  @Test
  public void whyOhWhy() throws Exception {

    read(0,0,1,0.5,400.0,1616.0,1886.0,0.0,0.0,-1,-1);
    read(1,1,1,1.5,400.0,-714.0,3624.0,0.0,0.0,-1,-1);
    read(2,2,1,1.0,400.0,-1742.0,4558.0,0.0,0.0,-1,-1);
    read(3,0,0,0.5,400.0,-2441.0,457.0,0.0,0.0,-1,-1);
    read(4,1,0,1.5,400.0,-2781.0,-2431.0,0.0,0.0,-1,-1);
    read(5,2,0,1.0,400.0,-3076.0,-3788.0,0.0,0.0,-1,-1);
    read(6,0,2,0.5,400.0,825.0,-2342.0,0.0,0.0,-1,-1);
    read(7,1,2,1.5,400.0,3496.0,-1193.0,0.0,0.0,-1,-1);
    read(8,2,2,1.0,400.0,4818.0,-770.0,0.0,0.0,-1,-1);
    read(9,3,-1,3.0,750.0,-2304.0,8441.0,105.0,-386.0,1,7);
    read(10,3,-1,3.0,750.0,-6158.0,-6216.0,282.0,284.0,1,7);
    read(11,3,-1,3.0,750.0,8462.0,-2225.0,-387.0,102.0,1,7);
    read(12,3,-1,3.0,600.0,-2301.0,10142.0,89.0,-390.0,1,4);
    read(13,3,-1,3.0,600.0,-7633.0,-7064.0,294.0,272.0,1,4);
    read(14,3,-1,3.0,600.0,9934.0,-3078.0,-382.0,118.0,1,4);
    read(15,3,-1,3.0,800.0,5132.0,7148.0,-233.0,-325.0,1,8);
    read(16,3,-1,3.0,800.0,-8757.0,870.0,398.0,-40.0,1,8);
    read(17,3,-1,3.0,800.0,3625.0,-8019.0,-165.0,364.0,1,8);
    
    
    EvalV2 eval = new EvalV2();
    
    Tanker tanker = eval.getBestTanker();
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
    Scanner in = new Scanner("" + unitId + " " + unitType + " " + playerId + " " + mass + " " + (int) radius + " " + (int) x + " " + (int) y + " " + (int) vx + " " + (int) vy + " "
        + extra + " " + extra2);
    in.useLocale(Locale.ENGLISH);
    entities[unitId] = Game.readOneUnit(in);
    Game.backup(); // we backup the number of entities each turn ...
  }

}
