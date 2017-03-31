package oldTests;

import org.junit.Test;

import gitc.GameState;
import gitc.ag.AGPool;
import gitc.situations.BB;
import gitc.situations.FB;
import gitc.situations.GameBuilder;
import gitc.situations.LB;
import gitc.situations.TB;

public class PlayerActionsTest {

  @Test
  public void player_should_not_send_2_bombs_on_same_factory() throws Exception {
    GameState state = new GameBuilder().l(new LB().f(0).t(1).d(2).b()).l(new LB().f(0).t(2).d(2).b()).l(new LB().f(0).t(3).d(4).b()).l(new LB().f(0).t(4).d(4).b()).l(new LB().f(0).t(5).d(6).b()).l(new LB().f(0).t(6).d(6).b()).l(new LB().f(0).t(7).d(2).b()).l(new LB().f(0).t(8).d(2).b()).l(new LB().f(0).t(9).d(1).b())
        .l(new LB().f(0).t(10).d(1).b()).l(new LB().f(0).t(11).d(8).b()).l(new LB().f(0).t(12).d(8).b()).l(new LB().f(1).t(2).d(6).b()).l(new LB().f(1).t(3).d(4).b()).l(new LB().f(1).t(4).d(7).b()).l(new LB().f(1).t(5).d(3).b()).l(new LB().f(1).t(6).d(10).b()).l(new LB().f(1).t(7).d(3).b()).l(new LB().f(1).t(8).d(4).b())
        .l(new LB().f(1).t(9).d(1).b()).l(new LB().f(1).t(10).d(5).b()).l(new LB().f(1).t(11).d(6).b()).l(new LB().f(1).t(12).d(10).b()).l(new LB().f(2).t(3).d(7).b()).l(new LB().f(2).t(4).d(4).b()).l(new LB().f(2).t(5).d(10).b()).l(new LB().f(2).t(6).d(3).b()).l(new LB().f(2).t(7).d(4).b()).l(new LB().f(2).t(8).d(3).b())
        .l(new LB().f(2).t(9).d(5).b()).l(new LB().f(2).t(10).d(1).b()).l(new LB().f(2).t(11).d(10).b()).l(new LB().f(2).t(12).d(6).b()).l(new LB().f(3).t(4).d(10).b()).l(new LB().f(3).t(5).d(2).b()).l(new LB().f(3).t(6).d(12).b()).l(new LB().f(3).t(7).d(2).b()).l(new LB().f(3).t(8).d(8).b()).l(new LB().f(3).t(9).d(2).b())
        .l(new LB().f(3).t(10).d(7).b()).l(new LB().f(3).t(11).d(2).b()).l(new LB().f(3).t(12).d(14).b()).l(new LB().f(4).t(5).d(12).b()).l(new LB().f(4).t(6).d(2).b()).l(new LB().f(4).t(7).d(8).b()).l(new LB().f(4).t(8).d(2).b()).l(new LB().f(4).t(9).d(7).b()).l(new LB().f(4).t(10).d(2).b()).l(new LB().f(4).t(11).d(14).b())
        .l(new LB().f(4).t(12).d(2).b()).l(new LB().f(5).t(6).d(14).b()).l(new LB().f(5).t(7).d(5).b()).l(new LB().f(5).t(8).d(9).b()).l(new LB().f(5).t(9).d(4).b()).l(new LB().f(5).t(10).d(9).b()).l(new LB().f(5).t(11).d(2).b()).l(new LB().f(5).t(12).d(15).b()).l(new LB().f(6).t(7).d(9).b()).l(new LB().f(6).t(8).d(5).b())
        .l(new LB().f(6).t(9).d(9).b()).l(new LB().f(6).t(10).d(4).b()).l(new LB().f(6).t(11).d(15).b()).l(new LB().f(6).t(12).d(2).b()).l(new LB().f(7).t(8).d(5).b()).l(new LB().f(7).t(9).d(1).b()).l(new LB().f(7).t(10).d(4).b()).l(new LB().f(7).t(11).d(5).b()).l(new LB().f(7).t(12).d(11).b()).l(new LB().f(8).t(9).d(4).b())
        .l(new LB().f(8).t(10).d(1).b()).l(new LB().f(8).t(11).d(11).b()).l(new LB().f(8).t(12).d(5).b()).l(new LB().f(9).t(10).d(4).b()).l(new LB().f(9).t(11).d(5).b()).l(new LB().f(9).t(12).d(10).b()).l(new LB().f(10).t(11).d(10).b()).l(new LB().f(10).t(12).d(5).b()).l(new LB().f(11).t(12).d(17).b())
        .f(new FB().id(0).player(0).units(0).prod(0).disabled(0).build())
        .f(new FB().id(1).player(1).units(3).prod(3).disabled(0).build())
        .f(new FB().id(2).player(-1).units(20).prod(3).disabled(0).build())
        .f(new FB().id(3).player(0).units(0).prod(0).disabled(0).build())
        .f(new FB().id(4).player(0).units(0).prod(0).disabled(0).build())
        .f(new FB().id(5).player(0).units(1).prod(3).disabled(0).build())
        .f(new FB().id(6).player(0).units(1).prod(3).disabled(0).build())
        .f(new FB().id(7).player(0).units(5).prod(3).disabled(0).build())
        .f(new FB().id(8).player(0).units(5).prod(3).disabled(0).build())
        .f(new FB().id(9).player(0).units(0).prod(0).disabled(0).build())
        .f(new FB().id(10).player(0).units(0).prod(0).disabled(0).build())
        .f(new FB().id(11).player(0).units(0).prod(0).disabled(0).build())
        .f(new FB().id(12).player(0).units(0).prod(0).disabled(0).build())
        .t(new TB().id(0).player(1).from(1).to(8).units(27).turnsLeft(4))
        .t(new TB().id(1).player(1).from(1).to(5).units(2).turnsLeft(3))
        .t(new TB().id(2).player(-1).from(2).to(6).units(2).turnsLeft(3))
        .t(new TB().id(3).player(-1).from(2).to(8).units(6).turnsLeft(3))
        .t(new TB().id(4).player(-1).from(2).to(4).units(1).turnsLeft(4))
        .t(new TB().id(5).player(-1).from(2).to(10).units(1).turnsLeft(1))
        .t(new TB().id(6).player(-1).from(2).to(12).units(1).turnsLeft(6))
        .t(new TB().id(7).player(-1).from(2).to(0).units(1).turnsLeft(2))
        .withBomb(new BB().id(0).player(0).from(1).to(2).turnsLeft(5))
        .build();
    
      AGPool.getPossibleActions();
    }
}
