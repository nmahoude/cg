package org.nmx.codingame.graphviewer;

import java.util.ArrayList;
import java.util.List;

public abstract class GameNode {
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
}