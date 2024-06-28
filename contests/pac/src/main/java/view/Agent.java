package view;

import java.util.function.Function;

import javafx.scene.Group;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;

public class Agent {
  String name;
  Rectangle[][] rects = new Rectangle[30][20];
  
  Function<int[], String> function;
  private Group root;
  
  
  Agent(String name, Group root) {
    this.root = root;
    this.name = name;
    
    buildCells();
  }
  
  public void setFunction(Function<int[], String> function) {
    this.function = function;
  }
  
  public void updateCells() {
    if (function == null) return;
    
    for (int y = 0; y < 20; y++) {
      for (int x = 0; x < 30; x++) {
        Rectangle rect = rects[x][y];

        String color = function.apply(new int[] { x, y });
        rect.setVisible(color != null);
        if (color != null) {
          rect.setFill(Paint.valueOf(color));
        }
      }
    }
  }
  
  private void buildCells() {
    int scale = 32;

    for (int y = 0; y < 20; y++) {
      for (int x = 0; x < 30; x++) {
        Rectangle rect = new Rectangle();
        rects[x][y] = rect;
        
        rect.setWidth(scale);
        rect.setHeight(scale);
        rect.setTranslateX(x * scale);
        rect.setTranslateY(y * scale);
        root.getChildren().add(rect);
      }
    }
  }
}
