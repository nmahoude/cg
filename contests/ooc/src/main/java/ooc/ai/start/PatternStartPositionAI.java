package ooc.ai.start;

import java.util.ArrayList;
import java.util.List;

import ooc.OOCMap;
import ooc.P;
import ooc.State;

public class PatternStartPositionAI {
	
	
	
	public P start;

  public void outputStartingPos(State state) {
		List<P> positions;
		
		positions = countSquareOfLength(state.map, 7);
		if (positions.size() > 0) {
			chooseBest(positions, 7);
			return;
		}
		
		positions = countSquareOfLength(state.map, 5);
		if (positions.size() > 0) {
      chooseBest(positions, 5);
      return;
		}

		positions = countSquareOfLength(state.map, 3);
		if (positions.size() > 0) {
      chooseBest(positions, 3);
      return;
		}

		// last resort
    positions = countSquareOfLength(state.map, 1);
    if (positions.size() > 0) {
      chooseBest(positions, 1);
      return;
    }
	}

  private void chooseBest(List<P> positions, int size) {
    int decal = size/2;
    start = P.I;
    int bestDist = Integer.MAX_VALUE;
    for (P pos : positions) {
      P center = P.get(pos.x+decal, pos.y+decal);
      if (center.manhattan(P.get(7, 7)) < bestDist) {
        bestDist = pos.manhattan(P.get(7, 7));
        start = center;
      }
    }
    System.out.println(String.format("%d %d", start.x, start.y));
  }
  
	
	List<P> countSquareOfLength(OOCMap map, int l) {
		return countRectanglesOfLength(map, l, l);
	}
		
	List<P> countRectanglesOfLength(OOCMap map, int h, int l) {
		List<P> leftCorners = new ArrayList<>();
		
		for (int y=0;y<=OOCMap.S - h;y++) {
			for (int x=0;x<=OOCMap.S - l;x++) {
				boolean empty = true;
				for (int dy=0;empty && dy<h;dy++) {
					for (int dx=0;empty && dx<l;dx++) {
						if (map.isIsland(P.get(x+dx, y+dy))) {
							empty = false;
						}
					}
				}
				if (empty) {
					leftCorners.add(P.get(x, y));
				}
			}
		}
		return leftCorners;
	}
}
