package org.nmx.codingame.graphviewer.placementstrategy;

import org.nmx.codingame.graphviewer.GfxNode;
import org.nmx.codingame.graphviewer.GlobalData;

public class TreePlacementStrategy implements PlacementStrategy {

  private GlobalData data;


  public void place(GfxNode root, GlobalData data) {
    this.data = data;
    placeNode(root);
    updateEdges(root);
  }

  
  private void placeNode(GfxNode node) {
    
    int width;
    if (node.children().isEmpty()) {
      width = node.node.getBoundingbox(data) + 2 ;
    } else {
      width = 0;
      for (GfxNode child : node.children()) {
        placeNode(child);
        width += child.width + 2;
      }
    }
    node.width = width;
    
    
    int currentX = -node.width / 2;
    for (GfxNode child : node.children()) {
      child.setTranslateX(currentX + child.width / 2);
      child.setTranslateY(64);
      
      currentX+=child.width+2;
    }
  }

  private void updateEdges(GfxNode node) {
    
    for (GfxNode child : node.children()) {
      updateEdges(child);
      
      child.line.setStartX(-child.getTranslateX());
      child.line.setStartY(-child.getTranslateY());
      child.line.setEndX(0);
      child.line.setEndY(0);
      
    }
  }

}
