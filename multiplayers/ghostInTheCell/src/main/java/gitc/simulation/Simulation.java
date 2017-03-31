package gitc.simulation;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import gitc.GameState;
import gitc.ag.AGPlayer;
import gitc.ag.AGSolution;
import gitc.ag.TurnAction;
import gitc.entities.Bomb;
import gitc.entities.Factory;
import gitc.entities.Owner;
import gitc.entities.Troop;
import gitc.simulation.actions.Action;
import gitc.simulation.actions.ActionType;
import gitc.simulation.actions.BombAction;
import gitc.simulation.actions.MoveAction;
import gitc.simulation.actions.UpgradeAction;

public class Simulation {
  private static final int COST_INCREASE_PRODUCTION = 10;
  private static final int MAX_PRODUCTION_RATE = 3;
  private GameState state;
  public List<Bomb> bombs = new ArrayList<>();
  public List<Troop> troops = new ArrayList<>();
  List<Bomb> newBombs = new ArrayList<>();
  List<Troop> newTroops = new ArrayList<>();

  public Simulation(GameState state) {
    this.state = state;
  }

  public void simulate(AGSolution solution) {
    prepareSimulation();
    
    for (int turn = 0; turn < AGSolution.SIMULATION_DEPTH; turn++) {
      simulate(solution, turn);
    }
    
    solution.calculateHeuristic(this);
    
    restoreGameState();
  }

  public void restoreGameState() {
    state.restoreState();
  }

  public void prepareSimulation() {
    troops.clear();
    troops.addAll(state.getTroops());
    
    bombs.clear();
    bombs.addAll(state.getBombs());
  }

  public void simulate(AGSolution solution, int turnIndex) {
    newBombs.clear();
    newTroops.clear();
    
    moveEntities();
    decreaseFactoriesDisableCoutdown();
    executeOrders(solution, turnIndex);
    createFactoryUnits();
    solveBattles(solution);
    solveBombs();

    // ---
    // Update score
    // ---
//    for (AGPlayer player : solution.players) {
//      player.units = 0;
//      player.production = 0;
//      player.dead = false;
//    }
    
    for (Factory factory : GameState.factories) {
      if (factory.owner != null) {
        AGPlayer player = solution.players.get(factory.owner.id);
        player.units += factory.units;
        player.production += factory.productionRate;
      }
    }
    for (Troop troop : troops) {
      if (troop.owner != null) {
        solution.players.get(troop.owner.id).units += troop.units;
      }
    }

    // ---
    // Check end conditions
    // ---
    for (AGPlayer player : solution.players) {
      if (player.units == 0) {
        int production = 0;
        for (Factory factory : GameState.factories) {
          if (factory.owner == player.owner) {
            production += factory.productionRate;
          }
        }
        if (production == 0) {
          player.dead = true;
        }
      }
    }
  }

  private void solveBombs() {
    for (Iterator<Bomb> it = bombs.iterator(); it.hasNext();) {
      Bomb bomb = it.next();
      if (bomb.remainingTurns <= 0) {
        bomb.explode();
        it.remove();
      }
    }
  }

  private void solveBattles(AGSolution solution) {
    // ---
    // Solve battles
    // ---
    for (Factory factory : GameState.factories) {
      factory.unitsReadyToFight[0] = factory.unitsReadyToFight[1] = 0;
    }

    for (Iterator<Troop> it = troops.iterator(); it.hasNext();) {
      Troop troop = it.next();
      if (troop.remainingTurns <= 0) {
        troop.destination.unitsReadyToFight[troop.owner.id] += troop.units;
        it.remove();
      }
    }
    for (Factory factory : GameState.factories) {
      // Units from both players fight first
      int units = Math.min(factory.unitsReadyToFight[0], factory.unitsReadyToFight[1]);
      factory.unitsReadyToFight[0] -= units;
      factory.unitsReadyToFight[1] -= units;

      // Remaining units fight on the factory
      for (AGPlayer player : solution.players) {
        if (factory.owner == player.owner) { // Allied
          factory.units += factory.unitsReadyToFight[player.owner.id];
        } else { // Opponent
          if (factory.unitsReadyToFight[player.owner.id] > factory.units) {
            factory.owner = player.owner;
            factory.units = factory.unitsReadyToFight[player.owner.id] - factory.units;
          } else {
            factory.units -= factory.unitsReadyToFight[player.owner.id];
          }
        }
      }
    }
  }

  private void createFactoryUnits() {
    for (Factory factory : GameState.factories) {
      if (factory.owner != null /*NOT neutral*/) {
        factory.units += factory.getCurrentProductionRate();
      }
    }
  }

  private void moveEntities() {
    for (Troop troop : troops) {
      troop.move();
    }
    for (Bomb bomb : bombs) {
      bomb.move();
    }
  }

  private void decreaseFactoriesDisableCoutdown() {
    for (Factory factory : GameState.factories) {
      if (factory.disabled > 0) {
        factory.disabled--;
      }
    }
  }

  private void executeOrders(AGSolution solution, int turnIndex) {
    for (AGPlayer player : solution.players) {
      TurnAction tAction = player.turnActions[turnIndex];
      // Send bombs
      
      for (Action action : tAction.actions) {
        if (action.type != ActionType.BOMB) continue;
        BombAction bombAction = (BombAction)action;
        int distance = bombAction.src.getDistanceTo(bombAction.dst);
        Bomb bomb = new Bomb(player.owner, bombAction.src, bombAction.dst, distance);
        if (player.remainingBombs > 0 && bomb.findWithSameRouteInList(newBombs) == null) {
          newBombs.add(bomb);
          bombs.add(bomb);
          player.remainingBombs--;
        }
      }

      // Send troops
      for (Action action : tAction.actions) {
        if (action.type != ActionType.MOVE) continue;
        MoveAction moveAction = (MoveAction)action;
        int unitsToMove = Math.min(moveAction.src.units, moveAction.units);
        Troop troop = new Troop(player.owner, moveAction.src, moveAction.dst, unitsToMove);

        // forbid same route bombs & units
        if (unitsToMove > 0 && troop.findWithSameRouteInList(newBombs) == null) { 
          moveAction.src.units -= unitsToMove;

          Troop other = troop.findWithSameRouteInList(newTroops);
          if (other != null) {
            other.units += unitsToMove;
          } else {
            troops.add(troop);
            newTroops.add(troop);
          }
        }
      }

      // Increase
      for (Action action : tAction.actions) {
        if (action.type != ActionType.UPGRADE) continue;
        UpgradeAction incAction = (UpgradeAction)action;
        if (incAction.src.units >= COST_INCREASE_PRODUCTION && incAction.src.productionRate < MAX_PRODUCTION_RATE) {
          incAction.src.productionRate++;
          incAction.src.units -= COST_INCREASE_PRODUCTION;
        }
      }
    }
  }

  public int getTroopsInTransit(Owner owner) {
    int inTransit = 0;
    for (Troop troop : troops) {
      if (troop.owner == owner) {
        inTransit+= troop.units;
      }
    }
    return inTransit;
  }

  public int getTroopsInFactory(Owner me) {
    int total = 0;
    for (Factory factory : GameState.factories) {
      if (factory.isMe()) {
        total = factory.units;
      }
    }
    return total;
  }
}
