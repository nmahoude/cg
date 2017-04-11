package csb.game;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import csb.entities.CheckPoint;
import csb.entities.Pod;
import trigonometry.Point;
import trigonometry.Vector;

public class Referee {
  private static final Pattern PLAYER_INPUT_MOVE_PATTERN = Pattern
      .compile("(?<x>-?[0-9]{1,8})\\s+(?<y>-?[0-9]{1,8})\\s+(?<thrust>([0-9]{1,8}))", Pattern.CASE_INSENSITIVE);

  private static final int CHECKPOINT_GENERATION_MAX_GAP = 30;

  static Random random = new Random();

  public static boolean collisionOn = true;
  public PhysicsEngine physics;
  public Pod pods[];
  public Point target[];
  
  public int checkPointCount;
  public CheckPoint checkPoints[];

  public boolean collisionOccur;

  public int playerCount;
  
  public void initReferee(int seed, int playerCount) throws Exception {
    this.playerCount = playerCount;
    random = new Random(seed);
  
    generateMap(random, maps.get(random.nextInt(maps.size())));
    this.checkPointCount = checkPoints.length;
    
    Point origin = checkPoints[0].position;
    Vector dir = checkPoints[1].position.sub(checkPoints[0].position);
    Vector ortho = dir.rotate(Math.PI/2).normalize();
    
    double radius= 400;
    double space = 200;
    origin = origin.add(ortho.dot(space*2 + radius*3));
    pods = new Pod[playerCount];
    target = new Point[playerCount];
    for (int i=0;i<playerCount;i++) {
      int index = (i+1) % (playerCount);
      pods[i] = new Pod(i);
      pods[i].nextCheckPointId = 1;
      pods[i].position = new Point((int)(origin.x-ortho.vx*(i*(radius*2+space))), (int)(origin.y-ortho.vy*(i*(radius*2+space))));
      pods[i].direction = checkPoints[1].position.sub(pods[i].position).normalize();
      target[i] = checkPoints[1].position;
      pods[i].backup();
    }
    
    physics = new PhysicsEngine();
    physics.pods = pods;
    physics.checkPoints = checkPoints;
  }
  
  protected String[] getInputForPlayer(int round, int playerIdx) {
    return null;
  }
  
  public void handlePlayerOutput(int frame, int round, int playerIdx, String[] outputs) {
    // output in the form : X Y thurst
    String action = outputs[0];
    Matcher matchMove = PLAYER_INPUT_MOVE_PATTERN.matcher(action);
    if (matchMove.matches()) {
      int x = Integer.parseInt(matchMove.group("x"));
      int y = Integer.parseInt(matchMove.group("y"));
      target[playerIdx] = new Point(x,y);
      int thrust = Math.min(100, Math.max(0, Integer.parseInt(matchMove.group("thrust"))));
      
      
      Pod pod = pods[playerIdx];
      Vector currentDirection = pod.direction;
      Vector n = currentDirection.ortho();
      Vector wishedDirection = new Point(x, y).sub(pod.position).normalize();
      double wishedAngle = Math.acos(currentDirection.dot(wishedDirection));
      double sign = n.dot(wishedDirection) > 0 ? 1.0 : -1.0;
      // limit angle
      if (Math.abs(wishedAngle) <  18 * Math.PI / 180) {
      } else {
        wishedAngle=+18* Math.PI / 180.0;
      }
      
      pod.apply(pod.direction.rotate(sign*wishedAngle), thrust);
    }
  }
  
  public void updateGame(int round) throws Exception {

    physics.simulate();

    for (Pod pod : pods) {
      pod.backup();
    }
  }

  static List<Point[]> maps = new ArrayList<>();
  static {
    maps.add(new Point[] { new Point(12460, 1350), new Point(10540, 5980), new Point(3580, 5180), new Point(13580, 7600) });
    maps.add(new Point[] { new Point(3600, 5280), new Point(13840, 5080), new Point(10680, 2280), new Point(8700, 7460), new Point(7200, 2160) });
    maps.add(new Point[] { new Point(4560, 2180), new Point(7350, 4940), new Point(3320, 7230), new Point(14580, 7700), new Point(10560, 5060), new Point(13100, 2320) });
    maps.add(new Point[] { new Point(5010, 5260), new Point(11480, 6080), new Point(9100, 1840) });
    maps.add(new Point[] { new Point(14660, 1410), new Point(3450, 7220), new Point(9420, 7240), new Point(5970, 4240) });
    maps.add(new Point[] { new Point(3640, 4420), new Point(8000, 7900), new Point(13300, 5540), new Point(9560, 1400) });
    maps.add(new Point[] { new Point(4100, 7420), new Point(13500, 2340), new Point(12940, 7220), new Point(5640, 2580) });
    maps.add(new Point[] { new Point(14520, 7780), new Point(6320, 4290), new Point(7800, 860), new Point(7660, 5970), new Point(3140, 7540), new Point(9520, 4380) });
    maps.add(new Point[] { new Point(10040, 5970), new Point(13920, 1940), new Point(8020, 3260), new Point(2670, 7020) });
    maps.add(new Point[] { new Point(7500, 6940), new Point(6000, 5360), new Point(11300, 2820) });
    maps.add(new Point[] { new Point(4060, 4660), new Point(13040, 1900), new Point(6560, 7840), new Point(7480, 1360), new Point(12700, 7100) });
    maps.add(new Point[] { new Point(3020, 5190), new Point(6280, 7760), new Point(14100, 7760), new Point(13880, 1220), new Point(10240, 4920), new Point(6100, 2200) });
    maps.add(new Point[] { new Point(10323, 3366), new Point(11203, 5425), new Point(7259, 6656), new Point(5425, 2838) });
  }
  private void generateMap(Random r, Point[] map) {
    List<CheckPoint> checkPoints = new ArrayList<>();
    List<Point> points = Arrays.asList(map);
    Collections.rotate(points, r.nextInt(points.size()));
    int id = 4;
    for (Point p : points) {
        checkPoints.add(new CheckPoint(id++, p.x + r.nextInt(CHECKPOINT_GENERATION_MAX_GAP * 2 + 1) - CHECKPOINT_GENERATION_MAX_GAP, p.y + r.nextInt(CHECKPOINT_GENERATION_MAX_GAP * 2 - 1) - CHECKPOINT_GENERATION_MAX_GAP));
    }
    this.checkPoints = checkPoints.toArray(new CheckPoint[checkPoints.size()]);
  }
}
