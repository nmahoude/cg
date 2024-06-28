package sg22;

import java.util.ArrayList;
import java.util.List;

import fast.read.FastReader;
import sg22.nodes.NodeCache;

public class State {
  private static Cards myOrOppTotal = new Cards();
  public static Cards opponent = new Cards();
  public static Cards opponentAutomated = new Cards();
  
  
  public GamePhase phase = GamePhase.MOVE;
  public Hand hand = new Hand();
  
  public Cards cardsOnDesks = Cards.forDesks();

  public Agent me = new Agent();
  public Agent opp = new Agent();
  public Agent agents[] = new Agent[] {me, opp};
  
  
  
  // calculated for inner state
  public int throwCount;
  public int giveCount;
  public int playCount;
  public int cardToGet;

  
  
  public final static List<Application> applications = new ArrayList<>();
  public final static Application[] applicationById = new Application[100];
  
  
  public void readGlobal(FastReader in) {
    
  }

  public void copyFrom(State model) {
    this.phase = model.phase;
    this.agents[0].copyFrom(model.agents[0]);    
    this.agents[1].copyFrom(model.agents[1]);

    this.hand.copyFrom(model.hand);
    
    // tant qu'ils sont static, pas besoin de recopier
    // this.opponent.copyFrom(model.opponent);
    // this.opponentAutomated.copyFrom(model.opponentAutomated);

    this.cardsOnDesks.copyFrom(model.cardsOnDesks);

    
    this.throwCount = model.throwCount;
    this.giveCount = model.giveCount;
    this.playCount = model.playCount;
    this.cardToGet = model.cardToGet;
    
  }

  public void readRemember(FastReader in) {
    char[] remember = in.nextChars();
    applyRemember(in.nextInt(), in.nextInt(), in.nextInt(), in.nextInt());
  }
  
  public void read(FastReader in) {
    NodeCache.reset();
    clear();
    
    
    char[] gamePhase = in.nextChars(); // can be MOVE, GIVE_CARD, THROW_CARD, PLAY_CARD or RELEASE
    int applicationsCount = in.nextInt();
    this.phase = GamePhase.from(gamePhase);

    System.err.println("");

    
    
    
    System.err.println("^ " +valueOf(gamePhase));
    System.err.println("^ " +applicationsCount);

    ApplicationCache.reset();
    applications.clear();
    for (int i = 0; i < applicationsCount; i++) {
        char[] objectType = in.nextChars();

        Application a = ApplicationCache.get();
        a.read(in);
        applications.add(a);
        
        applicationById[a.id]= a; 
        
        System.err.println(String.format("^ %-15s %2d %d %d %d %d %d %d %d %d", "APPLICATION", 
            a.id, 
            a.needed[0],
            a.needed[1],
            a.needed[2],
            a.needed[3],
            a.needed[4],
            a.needed[5],
            a.needed[6],
            a.needed[7]
            ));
    }
    for (int i = 0; i < 2; i++) {
        int playerLocation = in.nextInt(); // id of the zone in which the player is located
        int playerScore = in.nextInt();
        int playerPermanentDailyRoutineCards = in.nextInt(); // number of DAILY_ROUTINE the player has played. It allows them to take cards from the adjacent zones
        int playerPermanentArchitectureStudyCards = in.nextInt(); // number of ARCHITECTURE_STUDY the player has played. It allows them to draw more cards
        System.err.println(String.format("^ %d %d %d %d", playerLocation, playerScore, playerPermanentDailyRoutineCards, playerPermanentArchitectureStudyCards));
        
        agents[i].location = playerLocation;
        agents[i].score = playerScore;
        agents[i].permanentDailyRoutineCards = playerPermanentDailyRoutineCards;
        agents[i].permanentArchitectureStudyCards = playerPermanentArchitectureStudyCards;
        
    }
    int cardLocationsCount = in.nextInt();
    System.err.println("^ "+cardLocationsCount);
    for (int i = 0; i < cardLocationsCount; i++) {
        char[] cardsLocation = in.nextChars(); // the location of the card list. It can be HAND, DRAW, DISCARD or OPPONENT_CARDS (AUTOMATED and OPPONENT_AUTOMATED will appear in later leagues)
        int locIndex = -1;
        Cards c = null;
        if (cardsLocation[0] == 'H') {
          locIndex = Hand.LOC_HAND;
        } else if (cardsLocation[0] == 'O') {
          if (cardsLocation[9] == 'C') {
            c = opponent;
          } else {
            c = opponentAutomated;
          }
        } else if (cardsLocation[0] == 'A') {
          locIndex = Hand.LOC_AUTOMATED;
        } else if (cardsLocation[0] == 'D') {
          if (cardsLocation[1] == 'R') {
            locIndex = Hand.LOC_DRAW;
          } else {
            locIndex = Hand.LOC_DISCARD;
          }
        } else if (cardsLocation[0] == 'P') {
          locIndex = Hand.LOC_PLAYEDCARDS;
        } else {
          throw new RuntimeException("Unknwon location "+valueOf(cardsLocation));
        }
        if (locIndex != -1) {
          hand.read(locIndex, in);
        } else {
          // static opponent cards
          c.read(in);
        }

        String location = valueOf(cardsLocation);
        if (c!= null) {
        System.err.println(String.format("^ %-15s    %d %d %d %d %d %d %d %d %d %d", location, 
            c.count[0], 
            c.count[1], 
            c.count[2], 
            c.count[3], 
            c.count[4], 
            c.count[5], 
            c.count[6], 
            c.count[7], 
            c.count[8], 
            c.count[9] 
            ));
        } else {
          hand.debugCards(locIndex, location);
        }
    }


    System.err.println("^ 0 "); // no possible moves on replay
    
    // update card on desk
    for (int i=0;i<8;i++) {
      cardsOnDesks.count[i] = 5 - (hand.totalFromCard(i) + opponent.count[i] + opponentAutomated.count[i]); 
    }
    
    cardsOnDesks.count[Cards.DAILY_ROUTINE]-= (agents[0].permanentDailyRoutineCards + agents[1].permanentDailyRoutineCards);
    cardsOnDesks.count[Cards.ARCHITECTURE_STUDY]-= (agents[0].permanentArchitectureStudyCards + agents[1].permanentArchitectureStudyCards);
    
    cardsOnDesks.debugCards("CARDS ON DESK");
  }

