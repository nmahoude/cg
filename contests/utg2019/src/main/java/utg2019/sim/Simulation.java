package utg2019.sim;

import trigonometryInt.Point;
import utg2019.world.MapCell;
import utg2019.world.Team;
import utg2019.world.World;
import utg2019.world.entity.Robot;

/*
 * From the world and one action, create another world
 */
public class Simulation {

  public World sim(int teamIndex, World model, Action actions[]) {
    World current = model;
    for (int i=0;i<5;i++) {
      current = sim(teamIndex, i, current, actions[i]);
    }
    return current;
  }
  
  public World sim(int teamIndex, int robotIndex, World world, Action action) {
    if (action.order == Order.WAIT) {
      return world;
    }

    Team team = world.teams[teamIndex];
    Robot robot = team.robots[robotIndex]; 
    
    if (action.order == Order.MOVE) {
      robot.move(action.pos);
      if (action.pos.x == 0 && robot.hasOre()) {
        // deposit ore
        team.score += 1;
        robot.releaseOre();
      }
    } else if (action.order == Order.DIG) {
      world.setHole(action.pos.offset);
      if (world.hasTrap(action.pos.offset)) {
        explodeTrap(world, action.pos.offset);
      }

      if (world.hasRadar(action.pos.offset) && teamIndex == 1) {
        world.removeRadar(action.pos);
      }
      
      if (robot.t_radar > 0) {
        world.putRadar(action.pos);
        robot.t_radar = 0;
      }
      if (world.isCurrentlyKnown(action.pos.offset) && world.getOre(action.pos.offset)> 0) {
        world.setOre(action.pos, world.getOre(action.pos.offset)-1);
        robot.t_ore = 100;
      }
    } else if (action.order == Order.REQUEST) {
      if (action.item == Item.TRAP) {
        if (team.trapCooldown == 0) {
          robot.t_mine = 100;
          robot.t_radar = 0;
          team.trapCooldown = 5;
        }
      } else if (action.item == Item.RADAR) {
        if (team.radarCooldown == 0) {
          robot.t_mine = 0;
          robot.t_radar = 100;
          team.radarCooldown = 5;
        }
      } else {
        // anything
        robot.t_mine = 50;
        robot.t_radar = 50;
      }
    }
    
    return world;
  }

  private void explodeTrap(World world, int offset) {
    boolean needExplosion = world.hasTrap(offset);
    world.removeTrap(offset);
    explodeRobotsOnCell(world, offset);

    if (needExplosion) {
      world.setHole(offset);
      for (MapCell mc : World.mapCells[offset].neighbors) {
        explodeRobotsOnCell(world, mc.pos.offset);
        explodeTrap(world, mc.pos.offset);
      }
    }
  }
  
  private void explodeRobotsOnCell(World world, int offset) {
    for (int tx=0;tx<2;tx++) {
      for (int rx=0;rx<5;rx++) {
        Robot robot = world.teams[tx].robots[rx];
        if (robot.pos.offset == offset) {
          robot.pos = Point.Invalid;
          // TODO is the robot holding a trap ???
        }
      }
    }
  }
}
