package ooc;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class PTest {

	@BeforeAll
	public static void setup() {
		OOCMapTest.emptyMap(Player.map);
	}
	
	@Test
	void torpedoDistance_on() throws Exception {
		Assertions.assertThat(P.get(7,7).blastDistance(P.get(7,7))).isEqualTo(0);
	}
	
	@Test
	void torpedoDistance_near() throws Exception {
		Assertions.assertThat(P.get(7,7).blastDistance(P.get(6, 6))).isEqualTo(1);
		Assertions.assertThat(P.get(7,7).blastDistance(P.get(7, 6))).isEqualTo(1);
		Assertions.assertThat(P.get(7,7).blastDistance(P.get(8, 6))).isEqualTo(1);
		Assertions.assertThat(P.get(7,7).blastDistance(P.get(6, 7))).isEqualTo(1);
		Assertions.assertThat(P.get(7,7).blastDistance(P.get(8, 7))).isEqualTo(1);
		Assertions.assertThat(P.get(7,7).blastDistance(P.get(6, 8))).isEqualTo(1);
		Assertions.assertThat(P.get(7,7).blastDistance(P.get(7, 8))).isEqualTo(1);
		Assertions.assertThat(P.get(7,7).blastDistance(P.get(6, 8))).isEqualTo(1);
	}
}
