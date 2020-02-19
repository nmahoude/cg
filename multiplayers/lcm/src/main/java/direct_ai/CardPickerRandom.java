package direct_ai;

import lcm.Agent;
import lcm.PlayerOld;
import lcm.cards.CardTriplet;

public class CardPickerRandom implements Picker {
  public int pick(Agent agent, CardTriplet triplet) {
    return PlayerOld.random.nextInt(3);
  }
}
