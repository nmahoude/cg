package gitc.ag;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import gitc.GameState;
import gitc.entities.Bomb;
import gitc.entities.Factory;
import gitc.simulation.actions.Action;
import gitc.simulation.actions.BombAction;
import gitc.simulation.actions.MoveAction;
import gitc.simulation.actions.UpgradeAction;

public class AGPool {
//  private static Random random = new Random(System.nanoTime());
  public static Random random = new Random();
  private static final int AG_POOL = 10;
  AGSolution[] solutions = new AGSolution[AG_POOL];
  
  public AGPool() {
    reset();
  }
  
  public void createInitialPopulation() {
    for (int i=0;i<AG_POOL;i++) {
      solutions[i] = createRandom();
    }
  }
  
  public AGSolution createRandom() {
    Map<Integer, List<Action>> possibleActions = getPossibleActions();

    AGSolution solution = new AGSolution();
    for (Factory factory : GameState.factories) {
      List<Action> actions = possibleActions.get(factory.id);
      if (actions.size() > 0) {
        int actionNumber = random.nextInt(1+actions.size());
        Collections.shuffle(actions);
        for (int i=0;i<actionNumber;i++) {
          if (random.nextBoolean()) {
            solution.players.get(0).addAction(actions.get(i), 0);
          }
        }
      }
    }
    return solution;
  }
  
  public AGSolution cross() {
    int index1 = findIndex(solutions, -1);
    int index2 = findIndex(solutions, index1);
    
    return cross(solutions[index1], solutions[index2]);
  }

  int findIndex(AGSolution[] pool, int otherThanIndex) {
    int aIndex, bIndex;
    do {
      aIndex = random.nextInt(AG_POOL);
    } while (aIndex == otherThanIndex);

    for (int i=0;i<3;i++) {
      do {
        bIndex = random.nextInt(AG_POOL);
      } while (bIndex == aIndex || bIndex == otherThanIndex);
      
      aIndex = pool[aIndex].energy > pool[bIndex].energy ? aIndex : bIndex;
    }
    return aIndex;
  }
  
  public AGSolution cross(AGSolution ag1, AGSolution ag2) {
    AGSolution newSolution = new AGSolution();
    TurnAction newActions = newSolution.players.get(0).turnActions[0];
    
    TurnAction a1 = ag1.players.get(0).turnActions[0];
    TurnAction a2 = ag2.players.get(0).turnActions[0];
    for (Action action : a1.actions) {
      if (random.nextBoolean()) {
        newActions.actions.add(action);
      }      
    }
    for (Action action : a2.actions) {
      if (random.nextBoolean()) {
        newActions.actions.add(action);
      }      
    }
    
    return newSolution;
  }
  
  public void propose(AGSolution newSolution) {
    int minIndex = -1;
    double minEnergy = newSolution.energy;
    
    for (int i=0;i<AG_POOL;i++) {
      if (solutions[i].energy < minEnergy) {
        minEnergy = solutions[i].energy;
        minIndex = i;
      }
    }
    if (minIndex != -1) {
      solutions[minIndex] = newSolution;
    } else {
      // forget about this solution, not good enough
    }
  }
  
  public AGSolution getBest() {
    AGSolution best = null;
    for (int i=0;i<AG_POOL;i++) {
      if (best == null || best.energy < solutions[i].energy) {
        best = solutions[i];
      }
    }
    
    return best;
  }
  
  public static Map<Integer, List<Action>> getPossibleActions() {
    Map<Integer, List<Action> > possibleActions = new HashMap<>();
    for (Factory factory : GameState.factories) {
      List<Action> actions = new ArrayList<>();
      possibleActions.put(factory.id, actions);
      
      if (factory.isMe()) {
        getPossibleActionsForFactory(factory, actions);
      }
    }
    return possibleActions;
  }

