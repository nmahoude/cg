package spring2022._cgfx.components;

import cgfx.components.CGFactory;
import cgfx.components.GameOptionPane;
import cgfx.components.GameViewer;
import cgfx.wrappers.EvaluationWrapper;
import cgfx.wrappers.GameWrapper;

public class S22Factory implements CGFactory {

  @Override
  public GameWrapper createGameWrapper() {
    return new S22GameWrapper();
  }

  @Override
  public GameViewer createGameViewer(GameWrapper wrapper) {
    return new S22GameViewer(wrapper);
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
