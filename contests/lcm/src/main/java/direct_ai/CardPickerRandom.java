package direct_ai;

import lcm.Agent;
import lcm.Player;
import lcm.cards.CardTriplet;

public class CardPickerRandom implements Picker {
  public int pick(Agent agent, CardTriplet triplet) {
    return Player.random.nextInt(3);
  }
}
