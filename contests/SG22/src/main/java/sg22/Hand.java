package sg22;

import fast.read.FastReader;

public class Hand {
  public static final int CARDS_BY_LOC = 11;
  private static final int LOCS = 5;
  private static final Hand emptyHand= new Hand();
  
  public static final int LOC_HAND = 0;
  public static final int LOC_AUTOMATED = 1;
  public static final int LOC_DRAW = 2;
  public static final int LOC_PLAYEDCARDS = 3;
  public static final int LOC_DISCARD = 4;
  
  public static final int TRAINING = 0;
  public static final int CODING = 1;
  public static final int DAILY_ROUTINE = 2;
  public static final int TASK_PRIORIZATION = 3;
  public static final int ARCHITECTURE_STUDY = 4;
  public static final int CI = 5;
  public static final int CODE_REVIEW = 6;
  public static final int REFACTORING = 7;
  public static final int BONUS = 8;
  public static final int DEBT = 9;
  public static final int BACLEE = 10;
  
  final int[] cards = new int[CARDS_BY_LOC * LOCS];
  public int cardsToDraw = 0;
  
  public void copyFrom(Hand model) {
    System.arraycopy(model.cards, 0, this.cards, 0, CARDS_BY_LOC * LOCS);
    this.cardsToDraw = model.cardsToDraw;
  }
  
  public void read(int locIndex, FastReader in) {
    int decal = locIndex * CARDS_BY_LOC;
    
    cardsToDraw = 0;
    int baclee = 0;
    
    for (int i=0;i<10;i++) {
      int count = in.nextInt();
      cards[decal + i] = count;
      
      if (i < BONUS ) baclee += 2*count;
      if (i == BONUS) baclee += 1*count;
    }
    cards[decal + BACLEE] = baclee;
    
  }

  public void put(int locIndex, int index) {
    put(locIndex, index, 1);
  }
  
  public void put(int locIndex, int cardIndex, int count) {
    this.cards[CARDS_BY_LOC *locIndex + cardIndex]+=count;

    if (cardIndex == BONUS) this.cards[CARDS_BY_LOC *locIndex + BACLEE] += count;
    else if (cardIndex == DEBT) this.cards[CARDS_BY_LOC *locIndex + BACLEE] += 0;
    else this.cards[CARDS_BY_LOC *locIndex + BACLEE] += 2*count;
  }
  
  public void remove(int locIndex, int index) {
    remove(locIndex, index, 1);
  }

  public void remove(int locIndex, int cardIndex, int count) {
    this.cards[CARDS_BY_LOC *locIndex + cardIndex]-=count;

    if (cardIndex == BONUS) this.cards[CARDS_BY_LOC *locIndex + BACLEE] -= count;
    else if (cardIndex == DEBT) this.cards[CARDS_BY_LOC *locIndex + BACLEE] -= 0;
    else this.cards[CARDS_BY_LOC *locIndex + BACLEE] -= 2*count;
  }

  
  public static String toName(int loc) {
    switch (loc) {
    case TRAINING: return "TRAINING";
    case CODING: return "CODING";
    case DAILY_ROUTINE : return "DAILY_ROUTINE";
    case TASK_PRIORIZATION : return "TASK_PRIO";
    case ARCHITECTURE_STUDY: return "ARCHI";
    case CI : return "CI";
    case CODE_REVIEW : return "CODE REVIEW";
    case REFACTORING : return "REFACTO";
    case BONUS  : return "BONUS";
    case DEBT : return "DEBT";
    case BACLEE : return "BACLEE ?!";
    }
    return "CARD ??? ";
  }
  
  public void removeDebt(int locIndex, int debtToRemove) {
    cards[CARDS_BY_LOC * locIndex + Cards.DEBT] = Math.max(0, cards[CARDS_BY_LOC * locIndex + Cards.DEBT] - debtToRemove);
  }


  public void addBonus(int locIndex, int toAdd) {
    this.cards[CARDS_BY_LOC * locIndex + Cards.BONUS]+=toAdd;
  }


  public void addDebt(int locIndex, int debtToAdd) {
    this.cards[CARDS_BY_LOC * locIndex + Cards.DEBT] += debtToAdd;
  }
  
