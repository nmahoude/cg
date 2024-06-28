package _fx;

import _fx.modules.S23GameOptionPane;
import _fx.modules.S23GameViewer;
import _fx.modules.S23GameWrapper;
import cgfx.components.CGFactory;
import cgfx.components.GameOptionPane;
import cgfx.components.GameViewer;
import cgfx.wrappers.EvaluationWrapper;
import cgfx.wrappers.GameWrapper;

public class S23Factory implements CGFactory {

  @Override
  public GameWrapper createGameWrapper() {
    return new S23GameWrapper();
  }

  @Override
  public GameViewer createGameViewer(GameWrapper wrapper) {
    return new S23GameViewer((S23GameWrapper)createGameWrapper());
  }

  @Override
  public GameOptionPane createGameOptionPane() {
    return new S23GameOptionPane();
  }

  @Override
  public EvaluationWrapper createEvaluationWrapper() {
    return null;
  }

}
