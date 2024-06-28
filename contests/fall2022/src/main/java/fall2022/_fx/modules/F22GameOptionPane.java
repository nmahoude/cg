package fall2022._fx.modules;

import cgfx.components.GameOptionPane;
import fall2022.TimeTraveler;

public class F22GameOptionPane extends GameOptionPane {
	
	public static final String ENNEMY_HEATMAP = "ENNEMY_HEATMAP";
	public static final String MY_HEATMAP = "MY_HEATMAP";
	
	public static final String MY_SPAWN_MAP = "MY_SPAWN_HEATMAP";
	public static final String MY_SPREADER_MAP = "MY_SPREADER_HEATMAP";
	public static final String MY_SPREADER_SPAWN_MAP = "MY_SPREADER_SPAWN_HEATMAP";
	
	
	public static final String BFS = "bfs";
	public static final String SCRAPS = "scraps";
	public static final String ACTIONS = "actions";
	public static final String UNITS = "units";
	public static final String OWNERSHIP = "ownership";
	public static final String ILOTS = "ilots";
	public static final String ILOTS_10 = "ilots10";
	public static final String VORONOI = "voronoi";
	public static final String VORONOI_P = "voronoi_p";
	public static final String POS_AT_10 = "posAt10";
	public static final String REACHABLE_POSITIONS= "reachable_pos_opp";
	public static final String TERRITORY = "territory";
	public static final String DIFF_HEATMAP = "heatmap_diff";
	public static final String LOCK_MAP = "lock_map";
	public static final String RECYCLER_MAP = "recycler_map";
	public static final String MRU_SCORES_MAP = "mru_scores";
	
	public F22GameOptionPane() {
		addCheckBox(OWNERSHIP,  "Display Ownership", true);
		addCheckBox(UNITS, "Draw units", true);
		addCheckBox(SCRAPS, "Draw scraps", true);

		addCheckBox(ACTIONS,  "Display actions", true);

		addCheckBox(RECYCLER_MAP, "recyclers map (debug)", false);
		addCheckBox(MRU_SCORES_MAP, "mru scores (debug)", false);
		
		//addCheckBox(BFS, "Draw BFS", false);
		// addCheckBox(REACHABLE_POSITIONS, "Draw Reachable opp positions", false);
		addCheckBox(DIFF_HEATMAP, "Draw Diff heatmap", false);
		addCheckBox(ENNEMY_HEATMAP, "Draw Opp heatmap", false);
		addCheckBox(MY_HEATMAP, "Draw My heatmap", false);
		addCheckBox(MY_SPAWN_MAP, "Draw My spawn diffusemap", false);
		addCheckBox(MY_SPREADER_MAP, "Draw My spreadermap", false);
		addCheckBox(MY_SPREADER_SPAWN_MAP, "Draw My spawn spreadermap", false);
		// addCheckBox(ILOTS,  "Display Ilots", false);
		// addCheckBox(ILOTS_10,  "Display Ilots in 10", false);
		// addCheckBox(VORONOI,  "Display Voronoi (by units)", false);
		// addCheckBox(VORONOI_P,  "Display Voronoi (by players)", false);
		addCheckBox(TERRITORY,  "Display Territory",true);
		addCheckBox(LOCK_MAP,  "Display LockMap",false);
		
		addCheckBox(POS_AT_10,  "Travel in time (debug)", false);
		addSlider("POS_AT", 0, TimeTraveler.MAX_DEPTH-1);
		
	}
	
}

