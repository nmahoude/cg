package calm;

import java.util.Scanner;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

public class MapTest {

  
  private static final String EOF = "\r\n";

  @Test
  void readAMapAndCalculateDistances() throws Exception {
    Map map = new Map();
    map.read(new Scanner(""+EOF
        + "XXXXXXXXXXX"+EOF
        + "X         X"+EOF
        + "X         X"+EOF
        + "X         X"+EOF
        + "X         X"+EOF
        + "X         X"+EOF
        + "X         X"+EOF
        + "XXXXXXXXXXX"+EOF
        + ""));
    
    Assertions.assertThat(map.distanceFromTo(P.get(1, 1), P.get(9, 1))).isEqualTo(2); // floor to floor
    Assertions.assertThat(map.distanceFromTo(P.get(1, 1), P.get(10, 1))).isEqualTo(2); // floor to table
  }
}
