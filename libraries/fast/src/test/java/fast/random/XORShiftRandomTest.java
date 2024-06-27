package fast.random;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

public class XORShiftRandomTest {

	
	@Test
	void fastModulo() throws Exception {
		assertThat(new XORShiftRandom().nextInt(8, 4)).isEqualTo(0);
		assertThat(new XORShiftRandom().nextInt(9, 4)).isEqualTo(1);
		assertThat(new XORShiftRandom().nextInt(10, 4)).isEqualTo(2);
		assertThat(new XORShiftRandom().nextInt(11, 4)).isEqualTo(3);
		assertThat(new XORShiftRandom().nextInt(12, 4)).isEqualTo(0);
	}
}
