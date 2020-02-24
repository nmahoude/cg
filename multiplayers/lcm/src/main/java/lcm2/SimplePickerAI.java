package lcm2;

import lcm2.cards.Card;

public class SimplePickerAI {
	private Agent me;
	private String action;
	
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
		
		action = "PICK "+best;
		System.err.println("Chosen action "+action);
	}

	private double score(Card card) {
		double score = 0.0;
			
		switch(card.model.type) {
		case CREATURE:
			score += 1000 + card.attack + 2 * card.defense - 3 * card.model.cost;
			if (card.isGuard()) {
				score += 5;
			}
			if (card.isLethal()) {
			  score += 1;
			}
      if (card.isWarded()) {
        score += card.defense;
      }
      if (card.isCharge()) {
        score += card.attack;
      }
			break;
		case ITEM_BLUE:
			score += 1;
			break;
		case ITEM_GREEN:
			score += 2;
			break;
		case ITEM_RED:
			score += 3;
			break;
		}
		

		
		return score;
	}

	private void output() {
    System.out.println(action);
	}

}
