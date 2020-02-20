package lcm.predictors;

import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertThat;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import lcm.Player;
import lcm.cards.Card;
import lcm.cards.CardTriplet;
import lcm.fixtures.CardFixture;

public class OracleTest {

  private Oracle oracle;
  private Card card2;
  private Card card4;
  private Card card7;
  @Before
  public void setup() {
    Player.DEBUG_INPUT = false;
    
    oracle = new Oracle();
    card2 = CardFixture.card(2, -1);
    card4 = CardFixture.card(4, -1);
    card7 = CardFixture.card(7, -1);
  }
  
  
  public @Test void shouldKeepTheFeedTriplets() throws Exception {
    CardTriplet triplet = new CardTriplet(card2, card4, card7);
    
    oracle.feed(triplet, 0);
    
    assertThat(oracle.triplets.size(), is(1));
  }

  public @Test void feedTripletGivePotentialCards() {
    CardTriplet triplet = new CardTriplet(card2, card4, card7);
    oracle.feed(triplet, 0);

    List<Card> potential = oracle.oppPotential();
    
    assertThat(potential, hasItem(card2));
    assertThat(potential, hasItem(card4));
    assertThat(potential, hasItem(card7));
  }

  public @Test void shouldEvictSeenCards() throws Exception {
    CardTriplet triplet = new CardTriplet(card2, card4, card7);
    oracle.feed(triplet, 0);
    oracle.addOpponentSeenCard(CardFixture.card(4, 111));

    
    List<Card> potential = oracle.oppPotential();
    
    assertThat(potential, hasItem(card2));
    assertThat(potential, not(hasItem(card4)));
    assertThat(potential, hasItem(card7));
    
  }
  
  public @Test void shouldFilterOnManaMax() throws Exception {
    CardTriplet triplet = new CardTriplet(card2, card4, card7);
    oracle.feed(triplet, 0);
    oracle.addOpponentSeenCard(CardFixture.card(4, 111));

    
    List<Card> potential = oracle.oppPotential(1);
    
    assertThat(potential, hasItem(card2));
    assertThat(potential, not(hasItem(card4)));
    assertThat(potential, not(hasItem(card7)));
  }
}
