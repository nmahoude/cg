package calm.astar;

import calm.Desert;
import calm.Item;
import calm.P;
import calm.Player;
import calm.State;
import calm.ai.Order;

public class StarAI {

  private State state;

  public Order think(State state) {
    this.state = state;

    Desert desert = state.deserts[0]; // TODO choose wisely
    System.err.println("Desert is "+desert.item);
    
    // easy path, bring finished desert to bell
    if (state.me.hands.mask == desert.item.mask) {
      return Order.use(Player.map.bell);
    }
    
    Item ingredient = new Item(P.INVALID);
    ingredient.mask = desert.item.mask;
//    for (int i=0b1;i<ItemMask.LAST;i=i<<2) {
//      if ((state.me.hands.mask & i) != 0) continue;
//      else {
//        if (!state.me.hands.hasPlate() && state.me.hands.mask != 0) {
//          ingredient.mask = state.me.hands.mask | ItemMask.DISH;
//        } else {
//          ingredient.mask = state.me.hands.mask | i;
//        }
//        break;
//      }
//    }
    System.err.println("Next Goal is "+ingredient);
    
    AStar astar = new AStar();
    AStarNode lastNode = astar.find(state, ingredient);
    
    
    AStarNode firstNode = lastNode.first();
    Action action = firstNode.getAction();
    
    System.err.println("Need to "+action+" ("+firstNode.getActionPtr()+")");
    
    Order order = Order.WAIT;
    switch(action) {
      case USE_SW:
        order = Order.use(Player.map.strawberries);
        break;
      case USE_CHOPPER:
        order = Order.use(Player.map.chopper);
        break;
      case USE_DISH:
        order = Order.use(Player.map.dishwasher);
        break;
      case USE_DOUGH:
        order = Order.use(Player.map.dough);
        break;
      case USE_BB:
        order = Order.use(Player.map.blueberries);
        break;
      case USE_IC:
        order = Order.use(Player.map.icecream);
        break;
      case USE_OVEN_IN:
        order = Order.use(Player.map.ovenAsEquipment);
        break;
      case USE_OVEN_OUT:
        order = Order.use(Player.map.ovenAsEquipment);
        break;
        
    }
    
    return order;
  }
}
