package org.nmx.codingame.graphviewer;

import java.util.ArrayList;
import java.util.List;

import javafx.scene.Group;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;

class GfxNode extends Group {
  /**
	 * 
	 */
	private final CodingameView codingameView;
	private GameNode node;
  private boolean childrenHidden = false;

  private Circle circle;
  Line line;
  int width;

  List<GfxNode> gfxChildren = new ArrayList<>();
  
  public GfxNode(CodingameView codingameView, GameNode gameNode) {
    this.codingameView = codingameView;
		this.node = gameNode;
    this.codingameView.gfxNodes.add(this);
    
    if (gameNode.score() < this.codingameView.minValue) { this.codingameView.minValue = gameNode.score(); }
    if (gameNode.score() > this.codingameView.maxValue) { this.codingameView.maxValue = gameNode.score(); }
    
    for (GameNode child : node.getChildren()) {
      GfxNode c = new GfxNode(this.codingameView, child);
      this.getChildren().add(c);
      gfxChildren.add(c);
    }
    //setTranslateX(random.nextInt(7*32));
    setTranslateY(64);

    circle = new Circle();
    circle.setOnMouseClicked(event -> {
      this.codingameView.infoPanel.fill(gameNode.getTipDisplay());
      
      if (event.isControlDown()) {
        if (hasChildrenHidden())  {
          showChildren(); 
        } else {
          hideChildren();
        }
      }
    });
    
    this.getChildren().add(circle);
    this.codingameView.gfxNodes.add(this);

    line = new Line();
    this.getChildren().add(line);

  }

  public void updateWidth() {
    if (gfxChildren.isEmpty() || childrenHidden) {
      this.width = 32;
    } else {
      this.width = 0;
      for (GfxNode child : gfxChildren) {
        child.updateWidth();
        this.width+= child.width + 2;
      }
    }
  }
  
  boolean updateDetails() {
    circle.setRadius(16);
    if (node.score() >= this.codingameView.percentile * this.codingameView.maxValue) {
      circle.setFill(this.codingameView.colorFor(node.score()));
    } else {
      circle.setFill(CodingameView.DISABLE_PERCENTILE);
    }
    
    
    if (node.parent != null) {
      line.setStartX(-this.getTranslateX());
      line.setStartY(-this.getTranslateY());
      line.setEndX(0);
      line.setEndY(0);
    }
    
    boolean visible = !this.codingameView.hideNodeUnderThreshold || node.score() >= this.codingameView.percentile * this.codingameView.maxValue;
    for (GfxNode child : gfxChildren) {
      visible |= child.updateDetails();
    }
    
    if (!visible) {
      hide();
    } else {
      show();
    }
    
    return visible;
  }
  
  public void redispose() {

    int currentX = -width / 2;
    for (GfxNode child : gfxChildren) {
      child.setTranslateX(currentX + child.width / 2);
      currentX+=child.width+2;
      child.redispose();
    }
    updateDetails();
  }
  
  public boolean hasChildrenHidden() {
    return childrenHidden;
  }
  public void hideChildren() {
    childrenHidden = true;
    for (GfxNode node : this.gfxChildren) {
      node.hide();
    }
    this.codingameView.rootNode.updateWidth();
    this.codingameView.rootNode.redispose();
  }

  public void showChildren() {
    childrenHidden = false;
    for (GfxNode node : this.gfxChildren) {
      node.show();
    }
  }

  
  private void show() {
    setVisible(true);
  }

  private void hide() {
    setVisible(false);
  }

}