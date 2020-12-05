package lcm2.simpleai;

import lcm2.Agent;
import lcm2.cards.Card;

public class SimplePickerAI {
	private Agent me;
	private String action;
	int cardsByCosts[] = new int[20];
	
	public SimplePickerAI(Agent me) {
    this.me = me;
  }

  public void run() {
    think();
    output();
  }

	private void think() {
		int best = 0;
		double bestScore = Double.NEGATIVE_INFINITY;
		
		for (int i=0;i<3;i++) {
			Card card = me.handCards[i];
		
			double score = score(card);
			System.err.println("Score for "+i+" => "+score);
			if (score > bestScore) {
				bestScore = score;
				best = i;
			}
		}
	
		Card bestCard = me.handCards[best];
		cardsByCosts[bestCard.model.cost]++;
		action = "PICK "+best;
		System.err.println("Chosen action "+action);
	}

	private double score(Card card) {
		double score = 0.0;
		
		double coeff = coeff(card.model.cost);
		
		score += cardIndexP0[card.model.cardNumber] / (1.0 + coeff); 
		
//		switch(card.model.type) {
//		case CREATURE:
//			score += 1000 + 1.0 * (3 * card.attack + 1 * card.defense) / (card.model.cost+1);
//			
//			if (card.model.cost <= 2) {
//			  score -= 10;
//			}
//			if (card.model.cost >= 7) {
//			  score -= 10;
//			}
//			score -= coeff;
//			if (card.isGuard()) {
//				score += 2;
//			}
//			if (card.isLethal()) {
//			  score += 1;
//			}
//      if (card.isWarded()) {
//        score += 1;
//      }
//      if (card.isCharge()) {
//        score += card.attack;
//      }
//			break;
//		case ITEM_BLUE:
//			score += 1;
//			break;
//		case ITEM_GREEN:
//			score += 2;
//			break;
//		case ITEM_RED:
//			score += 3;
//			break;
//		}
		

		
		return score;
	}

	private double coeff(int cost) {
	  double coeffs[] = new double[] { 0.2, 0.6, 1.0, 0.6, 0.2 }; 
	  double total = 0;

	  for (int i=-2;i<=2;i++) {
	    if (cost +i < 0) continue;
	    
	    total += cardsByCosts[cost + i ] * coeffs[2+i];
	  }
	  return total / (manaCurveP0[cost]+1);
  }

  private void output() {
    System.out.println(action);
	}

  private int[] manaCurveP0 = { 0, 3, 5, 6, 6, 4, 3, 3, 3, 3, 3, 3, 3, 3, 3};
  private int[] cardIndexP0 = {0,
      66,32,115,53,81,87,147,106,123,17,92,85,75,26,113,42,103,148,122,30,114,54,130,20,59,91,31,129,150,61,10,156,142,89,35,95,146,60,46,38,79,11,33,136,40,23,57,145,159,127,154,134,153,112,7,51,13,41,68,18,90,52,14,131,152,132,140,157,144,108,71,73,69,72,111,25,62,21,83,149,116,138,76,151,141,82,125,143,58,78,49,8,65,50,124,118,99,80,133,77,44,27,117,84,97,100,15,28,120,1,101,67,12,98,121,160,19,74,45,47,119,63,34,22,56,86,48,93,104,24,29,37,135,107,102,36,64,9,155,2,94,39,3,109,88,43,139,128,70,105,158,126,4,16,96,5,110,137,55,6,
  };
}
