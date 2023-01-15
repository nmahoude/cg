package acoiaf._fx;

import cgfx.components.GameOptionPane;

public class AOICGameOptionPane extends GameOptionPane {

	public static final String MY_FLOODFILL = "MY_FLOODFILL";
	public static final String OPP_FLOODFILL = "OPP_FLOODFILL";
	public static final String COMBINED_FLOODFILL = "COMBINED_FLOODFILL";
	public static final String DRAW_ACTIONS = "DRAW_ACTIONS";
	
	public AOICGameOptionPane() {

	
		this.addCheckBox(DRAW_ACTIONS, "draw actions", true);
		this.addCheckBox(MY_FLOODFILL, "my flood fill", false);
		this.addCheckBox(OPP_FLOODFILL, "opp flood fill", false);
		this.addCheckBox(COMBINED_FLOODFILL, "combined flood fill", false);
	}
}
