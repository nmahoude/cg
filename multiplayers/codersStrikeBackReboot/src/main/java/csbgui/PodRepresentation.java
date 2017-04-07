package csbgui;

import csb.entities.Pod;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import trigonometry.Point;

public class PodRepresentation {
  Circle podCircle;
  Line target;
  Line speed;
  Line direction;
  public void build(Pane root, Pod pod) {
    Circle circle = new Circle();
    circle.setRadius(pod.radius / Gui.ratio);
    circle.setFill(Color.TRANSPARENT);
    if (pod.id<2) {
      circle.setStroke(Color.ORANGE);
    } else {
      circle.setStroke(Color.PINK);
    }
    podCircle = circle;

    Line line = new Line();
    line.setFill(Color.ORANGE);
    line.setStroke(Color.ORANGE);
    target = line;
    
    Line speedLine = new Line();
    speedLine.setStroke(Color.DARKVIOLET);
    speed = speedLine;

    Line directionLine = new Line();
    directionLine.setStroke(Color.RED);
    direction = directionLine;

    root.getChildren().add(circle);
    root.getChildren().add(line);
    root.getChildren().add(speedLine);
    root.getChildren().add(directionLine);
  }
  
  public void update(Pod pod, Point targetPoint) {
    this.podCircle.setCenterX(pod.position.x / Gui.ratio);
    this.podCircle.setCenterY(pod.position.y / Gui.ratio);

    this.target.setStartX(pod.position.x / Gui.ratio);
    this.target.setStartY(pod.position.y / Gui.ratio);
    this.target.setEndX(targetPoint.x / Gui.ratio);
    this.target.setEndY(targetPoint.y / Gui.ratio);

    this.speed.setStartX(pod.position.x / Gui.ratio);
    this.speed.setStartY(pod.position.y / Gui.ratio);
    this.speed.setEndX((pod.position.x + pod.speed.vx) / Gui.ratio);
    this.speed.setEndY((pod.position.y + pod.speed.vy) / Gui.ratio);
    
    this.direction.setStartX(pod.position.x / Gui.ratio);
    this.direction.setStartY(pod.position.y / Gui.ratio);
    this.direction.setEndX((pod.position.x + pod.radius*pod.direction.vx) / Gui.ratio);
    this.direction.setEndY((pod.position.y + pod.radius*pod.direction.vy) / Gui.ratio);
  }
}
