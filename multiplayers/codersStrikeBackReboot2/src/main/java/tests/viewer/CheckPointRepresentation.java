package tests.viewer;

import csb.entities.CheckPoint;
import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;

public class CheckPointRepresentation extends Group {

  private Circle circle;

  public CheckPointRepresentation(CheckPoint cp) {
    Text text = new Text(cp.x / Gui.ratio, cp.y / Gui.ratio, ""+cp.id);
    text.setFill(Color.BLUE);
    this.getChildren().add(text);
    
    circle = new Circle();
    circle.setCenterX(cp.x / Gui.ratio);
    circle.setCenterY(cp.y / Gui.ratio);
    circle.setRadius(cp.radius / Gui.ratio);
    circle.setFill(Color.TRANSPARENT);
    circle.setStroke(Color.BLUE);
    this.getChildren().add(circle);
  }

}
