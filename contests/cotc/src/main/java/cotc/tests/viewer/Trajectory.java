package cotc.tests.viewer;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;

/** hold the pod trajectory
 * 
 * @author nmahoude
 *
 */
public class Trajectory extends Group {
  List<Line> trajectory = new ArrayList<>();
  public Color color = Color.CYAN;
  
  public Point lastPoint = null;
  
  public void addPoint(Point p) {
    if (lastPoint != null) {
      Line line = new Line();
      line.setStroke(color);
      line.setStartX(lastPoint.x/Gui.ratio);
      line.setStartY(lastPoint.y/Gui.ratio);
      line.setEndX(p.x/Gui.ratio);
      line.setEndY(p.y/Gui.ratio);
      trajectory.add(line);
      this.getChildren().add(line);
    }
    lastPoint = p;
  }
}
