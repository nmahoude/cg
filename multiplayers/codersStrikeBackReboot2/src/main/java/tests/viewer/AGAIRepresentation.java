package tests.viewer;

import csb.ai.ag.AGAI;
import csb.ai.ag.AGSolution;
import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;

public class AGAIRepresentation extends Group{
  Rectangle[] rectangles;
  Line[][] lines;

  public AGAIRepresentation() {
    rectangles = new Rectangle[AGAI.POPULATION];
    lines = new Line[rectangles.length][8];
  
    for (int i=0;i<rectangles.length;i++) {
      rectangles[i] = new Rectangle();
      rectangles[i].setWidth(1);
      rectangles[i].setHeight(1);
      rectangles[i].setFill(Color.BLACK);
      
      this.getChildren().add(rectangles[i]);
      for (int j=0;j<lines[0].length;j++) {
        lines[i][j] = new Line();
        lines[i][j].setStrokeWidth(1);
        //this.getChildren().add(lines[i][j]);
      }
    }
  }

  public void update(AGAI ag) {
    for (int i=0;i<rectangles.length;i++) {
      AGSolution solution = ag.population[i];
      if (solution != null) {
        rectangles[i].setX(solution.finalPosition0.x / Gui.ratio);
        rectangles[i].setY(solution.finalPosition0.y / Gui.ratio);
      }
    }
  }
}
