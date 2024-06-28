package fall2022;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Voronoi {

	public Map<Pos, List<Pos>> map = new HashMap<>();
	
	public void calculate(State state) {
		int visited[] = new int[Pos.MAX_OFFSET];
		map.clear();
		
		// get our units
		List<Pos> init = new ArrayList<>();
		for (int y = 0; y < State.HEIGHT; y++) {
			for (int x = 0; x < State.WIDTH; x++) {
				Pos c = Pos.from(x,y);
				if (state.u[c.o] > 0) {
					ArrayList<Pos> list = new ArrayList<>();
					list.add(c);
					map.put(c, list);
					visited[c.o] = 1;
				}
			}
		}
	
		boolean didExploreation = true;
		while (didExploreation) {
			didExploreation = false;
			for (Pos p : map.keySet()) {
				List<Pos> explore = map.get(p);
				
				List<Pos> newExplore = new ArrayList<>();
				for (Pos toex : explore) {
					for (Pos n : toex.neighbors4dirs)  {
						if (!state.canMove(n)) continue;
						if (visited[n.o] != 0) continue;
						
						visited[n.o] = 1;
						newExplore.add(n);
						didExploreation = true;
					}
				}
				explore.addAll(newExplore);
			}
		}
	}
}
