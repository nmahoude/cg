package coderoyale;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

import coderoyale.sites.Barrack;
import coderoyale.sites.Mine;
import coderoyale.sites.Site;
import coderoyale.sites.Structure;
import coderoyale.sites.Tower;
import coderoyale.units.Disk;
import coderoyale.units.Queen;
import coderoyale.units.Unit;

public class Player {
  
  private static final int KNIGHT_COST = 80;
  private static final int ARCHER_COST = 100;
  private static final int GIANT_COST  = 140;
  
  public static List<Site> allSites = new ArrayList<>();
  private static List<Tower> allTowers = new ArrayList<>();
  private static List<Barrack> barracks = new ArrayList<>();
  private static List<Mine> mines = new ArrayList<>();

  public static final int WIDTH = 1920;
  static final int HEIGHT = 1000;
  static int oppositeWallX;

  static Phase phase = Phase.EXPANSION;
  
  private static List<Unit> units;
  public static int turn = 0;
  public static Queen me = new Queen();
  public static Queen him = new Queen();
  private static int keepForKnightRush = 0;
  private static int myInitialLife = -1;
  
  public static void main(String args[]) {
    Scanner in = new Scanner(System.in);
    readPreGame(in);

    // game loop
    while (true) {
      readGameState(in, numSites);

      try {
        issueQueenCommand();
        issueNoCommand(); // if here there is no command
      } catch(CommandException ce) {
        ce.output();
      }
      issueTrainCommand();
    }
  }


  private static Site getClosestFree() {
    Site closestFree = null;
    for (Site site : allSites) {
      if (site.imOwner()) {
        continue;
      }
      if (closestFree == null ) {
        closestFree = site;
      } else if (site.imNotOwner() && closestFree.pos.dist2(me.pos) > site.pos.dist2(me.pos)) {
        closestFree = site;
      }
    }
    return closestFree;
  }

  private static void readPreGame(Scanner in) {
    numSites = in.nextInt();
    for (int i = 0; i < numSites; i++) {
      int siteId = in.nextInt();
      int x = in.nextInt();
      int y = in.nextInt();
      int radius = in.nextInt();
      Site site = new Site(siteId, x, y, radius);
      allSites.add(site);
    }
  }
  private static void readGameState(Scanner in, int numSites) {
    turn++;

    me.reset();
    him.reset();
    
    me.gold = in.nextInt();
    me.touchedSite = in.nextInt(); // -1 if none
    
    barracks.clear();
    allTowers.clear();
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
      site.updateGold(gold);
      site.maxMineSize = maxMineSize;
      
      Queen ownerQueen = owner == 0 ? me : him;
      
      if (structureType == Structure.BARRACK) {
        Barrack b= new Barrack(site);
        b.turnBeforeTrain = param1;
        b.subtype = param2; // KNIGHT, ... , GIANT
        site.structure = b;
        barracks.add(b);
        if (b.subtype == Structure.KNIGHT) {
          ownerQueen.knightBarracks.add(b);
        }
      } else if (structureType == Structure.TOWER){
        Tower t = new Tower(site);
        t.health = param1;
        t.attackRadius = param2;
        site.structure = t;
        allTowers.add(t);
        ownerQueen.towers.add(t);
      } else if (structureType == Structure.MINE) {
        Mine m = new Mine(site);
        m.incomeRate = param1;
        site.structure = m;
        mines.add(m);
        ownerQueen.mines.add(m);
      }  else {
        site.structure = Structure.NONE;
      }
      site.structure.owner = owner;
    }
    
    inferMissingGold();
    
    int numUnits = in.nextInt();
    units = new ArrayList<>();
    for (int i = 0; i < numUnits; i++) {
      int x = in.nextInt();
      int y = in.nextInt();
      int owner = in.nextInt();
      int unitType = in.nextInt(); // -1 = QUEEN, 0 = KNIGHT, 1 = ARCHER
      int health = in.nextInt();
      Unit unit;
      
      Queen ownerQueen = owner == 0 ? me : him;
      if (unitType == -1 && owner == 0) {
        if (myInitialLife == -1) myInitialLife = health; 
        me.updatePos(x, y);
        unit = me;
      } else if (unitType == -1 && owner == 1) {
        him.updatePos(x, y);
        unit = him;
      } else {
        unit = new Unit(x, y, unitType);
        unit.owner = owner;
        unit.updatePos(x, y);
        ownerQueen.creeps.add(unit);
      }
      unit.health = health;
      units.add(unit);
    }
    
