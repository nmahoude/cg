package ooc;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Scanner;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

public class OOCMapTest {
	private static final String EOF = "\r";
	
	@Test
	void distancesWithoutIslands() throws Exception {
		OOCMap map = new OOCMap();
		emptyMap(map);
		
		assertThat(map.distances(P.get(3, 3), P.get(3, 3))).isEqualTo(0);
		assertThat(map.distances(P.get(0, 0), P.get(14, 14))).isEqualTo(28);
	}

	@Test
	void unreachableCell() throws Exception {
		OOCMap map = new OOCMap();
		createUnreachableIslandAt77(map);
		
		assertThat(map.distances(P.get(3, 3), P.get(7, 7))).isEqualTo(Integer.MAX_VALUE);
	}

	@Test
  void blastDistance() throws Exception {
    OOCMap map = new OOCMap();
    createUnreachableIslandAt77(map);

    assertThat(map.blastDistance(P.get(7, 5), P.get(8, 3))).isEqualTo(Integer.MAX_VALUE);
  }
	
	@Test
	void possibleDamageAtFrontier() throws Exception {
		OOCMap map = new OOCMap();
		createUnreachableIslandAt77(map);
		
		Assertions.assertThat(map.possibleDamageFromPosition(P.get(7, 7), P.get(7, 7))).isEqualTo(2);
		Assertions.assertThat(map.possibleDamageFromPosition(P.get(6, 6), P.get(7, 7))).isEqualTo(1);
		Assertions.assertThat(map.possibleDamageFromPosition(P.get(9, 6), P.get(7, 7))).isEqualTo(1);
		Assertions.assertThat(map.possibleDamageFromPosition(P.get(6, 9), P.get(7, 7))).isEqualTo(1);
		Assertions.assertThat(map.possibleDamageFromPosition(P.get(9, 9), P.get(7, 7))).isEqualTo(1);
	}

	@Test
	void possibleDamageInClearWater() throws Exception {
		OOCMap map = new OOCMap();
		createUnreachableIslandAt77(map);

		Assertions.assertThat(map.possibleDamageFromPosition(P.get(0, 0), P.get(0, 0))).isEqualTo(2);
		Assertions.assertThat(map.possibleDamageFromPosition(P.get(0, 0), P.get(0, 1))).isEqualTo(2);
		Assertions.assertThat(map.possibleDamageFromPosition(P.get(0, 0), P.get(0, 2))).isEqualTo(2);
		Assertions.assertThat(map.possibleDamageFromPosition(P.get(0, 0), P.get(0, 3))).isEqualTo(2);
		Assertions.assertThat(map.possibleDamageFromPosition(P.get(0, 0), P.get(0, 4))).isEqualTo(2);
		Assertions.assertThat(map.possibleDamageFromPosition(P.get(0, 0), P.get(0, 5))).isEqualTo(1);

	}

	private void createUnreachableIslandAt77(OOCMap map) {
		map.read(new Scanner(
        "15 15 0"+EOF
      + "               "+EOF
      + "               "+EOF
      + "               "+EOF
      + "               "+EOF
      + "               "+EOF
      + "               "+EOF
      + "       x       "+EOF
      + "      x x      "+EOF
      + "       x       "+EOF
      + "               "+EOF
      + "               "+EOF
      + "               "+EOF
      + "               "+EOF
      + "               "+EOF
      + "               "+EOF
      ));
	}
	
	
	public static void emptyMap(OOCMap map) {
		map.read(new Scanner(
          "15 15 0"+EOF
        + "               "+EOF
        + "               "+EOF
        + "               "+EOF
        + "               "+EOF
        + "               "+EOF
        + "               "+EOF
        + "               "+EOF
        + "               "+EOF
        + "               "+EOF
        + "               "+EOF
        + "               "+EOF
        + "               "+EOF
        + "               "+EOF
        + "               "+EOF
        + "               "+EOF
        ));
	}
}
