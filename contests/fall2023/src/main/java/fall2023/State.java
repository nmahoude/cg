package fall2023;

import java.util.ArrayList;
import java.util.List;

import fast.read.FastReader;

public class State {
  public static int turn = 0;
  public static int agentId = 0;

  public static int creatureStart = 10000;
  public static int creatureCount;
  public static int uglyCount;
  public static int fColor[] = new int[30];
  public static int fType[] = new int[30];
  public static String initPacked = null;

  public static boolean[] canBeInitial = new boolean[30]; // false be default, only set to true in Player
  public static boolean[] neverSawFish = new boolean[30]; // false be default, only set to true in Player
  
  
  public int myScore;
  public Scan myScans = new Scan();
  
  public int oppScore;
  public Scan oppScans = new Scan();
  public Drone[] dronesById = new Drone[] { new Drone(0), new Drone(1), new Drone(2), new Drone(3) };
  public Drone[] myDrones = new Drone[2];
  public Drone[] oppDrones = new Drone[2];
  public List<Fish> fishes = new ArrayList<>();
  
  public boolean[] fishPresent = new boolean[30];
  Zone currentTriangulations[] = new Zone[30];

  public State() {
    for (int i=0;i<30;i++) {
      currentTriangulations[i] = new Rectangle();
      fishPresent[i] = true;
    }
    
    // default values
    myDrones[0] = dronesById[0];
    myDrones[1] = dronesById[2];
    
    oppDrones[0] = dronesById[1];
    oppDrones[1] = dronesById[3];
  }
  
  
  public void copyFrom(State model) {
    this.previousState = model.previousState;
    this.myScore = model.myScore;
    this.oppScore = model.oppScore;
    this.myScans.copyFrom(model.myScans);
    this.oppScans.copyFrom(model.oppScans);
    
    for (int i=0;i<4;i++) {
      this.dronesById[i].copyFrom(model.dronesById[i]);
    }
    for (int i=0;i<2;i++) {
      this.myDrones[i] = this.dronesById[model.myDrones[i].id];
      this.oppDrones[i] = this.dronesById[model.oppDrones[i].id];
    }
    
    fishes.clear();
    for (Fish f : model.fishes) {
      Fish fn = new Fish(-1);
      fn.copyFrom(f);
      this.fishes.add(fn);
    }
    
    for (int i=4;i<16+uglyCount;i++) {
      this.currentTriangulations[i].copyFrom(model.currentTriangulations[i]);
      this.fishPresent[i] = model.fishPresent[i];
    }
  }
  
  
  public static void readInit(FastReader in) {
    creatureCount = in.nextInt();
    uglyCount = 0;
    
    //__err("^ "+creatureCount);
    initPacked = "^ "+creatureCount+" ";
    for (int i = 0; i < creatureCount; i++) {
        int creatureId = in.nextInt();
        creatureStart = Math.min(creatureStart, creatureId);
        int color = in.nextInt();
        int type = in.nextInt();
        if (color == -1) {
          color = 4;
          type = Fish.UGLY;
          uglyCount++;
        } 
        fColor[creatureId] = color;
        fType[creatureId] = type;
        
        int packed = creatureId + 100 * color + 1000 * type;
        initPacked+= packed+" ";
        //__err("^ "+creatureId+" "+color+" "+type);
    }
    // will be output by the turn ! __err(initPacked);
  }

  public static void readPackedInit(FastReader in) {
    creatureCount = in.nextInt();
    uglyCount = 0;
    for (int i = 0; i < creatureCount; i++) {
      int packed = in.nextInt();
      int creatureId = packed % 100;
      creatureStart = Math.min(creatureStart, creatureId);
      int color = (packed % 1000) / 100;
      int type = packed % 10000 / 1000;
      fColor[creatureId] = color;
      fType[creatureId] = type;
      initPacked+= packed+" ";
      
      if (fType[creatureId] == Fish.UGLY) {
        uglyCount++;
      }
      //__err("^ "+creatureId+" "+color+" "+type);
    }
    System.err.println(initPacked);
    
  }

  
  
