package cotc.ai;

import cotc.GameState;

public class DummySolution implements AISolution {
  GameState state;
  
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
