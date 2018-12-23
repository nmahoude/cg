package offline;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

import lcm.Player;
import lcm.State;
import lcm.cards.Card;
import lcm.cards.CardTriplet;
import lcm.cards.Location;
import lcm.fixtures.CardFixture;
import lcm.sim.Action;
import lcm.sim.Simulation;

public class MatchMaker {
  static Simulation simulation = new Simulation();

  static class Information {
    static Simulation sim = new Simulation();
    Player player;
    int nextId = 0;
    List<Card> pickedCards = new ArrayList<>();
    List<Card> deck = new ArrayList<>();
    
    List<Card> handCards = new ArrayList<Card>();
    List<Card> boardCards = new ArrayList<Card>();
    int health = 30;
    int mana = 0;
    int nextPick = 1;
    public int rune = 5;
    public int maxMana = 0;
    
    public Information(int side) {
      if (side ==0) 
        nextId = 0;
      else 
        nextId = 1;
    }
    
    public void pickCards() {
      int toPick = Math.min(deck.size(), Math.min(nextPick, 8-handCards.size()));
      deal(toPick);
      nextPick = 1;
    }

    public void deal(int count) {
      for (int i=0;i<count;i++) {
        Card card = deck.remove(0);
        card.id = nextId;
        card.location = Location.MY_HAND;
        nextId += 2;

        handCards.add(card);
      }
    }

    public void send(String input) {
      player.state.read(new Scanner(input));
      player.playBattle();
    }

    public int sendTriplet(CardTriplet triplet, Information other) {
      StringBuilder sb = new StringBuilder(buildPlayersInfo(this, other, 3));
      
      for (Card card : triplet.cards) {
        card.location = Location.GRAVEYARD;
        sb.append(" ");
        sb.append(card.inputString());
      }
      
      player.state.read(new Scanner(sb.toString()));
      player.playDraft();
      return player.state.me.actions.get(0).from;
    }

    public void remove(Card card) {
      removeFromList(card, handCards);
      removeFromList(card, boardCards);
    }

    private Card removeFromList(Card card, List<Card> cards) {
      Iterator<Card> ite = cards.iterator();
      while (ite.hasNext()) {
        Card c = ite.next();
        if (c.id == card.id) {
          ite.remove();
          return c;
        }
      }
      return null;
    }

    public void transferToBoard(Card card) {
      Card c = removeFromList(card, handCards);
      if (c != null) {
        boardCards.add(c);
      }
    }
  }
  
  
  Random  random;
  private Information informations[];
  private int turn;
  
  int getTurn() {
    return turn;
  }
  
  public MatchMaker(long seed) {
    random = new Random(seed);
  }
  
  int doMatch(Player p0, Player p1) {
    p0.init();
    p1.init();
    
    informations = new Information[] { new Information(0), new Information(1) };
    informations[0].player = p0;
    informations[1].player = p1;
    
    resolveDraft();
    shuffleDecks();
    distributeInitialCards();
    
    turn = 0;
    while(!gameFinished()) {
      turn++;
      resolve(informations[0], informations[1]);
      
      if (gameFinished()) break;

      resolve(informations[1], informations[0]);
      
    }
    int winner = informations[1].health <= 0 ? 0 : 1;
    //System.err.println("Match end in "+turn+" turns. health : "+informations[0].health +" / "+informations[1].health);
    updateStats(winner, informations);
    
    return winner;
  }

  static int stats[][] = new int[2][161];
  static long count[] = new long[161];
  
  private static void updateStats(int winner, Information informations[]) {
    for (Card card : informations[winner].pickedCards) {
      stats[winner][card.instanceId]++;
      count[card.instanceId]++;
    }
    for (Card card : informations[1-winner].pickedCards) {
      stats[1-winner][card.instanceId]--;
      count[card.instanceId]++;
    }
  }
  
  public static void printStats() {
    for (int i=0;i<161;i++) {
      System.err.println(""+i+" => p1="+ stats[0][i] + " p2="+stats[1][i] +  " ( out of "+count[i]+")");
    }
  }

  private void distributeInitialCards() {
    informations[0].deal(4);
    informations[1].deal(5);
  }