  void updateFromPreviousState(List<Fish> discoveredFishes) {
    if (State.turn > 0) {
      State previousStateWork = new State();
      previousStateWork.copyFrom(this.previousState);
      
      
      // retrofit actions
      Action previousActions[] = new Action[] { new Action(), new Action(), new Action(), new Action() };
      for (Drone d : this.dronesById) {
        previousActions[d.id].dx = d.pos.x - previousStateWork.dronesById[d.id].pos.x;
        previousActions[d.id].dy = d.pos.y - previousStateWork.dronesById[d.id].pos.y;
        previousActions[d.id].lamp = (d.battery == previousStateWork.dronesById[d.id].battery -5);
      }
      
      
      // simulate the turn
      Simulator sim = new Simulator();
      sim.apply(previousStateWork, previousActions);
      previousStateWork.fishes.addAll(discoveredFishes);
      
      if (Player.DEBUG_SIMULATION_UPDATE) {
        System.err.println("Debug after simulation ....");
        for (Drone d: previousStateWork.dronesById) {
          System.err.println(d);
        }
        for (Fish f : previousStateWork.fishes) {
          System.err.println(f);
        }
        System.err.println(" End of debug sim");
      }
    
      // add missing fishes from simulation & !
      for (Fish f : previousStateWork.fishes) {
        if (this.fishes.contains(f)) continue;

        // check if predicted position is valid in regards of drone lights
        boolean predictionPossible = true;
        
        if (!this.fishPresent[f.id]) predictionPossible = false;
        if (!previousStateWork.fishPresent[f.id]) predictionPossible = false; // should have be killed by a push ?
        if (!currentTriangulations[f.id].contains(f.pos)) predictionPossible = false;
        
        // if I don't see the fish and the fish should be seen, discard it
        for (Drone d : this.myDrones) {
          int radius2 = hadLights(d.id) ? Drone.BIG_LIGHT_RADIUS2 : Drone.NORMAL_LIGHT_RADIUS2;
          
          if (!d.currentScans.contains(f.id) && f.pos.dist2(d.pos) < radius2) {
            System.err.println("Bad prediction for "+f+" and drone "+d+" !");
            predictionPossible = false;
          }
        }
        
        // if opp scanned a fish and it was not in opp's radius, then discard it
        for (Drone opp : this.oppDrones) {
          if (!scannedFishFomLastTurn(opp, f.id)) continue; 
          
          int radius2 = hadLights(opp.id) ? Drone.BIG_LIGHT_RADIUS2 : Drone.NORMAL_LIGHT_RADIUS2;
          
          if (f.pos.dist2(opp.pos) > radius2) {
            System.err.println("Bad prediction for "+f+" and drone "+opp+" ! (scanned it but fictive pos was not in its radius");
            predictionPossible = false;
          }
        }
        
        
        if (predictionPossible) {
          if (Player.DEBUG_SIMULATION_UPDATE) System.err.println("Re-adding "+f);
          this.fishes.add(f);
        } else {
          if (Player.DEBUG_SIMULATION_UPDATE) System.err.println("prediction is proven wrong, resetting fish "+f);
        }
      }
    }
  }


  private boolean scannedFishFomLastTurn(Drone drone, int fishId) {
    if (previousState == null) return this.dronesById[drone.id].currentScans.contains(fishId);
    
    // not here previous turn, but here now
    return !previousState.dronesById[drone.id].currentScans.contains(fishId) && this.dronesById[drone.id].currentScans.contains(fishId);
  }


  public void read(FastReader in) {
    
    init();
    readScore(in);
    readAlreadyScans(in); 
    readDrones(in);
    readCurrenScans(in);
    readFishes(in);
    readBlips(in);
    
    List<Fish> newFishes = updateFromSymetry();
    updateFromPreviousState(newFishes);
//    fillMasters();
    __err("*** END ***");
  }


  
  
