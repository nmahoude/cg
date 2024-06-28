package ooc.charge;

import ooc.State;
import ooc.orders.Charge;

public class AttackChargeAI implements ChargeAI {
  boolean hasChargedSilenceOneTime = false;
  
  public Charge calculateCharge(State state) {
    if (state.cooldowns.silenceCooldown() == 0) {
        hasChargedSilenceOneTime = true;
    }
    
    
    if (state.cooldowns.torpedoCooldown() != 0) {
      return Charge.TORPEDO;
    } else if (!hasChargedSilenceOneTime && state.cooldowns.silenceCooldown() != 0) {
      return Charge.SILENCE;
    } else if (state.cooldowns.sonarCooldown() != 0) {
      return Charge.SONAR;
    } else if (state.cooldowns.silenceCooldown() != 0) {
      return Charge.SILENCE;
    } else if (state.cooldowns.mineCooldown() != 0) {
      return Charge.MINE;
    } else {
      return Charge.TORPEDO;
    }
  }

}
