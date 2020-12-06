package calm.aifsm;

import calm.Desert;
import calm.P;
import calm.Player;
import calm.State;
import calm.ai.Order;
import calm.desertmaker.DesertMakerAStar;

public class IngredientsGrabber extends Executor {
  
  private Desert desert;

  public IngredientsGrabber(State state, Desert desert) {
    super(state);
    this.desert = desert;
  }

  @Override
  public int eta() {
    return 1; // TODO calculate ETA !
  }
  
  @Override
  public Order execute() {
    if (!me.hands.isEmpty() && (me.hands.mask & ~desert.item.mask) != 0) {
      return me.getRidOff();
    }

    
    
    P nextAction = new DesertMakerAStar(state).find(desert);

    if (nextAction != null) {
      System.err.println("Using A* to go to "+nextAction);
      if (Player.map.cells[nextAction.offset] == 0) {
        return Order.move(nextAction);
      } else {
        return Order.use(nextAction);
      }
    } else {
      System.err.println("no a* result");
      return null;
    }
  }
}
