package fall2023._fx.modules;

import cgfx.components.GameOptionPane;

public class F23GameOptionPane extends GameOptionPane {
	
	public static final String DRAW_MY_RANGES = "DRAW_MY_RANGES";
	public static final String DRAW_MY_2000_RANGES = "DRAW_MY_2000_RANGES";
	public static final String DRAW_OPP_RANGES = "DRAW_OPP_RANGES";
  public static final String DRAW_OLD_TRIANGULATION = "DRAW_OLD_TRIANGULATION";
  public static final String DRAW_BEST_TRIANGULATION = "DRAW_BEST_TRIANGULATION";
  public static final String DRAW_ALL_TRIANGULATION = "DRAW_ALL_TRIANGULATION";
  public static final String DRAW_LAST_DRONE_POS = "DRAW_LAST_DRONE_POS";
  public static final String DRAW_TRAJECTORY_DRONE_POS = "DRAW_TRAJECTORY";
  public static final String DRAW_FICTIVE_POS = "DRAW_FICTIVE_POS";
  public static final String DRAW_REAL_POS = "DRAW_REAL_POS";

  public F23GameOptionPane() {
	  addCheckBox(DRAW_MY_RANGES, "draw MyRanges", true);
	  addCheckBox(DRAW_MY_2000_RANGES, "draw 2K Range", false);
	  addCheckBox(DRAW_OPP_RANGES, "draw oppRanges", false);
	  addCheckBox(DRAW_OLD_TRIANGULATION, "draw old triangulation", false);
	  addCheckBox(DRAW_BEST_TRIANGULATION, "draw best triangulation", false);
    addCheckBox(DRAW_ALL_TRIANGULATION, "draw all triangulation", false);
    addCheckBox(DRAW_LAST_DRONE_POS, "draw last drones pos", false);
    addCheckBox(DRAW_TRAJECTORY_DRONE_POS, "draw trajectory", true);
    addCheckBox(DRAW_FICTIVE_POS, "draw fictive pos", true);
    addCheckBox(DRAW_REAL_POS, "draw real pos", true);
	}
	
}

