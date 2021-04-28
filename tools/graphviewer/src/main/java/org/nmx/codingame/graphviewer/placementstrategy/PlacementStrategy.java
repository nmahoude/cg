package org.nmx.codingame.graphviewer.placementstrategy;

import org.nmx.codingame.graphviewer.GfxNode;
import org.nmx.codingame.graphviewer.GlobalData;

public interface PlacementStrategy {

  void place(GfxNode root, GlobalData data);

}
