package cgfx.components;

import cgfx.wrappers.EvaluationWrapper;
import cgfx.wrappers.GameWrapper;

public interface CGFactory {

  GameWrapper createGameWrapper();

  GameViewer createGameViewer(GameWrapper wrapper);

  GameOptionPane createGameOptionPane();

  EvaluationWrapper createEvaluationWrapper();

}