  public static void getPossibleActionsForFactory(Factory factory, List<Action> actions) {
    // upgrade
    addPotentialUpgradeAction(factory, actions);
    
    // move : add some possible move actions
    if (factory.units > 0) {
      addBombTrails(factory, actions);

      if (!factory.isFront) {
        /* ------------------------
               back Moves
         -------------------------*/
        for (Factory otherFactory : GameState.factories) {
          if (otherFactory != factory) {
            attackNeutrals(factory, actions, otherFactory);
            moveTroopsToFront(factory, actions, otherFactory);
            
            // reroute between back factories
//            if (!otherFactory.isFront && otherFactory.isMe()) {
//              if (otherFactory.getDistanceTo(factory) < 6) {
//                int units = 1+random.nextInt(factory.units);
//                if (factory.productionRate < 3 && factory.disabled == 0) {
//                  units = Math.min(Math.max(factory.units-10, 0), units);
//                }
//                MoveAction action = new MoveAction(factory, otherFactory, units);
//                action = findBetterRouteForMove(action);
//                actions.add(action);
//              }
//            }
          }
        }
      }
      
      /* -------------
      // front Moves
      // -------------*/
      if (factory.isFront) {
        for (Factory otherFactory : GameState.factories) {
          if (otherFactory != factory) {

            boolean attack = shouldFrontAttack(factory, otherFactory);
            if (!attack) continue;

            // attack front ennemy or neutrals
            MoveAction action = null;
            if ((!otherFactory.isMe() && otherFactory.isFront) 
                || (otherFactory.isNeutral() /*&& !inDeepEnnemyTerritory(factory, otherFactory)*/)) {

              int unitsToSend = 1 + Math.max(factory.units, random.nextInt((int) (1.5 * factory.units)));

              if (otherFactory.isOpponent()) {
                if (factory.productionRate>=otherFactory.productionRate) {
                  // check that we wont send more units than we can defend with !
                  int remainingUnits = factory.units-unitsToSend;
                  if (remainingUnits < otherFactory.units) {
                    unitsToSend = Math.max(0, factory.units - otherFactory.units);
                  }
                }
              }
              action = new MoveAction(factory, otherFactory, unitsToSend);
              addAction(actions, action);
            }
            
            rerouteToOtherFronts(factory, actions, otherFactory);
            
          }
        }
      }
    }
    addBombsActions(factory, actions);
  }

  private static void rerouteToOtherFronts(Factory factory, List<Action> actions, Factory otherFactory) {
    MoveAction action;
    if (otherFactory.isMe() && otherFactory.isFront) {
      action = new MoveAction(factory, otherFactory, 1 + random.nextInt(factory.units));
      addAction(actions, action);
    }
  }

  private static void addBombsActions(Factory factory, List<Action> actions) {
    if (GameState.me.bombsLeft > 0) {
      for (Factory otherFactory : GameState.oppFactories) {
        if (!otherFactory.bombIncomming && otherFactory.productionRate > 1) {
          int turnToCheck = Math.min(factory.getDistanceTo(otherFactory), AGSolution.SIMULATION_DEPTH-1);
          // check if we won't own the factory
          if (otherFactory.future[turnToCheck] > 0) continue;

          actions.add(new BombAction(factory, otherFactory));
        }
      }
    }
  }

  private static boolean shouldFrontAttack(Factory factory, Factory otherFactory) {
    boolean attack = true;
    for (Factory isNearer : GameState.factories) {
      if (isNearer.isOpponent() && isNearer.getDistanceTo(factory) < factory.getDistanceTo(otherFactory)) {
        attack = false;
      }
    }
    return attack;
  }

  private static void moveTroopsToFront(Factory factory, List<Action> actions, Factory otherFactory) {
    if ( otherFactory.isFront && otherFactory.isMe()) {
      int unitsToSend = 1 + Math.max(factory.units, random.nextInt((int) (2 * factory.units)));
      // check if we can upgrade
      //                if (factory.productionRate < 3 && factory.disabled == 0) {
      //                  units = Math.min(Math.max(factory.units-10, 0), units);
      //                }
      MoveAction action = new MoveAction(factory, otherFactory, unitsToSend);
      action = findBetterRouteForMoveForBack(action);
      actions.add(action);
    }
  }

  private static void attackNeutrals(Factory factory, List<Action> actions, Factory otherFactory) {
    if (otherFactory.isNeutral() && otherFactory.productionRate > 0) {
      int units = otherFactory.units+1;
      if (factory.units >= units) {
        MoveAction action = new MoveAction(factory, otherFactory, units);
        action = findBetterRouteForMove(action);
        actions.add(action);
      }
    }
  }

  /**
   * Check the bombTrail tactics
   * if a bomb will hit a factory with prod > 0 in turn-1 of me, i can send a unit to reclaim it!
   * And hopefully gets some production out of it
   */
  public static void addBombTrails(Factory factory, List<Action> actions) {
    for (Bomb bomb : GameState.bombs.values()) {
      Factory destination = bomb.destination;
      if (!bomb.isMe() || destination == GameState.unkownFactory) continue; // don't calculate if the bomb is not mine or we don't know th destination 
//      // if (destination.isMe()) continue; // don"t do it on me own factory
      if (destination.productionRate == 0) continue; // don't trail if factory can't produce, TODO : not sure about this one
      
      int turnToCheck = Math.min(bomb.remainingTurns, AGSolution.SIMULATION_DEPTH-1);
      //if (destination.future[turnToCheck] > 10) continue; // don't trail if the factory will still have units
      
      // ok we are clean 
      if (bomb.remainingTurns == factory.getDistanceTo(destination)) {
        MoveAction action = new MoveAction(factory, destination, 1);
        actions.add(action);
        //throw new RuntimeException("Adding a trail move "+action);
      }
    }
  }

