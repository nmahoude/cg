package coderoyale2;

import java.util.Random;
import java.util.Scanner;

import coderoyale2.simulation.Action;
import coderoyale2.simulation.Simulation;
import coderoyale2.structures.Barrack;
import coderoyale2.structures.Mine;
import coderoyale2.structures.Structure;
import coderoyale2.structures.Tower;
import coderoyale2.units.Creep;
import coderoyale2.units.Queen;
import coderoyale2.units.Site;
import coderoyale2.units.Unit;

public class Player {
  private static final int MAX_CREEPS = 1000;
  private static Random rand = new Random(0);
  
  public static int numSites;
  public static int turn = 0;

  public static Queen[] queens = new Queen[2];
  
  public static Site sites[] ;
  
  public static int creepsStart;
  public static int creepsFE = 0;
  private static int _creepsFE;
  public static Unit all[] = new Unit[100+MAX_CREEPS]; // numsite Site, 2 queen, creeps
  
  
  public static Barrack[] barracksCache;
  public static Mine[] minesCache;
  public static Tower[] towersCache;
  
  public static void main(String args[]) {
    Scanner in = new Scanner(System.in);
    readPreGame(in);

    // game loop
    while (true) {
      readGameState(in);
      
      backupGameState();
      
      Action actions[] = new Action[10];
      Action bestActions[] = new Action[10];
      for (int i=0;i<10;i++) {
        actions[i] = new Action();
        bestActions[i] = new Action();
      }
      double bestScore = -1;
      long start = System.currentTimeMillis();
      for (int i=0;i<100;i++) {
        for (int a=0;a<10;a++) {
          actions[a].angle = 2 * Math.PI * rand.nextDouble();
          double lrand = rand.nextDouble();
          if (lrand > 0.5) {
            actions[a].length = 60;
          } else {
            actions[a].length = 2.0 * lrand * 60;
          }
        }
        
        new Simulation().simulate(actions);
        
        double score = queens[0].health;
        for (int c=creepsStart;c<creepsFE;c++) {
          Creep creep = (Creep)all[c];
          if (creep.health <= 0) continue;
          if (creep.owner == 0) continue;
          double dist = creep.location.distanceTo(queens[0].location);
          score += dist;
        }
        if (score > bestScore) {
          bestScore = score;
          for (int a=0;a<10;a++) {
            bestActions[a].angle = actions[a].angle;
            bestActions[a].length = actions[a].length;
          }
        }
        restoreGameState();
      }
      long end = System.currentTimeMillis();
      System.err.println("Think in " + (end-start) + " ms" );
      if (bestActions[0].length == 0) {
        System.out.println("WAIT");
      } else {
        int x = (int) (queens[0].location.x + bestActions[0].length * Math.cos(bestActions[0].angle));
        int y = (int) (queens[0].location.y + bestActions[0].length * Math.sin(bestActions[0].angle));
        System.out.println("MOVE "+x + " " + y);
      }
      System.out.println("TRAIN");
      
    }
  }  
  private static void backupGameState() {
    _creepsFE = creepsFE;
    for (int i=0;i<creepsFE;i++) {
      all[i].backup();
    }
  }
  
  private static void restoreGameState() {
    creepsFE = _creepsFE;
    for (int i=0;i<creepsFE;i++) {
      all[i].restore();
    }
  }

  private static void readPreGame(Scanner in) {
    numSites = in.nextInt();
    creepsStart = numSites+2;
    
    sites = new Site[numSites];
    barracksCache = new Barrack[numSites];
    minesCache = new Mine[numSites];
    towersCache = new Tower[numSites];

    
    for (int i = 0; i < numSites; i++) {
      int siteId = in.nextInt();
      int x = in.nextInt();
      int y = in.nextInt();
      int radius = in.nextInt();
      Site site = new Site(siteId, new Point(x, y), radius);
      sites[siteId] = site;
      all[i] = site;
      barracksCache[siteId] = new Barrack(site);
      minesCache[siteId] = new Mine(site);
      towersCache[siteId] = new Tower(site);
      site.structure = null;
    }
    queens[0] = new Queen(0);
    queens[1] = new Queen(1);
    
    all[numSites] = queens[0];
    all[numSites+1] = queens[1];

    creepsStart = numSites + 2;
    for (int i=0;i<MAX_CREEPS;i++) {
      all[creepsStart+i] = new Creep(-1);
    }
    
  }
  
  private static void readGameState(Scanner in) {
    turn++;

    queens[0].gold = in.nextInt();
    queens[0].touchedSite = in.nextInt(); // -1 if none
    
    for (int i = 0; i < numSites; i++) {
      int siteId = in.nextInt();
      int gold = in.nextInt(); // used in future leagues
      int maxMineSize = in.nextInt(); // used in future leagues
      Site site = sites[siteId];
      site.gold = gold;
      site.maxMineSize = maxMineSize;
      
      
      int structureType = in.nextInt(); // -1 = No structure, 2 = Barracks
      int owner = in.nextInt(); // -1 = No structure, 0 = Friendly, 1 = Enemy
      int param1 = in.nextInt();
      int param2 = in.nextInt();
      
      Structure structure;
      if (structureType == Structure.BARRACK) {
        Barrack b = barracksCache[siteId];
        structure = b;
        b.turnBeforeTrain = param1;
        b.subtype = param2; // KNIGHT, ... , GIANT
      } else if (structureType == Structure.TOWER){
        Tower t = towersCache[siteId];
        structure = t;
        t.life = param1;
        t.attackRadius = param2;
      } else if (structureType == Structure.MINE) {
        Mine m = minesCache[siteId];
        structure = m;
        m.incomeRate = param1;
      }  else {
        structure = Structure.NONE;
      }
      structure.type = structureType;
      structure.owner = owner;
      site.structure = structure;
    }
    
    int numUnits = in.nextInt();
    creepsFE = creepsStart;
    for (int i = 0; i < numUnits; i++) {
      
      int x = in.nextInt();
      int y = in.nextInt();
      int owner = in.nextInt();
      int unitType = in.nextInt(); // -1 = QUEEN, 0 = KNIGHT, 1 = ARCHER
      int health = in.nextInt();

      if (unitType == -1 && owner == 0) {
        queens[0].location.x = x;
        queens[0].location.y = y;
        queens[0].health = health;
      } else if (unitType == -1 && owner == 1) {
        queens[1].location.x = x;
        queens[1].location.y = y;
        queens[1].health = health;
      } else {
        Creep c = (Creep)all[creepsFE++];
        c.setType(unitType);
        c.location.x = x;
        c.location.y = y;
        c.owner = owner;
        c.health = health;
      }
    }
  }
}
