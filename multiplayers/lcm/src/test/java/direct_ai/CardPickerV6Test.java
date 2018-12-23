package direct_ai;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import lcm.cards.Card;
import lcm.fixtures.CardFixture;

public class CardPickerV6Test {
  private static final Card CREA_12_MANA = CardFixture.card(116, -1);
  private CardPickerV6 picker;

  @Before
  public void setup() {
    picker = new CardPickerV6();
    picker.manaCurve = new int[]{ 30, 0, 0, 0, 0, 0 ,0 ,0};
  }
  
  @Test
  public void shouldLimitCostToSeven() throws Exception {

    int cost = picker.bucketCost(CREA_12_MANA);

    assertThat(cost, is(7));
  }
  
  @Test
  public void noMalusForEmptyPicker() throws Exception {
    picker.manaCurve = new int[]{ 0, 0, 0, 0, 0, 0 ,0 , 30};
    
    int malus = picker.getBucketMalus(CREA_12_MANA);
    
    assertThat(malus, is(0));
  }
  
  @Test
  public void shouldMalusIfCapacityReached() throws Exception {
    picker.manaCurve = new int[]{ 0, 0, 0, 30, 0, 0 ,0 , 1};
    picker.cardCount[7] = 1;
    
    int malus = picker.getBucketMalus(CREA_12_MANA);
    
    assertThat(malus, is(-10));
  }
  
  @Test
  @Ignore
  public void shouldBIGMalusIfCapacityReached() throws Exception {
    picker.manaCurve = new int[]{ 0, 0, 0, 30, 0, 0 ,0 , 3};
    picker.cardCount[7] = 5;
    
    int malus = picker.getBucketMalus(CREA_12_MANA);
    
    assertThat(malus, is(-20));
  }

  @Test
  @Ignore
  public void shouldMalusIfCapacityOfNeighboursExceededReached() throws Exception {
    picker.manaCurve = new int[]{ 0, 0, 0, 30, 0, 0 ,2 , 0};
    picker.cardCount[6] = 5;
    
    int malus = picker.getBucketMalus(CREA_12_MANA);
    
    assertThat(malus, is(-10));
  }

  @Test
  @Ignore
  public void shouldBIGMalusIfCapacityOfNeighboursExceededReached() throws Exception {
    picker.manaCurve = new int[]{ 0, 0, 0, 30, 0, 0 ,2 , 0};
    picker.cardCount[6] = 6;
    
    int malus = picker.getBucketMalus(CREA_12_MANA);
    
    assertThat(malus, is(-10));
  }
}