  public void readPossibleMoves(FastReader in) {
    int possibleMovesCount = in.nextInt();
    if (Player.DEBUG_POSSIBLE_MOVES) System.err.println("Possible moves : " + possibleMovesCount);
    for (int i = 0; i < possibleMovesCount; i++) {
        if (Player.DEBUG_POSSIBLE_MOVES) {
          String possibleMove = in.nextLine();
          System.err.println(possibleMove);
        } else {
          in.nextLinePass();
        }
    }
  }

  private void clear() {
    hand.clear();
    
  }

  private String valueOf(char[] cardsLocation) {
    StringBuilder sb = new StringBuilder();
    
    int i = 0;
    while (cardsLocation[i] != '\n' && cardsLocation[i] != ' ') {
      sb.append(cardsLocation[i++]);
    }
    return sb.toString();
  }

  public void discardCard(int index) {
    hand.remove(Hand.LOC_HAND, index);
    hand.put(Hand.LOC_DISCARD, index);
  }
  
  public void playCard(int index) {
    hand.remove(Hand.LOC_HAND, index);
    hand.put(Hand.LOC_PLAYEDCARDS, index);
  }


  public void updateNextPhase() {
    if (throwCount > 0) {
      this.phase = GamePhase.THROW;
    } else if (giveCount > 0) {
      this.phase = GamePhase.GIVE;
    } else if (playCount > 0){
      this.phase = GamePhase.PLAY;
      if (cardToGet != -1) {
        this.hand.put(Hand.LOC_HAND, cardToGet);
        cardToGet = -1;
      }
    } else {
      this.phase = GamePhase.RELEASE;
    }    
  }
  
  
  public void debugLite() {
    System.err.println("State (lite) : ");
    System.err.println("Next turn count : " + throwCount+" "+giveCount+" "+playCount+" " +cardToGet);
    System.err.println("Me : "+agents[0].location+" "+agents[0].score+" "+agents[0].permanentDailyRoutineCards+" "+agents[0].permanentArchitectureStudyCards);
    hand.debugCards();
  }

  public void discardAllCards() {
    hand.discardAllFromHand();
  }

  public boolean isOppNear(int location) {
    if (agents[1].location == -1) return false;
    
    if (agents[1].location == 7 && location == 0) return true;
    if (agents[1].location == 0 && location == 7) return true;
    
    int delta = (agents[1].location - location);
    return Math.abs(delta) <= 1;
  }

  public void applyRemember(int rememberThrowCount, int rememberGivecount, int rememberPlayCount,
      int rememberCardToGet) {

    if (phase == GamePhase.THROW) {
      throwCount = rememberThrowCount;
      giveCount = rememberGivecount;
      playCount = 1;
      cardToGet = rememberCardToGet;
    }
    if (phase == GamePhase.GIVE) {
      throwCount = 0;
      giveCount = 1;
      playCount = 1;
      cardToGet = rememberCardToGet;
    }
    if (phase == GamePhase.PLAY) {
      throwCount = 0;
      giveCount = 0;
      playCount = Math.max(1, rememberPlayCount);
      cardToGet = -1;
    }
    
    System.err.println("^ REMEMBER "+throwCount +" " + giveCount+ " "+playCount + " "+cardToGet);

  }

  public void debugCardsOnDesk() {
    cardsOnDesks.debugCards("CARDS ON DESK");
  }

  public Cards myTotal() {
    myOrOppTotal.clear();
    for (int i=0;i<9;i++) {
      myOrOppTotal.count[i] = (hand.totalOfAllLocs(i));
    }

    return myOrOppTotal;
  }

  public Cards oppTotal() {
    myOrOppTotal.clear();

    for (int i=0;i<9;i++) {
      myOrOppTotal.count[i] = (opponent.count[i] + opponentAutomated.count[i]);
    }

    return myOrOppTotal;
  }

}