  public void clear() {
    this.copyFrom(emptyHand);
  }
  
  
  public void debugCards(int locIndex, String location) {
    System.err.println(String.format("^ %-15s    %d %d %d %d %d %d %d %d %d %d", location, 
        this.cards[CARDS_BY_LOC * locIndex + 0], 
        this.cards[CARDS_BY_LOC * locIndex + 1], 
        this.cards[CARDS_BY_LOC * locIndex + 2], 
        this.cards[CARDS_BY_LOC * locIndex + 3], 
        this.cards[CARDS_BY_LOC * locIndex + 4], 
        this.cards[CARDS_BY_LOC * locIndex + 5], 
        this.cards[CARDS_BY_LOC * locIndex + 6], 
        this.cards[CARDS_BY_LOC * locIndex + 7], 
        this.cards[CARDS_BY_LOC * locIndex + 8], 
        this.cards[CARDS_BY_LOC * locIndex + 9] 
        ));
  }


  public int totalCount(int locIndex) {
    return         
        this.cards[CARDS_BY_LOC * locIndex +0]+ 
        this.cards[CARDS_BY_LOC * locIndex +1]+ 
        this.cards[CARDS_BY_LOC * locIndex +2]+ 
        this.cards[CARDS_BY_LOC * locIndex +3]+ 
        this.cards[CARDS_BY_LOC * locIndex +4]+ 
        this.cards[CARDS_BY_LOC * locIndex +5]+ 
        this.cards[CARDS_BY_LOC * locIndex +6]+ 
        this.cards[CARDS_BY_LOC * locIndex +7]+ 
        this.cards[CARDS_BY_LOC * locIndex +8]+
        this.cards[CARDS_BY_LOC * locIndex +9]
            ;
  }

  public int totalFromCard(int index) {
    return   cards[CARDS_BY_LOC * 0 + index]
            +cards[CARDS_BY_LOC * 1 + index]
            +cards[CARDS_BY_LOC * 2 + index]
            +cards[CARDS_BY_LOC * 3 + index]
            +cards[CARDS_BY_LOC * 4 + index];
  }

  public void debugCards() {
    debugCards(LOC_HAND, "HAND");
    debugCards(LOC_AUTOMATED, "AUTOMATED");
    debugCards(LOC_DRAW, "DRAW");
    debugCards(LOC_DISCARD, "DISCARD");
    debugCards(LOC_PLAYEDCARDS, "PLAYEDCARDS");
  }

  public void discardAllFromHand() {
    for (int i=0;i<11;i++) {
      cards[CARDS_BY_LOC * LOC_DISCARD + i] += cards[CARDS_BY_LOC * LOC_HAND + i]; 
      cards[CARDS_BY_LOC * LOC_HAND + i] = 0; 
    }
  }

  public int totalOfAllLocs(int cardIndex) {
    int count = 0;
    for (int loc=0;loc<LOCS;loc++) {
      count += cards[CARDS_BY_LOC * loc + cardIndex];
    }
    return count;
  }

  public int get(int locIndex, int cardIndex) {
    return cards[CARDS_BY_LOC * locIndex + cardIndex];
  }

  public int getHandCount(int cardIndex) {
    return cards[CARDS_BY_LOC * LOC_HAND + cardIndex];
  }

  public int getAliveCards() {
    int total = 0;
    for (int i=0;i<=Cards.BONUS;i++) {
      total += (cards[CARDS_BY_LOC * LOC_HAND + i] + cards[CARDS_BY_LOC * LOC_DISCARD + i] + cards[CARDS_BY_LOC * LOC_PLAYEDCARDS + i] + cards[CARDS_BY_LOC * LOC_DRAW + i]);
    }
    return total;
  }

  public int getDebtCards() {
    return cards[CARDS_BY_LOC * LOC_HAND + Cards.DEBT] + cards[CARDS_BY_LOC * LOC_DISCARD + Cards.DEBT] + cards[CARDS_BY_LOC * LOC_DRAW + Cards.DEBT];
  }

  public int getAliveCards(int locHand) {
    int total = 0;
    for (int i=0;i<=Cards.BONUS;i++) {
      total+= cards[CARDS_BY_LOC * locHand+ i];
      
    }
    return total;
  }

}