  /**
   * when nobody disturbs fishes, they remains symmetrical wrt x=5000
   */
  List<Fish> updateFromSymetry() {
    if (Player.DEBUG_CHECK_SYMMETRY) System.err.println("@@@@@@ CHECK SYMMETRY");
    
    // check the visible fishes
    List<Fish> newFishes = new ArrayList<>();
    for (Fish f : fishes) {
      if (!neverSawFish[f.id]) continue;
      
      neverSawFish[f.id] = false; 
      canBeInitial[f.id] = false;
      
      if (hasAlreadyPotentiallyMoved(f)) continue;
      
      if (Player.DEBUG_CHECK_SYMMETRY) System.err.println("First time saw "+f);
      int alteregoId = f.id % 2 == 0 ? f.id+1 : f.id -1;

      if (!neverSawFish[alteregoId]) continue;
      
      Fish alterFishEgo = new Fish(alteregoId);
      alterFishEgo.pos.set(Simulator.WIDTH - f.pos.x, f.pos.y);
      if (isInFearRadius(alterFishEgo)) {
        alterFishEgo.speed.set(0,0); // can't know the actual speed
      } else {
        alterFishEgo.speed.set(-f.speed.vx, -f.speed.vy);
      }
      
      if (!hasAlreadyPotentiallyMoved(alterFishEgo)) {
        System.err.println("Ok for alter ego "+alteregoId);
        currentTriangulations[alteregoId].exact(f.pos.x, f.pos.y); // symetry juste dériere
        ((Rectangle)currentTriangulations[alteregoId]).vSymmetry();
        
        if (Player.DEBUG_CHECK_SYMMETRY) System.err.println("New rect for alter ego "+currentTriangulations[alteregoId]);
        canBeInitial[alteregoId] = false;
        if (getFishById(alteregoId) == null) {
          if (Player.DEBUG_CHECK_SYMMETRY) System.err.println("Create a new fish for "+alteregoId);
          newFishes.add(alterFishEgo);
        }
      }
    }
    
    // update triangulations
    for (int f=4;f<16+uglyCount;f+=2) {
      boolean canDoSymmetry = true;
      if (State.turn > 3 && fType[f] == 0) canDoSymmetry = false;
      if (State.turn > 7 && fType[f] == 1) canDoSymmetry = false;
      if (State.turn > 11 && fType[f] == 2) canDoSymmetry = false;
      if (State.turn > 3 && fType[f] == Fish.UGLY) canDoSymmetry = false;
      
      if (canDoSymmetry) {
        if (Player.DEBUG_CHECK_SYMMETRY) System.err.println("Doing symmetry between "+f+" and "+(f+1));
        Rectangle rect;
        rect = new Rectangle((Rectangle)currentTriangulations[f]);
        rect.vSymmetry();
        currentTriangulations[f+1].intersect(rect);
        
        rect = new Rectangle((Rectangle)currentTriangulations[f+1]);
        rect.vSymmetry();
        currentTriangulations[f].intersect(rect);
        
        if (Player.DEBUG_CHECK_SYMMETRY) System.err.println("new rect for "+f+" => "+currentTriangulations[f]);
        if (Player.DEBUG_CHECK_SYMMETRY) System.err.println("new rect for "+(f+1)+" => "+currentTriangulations[f+1]);
      } else {
        // TODO more precise : check intersection boundary with drones
        if (fType[f] != Fish.UGLY) {
          if (Player.DEBUG_CHECK_SYMMETRY) System.err.println(" Won't be able to do symmetry with "+f);
          canBeInitial[f] = false;
        }
      }
    }
    return newFishes;
  }


  private boolean hasAlreadyPotentiallyMoved(Fish f) {
    State current = this.previousState;
    while (current != null) {
      if (fType[f.id] != Fish.UGLY && current.isInFearRadius(f)) return true;
      if (fType[f.id] == Fish.UGLY && current.isInAttractionRadius(f)) return true;
      current = current.previousState;
    }
    
    return false;
  }


  private boolean isInAttractionRadius(Fish f) {
    for (Drone d: dronesById) {
      if (d.pos.dist2(f.pos) < Drone.NORMAL_LIGHT_RADIUS) return true;
      if (hadLights(d.id) && d.pos.dist2(f.pos) < Drone.BIG_LIGHT_RADIUS) return true;
    }
    return false;
  }


