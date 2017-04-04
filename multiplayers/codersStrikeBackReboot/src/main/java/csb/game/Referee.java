package csb.game;

import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import csb.entities.CheckPoint;
import csb.entities.Pod;
import trigonometry.Point;
import trigonometry.Vector;

public class Referee {
  private static final Pattern PLAYER_INPUT_MOVE_PATTERN = Pattern
      .compile("(?<x>[0-9]{1,8})\\s+(?<y>[0-9]{1,8})\\s+(?<thrust>([0-9]{1,8}))", Pattern.CASE_INSENSITIVE);

  static Random random = new Random();
  static int borderX = 1000;
  static int borderY = 1000;
  public Pod pods[];
  public CheckPoint checkpoints[];
  
  public void initReferee(int seed, int playerCount, int checkPointCount) throws Exception {
    random = new Random(seed);
  
    checkpoints = new CheckPoint[checkPointCount];
    for (int i=0;i<checkPointCount;i++) {
      checkpoints[i] = new CheckPoint(borderX+random.nextInt(16000-borderX*2), borderY+random.nextInt(9000-borderY*2));
    }
    
    Point origin = checkpoints[0].position;
    Vector dir = checkpoints[1].position.sub(checkpoints[0].position);
    Vector ortho = dir.rotate(Math.PI/2).normalize();
    
    double radius= 400;
    double space = 50;
    origin = origin.add(ortho.dot(space*2 + radius*3));
    pods = new Pod[playerCount*2];
    for (int i=0;i<playerCount*2;i++) {
      pods[i] = new Pod();
      pods[i].position = new Point(origin.x-ortho.vx*(i*(radius*2+space)), origin.y-ortho.vy*(i*(radius*2+space)));
    }
  }
  
  protected String[] getInputForPlayer(int round, int playerIdx) {
    return null;
  }
  
  protected void handlePlayerOutput(int frame, int round, int playerIdx, String[] outputs) {
    // output in the form : X Y thurst
    String action = outputs[0];
    Matcher matchMove = PLAYER_INPUT_MOVE_PATTERN.matcher(action);
    if (matchMove.matches()) {
      int x = Integer.parseInt(matchMove.group("x"));
      int y = Integer.parseInt(matchMove.group("y"));
      int thurst = Integer.parseInt(matchMove.group("thrust"));
      
      Pod pod = pods[playerIdx];
      Vector wishedDirection = new Point(x, y).sub(pod.position).normalize();
      double wishedAngle = wishedDirection.dot(Pod.xVector);
      double delta = wishedAngle - pod.angle;
      if (Math.abs(delta) < Math.PI / 10) {
        pod.angle = wishedAngle;
      } else {
        if (delta > 0) {
          pod.angle+=18;
        } else {
          pod.angle-=18;
        }
      }
    }
  }
  
  protected void updateGame(int round) throws Exception {
    
  }
  
  
}
