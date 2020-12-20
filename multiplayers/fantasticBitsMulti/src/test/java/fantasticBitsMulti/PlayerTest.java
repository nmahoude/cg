package fantasticBitsMulti;

import static fantasticBitsMulti.TestOutputer.EOF;

import java.util.Scanner;

import org.junit.Before;
import org.junit.Test;

import fantasticBitsMulti.simulation.Action;
import fantasticBitsMulti.simulation.Simulation;
import fantasticBitsMulti.units.Wizard;

public class PlayerTest {

  private Action[] actions;


  @Before
  public void setup() {
    actions = new Action[4];
    for(int i=0;i<4;i++) {
      actions[i] = new Action();
    }
  }
  
  
  @Test
  public void checkRead() throws Exception {
    Player.init(0);
    
    String input = ""
        +"0 12 0 12 "+EOF
        +"11"+EOF
        +"0 WIZARD 6489 5410 433 -33 1 "+EOF
        +"1 WIZARD 6445 2061 433 -6 0 "+EOF
        +"2 OPPONENT_WIZARD 10257 611 -303 -36 0 "+EOF
        +"3 OPPONENT_WIZARD 9548 5407 -435 12 0 "+EOF
        +"4 SNAFFLE 6489 5410 433 -33 1 "+EOF
        +"5 SNAFFLE 9604 2235 0 0 0 "+EOF
        +"6 SNAFFLE 6906 2078 0 0 0 "+EOF
        +"7 SNAFFLE 9094 5422 0 0 0 "+EOF
        +"8 SNAFFLE 8000 3750 0 0 0 "+EOF
        +"9 BLUDGER 2446 5297 -195 -30 0 "+EOF
        +"10 BLUDGER 13489 3367 166 226 2 "+EOF
        ;
    Scanner scanner = new Scanner(input);
    Player.state.read(scanner);
    
    readActions(
        "THROW 16337 3674 500",
        "MOVE 16445 2061 150",
        "MOVE 9604 2235 150",
        "MOVE 9094 5422 150"
        );
    
    new Simulation().simulate(actions);
    
    System.err.println("RESULT");
    System.err.println("------");
    System.err.println("My score "+Player.state.myScore);
    System.err.println("Opp score "+Player.state.hisScore);
    for (int i=0;i<Player.state.unitsFE;i++) {
      System.err.println(""+Player.state.units[i]);
    }
    
  }


  private void readActions(String... actionsStr) {
    double dx,dy,length;
    
    for (int i=0;i<4;i++) {
      Wizard wizard = Player.state.wizards[i];
      
      String[] as  = actionsStr[i].split(" ");
      switch(as[0]) {
        case "THROW":
            actions[i].type = Action.TYPE_THROW;
            dx = Integer.parseInt(as[1])-wizard.position.x;
            dy = Integer.parseInt(as[2])-wizard.position.y;
            length = Math.sqrt(dx*dx+dy*dy);

            actions[i].cosAngle = dx / length;
            actions[i].sinAngle = dy / length;
            actions[i].thrust = Integer.parseInt(as[3]);
            break;
        case "MOVE": 
          actions[i].type = Action.TYPE_MOVE;
          dx = Integer.parseInt(as[1])-wizard.position.x;
          dy = Integer.parseInt(as[2])-wizard.position.y;
          length = Math.sqrt(dx*dx+dy*dy);

          actions[i].cosAngle = dx / length;
          actions[i].sinAngle = dy / length;
          actions[i].thrust = Integer.parseInt(as[3]);
          break;
          default:
            throw new RuntimeException("still to do ...."+as[0]);
      }
    }
  }
  
}
