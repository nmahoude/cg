package org.nmx.codingame.graphviewer;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import org.nmx.codingame.graphviewer.placementstrategy.DynamicTreePlacementStrategy;
import org.nmx.codingame.graphviewer.placementstrategy.PlacementStrategy;

import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;

public class GfxNode extends Group {
	
	private static PlacementStrategy strategy = new DynamicTreePlacementStrategy();
	static Consumer<GameNode> selectNodeCallback = gn -> {};

  /**
	 * 
	 */
	private final GraphPane codingameView;
	public GfxNode parent;
  List<GfxNode> gfxChildren = new ArrayList<>();
	public boolean replie = false;

	// back link to GameNode
	public GameNode node;
  

	// shape
  private Circle circle;
  public Line line;
  public int width;

  
  
  public GfxNode(GraphPane codingameView, GfxNode parent, GameNode gameNode) {
  	this.parent = parent;
    this.codingameView = codingameView;
		this.node = gameNode;
    this.codingameView.gfxNodes.add(this);
    
    this.codingameView.globalData.addValue(gameNode.score());
    
    for (GameNode child : node.getChildren()) {
      GfxNode c = new GfxNode(this.codingameView, this, child);
      this.getChildren().add(c);
      gfxChildren.add(c);
    }

    if (parent == null) {
    	addRootShape();
    }

    line = new Line();
    this.getChildren().add(line);

    circle = new Circle();
    circle.setOnMouseClicked(event -> {
      this.codingameView.infoPanel.fill(gameNode.getTipDisplay());
      
      Circle selectedNode = this.codingameView.selectedNode;
			selectedNode.setVisible(true);
      selectedNode.setRadius(node.getRadius(this.codingameView.globalData) + 5);
      selectedNode.setFill(Color.TRANSPARENT);
      selectedNode.setStroke(Color.web("#00FF00"));
      selectedNode.setStrokeWidth(2);
      if (!this.getChildren().contains(selectedNode)) {
        this.getChildren().add(0, selectedNode);
      }
    	
    	
      if (event.isControlDown()) {
        if (hasChildrenHidden())  {
          deplierNoeud(); 
        } else {
          replierNoeud();
        }
      }
      
      if (selectNodeCallback != null) {
        selectNodeCallback.accept(gameNode);
      }
    });
    this.getChildren().add(circle);
    
    
    this.codingameView.gfxNodes.add(this);
  }

  public List<GfxNode> children() {
    return gfxChildren;
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

  void updateRepresentation() {
    
    circle.setRadius(node.getRadius(this.codingameView.globalData));
    String nodeColor = node.getColor(this.codingameView.globalData);
    if (nodeColor != null) {
      circle.setFill(Color.web(nodeColor));
    } else {
      if (node.score() >= this.codingameView.globalData.scoreThreshold()) {
        circle.setFill(this.codingameView.colorFor(node.score()));
      } else {
        circle.setFill(GraphPane.DISABLE_PERCENTILE);
      }
    }
  }

  boolean updateDetails(boolean oneParentReplie) {
    updateRepresentation();
    
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
    	this.codingameView.rootNode.redispose();

    	GfxNode.strategy.place(this.codingameView.rootNode, this.codingameView.globalData);
    }
  }

  public void deplierNoeud() {
    deplierNoeud(false);
  }

  public void deplierNoeud(boolean differRedispose) {
    replie = false;
    if (!differRedispose) {
      this.codingameView.rootNode.redispose();
      GfxNode.strategy.place(this.codingameView.rootNode, this.codingameView.globalData);
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
      this.codingameView.rootNode.redispose();
      GfxNode.strategy.place(this.codingameView.rootNode, this.codingameView.globalData);
    }
	}

  public static void redraw(GfxNode rootNode, GlobalData globalData) {
    GfxNode.strategy.place(rootNode, globalData);
  }

}