package cgfx.components;

import java.util.Observable;

import javafx.scene.layout.VBox;

public abstract class GameOptionPane extends VBox{

  protected static class MyObservable extends Observable {
    @Override
    public void notifyObservers() {
      super.setChanged();
      super.notifyObservers();
    }

    @Override
      public void notifyObservers(Object arg) {
        super.setChanged();
        super.notifyObservers(arg);
      }
  }
  protected final MyObservable observable = new MyObservable();
  
  public void register(GameViewer observer) {
    observable.addObserver(observer);
  }

}
