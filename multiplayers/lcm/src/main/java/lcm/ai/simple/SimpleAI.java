package lcm.ai.simple;

import java.util.ArrayList;
import java.util.List;

import lcm.State;
import lcm.ai.AI;
import lcm.ai.eval.IEval;
import lcm.cards.Card;
import lcm.cards.Location;
import lcm.predictors.Oracle;
import lcm.sim.Action;

public class SimpleAI implements AI {

  private List<Action> actions = new ArrayList<>();
  private Oracle oracle;
  private IEval eval;

  public SimpleAI(Oracle oracle, IEval eval) {
    this.oracle = oracle;
    this.eval = eval;
  }

  @Override
  public void think(State state) {
    for (Card card : state.cards) {
      if (card == null) continue;
      if (card.location != Location.MY_HAND) continue;
      
      if (card.cost <= state.me.mana) {
        actions.add(Action.summon(card));
        state.me.mana -= card.cost;
      }
    }
    
    // attack - prepare cards
    List<Card> opponentBoardCards = new ArrayList<>();
    List<Card> opponentBoardGuardCards = new ArrayList<>();
    for (Card card : state.cards) {
      if (card == null) continue;
      if (card.location != Location.HIS_BOARD) continue;
      opponentBoardCards.add(card);
      if (card.isGuard()) {
        opponentBoardGuardCards.add(card);
      }
    }

    // if no guard and enough attack, go for deathly blow
    if (!state.opp.hasGuard() && state.opp.health < myAttack(state)) {
      opponentBoardCards.clear();
    }

    
    // attack with my cards
    for (Card card : state.cards) {
      if (card == null) continue;
      if (card.location != Location.MY_BOARD) continue;
      
      
      // attack guard cards
      if (!opponentBoardGuardCards.isEmpty()) {
        // attack guard cards 1st
        Card target = opponentBoardGuardCards.get(0);
        actions.add(Action.attack(card, target));
        target.defense -= card.attack;
        if (target.defense <= 0) {
          opponentBoardGuardCards.remove(target);
          opponentBoardCards.remove(target);
        }
        continue;
      }
      
      // attack non guard cards
      if (!opponentBoardCards.isEmpty()) {
        Card target = opponentBoardCards.get(0);
        actions.add(Action.attack(card, target));
        target.defense -= card.attack;
        if (target.defense <= 0) {
          opponentBoardGuardCards.remove(target);
          opponentBoardCards.remove(target);
        }
        continue;
      }
      
      
      
      if (!state.opp.hasGuard() || opponentBoardCards.isEmpty()) {
        actions.add(Action.attack(card, Card.opponent));
      }
      
    }
  }

  private int myAttack(State state) {
    int attack = 0;
    for (Card card : state.cards) {
      if (card == null) continue;
      if (card.location != Location.MY_BOARD) continue;
      attack+=card.attack;
    }
    return attack;
  }

  @Override
  public void output(State state) {
    for (Action action : actions) {
      action.print(state, System.out);
      System.out.print(";");
    }
    System.out.println();
  }
}
