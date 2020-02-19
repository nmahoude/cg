package lcm.ai.mc;

import java.util.ArrayList;
import java.util.List;

import lcm.PlayerOld;
import lcm.State;
import lcm.ai.AI;
import lcm.ai.eval.IEval;
import lcm.cards.Card;
import lcm.cards.Location;
import lcm.predictors.Oracle;
import lcm.sim.Action;
import lcm.sim.Cache;
import lcm.sim.LegalActionGenerator;
import lcm.sim.Simulation;

public class MCAI implements AI {
  private List<Action> actions = new ArrayList<>();
  private Oracle oracle;
  private IEval eval;

  public MCAI(Oracle oracle, IEval eval) {
    this.oracle = oracle;
    this.eval = eval;
  }

  @Override
  public void think(State state) {
    summonCards(state);
    attack(state);
  }

  private void attack(State state) {
    Cache.initCache();
    double bestScore = Double.NEGATIVE_INFINITY;
    List<Action> currentActions = new ArrayList<>();
    List<Action> bestAttackActions = new ArrayList<>();
    Action legalActions[] = new Action[100];
    Simulation sim = new Simulation();
    State copy = new State();

    for (int iter = 0; iter < 10000; iter++) {
      copy.copyFrom(state, true);
      currentActions.clear();

      int legalActionsFE = LegalActionGenerator.updateLegalActions(legalActions, copy, Action.endTurn(), true);
      for (int i = 0; i < legalActionsFE; i++) {
        int rand = PlayerOld.random.nextInt(legalActionsFE);
        Action todo = legalActions[rand];
        if (todo == null)
          continue;
        if (todo == Action.endTurn())
          break;

        currentActions.add(todo);
        sim.simulate(copy, todo);

        legalActionsFE = LegalActionGenerator.updateLegalActions(legalActions, copy, Action.endTurn(), true);
      }
      // measure the score
      int myDefense = 0, myAttack = 0;
      int hisDefense = 0, hisAttack = 0;
      for (int i = 0; i < copy.cardsFE; i++) {
        Card c = copy.cards[i];
        if (c == null || c.defense <= 0)
          continue;
        if (c.location == Location.MY_BOARD) {
          myDefense += c.defense;
          myAttack += c.attack;
        }
        if (c.location == Location.HIS_BOARD) {
          hisDefense += c.defense;
          hisAttack += c.attack;
        }
      }
      double score = copy.me.health +
          myAttack + 10 * myDefense -
          (10 * hisAttack + hisDefense
              + copy.opp.health);
      if (score > bestScore) {
        bestAttackActions.clear();
        bestAttackActions.addAll(currentActions);
        bestScore = score;
      }
    }
    
    this.actions.addAll(bestAttackActions);
  }

  private void summonCards(State state) {
    for (Card card : state.cards) {
      if (card == null) continue;
      if (card.location != Location.MY_HAND) continue;
      
      if (card.cost <= state.me.mana) {
        actions.add(Action.summon(card));
        state.me.mana -= card.cost;
      }
    }    
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
