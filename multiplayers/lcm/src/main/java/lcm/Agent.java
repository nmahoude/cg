package lcm;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import direct_ai.Picker;
import lcm.cards.Abilities;
import lcm.cards.Card;
import lcm.cards.Location;
import lcm.sim.Action;

public class Agent {
  Location myBoardLocation;
  public State state;
  
  public int id;
  public int health = 30;
  public int mana = 0; 
  public int maxMana = 0;
  public int rune = 5;
  public int nextTurnDraw = 1;
  
  public int handCardsCount;
  private int boardCardsCount = 0;
  public int guardsCardsCount = 0;
  public int deck;
  public int playerDraw;

  public List<Card> pickedCards = new ArrayList<>();
  public List<Action> actions = new ArrayList<>();

  public int getBoardCardsCount() {
    return boardCardsCount;
  }
  
  public Agent(State state,int id) {
    this.state = state;
    this.id = id;
    if (id == 0) {
      myBoardLocation = Location.MY_BOARD;
    } else {
      myBoardLocation = Location.HIS_BOARD;
    }
  }
  
  public void copyFrom(State state, Agent model) {
    this.state = state;
    this.myBoardLocation = model.myBoardLocation;
    this.id = model.id;
    this.health = model.health;
    this.mana = model.mana;
    this.maxMana = model.maxMana;
    this.rune = model.rune;
    this.nextTurnDraw = model.nextTurnDraw;
    
    this.handCardsCount = model.handCardsCount;
    this.boardCardsCount = model.boardCardsCount;
    this.guardsCardsCount = model.guardsCardsCount;
    this.deck = model.deck;
    this.playerDraw = model.playerDraw;

  }

  public void read(Scanner in) {
    health = in.nextInt();
    Player.start = System.currentTimeMillis(); // will read it 2 times (me & opp), but that should not matter
    mana = in.nextInt();
    maxMana = mana;
    deck = in.nextInt();
    rune = in.nextInt() / 5;
    playerDraw = in.nextInt();
    boardCardsCount = 0;
    handCardsCount = 0;
    guardsCardsCount = 0;
    
    if (Player.DEBUG_INPUT) {
      System.err.print(String.format("\"%d %d %d %d\"+NL+",
          health,
          mana,
          deck,
          (5 * rune)
          ));
    }
  }

  public void turnInit() {
    nextTurnDraw = 1;
  }

  public void printActions() {
    for (Action action: actions) {
      action.print(state, System.out);
      System.out.print(";");
    }
    System.out.println();
  }

  public void pickCard(Picker picker) {
    actions.clear();
    int index = picker.pick(this, State.triplet);
    Card bestCard = State.triplet.cards[index];
    pickedCards.add(bestCard);
    actions.add(Action.pick(index));
  }

  public void modifyHealth(int mod) {
    health += mod;

    if (mod >= 0) return;
    if (health < 0) health = 0;
    
    int h2r = health / 5;
    if (h2r <= rune) {
      nextTurnDraw += (rune-h2r);
      rune = h2r;
    }
  }

  /**
   * @return
   */
  public boolean hasGuard() {
    return this.guardsCardsCount > 0;
  }

  public void summon(Card card) {
    card.summon(myBoardLocation);
    handCardsCount--;
    boardCardsCount++;
    if ((card.abilities & Abilities.GUARD) != 0) guardsCardsCount++;
  }

  public void kill(Card card) {
    if (card.location == Location.GRAVEYARD) {
      return;
    } else {
      removeBoardCard(card);
    }
  }

  private void removeBoardCard(Card card) {
    boardCardsCount--;
    if ((card.abilities & Abilities.GUARD) != 0) guardsCardsCount--;
    card.defense = 0;
    card.location = Location.GRAVEYARD;
  }

  public void removeHandCard(Card item) {
    item.location = Location.GRAVEYARD;
    item.canAttack = false;
    handCardsCount--;
  }
  
  /**
   * Zobrist
   */
  private static long zobrist[][];
  public static void initZobrist() {
    if (zobrist != null) return;
    
    zobrist = new long[31][13 * 256];
    for (int deck=0;deck<31;deck++) {
      for (int hp = 0;hp<256;hp++) {
        for (int mana=0;mana<13;mana++) {
          zobrist[deck][13 * hp + mana] = Player.random.nextLong();
        }
      }
    }
  }
  public long getHash() {
    return zobrist[deck][13 * health + mana];
  }

  public void decreaseCardOnBoard() {
    boardCardsCount--;
  }

  public void incrementCoardsOnBoard() {
    boardCardsCount++;
  }

}
