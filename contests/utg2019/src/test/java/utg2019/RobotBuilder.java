package utg2019;

import trigonometryInt.Point;
import utg2019.world.World;
import utg2019.world.entity.Robot;
import utg2019.world.maps.TrapAdvisor;

public class RobotBuilder {
  public static World old;
  public static World current;
  public static TrapAdvisor trapAdvisor;
  
  private int team;
  private int index;
  private Robot oldRobot, newRobot;
  public RobotBuilder(int team, int index) {
    this.team = team;
    this.index = index;
    this.oldRobot = old.teams[team].robots[index];
    this.newRobot = current.teams[team].robots[index];
  }
  
  public RobotBuilder stayedAt(Point point) {
    from(point);
    to(point);
    return this;
  }

  public RobotBuilder isDangerous() {
    trapAdvisor.canTransportMine[index] = true;
    return this;
  }

  public RobotBuilder isNotDangerous() {
    trapAdvisor.canTransportMine[index] = false;
    return this;
  }

  public RobotBuilder move() {
    return this; // sugar
  }
  public RobotBuilder from(Point pos) {
    oldRobot.pos = pos;
    return this; 
  }
  public RobotBuilder to(Point pos) {
    newRobot.pos = pos;
    return this; 
  }

  public RobotBuilder digAt(Point point) {
    if (newRobot.pos == Point.Invalid) {
      stayedAt(point);
    }
    current.setHole(point);
    return this;
  }

  public RobotBuilder and() {
    return this;
  }

  public RobotBuilder dig() {
    current.setHole(newRobot.pos);
    return this;
  }
  
  public static RobotBuilder robot(int team, int index) {
    return new RobotBuilder(team, index);
  }

}

