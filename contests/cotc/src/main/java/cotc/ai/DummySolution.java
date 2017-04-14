package cotc.ai;

import cotc.GameState;
import trigonometry.Point;

public class DummySolution implements AISolution {
  GameState state;
  
  public Point target1;
  public int thrust1;
  public Point target2;
  public int thrust2;

  public DummySolution(GameState state) {
    this.state = state;
  }
  @Override
  public String[] output() {
    String[] output = new String[state.shipCount];
    for (int i=0;i<output.length;i++) {
      output[i] = "WAIT";
    }
    return output;
  }

}
