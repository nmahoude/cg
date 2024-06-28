package utg2019.sim;

import java.util.HashSet;
import java.util.Set;

import org.assertj.core.api.Assertions;
import org.junit.Test;

import trigonometryInt.Point;

public class ActionTest {

  
  @Test
  public void okWithSets() throws Exception {
    Point.init(15, 30);
    Set<Action> actions = new HashSet<>();
    
    actions.add(Action.move(Point.get(3,5)));
    actions.add(Action.move(Point.get(3,5)));
    
    Assertions.assertThat(actions.size()).isEqualTo(1);
  }
}
