package spring2022.ag;

import java.util.concurrent.ThreadLocalRandom;

import spring2022.Player;

public class Chromosome {
  private static ThreadLocalRandom random = ThreadLocalRandom.current();
  public int angle;
  public int speed;

  public Chromosome() {
  }
  
  public Chromosome(int angle, int speed) {
    this.angle = angle;
    this.speed = speed;
  }


  public void copyFrom(Chromosome model) {
    this.angle = model.angle;
    this.speed = model.speed;
  }

  public void random() {
    angle = 5 * random.nextInt(360 / 5);
    speed = 25 * random.nextInt(32+1);
    
    if (random.nextDouble() > 0.95) {
      speed = -1;
      angle = -1;
    }
    // no control here
  }
  
  public void randomFromCorner() {
    if (random.nextDouble() > 0.6) {
      random();
    } else {
      int deviationAngle = 20;
      angle = clipAngle(0  - deviationAngle + random.nextInt(90 + deviationAngle));
      speed = 25 * random.nextInt(32+1);
    }
  }

  

  private int clipAngle(int a) {
    if (a >= 360) a -= 360;
    if (a < 0 ) a += 360;

    return a;
  }

  public void mutate(Chromosome model) {
    
    if (random.nextDouble() > 0.9) {
      // do a random wind
      speed = -1;
      angle = clipAngle(random.nextInt(110) - 10);
      return;
    }
    
    if (Player.ennemyIdFE != 0 && random.nextDouble() > 0.9) {
      // do a random control
      speed = -666;
      angle = Player.ennemyId[random.nextInt(Player.ennemyIdFE)];
      return;
    }
    
    this.angle = model.angle;
    this.speed = model.speed;

    if (speed >= 0) {
      int deltaSpeed = 25 * (3 - random.nextInt(3*2));
      speed = Math.max(0, Math.min(800, speed + deltaSpeed));
    } else {
      // wind or control or shield : don't change speed !
    }
    
    if (speed >= -10) {
      // move or wind
      int deltaAngle = 5 - random.nextInt(5*2) ;
      angle = clipAngle(angle + deltaAngle);
    } else {
      // control : don't change angle (entityid)
      // shield
    }
    
  }

}