  /**
   * is in fear radius of a drone
   * @param f
   * @return
   */
  private boolean isInFearRadius(Fish f) {
    for (Drone d: dronesById) {
      if (d.pos.dist2(f.pos) < Fish.FISH_HEARING_RANGE2) return true;
    }
    return false;
  }


  private void readBlips(FastReader in) {
    __err("--- Blips --- ");
    int radarBlipCount = in.nextInt();
    __err("^ "+radarBlipCount);
    
    for (int i=4;i<16;i++) {
      fishPresent[i] = false;
    }
    
    System.err.print("^ ");
    for (int i = 0; i < radarBlipCount; i++) {
        int droneId = in.nextInt();
        
        int creatureId = in.nextInt();
        int quadrant = radarToQuadran(in.nextChars());
        
        int packed = creatureId + 100 * quadrant + 1000 * droneId;
    
        fishPresent[creatureId] = true;
        dronesById[droneId].blips.add(new Blip(creatureId, quadrant));
        
        updateTriangulation(dronesById[droneId], currentTriangulations[creatureId], quadrant);
        currentTriangulations[creatureId].intersect(Rectangle.byTypes[fType[creatureId]]);
        
        // __err("^ "+droneId+" "+creatureId+" "+in.nextChars());
        System.err.print(packed+" ");
    }
    System.err.println();

  }

  Rectangle work = new Rectangle();
  public State previousState;
  
  public boolean updateTriangulation(Drone d, Zone current, int quadrant ) {
    switch (quadrant) {
    case 0:
      work.set(d.pos.x, 0, 10000-d.pos.x, d.pos.y);
      break;
    case 1:
      work.set(d.pos.x, d.pos.y, 10000-d.pos.x, 10000-d.pos.y);
      break;
    case 2:
      work.set(0, d.pos.y, d.pos.x, 10000-d.pos.y);
      break;
    case 3:
      work.set(0, 0, d.pos.x, d.pos.y);
      break;
    }
    boolean intersect = current.intersect(work);
    if (!intersect || current.surface() == 0) {
      current.reset();
      current.intersect(work);
    }
    return intersect;
  }
  
  private void readPackedBlips(FastReader in) {
    int radarBlipCount = in.nextInt();
    __err("^ "+radarBlipCount);
    
    for (int i=4;i<16;i++) {
      fishPresent[i] = false;
    }
    
    System.err.print("^ ");
    for (int i = 0; i < radarBlipCount; i++) {
        int packed = in.nextInt();
        
        int droneId = (packed % 10_000 ) / 1000;
        int quadrant = (packed % 1000 ) / 100;
        int creatureId = (packed % 100) ;
      
        fishPresent[creatureId] = true;
        dronesById[droneId].blips.add(new Blip(creatureId, quadrant));
        updateTriangulation(dronesById[droneId], currentTriangulations[creatureId], quadrant);
        currentTriangulations[creatureId].intersect(Rectangle.byTypes[fType[creatureId]]);
        
        // __err("^ "+droneId+" "+creatureId+" "+in.nextChars());
        System.err.print(packed+" ");
    }
    System.err.println();
    
  }


  private void readFishes(FastReader in) {
    __err("--- Fishes --- ");
    int visibleCreatureCount = in.nextInt();
    __err("^ "+visibleCreatureCount);
    for (int i = 0; i < visibleCreatureCount; i++) {
        int creatureId = in.nextInt();
        int creatureX = in.nextInt();
        int creatureY = in.nextInt();
        int creatureVx = in.nextInt();
        int creatureVy = in.nextInt();
        
        Fish fish = new Fish(creatureId);
        fish.id = creatureId;
        fish.pos.x = creatureX;
        fish.pos.y = creatureY;
        fish.speed.vx = creatureVx;
        fish.speed.vy = creatureVy;
        fishes.add(fish);
        
        currentTriangulations[creatureId].exact(creatureX, creatureY);
        __err("^ "+creatureId+" "+creatureX+" "+creatureY+" "+creatureVx+" "+creatureVy);
    }
    
  }

