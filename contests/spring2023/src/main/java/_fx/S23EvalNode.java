package _fx;

import java.util.List;

import _fx.modules.S23GameWrapper;
import cgfx.components.EvalNode;
import spring2023.Fitnesse;

public class S23EvalNode implements EvalNode {

  double[] values = new double[2];
  
  public static List<String> names() {
    return List.of("scores", "my ants");
  }

  public S23EvalNode(S23GameWrapper wrapper) {
    values[0] = Fitnesse.Scores(wrapper.state);
    values[1] = Fitnesse.myAntsScore(wrapper.state);
  }
  
  @Override
  public double value(int index) {
    return values[index];
  }

}
