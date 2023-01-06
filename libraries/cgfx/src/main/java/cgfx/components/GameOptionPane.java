package cgfx.components;

import java.util.HashMap;
import java.util.Map;
import java.util.Observable;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Slider;
import javafx.scene.layout.VBox;

public abstract class GameOptionPane extends VBox{

	private final Map<String, CheckBox> checkBoxes = new HashMap<>();
	private Map<String , Slider> sliders = new HashMap<>();
	
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

	public int sliderValue(String name) {
		Slider slider = sliders.get(name);
		if( slider == null) {
			System.err.println("Slider inconnu : "+name);
			return 0;
		} else {
			return (int)slider.getValue();
		}
	}
	
	public Slider addSlider(String name, int minValue, int maxValue) {
		Slider posAtSlider = new Slider();
		posAtSlider.setMin(minValue);
		posAtSlider.setMax(maxValue);
		posAtSlider.setValue(minValue);
		posAtSlider.setShowTickLabels(true);
		posAtSlider.setShowTickMarks(true);
		posAtSlider.setMajorTickUnit(1);
		posAtSlider.setMinorTickCount(0);
		posAtSlider.setBlockIncrement(1);
	    
		posAtSlider.valueProperty().addListener(new ChangeListener<Number>() {
      public void changed(ObservableValue<? extends Number> ov, Number oldVal, Number newVal) {
  			observable.notifyObservers();
      	
      }
		});

		this.getChildren().add(posAtSlider);
		sliders.put(name, posAtSlider);
		return posAtSlider;
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
