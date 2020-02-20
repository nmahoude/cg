package bttc;

public class AI {
  
  private static int lastDepth = 0;
  private static int lastDdx;
  private static int lastDdy;
  private static int lastDx[] = new int[50];
  private static int lastDy[] = new int[50];

  
  private static double bestScore;
  private static Map testMap;
  private static double[] patience;
  static {
    patience = new double[50];
    for (int i=0;i<50;i++) {
      patience[i] = Math.pow(0.9, i);
    }
  }
  private int bestDepth;
  private int bestDxs[];
  private int bestDys[];
  private P currentPos;
  private Map map;
  private int iter;
  
  public void think(P currentPos, Map map) {
    
    this.currentPos = currentPos;
    this.map = map;
    bestScore = 0;
    testMap = new Map();


    bestDepth = 0;
    bestDxs = new int[50]; 
    bestDys = new int[50];

    int dx[] = new int[50]; 
    int dy[] = new int[50];
    
    iter = 0;
    while (true) {
      iter++;
      
      if ((iter & 255) == 0) {
        if (System.currentTimeMillis() - Player.start > Player.MAX_TIME) break;
      }
      testMap.copy(map);
      
      int maxDepth = Player.rand.nextInt(15)+1;
      if (iter == 0) {
        for (int i=0;i<lastDepth;i++) {
          dx[i] = lastDx[i];
          dy[i] = lastDy[i];
        }
        maxDepth = lastDepth;
      } else {
        calculatePathAtDepth(testMap, currentPos.x, currentPos.y, dx, dy, 0, maxDepth);
      }
      
      double pathScore = pathScore(testMap, currentPos.x, currentPos.y, dx, dy, maxDepth);
      double fillScore = (1.0 * testMap.fillForPoints()) / (maxDepth );
      
      double score = 0;
      if (fillScore > 0) {
         score = pathScore + fillScore; //1.0 * Math.pow(fillScore, 3) / maxDepth;
      }
      if (score > bestScore) {
        bestScore = score;
        bestDepth = maxDepth;
        for (int i=0;i<bestDepth;i++) {
          bestDxs[i] = dx[i];
          bestDys[i] = dy[i];
        }
      }
    }
  }
  
  public void output() {
    System.err.println("iterations : " + iter+" in "+(System.currentTimeMillis()-Player.start)+" ms");
    if (bestScore > 0) {
      System.err.println("Moving to close with score "+bestScore);
      System.err.println("path : ");
      for (int i=0;i<bestDepth;i++) {
        System.err.print("["+bestDxs[i]+","+bestDys[i]+"],");
      }
      System.err.println();

      lastDdx = bestDxs[0];
      lastDdy = bestDys[0];
      for (int i=0;i<bestDepth;i++) {
        lastDx[i] = bestDxs[i+1];
        lastDy[i] = bestDys[i+1];
      }
      lastDepth = bestDepth;
      
      System.out.println(""+(currentPos.x+bestDxs[0])+" "+(currentPos.y+bestDys[0]));
    } else {
      P closestFree = map.findNearestEmptyCell(currentPos);
      System.err.println("Moving to closest freecell");
      System.out.println(""+closestFree.x+" "+closestFree.y);
    }
  }
  
  private static double pathScore(Map testMap, int x, int y, int[] dx, int[] dy, int depth) {
    double score = 0;
    
    for (int p=0;p<depth;p++) {
      x+=dx[p];
      y+=dy[p];
      if (p > 0) {
        if (dx[p] == dx[p-1] && dy[p] == dy[p-1]) {
          score+= 5 * patience [p];
        }
      } else {
        if (lastDdx == dx[0] && lastDdy == dy[0]) {
          score+= 5;
        }
      }
      
      // update owner
      int owner = testMap.getPlayerAt(x, y);
      if (owner == -1) {
        score +=5; //1;
        testMap.move(0, x, y);
        // TODO what if we fill the map here ? :(
      } else if (owner == 0) {
        score -= 0; //10;
      } else {
        score -= 0; //10;
      }
      
    }
    return score;
  }
  
  private static void calculatePathAtDepth(Map testMap, int x, int y, int[] dx, int[] dy, int depth, int maxDepth) {
    if (depth == maxDepth) return;

    int ddx = 0, ddy = 0;
    int dir = Player.rand.nextInt(4);
    switch(dir) {
      case 0: ddx = 1; break;
      case 1: ddx = -1; break;
      case 2: ddy = 1; break;
      case 3: ddy = -1; break;
    }
    
    
    
    if ((x+ddx)<0 || (x+ddx)>=35 || (y+ddy) <0 || (y+ddy)>=20) {
      calculatePathAtDepth(testMap, x, y, dx, dy, depth, maxDepth);
    } else {
      dx[depth] = ddx;
      dy[depth] = ddy;
      calculatePathAtDepth(testMap, x+ddx, y+ddy, dx, dy, depth+1, maxDepth);
    }
  }

}
