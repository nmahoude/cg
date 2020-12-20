package fantasticBitsMulti;

import static fantasticBitsMulti.TestOutputer.EOF;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.*;

import java.util.Scanner;

import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;

import fantasticBitsMulti.simulation.Action;
import fantasticBitsMulti.simulation.Simulation;
import fantasticBitsMulti.spells.Spell;
import fantasticBitsMulti.units.Wizard;

public class PlayerTest {

  private Action[] actions;
  private Simulation simulation;


  @Before
  public void setup() {
    simulation = new Simulation();

    actions = new Action[4];
    for(int i=0;i<4;i++) {
      actions[i] = new Action();
    }
  }
  
  
  @Test
  public void checkRead() throws Exception {
    Player.init(0);
    
    String input = ""
        +"0 0 "+EOF
        +"0 0 "+EOF
        +"13 "+EOF
        +"0 WIZARD 1000 5250 0 0 0 "+EOF
        +"1 WIZARD 1000 2250 0 0 0 "+EOF
        +"2 OPPONENT_WIZARD 15000 2250 0 0 0 "+EOF
        +"3 OPPONENT_WIZARD 15000 5250 0 0 0 "+EOF
        +"4 SNAFFLE 2411 2725 0 0 0 "+EOF
        +"5 SNAFFLE 13589 4775 0 0 0 "+EOF
        +"6 SNAFFLE 6170 5908 0 0 0 "+EOF
        +"7 SNAFFLE 9830 1592 0 0 0 "+EOF
        +"8 SNAFFLE 5678 3077 0 0 0 "+EOF
        +"9 SNAFFLE 10322 4423 0 0 0 "+EOF
        +"10 SNAFFLE 8000 3750 0 0 0 "+EOF
        +"11 BLUDGER 7450 3750 0 0 -1 "+EOF
        +"12 BLUDGER 8550 3750 0 0 -1 "+EOF
        ;
    Player.state.read(new Scanner(input));
    
    readActions(
        "MOVE 2736 -4598 150",
        "MOVE 2736 -7598 150",
        "MOVE 6340 7250 150",
        "MOVE 8572 -2410 150"
        );
    
    simulation.simulate(actions);
    
    debugSimulationResult();
    
    
    String result = ""
        +"0 1 "+EOF
        +"0 1 "+EOF
        +"13 "+EOF
        +"0 WIZARD 1026 5102 20 -111 0 "+EOF
        +"1 WIZARD 1026 2102 20 -111 0 "+EOF
        +"2 OPPONENT_WIZARD 14870 2325 -97 56 0 "+EOF
        +"3 OPPONENT_WIZARD 14904 5135 -72 -86 0 "+EOF
        +"4 SNAFFLE 2411 2725 0 0 0 "+EOF
        +"5 SNAFFLE 13589 4775 0 0 0 "+EOF
        +"6 SNAFFLE 6170 5908 0 0 0 "+EOF
        +"7 SNAFFLE 9830 1592 0 0 0 "+EOF
        +"8 SNAFFLE 5678 3077 0 0 0 "+EOF
        +"9 SNAFFLE 10322 4423 0 0 0 "+EOF
        +"10 SNAFFLE 8000 3750 0 0 0 "+EOF
        +"11 BLUDGER 7328 3778 -110 25 -1 "+EOF
        +"12 BLUDGER 8672 3722 110 -25 -1 "+EOF
        ;
    
    State realResult = new State(Player.state.myTeam);
    realResult.read(new Scanner(result));
    
    assertSameState(Player.state, realResult);
  }

