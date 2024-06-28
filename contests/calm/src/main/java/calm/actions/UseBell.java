package calm.actions;

import calm.state.Agent;
import calm.state.Order;
import calm.state.State;
import calm.state.Table;

public class UseBell extends Action {

  public UseBell(Table table) {
    super(table);
  }

  @Override
  public boolean prerequisites(State state, Agent agent) {
    return true;
  }

  @Override
  protected void privateApplyEffect(State state, Agent agent) {
    for (Order order : state.orderList) {
      if (order.items == agent.items) {
        state.score += order.award;
      }
    }
  }

  @Override
  public String describe() {
    return "Use bell";
  }

}
