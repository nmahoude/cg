package tests;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import csb.Team;
import csb.entities.CheckPoint;
import csb.entities.Pod;
import csb.game.PhysicsEngine;
import trigonometry.Point;
import trigonometry.Vector;

public class Referee {
  public static final int LAP_COUNT = 3;
  public static final int TIMEOUT = 100;
  
  public static boolean debugoutput = false;
  
  private static final Pattern PLAYER_INPUT_MOVE_PATTERN = Pattern
      .compile("(?<x>-?[0-9]{1,8})\\s+(?<y>-?[0-9]{1,8})\\s+(?<thrust>([0-9]{1,8}))", Pattern.CASE_INSENSITIVE);

  private static final int CHECKPOINT_GENERATION_MAX_GAP = 30;

  static Random random = new Random();

  public static boolean collisionOn = true;
  public PhysicsEngine physics;
  public Team teams[] = new Team[2];
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
    
    Point origin = new Point(checkPoints[0].x, checkPoints[0].y);
    Vector dir = new Vector(checkPoints[1].x-checkPoints[0].x, checkPoints[1].y-checkPoints[0].y);
    Vector ortho = dir.rotate(Math.PI/2).normalize();
    
    double radius= 400;
    double space = 200;
    origin = origin.add(ortho.dot(space*2 + radius*3));
    pods = new Pod[playerCount];
    target = new Point[playerCount];
    
    teams[0] = new Team();
    teams[1] = new Team();
    pods[0] = new Pod(0, teams[0]);
    pods[1] = new Pod(1, teams[0]);
    pods[2] = new Pod(2, teams[1]);
    pods[3] = new Pod(3, teams[1]);
    
    for (int i=0;i<playerCount;i++) {
      int index = (i+1) % (playerCount);
      pods[i].nextCheckPointId = 1;
      pods[i].x=(int)(origin.x-ortho.vx*(index*(radius*2+space)));
      pods[i].y=(int)(origin.y-ortho.vy*(index*(radius*2+space)));
      pods[i].direction = new Vector(checkPoints[1].x-pods[i].x, checkPoints[1].y-pods[i].y) .normalize();
      target[i] = new Point(checkPoints[1].x, checkPoints[1].y);
      pods[i].backup();
    }
    
    physics = new PhysicsEngine();
    physics.pods = pods;
    physics.checkPoints = checkPoints;
  }
  
  protected String[] getInputForPlayer(int round, int playerIdx) {
    return null;
  }
  
  public void handlePlayerOutput(int frame, int round, int playerIdx, String action) {
    // output in the form : X Y thurst
    if (debugoutput) {
      if (playerIdx == 0) {
        System.err.println("----");
        System.err.println("Player1");
      }
      if (playerIdx == 2) {
        System.err.println("Player2");
      }
      System.err.println(action);
    }
    Matcher matchMove = PLAYER_INPUT_MOVE_PATTERN.matcher(action);
    if (matchMove.matches()) {
      int x = Integer.parseInt(matchMove.group("x"));
      int y = Integer.parseInt(matchMove.group("y"));
      target[playerIdx] = new Point(x,y);
      int thrust = Math.min(100, Math.max(0, Integer.parseInt(matchMove.group("thrust"))));
      
      
      Pod pod = pods[playerIdx];
      Vector currentDirection = pod.direction;
      Vector n = currentDirection.ortho();
      Vector wishedDirection = new Vector(x-pod.x, y-pod.y).normalize();
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
      if (pod.lap == LAP_COUNT) {
        throw new RaceFinished(pod.id); // TODO handle draw ?
      }
      if (pod.team.timeout >= TIMEOUT) {
        throw new RaceFinished((pod.id+2) % 4); // TODO better handle this ?
      }
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
