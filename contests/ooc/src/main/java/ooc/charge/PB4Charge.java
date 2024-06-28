package ooc.charge;

import ooc.State;
import ooc.orders.Charge;

public class PB4Charge implements ChargeAI {

	boolean firstMine = false;
	boolean secondMine = false;
	boolean firstTorpedo = false;
	boolean firstSonar = false;
	
	
	@Override
	public Charge calculateCharge(State state) {
		if (state.cooldowns.mineCooldown() > 0 && !firstMine) {
			if (state.cooldowns.mineCooldown() == 1) {
				firstMine = true;
			}
			return Charge.MINE;
		}
		if (state.cooldowns.mineCooldown() > 0 && !secondMine) {
			if (state.cooldowns.mineCooldown() == 1) {
				secondMine = true;
			}
			return Charge.MINE;
		}
		if (state.cooldowns.torpedoCooldown() > 0 && !firstTorpedo) {
			if (state.cooldowns.torpedoCooldown() == 1) {
				firstTorpedo = true;
			}
			return Charge.TORPEDO;
		}
		if (state.cooldowns.sonarCooldown() > 0 && !firstSonar) {
			if (state.cooldowns.sonarCooldown() == 1) {
				firstSonar = true;
			}
			return Charge.SONAR;
		}
		
		if (state.cooldowns.torpedoCooldown() != 0) {
      return Charge.TORPEDO;
    } else if (state.cooldowns.silenceCooldown() != 0) {
      return Charge.SILENCE;
    } else if (state.cooldowns.mineCooldown() != 0) {
    	return Charge.MINE;
    } else if (state.cooldowns.sonarCooldown() != 0) {
      return Charge.SONAR;
    } else {
      return Charge.TORPEDO;
    }
	}

}
