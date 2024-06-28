package _fx.modules;

import cgfx.components.GameOptionPane;

public class S23GameOptionPane extends GameOptionPane {
	
	public static final String DRAW_CELL_INDEX = "DRAW_CELL_INDEX";
	public static final String DRAW_OPP_ANTS = "DRAW_OPP_ANTS";
  public static final String DRAW_REACHABLE = "DRAW_REACHABLE";

  public S23GameOptionPane() {
	  addCheckBox(DRAW_CELL_INDEX, "Draw cell index", true);
	  addCheckBox(DRAW_OPP_ANTS, "Draw opp ants", true);
	  addCheckBox(DRAW_REACHABLE, "Draw reachable", true);
	}
	
}

