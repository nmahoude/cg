package botg.ai;

import java.util.Arrays;

import botg.ai.handlers.AttackNearestUnitHandler;
import botg.ai.handlers.BuyStuffHandler;
import botg.ai.handlers.DoLastHitHandler;
import botg.ai.handlers.HandleGrootHandler;
import botg.ai.handlers.MoveBackHandler;
import botg.ai.handlers.RangedHeroAttackHandler;
import botg.ai.handlers.strange.CastHealHandler;
import botg.ai.handlers.strange.CastPullHandler;
import botg.ai.handlers.strange.CastShieldHandler;

public class StrangeStrategy extends Strategy {

  public StrangeStrategy() {
    handlers = Arrays.asList(
        new HandleGrootHandler(),
        new MoveBackHandler(),
        new DoLastHitHandler(),
        new BuyStuffHandler(),
        new CastHealHandler(),
        new CastShieldHandler(),
        new CastPullHandler(),
        new RangedHeroAttackHandler(),
        new AttackNearestUnitHandler()
        );
  }
}