  private void readCurrenScans(FastReader in) {
    int droneScanCount = in.nextInt();
    __err("--- Current Scans ---");
    System.err.print("^ "+droneScanCount+" ");
    for (int i = 0; i < droneScanCount; i++) {
        int droneId = in.nextInt();
        int creatureId = in.nextInt();
        
        dronesById[droneId].setScan(creatureId);
        System.err.print(droneId+" "+creatureId+" ");
    }
    System.err.println();
    
    checkNewScans();
  }

  private void checkNewScans() {
    for (int i=0;i<4;i++) {
      Drone drone = dronesById[i];
      if (drone.emergency) continue;
      
      Drone lastDrone = previousState.dronesById[i];
      Scan lastScan = lastDrone.currentScans;
      
      updateTriangulations(drone, lastScan);
    }
  }


  private void updateTriangulations(Drone drone, Scan lastScan) {
    int radius = hadLights(drone.id) ? Drone.BIG_LIGHT_RADIUS : Drone.NORMAL_LIGHT_RADIUS;

    for (int c=4;c<16;c++) {
      if (!fishPresent[c]) continue;
      
      if (drone.currentScans.contains(c) && !lastScan.contains(c)) {
        if( Player.DEBUG_TRIANGULATION) System.err.println("For drone "+drone.id+", New fish is "+c+" at pos "+drone.pos+" light? "+ hadLights(drone.id)+" ... cropping out ");
        currentTriangulations[c].cropOutsideCircle(drone.pos.x, drone.pos.y, radius);
      } else if (!drone.currentScans.contains(c)){
        // remove interior of circle if the drone has not the fish yet!
        if (currentTriangulations[c].hasPotentialIntersectionWithCircle(drone.pos.x, drone.pos.y, radius)) {
          if( Player.DEBUG_TRIANGULATION) System.err.println("For drone "+drone.id+", No scan of fish "+c+" at pos "+drone.pos+" light? "+ hadLights(drone.id)+" ... cropping in");
          currentTriangulations[c].cropInsideCircle(drone.pos.x, drone.pos.y, radius);
        }
      }
    }
  }


  private boolean hadLights(int id) {
    if (previousState == null) return false;
    return previousState.dronesById[id].battery == dronesById[id].battery+5;
  }


  private void readDrones(FastReader in) {
    int myDroneCount = in.nextInt();
    __err("^ "+myDroneCount);
    for (int i = 0; i < myDroneCount; i++) {
        int droneId = in.nextInt();
        int droneX = in.nextInt();
        int droneY = in.nextInt();
        int emergency = in.nextInt();
        int battery = in.nextInt();
        
        dronesById[droneId].update(droneX, droneY, emergency == 1, battery);
        dronesById[droneId].outputDebug();
        myDrones[i] = dronesById[droneId]; 
    }

    int foeDroneCount = in.nextInt();
    __err("^ "+foeDroneCount);
    for (int i = 0; i < foeDroneCount; i++) {
        int droneId = in.nextInt();
        int droneX = in.nextInt();
        int droneY = in.nextInt();
        int emergency = in.nextInt();
        int battery = in.nextInt();

        dronesById[droneId].update(droneX, droneY, emergency == 1, battery);
        dronesById[droneId].outputDebug();
        oppDrones[i] = dronesById[droneId]; 
    }
    
    State.agentId = (myDrones[0].id == 0) ? 0 : 1;
  }

  private void readAlreadyScans(FastReader in) {
    myScans.copyFrom(previousState.myScans);
    oppScans.copyFrom(previousState.oppScans);
    
    int myScanCount = in.nextInt();
    __err("Already scan : ");
    System.err.print("^" +myScanCount);
    for (int i = 0; i < myScanCount; i++) {
        int creatureId = in.nextInt();
        System.err.print(" "+creatureId);
        myScans.scan(creatureId);
    }
    System.err.println(); 
    
    __err("---");
    int foeScanCount = in.nextInt();
    System.err.print("^" +foeScanCount);
    for (int i = 0; i < foeScanCount; i++) {
        int creatureId = in.nextInt();
        System.err.print(" "+creatureId);
        oppScans.scan(creatureId);
    }
    System.err.println();
    
    
    updateGlobalScans();
    
  }

