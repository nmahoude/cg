package fall2023;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class GridMasterTest {
  GridMaster grid;
  
  @BeforeEach
  void setup() {
    grid = new GridMaster();
  }
  
  
  @Test
  void aSetCanBeRetrieve() throws Exception {
    grid.set(100, 200);
    assertThat(grid.isSet(100, 200)).isTrue();
    assertThat(grid.isSet(101, 200)).isFalse();
    
    grid.clear(100, 200);
    assertThat(grid.isSet(100, 200)).isFalse();
  }
}