  @Test
  public void petrificusByEnemy() throws Exception {
    Player.init(0);
    Player.state.read(new Scanner(""
        +"0 10 1 10 "+EOF
        +"12 "+EOF
        +"0 WIZARD 5795 2164 -416 47 0 "+EOF
        +"1 WIZARD 10036 2142 356 281 0 "+EOF
        +"2 OPPONENT_WIZARD 2505 3535 -674 35 0 "+EOF
        +"3 OPPONENT_WIZARD 8298 2901 122 -89 0 "+EOF
        +"4 SNAFFLE 1432 3834 -488 3 0 "+EOF
        +"5 SNAFFLE 8408 2358 400 171 0 "+EOF
        +"6 SNAFFLE 6170 5908 0 0 0 "+EOF
        +"7 SNAFFLE 11055 2298 1140 352 0 "+EOF
        +"8 SNAFFLE 5370 3146 -260 105 0 "+EOF
        +"10 SNAFFLE 9798 2415 6 194 0 "+EOF
        +"11 BLUDGER 7487 2043 184 223 1 "+EOF
        +"12 BLUDGER 6396 2237 -121 -273 0 "+EOF)); 
    
    readActions(
        "MOVE 15192 5584 150",
        "MOVE 639 -1278 150",
        "PETRIFICUS 7",
        "MOVE 18146 4637 150"
        );
    
    
    Player.state.bludgers[1].last = Player.state.wizards[0];
    simulation.simulate(actions);
    
    String result = ""
        +"0 11 1 1 "+EOF
        +"12 "+EOF
        +"0 WIZARD 5520 2262 -206 74 0 "+EOF
        +"1 WIZARD 10251 2372 161 172 0 "+EOF
        +"2 OPPONENT_WIZARD 1831 3570 -506 26 0 "+EOF
        +"3 OPPONENT_WIZARD 8568 2838 202 -47 1 "+EOF
        +"4 SNAFFLE 944 3837 -366 2 0 "+EOF
        +"5 SNAFFLE 8568 2838 202 -47 1 "+EOF
        +"6 SNAFFLE 6170 5908 0 0 0 "+EOF
        +"7 SNAFFLE 12195 2650 855 264 0 "+EOF
        +"8 SNAFFLE 5110 3251 -195 79 0 "+EOF
        +"10 SNAFFLE 9804 2609 5 146 0 "+EOF
        +"11 BLUDGER 7757 2357 243 282 1 "+EOF
        +"12 BLUDGER 6393 2005 -3 -209 0 "+EOF
        ;
    
    State realResult = new State(Player.state.myTeam);
    realResult.read(new Scanner(result));
    
    assertSameState(Player.state, realResult);
    
    
  }
  
  
  private void debugSimulationResult() {
    System.err.println("RESULT");
    System.err.println("------");
    System.err.println("My score "+Player.state.teamInfos[0]);
    System.err.println("Opp score "+Player.state.teamInfos[1]);
    for (int i=0;i<Player.state.unitsFE;i++) {
      System.err.println(""+Player.state.units[i]);
    }
  }


  private void assertSameState(State state, State result) {
    assertThat(state.teamInfos[0].mana).isEqualTo(result.teamInfos[0].mana);
    assertThat(state.teamInfos[0].score).isEqualTo(result.teamInfos[0].score);
    assertThat(state.teamInfos[1].mana).isEqualTo(result.teamInfos[1].mana);
    assertThat(state.teamInfos[1].score).isEqualTo(result.teamInfos[1].score);
    
    for (int i=0;i<Player.state.unitsFE;i++) {
      assertThat(Player.state.units[i]).isEqualTo(result.units[i]);
    }    
  }


  private void readActions(String... actionsStr) {
    
    for (int i=0;i<4;i++) {
      int index;
      if (Player.state.myTeam == 0) {
        index = i;
      } else {
        index = (2 + i) % 4;
      }
      Action action = actions[index];
      updateWizard(action, Player.state.wizards[index], actionsStr[i]);
    }
  }


  private void updateWizard(Action action, Wizard wizard, String actionStr) {
    double dx;
    double dy;
    double length;
    String[] as  = actionStr.split(" ");
    switch(as[0]) {
      case "THROW":
        action.type = Action.TYPE_THROW;
        dx = Integer.parseInt(as[1])-wizard.position.x;
        dy = Integer.parseInt(as[2])-wizard.position.y;
        length = Math.sqrt(dx*dx+dy*dy);

        action.angle = (int)(0.5 + (180 * Math.acos(dx/length) / Math.PI ) / 10);
        if (dy / length < 0) action.angle= 36-action.angle;
        action.thrust = Integer.parseInt(as[3]);
        break;
      case "MOVE": 
        action.type = Action.TYPE_MOVE;
        dx = Integer.parseInt(as[1])-wizard.position.x;
        dy = Integer.parseInt(as[2])-wizard.position.y;
        length = Math.sqrt(dx*dx+dy*dy);

        action.angle = (int)(0.5 + (180 * Math.acos(dx/length) / Math.PI ) / 10);
        if (dy / length < 0) action.angle= 36-action.angle;
        action.thrust = Integer.parseInt(as[3]);
        break;
      case "PETRIFICUS":
        action.type = Action.TYPE_CAST;
        action.spellId = Spell.PETRIFICUS;
        action.target = Player.state.unitsById[Integer.parseInt(as[1])];
        break;
      default:
        throw new RuntimeException("still to do ...."+as[0]);
    }
  }
  
}
