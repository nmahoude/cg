package marslander3.cgfx.components;

import cgfx.components.CGFactory;
import cgfx.components.GameOptionPane;
import cgfx.components.GameViewer;
import cgfx.wrappers.EvaluationWrapper;
import cgfx.wrappers.GameWrapper;

public class ML3Factory implements CGFactory {

  @Override
  public GameWrapper createGameWrapper() {
    return new ML3GameWrapper();
  }

  @Override
  public GameViewer createGameViewer(GameWrapper wrapper) {
    return new ML3GameViewer(wrapper);
  }

  @Override
  public GameOptionPane createGameOptionPane() {
    return null;
  }

  @Override
  public EvaluationWrapper createEvaluationWrapper() {
    return null;
  }

}
