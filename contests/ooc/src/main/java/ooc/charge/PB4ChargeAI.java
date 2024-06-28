package ooc.charge;

import ooc.State;
import ooc.orders.Charge;

public class PB4ChargeAI implements ChargeAI {

	boolean firstMine = false;
	boolean secondMine = false;
	boolean firstTorpedo = false;
	boolean firstSonar = false;

	public void updateReal(State state) {
	  if (!firstMine  && state.cooldowns.mineCooldown() == 0) {
	    firstMine = true;
	  } else if (!secondMine  && state.cooldowns.mineCooldown() == 0) {
      secondMine = true;
    } else if (!firstTorpedo && state.cooldowns.torpedoCooldown() == 0) {
      firstTorpedo = true;
    } else if (!firstSonar && state.cooldowns.sonarCooldown() == 0) {
      firstSonar= true;
    } 
  }
	
	
	@Override
	public Charge calculateCharge(State state) {
		if (state.cooldowns.mineCooldown() > 0 && !firstMine) {
			return Charge.MINE;
		}
		if (state.cooldowns.mineCooldown() > 0 && !secondMine) {
			return Charge.MINE;
		}
//    if (state.cooldowns.sonarCooldown() > 0 && !firstSonar && Player.oracle.oppMapper.potentialPositions.size() > 1 && Player.oracle.oppMineCount > 2) {
//      return Charge.SONAR;
//    }
		if (state.cooldowns.torpedoCooldown() > 0 && !firstTorpedo) {
			return Charge.TORPEDO;
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