  private static void addPotentialUpgradeAction(Factory factory, List<Action> actions) {
    if (factory.productionRate < 3) {
      boolean canUpgrade = true;
      // check if we are under attack in the future and loosing 10 units is bad
      for (int units : factory.future) {
        if (units <= 10) {
          canUpgrade = false;
        }
      }
      // check if our front needs this 10 units now more than more units in the future
      int frontNeededUnits = 0;
      for (Factory myFront : GameState.factories) {
        if (myFront.isFront && myFront.isMe()) {
          frontNeededUnits += myFront.unitsNeededCount;
        }
      }
      if (frontNeededUnits > 0) {
        canUpgrade = false;
      }
      
      if (canUpgrade && GameState.center != factory) {
        actions.add(new UpgradeAction(factory));
      }
    }
  }

  /**
   * check if a factory is not in deep ennemy territory
   * @param factory
   * @param otherFactory
   * @return
   */
  private static boolean inDeepEnnemyTerritory(Factory mine, Factory target) {
    int numberOfFactory = 0;
    int distanceTo = mine.getDistanceTo(target);
    for (Factory testEnnemy : GameState.oppFactories) {
      if (testEnnemy.getDistanceTo(target) < distanceTo && distanceTo > mine.getDistanceTo(testEnnemy) ) {
        numberOfFactory++;
      }
    }
    return numberOfFactory > 0;
  }

  private static void addAction(List<Action> actions, MoveAction action) {
    if (action != null) {
      if (bombWillNotDestroyOurTroops(action)) {
        action = findBetterRouteForMove(action);
        actions.add(action);
      }
    }
  }

  private static MoveAction findBetterRouteForMoveForBack(MoveAction action) {
    // A -> B
    int AtoBDistance = action.src.getDistanceTo(action.dst);
    
    int nearestDistance = AtoBDistance;
    Factory nearestFacory = null;
    
    for (Factory factory : GameState.myFactories) {
      if (factory == action.src || factory == action.dst) continue;
      
      if (factory.nearestEnnemyFactory == null || action.dst.nearestEnnemyFactory == null) continue;
      // Ne pas tester si I est plus eloignée des ennemy que B !
      if (factory.getDistanceTo(factory.nearestEnnemyFactory) > action.dst.getDistanceTo(action.dst.nearestEnnemyFactory)) {
        continue;
      }
      // A -> I(ntermediaire) -> B
      int AtoIntermediaireDistance = factory.getDistanceTo(action.src);
      int IntermedaireToBDistance = factory.getDistanceTo(action.dst);

      if (AtoIntermediaireDistance < AtoBDistance 
          && IntermedaireToBDistance < AtoBDistance) {
    
        if (AtoIntermediaireDistance < nearestDistance) {
          nearestDistance = AtoIntermediaireDistance;
          nearestFacory = factory;
        }
      }
    }
    if (nearestFacory != null) {
      action = new MoveAction(action.src, nearestFacory, action.units);
    }
    return action;
  }

  
  private static MoveAction findBetterRouteForMove(MoveAction action) {
    // A -> B
    int distance = action.src.getDistanceTo(action.dst);
    
    int nearestDistance = distance;
    Factory nearestFacory = null;
    
    for (Factory factory : GameState.factories) {
      if (factory == action.src || factory == action.dst) continue;
      if (!factory.isMe()) continue;
      
      // A -> Intermediaire -> B
      int sourceToFactoryDistance = factory.getDistanceTo(action.src);
      int factoryToDestinationDistance = factory.getDistanceTo(action.dst);

      if (sourceToFactoryDistance < distance 
          && factoryToDestinationDistance < distance) {
    
        if (sourceToFactoryDistance < nearestDistance) {
          nearestDistance = sourceToFactoryDistance;
          nearestFacory = factory;
        }
      }
    }
    if (nearestFacory != null) {
      action = new MoveAction(action.src, nearestFacory, action.units);
    }
    return action;
  }

  private static boolean bombWillNotDestroyOurTroops(MoveAction action) {
    // check for a bomb that would destroy our army !
    boolean doAction = true;
    for (Bomb bomb : GameState.bombs.values()) {
      if (bomb.destination == action.dst && bomb.remainingTurns >= action.dst.getDistanceTo(action.src)) {
        doAction = false;
      }
    }
    return doAction;
  }

  public void reset() {
    for (int i=0;i<AG_POOL;i++) {
      solutions[i] = new AGSolution();
    }
  }
}
