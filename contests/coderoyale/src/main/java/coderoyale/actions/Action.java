package coderoyale.actions;

import coderoyale.units.Queen;

public abstract class Action {
  boolean isFinished;

  public abstract void doAction(Queen me);

  public boolean isFinished() {
    return isFinished;
  }

  public void finish() {
    isFinished = true;
  }
  public void success() {
    isFinished = true;
  }
  public void failure() {
    isFinished = true;
  }
}
