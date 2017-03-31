package gitc;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import gitc.ag.AGSolution;
import gitc.entities.Bomb;
import gitc.entities.EntityType;
import gitc.entities.Factory;
import gitc.entities.Owner;
import gitc.entities.Troop;
import gitc.simulation.Simulation;

public class GameState {
  public static boolean TDD_OUPUT = false;
  public static boolean FACTORY_FUTURE_OUPUT = false;
  public static boolean FRONT_BACK_OUTPUT = false;
  
  List<String> inputSetupBackup = new ArrayList<>();
  List<String> inputBackup = new ArrayList<>();

  public static final Owner me = new Owner(0);
  public static final Owner opp = new Owner(1);
  public int factoryCount;
  public static Factory myInitialBase;
  public static Factory oppInitialBase;
  public static Factory center;
  public static Factory[] factories;
  public static Factory unkownFactory = new Factory(0, 1);
  public static Factory[] myFactories;
  public static Factory[] oppFactories;
  public static Factory[] neutralFactories;

  
  public static List<Troop> troops = new ArrayList<>();
  public static Map<Integer, Bomb> bombs = new HashMap<>();
  public static int units[] = new int[2];
  public int production[] = new int [2];
  public int unitsTotal;
  private int entityCount;
  
  public void readSetup(Scanner in) {
    factoryCount = in.nextInt();
    int linkCount = in.nextInt(); // the number of links between factories

    factories = new Factory[factoryCount];
    for (int id=0;id<factoryCount;id++) {
      factories[id] = new Factory(id, factoryCount);
    }
    
    if (TDD_OUPUT)
      inputSetupBackup.add("GameState state = new GameBuilder()");
    
    for (int i = 0; i < linkCount; i++) {
      int factory1 = in.nextInt();
      int factory2 = in.nextInt();
      int distance = in.nextInt();
      factories[factory1].setupDistance(factories[factory2], distance);
      factories[factory2].setupDistance(factories[factory1], distance);
      
      if (TDD_OUPUT)
        inputSetupBackup.add(".l(new LB().f("+factory1+").t("+factory2+").d("+distance+").b())");
    }
 
    setupTddOutput();
  }
  
  private void calculateCenterOfMap() {
    for (Factory factory : factories) {
      if (factory.isMe()) {
        myInitialBase = factory;
      }
      if (factory.isOpponent()) {
        oppInitialBase = factory;
      }
    }
    
    for (Factory factory : factories) {
      if (factory.getDistanceTo(myInitialBase) == factory.getDistanceTo(oppInitialBase)) {
        center = factory;
      }
    }    
    
  }

  public void read(Scanner in) {
    inputBackup.clear();
    
    clearRound();
    
    entityCount = in.nextInt();
    // ** INIT PLAYER START */
    Player.start = System.currentTimeMillis();

    Map<Integer, Bomb> newBombs = new HashMap<>();

    for (int i = 0; i < entityCount; i++) {
        int entityId = in.nextInt();
        String entityType = in.next();
//        System.err.println("Reading entityType : "+entityType+" with Id :"+entityId);
        if (entityType.equals(EntityType.FACTORY.name())) {
          Factory factory = factories[entityId];
          factory.read(in);
          if (factory.owner != null) {
            production[factory.owner.id]+=factory.productionRate;
            units[factory.owner.id]+=factory.units;
          }
          if (TDD_OUPUT) {
            inputBackup.add(factories[entityId].tddOutput());
          }
        } else if (entityType.equals(EntityType.TROOP.name())){
          int troopId = i-factoryCount;
          Troop troop = new Troop(troopId);
          troop.read(in);
          troop.affectToFactory(factories);
          units[troop.owner.id]+=troop.units;
          troops.add(troop);
          if (TDD_OUPUT) {
            inputBackup.add(troop.tddOutput());
          }
        } else if (entityType.equals(EntityType.BOMB.name())){
          Bomb bomb = bombs.get(entityId);
          if (bomb == null) {
            bomb = new Bomb(entityId);
            bomb.destination = GameState.unkownFactory;
            bomb.read(in);
            if (bomb.destination != unkownFactory) {
              bomb.destination.bombIncomming = true;
            } else {
              getBombDestinationFromKnowledge(bomb);
            }
          } else {
            // only read, we already know the bomb
            bomb.read(in);
          }

          newBombs.put(entityId, bomb);
        }
    }

    if (Player.turn == 0) {
      calculateCenterOfMap();
    }
    // replace bombs
    bombs = newBombs;
    
    unitsTotal = units[0] + units[1];
    
    if (TDD_OUPUT) {
      tddOuput();
    }

    preTurnUpdate();
    backupState();
    updateFuture();
    updateNearestEnnemy();
  }

