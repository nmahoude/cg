package direct_ai;

import lcm.Agent;
import lcm.cards.CardTriplet;

public interface Picker {

  int pick(Agent agent, CardTriplet triplet);

}