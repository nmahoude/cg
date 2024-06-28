package sg22;

import fast.read.FastReader;

public class Application {
  public int id;
  public int needed[] = new int [8];

  public void read(FastReader in) {
    id = in.nextInt();
    
    for (int i=0;i<8;i++) {
      needed[i] = in.nextInt();
    }
  }

  public int canFinish(Hand hand) {
    int bonus = hand.get(Hand.LOC_HAND, Cards.BONUS) + hand.get(Hand.LOC_AUTOMATED, Cards.BONUS);
    
    int missingResources = 0;
    
    for (int i=0;i<8;i++) {
      int totalSkills = hand.cards[Hand.CARDS_BY_LOC * Hand.LOC_HAND +  i] + hand.cards[Hand.CARDS_BY_LOC * Hand.LOC_AUTOMATED +  i];
      if (totalSkills * 2 < needed[i]) {
        missingResources += (needed[i] - 2 * totalSkills);
      }
    }    
    missingResources -= bonus;
    if (missingResources > 0) {
      int totalBaclee = hand.cards[Hand.CARDS_BY_LOC * Hand.LOC_HAND +  Hand.BACLEE] + hand.cards[Hand.CARDS_BY_LOC * Hand.LOC_AUTOMATED +  Hand.BACLEE];
      
      if (missingResources <= totalBaclee) {
        return missingResources;
      } else {
        return -1; // not doable
      }
    } else {
      return 0;
    }
  }
}
