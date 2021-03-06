package lcm.ai.beam;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import lcm.Player;
import lcm.State;
import lcm.ai.eval.IEval;
import lcm.cards.Card;
import lcm.cards.Location;
import lcm.predictors.Oracle;
import lcm.sim.Action;
import lcm.sim.Cache;
import lcm.sim.Simulation;

public class BeamSearch implements IBeamSearch {
  private int myMaxLayer;
  private BSLayer[] layers = new BSLayer[14];
  private double minimizedScores[][] = new double[6][CHILD_SIZE];
  private BSLayer[] his1stLayers = new BSLayer[14];
  private BSLayer[] my2ndLayers = new BSLayer[14];
  {
    for (int i = 0; i < layers.length; i++) {
      layers[i] = new BSLayer();
      his1stLayers[i] = new BSLayer();
      my2ndLayers[i] = new BSLayer();
    }
  }
  private BSNode hisBest;
  IEval eval;
  private Oracle oracle;
  public BSNode bestNode;

  public BeamSearch(Oracle oracle, IEval eval) {
    this.oracle = oracle;
    this.eval = eval;
  }

  public BSNode getBestNode() {
    return bestNode;
  }
  /* (non-Javadoc)
   * @see lcm.ai.beam.IBeamSearch#think(lcm.State)
   */
  @Override
  public void think(State state) {

    initCaches();
    initLayers();

    buildMyLayers(state);

    // prepare cards for his hand
    List<Card> fakeCards = new ArrayList<>();
    // if (state.opp.maxMana <4) {
    // fakeCards.add(Card.card2);
    // fakeCards.add(Card.card2);
    // } else if (state.opp.maxMana < 6) {
    // fakeCards.add(Card.card2);
    // fakeCards.add(Card.card2);
    // fakeCards.add(Card.card4);
    // } else if (state.opp.maxMana < 8) {
    // fakeCards.add(Card.card2);
    // fakeCards.add(Card.card2);
    // fakeCards.add(Card.card4);
    // fakeCards.add(Card.card6);
    // } else {
    // fakeCards.add(Card.card2);
    // fakeCards.add(Card.card2);
    // fakeCards.add(Card.card4);
    // fakeCards.add(Card.card4);
    // fakeCards.add(Card.card6);
    // fakeCards.add(Card.card8);
    // }

    bestNode = layers[myMaxLayer].bestNode();
    bestNode = minimizedMyScores(false, 0, fakeCards, layers[myMaxLayer], his1stLayers);

    // for (int i=1;i<5;i++) {
    // List<Card> cards = oracle.giveMeCards(5);
    // minimizedMyScores(i, cards);
    // }
    // BSCache.printInfo();
  }

  private void initLayers() {
    for (int i = 0; i < layers.length; i++) {
      layers[i].nodesFE = 0;
      his1stLayers[i].nodesFE = 0;
      my2ndLayers[i].nodesFE = 0;
    }
  }

  /**
   * After this pass, we have best with our bestNode & minimizedScores is filled
   * REturn the best node of this pass
   */
  private BSNode minimizedMyScores(boolean maximizer, int tryIndex, List<Card> handCards, BSLayer previouslayer, BSLayer layers[]) {
    BSNode best;
    if (Player.DEBUG_MINIMIZATION) {
      if (maximizer) {
        System.err.println("Minimization : Trying with hand cards : ");
      } else {
        System.err.println("Maximizer : Trying with hand cards : ");
      }
      for (Card c : handCards) {
        c.debugInput();
      }
      System.err.println("%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%");
    }
    best = null;
    hisBest = null;
    double bestScore = Double.NEGATIVE_INFINITY;
    int i = 0;
    for (i = 0; i < previouslayer.nodesFE; i++) {
      long elapsedTime = System.currentTimeMillis() - Player.start;
      if (!Player.DEBUG_CUT_BEAM && elapsedTime > 90) {
        break;
      }
      BSLayer.initZobrist();
      BSNode n = previouslayer.nodes[i];
      if (Player.DEBUG_BEAM) {
        debugMaximizer(i, n);
      }
      State base = prepareNextTurn(n.state, maximizer, handCards);
      int currentMaximumLayer = expand(base, layers, maximizer, Player.USE_CUTOFF ? bestScore : Double.NEGATIVE_INFINITY, eval);
      if (currentMaximumLayer == -1) {
        minimizedScores[tryIndex][i] = Double.NEGATIVE_INFINITY;
        if (Player.DEBUG_BEAM) {
          System.err.println("Score is worse for me, won't take this action");
        }
        continue;
      }
      // temporary score from this layer
      // worst node as minimizer try to minize
      hisBest = layers[currentMaximumLayer].worstNode();
      double score = hisBest.score;
      minimizedScores[tryIndex][i] = score;
      if (Player.DEBUG_BEAM) {
        System.err.println("TOTAL SCORE : " + score);
        debugMinimizer(n, layers[currentMaximumLayer], score);
      }
      // si le score minimizé est meilleur que le best, on change
      if (score > bestScore) {
        if (Player.DEBUG_BEAM) {
          System.err.println("%%%% CHANGING BEST %%%%");
        }
        bestScore = score;
        best = n;
      }
    }
    return best;
  }

