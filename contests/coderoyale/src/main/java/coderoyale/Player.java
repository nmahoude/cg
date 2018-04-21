package coderoyale;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

import coderoyale.sites.Barrack;
import coderoyale.sites.Mine;
import coderoyale.sites.Site;
import coderoyale.sites.Structure;
import coderoyale.sites.Tower;
import coderoyale.units.Queen;
import coderoyale.units.Unit;

public class Player {
  
  private static final int KNIGHT_COST = 80;
  private static final int ARCHER_COST = 100;
  private static final int GIANT_COST  = 140;
  
  private static List<Site> sitesRenamed;
  private static List<Tower> towers = new ArrayList<>();
  private static List<Barrack> barracks = new ArrayList<>();
  private static List<Mine> mines = new ArrayList<>();
  
  private static List<Unit> units;
  public static Queen me = new Queen();
  private static Queen him = new Queen();
  
  public static void main(String args[]) {
    Scanner in = new Scanner(System.in);
    int numSites = in.nextInt();
    sitesRenamed = new ArrayList<>();
    for (int i = 0; i < numSites; i++) {
      int siteId = in.nextInt();
      int x = in.nextInt();
      int y = in.nextInt();
      int radius = in.nextInt();
      Site site = new Site(siteId, x, y, radius);
      sitesRenamed.add(site);
    }

    // game loop
    while (true) {
      me.gold = in.nextInt();
      me.touchedSite = in.nextInt(); // -1 if none
      
      barracks.clear();
      towers.clear();
      mines.clear();
      
      for (int i = 0; i < numSites; i++) {
        int siteId = in.nextInt();
        int gold = in.nextInt(); // used in future leagues
        int maxMineSize = in.nextInt(); // used in future leagues
        int structureType = in.nextInt(); // -1 = No structure, 2 = Barracks
        int owner = in.nextInt(); // -1 = No structure, 0 = Friendly, 1 = Enemy
        int param1 = in.nextInt();
        int param2 = in.nextInt();
        Site site = getSite(siteId);
        site.gold = gold;
        site.maxMineSize = maxMineSize;
        if (structureType == Structure.BARRACK) {
          Barrack b= new Barrack(site);
          b.turnBeforeTrain = param1;
          b.subtype = param2; // KNIGHT, ... , GIANT
          site.structure = b;
          barracks.add(b);
        } else if (structureType == Structure.TOWER){
          Tower t = new Tower(site);
          t.life = param1;
          t.attackRadius = param2;
          site.structure = t;
          towers.add(t);
        } else if (structureType == Structure.MINE) {
          Mine m = new Mine(site);
          m.incomeRate = param1;
          site.structure = m;
          mines.add(m);
        } 
        site.structure.owner = owner;
        
      }
      
      int numUnits = in.nextInt();
      units = new ArrayList<>();
      for (int i = 0; i < numUnits; i++) {
        int x = in.nextInt();
        int y = in.nextInt();
        int owner = in.nextInt();
        int unitType = in.nextInt(); // -1 = QUEEN, 0 = KNIGHT, 1 = ARCHER
        int health = in.nextInt();
        Unit unit;
        
        if (unitType == -1 && owner == 0) {
          me.pos.x = x;
          me.pos.y = y;
          unit = me;
        } else if (unitType == -1 && owner == 1) {
          him.pos.x = x;
          him.pos.y = y;
          unit = him;
        } else {
          unit = new Unit();
          unit.owner = owner;
          unit.pos = new Pos(x, y);
          unit.type = unitType;
        }
        unit.health = health;
        units.add(unit);
      }

      Site closestFree = null;
      for (Site site : sitesRenamed) {
        if (site.imOwner()) {
          continue;
        }
        if (closestFree == null ) {
          closestFree = site;
        } else if (site.imNotOwner() && closestFree.pos.dist2(me.pos) > site.pos.dist2(me.pos)) {
          closestFree = site;
        }
      }

      // Write an action using System.out.println()
      // To debug: System.err.println("Debug messages...");

      // First line: A valid queen action
      // Second line: A set of training instructions
      issueQueenCommand(closestFree);
      issueTrainCommand();
    }
  }

