package utg2019;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import trigonometryInt.Point;
import utg2019.world.MapCell;
import utg2019.world.World;
import utg2019.world.entity.Robot;
import utg2019.world.maps.Oracle;

public class RadarOptimizer {
  private static final int MINIMUM_POTENTIAL_FOR_RADAR = 5;
  public int density[] = new int[World.MAX_OFFSET];

  static class RadarInfo {
    public final Point pos;
    double score;
    public RadarInfo(Point pos) {
      this.pos = pos;
      score = Double.NEGATIVE_INFINITY;
    }
  }
  
  static RadarInfo currentInfos[] = new RadarInfo[World.MAX_OFFSET];
  static {
    for (int i=0;i<World.MAX_OFFSET;i++) {
      currentInfos[i] = new RadarInfo(Point.getFromOffset(i));
    }
  }
  
  static List<Point> staticRadarLocations = new ArrayList<>();
  {
    staticRadarLocations.add(Point.get(5+1,3));
    staticRadarLocations.add(Point.get(5+1,11));
    staticRadarLocations.add(Point.get(9+1,7));
    staticRadarLocations.add(Point.get(13+1,3));
    staticRadarLocations.add(Point.get(13+1,11));
    staticRadarLocations.add(Point.get(17+1,7));
    staticRadarLocations.add(Point.get(21+1,3));
    staticRadarLocations.add(Point.get(21+1,11));
    staticRadarLocations.add(Point.get(25+1,7));

    // edges last ?
    staticRadarLocations.add(Point.get(28,1));
    staticRadarLocations.add(Point.get(28,13));
    staticRadarLocations.add(Point.get(10,0));
    staticRadarLocations.add(Point.get(10,14));
    staticRadarLocations.add(Point.get(18,0));
    staticRadarLocations.add(Point.get(18,14));
  }
  
  
  public void update(World current, int potentialOreMap[]) {
    updateDensityOfCellCoverage(current, potentialOreMap);
    
    
    for (int i=0;i<World.MAX_OFFSET;i++) {
      Point pos = Point.getFromOffset(i);
      
      double best = Double.POSITIVE_INFINITY;
      for (Point rpos : staticRadarLocations) {
        if (rpos.distance(pos) < best) {
          best = rpos.distance(pos);
        }
      }
      currentInfos[i].score = 1.0 / (best+1);
    }
    Arrays.sort(currentInfos, (a,b) -> Double.compare(b.score, a.score)); // sort by score (best first)
  }
  
  /*
   * Robot has a radar, find the best spot to put it ...
   * 
   * 1st implem : simple list of static locations
   */
  public Point findSpot(Robot robot, World world) {
    for (Point target : staticRadarLocations) {
      if (world.hasRadar(target) == false) {
        // dont put back radar if we know there is nothing here (case for destroyed radar)
        int potentialOre = getPotentialOreAt(world, target, Player.oracle.potentialOre);
        if (potentialOre > MINIMUM_POTENTIAL_FOR_RADAR) {
          return target;
        }
      }
    }
    return Point.Invalid;
  }

  /**
   * Test : the min of the proba * pos.x / count
   * @param robot
   * @param world
   * @return
   */
  public Point findSpot2(Robot robot, World current) {
    double bestSum = 0;
    Point best = Point.Invalid;
    
    for (int i=0;i<World.MAX_OFFSET;i++) {
      Point center = Point.getFromOffset(i);
      
      double score = scoreForPos(current, center);
      if (score > bestSum) {
        bestSum = score;
        best = center;
      }
    }

    return best;
  }

  double scoreForPos(World current, Point center) {
    int count = 0;
    double sum = 0;
    
    MapCell mcCenter = World.mapCells[center.offset];
    for (MapCell mc : mcCenter.neighborsRadius4) {
      Point pos = mc.pos;
      if (pos.x == 0) continue;
      if (current.isCurrentlyKnown(pos)) continue;
      if (Player.oracle.potentialOre[pos.offset] == 0) continue;
      
      count++;
      sum += 1.0 / pos.x;
      
    }
    double coeff = (center.x < 7 ? 0.2 : 1.0); 
    double score = coeff * count * sum;
    return score;
  }
  
  
  private int getPotentialOreAt(World current, Point center, int potentialOreMap[]) {
    int total = 0;
    
    int radius = 4;
    for (int dy = -radius; dy <= radius; dy++) {
      for (int dx = -radius; dx <= radius; dx++) {
        Point pos = Point.getSecured(center.x+dx, center.y+dy);
        if (pos != Point.Invalid) {
          if (pos.x == 0) continue; 
          int distance = center.distance(pos);
          if (distance > radius) continue;

          if (current.isCurrentlyKnown(pos)) {
            total += current.getOre(pos.offset);
          } else if (potentialOreMap[pos.offset] == 0) {
            total+=0;
          } else if (potentialOreMap[pos.offset] == -1) {
            total+=3;
          } else {
            total += potentialOreMap[pos.offset];
          }
        }
      }
    }
    
    return total;
  }

