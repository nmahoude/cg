package utg2019.world.maps;

import trigonometryInt.Point;
import utg2019.world.MapCell;
import utg2019.world.World;
import utg2019.world.entity.Robot;

public class InsecureMap {
  public static final int INSECURE_OFFSET = 1;
  int insecure[] = new int[World.WIDTH*World.HEIGHT];
  boolean dangerous[] = new boolean[5]; // keep track of threat of robots
  int transportOre[] = new int[5]; // keep track if the robot is transporting ore

  public void update(World old, World current) {
    for (int robotIndex=0;robotIndex<5;robotIndex++) {
      Robot robot = current.teams[1].robots[robotIndex];
      Point currentPos = robot.pos;
      if (robot.isDead()) continue;
      
      if (currentPos != old.teams[1].robots[robotIndex].pos) {
        if (currentPos.x == 0 && old.teams[1].robots[robotIndex].pos.x != 0) {
          // TODO we consider that if the robot come back to base, it's not dangerous anymore
          // unless it stays more than one turn
          dangerous[robotIndex] = false;
          transportOre[robotIndex] = 0;
        }
        continue;
      }

      // robot stayed on the same spot
      if (currentPos.x == 0) {
        dangerous[robotIndex] = true;
        transportOre[robotIndex] = 0;
      } else if (dangerous[robotIndex]){
        // TODO Bombs ?
        if (newHoleThatNobodyElseCouldDo(old, current, robot)) {
          setNewHoleInsecure(old, current, currentPos);
          dangerous[robotIndex] = false;
        } else if (holeAround(current, robot)) {
          setInsecure(current, currentPos);
          // ok the spot is now insecure, but can we detect robot is now NOT threat anymore ?
          if (radarIsMissingAndNobodyAround(old, current, robot)) {
            dangerous[robotIndex] = false;
          }
        }
      }
    }
  }

  private boolean radarIsMissingAndNobodyAround(World old, World current, Robot robot) {
    for (MapCell neighbor : World.mapCells[robot.pos.offset].neighborsAndSelf) {
      int offset = neighbor.pos.offset;
      if (old.hasRadar(offset) && !current.hasRadar(offset)) {
        boolean missingRadarByRobotOnly = true;
        
        for (MapCell mapCell : World.mapCells[offset].neighborsAndSelf) {
          for (int robotIndex=0;robotIndex<5;robotIndex++) {
            Robot other = current.teams[1].robots[robotIndex];
            if (other.isDead()) continue;
            if (other == robot) continue;
            if (other.pos == mapCell.pos) {
              missingRadarByRobotOnly = false;
              break;
            }
          }
        }
        if (missingRadarByRobotOnly) {
          return true;
        }
      }
    }
    
    return false;
  }

  /** is there a hole in the neighboroud */
  private boolean holeAround(World current, Robot robot) {
    for (MapCell neighbor : World.mapCells[robot.pos.offset].neighborsAndSelf) {
      int offset = neighbor.pos.offset;
      if (current.hasHole(offset)
          && ! (current.hasTrap(offset) || current.hasRadar(offset))) {
        return true;
      }
    }
    return false;
  }

  private boolean newHoleThatNobodyElseCouldDo(World old, World current, Robot robot) {
    for (MapCell neighbor : World.mapCells[robot.pos.offset].neighborsAndSelf) {
      int offset = neighbor.pos.offset;
      
      if (current.hasHole(offset) && !old.hasHole(offset)) {
        // new hole
        boolean newHoleByRobot = true;
        for (MapCell mapCell : World.mapCells[offset].neighborsAndSelf) {
          for (int otherIndex=0;otherIndex<5;otherIndex++) {
            Robot other = current.teams[1].robots[otherIndex];
            if (other.isDead()) continue;
            if (other == robot) continue;
            if (other.pos == mapCell.pos && didntMove(otherIndex, old, current)) {
              newHoleByRobot = false;
              break;
            }
          }
        }
        if (newHoleByRobot) {
          return true;
        }
      }
    }
    return false;
  }

  private boolean didntMove(int index, World old, World current) {
    return old.teams[1].robots[index].pos == current.teams[1].robots[index].pos;
  }

  private void setNewHoleInsecure(World old, World current, Point pos) {
    for (MapCell neighbor : World.mapCells[pos.offset].neighborsAndSelf) {
      if (current.hasHole(neighbor.pos) && !old.hasHole(neighbor.pos)) {
        insecure[neighbor.pos.offset] += INSECURE_OFFSET;
      }
    }
  }

  private void setInsecure(World current, Point pos) {
    for (MapCell neighbor : World.mapCells[pos.offset].neighborsAndSelf) {
      if (current.hasHole(neighbor.pos)) {
        insecure[neighbor.pos.offset] += INSECURE_OFFSET;
      }
    }
  }
}
