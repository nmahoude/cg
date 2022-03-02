package botg.ai;

import java.util.Arrays;

import botg.ai.handlers.AttackNearestUnitHandler;
import botg.ai.handlers.BuyStuffHandler;
import botg.ai.handlers.DoLastHitHandler;
import botg.ai.handlers.GrootHandler;
import botg.ai.handlers.MoveBackHandler;
import botg.ai.handlers.PanicSellHandler;
import botg.ai.handlers.RangedHeroAttackHandler;
import botg.ai.handlers.ironman.CastBurnHandler;
import botg.ai.handlers.ironman.CastFireballHandler;

public class IronmanStrategy extends Strategy {

  public IronmanStrategy() {
    handlers = Arrays.asList(
        new PanicSellHandler(),
        new GrootHandler(),
        new MoveBackHandler(),
        new DoLastHitHandler(),
        new BuyStuffHandler(),
        new CastFireballHandler(),
        new CastBurnHandler(),
        new RangedHeroAttackHandler(),
        new AttackNearestUnitHandler()
        );
  }
}
