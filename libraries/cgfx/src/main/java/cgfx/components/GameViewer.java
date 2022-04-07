package cgfx.components;

import java.util.Observable;
import java.util.Observer;

import javafx.scene.Group;


public abstract class GameViewer extends Group implements Observer {

	protected abstract void updateView();

	@Override
	public void update(Observable o, Object arg) {
		this.updateView();
	}

  public abstract void setOptionsPane(GameOptionPane options);
}
