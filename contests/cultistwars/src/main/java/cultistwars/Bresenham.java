package cultistwars;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * form a starting point to a target point, give all point between following bresenham algorithm 
 * 
 * https://en.wikipedia.org/wiki/Bresenham%27s_line_algorithm
 * 
 * @author nmahoude
 *
 */
public class Bresenham {
	private static int[][] grid;

	private static List<Pos>[] lines = new List[13*7 * 13*7];
	
	public static List<Pos> line(Pos start, Pos target) {
		
		return lines[start.offset + target.offset * 13 * 7];
	}
	
	
	public static void initialize(int[][] grid) {
		
		for (int i=0;i<lines.length;i++) {
			lines[i] = Collections.emptyList();
		}
		
		Bresenham.grid = grid;
		
		for (int sy=0;sy<7;sy++) {
			for (int sx=0;sx<13;sx++) {
				Pos start = Pos.get(sx, sy);
				
				for (int ty=0;ty<7;ty++) {
					for (int tx=0;tx<13;tx++) {
						Pos target = Pos.get(tx, ty);
						
						if (target.manhattan(start)> 6) continue;
						
						if (start == Pos.get(9,1) && target == Pos.get(9,0)) {
							System.err.println("here");
						}
						
						List<Pos> result;
						if (start.y < target.y) {
							result = bresenhamForward(start, target);
						} else {
							result = bresenhamBackward(start, target);
						}
						
						lines[start.offset + target.offset * 13 * 7] = result;
						
					}
				}
			}
		}
	}
	
	private static List<Pos> bresenhamForward(Pos startTile, Pos targetTile) {
    int x0, y0, x1, y1;
    x0 = startTile.x;
    y0 = startTile.y;
    x1 = targetTile.x;
    y1 = targetTile.y;

    List<Pos> line = new ArrayList<>();

    int dx = Math.abs(x1 - x0);
    int dy = Math.abs(y1 - y0);

    int sx = x0 < x1 ? 1 : -1;
    int sy = y0 < y1 ? 1 : -1;

    int err = dx - dy;
    int e2;
    int currentX = x0;
    int currentY = y0;

    while (true) {
        e2 = 2 * err;
        if (e2 > -1 * dy) {
            err -= dy;
            currentX += sx;
        }

        if (e2 < dx) {
            err += dx;
            currentY += sy;
        }

        if (currentX == x1 && currentY == y1) {
        	line.add(Pos.get(currentX, currentY));
        	break;
        }

        if (grid[currentX][currentY] == State.WALL) {
        	return Collections.emptyList();
        }
        line.add(Pos.get(currentX, currentY));
        
    }
    return line;
}

	private static List<Pos> bresenhamBackward(Pos startTile, Pos targetTile) {
    List<Pos> line = new ArrayList<>();
		int x0, y0, x1, y1;

		x0 = startTile.x;
		y0 = startTile.y;
    x1 = targetTile.x;
    y1 = targetTile.y;


    int dx = Math.abs(x1 - x0);
    int dy = Math.abs(y1 - y0);

    int sx = x0 < x1 ? 1 : -1;
    int sy = y0 < y1 ? 1 : -1;

    int err = dx - dy;
    int e2;
    int currentX = x0;
    int currentY = y0;

    while (true) {
        e2 = 2 * err;
        if (e2 > -1 * dy) {
            err -= dy;
            currentX += sx;
        }

        if (e2 < dx) {
            err += dx;
            currentY += sy;
        }

        if (currentX == x1 && currentY == y1) {
          line.add(0, Pos.get(currentX, currentY));
        	break;
        }

        if (grid[currentX][currentY] == State.WALL) {
        	return Collections.emptyList();
        }
        line.add(0, Pos.get(currentX, currentY));
        
    }

    return line;
}

	
	
}
