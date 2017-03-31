package oldTests;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;

import gitc.GameState;
import gitc.Player;
import gitc.ag.AGSolution;
import gitc.ag.AGSolutionComparator;
import gitc.simulation.actions.MoveAction;
import gitc.situations.FB;
import gitc.situations.GameBuilder;
import gitc.situations.LB;
import gitc.situations.TB;

public class FactoryUpgradeTest {

  @Test
  public void whateDoFactory5() throws Exception {
    GameState state = new GameBuilder()
        .l(new LB().f(0).t(1).d(3).b())
        .l(new LB().f(0).t(2).d(3).b())
        .l(new LB().f(0).t(3).d(6).b())
        .l(new LB().f(0).t(4).d(6).b())
        .l(new LB().f(0).t(5).d(5).b())
        .l(new LB().f(0).t(6).d(5).b())
        .l(new LB().f(1).t(2).d(9).b())
        .l(new LB().f(1).t(3).d(1).b())
        .l(new LB().f(1).t(4).d(11).b())
        .l(new LB().f(1).t(5).d(1).b())
        .l(new LB().f(1).t(6).d(10).b())
        .l(new LB().f(2).t(3).d(11).b())
        .l(new LB().f(2).t(4).d(1).b())
        .l(new LB().f(2).t(5).d(10).b())
        .l(new LB().f(2).t(6).d(1).b())
        .l(new LB().f(3).t(4).d(14).b())
        .l(new LB().f(3).t(5).d(3).b())
        .l(new LB().f(3).t(6).d(12).b())
        .l(new LB().f(4).t(5).d(12).b())
        .l(new LB().f(4).t(6).d(3).b())
        .l(new LB().f(5).t(6).d(12).b())
    .f(new FB().id(0).player(1).units(0).prod(0).disabled(0).build())
    .f(new FB().id(1).player(-1).units(21).prod(1).disabled(0).build())
    .f(new FB().id(2).player(-1).units(50).prod(3).disabled(0).build())
    .f(new FB().id(3).player(-1).units(31).prod(3).disabled(0).build())
    .f(new FB().id(4).player(-1).units(51).prod(3).disabled(0).build())
    .f(new FB().id(5).player(1).units(34).prod(3).disabled(0).build())
    .f(new FB().id(6).player(-1).units(42).prod(3).disabled(0).build())
    .t(new TB().id(0).player(-1).from(4).to(1).units(12).turnsLeft(1))
    .t(new TB().id(1).player(1).from(1).to(6).units(1).turnsLeft(3))
    .t(new TB().id(2).player(1).from(1).to(6).units(1).turnsLeft(4))
    .t(new TB().id(3).player(1).from(1).to(6).units(1).turnsLeft(5))
    .t(new TB().id(4).player(1).from(1).to(6).units(1).turnsLeft(6))
    .t(new TB().id(5).player(1).from(1).to(6).units(1).turnsLeft(7))
    .t(new TB().id(6).player(1).from(1).to(6).units(1).turnsLeft(8))
    .t(new TB().id(7).player(1).from(1).to(6).units(1).turnsLeft(9))
    .build();
    
    AGSolution solution1 = new AGSolution("WAIT");
    Player.simulation.simulate(solution1);
    
    AGSolution solution2 = new AGSolution("MOVE");
    solution2.players.get(0).addAction(new MoveAction(GameState.factories[5], GameState.factories[0], 15), 0);
    Player.simulation.simulate(solution2);
    
    AGSolutionComparator.compare(solution1, solution2);
    assertThat(solution2.energy > solution1.energy, is(true));
  }
}
