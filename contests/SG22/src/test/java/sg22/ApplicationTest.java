package sg22;

import static fast.read.FastReader.fromString;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

public class ApplicationTest {

  void notEnough() throws Exception {
    Hand hand = new Hand();
    
    hand.read(Hand.LOC_HAND,        fromString("0 0 0 1 0 0 0 0 1 0 "));
    assertThat(applicationFrom("19 0 0 0 4 0 4 0 0").canFinish(hand)).isEqualTo(-1);
  }
  
  @Test
  void enough() throws Exception {
    Hand hand = new Hand();

    hand.read(Hand.LOC_HAND,        fromString("0 0 0 1 0 0 0 0 2 3 "));
    assertThat(applicationFrom("13 0 0 4 4 0 0 0 0").canFinish(hand)).isGreaterThanOrEqualTo(0); 
    assertThat(applicationFrom("21 0 0 0 4 0 0 0 4").canFinish(hand)).isGreaterThanOrEqualTo(0);
  }

  private Application applicationFrom(String input) {
    Application a = new Application();
    a.read(fromString(input+" "));
    return a;
  }
}
