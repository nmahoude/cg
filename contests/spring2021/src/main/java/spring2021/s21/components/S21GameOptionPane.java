package spring2021.s21.components;

import cgfx.components.GameOptionPane;
import javafx.scene.control.CheckBox;

/**
 * Create an optionPanel and apply to gameViewer
 * @author TH5893
 *
 */
public class S21GameOptionPane extends GameOptionPane {
	public final CheckBox displayIndexes;
	public final CheckBox drawShadows;
	public final CheckBox drawSunMap;
	public final CheckBox drawForbidenCells;
	
	
	public S21GameOptionPane() {
		displayIndexes = new CheckBox("Display indexes");
		displayIndexes.setSelected(true);
		displayIndexes.selectedProperty().addListener(event -> {
			observable.notifyObservers();
			
		});

		drawShadows= new CheckBox("Draw shadows");
		drawShadows.setSelected(true);
		drawShadows.selectedProperty().addListener(event -> {
			observable.notifyObservers();
		});
		drawSunMap = new CheckBox("Draw SunMap");
		drawSunMap.setSelected(false);
		drawSunMap.selectedProperty().addListener(event -> {
			observable.notifyObservers();
		});

		drawForbidenCells = new CheckBox("Draw Forbiden Cells");
		drawForbidenCells.setSelected(false);
		drawForbidenCells.selectedProperty().addListener(event -> {
			observable.notifyObservers();
		});

		
		this.getChildren().add(displayIndexes);
		this.getChildren().add(drawShadows);
		this.getChildren().add(drawSunMap);
		this.getChildren().add(drawForbidenCells);
	}
	
}
