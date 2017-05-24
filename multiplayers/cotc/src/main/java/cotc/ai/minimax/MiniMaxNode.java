package cotc.ai.minimax;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cgcollections.arrays.FastArray;
import cotc.GameState;
import cotc.entities.Action;
import cotc.entities.Ship;
import cotc.game.Simulation;
import cotc.utils.Coord;

public class MiniMaxNode {
  private static Simulation simulation = new Simulation(null);
  
  public double score;
  public double alpha =Double.NEGATIVE_INFINITY;
  public double beta = Double.POSITIVE_INFINITY;
  
  private GameState state;
  public List<FastArray<Action> > possibleActions = new ArrayList<>();
  
  Map<Integer, MiniMaxNode> children = new HashMap<>();
  private Integer hash;
  
  private Action action;
  private Coord target;

  public MiniMaxNode() {
  }

  
  public void startMinimax(int level, GameState state) {
    this.state = state;
    int player = 1;
    MiniMaxNode child = prepareChild(1-player);
    child.minimax(level, state, 1-player);
  }

  public double minimax(int level, GameState state, int player) {
    this.state = state;

    if (player == 0) {
      // prepare player1 actions
      MiniMaxNode child = prepareChild(1);
      score = child.minimax(level, state, 1-player);
      if (score > alpha) alpha = score;
      return alpha;
    } else {
      // player 1 simulate the new state
      simulation.setState(state);
      simulation.playOneTurn();
      // if game over, player 1 is happy
      if (state.gameOver()) {
        if (state.teams[0].dead) {
          score = -1_000_000;
        } else {
          score = 1_000_000;
        }
        if (score < beta) beta = score;
        return beta;
      } else {
        if (level ==0) {
          // end
          score = evaluateScore();
          if (score < beta) beta = score;
          return beta;
        } else {
          // next 'turn'
          MiniMaxNode child = prepareChild(1);
          score = child.minimax(level-1, state, 0);
          if (score < beta) beta = score;
          return beta;
        }
      }
    }
  }

  private MiniMaxNode prepareChild(int player) {
    // prepare all possible actions for player 0
    getPossibleActionsFor(player);
    FastArray<Action> actionsForPlayer0 = possibleActions.get(0);
    // get one action
    action = actionsForPlayer0.elements[MiniMax.rand.nextInt(actionsForPlayer0.length)];
    hash = action.ordinal();
    
    // get children
    MiniMaxNode child = children.get(hash);
    if (child == null) {
      child = new MiniMaxNode();
      children.put(hash, child);
    }
    child.action = action;
    child.target = target;
    child.hash = hash;

    // on applique les actions en prévision de la simulation
    applyActions(player, action, target);
    return child;
  }
  

  private double evaluateScore() {
    Ship myShip = state.teams[0].ships.elements[0];
    Ship other = state.teams[1].ships.elements[0];
    double score = 0
        + myShip.health - other.health
        + myShip.speed;
    return score;
  }

  private void applyActions(int teamIndex, Action action, Coord target) {
    Ship ship = state.teams[teamIndex].ships.elements[0];
    ship.action = action;
    ship.target = target;
  }

  private void getPossibleActionsFor(int player) {
    for (int s=0;s<state.teams[player].ships.length;s++) {
      Ship ship = state.teams[player].ships.elements[s];
      FastArray<Action> actions = new FastArray<Action>(Action.class, 20);
      getPossibleActions(actions, state, ship);
      possibleActions.add(actions);
   }
  }
  
  private void getPossibleActions(FastArray<Action> actions, GameState state, Ship ship) {
    actions.clear();
    actions.add(Action.WAIT);
    if (ship.speed < 2) actions.add(Action.FASTER);
    if (ship.speed > 0) actions.add(Action.SLOWER);
//    actions.add(Action.PORT);
//    actions.add(Action.STARBOARD);
//    if (ship.mineCooldown == 0) actions.add(Action.MINE);
    // TODO reput fire !
    //if (ship.cannonCooldown == 0) actions.add(Action.FIRE);
  }

  public MiniMaxNode getBestChild() {
    MiniMaxNode best = null;
    double bestScore = Double.NEGATIVE_INFINITY;
    for (MiniMaxNode node : children.values()) {
      if (node.score > bestScore) {
        bestScore = node.score;
        best = node;
      }
    }
    return best;
  }
}