  /**
   * Update the density of unknown cells that may have ore if we placed the radar here
   * @param current
   */
  public void updateDensityOfCellCoverage(World current, int potentialOreMap[]) {
    for (int i=0;i<World.MAX_OFFSET;i++) {
      density[i] = 0; // reset density
      Point center = Point.getFromOffset(i);
      
      int radius = 4;
      for (int dy = -radius; dy <= radius; dy++) {
        for (int dx = -radius; dx <= radius; dx++) {
          Point pos = Point.getSecured(center.x+dx, center.y+dy);
          if (pos != Point.Invalid) {
            if (pos.x == 0) continue; 
            int distance = center.distance(pos);
            if (distance > radius) continue;

            if (current.isCurrentlyKnown(pos) || potentialOreMap[pos.offset] == 0) continue;
            density[center.offset] += 1; 
          }
        }
      }
    }
    
    if (Player.DEBUG_ORACLE_DENSITY_MAP) {
      Oracle.debugMap("density (1/8eme)", offset -> ""+(char)('0' + density[offset] / 8) );
    }
  }

  public double getRadarScore(World current, int potentialOreMap[], Point center) {
    double score = 0.0;
    
    int radius = 4;
    for (int dy = -radius; dy <= radius; dy++) {
      for (int dx = -radius; dx <= radius; dx++) {
        Point pos = Point.getSecured(center.x+dx, center.y+dy);
        if (pos != Point.Invalid) {
          if (pos.x == 0) continue; 
          int distance = center.distance(pos);
          if (distance > radius) continue;

          if (current.isCurrentlyKnown(pos) || potentialOreMap[pos.offset] == 0) continue;
          
          score+= Math.pow(ORE_PROBABILITY[pos.x][pos.y], 0.25) / pos.x;
        }
      }
    }
    
    return score;
  }
  
  public static double getOreFromProbability(Point p) {
    return 1.0 * ORE_PROBABILITY[p.x][p.y] / 100.0;
  }
  public static final double[][] ORE_PROBABILITY =
    {{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 }, 
  {0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0 }, 
  {0, 1, 1, 2, 2, 2, 2, 2, 2, 2, 2, 2, 1, 1, 0 }, 
  {1, 2, 3, 4, 5, 5, 5, 5, 5, 5, 5, 4, 3, 2, 1 }, 
  {1, 3, 6, 8, 9, 9, 9, 9, 9, 9, 9, 8, 6, 3, 1 }, 
  {2, 4, 10, 12, 14, 14, 14, 14, 14, 14, 14, 12, 9, 4, 2 }, 
  {2, 6, 12, 16, 18, 18, 18, 18, 18, 18, 18, 16, 12, 6, 2 }, 
  {3, 7, 15, 19, 22, 21, 22, 22, 22, 21, 22, 19, 15, 7, 3 }, 
  {3, 8, 18, 22, 25, 25, 25, 25, 25, 25, 25, 22, 18, 8, 3 }, 
  {4, 9, 20, 25, 28, 28, 28, 28, 28, 28, 28, 25, 20, 9, 4 }, 
  {4, 10, 22, 28, 31, 31, 31, 31, 31, 31, 31, 28, 22, 10, 4 }, 
  {5, 11, 24, 30, 34, 34, 34, 34, 34, 34, 34, 30, 24, 11, 5 }, 
  {5, 12, 26, 33, 37, 37, 37, 37, 37, 37, 37, 32, 26, 12, 5 }, 
  {5, 13, 28, 35, 40, 39, 39, 39, 39, 39, 40, 35, 28, 13, 5 }, 
  {6, 14, 30, 37, 42, 42, 42, 41, 41, 42, 42, 37, 30, 14, 6 }, 
  {6, 14, 32, 39, 44, 44, 44, 44, 44, 44, 44, 39, 32, 14, 6 }, 
  {6, 15, 33, 41, 47, 46, 46, 46, 46, 46, 47, 41, 33, 15, 6 }, 
  {7, 16, 35, 43, 49, 48, 48, 48, 48, 48, 49, 43, 35, 16, 7 }, 
  {7, 17, 36, 45, 51, 50, 50, 50, 50, 50, 51, 45, 36, 17, 7 }, 
  {7, 17, 38, 47, 53, 52, 52, 52, 52, 52, 53, 47, 38, 17, 7 }, 
  {8, 18, 39, 49, 55, 54, 54, 54, 54, 54, 55, 49, 39, 18, 8 }, 
  {8, 19, 41, 50, 57, 56, 56, 56, 56, 56, 57, 50, 41, 19, 8 }, 
  {8, 19, 42, 52, 59, 58, 58, 58, 58, 58, 59, 52, 42, 19, 8 }, 
  {8, 20, 43, 54, 60, 60, 59, 59, 59, 60, 60, 54, 43, 20, 8 }, 
  {9, 21, 45, 55, 62, 62, 61, 61, 61, 61, 62, 55, 45, 21, 9 }, 
  {9, 21, 47, 58, 65, 64, 64, 64, 64, 64, 65, 58, 47, 22, 9 }, 
  {8, 19, 43, 53, 59, 58, 58, 58, 58, 58, 59, 52, 43, 19, 8 }, 
  {6, 15, 36, 44, 48, 48, 48, 48, 48, 48, 48, 43, 36, 15, 6 }, 
  {4, 8, 15, 19, 23, 22, 22, 22, 22, 22, 23, 19, 15, 8, 4 }, 
  {2, 4, 6, 8, 10, 9, 9, 9, 9, 9, 10, 8, 6, 4, 2 }};
}
