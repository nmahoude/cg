package lcm.ai;

import lcm.State;
import lcm.cards.Card;
import lcm.cards.Location;

public class PositionAssessment {

  public int clocks[] = new int[2];
  
  
  public void assessPosition(State state, boolean debug) {
    // TODO si il a bcp plus de dégats que ma def
    double myDef = 0, hisDef = 0;
    double myAtk = 0, hisAtk = 0;
    double myGuardDef = 0, hisGuardDef = 0;
    for (int i=0;i<state.cardsFE;i++) {
      Card card = state.cards[i];
      if (card.location == Location.MY_BOARD) {
        myAtk += card.attack;
        myDef += card.defense;
        if (card.isGuard()) myGuardDef += card.defense;
      }
      if (card.location == Location.HIS_BOARD) {
        hisAtk += card.attack;
        hisDef += card.defense;
        if (card.isGuard()) hisGuardDef += card.defense;
      }
    }
    int tempo = state.me.getBoardCardsCount() - state.opp.getBoardCardsCount();
    int myClockToHisFace = Integer.MAX_VALUE, hisClockToMyFace = Integer.MAX_VALUE;
    if (myAtk-hisGuardDef > 0  && state.me.getBoardCardsCount() > state.opp.guardsCardsCount) {
      myClockToHisFace = (int)Math.ceil(state.opp.health / (myAtk-hisGuardDef));
    }
    if (hisAtk-myGuardDef > 0 && state.opp.getBoardCardsCount() > state.me.guardsCardsCount) {
      hisClockToMyFace = (int)Math.ceil(state.me.health / (hisAtk - myGuardDef));
    }
    
    clocks[0] = hisClockToMyFace; // how many turns before I die
    clocks[1] = myClockToHisFace; // how many turns before he dies
    
    if (debug) {
      System.err.println("tempo : " + tempo);
      System.err.println("myClockToHisFace : "+myClockToHisFace);
      System.err.println("hisClockToHisFace : "+hisClockToMyFace);
    }
  }

}