  private static void issueTrainCommand() {
    String train = "TRAIN ";
    List<Site> closestToHim = getSiteByClosestDistance(him).stream()
      .filter(s -> { return s.imOwner() && s.canTrain(); })
      .collect(Collectors.toList());
    
    System.err.println("Build as many knight as possible ....");
    int gold = me.gold;
    for (Site site : closestToHim) {
      if (gold < KNIGHT_COST) break;
      train += site.id + " ";
      gold -= KNIGHT_COST;
    }
    System.out.println(train.trim());
  }

  private static void issueQueenCommand(Site closestFree) {
    if (needSomeInitialMines(closestFree)) {
      return;
    }
    
    int ennemyKnightAround = getEnnemyKnightAround(600);
    System.err.println("Ennemy knights around : "+ ennemyKnightAround);
    if (ennemyKnightAround > 2 && doDefensiveMove()) {
      return;
    }
    
    if (doGiantInitiative()) {
      return;
    }
    if (doNormalMove(closestFree)) {
      return;
    }
    
    System.out.println("WAIT");
    return;
  }

  private static boolean doGiantInitiative() {
    // check if we need to do a giant barracks & build giant
    long ennemyTowers = towers.stream()
                          .filter(t -> {return t.owner != 0; })
                          .count();
    if (ennemyTowers > 100) { // TODO yolo
      Site site = getSiteByClosestDistance(me).stream()
          .filter(s -> { return s.isTower(); } )
          .findFirst().orElse(null);
      if (site != null) {
        if (site.isGiant()) {
          // TODO hmmmm now we need to train a giant ....
          System.err.println("ARGGG need to train a giant now in priority !");
        } else {
          return me.moveTo(site).then(site::buildGiant).end();
        }
      }
      
    } 
    return false;
  }

  private static boolean needSomeInitialMines(Site closestFree) {
    List<Mine> myMines = mines.stream()
        .filter(mine -> {return mine.owner == 0;})
        .collect(Collectors.toList());

    int minedGoldPerTurn = myMines.stream()
        .filter(mine -> mine.hasGold())
        .map( mine -> mine.incomeRate)
        .reduce(0, Integer::sum);

    // check if we are near an upgradable mine first !
    if (finishMineUpgrade()) {
      return true;
    }
    
    System.err.println("I'm currently mining " + minedGoldPerTurn);
    if (minedGoldPerTurn < 4) {
      System.err.println("Still got to add some mines ...");
      List<Site> sites= getSiteByClosestDistance(me).stream()
                            .filter(s -> !s.isTower())
                            .filter(s -> { return s.imNotOwner() || !s.maxMined(); })
                            .collect(Collectors.toList());
      for (Site site : sites) {
        boolean action = moveToSiteAndBuildMine(site);
        if (action) {
          return true; 
        } else {
          System.err.println("Site " + site.id + " is not eligeable");
        }
      }
    }
    return false;
  }

  private static boolean finishMineUpgrade() {
    List<Site> sList = getSiteByClosestDistance(me);
    if (sList.isEmpty()) return false;
    
    Site mineSite = sList.get(0);
    if (mineSite.isInRange(me) && mineSite.isAMine() && mineSite.imOwner()) {
      System.err.println("Finish upgrading mine "+ mineSite.id);
      if (me.moveTo(mineSite).then(mineSite::buildMine).then(mineSite::upgradeMine).end()) {
        return true;
      }
    }
    return false;
  }

  private static List<Site> getSiteByClosestDistance(Queen queen) {
    return sitesRenamed.stream()
        .sorted((s1, s2) -> Double.compare(s1.pos.dist(queen.pos), s2.pos.dist(queen.pos)))
        .collect(Collectors.toList());
  }

  private static boolean moveToSiteAndBuildMine(Site site) {
    System.err.println("Move and build to site "+ site.id);
    return me.moveTo(site).then(site::buildMine).then(site::upgradeMine).end();
  }