    me.calculateFrontierPosition();
    him.calculateFrontierPosition();
  }

  private static void inferMissingGold() {
    for (int i=0;i<numSites;i=i+2) {
      Site s1 = getSite(i);
      Site s2 = getSite(i+1);
      if (s1.gold == -1 && s2.gold != -1) s1.updateGold(s2.gold);
      if (s2.gold == -1 && s1.gold != -1) s2.updateGold(s1.gold);
    }
  }


  private static void issueTrainCommand() {
    String train = "TRAIN ";
    List<Site> closestToHim = getSiteByClosestDistance(him).stream().filter( s -> {
        return s.imOwner() && s.canTrain();
    }).collect(Collectors.toList());
    int gold = me.gold;
    boolean needGiant = needGiant();
    if (needGiant && gold > GIANT_COST) {
        for (Site site : closestToHim) {
            if (site.isGiant()) {
                train += site.id + " ";
                gold -= GIANT_COST;
                needGiant = false;
            }
        }
    }
    // only spend money on knight if we dont need a giant ...
    if (!needGiant) {
        keepForKnightRush = calculateKeepForKnightRush();
        if (keepForKnightRush <= 1) {
            System.err.println("Build as many knight as possible ....");
            for (Site site : closestToHim) {
                if (gold < KNIGHT_COST)
                    break;
                train += site.id + " ";
                gold -= KNIGHT_COST;
            }
        } else {
            if (gold >= keepForKnightRush * KNIGHT_COST) {
                for (Site site : closestToHim) {
                    if (gold < KNIGHT_COST)
                        break;
                    train += site.id + " ";
                    gold -= KNIGHT_COST;
                }
            }
        }
    } else {
        System.err.println("not spending for giant");
    }
    System.out.println(train.trim());
  }

  private static int calculateKeepForKnightRush() {
    if (him.onHomeSide(him.pos.x) && him.towers.size() > 3) {
      System.err.println("keep gold for 2 waves");
      return 2; 
    }
    return 0;
  }

  private static boolean needGiant() {
    // TODO when do we need a giant ?
    boolean giantExists = units.stream()
        .filter(u -> u.owner == me.owner)
        .filter(Unit::isGiant)
        .count() > 0;
    boolean giantBarrackExists = allSites.stream()
        .filter(Site::imOwner)
        .filter(Site::isGiant)
        .count() > 0;
        return giantBarrackExists && !giantExists ;
  }

  private static void issueQueenCommand() {
    Site closestFree = getClosestFree();
    if (myInitialLife  <= 25) {
      rushWithLowInitialLife();
    }
    //initialRush();
    fleeFromCreeps();
    takeCareOfTowers(200);
    fleeFromTowers();
    stabilisation();
    takeCareOfTowers(600);
    doDefensiveMoves();
    //buildSomeInitialBarracks();
    buildSomeInitialMines();
    doGiantInitiative();
    doOtherMoves(closestFree);
    
    exploitation();
    
    upgradeClosestTower(); // last resort

    return;
  }

  private static void exploitation() {
    System.err.println(" --- exploitation ---");
    //look in our side if there is management to do ...

    // find barracks not used and replace them by mine or tower
    List<Site> notUsedBarracks = getSiteByClosestDistance(him).stream()
        .filter(s -> s.isBarrack())
        .filter(s -> s.imOwner())
        .collect(Collectors.toList())
        ;

    if (notUsedBarracks.size() >= 2) {
      // 1st is the closest, go replace the other ...
      notUsedBarracks.remove(notUsedBarracks.get(0));
      Site toChange = notUsedBarracks.stream().sorted(me::closest).findFirst().orElse(null);
      if (toChange.hasGold()) {
        me.moveTo(toChange).then(toChange::buildMine).then(toChange::upgradeMine).end();
      } else {
        me.moveTo(toChange).then(toChange::buildTower).then(toChange::upgradeTower).end();
      }
    }
  }

  private static void rushWithLowInitialLife() {
    // quickly build a barrack and a tower
    
    finishNearMineUpgrade();
    
    List<Site> sites= getSiteByClosestDistance(me).stream()
        .filter(s -> s.structure.owner == -1)
        .collect(Collectors.toList());

    Site site = sites.get(0);
    if (me.mines.size() < 2 ) {
      me.moveTo(site).then(site::buildMine).end();
    }
    
    if (me.knightBarracks.size() == 0) {
      me.moveTo(site).then(site::buildKnightBarrack).end();
    }
    if (me.towers.size() == 0) {
      me.moveTo(site).then(site::buildTower).end();
    }
  }


  private static void fleeFromCreeps() {
    
    //new DamageEvaluator().evaluateDamageInFuture(me.pos, me.pos);
    new DamageEvaluator().findFleeSolution();
    
    // TODO When creeps are near, 
    // we have to flee or turn around any tower to save some life
    // the better would be to flee toward an unused structure to prepare for next move
    
    // what are the conditions to flee ?
    // 1. the creeps will eventually catch us (ie : they wont be dead before ...)
    // 2. we are not building a fancy new tower to kill them ?
    // 3. they dont push us in the ennemy territory ....
    // 4. it won't derails us from a good strategy (ie : we could exchange a little bit of life for a good move)
    
    
    // What is the strategy to flee
    // 1. build a flee vector from all the creeps nearby to get a general direction
    // 2. try to find a target in or near this direction (ie : a tower, a free Site ?)
    // 3. dont run into ennemy towers ...
    // 4. don't flee towards mines (unless it's almost depleted or they wont reach it)
    // 5. beware of walls ...
    
    // What functionnality I need :
    // 1. how much time a creep will outlive the towers ?
    // 2. basic math to chekc for Site and vector collision/distance
    
  }

  
  

  /**
   *  try to stabilize the game when there is enough security
   *  low creeps number, a barrier of towers, etc ...
   */
  private static void stabilisation() {
    System.err.println("--- stabilisation");
    if (me.towers.size() < 4) {
      return; // too few towers to try to stabilize
    }

    List<Site> sites = getSiteByClosestDistance(me);

    // check if there is still empty sites behind the frontier (better attacks this space!)
    for (Site site : sites) {
      if (site.noBuilding() && !site.inTerritoryOf(me) && !site.inTerritoryOf(him)) {
        System.err.println("Still room for expansion ...");
        
        return; // don't try to stabilise if there is still room to expand
      }
    }
    
    // try to build mine & barracks if needed
    for (Site site : sites) {
      if (site.noBuilding() && site.inTerritoryOf(me)) {
        // build a mine if possible
        moveToSiteAndBuildMine(site);
        
        // build a barrack if needed
        moveToSiteAndBuildBarracks(site, false);
        
        // build a giant if needed
        // TODO find a good spot to build giant ?
        // buildGiantBarracks(site);
      }
    }
 /** all building behind our line of 'defense' (frontier) are built
 / check if we can destroy some tower ?
 */
    for (Site site : sites) {
      if (site.isTower() && site.inTerritoryOf(me)) {
        moveToSiteAndBuildMine(site);
        moveToSiteAndBuildBarracks(site, false);
      }
    }
    
  }

  private static void buildGiantBarracks(Site site) {
    if (allSites.stream()
        .filter(Site::imOwner)
        .filter(Site::isGiant)
        .count() == 0) {
      me.moveTo(site).then(site::buildGiant).end();
    }
  }

  private static void fleeFromTowers() {
    List<Site> hisTowerSites = allSites.stream()
        .filter(Site::isTower)
        .filter(Site::imNotOwner)
        .collect(Collectors.toList());
    int underAttacks = 0;
    for (Site st : hisTowerSites) {
      if (st.getTower().protects(me, 0)) {
        if (!doTowerNeedToKillCreeps(me)) {
          underAttacks++;
        }
      }
    }
    if (underAttacks > 0) {
      System.err.println("I'm in enemy territory ("+underAttacks+")");
      flee();
    }
  }

  private static boolean doTowerNeedToKillCreeps(Queen queen) {
    for (Unit unit : units) {
      if (unit.owner == queen.owner && unit.isKnight()) {
        return true;
      }
    }
    return false;
  }

  // flee from close enemy creeps
  private static void flee() {
    List<Unit> enemyUnits = units.stream()
        .filter(u -> u.owner == 1)
        .collect(Collectors.toList());
    List<Site> hisTowers = allSites.stream()
          .filter(Site::isTower)
          .filter(Site::imNotOwner)
          .filter(s -> { return s.getTower().protects(me, 0);})
          .collect(Collectors.toList());
    
    if (enemyUnits.size() == 0 && hisTowers.size() == 0) {
      return; // no need to flee ....
    }
    
    // build a flee vector
    CRVector flee = new CRVector();
    System.err.println("Building flee vector");
    for (Unit unit : enemyUnits) {
      double dist = unit.pos.dist(me.pos);
      CRVector invDir = unit.pos.direction(me.pos).normalize();
      CRVector contribution = invDir.mult(1.0 / (dist * dist)); // plus c'est loin moins ca compte
      flee = flee.add(contribution);
    }
    
    for (Site site : hisTowers) {
      Tower t = site.getTower();
      if (t.protects(me, 60)) {
        // the tower can harm me, 
        double dist = site.pos.dist(me.pos);
        CRVector invDir = site.pos.direction(me.pos).normalize();
        System.err.println("dist : "+dist + " invDir : "+invDir);
        CRVector contribution = invDir.mult(1.0 / (dist * dist)); // plus c'est loin moins ca compte
        System.err.println("site "+ site + " contibution : "+ contribution);
        flee = flee.add(contribution);
      }
    }
    flee = flee.normalize().mult(60);
    System.err.println("Flee Vector : " + flee);
    me.moveTo(me.pos.add(flee)).end();
    
  }
  private static void blitz() {
    // transform every tower in barracks
    List<Site> sites = getSiteByClosestDistance(me).stream()
        .filter(s -> ! (s.imNotOwner() && s.isTower()))
        .filter(s -> ! (s.imOwner() && s.isBarrack()))
        .collect(Collectors.toList());
    
    if (!sites.isEmpty()) {
      Site site = sites.get(0);
      moveToSiteAndBuildMine(site);
      if (!site.isAMine()) {
        me.moveTo(site).then(site::buildKnightBarrack).end();
      }
    }
  }

  private static boolean needToBlitz() {
    if (turn < 150) return false;
    if (me.health > him.health) return false;
    System.err.println("BLITZ !");
    return true;
  }

  private static void buildSomeInitialBarracks() {
    if (barracks.stream().filter(b -> { return b.owner == me.owner;}).count() == 0) {
      List<Site> sites = getSiteByClosestDistance(me);
      Site site = sites.get(0);
      me.moveTo(site).then(site::buildKnightBarrack).end();
    }
  }

  static boolean initialRush = true;
  static Site rushSite  = null;
  private static int numSites;
  private static void initialRush() {
    if (!initialRush) return;
    System.err.println("STILL IN RUSH");
    
    if (rushSite == null) {
      // find the farthest site that is closest to me than him
      double farther = 0;
      Site best = null;
      for (Site site : allSites) {
        double dist = site.pos.dist(me.pos);
        if (dist > farther && dist < site.pos.dist(him.pos)) {
          farther = dist;
          best = site;
        }
      }
      rushSite = best;
      System.err.println("Deciding rush site " + rushSite);
    }
    
    me.moveTo(rushSite)
          .then(rushSite::buildTower)
          .then(rushSite::upgradeTower)
          .end();
    System.err.println("finishing rush");
    initialRush = false;    
  }

  private static void takeCareOfTowers(int energyThreshold) {
    System.err.println("Take care of towers ... ");
    if (him.creeps.size() == 0) {
      System.err.println("No creep, let the tower decay ...");
      return;
    }
    
//    Site nearSite = getSiteByClosestDistance(me).get(0);
//    if (!nearSite.imOwner() 
//          && !nearSite.isNotBuildable(me) 
//          && nearSite.pos.dist(me.pos) < 20 + nearSite.radius + me.radius) {
//      System.err.println("Ok, I'm near a build site, let it finish before refilling");
//      return;
//    }
    
    List<Site> towers = getSiteByClosestDistance(me).stream()
        .filter(Site::imOwner)
        .filter(Site::isTower)
        .collect(Collectors.toList());
    
    if (towers.isEmpty()) {
      return;
    }
    for (int i=0;i<Math.min(2, towers.size());i++) {
      Site site = towers.get(i);
      Tower tower = (Tower)(site.structure);
      if (isProtectedByEnnemyTowers(site)) {
        continue;
      }
      
      if (tower.health < energyThreshold) {
        System.err.println("Refill tower " + site + "because energy "+ tower.health +" is less than "+ energyThreshold);
        me.moveTo(site).then(site::upgradeTower).end();
      }
    }
  }

  private static void doDefensiveMoves() {
    int maxRange = 600;
    int ennemyKnightAround = getEnnemyKnightAround(maxRange);
    System.err.println("Ennemy knights around : "+ ennemyKnightAround);
    if (ennemyKnightAround > 2) {
      doDefensiveMove();
    }
  }

  private static boolean doGiantInitiative() {
    // check if we need to do a giant barracks & build giant
    long ennemyTowers = allTowers.stream()
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

  private static boolean buildSomeInitialMines() {
    System.err.println("--- buildSomeInitialMines");

    System.err.println("Dealing with mines ...");
    List<Mine> myMines = mines.stream()
        .filter(mine -> {return mine.owner == 0;})
        .collect(Collectors.toList());

    int minedGoldPerTurn = myMines.stream()
        .filter(mine -> mine.hasGold())
        .map( mine -> mine.incomeRate)
        .reduce(0, Integer::sum);

    // check if we are near an upgradable mine first !
    finishNearMineUpgrade();
    
    System.err.println("I'm currently mining " + minedGoldPerTurn);
    if (minedGoldPerTurn < 4) {
      System.err.println("Still got to add some mines ...");
      List<Site> sites= getSiteByClosestDistance(me).stream()
                            .filter(s -> !s.isTower())
                            .filter(s -> s.imNotOwner() || s.noBuilding())
                            .filter(s -> { return s.imNotOwner() || !s.maxMined(); })
                            .collect(Collectors.toList());
      for (Site site : sites) {
        if (site.maxMineSize == 1 && me.knightBarracks.size() == 0) {
          System.err.println("Not a good mine and need some barracks, so will build on it");
          me.moveTo(site).then(site::buildKnightBarrack).end();
        }
        
        if (isProtectedByEnnemyTowers(site)) {
          System.err.println("Won't build in tower protected site: "+site);
        } else {
          moveToSiteAndBuildMine(site);
          System.err.println("Site " + site.id + " is not eligeable");
        }
      }
    }
    return false;
  }

  private static boolean finishNearMineUpgrade() {
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

  private static List<Site> getSiteByBarycenter(Disk unit) {
    int positions = 1;
    Pos barycenter = new Pos();
    barycenter.x = me.pos.x;
    barycenter.y = me.pos.y;
    
    for (Site site : allSites) {
      if (site.imOwner()) {
        positions++;
        barycenter.x += site.pos.x;
        barycenter.y += site.pos.y;
      }
    }
    barycenter.x /= positions;
    barycenter.y /= positions;
    
    
    return allSites.stream()
        .sorted((s1, s2) -> Double.compare(s1.pos.dist(barycenter), s2.pos.dist(barycenter)))
        .collect(Collectors.toList());
  }
  
  private static List<Site> getSiteByClosestDistance(Disk unit) {
    return allSites.stream()
        .sorted((s1, s2) -> Double.compare(s1.pos.dist(unit.pos), s2.pos.dist(unit.pos)))
        .collect(Collectors.toList());
  }
  
  private static boolean moveToSiteAndBuildMine(Site site) {
    if (site.isAMine() && site.maxMined()) {
      return false; 
    }
    if (getEnnemyKnightAround(60) > 0) {
      System.err.println("Won't build a mine with ennemy around ...");
      return false;
    }
    if (!site.hasGold()) {
      System.err.println("No more gold in " + site);
      return false;
    }
    
    System.err.println("Move and build MINE to site "+ site.id);
    return me.moveTo(site).then(site::buildMine).then(site::upgradeMine).end();
  }

  private static boolean doOtherMoves(Site closestFree) {
    buildNewBarracks(closestFree);
    
    // TODO reput barycenter ?
    List<Site> closestAvailableSites = getSiteByClosestDistance(me).stream()
        .filter( s -> {return s.imNotOwner() && !s.isTower();})
        .collect(Collectors.toList());

    buildNewTowers(closestAvailableSites);
    buildNewMineOrBarracks(closestAvailableSites);
    
    
    //TODO Here, THERE IS A LOT TO DO
    //  - if we are endanger, we may goback near tower and (re)power them
    //  - we may want to build outpost to get nearest the ennemy (faster waves of creeps)
    //  - we may want to build new towers to increase protection
    
    return false;
  }

  private static void upgradeClosestTower() {
    List<Site> closestTowers = getSiteByClosestDistance(me).stream()
        .filter(s -> { return s.imOwner() && s.isTower(); })
        .collect(Collectors.toList());
    
    if (!closestTowers.isEmpty()) {
      System.err.println("Move back to tower and upgrade it");
      Site towerSite = closestTowers.get(0);
      System.err.println("Towerlife is " + towerSite.getTower().health);
      if (towerSite.getTower().health > 700) {
        return;
      }
      if (!isProtectedByEnnemyTowers(towerSite)) {
        me.action(towerSite::moveTo).then(towerSite::buildTower).then(towerSite::upgradeTower).end();
      } else {
        System.err.println("Protected by enemy towers, we should do something ?");
      }
    } else {
      System.err.println("No Tower to move back");
    }
    
//    for (Site towerSite : closestTowers) {
//      if (!isProtectedByEnnemyTowers(towerSite)) {
//        System.err.println("Move back to tower and upgrade it");
//
//        me.action(towerSite::moveTo)
//               .then(towerSite::buildTower)
//               .then(towerSite::upgradeTower)
//               .end();
//      }
//    }
  }

  private static void buildNewMineOrBarracks(List<Site> closestAvailableSites) {
    System.err.println("--- buildNewMineOrBarracks");
    for (Site site : closestAvailableSites) {
      if (isProtectedByEnnemyTowers(site)) {
        System.err.println("Won't build in tower protected site: "+site);
        continue;
      } else if (isEnnemyTerritory(site)) {
        System.err.println("Won't build in ennermy territory site: "+site);
        continue;
      }
      
      if (priorityToBarrack(site)) {
        System.err.println("Priority building  barracks on " + site);
        moveToSiteAndBuildBarracks(site, true);
      }

      System.err.println("Try to build mine on " + site);
      moveToSiteAndBuildMine(site);

      System.err.println("Try to build barracks on " + site);
      moveToSiteAndBuildBarracks(site, false);
    }
  }

  private static boolean priorityToBarrack(Site site) {
    if (true) {
      double bestDist = Double.POSITIVE_INFINITY;
      for (Barrack b : me.knightBarracks) {
        double dist = b.attachedTo.pos.dist(him.pos);
        System.err.println("barrack "+b.attachedTo+" dist to queen : "+dist);
        if (dist < bestDist) {
          bestDist = dist;
        }
      }
      System.err.println("best dist to queen : " + bestDist);
      double siteDist = site.pos.dist(him.pos);
      System.err.println("potential site "+ site + " : "+ siteDist);
      if (siteDist < bestDist - 500) {
        return true;
      } else {
        // no ROI 
        return false;
      }
    }
    return false;
  }


  private static boolean checkBeforeBuildingMineOn(Site site) {
    List<Site> closeToHim = getSiteByClosestDistance(him);
    for (Site free : closeToHim) {
      if (free.isNotBuildable(me)) continue;
      if (isProtectedByEnnemyTowers(free)) continue;
      if (free ==  site) {
        System.err.println("Won't build mine, it's the front site");
        return false;
      } else {
        System.err.println("Site "+ free + " is a better front");
        return true;
      }
    }
    return true;
  }


  private static boolean isEnnemyTerritory(Site site) {
    return site.inTerritoryOf(him);
  }

  private static void moveToSiteAndBuildBarracks(Site site, boolean force) {
    // dont go in back lines and build barracks if sufficient well placed barracks already exists !
    // we may force if we want to build a barracks because we will destroy the others soon (for mine)
    if (!force) {
      for (Barrack b : me.knightBarracks) {
        if (b.attachedTo.pos.dist2(him.pos) < site.pos.dist2(him.pos)) {
          System.err.println("Barracks with better place exists");
          return;
        }
      }
    }
    
    me.moveTo(site).then(site::buildKnightBarrack).end();
  }

  private static boolean isProtectedByEnnemyTowers(Site site) {
    for (Tower tower : him.towers) {
      if (tower.protects(site.pos, -site.radius)) {
        // check for my creeps
        boolean occupee = false;
//        for (Unit creep : me.creeps) {
//          if (tower.protects(creep.pos, 0)) {
//            occupee = true;
//            break;
//          }
//        }
        if (occupee) {
          System.err.println("Il y a du boulot pour la tour " + tower.attachedTo.id);
        } else {
          return true;
        }
      }
    }
    return false;
  }

  
  private static void buildNewTowers(List<Site> closestAvailableSites) {
    if (!closestAvailableSites.isEmpty()) {
      if (!towerCoverMap(me)) {
        Site site = closestAvailableSites.get(0);
        System.err.println("Try to build tower on " + site);
        me.moveTo(site).then(site::buildTower).then(site::upgradeTower).end();
      }
    }
  }
  
  private static void buildNewBarracks(Site closestFree) {
    long myBarracksCount = barracks.stream()
        .filter(Structure::isMine)
        .count();

    if (myBarracksCount == 0) {
      System.err.println("Building first barracks ....");
      moveToSiteAndBuildBarracks(closestFree, true);
    }
  }

  /**
   * Returns whether our towers build a WALL between opponent and ourself 
   */
  private static boolean towerCoverMap(Queen me) {
    // TODO really check map coverage vs creeps barracks ...
    long myTowers = allTowers.stream()
        .filter(u -> u.owner == me.owner)
        .count();
    return myTowers > 3;
  }

  private static boolean doDefensiveMove() {
    System.err.println("Defensive mode activated ... ");
    // defensive move, go back to tower !
    List<Site> closestTowers = getSiteByClosestDistance(me).stream()
        .filter(s -> { return s.imOwner() && s.isTower(); })
        .collect(Collectors.toList());
    
    int protection = 0;
    for (Site towerSite : closestTowers) {
      Tower tower = towerSite.getTower();
      if (tower.protects(me, 0)) {
        protection++;
      } else {
      }
    }
    if (protection >= 4 ) {
      System.err.println("current tower protection " + protection + " not going anywhere");
      return false;
    }
    
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
    Site site = allSites.stream()
        // not the current protecting tower 
      .filter(s -> { return s != closestTower;})
        // but protection range
      .filter(s -> { return s.pos.dist(closestTower.pos) < t.attackRadius + s.radius; } )
      .filter(s -> (s.imNotOwner() && !s.isTower()) || s.noBuilding())
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
      if (t2.protects(me, 0)) {
        // it already protects me, no need to go there
        return false; 
      }
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

  static Site getSite(int siteId) {
    List<Site> sList = allSites.stream().filter(s -> s.id == siteId).collect(Collectors.toList());
    if (sList.isEmpty()) {
      System.out.println("Cant find id " + siteId + " !!!!!!!");
      return null;
    } else {
      return sList.get(0);
    }
  }
  private static void issueNoCommand() {
    System.err.println("WARNING : NO COMMAND ISSUED");
    System.out.println("WAIT");
  }

}
