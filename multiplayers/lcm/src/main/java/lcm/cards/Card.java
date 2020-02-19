package lcm.cards;

import java.util.Scanner;

import lcm.Agent;
import lcm.PlayerOld;

public class Card {
  public static Card opponent = new Card() {
    {
      stateIndex = -1;
      id = -1;
    }
  };

  public int id;
  public int stateIndex; // to help retrieve the card in the state
  public CardType type;
  public int cost;
  public int myHealthChange;
  public int opponentHealthChange;
  public int cardDraw;

  public Location location;
  public int abilities;
  public int attack;
  public int defense;
  public boolean canAttack = true;
  public boolean hasAttacked = false;

  public double score = -1; // not calculated yet

  public int instanceId;

  public long hash;

  public final void copyFrom(final Card model) {
    stateIndex = model.stateIndex;
    instanceId = model.instanceId;
    id = model.id;
    type = model.type;
    cost = model.cost;
    cardDraw = model.cardDraw;
    myHealthChange = model.myHealthChange;
    opponentHealthChange = model.opponentHealthChange;

    location = model.location;
    abilities = model.abilities;
    attack = model.attack;
    defense = model.defense;
    canAttack = model.canAttack;

    hasAttacked = model.hasAttacked;
  }

  public void read(Scanner in) {
    instanceId = in.nextInt();
    id = in.nextInt();
    location = Location.fromValue(in.nextInt());
    type = CardType.fromValue(in.nextInt());
    cost = in.nextInt();
    attack = in.nextInt();
    defense = in.nextInt();
    abilities = Abilities.read(in.next());
    myHealthChange = in.nextInt();
    opponentHealthChange = in.nextInt();
    cardDraw = in.nextInt();

    initTurn();
    if (PlayerOld.DEBUG_INPUT) {
      debugInput();
    }
  }

  public String inputString() {
    return         String.format("%-3d %-2d %d %d %d %d %d %-6s %d %d %d",
        instanceId,
        id,
        location.intValue(),
        type.intValue(),
        cost,
        attack,
        defense,
        Abilities.toString(abilities),
        myHealthChange,
        opponentHealthChange,
        cardDraw);
  }

  public void debugInput() {
    System.err.println("\""+inputString()+"\"+NL+");
  }

  @Override
  public String toString() {
    return String.format("id:%d cost:%d abs:%s", id, cost, abilities);
  }

  public final boolean isGuard() {
    return (abilities & Abilities.GUARD) != 0;
  }

  public boolean isLethal() {
    return (abilities & Abilities.LETHAL) != 0;
  }

  
  public boolean hasWard() {
    return (abilities & Abilities.WARD) != 0;
  }

  public boolean hasCharge() {
    return (abilities & Abilities.CHARGE) != 0;
  }

  public boolean isCreature() {
    return type == CardType.CREATURE;
  }

  public boolean isBlue() {
    return type == CardType.ITEM_BLUE;
  }

  public boolean isRed() {
    return type == CardType.ITEM_RED;
  }

  public boolean isGreen() {
    return type == CardType.ITEM_GREEN;
  }

  public void applyItem(Agent agent, Card item) {
    if (item.type == CardType.ITEM_GREEN) {
      if ((item.abilities & Abilities.GUARD) != 0 && (this.abilities & Abilities.GUARD) == 0) {
        agent.guardsCardsCount++;
      }
      abilities = Abilities.upgrade(abilities, item);
      if ((item.abilities & Abilities.CHARGE) != 0) {
        canAttack = !hasAttacked;
      }
    } else {
      if ((item.abilities & Abilities.GUARD) != 0 && (this.abilities & Abilities.GUARD) != 0) {
        agent.guardsCardsCount--;
      }

      abilities = Abilities.downgrade(abilities, item);
    }

    attack = Math.max(0, attack + item.attack);
    if ((abilities & Abilities.WARD) != 0 && item.defense < 0)
      abilities = abilities & ~Abilities.WARD;
    else
      defense += item.defense;

    if (defense <= 0) {
      if ((this.abilities & Abilities.GUARD) != 0) {
        agent.guardsCardsCount--;
      }
      location = Location.GRAVEYARD;
      agent.decreaseCardOnBoard();
    }
  }

  public void initTurn() {
    canAttack = true;
    hasAttacked = false;
  }

  public void summon(Location location) {
    this.location = location;
    hasAttacked = false;
    canAttack = (abilities & Abilities.CHARGE) != 0;
  }

  public double faceValue() {
    return 0.0
        + Math.abs(attack)
        + Math.abs(defense)
        + ((abilities & Abilities.GUARD) != 0 ? defense : 0)
        + ((abilities & Abilities.DRAIN) != 0 ? attack : 0)
        + ((abilities & Abilities.BREAKTHROUGH) != 0 ? 3 : 0)
        + ((abilities & Abilities.LETHAL) != 0 ? 8 : 0)
        + ((abilities & Abilities.CHARGE) != 0 ? attack : 0)
        + ((abilities & Abilities.WARD) != 0 ? defense : 0)
        + (Math.abs(opponentHealthChange))
        + (Math.abs(myHealthChange))
        + (cardDraw)
        - (2 * cost + 1);
  }

