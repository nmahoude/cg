package cgfx.sample1.components;

import java.util.Collections;
import java.util.List;
import java.util.Scanner;

import cgfx.wrappers.GameWrapper;

public class Sample1GameWrapper extends GameWrapper {

  @Override
  protected void _copyFrom(GameWrapper gameWrapper) {
  }

  @Override
  protected void readGlobal(Scanner in) {
  }

  @Override
  protected void readTurn(Scanner in) {
  }

  @Override
  protected List<String> _calculateAIListOfActions() {
    return Collections.emptyList();
  }

  @Override
  protected void _think() {
  }

  @Override
  protected void _resetFromBase() {
  }

  @Override
  protected void _applyAction(String string) {
  }

}