  private static boolean doNormalMove(Site closestFree) {
    long myBarracksCount = barracks.stream()
          .filter(Structure::isMine)
          .count();
    if (myBarracksCount < 3) {
      System.err.println("Building barracks ....");
      return me.moveTo(closestFree).then(closestFree::buildKnightBarrack).end();
    } else {
      System.err.println("Enough barracks");
    }
    
    // build some more mines
    List<Site> closestAvailableSites = getSiteByClosestDistance(me).stream()
        .filter( s -> {return s.imNotOwner() && !s.isTower();})
        .collect(Collectors.toList());
    
    if (!closestAvailableSites.isEmpty()) {
      Site site = closestAvailableSites.get(0);
      System.err.println("Move to site " + site);
      return me.moveTo(site).then(site::buildMine).then(site::upgradeMine).end();
    }
    
    
    //TODO Here, THERE IS A LOT TO DO
    //  - if we are endanger, we may goback near tower and (re)power them
    //  - we may want to add mines to have more knight later
    //  - we may want to build outpost to get nearest the ennemy (faster waves of creeps)
    //  - we may want to build new towers to increase protection
    
    List<Site> closestTowers = getSiteByClosestDistance(me).stream()
        .filter(s -> { return s.imOwner() && s.isTower(); })
        .collect(Collectors.toList());
    
    if (!closestTowers.isEmpty()) {
      System.err.println("Move back to tower and upgrade it");
      Site towerSite = closestTowers.get(0);
      return me.moveTo(towerSite).then(towerSite::buildTower).end();
    } else {
      System.err.println("No Tower to move back");
    }
    
    return false;
  }

  private static boolean doDefensiveMove() {
    System.err.println("Defensive mode activated ... ");
    // defensive move, go back to tower !
    List<Site> closestTowers = getSiteByClosestDistance(me).stream()
        .filter(s -> { return s.imOwner() && s.isTower(); })
        .collect(Collectors.toList());
    
    
    if (!closestTowers.isEmpty()) {
      Site closestTower = closestTowers.get(0);
      Tower t = (Tower)closestTower.structure;
      if (!t.protects(me, 0)) {
        System.err.println("Move to closest tower because it doesnt protect us! ");
        return closestTower.moveTo();
      } else {
        // check if there is a second tower in viccinity to protects us more
        if (tryToMoveBetweenSecondClosestTower(closestTowers, closestTower)) {
          return true;
        }
        if (tryToBuildASecondTowerInProtectionRange(closestTower)) {
          return true;
        } else if (increaseTowerRange(closestTower)) {
          return true;
        }
      }
    } else {
      System.err.println("Will build a  tower near to defend me");
      List<Site> closestToMe = getSiteByClosestDistance(me);
      Site closestSite = closestToMe.get(0);
      return me.moveTo(closestSite).then(closestSite::buildTower).end();
    }
    return false; // TODO remove this and check all paths !
  }

  private static boolean increaseTowerRange(Site closestTower) {
    System.err.println("Increasing tower range ... ");
    return closestTower.buildTower();
  }

  private static boolean tryToBuildASecondTowerInProtectionRange(Site closestTower) {
    Tower t = (Tower)closestTower.structure;
    
    // site qui ne sont pas closestTower, mais qui sont dans son range
    Site site = sitesRenamed.stream()
        // not the current protecting tower 
      .filter(s -> { return s != closestTower;})
        // but protection range
      .filter(s -> { return s.pos.dist(closestTower.pos) < t.attackRadius + s.radius; } )
      .sorted(me::closest)
      .findFirst().orElse(null);
    
    if (site != null) {
      System.err.println("Will move & build on seconde site " + site.id);
      return me.moveTo(site).then(site::buildTower).end();
    }
      
    return false;
  }

  private static boolean tryToMoveBetweenSecondClosestTower(List<Site> closestTowers, Site closestTower) {
    Tower t = (Tower)closestTower.structure;
    if (closestTowers.size() >=2) {
      Site secondTowerSite = closestTowers.get(1);
      Tower t2 = (Tower)secondTowerSite.structure;
      if (secondTowerSite.pos.dist(closestTower.pos) < t.attackRadius + t2.attackRadius) { 
        // TODO move to the barycenter ???
        System.err.println("Move to second closest tower because we can be protected by both ! ");
        return secondTowerSite.moveTo();
      }
    } 
    return false;
  }

  private static int getEnnemyKnightAround(int range) {
    return (int)units.stream()
        .filter(unit -> unit.owner == 1)
        .filter(unit -> unit.isKnight())
        .filter(unit -> unit.pos.dist(me.pos) < range)
        .count();
  }

  private static Site getSite(int siteId) {
    List<Site> sList = sitesRenamed.stream().filter(s -> s.id == siteId).collect(Collectors.toList());
    if (sList.isEmpty()) {
      System.out.println("Cant find id " + siteId +" !!!!!!!");
      return null;
    } else {
      return sList.get(0);
    }
  }
}