  private void updateGlobalScans() {
    
    myScans.updateFirsts(oppScans);
    oppScans.updateFirsts(myScans);
  }


  private void readScore(FastReader in) {
    myScore = in.nextInt();
    oppScore = in.nextInt();
    // need to wait for the 1st read to output init & optional
    outputInit();
    outputOptional();
    __err("*** TURN ***");
    __err("^ "+myScore+" "+oppScore);
  }

  private void init() {
    for (int i=0;i<4;i++) {
      dronesById[i].clear();
    }

    
    for (int i=4;i<16+uglyCount;i++) {
      if (State.turn == 0) {
        if (fType[i] == Fish.UGLY) {
          currentTriangulations[i].intersect(Rectangle.UGLY_START);
        } else {
          currentTriangulations[i].intersect(Rectangle.byTypes[fType[i]]);
        }
      } else {
        currentTriangulations[i].copyFrom(previousState.currentTriangulations[i]);
      }
      
      
      if (fType[i] == Fish.UGLY) {
        // TODO CHECK THAT !
        boolean aggressive = false;
//        for (int d=0;d<4;d++) {
//          if (wasPotentiallyInLightRadius(d, i)) {
//            aggressive = true;
//            break;
//          }
//        }
        aggressive = true;
        currentTriangulations[i].expand(aggressive ? 540 : 270); // take the fish movement into account TODO not chase !
      } else {
        // normal fish
        
//        boolean fear = false;
//        for (int d=0;d<4;d++) {
//          if (previousState.hadMotor(d) && previousState.currentTriangulations[i].hasPotentialIntersectionWithCircle(previousState.dronesById[d].pos.x, previousState.dronesById[d].pos.y, 1400)) {
//            fear = true;
//            break;
//          }
//        }
//        
//        currentTriangulations[i].expand(fear ? 400 : 200); // take the fish movement into account  
        expandNormalFish(i);
      }
    }
    
    fishes.clear();
    myScans.clear();
    oppScans.clear();
  }
  
  private void expandNormalFish(int f) {
    Zone newRect = previousState.currentTriangulations[f].newInstance();
    newRect.expand(Fish.FISH_SWIM_SPEED);  // just swimming
    
    for (int d=0;d<4;d++) {
      if (previousState.hadMotor(d) && previousState.currentTriangulations[f].hasPotentialIntersectionWithCircle(previousState.dronesById[d].pos.x, previousState.dronesById[d].pos.y, 1400)) {
        Rectangle intersection = new Rectangle((Rectangle)previousState.currentTriangulations[f]);
        intersection.intersectionWithCircle(previousState.dronesById[d].pos.x, previousState.dronesById[d].pos.y, 1400);

        intersection.expandFromPos(dronesById[d].pos, Fish.FISH_FLEE_SPEED);
        
        newRect.maxOf(intersection);
      }
    }
    
    currentTriangulations[f].copyFrom(newRect);  
    
  }
  
  
  private boolean wasPotentiallyInLightRadius(int droneId, int fishId) {
    int lightRadius = hadLights(droneId) ? 2000 : 800;
    
    if (currentTriangulations[fishId].hasPotentialIntersectionWithCircle(previousState.dronesById[droneId].pos.x, previousState.dronesById[droneId].pos.y, lightRadius)) {
      return true;
    } else {
      return false;
    }
  }

  private boolean hadMotor(int d) {
    // TODO better prevision ?
    if (previousState == null) return true;
    
    return dronesById[d].pos.y != previousState.dronesById[d].pos.y - 300;
  }


  private int radarToQuadran(char[] radar) {
    if (radar[0] == 'T') {
      if (radar[1] == 'R') return 0; else return 3;
    } else {
      if (radar[1] == 'R') return 1; else return 2;
    }
  }

  private void outputInit() {
    __err("*** INIT ***");
    __err(initPacked);
  }

