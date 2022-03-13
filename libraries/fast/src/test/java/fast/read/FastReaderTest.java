package fast.read;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

public class FastReaderTest {

  @Test
  void nextStringReturnsTheStringWhenStartingWithSpaces() throws Exception {
    FastReader reader = FastReader.fromString("  theLine  ");
    Assertions.assertThat(reader.nextString()).isEqualTo("theLine");
  }
  
  @Test
  void nextStringReturnsFirstStringWhenMultipleString() throws Exception {
    FastReader reader = FastReader.fromString("first second third  ");
    Assertions.assertThat(reader.nextString()).isEqualTo("first");
  }
  
}
