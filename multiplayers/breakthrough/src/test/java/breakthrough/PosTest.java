package breakthrough;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

public class PosTest {

  @Test
  void readG1WhenFlipped() throws Exception {
    Player.firstPlayer = false;
    Pos g1 = Pos.from('g', '1');
    
    Assertions.assertThat(g1).isEqualTo(Pos.from(6,7));
  }
}
