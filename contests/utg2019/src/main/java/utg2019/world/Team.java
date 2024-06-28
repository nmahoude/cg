package utg2019.world;

import java.util.Scanner;

import trigonometryInt.Point;
import utg2019.Owner;
import utg2019.Player;
import utg2019.world.entity.EntityType;
import utg2019.world.entity.Robot;

public class Team {
  public final Owner owner;
 
  public int score;
  public int radarCooldown;
  public int trapCooldown;
  
  private int robotsFE;
  public Robot robots[] = new Robot[5];
  
  
  public Team(Owner owner) {
    this.owner = owner;
    
    robotsFE = 0;
    for (int i=0;i<robots.length;i++) {
      robots[robotsFE++] = new Robot(owner, i);
    }
  }
  
  public void read(Scanner in) {
    score = in.nextInt();
    if (Player.DEBUG_INPUT) {
      System.err.print(String.format("%d ",score));
    }
  }

  public void addRobot(int id, Point pos, EntityType item) {
    // TODO handle enemy robots uncertainty ??
    Robot robot = robots[robotsFE++];
    robot.pos = pos;
    robot.resetTransport();
    
    switch(item) {
    case AMADEUSIUM:
      robot.setOre(100);
      break;
    case NOTHING:
      break;
    case RADAR:
      robot.setRadar(100);
      break;
    case TRAP:
      robot.setMine(100);
      break;
    default:
      throw new RuntimeException("Unknwon transportation type : "+item);
    }
  }

  public void resetFE() {
    robotsFE = 0;
  }

  public void copyFrom(Team model) {
    this.score = model.score;
    this.radarCooldown = model.radarCooldown;
    this.trapCooldown = model.trapCooldown;
    
    for (int i=0;i<5;i++) {
      this.robots[i].copyFrom(model.robots[i]);
    }
  }
}
