package cultistwars;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class BresenhamTest {

	@BeforeAll
	static void setup() {
		Bresenham.initialize(new int[13][7]);
	}
	
	@Test
	void emptyGrid() throws Exception {
		assertThat(Bresenham.line(Pos.get(11,1), Pos.get(11, 0))).isNotEmpty();
		assertThat(Bresenham.line(Pos.get(9,1), Pos.get(9, 0))).contains(Pos.get(9,0));
	}
}
