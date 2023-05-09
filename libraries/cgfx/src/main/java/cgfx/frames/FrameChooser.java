package cgfx.frames;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

public class FrameChooser extends HBox {
  
  private int min, max;
  private int currentFrame;
  
  private Button first, next, prev, last;
  private Label turnLabel;
  private Set<Consumer<Integer>> viewers = new HashSet<>();
  
  public void addViewer(Consumer<Integer> c) {
    viewers.add(c);
  }
  
  public FrameChooser(int min, int max) {
    this.min = min;
    this.max = max;
    
    first = new Button("<<");
    first.setOnAction(value -> setFrame(0));

    prev = new Button("<");
    prev.setOnAction(value -> setFrame(currentFrame-1));

    next = new Button(">");
    next.setOnAction(value -> setFrame(currentFrame+1));

    last = new Button(">>");
    last.setOnAction( value -> setFrame(max));
    
    
    turnLabel = new Label();
    
    this.getChildren().add(first);
    this.getChildren().add(prev);
    this.getChildren().add(turnLabel);
    this.getChildren().add(next);
    this.getChildren().add(last);
    

    setFrame(min);
  }

  private void setFrame(int frameIndex) {
    if (frameIndex < 0) {
      this.currentFrame = 0;
    } else if (frameIndex > max) {
      this.currentFrame = max;
    } else {
      this.currentFrame = frameIndex;
    }
    
    turnLabel.setText(String.format("%3s", currentFrame));
    first.setDisable(currentFrame == 0);
    prev.setDisable(currentFrame == 0);
    next.setDisable(currentFrame == max);
    last.setDisable(currentFrame == max);
    
    
    viewers.forEach(v -> v.accept(currentFrame));
  }
  
}