  private void resolve(Information player, Information other) {
    player.pickCards();
    String input = buildInput(player, other);
    List<Action> actions = Collections.emptyList();
    try {
      player.send(input);
      if (player.player.ai.getBestNode() != null && player.player.ai.getBestNode().getActions() != null) {
        actions = player.player.ai.getBestNode().getActions();
      }
    } catch(Exception e ) {
      e.printStackTrace();
    }
    
    State state = new State();
    prepareState(player, other, state);
    State result = simulation.simulate(state, actions);
    simulation.simulate(result, Action.endTurn());
    if (result.me.health <0 || result.opp.health < 0) {
      System.err.println("ERROR");
    }
    removeCards(player, other, result);
    
    copyAftermath(player, other, result);
  }


  private void removeCards(Information p0, Information p1, State state) {
    for (int i=0;i<state.cardsFE;i++) {
      Card card = state.cards[i];
      if (card.location == Location.GRAVEYARD) {
        p0.remove(card);
        p1.remove(card);
      } else if (card.location == Location.MY_BOARD) {
        p0.transferToBoard(card);
      }      
    }
  }

  private void copyAftermath(Information player, Information other, State state) {
    player.health = state.me.health;
    player.nextPick = state.me.nextTurnDraw;
    player.rune = state.me.rune;
    player.maxMana +=1;
    player.mana = player.maxMana;

    other.health = state.opp.health;
    other.nextPick = state.opp.nextTurnDraw;
    other.rune  = state.opp.rune;
  }

  private String buildInput(Information player, Information other) {
    StringBuilder sb = new StringBuilder(buildPlayersInfo(player, other, player.handCards.size() + player.boardCards.size() + other.boardCards.size()));

    for (Card card : player.handCards) {
      card.location = Location.MY_HAND;
      sb.append(card.inputString());
      sb.append(" ");
    }
    for (Card card : player.boardCards) {
      card.location = Location.MY_BOARD;
      sb.append(card.inputString());
      sb.append(" ");
    }
    for (Card card : other.boardCards) {
      card.location = Location.HIS_BOARD;
      sb.append(card.inputString());
      sb.append(" ");
    }
    
    return sb.toString();
  }

  private static String buildPlayersInfo(Information player, Information other, int cardCount) {
    
    StringBuilder sb = new StringBuilder();
    sb.append(player.health).append(" ").append(player.mana).append(" ").append(player.deck.size()).append(" ").append(player.rune * 5).append(" ");
    sb.append(other.health).append(" ").append(other.mana).append(" ").append(other.deck.size()).append(" ").append(other.rune * 5).append(" ");
    
    sb.append(other.handCards.size()).append(" ").append(cardCount).append(" ");
    return sb.toString();
  }

  private void prepareState(Information player, Information other, State state) {
    state.me.health = player.health;
    state.me.nextTurnDraw = player.nextPick;
    state.me.rune = player.rune;
    state.me.maxMana = player.maxMana;
    state.me.mana = player.maxMana;
    state.me.deck = player.deck.size();
    
    state.opp.health = other.health;
    state.opp.nextTurnDraw = other.nextPick;
    state.opp.rune = other.rune;
    state.opp.deck = other.deck.size();
    
    for (Card card : player.handCards) {
      card.location = Location.MY_HAND;
      state.addCard(card);
    }
    for (Card card : player.boardCards) {
      card.location = Location.MY_BOARD;
      state.addCard(card);
    }
    for (Card card : other.boardCards) {
      card.location = Location.HIS_BOARD;
      state.addCard(card);
    }
  }

  private void shuffleDecks() {
    for (int i=0;i<2;i++) {
      Collections.shuffle(informations[i].deck, random);
    }
  }

  private boolean gameFinished() {
    return informations[0].health <=0 || informations[1].health <= 0;
  }

  private void resolveDraft() {
    //System.err.println("Draft");

    for (int draft=0;draft<30;draft++) {
      // choose 3 cards
      CardTriplet triplet = buildRandomTriplet();
      //System.err.print(triplet.cards[0].instanceId + " "+ triplet.cards[1].instanceId+" "+triplet.cards[2].instanceId);
      for (int i=0;i<2;i++) {
        int index = informations[i].sendTriplet(triplet, informations[1-i]);
        //System.err.print(""+index+" ");
        Card card = new Card();
        card.copyFrom(triplet.cards[index]);
        informations[i].pickedCards.add(card);
        informations[i].deck.add(card);
      }
      //System.err.println();
    }
  }

  private CardTriplet buildRandomTriplet() {
    CardTriplet triplet = new CardTriplet(null, null, null);
    for (int i=0;i<3;i++) {
      Card card = null;
      do {
        card = CardFixture.card(1 + random.nextInt(160), -1);
      } while (triplet.hasCard(card));
      triplet.cards[i] = card;
    }
    return triplet;
  }
}
