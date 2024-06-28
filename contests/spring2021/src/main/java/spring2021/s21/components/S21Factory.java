package spring2021.s21.components;

import cgfx.components.CGFactory;
import cgfx.components.GameOptionPane;
import cgfx.components.GameViewer;
import cgfx.wrappers.EvaluationWrapper;
import cgfx.wrappers.GameWrapper;

public class S21Factory implements CGFactory {
  @Override
  public GameWrapper createGameWrapper() {
    return new S21GameWrapper();
  }

  @Override
  public GameViewer createGameViewer(GameWrapper wrapper) {
    return new S21GameViewer((S21GameWrapper)wrapper);
  }

  @Override
  public GameOptionPane createGameOptionPane() {
    return new S21GameOptionPane();
  }
  
  @Override
  public EvaluationWrapper createEvaluationWrapper() {
    return null;
  }
  
}