  /**
   * Zobrist
   */
  private static long cardZobrist[] = null;
  private static long zobrist[][][][][] = null;

  public static void initZobrist() {
    if (zobrist != null) return;
    
    cardZobrist = new long[200];
    zobrist = new long[2][32][32][64][2];
    for (int i = 0; i < 200; i++) {
      cardZobrist[i] = PlayerOld.random.nextLong();
    }
    for (int l=0;l<2;l++) {
      for (int hp = 0; hp < 32; hp++) {
        for (int att = 0; att < 32; att++) {
          for (int abilities = 0; abilities < 0b111111; abilities++) {
            for (int canAttack = 0; canAttack < 2; canAttack++) {
              zobrist[l][hp][att][abilities][canAttack] = PlayerOld.random.nextLong();
            }
          }
        }
      }
    }
  }

  public void calculateHash() {
    hash = getHash();
  }

  public long getHash() {
    if (location != Location.HIS_BOARD && location != Location.MY_BOARD) {
      return cardZobrist[this.instanceId];
    } else {
      // value may vary
      // try {
      int l = (this.location == Location.MY_BOARD ? 0 : 1);
      return zobrist[l][defense][attack][abilities][canAttack ? 1 : 0];
      // } catch (Exception e) {
      // System.err.println(""+this);
      // System.err.println("Exception in zobrist !");
      // System.err.println("zobrist : "+zobrist);
      // System.err.println(""+defense+" / "+attack+" / "+abilities+" /
      // "+canAttack);
      // throw e;
      // }
    }
  }

  public static int precalculatedFaceValue[] = {
      0, 
      // closetAI : 
      124, 74, 148, 101, 141, 140, 168, 139, 118, 30, 114, 84, 85, 29, 70, 57, 95, 152, 111, 34, 94, 59, 103, 55, 88, 134, 87, 153, 160, 86, 46, 151, 116, 53, 44, 31, 110, 129, 121, 39, 60, 26, 42, 147, 27, 48, 119, 170, 169, 150, 166, 142, 164, 157, 20, 51, 33, 54, 90, 35, 77, 69, 56, 146, 167, 126, 143, 165, 161, 99, 50, 62, 76, 49, 115, 37, 71, 40, 72, 155, 81, 128, 132, 158, 137, 93, 109, 123, 66, 58, 133, 15, 125, 98, 144, 136, 107, 82, 138, 63, 47, 36, 127, 78, 117, 102, 25, 45, 108, 13, 97, 68, 21, 120, 91, 159, 28, 122, 100, 67, 89, 65, 43, 19, 73, 92, 61, 79, 113, 38, 32, 16, 131, 64, 105, 75, 106, 17, 162, 11, 145, 22, 23, 156, 96, 52, 135, 130, 24, 154, 163, 104, 14, 41, 112, 18, 80, 149, 83, 12
      // Mine :
      // 104,109,154,113,96,129,168,136,142,43,130,125,78,33,120,60,116,153,146,30,118,82,141,63,93,119,100,99,124,91,45,117,98,54,29,66,123,128,112,80,89,32,48,156,51,52,94,162,164,145,166,158,161,151,110,75,39,74,95,42,88,49,157,163,169,155,152,167,160,114,76,81,90,55,127,53,111,22,87,137,107,101,105,165,132,102,143,135,71,72,115,36,97,133,149,122,106,121,150,144,59,34,138,69,92,85,17,28,140,44,73,64,16,86,67,126,46,56,40,68,61,21,35,31,23,38,14,27,19,13,26,24,41,65,15,50,47,18,83,57,148,62,20,147,77,58,103,139,37,134,159,108,10,25,84,11,79,131,70,12,
      // Closet rework
      // 124,74,149,101,140,140,166,139,118,30,114,84,85,29,95,57,95,153,108,34,94,59,103,55,88,134,87,148,157,86,46,152,116,53,44,31,110,129,121,39,60,26,42,147,27,48,119,168,170,150,165,141,169,154,20,51,33,54,90,35,77,69,56,145,164,126,146,167,161,99,80,62,76,49,115,37,71,40,72,158,81,128,132,159,137,93,109,123,66,58,133,15,125,98,144,136,107,82,138,63,47,36,127,78,117,102,25,45,108,13,97,68,21,120,91,163,28,100,100,67,89,65,43,19,73,92,61,79,113,38,32,16,131,64,105,75,106,17,160,11,143,22,23,156,96,52,135,155,24,142,162,104,14,41,112,18,60,151,83,12,
  };


  // dumb cards
  public final static Card card2;
  public final static Card card4;
  public final static Card card6;
  public final static Card card8;
  static {
    card2 = new Card();
    card2.attack = 2; card2.defense = 2; card2.cost = 2;
    card4 = new Card();
    card4.attack = 4; card4.defense = 4; card4.cost = 4;
    card6 = new Card();
    card6.attack = 6; card6.defense = 6; card6.cost = 6;
    card8 = new Card();
    card8.attack = 8; card8.attack = 8; card8.cost = 8;
  }
}
