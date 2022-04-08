package cgfx.sample1.components;

import cgfx.components.CGFactory;
import cgfx.components.GameOptionPane;
import cgfx.components.GameViewer;
import cgfx.wrappers.EvaluationWrapper;
import cgfx.wrappers.GameWrapper;

public class Sample1Factory implements CGFactory {

  @Override
  public GameWrapper createGameWrapper() {
    return new Sample1GameWrapper();
  }

  @Override
  public GameViewer createGameViewer(GameWrapper wrapper) {
    return new Sample1GameViewer(wrapper);
  }

  @Override
  public GameOptionPane createGameOptionPane() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public EvaluationWrapper createEvaluationWrapper() {
    // TODO Auto-generated method stub
    return null;
  }

}