  private State prepareNextTurn(State from, boolean maximizer, List<Card> handCards) {
    State base = Cache.popState();
    base.copyFrom(from);
    base.turn++;
    if (maximizer) {
      base.me.maxMana++;
    } else {
      // if i'm second to play the minimizer will have 1 mana more
      if (from.side == State.SECOND) {
        base.opp.maxMana++;
      }
    }
    base.opp.nextTurnDraw = 1;
    base.opp.mana = base.opp.maxMana;
    if (!handCards.isEmpty()) {
      for (Card card : handCards) {
        card.location = Location.HIS_HAND;
        base.addCard(card);
      }
    }
    return base;
  }

  private void initCaches() {
    Cache.initCache();
    BSCache.init();
    BSLayer.initZobrist();
  }

  private void buildMyLayers(State state) {
    myMaxLayer = expand(state, layers, true, Double.NEGATIVE_INFINITY, eval);
    // sort the layer to help a/b cutoff to maximum
    Arrays.sort(layers[myMaxLayer].nodes, 0, layers[myMaxLayer].nodesFE, new Comparator<BSNode>() {

      @Override
      public int compare(BSNode o1, BSNode o2) {
        return Double.compare(o2.score, o1.score);
      }
    });
  }

  private void debugMaximizer(int index, BSNode n) {
    System.err.println("------------------------------------");
    System.err.println("["+index+"] My attack : " + n.score);
    System.err.println(n.toString()); // n.debugActions();
    System.err.println();
  }

  private void debugMinimizer(BSNode n, BSLayer layer, double score) {
    System.err.println(">> Counter attack : " + score);
    System.err.println(layer.worstNode());
    System.err.println();
    System.err.println("------------------------------------");
  }

  /**
   * return the best score from the initial node
   * 
   * @param myTerminalNode
   * @return
   */
  private int expand(State initialState, BSLayer[] layers, boolean maximizer, double limit, IEval eval) {
    BSLayer rootLayer = layers[0];
    rootLayer.nodesFE = 0;
    BSNode rootNode = BSCache.pop();
    rootNode.state.copyFrom(initialState);
    rootNode.action = Action.endTurn();
    rootNode.score = eval.eval(initialState, false);
    rootLayer.addNodeWithEviction(rootNode, maximizer);
    int maxLayer = 0;

    for (int i = 0; i < 14; i++) {
      maxLayer = i + 1;
      int result = layers[i].expand(layers[i + 1], maximizer, limit, eval);
      if (result == 0) {
        // no more node to expand
        break;
      } else {
        if (result == -1) {
          // limit was reached
          return -1;
        }
      }
    }
    return maxLayer;
  }

  /* (non-Javadoc)
   * @see lcm.ai.beam.IBeamSearch#getResultActions()
   */
  @Override
  public List<Action> getResultActions() {
    return bestNode.getActions();
  }

  /* (non-Javadoc)
   * @see lcm.ai.beam.IBeamSearch#output(lcm.State)
   */
  @Override
  public void output(State state) {
    if (Player.DEBUG_BEAM_RESULT) {
      System.err.println("--------------- RESULTS ----------------");
      debugOutput(state);
    }

    if (bestNode == null) {
      System.out.println("PASS I surrender, you are the best ");
      return;
    }

    List<Action> actions = bestNode.getActions();

    Simulation sim = new Simulation();
    State result = sim.simulate(state, actions);
    eval.eval(result, true);

    for (Action action : actions) {
      action.print(state, System.out);
      System.out.print(";");
    }
    System.out.println();
  }

  private void debugOutput(State state) {
    if (bestNode != null) {
      System.err.println("-------------------------------------");
      System.err.println("My Actions :");
      System.err.println(bestNode);
    }
    System.err.println("-------------------------------------");
    if (hisBest != null) {
      System.err.println("Predicted replica (without summon) : ");
      System.err.println(hisBest);
      System.err.println("-------------------------------------");
    }
  }
}
