package ooc.charge;

import ooc.Cooldown;
import ooc.State;
import ooc.orders.Charge;

public class DangerChargeAI implements ChargeAI {
  
  public Charge calculateCharge(State state) {
    int order[]  = new int[] { Cooldown.SILENCE, Cooldown.TORPEDO, Cooldown.MINE, Cooldown.SONAR };
    
    for (int i=0;i<order.length;i++) {
      if (state.cooldowns.get(order[i]) == 0) continue;
      
      return Charge.fromIndex(order[i]);
    }
    return Charge.SILENCE;
  }
  
}
