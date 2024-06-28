package ooc.charge;

import ooc.State;
import ooc.orders.Charge;

public interface ChargeAI {

  public Charge calculateCharge(State state);

}
