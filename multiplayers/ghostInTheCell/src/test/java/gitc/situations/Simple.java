package gitc.situations;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;

import gitc.GameState;
import gitc.Player;
import gitc.ag.AGSolution;
import gitc.simulation.actions.MoveAction;

public class Simple {

  @Test
  public void apiBuilding() throws Exception {
    GameState state = new GameBuilder()
      .f(new FB().id(0).mine().units(50).prod(0).build())
      .f(new FB().id(1).opp().units(25).prod(0).build())
      .l(new LB().f(0).t(1).d(2).b())
      .build();
    
    AGSolution solution1 = new AGSolution("WAIT");
    Player.simulation.simulate(solution1);
    
    AGSolution solution2 = new AGSolution("MOVE");
    solution2.players.get(0).addAction(new MoveAction(GameState.factories[0], GameState.factories[1], 50), 0);
    Player.simulation.simulate(solution2);
    
    assertThat(solution2.energy > solution1.energy, is(true));
  }
}