  private void outputOptional() {
    __err("*** OPTIONAL ***");
    
    int playerId;
    if (previousState != null &&  previousState.myDrones[0] != null) {
      playerId = previousState.myDrones[0].id == 0 ? 0 : 1;
    } else {
      playerId = 0;
    }
    
    System.err.println("^ "+State.turn + " " + playerId);
    // drones
    for (int i=0;i<4;i++) {
      previousState.dronesById[i].outputDebugOptional();
    }
    System.err.println();

    System.err.println("^ "+previousState.fishes.size());
    for (Fish fish : previousState.fishes) {
      fish.outputDebugOptional();
    }
    System.err.println();
    
    // triangulations
    System.err.print("^ ");
    for (int i=4;i<4+creatureCount;i++) {
      System.err.print(previousState.currentTriangulations[i].pack()+" ");
    }
    System.err.println();
    
    System.err.println("^ "+previousState.myScans.toString()+" "+previousState.oppScans.toString());
  }

  private static void __err(String err) {
    System.err.println(err);
  }
  
  public void readOptional(FastReader in) {
    this.previousState = new State();
    
    State.turn = in.nextInt();
    int myPlayerId = in.nextInt();
    
    for (int i=0;i<4;i++) {
      previousState.dronesById[i].readDebugOptional(in);
    }
    if (myPlayerId == 0) {
      previousState.myDrones[0] = previousState.dronesById[0]; 
      previousState.myDrones[1] = previousState.dronesById[2]; 
      previousState.oppDrones[0] = previousState.dronesById[1]; 
      previousState.oppDrones[1] = previousState.dronesById[3]; 
    } else {
      previousState.myDrones[0] = previousState.dronesById[1]; 
      previousState.myDrones[1] = previousState.dronesById[3]; 
      previousState.oppDrones[0] = previousState.dronesById[0]; 
      previousState.oppDrones[1] = previousState.dronesById[2]; 
    }
    
    int fishCount = in.nextInt();
    for (int i=0;i<fishCount;i++) {
      Fish fish = new Fish(-1);
      fish.readDebugOptional(in);
      previousState.fishes.add(fish);
    }

    for (int i=4;i<4+creatureCount;i++) {
      previousState.currentTriangulations[i].unpack(in.nextLong());
    }
    
    previousState.myScans.scaned = in.nextLong();
    previousState.oppScans.scaned = in.nextLong();
    
  }

  public void readPacked(FastReader in) {
    __err("*** PACKED! ***");
    init();
    readScore(in);
    readAlreadyScans(in); 
    readDrones(in);
    readCurrenScans(in);
    readFishes(in);
    readPackedBlips(in);
    
    List<Fish> discoveredFished = updateFromSymetry();
    updateFromPreviousState(discoveredFished);
  }

  public void saveOptionals() {
  }


  /**
   * get a fish by its id
   * @param id
   * @return the fish or <b>null</b> if not found
   */
  public Fish getFishById(int id) {
    for (Fish fish : fishes) {
      if (fish.id == id) return fish;
    }
    return null;
  }


  public boolean isMine(Drone drone) {
    return drone.id == myDrones[0].id || drone.id == myDrones[1].id;
  }
  
  public Zone getZoneTriangulation(int fishId) {
    return new Rectangle((Rectangle)currentTriangulations[fishId]);
  }
  
  public Zone getBestTriangulation(int fishId) {
    Fish fish = getFishById(fishId);
    if (fish != null) {
      return new Rectangle(fish.pos.x, fish.pos.y, 1, 1);
    } else if (fishPresent[fishId]){
      return new Rectangle((Rectangle)currentTriangulations[fishId]);
    } else {
      return new Rectangle(-10000,-10000, 10, 10);
    }
  }


  /**
   * calling this method allows for fish position detection by symmetry if it is possible
   * normally should only be called in player while in game
   */
  public static void setCanBeInitial() {
    for (int i=0;i<30;i++) {
      canBeInitial[i] = true;
      neverSawFish[i] = true;
    }
  }
}