  /**
   * For each factory, get the nearest ennemy
   */
  private void updateNearestEnnemy() {
    for (Factory f1 : factories) {
      int minDistance= 1_000_000;
      Factory nearestFactory = null;
      for (Factory f2 : factories) {
        if (f2.owner != null && f2.owner != f1.owner && f2.getDistanceTo(f1) < minDistance) {
          minDistance = f2.getDistanceTo(f1);
          nearestFactory = f2;
        }
      }
      f1.nearestEnnemyFactory = nearestFactory;
    }
  }

  private void updateFuture() {
    AGSolution dummy = new AGSolution();
    
    me.totalDisposable = 0;
    opp.totalDisposable = 0;
    
    Simulation simulation = new Simulation(this);
    simulation.prepareSimulation();
    for (int turn = 0; turn < AGSolution.SIMULATION_DEPTH; turn++) {
      simulation.simulate(dummy, turn);
      for (Factory factory : factories) {
        factory.future[turn] = factory.units * (factory.owner == me ? 1 : -1);
      }
    }
    
    dummy.calculateHeuristic(simulation);
    
    simulation.restoreGameState();
    
    /**
     * calculate the needed units and when
     */
    for (Factory factory : factories) {
      factory.unitsNeededCount = 0;
      factory.unitsNeededAt = 0;
      factory.unitsDisposable = factory.units;

      int mult = 1;
      if (factory.isNeutral()) { 
        continue;
      }
      if (factory.isOpponent())  {
        mult = -1;
      }
      for (int turn = 0;turn<AGSolution.SIMULATION_DEPTH;turn++) {
        factory.unitsDisposable = Math.min(factory.unitsDisposable, mult*factory.future[turn]);
        if (factory.unitsNeededAt == 0 && mult*factory.future[turn] < 0) {
          factory.unitsNeededCount = -mult*factory.future[turn];
          factory.unitsNeededAt = turn+1;
        }
      }        
    }    
    for (Factory factory : factories) {
      if (!factory.isNeutral() && factory.unitsDisposable > 0) {
        factory.owner.totalDisposable +=factory.unitsDisposable ;
      }
    }
    
    if (FACTORY_FUTURE_OUPUT) {
      System.err.println("Dummy simulation score : "+dummy.energy);
      System.err.println("Future :");
  
      String title = String.format("%4s", " ")+" ";
      for (int turn = 0;turn<AGSolution.SIMULATION_DEPTH;turn++) {
        title+=String.format("%4d",turn+1);
      }
      System.err.println(title);
      for (Factory factory : factories) {
        String output = ""+String.format("%4d", factory.id)+" ";
        for (int turn = 0;turn<AGSolution.SIMULATION_DEPTH;turn++) {
          output+=String.format("%4d",factory.future[turn]);
        }
        System.err.println(output);
      }
  
      // disposable / needed
      for (Factory factory : factories) {
        if (factory.isNeutral()) continue;
        String who = ""+ (factory.isMe() ? "Me  " : 
                                           "Op ");
        String front = ""+(factory.isFront ? " (F) " : " (B) ");
        if (factory.unitsDisposable >= 0) {
          System.err.println(who+front+factory.id+" -clear  "+factory.unitsDisposable);
        } else {
          System.err.println(who+front+factory.id+" -uAtt   "+factory.unitsNeededCount+" at "+factory.unitsNeededAt);
        }
      }
    }
  }

