package cgfx.components;

import java.util.HashMap;
import java.util.Map;
import java.util.Observable;

import javafx.scene.control.CheckBox;
import javafx.scene.layout.VBox;

public abstract class GameOptionPane extends VBox{

	private final Map<String, CheckBox> checkBoxes = new HashMap<>();
	
	public void addCheckBox(String name, String displayName, boolean defaultValue) {
		CheckBox cb = new CheckBox(displayName);
		cb.setSelected(defaultValue);
		cb.selectedProperty().addListener(event -> {
			observable.notifyObservers();
		});
		checkBoxes.put(name, cb);
		this.getChildren().add(cb);
	}
	
	public boolean isSelected(String name) {
		CheckBox cb = checkBoxes.get(name);
		if (cb == null) {
			// TODO log
			return false;
		} else {
			return cb.isSelected();
		}
	}

	
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
