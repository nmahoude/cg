package pac.simpleai;

import static org.junit.jupiter.api.Assertions.*;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import pac.map.Pos;

public class ColliderResolverTest {

	
	private Pos[] first;
	private Pos[] second;

	@BeforeEach
	public void init() {
		first = new Pos[2];
		second = new Pos[2];
		
	}
	
	@Test
	void firstMovetoActualPosition() throws Exception {
		first[0] = Pos.get(10, 10);
		first[1] = Pos.get(10, 10);
		
		second[0] = Pos.get(11, 10);
		second[1] = Pos.get(11, 10);

		boolean result = ColliderResolver.hasCollided(first, second);
		
		Assertions.assertThat(result).isEqualTo(false);
	}
	
	@Test
	void totallyDifferentPositions_no_collision() throws Exception {
		first[0] = Pos.get(10, 10);
		first[1] = Pos.get(10, 11);
		
		second[0] = Pos.get(20, 10);
		second[1] = Pos.get(20, 11);

		boolean result = ColliderResolver.hasCollided(first, second);
		
		Assertions.assertThat(result).isEqualTo(false);
	}

	@Test
	void depth1_collision() throws Exception {
		first[0] = Pos.get(10, 10);
		first[1] = Pos.get(10, 10);
		
		second[0] = Pos.get(10, 10);
		second[1] = Pos.get(10, 10);

		boolean result = ColliderResolver.hasCollided(first, second);
		
		Assertions.assertThat(result).isEqualTo(true);
	}

	@Test
	void depth2_collision_at_depth1() throws Exception {
		first[0] = Pos.get(10, 10);
		first[1] = Pos.get(10, 11);
		
		second[0] = Pos.get(10, 10);
		second[1] = Pos.get(11, 10);

		boolean result = ColliderResolver.hasCollided(first, second);
		
		Assertions.assertThat(result).isEqualTo(true);
	}
	
	@Test
	void depth2_collision_at_depth2() throws Exception {
		first[0] = Pos.get(10, 10);
		first[1] = Pos.get(10, 11);
		
		second[0] = Pos.get(11, 10);
		second[1] = Pos.get(10, 11);

		boolean result = ColliderResolver.hasCollided(first, second);
		
		Assertions.assertThat(result).isEqualTo(true);
	}
	
	@Test
	void depth2_no_collision_when_following() throws Exception {
		first[0] = Pos.get(10, 10);
		first[1] = Pos.get(10, 11);
		
		second[0] = Pos.get(10, 11);
		second[1] = Pos.get(10, 12);

		boolean result = ColliderResolver.hasCollided(first, second);
		
		Assertions.assertThat(result).isEqualTo(false);
	}
	@Test
	void depth2_collision_when_crossing() throws Exception {
		first[0] = Pos.get(10, 10);
		first[1] = Pos.get(10, 11);
		
		second[0] = Pos.get(10, 11);
		second[1] = Pos.get(10, 10);

		boolean result = ColliderResolver.hasCollided(first, second);
		
		Assertions.assertThat(result).isEqualTo(true);
	}
}
