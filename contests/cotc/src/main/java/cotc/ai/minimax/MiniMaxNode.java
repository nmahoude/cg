package cotc.ai.minimax;

import java.util.HashMap;
import java.util.Map;

import cotc.GameState;
import cotc.ai.ag.Feature;
import cotc.ai.ag.FeatureWeight;
import cotc.ai.ag.ShipStateAnalysis;
import cotc.ai.ag.StateAnalyser;
import cotc.entities.Action;
import cotc.entities.Ship;
import cotc.game.Simulation;
import cotc.utils.Coord;

public class MiniMaxNode {
  private static final Action[] ACTION_VALUES = Action.values();

  Map<Integer, MiniMaxNode> children = new HashMap<>();
  
  public MiniMaxNode(double energy) {
    this.energy = energy;
  }

  boolean isOptimizer; 
  double energy; // energy of the state
  FeatureWeight weights;
  
  public void randomize(GameState state, StateAnalyser analyser) {
    weights.weights[Feature.HIS_DELTA_HEALTH_FEATURE] = 0.0; // don't care about its health
    for (int s=0;s<state.teams[0].shipsAlive.FE;s++) {
      Ship ship = state.teams[0].shipsAlive.elements[s];
      ShipStateAnalysis shipAnalysis = analyser.analyse.get(ship);
      Action action = ACTION_VALUES[MiniMax.rand.nextInt(ACTION_VALUES.length)];
      Coord target = Simulation.COORD_ZERO;
      
      // eliminate impossible actions
      if (action == Action.SLOWER && ship.speed == 0) {
        action = Action.FASTER;
      }
      if (action == Action.FASTER && ship.speed == 2) {
        action = Action.WAIT;
      }
      if (action == Action.MINE) {
        action = Action.WAIT;
        if (shipAnalysis.enemyAtStern[0] == true) {
          action = Action.MINE;
          weights.weights[Feature.HIS_DELTA_HEALTH_FEATURE] = -1.0; // don't care about its health
        } else if (shipAnalysis.enemyAtStern[1] == true && shipAnalysis.closestEnemy.speed == 2) {
          // TODO enemy ship can brake, but we drop a mine anyway
          weights.weights[Feature.HIS_DELTA_HEALTH_FEATURE] = -0.5; // don't care about its health
          action = Action.MINE;
        } 
      }
      if (action == Action.FIRE) {
        ShipStateAnalysis otherShipAnalysis = analyser.analyse.get(shipAnalysis.closestEnemy);
        if (shipAnalysis.closestEnemy.position.distanceTo(ship.position) <= 3) {
          // target the next position of the ship
          Coord c1 = shipAnalysis.closestEnemy.position.neighborsCache[shipAnalysis.closestEnemy.orientation];
          Coord c2 = c1.neighborsCache[shipAnalysis.closestEnemy.orientation];
          if (shipAnalysis.closestEnemy.speed == 2 && c2.isInsideMap()) {
            target = c2;
          } else {
            target = c1;
          }
        }
        if (otherShipAnalysis.canMove[0] == false) {
          target = shipAnalysis.closestEnemy.position;
        } else {
          action = Action.WAIT;
        }
      }
      ship.action = action;
      ship.target = target;
    }
  }

  public MiniMaxNode getBestChild() {
    MiniMaxNode best = null;
    for (MiniMaxNode node : children.values()) {
      if (best == null || node.energy > best.energy) {
        best = node;
      }
    }
    return best;
  }

  public void sendOneBeam(int remainingDepth, GameState myState, GameState hisState) {
    if (isOptimizer) {
      // prepare actions for the other one
      randomize(MiniMax.hisState, MiniMax.hisAnalyser);
      Simulation simulation = new Simulation(myState);
      simulation.playOneTurn();
    }
  }

}
