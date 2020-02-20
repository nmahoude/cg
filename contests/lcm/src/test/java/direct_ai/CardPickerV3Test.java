package direct_ai;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Before;
import org.junit.Test;

import lcm.cards.Card;

public class CardPickerV3Test {

  private CardPickerV3 picker;

  @Before
  public void setup() {
    picker = new CardPickerV3();
  }
  
  @Test
  public void initial_0_card() throws Exception {
    Card card1 = new Card();
    card1.cost = 2;
    
    double value1 = picker.getBucketValue(card1);
    
    card1.cost = 3;
    double value2 = picker.getBucketValue(card1);
    
    
  }

}
