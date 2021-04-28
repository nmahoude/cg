package org.nmx.codingame.graphviewer;

import java.util.ArrayList;
import java.util.List;

public abstract class GameNode {
  private static final int DEFAULT_RADIUS = 16;
  protected GameNode parent;
  protected List<GameNode> children = new ArrayList<>();

  public GameNode() {
  }

  public List<GameNode> getChildren() {
    return children;
  }
  
	public void addChild(GameNode child) {
		children.add(child);
		child.parent = this;
	}

  public abstract List<String> getTipDisplay();
  public abstract double score();

  public int getRadius(GlobalData data) {
    return DEFAULT_RADIUS;
  }

  public String getColor(GlobalData data) {
    return null;
  }

  public int getBoundingbox(GlobalData data) {
    return getRadius(data) * 2;
  }
}