package utg2019.world;

import java.util.ArrayList;
import java.util.List;

import trigonometryInt.Point;

public class MapCell {
  public final Point pos;
  
  public final List<MapCell> neighbors = new ArrayList<>();
  public final List<MapCell> neighborsAndSelf = new ArrayList<>();

  public final List<MapCell> neighborsRadius4 = new ArrayList<>();

  public MapCell(int x, int y) {
    this.pos = Point.get(x,y);
  }

  /** init the Graph of cells */
  public static void init(int width, int height, MapCell[] mapCells) {
    // favorisé les plus petits x dans les recherches !
    for (int x=0;x<width;x++) {
      for (int y=0;y<height;y++) {
        Point pos = Point.get(x, y);
        MapCell current = mapCells[pos.offset];
        current.neighborsAndSelf.add(current);
        if (pos.x-1 >= 0) current.addNeighbor(mapCells[pos.offset-1]);
        if (pos.x+1 < width) current.addNeighbor(mapCells[pos.offset+1]);
        if (pos.y-1 >= 0) current.addNeighbor(mapCells[pos.offset-width]);
        if (pos.y+1 < height) current.addNeighbor(mapCells[pos.offset+width]);

        current.fillRadius(pos, 4, mapCells);
      }
    }
  }

  private void fillRadius(Point center, int radius, MapCell[] mapCells) {
    for (int dy = -radius; dy <= radius; dy++) {
      for (int dx = -radius; dx <= radius; dx++) {
        Point potNeighbor = Point.getSecured(center.x+dx, center.y+dy);
        if (potNeighbor != Point.Invalid) {
          int distance = center.distance(potNeighbor);
          if (distance > radius) continue;
          MapCell current = mapCells[potNeighbor.offset];
          this.neighborsRadius4.add(current);
        }
      }
    }
  }

  private void addNeighbor(MapCell cell) {
    neighbors.add(cell);
    neighborsAndSelf.add(cell);
  }
}
