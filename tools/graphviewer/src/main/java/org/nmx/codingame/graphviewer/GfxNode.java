package org.nmx.codingame.graphviewer;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;

class GfxNode extends Group {
  /**
	 * 
	 */
	private final CodingameView codingameView;
	GfxNode parent;
  List<GfxNode> gfxChildren = new ArrayList<>();
	private boolean replie = false;

	// back link to GameNode
	private GameNode node;
  

	// shape
  private Circle circle;
  Line line;
  int width;

  
  
  public GfxNode(CodingameView codingameView, GfxNode parent, GameNode gameNode) {
  	this.parent = parent;
    this.codingameView = codingameView;
		this.node = gameNode;
    this.codingameView.gfxNodes.add(this);
    
    if (gameNode.score() < this.codingameView.minValue) { this.codingameView.minValue = gameNode.score(); }
    if (gameNode.score() > this.codingameView.maxValue) { this.codingameView.maxValue = gameNode.score(); }
    
    for (GameNode child : node.getChildren()) {
      GfxNode c = new GfxNode(this.codingameView, this, child);
      this.getChildren().add(c);
      gfxChildren.add(c);
    }
    //setTranslateX(random.nextInt(7*32));
    setTranslateY(64);

    if (parent == null) {
    	addRootShape();
    }

    line = new Line();
    this.getChildren().add(line);

    circle = new Circle();
    circle.setOnMouseClicked(event -> {
      this.codingameView.infoPanel.fill(gameNode.getTipDisplay());
      
      if (event.isControlDown()) {
        if (hasChildrenHidden())  {
          deplierNoeud(); 
        } else {
          replierNoeud();
        }
      }
    });
    this.getChildren().add(circle);
    
    
    this.codingameView.gfxNodes.add(this);
  }

  
  public void dfs(Consumer<GfxNode> function) {
  	function.accept(this);
  	for (GfxNode child : gfxChildren) {
  		child.dfs(function);
  	}
  }

  public GameNode node() {
  	return node;
  }
  
  private void addRootShape() {
  	Circle ring = new Circle();
  	ring.setRadius(20);
  	ring.setFill(Color.TRANSPARENT);
  	ring.setStroke(Color.AQUA);
  	ring.setStrokeWidth(2);
  	this.getChildren().add(ring);
	}

	public void updateWidth() {
    if (gfxChildren.isEmpty() || replie) {
      this.width = 32;
    } else {
      this.width = 0;
      for (GfxNode child : gfxChildren) {
        child.updateWidth();
        this.width+= child.width + 2;
      }
    }
  }
  
  boolean updateDetails(boolean oneParentReplie) {
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
    
    boolean visible = !oneParentReplie;
    for (GfxNode child : gfxChildren) {
      visible |= child.updateDetails(oneParentReplie | this.replie);
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
    updateDetails(parent == null ? false : parent.replie);
  }
  
  public boolean hasChildrenHidden() {
    return replie;
  }

  public void replierNoeud() {
  	replierNoeud(false);
  }
  public void replierNoeud(boolean differRedispose) {
    replie = true;
    if (!differRedispose) {
    	this.codingameView.rootNode.updateWidth();
    	this.codingameView.rootNode.redispose();
    }
  }

  public void deplierNoeud() {
  	deplierNoeud(false);
  }
  	public void deplierNoeud(boolean differRedispose) {
    replie = false;
    if (!differRedispose) {
	    this.codingameView.rootNode.updateWidth();
	    this.codingameView.rootNode.redispose();
    }
  }

  
  private void show() {
    setVisible(true);
  }

  private void hide() {
    setVisible(false);
  }


	public void deplierNoeudEtParent(boolean differRedispose) {
    replie = false;
    if (this.parent != null) this.parent.deplierNoeudEtParent(differRedispose);
    if (!differRedispose) {
	    this.codingameView.rootNode.updateWidth();
	    this.codingameView.rootNode.redispose();
    }
	}

}