  private void preTurnUpdate() {
    updateFactoryHelpers();
    updateFactoryInfluence();
    updateFactoryFront();
  }

  private void updateFactoryHelpers() {
    List<Factory> mine = new ArrayList<>();
    List<Factory> opp = new ArrayList<>();
    List<Factory> neutral = new ArrayList<>();
    for (Factory factory : factories) {
      if (factory.isMe()) {
        mine.add(factory);
      } else if (factory.isOpponent()) {
        opp.add(factory); 
      } else {
        neutral.add(factory);
      }
    }
    myFactories = mine.toArray(new Factory[0]);
    oppFactories = opp.toArray(new Factory[0]);
    neutralFactories = neutral.toArray(new Factory[0]);
  }

  private void updateFactoryFront() {
//    System.err.println("Factory front : ");
    for (Factory factory : factories) {
      factory.calculateFront();
//      System.err.println(""+factory.id+": "+(factory.isFront ? " FRONT" : "BACK"));
    }
  }

  private void updateFactoryInfluence() {
    // System.err.println("Factory influences : ");
    for (Factory factory : factories) {
      factory.calculateInfluence(troops);
       // System.err.println("   "+factory.id+" = "+ factory.influence);
    }
  }

  private void getBombDestinationFromKnowledge(Bomb bomb) {
    Factory myOnlyFactory = onlyOneFactoryOwned();
    if (myOnlyFactory != null) {
      bomb.destination = myOnlyFactory;
      bomb.remainingTurns = myOnlyFactory.getDistanceTo(bomb.source);
      System.err.println("Found what the bomb will attack ("+myOnlyFactory.id+")");
    } else {
    }
  }

  private Factory onlyOneFactoryOwned() {
    Factory myOnlyFactory = null;
    for (Factory factory : factories) {
      if (factory.isMe()) {
        if (myOnlyFactory != null) {
          return null; // at least 2
        } else {
          myOnlyFactory = factory;
        }
      }
    }
    return myOnlyFactory;
  }

  private void clearRound() {
    troops.clear();
    // don't clear bombs, we use persistence on them
    // bombs.clear();

    entityCount = 0;
    unitsTotal = 0;
    units[0] = units[1] = 0;
    production[0] = production[1] = 0;
    for (Factory factory : factories) {
      factory.clear();
    }
  }

  private void tddOuput() {
    for (String line : inputBackup) {
      System.err.println(line);
    }
  }

  private void setupTddOutput() {
    int i = 0;
    for (String setupLine : inputSetupBackup) {
      i++;
      System.err.print(setupLine);
      if (i % 10 == 0) {
        System.err.println("");
      }
    }
    System.err.println("");
  }

  /** prepare for restore */
  private void backupState() {
    for (Factory factory : factories) {
      factory.backup();
    }
    for (Troop troop : troops) {
      troop.backup();
    }
    for (Bomb bomb : bombs.values()) {
      bomb.backup();
    }
  }
  
  public void restoreState() {
    for (Factory factory : factories) {
      factory.restore();
    }
    for (Troop troop : troops) {
      troop.restore();
    }
    for (Bomb bomb : bombs.values()) {
      bomb.restore();
    }
  }
  public Factory[] getFactories() {
    return factories;
  }

  public List<Troop> getTroops() {
    return troops;
  }
  public Collection<Bomb> getBombs() {
    return bombs.values();
  }

  public int willBombHitFactory(Factory attackFactory) {
    for (Bomb bomb : bombs.values()) {
      if (bomb.destination == attackFactory) {
        return bomb.remainingTurns;
      }
    }
    return -1;
  }
}
